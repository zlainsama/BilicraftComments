package lain.mods.bilicraftcomments.server.command;

import lain.mods.bilicraftcomments.server.Messenger;
import lain.mods.bilicraftcomments.server.Messenger.Message;
import lain.mods.bilicraftcomments.server.ServerProxy;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumChatFormatting;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;

public class CommandBroadcast extends CommandBase
{

    @Override
    public String getCommandName()
    {
        return "bcc_broadcast";
    }

    @Override
    public String getCommandUsage(ICommandSender arg0)
    {
        return null;
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public void processCommand(ICommandSender arg0, String[] arg1)
    {
        if (arg1.length >= 4)
        {
            EntityPlayerMP[] players = PlayerSelector.matchPlayers(arg0, arg1[0]);
            if (players == null)
            {
                players = new EntityPlayerMP[] { FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().func_152612_a(arg1[0]) };
                if (players[0] == null)
                    throw new PlayerNotFoundException();
            }
            int mode = parseIntBounded(arg0, arg1[1], 0, 3);
            int lifespan = parseIntWithMin(arg0, arg1[2], -1);
            StringBuilder buf = new StringBuilder();
            for (int i = 3; i < arg1.length; i++)
            {
                if (i > 3)
                    buf.append(" ");
                buf.append(arg1[i]);
            }
            String text = buf.toString().trim().replace("&", "\u00a7").replace("\u00a7\u00a7", "&");
            FMLProxyPacket packet = ServerProxy.INSTANCE.createDisplayRequest(mode, lifespan, text);
            ServerProxy.INSTANCE.chatLogger.info(String.format("[CONSOLE] [mode:%d] [lifespan:%d] %s", mode, lifespan, text));
            ServerProxy.INSTANCE.channel.sendToAll(packet);
        }
        else
        {
            Messenger.sendWithColor(arg0, Message.msgBroadcastUsage, EnumChatFormatting.DARK_RED);
        }
    }

}
