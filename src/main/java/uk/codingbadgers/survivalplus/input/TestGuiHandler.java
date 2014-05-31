package uk.codingbadgers.survivalplus.input;

import net.minecraft.client.Minecraft;
import uk.codingbadgers.survivalplus.gui.SkillsGui;

public class TestGuiHandler implements BindingHandler {
    @Override
    public void onKeyPress() {
        Minecraft.getMinecraft().displayGuiScreen(new SkillsGui());
    }
}
