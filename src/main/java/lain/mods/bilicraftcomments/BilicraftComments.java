package lain.mods.bilicraftcomments;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import lain.mods.bilicraftcomments.common.CommonProxy;
import lain.mods.bilicraftcomments.common.PacketHandler;
import lain.mods.bilicraftcomments.common.Settings;
import lain.mods.bilicraftcomments.utils.SimpleLanguageFileLoader;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(modid = "BilicraftComments", useMetadata = true)
@NetworkMod(clientSideRequired = true, serverSideRequired = false, channels = { "LC|BcC|R", "LC|BcC|D" }, packetHandler = PacketHandler.class)
public class BilicraftComments
{

    public static class CommentLoggingLevel extends Level
    {
        private static final long serialVersionUID = -1325549084292721888L;

        public static final Level level = new CommentLoggingLevel("COMMENT", FINEST.intValue(), FINEST.getResourceBundleName());

        protected CommentLoggingLevel(String name, int value, String resourceBundleName)
        {
            super(name, value, resourceBundleName);
        }
    }

    @SidedProxy(serverSide = "lain.mods.bilicraftcomments.common.CommonProxy", clientSide = "lain.mods.bilicraftcomments.client.ClientProxy")
    public static CommonProxy proxy;

    public static File rootDir;
    public static Configuration config;
    public static Logger logger;

    public static IPermissionManager manager;

    public static final int logLimit = 33554432;

    public static Packet250CustomPayload createDisplayPacket(int mode, int lifespan, String text)
    {
        return createPacket("LC|BcC|D", mode, lifespan, text);
    }

    public static Packet250CustomPayload createPacket(String channel, int mode, int lifespan, String text)
    {
        DataOutputStream dos = null;
        try
        {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            dos = new DataOutputStream(buf);
            dos.writeShort(mode);
            dos.writeShort(lifespan);
            dos.writeUTF(text);
            dos.flush();
            return new Packet250CustomPayload(channel, buf.toByteArray());
        }
        catch (Exception e)
        {
            logger.warning("error creating packet: " + e.toString());
            return null;
        }
        finally
        {
            if (dos != null)
                try
                {
                    dos.close();
                }
                catch (IOException ignored)
                {
                }
        }
    }

    public static String createRequestCommandLine(int mode, int lifespan, String text)
    {
        return String.format("/bcc_comment %d %d %s", mode, lifespan, text);
    }

    public static Packet250CustomPayload createRequestPacket(int mode, int lifespan, String text)
    {
        return createPacket("LC|BcC|R", mode, lifespan, text);
    }

    @Mod.EventHandler
    public void init(FMLPreInitializationEvent event)
    {
        rootDir = event.getModConfigurationDirectory().getParentFile();
        config = new Configuration(event.getSuggestedConfigurationFile());
        logger = event.getModLog();

        try
        {
            File logPath = new File(rootDir, "BcC_CommentLog_%g.log");
            logger.addHandler(new FileHandler(logPath.getPath(), logLimit, 4, true)
            {
                {
                    setLevel(Level.ALL);
                    setFormatter(new Formatter()
                    {
                        String LINE_SEPARATOR = System.getProperty("line.separator");
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        @Override
                        public String format(LogRecord record)
                        {
                            if (record.getLevel() == CommentLoggingLevel.level)
                            {
                                StringBuilder msg = new StringBuilder();
                                msg.append(dateFormat.format(Long.valueOf(record.getMillis())));
                                msg.append(" ");
                                msg.append(record.getMessage());
                                msg.append(LINE_SEPARATOR);
                                return msg.toString();
                            }
                            return "";
                        }
                    });
                }

                @Override
                public synchronized void close() throws SecurityException
                {
                }
            });
        }
        catch (Exception e)
        {
            System.err.println("error adding comment logger: " + e.toString());
        }

        Settings.reload(config);

        try
        {
            SimpleLanguageFileLoader.load("/assets/bilicraftcomments/lang/en_US.lang", "en_US");
        }
        catch (IOException e)
        {
            logger.warning("error loading en_US language file: " + e.toString());
        }
        try
        {
            SimpleLanguageFileLoader.load("/assets/bilicraftcomments/lang/zh_CN.lang", "zh_CN");
        }
        catch (IOException e)
        {
            logger.warning("error loading zh_CN language file: " + e.toString());
        }
    }

    @Mod.EventHandler
    public void load(FMLInitializationEvent event)
    {
        proxy.load();
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event)
    {
        proxy.registerCommands(event.getServer().getCommandManager());
    }

}
