package uk.codingbadgers.survivalplus.network.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import net.minecraft.entity.player.EntityPlayer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import uk.codingbadgers.survivalplus.ModConstants;
import uk.codingbadgers.survivalplus.SurvivalPlus;
import uk.codingbadgers.survivalplus.gui.SkillsGui;
import uk.codingbadgers.survivalplus.network.Packet;

public class HandshakePacket extends Packet {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker PACKET_HANDSHAKE = MarkerManager.getMarker("NETWORK_PACKET_HANDSHAKE", MARKER_PACKET);

    private HandshakeStatus status;
    private int protocolVersion;
    private String version;

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        buffer.writeByte(status.ordinal());
        buffer.writeShort(protocolVersion);
        writeString(version, buffer);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        status = HandshakeStatus.getById(buffer.readByte());
        protocolVersion = buffer.readShort();
        version = readString(buffer);
    }

    @Override
    public void handlePacket(EntityPlayer player) {
        LOGGER.info(PACKET_HANDSHAKE, "Received handshake packet");
        HandshakeStatus responseStatus = HandshakeStatus.OK;

        if (this.status != HandshakeStatus.REQUESTING) {
            responseStatus = HandshakeStatus.INVALID;
        } else if (this.protocolVersion > ModConstants.NETWORK_PROTOCOL_VERSION) {
            responseStatus = HandshakeStatus.OUT_OF_DATE_SERVER;
        } else if (this.protocolVersion < ModConstants.NETWORK_PROTOCOL_VERSION) {
            responseStatus = HandshakeStatus.OUT_OF_DATE_CLIENT;
        }

        if (responseStatus == HandshakeStatus.OK) {
            SurvivalPlus.INSTANCE.enabled = true;
        }

        SkillsGui.clearTabs();
        LOGGER.info(PACKET_HANDSHAKE, "Handshake response: {}", responseStatus.name());
        SurvivalPlus.INSTANCE.networkHandler.sendPacket(HandshakePacket.build(responseStatus));
    }

    public static HandshakePacket build(HandshakeStatus status) {
        HandshakePacket packet = new HandshakePacket();
        packet.status = status;
        packet.protocolVersion = ModConstants.NETWORK_PROTOCOL_VERSION;
        packet.version = ModConstants.MOD_VERSION;
        return packet;
    }

    public static enum HandshakeStatus {
        REQUESTING,
        OK,
        INVALID,
        OUT_OF_DATE_SERVER,
        OUT_OF_DATE_CLIENT;

        public static HandshakeStatus getById(int id) {
            return values()[id];
        }
    }
}
