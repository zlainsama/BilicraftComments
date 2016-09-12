package lain.mods.bilicraftcomments.server;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class Messenger
{

    public static enum Message
    {
        msgInternalError("BcC_InternalError"),
        msgNotInWhitelist("BcC_NotInWhitelist"),
        msgInBlacklist("BcC_InBlacklist"),
        msgTooFastToComment("BcC_TooFastToComment"),
        msgInvalidArguments("BcC_InvalidArguments"),
        msgBroadcastUsage("BcC_Broadcast_Usage"),
        msgWhitelistUsageAdd("BcC_Whitelist_Usage_Add"),
        msgWhitelistUsageRemove("BcC_Whitelist_Usage_Remove"),
        msgWhitelistAdded("BcC_Whitelist_Added"),
        msgWhitelistRemoved("BcC_Whitelist_Removed"),
        msgBlacklistUsageAdd("BcC_Blacklist_Usage_Add"),
        msgBlacklistUsageRemove("BcC_Blacklist_Usage_Remove"),
        msgBlacklistAdded("BcC_Blacklist_Added"),
        msgBlacklistRemoved("BcC_Blacklist_Removed"),
        msgNoPermission("BcC_NoPermission"),
        msgReloaded("BcC_Reloaded");

        public final String key;

        private Message(String key)
        {
            this.key = key;
        }
    }

    public static void send(ICommandSender receiver, Message msg)
    {
        ITextComponent tmp = new TextComponentTranslation(msg.key, new Object[0]);
        receiver.addChatMessage(tmp);
    }

    public static void send(ICommandSender receiver, Message msg, Object... objects)
    {
        ITextComponent tmp = new TextComponentTranslation(msg.key, objects);
        receiver.addChatMessage(tmp);
    }

    public static void sendWithColor(ICommandSender receiver, Message msg, TextFormatting color)
    {
        ITextComponent tmp = new TextComponentTranslation(msg.key, new Object[0]);
        tmp.getStyle().setColor(color);
        receiver.addChatMessage(tmp);
    }

    public static void sendWithColor(ICommandSender receiver, Message msg, TextFormatting color, Object... objects)
    {
        ITextComponent tmp = new TextComponentTranslation(msg.key, objects);
        tmp.getStyle().setColor(color);
        receiver.addChatMessage(tmp);
    }

}
