package lain.mods.bilicraftcomments.server;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import lain.mods.bilicraftcomments.BilicraftCommentsServer;
import lain.mods.bilicraftcomments.server.Messenger.Message;
import lain.mods.bilicraftcomments.server.command.CommandBlacklistAdd;
import lain.mods.bilicraftcomments.server.command.CommandBlacklistRemove;
import lain.mods.bilicraftcomments.server.command.CommandBroadcast;
import lain.mods.bilicraftcomments.server.command.CommandReload;
import lain.mods.bilicraftcomments.server.command.CommandWhitelistAdd;
import lain.mods.bilicraftcomments.server.command.CommandWhitelistRemove;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StringUtils;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;

public class ServerProxy
{

    public static void setup()
    {
        if (INSTANCE == null)
            throw new RuntimeException();
    }

    public static final ServerProxy INSTANCE = new ServerProxy();

    public static final String TARGET = "BcC|C";
    public static final String LOCAL = "BcC|S";
    public FMLEventChannel channel;

    public final JsonPlayerList whitelist = new JsonPlayerList();
    public final JsonPlayerList blacklist = new JsonPlayerList();
    public final PerPlayerTimeMarker marker = new PerPlayerTimeMarker("timeComment", true);
    public final Logger chatLogger = Logger.getLogger(LOCAL);

    public static final int logLimit = 33554432;

    private ServerProxy()
    {
        channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(LOCAL);
        channel.register(this);

        try
        {
            whitelist.loadFile(new File(BilicraftCommentsServer.rootDir, "BcC_Whitelist.json"));
        }
        catch (Exception e)
        {
            BilicraftCommentsServer.logger.fatal("error loading whitelist file: " + e.toString());
            throw new RuntimeException(e);
        }
        try
        {
            blacklist.loadFile(new File(BilicraftCommentsServer.rootDir, "BcC_Blacklist.json"));
        }
        catch (Exception e)
        {
            BilicraftCommentsServer.logger.fatal("error loading blacklist file: " + e.toString());
            throw new RuntimeException(e);
        }
        try
        {
            File logPath = new File(BilicraftCommentsServer.rootDir, "BcC_CommentLog_%g.log");
            chatLogger.addHandler(new FileHandler(logPath.getPath(), logLimit, 4, true)
            {
                {
                    setLevel(Level.ALL);
                    setFormatter(new Formatter()
                    {
                        String LINE_SEPARATOR = System.getProperty("line.separator");
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        @Override
                        public String format(LogRecord record)
                        {
                            StringBuilder msg = new StringBuilder();
                            msg.append(dateFormat.format(Long.valueOf(record.getMillis())));
                            msg.append(" ");
                            msg.append(record.getMessage());
                            msg.append(LINE_SEPARATOR);
                            return msg.toString();
                        }
                    });
                }

                @Override
                public synchronized void close() throws SecurityException
                {
                }
            });
        }
        catch (Exception e)
        {
            BilicraftCommentsServer.logger.fatal("error adding comment logger: " + e.toString());
        }
    }

    public FMLProxyPacket createDisplayRequest(int mode, int lifespan, String s)
    {
        try
        {
            ByteBufOutputStream bbos = new ByteBufOutputStream(Unpooled.buffer());
            DataOutputStream dos = new DataOutputStream(bbos);
            dos.writeInt(mode);
            dos.writeInt(lifespan);
            dos.writeUTF(s);
            dos.close();
            return new FMLProxyPacket(bbos.buffer(), TARGET);
        }
        catch (Exception e)
        {
            BilicraftCommentsServer.logger.fatal("error creating display request: " + e.toString());
            return null;
        }
    }

    @SubscribeEvent
    public void networkHook(FMLNetworkEvent.ServerCustomPacketEvent event)
    {
        DataInputStream dis = null;
        try
        {
            EntityPlayerMP sender = ((NetHandlerPlayServer) event.handler).playerEntity;
            if (ServerConfigs.whitelistMode && !whitelist.contains(sender))
            {
                Messenger.send(sender, Message.msgNotInWhitelist, EnumChatFormatting.DARK_RED);
                return;
            }
            if (blacklist.contains(sender))
            {
                Messenger.send(sender, Message.msgInBlacklist, EnumChatFormatting.DARK_RED);
                return;
            }
            if (!marker.checkTimeIfValid(sender, sender.worldObj.getTotalWorldTime(), ServerConfigs.commentInterval, false))
            {
                Messenger.send(sender, Message.msgTooFastToComment, EnumChatFormatting.DARK_RED);
                return;
            }
            dis = new DataInputStream(new ByteBufInputStream(event.packet.payload()));
            int mode = dis.readInt();
            int lifespan = dis.readInt();
            String text = dis.readUTF().replace("&", "\u00a7").replace("\u00a7\u00a7", "&");
            if (!ServerConfigs.isModeAllowed(mode) || lifespan < ServerConfigs.minLifespan || lifespan > ServerConfigs.maxLifespan || StringUtils.stripControlCodes(text).isEmpty())
            {
                Messenger.send(sender, Message.msgInvalidArguments, EnumChatFormatting.DARK_RED);
                return;
            }
            chatLogger.info(String.format("[uuid:%s] [username:%s] [mode:%d] [lifespan:%d] %s", sender.getUniqueID().toString(), StringUtils.stripControlCodes(sender.getCommandSenderName()), mode, lifespan, text));
            marker.markTime(sender, sender.worldObj.getTotalWorldTime());
            FMLProxyPacket packet = createDisplayRequest(mode, lifespan, text);
            channel.sendToAll(packet);
        }
        catch (IOException e)
        {
            BilicraftCommentsServer.logger.warn("error handling comment request: " + e.toString());
        }
        finally
        {
            if (dis != null)
                try
                {
                    dis.close();
                }
                catch (IOException ignored)
                {
                }
        }
    }

    public void registerCommands(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CommandReload());
        event.registerServerCommand(new CommandBroadcast());
        event.registerServerCommand(new CommandWhitelistAdd());
        event.registerServerCommand(new CommandWhitelistRemove());
        event.registerServerCommand(new CommandBlacklistAdd());
        event.registerServerCommand(new CommandBlacklistRemove());
    }

}
