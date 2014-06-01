package uk.codingbadgers.survivalplus.network.packets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import net.minecraft.entity.player.EntityPlayer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import uk.codingbadgers.survivalplus.ModConstants;
import uk.codingbadgers.survivalplus.SurvivalPlus;
import uk.codingbadgers.survivalplus.data.TabsData;
import uk.codingbadgers.survivalplus.gui.SkillsGui;
import uk.codingbadgers.survivalplus.gui.tabs.RemoteTab;
import uk.codingbadgers.survivalplus.network.Packet;

public class ListTabsPacket extends Packet {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker PACKET_LIST_TABS = MarkerManager.getMarker("NETWORK_PACKET_LIST_TABS", MARKER_PACKET);

    private TabsData data;

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        writeString(GSON.toJson(data), buffer);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        String string = readString(buffer);
        LOGGER.info("Data: {}", string);
        data = GSON.fromJson(string, TabsData.class);
    }

    @Override
    public void handlePacket(EntityPlayer player) {
        for (TabsData.Tab tab : data.tabs) {
            LOGGER.info(PACKET_LIST_TABS, "Loaded tab {} from remote.", tab.id);
            SkillsGui.registerCustomTab(tab);
            SurvivalPlus.INSTANCE.sendPacket(TabDataPacket.build(tab.id));
        }
    }
}
