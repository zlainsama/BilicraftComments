package lain.mods.bilicraftcomments.server;

import java.io.File;
import java.util.Set;
import net.minecraftforge.common.config.Configuration;
import com.google.common.collect.Sets;

public class ServerConfigs
{

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

    public static void load(File configFile)
    {
        config = new Configuration(configFile);

        sync();
    }

    public static void reload()
    {
        config.load();

        sync();
    }

    protected static void sync()
    {
        allowedMode = config.get(Configuration.CATEGORY_GENERAL, "allowedMode", "0;1;2").getString();
        minLifespan = config.get(Configuration.CATEGORY_GENERAL, "minLifespan", 40).getInt(40);
        maxLifespan = config.get(Configuration.CATEGORY_GENERAL, "maxLifespan", 400).getInt(400);
        commentInterval = config.get(Configuration.CATEGORY_GENERAL, "commentInterval", 100).getInt(100);
        whitelistMode = config.get(Configuration.CATEGORY_GENERAL, "whitelistMode", true).getBoolean(true);
        allowLANServer = config.get(Configuration.CATEGORY_GENERAL, "allowLANServer", false).getBoolean(false);

        if (config.hasChanged())
            config.save();
    }

    private static Configuration config;

    public static String allowedMode = "0;1;2";
    public static int minLifespan = 40;
    public static int maxLifespan = 400;
    public static int commentInterval = 100;
    public static boolean whitelistMode = true;
    public static boolean allowLANServer = false;

    private static Set<Integer> sModes = Sets.newHashSet();
    private static int hModes = 0;

}
