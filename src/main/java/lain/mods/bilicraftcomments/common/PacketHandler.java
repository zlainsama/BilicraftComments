package lain.mods.bilicraftcomments.common;

import lain.mods.bilicraftcomments.BilicraftComments;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

public class PacketHandler
{

    private static FMLEventChannel ChannelDisplay;

    public static void load()
    {
        ChannelDisplay = NetworkRegistry.INSTANCE.newEventDrivenChannel("LC|BcC|D");

        ChannelDisplay.register(new PacketHandler(1));
    }

    private final int T;

    private PacketHandler(int T)
    {
        this.T = T;
    }

    @SubscribeEvent
    public void onCustomPacketEvent(ClientCustomPacketEvent event)
    {
        switch (T)
        {
            case 1:
                BilicraftComments.proxy.displayComment(event.packet);
                break;
        }
    }

    @SubscribeEvent
    public void onCustomPacketEvent(ServerCustomPacketEvent event)
    {
        switch (T)
        {
            case 1:
                BilicraftComments.proxy.displayComment(event.packet);
                break;
        }
    }

}
