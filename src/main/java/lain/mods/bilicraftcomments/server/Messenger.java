package lain.mods.bilicraftcomments.server;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

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
        msgBlacklistRemoved("BcC_Blacklist_Removed");

        public final String key;

        private Message(String key)
        {
            this.key = key;
        }
    }

    public static void send(ICommandSender receiver, Message msg)
    {
        IChatComponent tmp = new ChatComponentTranslation(msg.key, new Object[0]);
        receiver.addChatMessage(tmp);
    }

    public static void send(ICommandSender receiver, Message msg, Object... objects)
    {
        IChatComponent tmp = new ChatComponentTranslation(msg.key, objects);
        receiver.addChatMessage(tmp);
    }

    public static void sendWithColor(ICommandSender receiver, Message msg, EnumChatFormatting color)
    {
        IChatComponent tmp = new ChatComponentTranslation(msg.key, new Object[0]);
        tmp.getChatStyle().setColor(color);
        receiver.addChatMessage(tmp);
    }

    public static void sendWithColor(ICommandSender receiver, Message msg, EnumChatFormatting color, Object... objects)
    {
        IChatComponent tmp = new ChatComponentTranslation(msg.key, objects);
        tmp.getChatStyle().setColor(color);
        receiver.addChatMessage(tmp);
    }

}
