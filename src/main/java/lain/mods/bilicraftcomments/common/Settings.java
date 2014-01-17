package lain.mods.bilicraftcomments.common;

import java.util.Set;
import lain.mods.bilicraftcomments.BilicraftComments;
import net.minecraftforge.common.config.Configuration;
import com.google.common.collect.Sets;

public class Settings
{

    public static String allowedMode = "0;1;2";
    public static int minLifespan = 40;
    public static int maxLifespan = 400;
    public static int commentInterval = 100;
    public static boolean whitelistMode = true;

    private static Set<Integer> sModes = Sets.newHashSet();
    private static int hModes = 0;

    public static boolean isModeAllowed(int mode)
    {
        if (hModes != allowedMode.hashCode())
        {
            sModes.clear();
            for (String s : allowedMode.split(";"))
            {
                try
                {
                    sModes.add(Integer.parseInt(s));
                }
                catch (NumberFormatException ignored)
                {
                }
            }
            hModes = allowedMode.hashCode();
        }
        return sModes.contains(mode);
    }

    public static void reload(Configuration config)
    {
        String b_allowedMode = allowedMode;
        int b_minLifespan = minLifespan;
        int b_maxLifespan = maxLifespan;
        int b_commentInterval = commentInterval;
        boolean b_whitelistMode = whitelistMode;
        try
        {
            allowedMode = "0;1;2";
            minLifespan = 40;
            maxLifespan = 400;
            commentInterval = 100;
            whitelistMode = true;
            config.load();
            allowedMode = config.get(Configuration.CATEGORY_GENERAL, "allowedMode", allowedMode).getString();
            minLifespan = config.get(Configuration.CATEGORY_GENERAL, "minLifespan", minLifespan).getInt(minLifespan);
            maxLifespan = config.get(Configuration.CATEGORY_GENERAL, "maxLifespan", maxLifespan).getInt(maxLifespan);
            commentInterval = config.get(Configuration.CATEGORY_GENERAL, "commentInterval", commentInterval).getInt(commentInterval);
            whitelistMode = config.get(Configuration.CATEGORY_GENERAL, "whitelistMode", whitelistMode).getBoolean(whitelistMode);
            config.save();
        }
        catch (Exception e)
        {
            allowedMode = b_allowedMode;
            minLifespan = b_minLifespan;
            maxLifespan = b_maxLifespan;
            commentInterval = b_commentInterval;
            whitelistMode = b_whitelistMode;
            BilicraftComments.logger.warn("error loading config", e);
        }
    }

}
