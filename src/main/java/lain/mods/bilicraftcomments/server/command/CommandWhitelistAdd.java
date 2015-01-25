package lain.mods.bilicraftcomments.server.command;

import lain.mods.bilicraftcomments.BilicraftCommentsServer;
import lain.mods.bilicraftcomments.server.Messenger;
import lain.mods.bilicraftcomments.server.Messenger.Message;
import lain.mods.bilicraftcomments.server.ServerProxy;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

public class CommandWhitelistAdd extends CommandBase
{

    @Override
    public void execute(ICommandSender arg0, String[] arg1) throws CommandException
    {
        if (arg1.length > 0)
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
            ServerProxy.INSTANCE.whitelist.add(arg1[0]);
            try
            {
                ServerProxy.INSTANCE.whitelist.saveFile(ServerProxy.INSTANCE.whitelist.currentFile);
            }
            catch (Exception e)
            {
                BilicraftCommentsServer.logger.fatal("error saving whitelist file: " + e.toString());
                throw new RuntimeException(e);
            }
            Messenger.sendWithColor(arg0, Message.msgWhitelistAdded, EnumChatFormatting.DARK_RED, arg1[0]);
        }
        else
            Messenger.sendWithColor(arg0, Message.msgWhitelistUsageAdd, EnumChatFormatting.DARK_RED);
    }

    @Override
    public String getCommandUsage(ICommandSender arg0)
    {
        return null;
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

}
