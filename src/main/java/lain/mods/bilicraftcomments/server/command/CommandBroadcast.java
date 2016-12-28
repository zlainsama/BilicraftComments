package lain.mods.bilicraftcomments.server.command;

import java.util.List;
import lain.mods.bilicraftcomments.server.Messenger.Message;
import lain.mods.bilicraftcomments.server.ServerProxy;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

public class CommandBroadcast extends CommandBase
{

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length >= 4)
        {
            List<EntityPlayerMP> players = EntitySelector.matchEntities(sender, args[0], EntityPlayerMP.class);
            if (players.isEmpty())
                throw new PlayerNotFoundException("commands.generic.player.notFound", args[0]);
            int mode = parseInt(args[1], 0, 3);
            int lifespan = parseInt(args[2], -1);
            StringBuilder buf = new StringBuilder();
            for (int i = 3; i < args.length; i++)
            {
                if (i > 3)
                    buf.append(" ");
                buf.append(args[i]);
            }
            String text = buf.toString().trim().replace("&", "\u00a7").replace("\u00a7\u00a7", "&");
            FMLProxyPacket packet = ServerProxy.INSTANCE.createDisplayRequest(mode, lifespan, text);
            ServerProxy.INSTANCE.chatLogger.info(String.format("[CONSOLE] [mode:%d] [lifespan:%d] %s", mode, lifespan, text));
            ServerProxy.INSTANCE.channel.sendToAll(packet);
        }
        else
        {
            throw new WrongUsageException(Message.msgBroadcastUsage.key);
        }
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

    @Override
    public String getUsage(ICommandSender arg0)
    {
        return Message.msgBroadcastUsage.key;
    }

}
