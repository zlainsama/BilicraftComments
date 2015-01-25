package lain.mods.bilicraftcomments;

import java.io.File;
import lain.mods.bilicraftcomments.server.ServerConfigs;
import lain.mods.bilicraftcomments.server.ServerProxy;
import org.apache.logging.log4j.Logger;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(modid = "BilicraftComments", useMetadata = true)
public class BilicraftCommentsServer
{

    public static Logger logger;
    public static File rootDir;

    @Mod.EventHandler
    public void init(FMLPreInitializationEvent event)
    {
        ServerConfigs.load(event.getSuggestedConfigurationFile());
        if (!ServerConfigs.allowLANServer && event.getSide().isClient())
            return;

        logger = event.getModLog();
        rootDir = event.getModConfigurationDirectory().getParentFile();

        ServerProxy.setup();
    }

    @Mod.EventHandler
    public void startupHook(FMLServerStartingEvent event)
    {
        ServerProxy.INSTANCE.registerCommands(event);
    }

}
