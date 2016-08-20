package lain.mods.bilicraftcomments.server.command;

import lain.mods.bilicraftcomments.BilicraftCommentsServer;
import lain.mods.bilicraftcomments.server.Messenger;
import lain.mods.bilicraftcomments.server.Messenger.Message;
import lain.mods.bilicraftcomments.server.ServerProxy;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;

public class CommandBlacklistAdd extends CommandBase
{

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length > 0)
        {
            try
            {
                ServerProxy.INSTANCE.blacklist.loadFile(ServerProxy.INSTANCE.blacklist.currentFile);
            }
            catch (Exception e)
            {
                BilicraftCommentsServer.logger.fatal("error loading blacklist file: " + e.toString());
                throw new RuntimeException(e);
            }
            ServerProxy.INSTANCE.blacklist.add(args[0]);
            try
            {
                ServerProxy.INSTANCE.blacklist.saveFile(ServerProxy.INSTANCE.blacklist.currentFile);
            }
            catch (Exception e)
            {
                BilicraftCommentsServer.logger.fatal("error saving blacklist file: " + e.toString());
                throw new RuntimeException(e);
            }
            Messenger.sendWithColor(sender, Message.msgBlacklistAdded, TextFormatting.DARK_RED, args[0]);
        }
        else
            Messenger.sendWithColor(sender, Message.msgBlacklistUsageAdd, TextFormatting.DARK_RED);
    }

    @Override
    public String getCommandUsage(ICommandSender arg0)
    {
        return null;
    }

    @Override
    public String getCommandName()
    {
        return "bcc_blacklist_add";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 3;
    }

}
