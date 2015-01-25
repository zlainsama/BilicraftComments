package lain.mods.bilicraftcomments.server;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class PerPlayerTimeMarker
{

    private static final String TAGBASE = "BcC|S:PerPlayerTimer";

    private final String name;
    private final boolean persistent;

    public PerPlayerTimeMarker(String name)
    {
        this.name = name;
        this.persistent = false;
    }

    public PerPlayerTimeMarker(String name, boolean persistent)
    {
        this.name = name;
        this.persistent = persistent;
    }

    public boolean checkTimeIfValid(EntityPlayer player, long time, long delay)
    {
        return checkTimeIfValid(player, time, delay, true);
    }

    public boolean checkTimeIfValid(EntityPlayer player, long time, long delay, boolean auto)
    {
        long lastMark = getLastMark(player);
        if (time < lastMark)
        {
            markTime(player, time);
            return false;
        }
        else if (lastMark + delay <= time)
        {
            if (auto)
                markTime(player, time);
            return true;
        }
        return false;
    }

    public void clear(EntityPlayer player)
    {
        getBase(player).setLong(name, 0L);
    }

    private NBTTagCompound getBase(EntityPlayer player)
    {
        NBTTagCompound tmp = player.getEntityData();
        if (persistent)
        {
            if (!tmp.hasKey(EntityPlayer.PERSISTED_NBT_TAG, 10))
                tmp.setTag(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());
            tmp = tmp.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        }
        if (!tmp.hasKey(TAGBASE, 10))
            tmp.setTag(TAGBASE, new NBTTagCompound());
        return tmp.getCompoundTag(TAGBASE);
    }

    public long getLastMark(EntityPlayer player)
    {
        return getBase(player).getLong(name);
    }

    public void markTime(EntityPlayer player, long time)
    {
        getBase(player).setLong(name, time);
    }

}
