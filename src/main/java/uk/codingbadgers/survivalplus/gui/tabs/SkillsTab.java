package uk.codingbadgers.survivalplus.gui.tabs;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import static org.lwjgl.opengl.GL11.*;

import uk.codingbadgers.survivalplus.ModConstants;
import uk.codingbadgers.survivalplus.icon.Icon;

public abstract class SkillsTab extends Gui {

    private static final ResourceLocation guiTexture = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/skills.png");

    public void renderTab(Minecraft mc, int x, int y, boolean selected, int type) {
        mc.renderEngine.bindTexture(guiTexture);

        int u = type == 0 ? 0 : 28;
        int v = selected ? 224 : 194;
        int h = selected ? 32 : 30;

        drawTexturedModalRect(x, y, u, v, 28, h);

        if (getIcon() != null) {
            getIcon().draw(mc, x + 3, y + 5, (int) this.zLevel, 22, 22);
        }

        glDisable(GL_LIGHTING);
    }

    public abstract Icon getIcon();

    public abstract ResourceLocation getBackground();

    public abstract void drawTabContent(Minecraft mc, int xPos, int yPos, int mouseX, int mouseY, float scroll);

    public abstract String getName();

    public abstract boolean isScrollEnabled();

    public String getId() {
        return getClass().getSimpleName().toLowerCase();
    }

    public boolean isGlobal() {
        return true;
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {}

}
