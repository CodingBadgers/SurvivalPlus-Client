package uk.codingbadgers.survivalplus.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import uk.codingbadgers.survivalplus.ModConstants;

import static org.lwjgl.opengl.GL11.glColor4f;

public class GuiBackButton extends GuiButton {

    public GuiBackButton(int par1, int par2, int par3) {
        super(par1, par2, par3, "");

        this.width = 20;
        this.height = 20;
    }

    @Override
    public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
        if (!this.visible) {
            return;
        }

        // field_146123_n represents if the mouse is over the check box region
        this.field_146123_n = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
        // get the hover state of the mouse and check box
        final int hoverState = getHoverState(field_146123_n);

        // work out the local offset into the image atlas
        final int checkboxImageSize = 20;
        final int u = 0;
        final int v = hoverState == 2 ? checkboxImageSize * 2 : checkboxImageSize;

        minecraft.renderEngine.bindTexture(new ResourceLocation(ModConstants.MOD_ID, "textures/gui/buttons.png"));
        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexturedModalRect(xPosition, yPosition, u, v, checkboxImageSize, checkboxImageSize);
    }
}
