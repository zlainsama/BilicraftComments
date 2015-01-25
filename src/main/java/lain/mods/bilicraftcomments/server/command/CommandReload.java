package lain.mods.bilicraftcomments.server.command;

import lain.mods.bilicraftcomments.BilicraftCommentsServer;
import lain.mods.bilicraftcomments.server.Messenger;
import lain.mods.bilicraftcomments.server.Messenger.Message;
import lain.mods.bilicraftcomments.server.ServerConfigs;
import lain.mods.bilicraftcomments.server.ServerProxy;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

public class CommandReload extends CommandBase
{

    @Override
    public void execute(ICommandSender arg0, String[] arg1) throws CommandException
    {
        ServerConfigs.reload();
        try
        {
            ServerProxy.INSTANCE.whitelist.loadFile(ServerProxy.INSTANCE.whitelist.currentFile);
        }
        catch (Exception e)
        {
            BilicraftCommentsServer.logger.fatal("error loading whitelist file: " + e.toString());
            throw new RuntimeException(e);
        }
        try
        {
            ServerProxy.INSTANCE.blacklist.loadFile(ServerProxy.INSTANCE.blacklist.currentFile);
        }
        catch (Exception e)
        {
            BilicraftCommentsServer.logger.fatal("error loading blacklist file: " + e.toString());
            throw new RuntimeException(e);
        }
        Messenger.sendWithColor(arg0, Message.msgReloaded, EnumChatFormatting.DARK_RED);
    }

    @Override
    public String getCommandUsage(ICommandSender arg0)
    {
        return null;
    }

    @Override
    public String getName()
    {
        return "bcc_reload";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 3;
    }

}
