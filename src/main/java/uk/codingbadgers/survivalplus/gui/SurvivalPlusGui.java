package uk.codingbadgers.survivalplus.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public abstract class SurvivalPlusGui extends GuiScreen {

    @SuppressWarnings("unchecked")
    public void addButton(GuiButton button) {
        this.buttonList.add(button);
    }

    @Override
    public final void drawScreen(int mouseX, int mouseY, float par3) {
        Minecraft.getMinecraft().mcProfiler.startSection("survival-plus");
        drawScreen(mouseX, mouseY);
        super.drawScreen(mouseX, mouseY, par3);
        Minecraft.getMinecraft().mcProfiler.endSection();
    }

    protected abstract void drawScreen(int mouseX, int mouseY);

}
