package uk.codingbadgers.survivalplus.gui.tabs;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import uk.codingbadgers.survivalplus.ModConstants;
import uk.codingbadgers.survivalplus.data.SkillsData;
import uk.codingbadgers.survivalplus.data.SkillsData.Skill;
import uk.codingbadgers.survivalplus.icon.Icon;
import uk.codingbadgers.survivalplus.icon.SimpleIcon;

public class ProgressTab extends SkillsTab {

    public static final float SCALE = 0.00390625F;
    private static SkillsData data = new SkillsData();

    public static void setData(SkillsData data) {
        ProgressTab.data = data;
    }

    public static SkillsData getData() {
        return data;
    }

    @Override
    public Icon getIcon() {
        return new SimpleIcon(new ResourceLocation(ModConstants.MOD_ID, "textures/icons/skills.png"));
    }

    @Override
    public ResourceLocation getBackground() {
        return new ResourceLocation(ModConstants.MOD_ID, "textures/gui/tabs/tab-progress.png");
    }

    @Override
    public void drawTabContent(Minecraft mc, int xPos, int yPos, float scroll) {
        if (data == null) {
            return;
        }

        int i = 0;

        if (isScrollEnabled()) {
            int rows = (int) Math.ceil(data.skills.length / 3f);
            i = (int)((double)(scroll * rows));
        }

        if (i < 0) {
            i = 0;
        }

        int j = i * 3;

        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 3; x++) {
                if (j >= data.skills.length) {
                    break;
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
        return data != null && data.skills.length > 12;
    }

}
