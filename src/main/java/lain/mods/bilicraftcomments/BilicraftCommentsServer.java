package lain.mods.bilicraftcomments;

import java.io.File;
import lain.mods.bilicraftcomments.server.ServerConfigs;
import lain.mods.bilicraftcomments.server.ServerProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = "BilicraftComments", useMetadata = true, acceptedMinecraftVersions = "[1.10],[1.10.2]")
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
