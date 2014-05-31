package uk.codingbadgers.survivalplus.proxy;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public interface Proxy {

    public void init(FMLInitializationEvent event);

    public void preInit(FMLPreInitializationEvent event);

    public void postInit(FMLPostInitializationEvent event);

}
