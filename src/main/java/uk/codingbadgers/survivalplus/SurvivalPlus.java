package uk.codingbadgers.survivalplus;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import uk.codingbadgers.survivalplus.network.NetworkHandler;
import uk.codingbadgers.survivalplus.network.PacketPipeline;
import uk.codingbadgers.survivalplus.network.packets.*;
import uk.codingbadgers.survivalplus.proxy.Proxy;


@Mod(modid = ModConstants.MOD_ID,
        name = ModConstants.MOD_NAME,
        version = ModConstants.MOD_ID)
public class SurvivalPlus {

    @Instance(ModConstants.MOD_ID)
    public static SurvivalPlus INSTANCE;

    @SidedProxy(clientSide = ModConstants.PROXY_CLIENT,
                serverSide = ModConstants.PROXY_SEVER)
    public static Proxy PROXY;

    public NetworkHandler networkHandler;
    public boolean enabled = false; // TODO reset on joining server

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        networkHandler = new PacketPipeline();

        PROXY.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        networkHandler.init();
        networkHandler.registerPacket(HandshakePacket.class);
        networkHandler.registerPacket(ListTabsPacket.class);
        networkHandler.registerPacket(TabDataPacket.class);
        networkHandler.registerPacket(SkillsPacket.class);

        PROXY.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        networkHandler.lock();

        PROXY.postInit(event);
    }
}
