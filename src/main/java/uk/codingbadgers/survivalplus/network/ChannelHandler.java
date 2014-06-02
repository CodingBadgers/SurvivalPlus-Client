package uk.codingbadgers.survivalplus.network;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import uk.codingbadgers.survivalplus.network.exceptions.NetworkException;

import java.util.*;

/**
 * Based of iChun's Channel Handler
 * <p />
 * <a href="https://github.com/iChun/iChunUtil/blob/master/src/main/java/ichun/common/core/network/ChannelHandler.java">View here</a>
 *
 * @author iChun
 */
@Sharable
public class ChannelHandler extends FMLIndexedMessageToMessageCodec<Packet> {

    private static final Logger logger = LogManager.getLogger();
    public static final Marker NETWORK = MarkerManager.getMarker("NETWORK");

    private EnumMap<Side, FMLEmbeddedChannel> channels;
    private LinkedList<Class<? extends Packet>> packets = Lists.newLinkedList();
    private boolean isPostInitialised = false;
    public final String channel;

    private ChannelHandler(String s, Class<? extends Packet>... packetTypes) {
        channel = s;

        Arrays.sort(packetTypes, new Comparator<Class<? extends Packet>>() {
            @Override
            public int compare(Class<? extends Packet> clazz1, Class<? extends Packet> clazz2) {
                int com = String.CASE_INSENSITIVE_ORDER.compare(clazz1.getSimpleName(), clazz2.getSimpleName());
                if (com == 0) {
                    com = clazz1.getSimpleName().compareTo(clazz2.getSimpleName());
                }

                return com;
            }
        });

        ArrayList<Class<? extends Packet>> list = new ArrayList<Class<? extends Packet>>();
        for(int i = 0; i < packetTypes.length; i++)
        {
            if(!list.contains(packetTypes[i])) {
                list.add(packetTypes[i]);
            } else {
                logger.warn(NETWORK, "Channel " + channel + " is reregistering packet types!", true);
            }
            addDiscriminator(i, packetTypes[i]);
        }
    }

    public static FMLEmbeddedChannel getChannelHandlers(String modId, Class<? extends Packet>... packetTypes) {
        EnumMap<Side, FMLEmbeddedChannel> handlers = NetworkRegistry.INSTANCE.newChannel(modId, new ChannelHandler(modId, packetTypes));

        PacketExecutor executor = new PacketExecutor();

        for(Map.Entry<Side, FMLEmbeddedChannel> e : handlers.entrySet()) {
            FMLEmbeddedChannel channel = e.getValue();
            String codec = channel.findChannelHandlerNameForType(ChannelHandler.class);
            channel.pipeline().addAfter(codec, "PacketExecutor", executor);
        }

        return handlers.get(Side.CLIENT);
    }

    @Override
    public void encodeInto(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf byteBuf) throws Exception {
        try {
            packet.encodeInto(channelHandlerContext, byteBuf);
        } catch (NetworkException ex) {
            logger.error(NETWORK, "Error encoding packet {}", packet.getClass().getSimpleName());
            logger.error(NETWORK, "Exception: {} ({})", ex.getMessage(), ex.getClass().getSimpleName());
            logger.error(NETWORK, "Exception:", ex);
        }
    }

    @Override
    public void decodeInto(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, Packet packet) {
        try {
            packet.decodeInto(channelHandlerContext, byteBuf);
        } catch (NetworkException ex) {
            logger.error(NETWORK, "Error decoding packet {}", packet.getClass().getSimpleName());
            logger.error(NETWORK, "Exception: {} ({})", ex.getMessage(), ex.getClass().getSimpleName());
            logger.error(NETWORK, "Exception:", ex);
        }
    }

    @Sharable
    private static class PacketExecutor extends SimpleChannelInboundHandler<Packet>
    {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Packet msg) throws Exception {
            Side side = ctx.channel().attr(NetworkRegistry.CHANNEL_SOURCE).get();
            EntityPlayer player;
            if(side.isServer())
            {
                INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
                player = ((NetHandlerPlayServer) netHandler).playerEntity;
            }
            else
            {
                player = this.getClientPlayer();
            }

            msg.handlePacket(player);
        }

        @SideOnly(Side.CLIENT)
        private EntityPlayer getClientPlayer() {
            return Minecraft.getMinecraft().thePlayer;
        }
    }


}
