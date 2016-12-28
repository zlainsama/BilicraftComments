package lain.mods.bilicraftcomments.server.command;

import lain.mods.bilicraftcomments.BilicraftCommentsServer;
import lain.mods.bilicraftcomments.server.Messenger;
import lain.mods.bilicraftcomments.server.Messenger.Message;
import lain.mods.bilicraftcomments.server.ServerProxy;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;

public class CommandWhitelistRemove extends CommandBase
{

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length > 0)
        {
            try
            {
                ServerProxy.INSTANCE.whitelist.loadFile(ServerProxy.INSTANCE.whitelist.currentFile);
            }
            catch (Exception e)
            {
                BilicraftCommentsServer.logger.fatal("error loading whitelist file: " + e.toString());
                throw new RuntimeException(e);
            }
            ServerProxy.INSTANCE.whitelist.remove(args[0]);
            try
            {
                ServerProxy.INSTANCE.whitelist.saveFile(ServerProxy.INSTANCE.whitelist.currentFile);
            }
            catch (Exception e)
            {
                BilicraftCommentsServer.logger.fatal("error saving whitelist file: " + e.toString());
                throw new RuntimeException(e);
            }
            Messenger.sendWithColor(sender, Message.msgWhitelistRemoved, TextFormatting.RED, args[0]);
        }
        else
            throw new WrongUsageException(Message.msgWhitelistUsageRemove.key);
    }

    @Override
    public String getName()
    {
        return "bcc_whitelist_remove";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 3;
    }

    @Override
    public String getUsage(ICommandSender arg0)
    {
        return Message.msgWhitelistUsageRemove.key;
    }

}
