package lain.mods.bilicraftcomments.common;

import lain.mods.bilicraftcomments.BilicraftComments;
import lain.mods.bilicraftcomments.utils.Translator;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StringUtils;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.Player;

public class CommonProxy
{

    Translator msgInternalError = new Translator("BcC_InternalError");
    Translator msgNotInWhitelist = new Translator("BcC_NotInWhitelist");
    Translator msgInBlacklist = new Translator("BcC_InBlacklist");
    Translator msgTooFastToComment = new Translator("BcC_TooFastToComment");
    Translator msgInvalidArguments = new Translator("BcC_InvalidArguments");
    Translator msgOutdatedProtocol = new Translator("BcC_OutdatedProtocol");
    Translator msgBroadcastUsage = new Translator("BcC_Broadcast_Usage");
    Translator msgWhitelistUsageAdd = new Translator("BcC_Whitelist_Usage_Add");
    Translator msgWhitelistUsageRemove = new Translator("BcC_Whitelist_Usage_Remove");
    Translator msgWhitelistAdded = new Translator("BcC_Whitelist_Added");
    Translator msgWhitelistRemoved = new Translator("BcC_Whitelist_Removed");
    Translator msgBlacklistUsageAdd = new Translator("BcC_Blacklist_Usage_Add");
    Translator msgBlacklistUsageRemove = new Translator("BcC_Blacklist_Usage_Remove");
    Translator msgBlacklistAdded = new Translator("BcC_Blacklist_Added");
    Translator msgBlacklistRemoved = new Translator("BcC_Blacklist_Removed");

    public void displayComment(INetworkManager manager, Packet250CustomPayload packet, Player player)
    {
    }

    public void handleCommentRequest(EntityPlayerMP plr, String[] args)
    {
        if (args.length >= 3)
        {
            ExtendedPlayerProperties prop = ExtendedPlayerProperties.getProperties(plr);
            if (prop == null)
            {
                msgInternalError.sendWithColor(plr, EnumChatFormatting.DARK_RED);
                return;
            }
            if (Settings.whitelistMode && !Whitelist.contains(plr.username))
            {
                msgNotInWhitelist.sendWithColor(plr, EnumChatFormatting.DARK_RED);
                return;
            }
            if (Blacklist.contains(plr.username))
            {
                msgInBlacklist.sendWithColor(plr, EnumChatFormatting.DARK_RED);
                return;
            }
            if (!prop.timer.checkTimeIfValid(plr.worldObj.getTotalWorldTime(), Settings.commentInterval, false))
            {
                msgTooFastToComment.sendWithColor(plr, EnumChatFormatting.DARK_RED);
                return;
            }
            int mode = Integer.parseInt(args[0]);
            int lifespan = Integer.parseInt(args[1]);
            StringBuilder buf = new StringBuilder();
            for (int i = 2; i < args.length; i++)
            {
                if (i > 2)
                    buf.append(" ");
                buf.append(args[i]);
            }
            String text = buf.toString().trim().replace("&", "\u00a7").replace("\u00a7\u00a7", "&");
            if (!Settings.isModeAllowed(mode) || lifespan < Settings.minLifespan || lifespan > Settings.maxLifespan || StringUtils.stripControlCodes(text).isEmpty())
            {
                msgInvalidArguments.sendWithColor(plr, EnumChatFormatting.DARK_RED);
                return;
            }
            if (BilicraftComments.manager != null)
            {
                if (!BilicraftComments.manager.hasPermission(plr.username, "BcC.commentMode." + mode))
                {
                    msgInvalidArguments.sendWithColor(plr, EnumChatFormatting.DARK_RED);
                    return;
                }
                if (!BilicraftComments.manager.hasPermission(plr.username, "BcC.colorComments"))
                    text = StringUtils.stripControlCodes(text);
            }
            BilicraftComments.logger.log(BilicraftComments.CommentLoggingLevel.level, String.format("[username:%s] [mode:%d] [lifespan:%d] %s", plr.username, mode, lifespan, text));
            prop.timer.markTime(plr.worldObj.getTotalWorldTime());
            Packet packet = BilicraftComments.createDisplayPacket(mode, lifespan, EnumChatFormatting.RESET + plr.getTranslatedEntityName() + " > " + text);
            for (Object o : FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList)
            {
                if (o instanceof EntityPlayerMP)
                    ((EntityPlayerMP) o).playerNetServerHandler.sendPacketToPlayer(packet);
            }
        }
    }

