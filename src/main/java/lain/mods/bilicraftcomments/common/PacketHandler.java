package lain.mods.bilicraftcomments.common;

import ibxm.Player;
import lain.mods.bilicraftcomments.BilicraftComments;

public class PacketHandler implements IPacketHandler
{

    @Override
    public void onPacketData(INetworkManager paramINetworkManager, Packet250CustomPayload paramPacket250CustomPayload, Player paramPlayer)
    {
        if ("LC|BcC|R".equals(paramPacket250CustomPayload.channel))
            BilicraftComments.proxy.handleCommentRequest(paramINetworkManager, paramPacket250CustomPayload, paramPlayer);
        else if ("LC|BcC|D".equals(paramPacket250CustomPayload.channel))
            BilicraftComments.proxy.displayComment(paramINetworkManager, paramPacket250CustomPayload, paramPlayer);
    }

}
