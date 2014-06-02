package uk.codingbadgers.survivalplus.gui.tabs;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import uk.codingbadgers.survivalplus.ModConstants;
import uk.codingbadgers.survivalplus.SurvivalPlus;
import uk.codingbadgers.survivalplus.data.SkillsData;
import uk.codingbadgers.survivalplus.data.SkillsData.Skill;
import uk.codingbadgers.survivalplus.gui.SkillsGui;
import uk.codingbadgers.survivalplus.gui.elements.GuiBackButton;
import uk.codingbadgers.survivalplus.icon.Icon;
import uk.codingbadgers.survivalplus.icon.SimpleIcon;
import uk.codingbadgers.survivalplus.network.packets.TabDataPacket;

public class ProgressTab extends SkillsTab {

    public static final float SCALE = 0.00390625F;
    private static final Icon icon = new SimpleIcon(new ResourceLocation(ModConstants.MOD_ID, "textures/icons/skills.png"));
    private static final ResourceLocation background = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/tabs/tab-progress.png");

    private static SkillsData data = new SkillsData();

    private SkillsTab subTab;
    private GuiButton backButton;

    public static void setData(SkillsData data) {
        ProgressTab.data = data;
    }

    public static SkillsData getData() {
        return data;
    }

    @Override
    public Icon getIcon() {
        return subTab == null ? icon : subTab.getIcon();
    }

    @Override
    public ResourceLocation getBackground() {
        return subTab == null ? background : subTab.getBackground();
    }

    @Override
    public void drawTabContent(Minecraft mc, int xPos, int yPos, int mouseX, int mouseY, float scroll) {
        if (data == null) {
            return;
        }

        if (subTab != null) {
            if (backButton == null) {
                backButton = new GuiBackButton(0, xPos, yPos + 126);
            }

            subTab.drawTabContent(mc, xPos, yPos, mouseX, mouseY, scroll);
            backButton.drawButton(mc, mouseX, mouseY);
            return;
        }

        int i = 0;
        int hover = getHovering(mouseX, mouseY);

        if (isScrollEnabled()) {
            int rows = (int) Math.ceil(data.skills.length / 3f);
            i = (int)((double)(scroll * rows));
        }

        if (i < 0) {
            i = 0;
        }

        int j = i * 3;

        for (int y = 0; y < 4; y++) {
            if (y <- i) {
                break;
            }

            for (int x = 0; x < 3; x++) {
                if (j >= data.skills.length) {
                    break;
                }

                if (hover == j) { // Draw hovering highlight
                    drawRect(xPos + (x * 77), yPos + (y * 38) - 1, xPos + ((x + 1) * 77) - 5, yPos + ((y + 1) * 38) - 6, 0x7D87BEFF);
                }

                Skill skill = data.skills[j];
                mc.fontRenderer.drawStringWithShadow(skill.name, xPos + (x * 77) + 4, yPos + (y * 37) + 4, -1);
                drawCenteredString(mc.fontRenderer, (skill.level < 10 ? "0" : "") + skill.level + "/" + skill.maxLevel, xPos + (x * 77) + 36, yPos + (y * 37) + 14, -1);
                mc.renderEngine.bindTexture(getBackground());
                drawProgressBar(xPos + (x * 77) + 2, yPos + (y * 37) + 24, skill.progress);
                j++;
            }
        }
    }

    private int getHovering(int mouseX, int mouseY) {
        int x = (int) ((mouseX - 8) / 78f);
        int y = (int) ((mouseY - 9) / 37f);
        return x - 1 + ((y - 1) * 3);
    }

    private void drawProgressBar(int x, int y, float progress) {
        int width = 68;
        int height = 5;

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();

        tessellator.addVertexWithUV((double)(x), (double)(y + height), (double)this.zLevel, 0 * SCALE, 175 * SCALE);
        tessellator.addVertexWithUV((double)(x + width), (double)(y + height), (double)this.zLevel, 101 * SCALE, 175 * SCALE);
        tessellator.addVertexWithUV((double)(x + width), (double)(y), (double)this.zLevel, 101 * SCALE, 170 * SCALE);
        tessellator.addVertexWithUV((double)(x), (double) (y), (double) this.zLevel, 0 * SCALE, 170 * SCALE);

        tessellator.addVertexWithUV((double)(x), (double)(y + height), (double)this.zLevel, 0 * SCALE, 180 * SCALE);
        tessellator.addVertexWithUV((double)(x + (width * progress)), (double)(y + height), (double)this.zLevel, (progress * 101) * SCALE, 180 * SCALE);
        tessellator.addVertexWithUV((double)(x + (width * progress)), (double)(y), (double)this.zLevel, (progress * 101) * SCALE, 175 * SCALE);
        tessellator.addVertexWithUV((double)(x), (double)(y), (double)this.zLevel, 0 * SCALE, 175 * SCALE);

        tessellator.draw();
    }

    @Override
    public String getName() {
        return "Skills";
    }

    @Override
    public boolean isScrollEnabled() {
        return data != null && data.skills != null && data.skills.length > 12;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (button == 0) {
            if (subTab == null) {
                int i = getHovering(mouseX, mouseY);

                if (i < data.skills.length && data.skills[i].tab != null) {
                    SurvivalPlus.INSTANCE.sendPacket(TabDataPacket.build(data.skills[i].tab));
                    subTab = SkillsGui.getTab(data.skills[i].tab);
                }
            } else {
                if (backButton.mousePressed(null /* Should be the mc instance, but it isn't needed*/, mouseX, mouseY)) {
                    subTab = null;
                }
            }
        }
    }
}
