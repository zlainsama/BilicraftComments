package lain.mods.bilicraftcomments;

import java.util.regex.Pattern;

public class MCUtils
{

    private static final Pattern patternControlCode = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");

    public static String stripControlCodes(String str)
    {
        return patternControlCode.matcher(str).replaceAll("");
    }

}
