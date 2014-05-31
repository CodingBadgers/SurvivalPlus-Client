package uk.codingbadgers.survivalplus.network.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import net.minecraft.entity.player.EntityPlayer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import uk.codingbadgers.survivalplus.data.TabContentsData;
import uk.codingbadgers.survivalplus.gui.SkillsGui;
import uk.codingbadgers.survivalplus.network.Packet;
import uk.codingbadgers.survivalplus.network.exceptions.NetworkException;

public class TabDataPacket extends Packet {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker PACKET_TAB_DATA = MarkerManager.getMarker("NETWORK_PACKET_TAB_DATA", MARKER_PACKET);

    private TabContentsData data;

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) throws NetworkException {
        writeString(GSON.toJson(data), buffer);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) throws NetworkException {
        data = GSON.fromJson(readString(buffer), TabContentsData.class);
    }

    @Override
    public void handlePacket(EntityPlayer player) throws NetworkException {
        SkillsGui.setContents(data);
        LOGGER.info(PACKET_TAB_DATA, "Loaded data for tab {}", data.tab);
    }

    public static TabDataPacket build(String id) {
        TabContentsData data = new TabContentsData();
        data.tab = id;

        TabDataPacket packet = new TabDataPacket();
        packet.data = data;
        return packet;
    }
}
