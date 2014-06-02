package uk.codingbadgers.survivalplus.proxy;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import uk.codingbadgers.survivalplus.input.KeyBindingHandler;
import uk.codingbadgers.survivalplus.input.TestGuiHandler;

@SideOnly(Side.CLIENT)
public class ClientProxy implements Proxy {

    @Override
    public void init(FMLInitializationEvent event) {
        KeyBindingHandler handler = new KeyBindingHandler();
        handler.registerNewBinding(new KeyBinding("Test Gui", Keyboard.KEY_P, "Survival Plus"), new TestGuiHandler());
        FMLCommonHandler.instance().bus().register(handler);
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {

    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {

    }

}
