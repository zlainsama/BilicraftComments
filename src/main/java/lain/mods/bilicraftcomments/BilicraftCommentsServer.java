package lain.mods.bilicraftcomments;

import java.io.File;
import org.apache.logging.log4j.Logger;
import lain.mods.bilicraftcomments.server.ServerConfigs;
import lain.mods.bilicraftcomments.server.ServerProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = "bilicraftcomments", useMetadata = true, acceptedMinecraftVersions = "[1.12, 1.13)", certificateFingerprint = "aaaf83332a11df02406e9f266b1b65c1306f0f76")
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
