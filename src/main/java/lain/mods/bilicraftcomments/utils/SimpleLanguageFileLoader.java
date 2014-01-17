package lain.mods.bilicraftcomments.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import lain.mods.bilicraftcomments.io.UnicodeInputStreamReader;
import com.google.common.io.CharStreams;
import com.google.common.io.LineProcessor;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class SimpleLanguageFileLoader
{

    public static void load(InputStream datastream, String lang) throws IOException
    {
        final String l = lang;
        CharStreams.readLines(new UnicodeInputStreamReader(datastream, "UTF-8"), new LineProcessor<Void>()
        {
            @Override
            public Void getResult()
            {
                return null;
            }

            @Override
            public boolean processLine(String line) throws IOException
            {
                line = line.trim();
                if (line.startsWith("#"))
                    return true;
                if (line.indexOf("=") == -1)
                    return true;
                String k = line.substring(0, line.indexOf("=")).trim();
                String v = line.substring(line.indexOf("=") + 1).trim();
                LanguageRegistry.instance().addStringLocalization(k, l, v);
                return true;
            }
        });
    }

    public static void load(String location, String lang) throws IOException
    {
        load(SimpleLanguageFileLoader.class.getResource(location), lang);
    }

    public static void load(URL location, String lang) throws IOException
    {
        load(location.openStream(), lang);
    }

    public static boolean loadSafe(InputStream datastream, String lang)
    {
        try
        {
            load(datastream, lang);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public static boolean loadSafe(String location, String lang)
    {
        try
        {
            load(SimpleLanguageFileLoader.class.getResource(location), lang);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public static boolean loadSafe(URL location, String lang)
    {
        try
        {
            load(location.openStream(), lang);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

}
