package uk.codingbadgers.survivalplus.input;

import com.google.common.collect.Maps;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraft.client.settings.KeyBinding;

import java.util.Map;

public class KeyBindingHandler {

    private final Map<KeyBinding, BindingHandler> bindings = Maps.newHashMap();

    public void registerNewBinding(KeyBinding binding, BindingHandler handler) {
        this.bindings.put(binding, handler);
        ClientRegistry.registerKeyBinding(binding);
    }

    @SubscribeEvent
    public void onKeyPress(KeyInputEvent event) {
        for (KeyBinding binding : bindings.keySet()) {
            if (binding.getIsKeyPressed()) {
                bindings.get(binding).onKeyPress();
                break;
            }
        }
    }

}
