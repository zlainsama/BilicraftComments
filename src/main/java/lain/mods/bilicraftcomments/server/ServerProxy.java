package lain.mods.bilicraftcomments.server;

import io.netty.buffer.ByteBufInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.logging.Logger;
import lain.mods.bilicraftcomments.BilicraftCommentsServer;
import lain.mods.bilicraftcomments.server.Messenger.Message;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StringUtils;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

public class ServerProxy
{

    public static void setup()
    {
        if (INSTANCE == null)
            throw new RuntimeException();
    }

    public static final ServerProxy INSTANCE = new ServerProxy();

    private static final String TARGET = "BcC|C";
    private static final String LOCAL = "BcC|S";
    private FMLEventChannel channel;

    protected final JsonPlayerList whitelist = new JsonPlayerList();
    protected final JsonPlayerList blacklist = new JsonPlayerList();
    protected final PerPlayerTimeMarker marker = new PerPlayerTimeMarker("timeComment", true);
    protected final Logger chatLogger = Logger.getLogger(LOCAL);

    private ServerProxy()
    {
        channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(LOCAL);
        channel.register(this);
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

    }
    
    public FMLProxyPacket createDisplayPacket()
    {
        
    }

}
