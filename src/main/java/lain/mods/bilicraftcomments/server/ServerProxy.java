package lain.mods.bilicraftcomments.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import lain.mods.bilicraftcomments.BilicraftCommentsServer;
import lain.mods.bilicraftcomments.MCUtils;
import lain.mods.bilicraftcomments.server.Messenger.Message;
import lain.mods.bilicraftcomments.server.command.CommandBlacklistAdd;
import lain.mods.bilicraftcomments.server.command.CommandBlacklistRemove;
import lain.mods.bilicraftcomments.server.command.CommandBroadcast;
import lain.mods.bilicraftcomments.server.command.CommandReload;
import lain.mods.bilicraftcomments.server.command.CommandWhitelistAdd;
import lain.mods.bilicraftcomments.server.command.CommandWhitelistRemove;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

public class ServerProxy
{

    public static final ServerProxy INSTANCE = new ServerProxy();

    public static final String TARGET = "BcC|C";
    public static final String LOCAL = "BcC|S";

    public static final int logLimit = 33554432;

    public static void setup()
    {
        if (INSTANCE == null)
            throw new RuntimeException();
    }

    public FMLEventChannel channel;

    public final JsonPlayerList whitelist = new JsonPlayerList();
    public final JsonPlayerList blacklist = new JsonPlayerList();
    public final PerPlayerTimeMarker marker = new PerPlayerTimeMarker("timeComment", true);
    public final Logger chatLogger = Logger.getLogger(LOCAL);

    private ServerProxy()
    {
        channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(LOCAL);
        channel.register(this);

        try
        {
            whitelist.loadFile(new File(BilicraftCommentsServer.rootDir, "BcC_Whitelist.json"));
        }
        catch (FileNotFoundException e)
        {
            try
            {
                whitelist.saveFile(whitelist.currentFile);
            }
            catch (IOException e1)
            {
                BilicraftCommentsServer.logger.fatal("error creating default whitelist file: " + e1.toString());
                throw new RuntimeException(e1);
            }
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
        catch (FileNotFoundException e)
        {
            try
            {
                blacklist.saveFile(blacklist.currentFile);
            }
            catch (IOException e1)
            {
                BilicraftCommentsServer.logger.fatal("error creating default blacklist file: " + e1.toString());
                throw new RuntimeException(e1);
            }
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
            return new FMLProxyPacket(new PacketBuffer(bbos.buffer()), TARGET);
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
            EntityPlayerMP sender = ((NetHandlerPlayServer) event.getHandler()).player;
            if (ServerConfigs.whitelistMode && !whitelist.contains(sender))
            {
                Messenger.sendWithColor(sender, Message.msgNotInWhitelist, TextFormatting.RED);
                return;
            }
            if (blacklist.contains(sender))
            {
                Messenger.sendWithColor(sender, Message.msgInBlacklist, TextFormatting.RED);
                return;
            }
            if (!marker.checkTimeIfValid(sender, sender.world.getTotalWorldTime(), ServerConfigs.commentInterval, false))
            {
                Messenger.sendWithColor(sender, Message.msgTooFastToComment, TextFormatting.RED);
                return;
            }
            dis = new DataInputStream(new ByteBufInputStream(event.getPacket().payload()));
            int mode = dis.readInt();
            int lifespan = dis.readInt();
            String text = dis.readUTF().replace("&", "\u00a7").replace("\u00a7\u00a7", "&");
            if (!ServerConfigs.isModeAllowed(mode) || lifespan < ServerConfigs.minLifespan || lifespan > ServerConfigs.maxLifespan || MCUtils.stripControlCodes(text).isEmpty())
            {
                Messenger.sendWithColor(sender, Message.msgInvalidArguments, TextFormatting.RED);
                return;
            }
            chatLogger.info(String.format("[uuid:%s] [username:%s] [mode:%d] [lifespan:%d] %s", sender.getUniqueID().toString(), MCUtils.stripControlCodes(sender.getName()), mode, lifespan, text));
            marker.markTime(sender, sender.world.getTotalWorldTime());
            FMLProxyPacket packet = createDisplayRequest(mode, lifespan, ServerConfigs.appendUsername ? MCUtils.stripControlCodes(sender.getName()) + ": " + text : text);
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
