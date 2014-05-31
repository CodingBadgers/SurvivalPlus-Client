package uk.codingbadgers.survivalplus.gui.tabs;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import uk.codingbadgers.survivalplus.ModConstants;
import uk.codingbadgers.survivalplus.icon.Icon;
import uk.codingbadgers.survivalplus.icon.SimpleIcon;

public class HomeTab extends SkillsTab {

    @Override
    public Icon getIcon() {
        return new SimpleIcon(new ResourceLocation(ModConstants.MOD_ID, "textures/icons/home.png"));
    }

    @Override
    public ResourceLocation getBackground() {
        return null;
    }

    @Override
    public void drawTabContent(Minecraft mc, int x, int y, float scroll) {

        int xPos = (int) (x + (226 / 2f));
        int yPos = (int) (y + (mc.fontRenderer.FONT_HEIGHT / 2f));

        this.drawCenteredString(mc.fontRenderer, EnumChatFormatting.GOLD + "" + EnumChatFormatting.BOLD + "" + EnumChatFormatting.UNDERLINE + "Forgotten - Realms", xPos, yPos, -1);
        yPos += mc.fontRenderer.FONT_HEIGHT + 2;
        this.drawCenteredString(mc.fontRenderer, EnumChatFormatting.RED + "Discover the lands where Gods used to rule", xPos, yPos, -1);
        yPos += (mc.fontRenderer.FONT_HEIGHT + 2) * 2;

        this.drawCenteredString(mc.fontRenderer, "Welcome to forgotten realms a minecraft", xPos, yPos, -1);
        yPos += (mc.fontRenderer.FONT_HEIGHT + 2);
        this.drawCenteredString(mc.fontRenderer, "server for the new age.", xPos, yPos, -1);
        yPos += (mc.fontRenderer.FONT_HEIGHT + 2);

    }

    @Override
    public String getName() {
        return "Home";
    }

    @Override
    public boolean isScrollEnabled() {
        return false;
    }

}
