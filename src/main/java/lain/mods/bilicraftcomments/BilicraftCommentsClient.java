package lain.mods.bilicraftcomments;

import lain.mods.bilicraftcomments.client.ClientProxy;
import org.apache.logging.log4j.Logger;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "BilicraftComments|Client", useMetadata = true)
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
