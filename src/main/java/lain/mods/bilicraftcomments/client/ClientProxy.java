package lain.mods.bilicraftcomments.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import lain.mods.bilicraftcomments.BilicraftCommentsClient;
import lain.mods.bilicraftcomments.MCUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

public class ClientProxy
{

    public static final ClientProxy INSTANCE = new ClientProxy();

    private static final String TARGET = "BcC|S";
    private static final String LOCAL = "BcC|C";

    public static void setup()
    {
        if (INSTANCE == null)
            throw new RuntimeException();
    }

    private FMLEventChannel channel;

    protected final List<Comment> comments = new CopyOnWriteArrayList<Comment>();
    protected KeyBinding keyOpenCommentGui;
    private long ticks = 0L;

    private ClientProxy()
    {
        MinecraftForge.EVENT_BUS.register(this);

        keyOpenCommentGui = new KeyBinding("key.openCommentGui", 0x17, "key.categories.multiplayer");
        ClientRegistry.registerKeyBinding(keyOpenCommentGui);

        channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(LOCAL);
        channel.register(this);
    }

    @SubscribeEvent
    public void inputHook(InputEvent.KeyInputEvent event)
    {
        if (keyOpenCommentGui.isKeyDown())
        {
            Minecraft client = FMLClientHandler.instance().getClient();
            if (client != null && client.currentScreen == null)
                client.displayGuiScreen(new GuiComment());
        }
    }

    @SubscribeEvent
    public void networkHook(FMLNetworkEvent.ClientConnectedToServerEvent event)
    {
        comments.clear();
    }

    @SubscribeEvent
    public void networkHook(FMLNetworkEvent.ClientCustomPacketEvent event)
    {
        DataInputStream dis = null;
        try
        {
            dis = new DataInputStream(new ByteBufInputStream(event.getPacket().payload()));
            int mode = dis.readInt();
            int lifespan = dis.readInt();
            String text = dis.readUTF();
            if (!MCUtils.stripControlCodes(text).isEmpty())
            {
                Comment comment = new Comment(mode, text, lifespan, ticks);
                comment.onAdd();
                comments.add(comment);
            }
        }
        catch (IOException e)
        {
            BilicraftCommentsClient.logger.warn("error handling incoming comment: " + e.toString());
        }
        finally
        {
            if (dis != null)
                try
                {
                    dis.close();
                }
                catch (IOException ignored)
                {
                }
        }
    }

    @SubscribeEvent
    public void renderHook(RenderGameOverlayEvent.Post event)
    {
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL)
        {
            if (!comments.isEmpty())
            {
                Comment.prepare();
                for (Comment comment : comments)
                {
                    if (comment.isDead(ticks))
                    {
                        comment.onRemove();
                        comments.remove(comment);
                        continue;
                    }
                    comment.update(ticks, event.getPartialTicks());
                    comment.draw();
                }
            }
        }
    }

    public void sendRequest(int mode, int lifespan, String s)
    {
        try
        {
            ByteBufOutputStream bbos = new ByteBufOutputStream(Unpooled.buffer());
            DataOutputStream dos = new DataOutputStream(bbos);
            dos.writeInt(mode);
            dos.writeInt(lifespan);
            dos.writeUTF(s);
            dos.close();
            channel.sendToServer(new FMLProxyPacket(new PacketBuffer(bbos.buffer()), TARGET));
        }
        catch (Exception e)
        {
            BilicraftCommentsClient.logger.fatal("error sending comment request: " + e.toString());
        }
    }

    @SubscribeEvent
    public void tickHook(TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END)
            ticks = ticks + 1;
    }

}