    public void handleCommentRequest(INetworkManager manager, Packet250CustomPayload packet, Player player)
    {
        if (player instanceof EntityPlayerMP)
        {
            msgOutdatedProtocol.sendWithColor((EntityPlayerMP) player, EnumChatFormatting.DARK_RED);
            // EntityPlayerMP plr = (EntityPlayerMP) player;
            // ExtendedPlayerProperties prop = ExtendedPlayerProperties.getProperties(plr);
            // if (prop == null)
            // {
            // msgInternalError.s(plr, EnumChatFormatting.DARK_RED.toString());
            // return;
            // }
            // if (Settings.whitelistMode && !Whitelist.contains(plr.username))
            // {
            // msgNotInWhitelist.s(plr, EnumChatFormatting.DARK_RED.toString());
            // return;
            // }
            // if (Blacklist.contains(plr.username))
            // {
            // msgInBlacklist.s(plr, EnumChatFormatting.DARK_RED.toString());
            // return;
            // }
            // if (!prop.timer.checkTimeIfValid(plr.worldObj.getTotalWorldTime(), Settings.commentInterval, false))
            // {
            // msgTooFastToComment.s(plr, EnumChatFormatting.DARK_RED.toString());
            // return;
            // }
            // DataInputStream dis = null;
            // try
            // {
            // dis = new DataInputStream(new ByteArrayInputStream(packet.data));
            // int mode = dis.readShort();
            // int lifespan = dis.readShort();
            // String text = dis.readUTF().trim().replace("&", "\u00a7").replace("\u00a7\u00a7", "&");
            // if (!Settings.isModeAllowed(mode) || lifespan < Settings.minLifespan || lifespan > Settings.maxLifespan || StringUtils.stripControlCodes(text).isEmpty())
            // {
            // msgInvalidArguments.s(plr, EnumChatFormatting.DARK_RED.toString());
            // return;
            // }
            // if (BilicraftComments.manager != null)
            // {
            // if (!BilicraftComments.manager.hasPermission(plr.username, "BcC.commentMode." + mode))
            // {
            // msgInvalidArguments.s(plr, EnumChatFormatting.DARK_RED.toString());
            // return;
            // }
            // if (!BilicraftComments.manager.hasPermission(plr.username, "BcC.colorComments"))
            // text = StringUtils.stripControlCodes(text);
            // }
            // BilicraftComments.logger.log(LevelComment.comment, String.format("[username:%s] [mode:%d] [lifespan:%d] %s", plr.username, mode, lifespan, text));
            // prop.timer.markTime(plr.worldObj.getTotalWorldTime());
            // packet = BilicraftComments.createDisplayPacket(mode, lifespan, EnumChatFormatting.RESET + plr.getTranslatedEntityName() + " > " + text);
            // for (Object o : FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList)
            // {
            // if (o instanceof EntityPlayerMP)
            // ((EntityPlayerMP) o).playerNetServerHandler.sendPacketToPlayer(packet);
            // }
            // }
            // catch (IOException e)
            // {
            // System.err.println("error reading incoming comment request: " + e.toString());
            // }
            // finally
            // {
            // if (dis != null)
            // try
            // {
            // dis.close();
            // }
            // catch (IOException ignored)
            // {
            // }
            // }
        }
    }

    public void load()
    {
        Whitelist.load();
        Blacklist.load();
        ExtendedPlayerProperties.load();
    }

