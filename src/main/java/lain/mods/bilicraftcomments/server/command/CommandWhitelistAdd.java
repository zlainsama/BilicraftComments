package lain.mods.bilicraftcomments.server.command;

import lain.mods.bilicraftcomments.BilicraftCommentsServer;
import lain.mods.bilicraftcomments.server.Messenger;
import lain.mods.bilicraftcomments.server.Messenger.Message;
import lain.mods.bilicraftcomments.server.ServerProxy;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

public class CommandWhitelistAdd extends CommandBase
{

    @Override
    public String getCommandName()
    {
        return "bcc_whitelist_add";
    }

    @Override
    public String getCommandUsage(ICommandSender arg0)
    {
        return null;
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 3;
    }

    @Override
    public void processCommand(ICommandSender arg0, String[] arg1)
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
            Messenger.send(arg0, Message.msgWhitelistAdded, EnumChatFormatting.DARK_RED, arg1[0]);
        }
        else
            Messenger.send(arg0, Message.msgWhitelistUsageAdd, EnumChatFormatting.DARK_RED);
    }

}
