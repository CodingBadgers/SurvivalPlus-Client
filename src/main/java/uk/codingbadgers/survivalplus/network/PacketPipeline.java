package uk.codingbadgers.survivalplus.network;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import uk.codingbadgers.survivalplus.ModConstants;

import java.util.*;

@ChannelHandler.Sharable
public class PacketPipeline extends MessageToMessageCodec<FMLProxyPacket, Packet> implements NetworkHandler {

    private static final Logger logger = LogManager.getLogger();
    public static final Marker NETWORK = MarkerManager.getMarker("NETWORK");

    private EnumMap<Side, FMLEmbeddedChannel> channels;
    private LinkedList<Class<? extends Packet>> packets = Lists.newLinkedList();
    private boolean isPostInitialised = false;

    /**
     * Register your packet with the pipeline. Discriminators are automatically set.
     *
     * @param clazz the class to register
     */
    public void registerPacket(Class<? extends Packet> clazz) {
        Preconditions.checkState(!this.isPostInitialised, "Cannot register new packet after mod has initialized");
        Preconditions.checkState(this.packets.size() <= 256, "Already registered 256 packets, we can't register any more");
        Preconditions.checkArgument(!this.packets.contains(clazz), "Packet already registered");

        logger.info(NETWORK, "Registering packet {}", clazz.getSimpleName());
        this.packets.add(clazz);
    }

    // In line encoding of the packet, including discriminator setting
    @Override
    protected void encode(ChannelHandlerContext ctx, Packet msg, List<Object> out) throws Exception {
        ByteBuf buffer = Unpooled.buffer();
        Class<? extends Packet> clazz = msg.getClass();
        if (!this.packets.contains(msg.getClass())) {
            throw new NullPointerException("No Packet Registered for: " + msg.getClass().getCanonicalName());
        }

        byte discriminator = (byte) this.packets.indexOf(clazz);
        logger.info(NETWORK, "Sending packet {} (id {}) to server.", clazz.getSimpleName(), discriminator);

        buffer.writeByte(discriminator);
        msg.encodeInto(ctx, buffer);
        FMLProxyPacket proxyPacket = new FMLProxyPacket(buffer.copy(), ctx.channel().attr(NetworkRegistry.FML_CHANNEL).get());
        out.add(proxyPacket);
    }

    // In line decoding and handling of the packet
    @Override
    protected void decode(ChannelHandlerContext ctx, FMLProxyPacket msg, List<Object> out) throws Exception {
        ByteBuf payload = msg.payload();
        byte discriminator = payload.readByte();
        Class<? extends Packet> clazz = this.packets.get(discriminator);
        if (clazz == null) {
            throw new NullPointerException("No packet registered for discriminator: " + discriminator);
        }

        logger.info(NETWORK, "Received packet {} (id {}) from Server.", clazz.getSimpleName(), discriminator);

        Packet pkt = clazz.newInstance();
        pkt.decodeInto(ctx, payload.slice());

        EntityPlayer player;
        switch (FMLCommonHandler.instance().getEffectiveSide()) {
            case CLIENT:
                player = this.getClientPlayer();
                pkt.handlePacket(player);
                break;

            default:
                throw new IllegalStateException("Side " + FMLCommonHandler.instance().getEffectiveSide().name() + " is not supported by SurvivalPlus Client.");
        }

        out.add(pkt);
    }

    // Method to call from FMLInitializationEvent
    public void init() {
        this.channels = NetworkRegistry.INSTANCE.newChannel(ModConstants.NETWORK_CHANNEL_ID, this);
    }

    // Method to call from FMLPostInitializationEvent
    // Ensures that packet discriminators are common between server and client by using logical sorting
    public void lock() {
        if (this.isPostInitialised) {
            return;
        }

        this.isPostInitialised = true;
        Collections.sort(this.packets, new Comparator<Class<? extends Packet>>() {

            @Override
            public int compare(Class<? extends Packet> clazz1, Class<? extends Packet> clazz2) {
                int com = String.CASE_INSENSITIVE_ORDER.compare(clazz1.getSimpleName(), clazz2.getSimpleName());
                if (com == 0) {
                    com = clazz1.getSimpleName().compareTo(clazz2.getSimpleName());
                }

                return com;
            }
        });

        int id = 0;

        for (Class<? extends Packet> clazz : this.packets) {
            logger.info(NETWORK, "Packet {} (id {}).", clazz.getSimpleName(), id++);
        }
    }

    @SideOnly(Side.CLIENT)
    private EntityPlayer getClientPlayer() {
        return Minecraft.getMinecraft().thePlayer;
    }

    /**
     * Send this message to the server.
     * <p/>
     * Adapted from CPW's code in cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper
     *
     * @param message The message to send
     */
    public void sendPacket(Packet message) {
        this.channels.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
        this.channels.get(Side.CLIENT).writeAndFlush(message);
    }
}