    public void registerCommands(ICommandManager manager)
    {
        if (manager instanceof CommandHandler)
        {
            CommandHandler h = (CommandHandler) manager;
            h.registerCommand(new CommandBase()
            {
                @Override
                public int compareTo(Object arg0)
                {
                    if (arg0 instanceof ICommand)
                        return getCommandName().compareTo(((ICommand) arg0).getCommandName());
                    return 0;
                }

                @Override
                public String getCommandName()
                {
                    return "bcc_broadcast";
                }

                @Override
                public String getCommandUsage(ICommandSender arg0)
                {
                    return null;
                }

                @Override
                public int getRequiredPermissionLevel()
                {
                    return 2;
                }

                @Override
                public void processCommand(ICommandSender arg0, String[] arg1)
                {
                    if (arg1.length >= 4)
                    {
                        EntityPlayerMP[] players = PlayerSelector.matchPlayers(arg0, arg1[0]);
                        if (players == null)
                        {
                            players = new EntityPlayerMP[] { FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getPlayerForUsername(arg1[0]) };
                            if (players[0] == null)
                                throw new PlayerNotFoundException();
                        }
                        int mode = parseIntBounded(arg0, arg1[1], 0, 3);
                        int lifespan = parseIntWithMin(arg0, arg1[2], -1);
                        StringBuilder buf = new StringBuilder();
                        for (int i = 3; i < arg1.length; i++)
                        {
                            if (i > 3)
                                buf.append(" ");
                            buf.append(arg1[i]);
                        }
                        String text = buf.toString().trim().replace("&", "\u00a7").replace("\u00a7\u00a7", "&");
                        BilicraftComments.logger.log(BilicraftComments.CommentLoggingLevel.level, String.format("[CONSOLE](target:%s) [mode:%d] [lifespan:%d] %s", arg1[0], mode, lifespan, text));
                        Packet250CustomPayload packet = BilicraftComments.createDisplayPacket(mode, lifespan, text);
                        for (EntityPlayerMP player : players)
                            player.playerNetServerHandler.sendPacketToPlayer(packet);
                    }
                    else
                    {
                        msgBroadcastUsage.sendWithColor(arg0, EnumChatFormatting.DARK_RED);
                    }
                }
            });
            h.registerCommand(new CommandBase()
            {
                @Override
                public int compareTo(Object arg0)
                {
                    if (arg0 instanceof ICommand)
                        return getCommandName().compareTo(((ICommand) arg0).getCommandName());
                    return 0;
                }

                @Override
                public String getCommandName()
                {
                    return "bcc_reload";
                }

                @Override
                public String getCommandUsage(ICommandSender paramICommandSender)
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
                    Settings.reload(BilicraftComments.config);
                    Whitelist.load();
                    Blacklist.load();
                }
            });
            h.registerCommand(new CommandBase()
            {
                @Override
                public int compareTo(Object arg0)
                {
                    if (arg0 instanceof ICommand)
                        return getCommandName().compareTo(((ICommand) arg0).getCommandName());
                    return 0;
                }

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
                        Whitelist.load();
                        Whitelist.add(arg1[0]);
                        Whitelist.save();
                        msgWhitelistAdded.sendWithColor(arg0, EnumChatFormatting.DARK_RED, arg1[0]);
                    }
                    else
                        msgWhitelistUsageAdd.sendWithColor(arg0, EnumChatFormatting.DARK_RED);
                }
            });
            h.registerCommand(new CommandBase()
            {
                @Override
                public int compareTo(Object arg0)
                {
                    if (arg0 instanceof ICommand)
                        return getCommandName().compareTo(((ICommand) arg0).getCommandName());
                    return 0;
                }

                @Override
                public String getCommandName()
                {
                    return "bcc_whitelist_remove";
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
                        Whitelist.load();
                        Whitelist.remove(arg1[0]);
                        Whitelist.save();
                        msgWhitelistRemoved.sendWithColor(arg0, EnumChatFormatting.DARK_RED, arg1[0]);
                    }
                    else
                        msgWhitelistUsageRemove.sendWithColor(arg0, EnumChatFormatting.DARK_RED);
                }
            });
            h.registerCommand(new CommandBase()
            {
                @Override
                public int compareTo(Object arg0)
                {
                    if (arg0 instanceof ICommand)
                        return getCommandName().compareTo(((ICommand) arg0).getCommandName());
                    return 0;
                }

                @Override
                public String getCommandName()
                {
                    return "bcc_blacklist_add";
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
                        Blacklist.load();
                        Blacklist.add(arg1[0]);
                        Blacklist.save();
                        msgBlacklistAdded.sendWithColor(arg0, EnumChatFormatting.DARK_RED, arg1[0]);
                    }
                    else
                        msgBlacklistUsageAdd.sendWithColor(arg0, EnumChatFormatting.DARK_RED);
                }
            });
            h.registerCommand(new CommandBase()
            {
                @Override
                public int compareTo(Object arg0)
                {
                    if (arg0 instanceof ICommand)
                        return getCommandName().compareTo(((ICommand) arg0).getCommandName());
                    return 0;
                }

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
                        Blacklist.load();
                        Blacklist.remove(arg1[0]);
                        Blacklist.save();
                        msgBlacklistRemoved.sendWithColor(arg0, EnumChatFormatting.DARK_RED, arg1[0]);
                    }
                    else
                        msgBlacklistUsageRemove.sendWithColor(arg0, EnumChatFormatting.DARK_RED);
                }
            });
            h.registerCommand(new CommandBase()
            {
                @Override
                public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender)
                {
                    return par1ICommandSender instanceof EntityPlayerMP;
                }

                @Override
                public int compareTo(Object arg0)
                {
                    if (arg0 instanceof ICommand)
                        return getCommandName().compareTo(((ICommand) arg0).getCommandName());
                    return 0;
                }

                @Override
                public String getCommandName()
                {
                    return "bcc_comment";
                }

                @Override
                public String getCommandUsage(ICommandSender arg0)
                {
                    return "****DO NOT USE THIS, ITS FOR INTERNAL USE****";
                }

                @Override
                public int getRequiredPermissionLevel()
                {
                    return 0;
                }

                @Override
                public void processCommand(ICommandSender arg0, String[] arg1)
                {
                    handleCommentRequest((EntityPlayerMP) arg0, arg1);
                }
            });
        }
    }

}
