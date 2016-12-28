package lain.mods.bilicraftcomments.server.command;

import lain.mods.bilicraftcomments.BilicraftCommentsServer;
import lain.mods.bilicraftcomments.server.Messenger;
import lain.mods.bilicraftcomments.server.Messenger.Message;
import lain.mods.bilicraftcomments.server.ServerConfigs;
import lain.mods.bilicraftcomments.server.ServerProxy;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;

public class CommandReload extends CommandBase
{

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
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
        Messenger.sendWithColor(sender, Message.msgReloaded, TextFormatting.RED);
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

    @Override
    public String getUsage(ICommandSender arg0)
    {
        return Message.msgReloadUsage.key;
    }

}
