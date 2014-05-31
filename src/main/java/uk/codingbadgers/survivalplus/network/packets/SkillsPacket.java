package uk.codingbadgers.survivalplus.network.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import uk.codingbadgers.survivalplus.data.SkillsData;
import uk.codingbadgers.survivalplus.gui.tabs.ProgressTab;
import uk.codingbadgers.survivalplus.network.Packet;
import uk.codingbadgers.survivalplus.network.exceptions.NetworkException;

public class SkillsPacket extends Packet {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker PACKET_SKILLS = MarkerManager.getMarker("NETWORK_PACKET_SKILLS", MARKER_PACKET);

    private SkillsData data;

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) throws NetworkException {
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) throws NetworkException {
        data = GSON.fromJson(readString(buffer), SkillsData.class);
    }

    @Override
    public void handlePacket(EntityPlayer player) throws NetworkException {
        ProgressTab.setData(this.data);
    }

}
