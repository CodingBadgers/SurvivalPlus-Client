package uk.codingbadgers.survivalplus.proxy;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ServerProxy implements Proxy {
    @Override
    public void init(FMLInitializationEvent event) {

    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        throw new RuntimeException("Survival Plus Client cannot be installed on the server");
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {

    }
}
