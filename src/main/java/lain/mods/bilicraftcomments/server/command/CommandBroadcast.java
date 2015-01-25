package lain.mods.bilicraftcomments.server.command;

import java.util.List;
import lain.mods.bilicraftcomments.server.Messenger;
import lain.mods.bilicraftcomments.server.Messenger.Message;
import lain.mods.bilicraftcomments.server.ServerProxy;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

public class CommandBroadcast extends CommandBase
{

    @Override
    public void execute(ICommandSender arg0, String[] arg1) throws CommandException
    {
        if (arg1.length >= 4)
        {
            @SuppressWarnings("unchecked")
            List<EntityPlayerMP> players = PlayerSelector.matchEntities(arg0, arg1[0], EntityPlayerMP.class);
            if (players.isEmpty())
                throw new PlayerNotFoundException();
            int mode = parseInt(arg1[1], 0, 3);
            int lifespan = parseInt(arg1[2], -1);
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

    @Override
    public String getCommandUsage(ICommandSender arg0)
    {
        return null;
    }

    @Override
    public String getName()
    {
        return "bcc_broadcast";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

}
