package lain.mods.bilicraftcomments;

import lain.mods.bilicraftcomments.client.ClientProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = "bilicraftcomments|client", useMetadata = true, acceptedMinecraftVersions = "[1.12, 1.13)", certificateFingerprint = "aaaf83332a11df02406e9f266b1b65c1306f0f76")
public class BilicraftCommentsClient
{

    public static Logger logger;

    @Mod.EventHandler
    public void init(FMLPreInitializationEvent event)
    {
        if (!event.getSide().isClient())
            return;

        logger = event.getModLog();

        ClientProxy.setup();
    }

}
