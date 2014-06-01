package uk.codingbadgers.survivalplus.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import net.minecraft.entity.player.EntityPlayer;

import org.apache.commons.io.Charsets;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import uk.codingbadgers.survivalplus.ModConstants;
import uk.codingbadgers.survivalplus.network.exceptions.NetworkException;

/**
 * Packet class. Should be the parent of all packets wishing to use the PacketPipeline.
 * @author sirgingalot
 */
public abstract class Packet {

    protected static final Marker MARKER_PACKET = MarkerManager.getMarker("NETWORK_PACKET", ChannelHandler.NETWORK);

    protected static final Gson GSON = new GsonBuilder()
            .setVersion(ModConstants.NETWORK_PROTOCOL_VERSION)
            .create();

    /**
     * Encode the packet data into the ByteBuf stream. Complex data sets may need specific data handlers (See @link{cpw.mods.fml.common.network.ByteBuffUtils})
     *
     * @param ctx    channel context
     * @param buffer the buffer to encode into
     */
    public abstract void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) throws NetworkException;

    /**
     * Decode the packet data from the ByteBuf stream. Complex data sets may need specific data handlers (See @link{cpw.mods.fml.common.network.ByteBuffUtils})
     *
     * @param ctx    channel context
     * @param buffer the buffer to decode from
     */
    public abstract void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) throws NetworkException;

    /**
     * Handle a packete. Note this occurs after decoding has completed.
     *
     * @param player the player reference
     */
    public abstract void handlePacket(EntityPlayer player) throws NetworkException;

    /**
     * Write a string to the byte buffer.
     * <p />
     * String format is:
     * <table>
     *     <tr>
     *         <td>length</td>
     *         <td>short</td>
     *         <td>2 bytes</td>
     *     </tr>
     *     <tr>
     *         <td>content (UTF-8 encoded)</td>
     *         <td>byte[]</td>
     *         <td>length bytes</td>
     *     </tr>
     * </table>
     *
     * @param string the string to write
     * @param buffer the buffer to write to
     */
    public void writeString(String string, ByteBuf buffer) { // TODO convert to use ByteBufUtils
        byte[] bytes = string.getBytes(Charsets.UTF_8);
        buffer.writeShort(bytes.length);
        buffer.writeBytes(bytes);
    }

    /**
     * Read a string from the byte buffer
     *
     * @param buffer the buffer to read from
     * @return the read string
     */
    public String readString(ByteBuf buffer) {
        short length = buffer.readShort();
        byte[] bytes = new byte[length];
        buffer.readBytes(bytes);
        return new String(bytes, Charsets.UTF_8);
    }
}
