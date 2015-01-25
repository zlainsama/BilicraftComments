package lain.mods.bilicraftcomments;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.File;
import lain.mods.bilicraftcomments.common.CommonProxy;
import lain.mods.bilicraftcomments.common.Settings;
import lain.mods.bilicraftcomments.network.NetworkManager;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Logger;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;

@Mod(modid = "BilicraftComments", useMetadata = true)
public class BilicraftComments
{

    @SidedProxy(clientSide = "lain.mods.bilicraftcomments.network.NetworkManagerClient", serverSide = "lain.mods.bilicraftcomments.network.NetworkManager")
    public static NetworkManager network;

    public static FMLProxyPacket createDisplayPacket(int mode, int lifespan, String text)
    {
        return createPacket("LC|BcC|D", mode, lifespan, text);
    }

    public static FMLProxyPacket createPacket(String channel, int mode, int lifespan, String text)
    {
        try
        {
            ByteBuf buf = Unpooled.buffer();
            buf.writeShort(mode);
            buf.writeShort(lifespan);
            buf.writeBytes(text.getBytes("UTF-8"));
            return new FMLProxyPacket(buf, channel);
        }
        catch (Exception e)
        {
            logger.catching(e);
            return null;
        }
    }

    public static String createRequestCommandLine(int mode, int lifespan, String text)
    {
        return String.format("/bcc_comment %d %d %s", mode, lifespan, text);
    }

    @SidedProxy(serverSide = "lain.mods.bilicraftcomments.common.CommonProxy", clientSide = "lain.mods.bilicraftcomments.client.ClientProxy")
    public static CommonProxy proxy;

    public static File rootDir;

    public static Configuration config;

    public static Logger logger;

    public static IPermissionManager manager;

    public static final int logLimit = 33554432;

    @Mod.EventHandler
    public void init(FMLPreInitializationEvent event)
    {
        rootDir = event.getModConfigurationDirectory().getParentFile();
        config = new Configuration(event.getSuggestedConfigurationFile());
        logger = event.getModLog();

        Settings.reload(config);
    }

    @Mod.EventHandler
    public void load(FMLInitializationEvent event)
    {
        proxy.load();
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event)
    {
        proxy.registerCommands(event.getServer().getCommandManager());
    }

}