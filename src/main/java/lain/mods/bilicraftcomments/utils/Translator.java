package lain.mods.bilicraftcomments.utils;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;

public class Translator
{

    public final String key;

    public Translator(String key)
    {
        this.key = key;
    }

    public void send(ICommandSender receiver)
    {
        receiver.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey(key));
    }

    public void send(ICommandSender receiver, Object... objects)
    {
        receiver.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions(key, objects));
    }

    public void sendWithColor(ICommandSender receiver, EnumChatFormatting color)
    {
        receiver.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey(key).setColor(color));
    }

    public void sendWithColor(ICommandSender receiver, EnumChatFormatting color, Object... objects)
    {
        receiver.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions(key, objects).setColor(color));
    }

}
