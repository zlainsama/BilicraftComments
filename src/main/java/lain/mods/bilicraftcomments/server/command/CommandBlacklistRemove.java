package lain.mods.bilicraftcomments.server.command;

import lain.mods.bilicraftcomments.BilicraftCommentsServer;
import lain.mods.bilicraftcomments.server.Messenger;
import lain.mods.bilicraftcomments.server.Messenger.Message;
import lain.mods.bilicraftcomments.server.ServerProxy;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

public class CommandBlacklistRemove extends CommandBase
{

    @Override
    public String getCommandName()
    {
        return "bcc_blacklist_remove";
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
                ServerProxy.INSTANCE.blacklist.loadFile(ServerProxy.INSTANCE.blacklist.currentFile);
            }
            catch (Exception e)
            {
                BilicraftCommentsServer.logger.fatal("error loading blacklist file: " + e.toString());
                throw new RuntimeException(e);
            }
            ServerProxy.INSTANCE.blacklist.remove(arg1[0]);
            try
            {
                ServerProxy.INSTANCE.blacklist.saveFile(ServerProxy.INSTANCE.blacklist.currentFile);
            }
            catch (Exception e)
            {
                BilicraftCommentsServer.logger.fatal("error saving blacklist file: " + e.toString());
                throw new RuntimeException(e);
            }
            Messenger.sendWithColor(arg0, Message.msgBlacklistRemoved, EnumChatFormatting.DARK_RED, arg1[0]);
        }
        else
            Messenger.sendWithColor(arg0, Message.msgBlacklistUsageRemove, EnumChatFormatting.DARK_RED);
    }

}
