package lain.mods.bilicraftcomments.utils;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class Translator
{

    public final String key;

    public Translator(String key)
    {
        this.key = key;
    }

    public void send(ICommandSender receiver)
    {
        IChatComponent tmp = new ChatComponentTranslation(key, new Object[0]);
        receiver.func_145747_a(tmp);
    }

    public void send(ICommandSender receiver, Object... objects)
    {
        IChatComponent tmp = new ChatComponentTranslation(key, objects);
        receiver.func_145747_a(tmp);
    }

    public void sendWithColor(ICommandSender receiver, EnumChatFormatting color)
    {
        IChatComponent tmp = new ChatComponentTranslation(key, new Object[0]);
        tmp.func_150256_b().func_150238_a(color);
        receiver.func_145747_a(tmp);
    }

    public void sendWithColor(ICommandSender receiver, EnumChatFormatting color, Object... objects)
    {
        IChatComponent tmp = new ChatComponentTranslation(key, objects);
        tmp.func_150256_b().func_150238_a(color);
        receiver.func_145747_a(tmp);
    }

}
