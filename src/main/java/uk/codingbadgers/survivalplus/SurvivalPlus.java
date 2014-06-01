package uk.codingbadgers.survivalplus;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.relauncher.Side;
import uk.codingbadgers.survivalplus.network.ChannelHandler;
import uk.codingbadgers.survivalplus.network.Packet;
import uk.codingbadgers.survivalplus.network.packets.*;
import uk.codingbadgers.survivalplus.proxy.Proxy;

import java.io.File;


@Mod(modid = ModConstants.MOD_ID,
        name = ModConstants.MOD_NAME,
        version = ModConstants.MOD_ID)
public class SurvivalPlus {

    @Instance(ModConstants.MOD_ID)
    public static SurvivalPlus INSTANCE;

    @SidedProxy(clientSide = ModConstants.PROXY_CLIENT,
                serverSide = ModConstants.PROXY_SEVER)
    public static Proxy PROXY;

    public FMLEmbeddedChannel networkHandler;
    public boolean enabled = false; // TODO reset on joining server

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        PROXY.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {

        networkHandler = ChannelHandler.getChannelHandlers(ModConstants.NETWORK_CHANNEL_ID,
                HandshakePacket.class,
                ListTabsPacket.class,
                TabDataPacket.class,
                SkillsPacket.class
        );

        PROXY.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        PROXY.postInit(event);
    }

    public void sendPacket(Packet packet) {
        networkHandler.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
        networkHandler.writeAndFlush(packet);
    }
}
