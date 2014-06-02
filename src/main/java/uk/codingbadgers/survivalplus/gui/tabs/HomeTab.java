package uk.codingbadgers.survivalplus.gui.tabs;

import com.google.common.base.Splitter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import uk.codingbadgers.survivalplus.ModConstants;
import uk.codingbadgers.survivalplus.icon.Icon;
import uk.codingbadgers.survivalplus.icon.PlayerHeadIcon;
import uk.codingbadgers.survivalplus.icon.SimpleIcon;

public class HomeTab extends SkillsTab {

    private static final Splitter splitter = Splitter.on("\n");
    private Icon icon = new PlayerHeadIcon();

    @Override
    public Icon getIcon() {
        return icon;
    }

    @Override
    public ResourceLocation getBackground() {
        return null;
    }

    @Override
    public void drawTabContent(Minecraft mc, int x, int y, int mouseX, int mouseY, float scroll) {
        int xPos = (int) (x + (226 / 2f));
        int yPos = (int) (y + (mc.fontRenderer.FONT_HEIGHT / 2f));
        int space = mc.fontRenderer.FONT_HEIGHT + 2;

        this.drawCenteredString(mc.fontRenderer, EnumChatFormatting.GOLD + "" + EnumChatFormatting.BOLD + "" + EnumChatFormatting.UNDERLINE + "Forgotten - Realms", xPos, yPos, -1);
        yPos += space + (space / 2);
        this.drawCenteredString(mc.fontRenderer, EnumChatFormatting.RED + "Discover the lands where Gods used to rule", xPos, yPos, -1);
        yPos += space * 2;

        this.drawCenteredString(mc.fontRenderer, "Put some text here mabey...", xPos, yPos, -1);
        yPos += space;
        this.drawCenteredString(mc.fontRenderer, "or not, doesn't really matter.", xPos, yPos, -1);
        yPos += space * 2;

        for (String line : splitter.split(ModConstants.MOD_VERSION_TYPE.getMessage())) {
            this.drawCenteredString(mc.fontRenderer, line, xPos, yPos, -1);
            yPos += space;
        }
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
