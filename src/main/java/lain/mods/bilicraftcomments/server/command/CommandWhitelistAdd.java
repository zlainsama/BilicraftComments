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

public class CommandWhitelistAdd extends CommandBase
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
            ServerProxy.INSTANCE.whitelist.add(args[0]);
            try
            {
                ServerProxy.INSTANCE.whitelist.saveFile(ServerProxy.INSTANCE.whitelist.currentFile);
            }
            catch (Exception e)
            {
                BilicraftCommentsServer.logger.fatal("error saving whitelist file: " + e.toString());
                throw new RuntimeException(e);
            }
            Messenger.sendWithColor(sender, Message.msgWhitelistAdded, TextFormatting.RED, args[0]);
        }
        else
            throw new WrongUsageException(Message.msgWhitelistUsageAdd.key);
    }

    @Override
    public String getName()
    {
        return "bcc_whitelist_add";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 3;
    }

    @Override
    public String getUsage(ICommandSender arg0)
    {
        return Message.msgWhitelistUsageAdd.key;
    }

}
