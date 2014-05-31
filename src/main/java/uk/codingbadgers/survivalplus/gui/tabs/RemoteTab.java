package uk.codingbadgers.survivalplus.gui.tabs;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import uk.codingbadgers.survivalplus.data.TabContentsData;
import uk.codingbadgers.survivalplus.data.TabsData.*;

public class RemoteTab extends SkillsTab {

    private Tab data;
    private TabContentsData contents;

    public RemoteTab(Tab tab) {
        this.data = tab;
    }

    @Override
    public ResourceLocation getIcon() {
        return null;
    }

    @Override
    public ResourceLocation getBackground() {
        return null;
    }

    @Override
    public void drawTabContent(Minecraft mc, int x, int y, float scroll) {
        if (this.contents == null) {
            return;
        }

        int xPos = (int) (x + (226 / 2f));
        int yPos = (int) (y + (mc.fontRenderer.FONT_HEIGHT / 2f));
        drawCenteredString(mc.fontRenderer, contents.title, xPos, yPos, -1);
        xPos = x + 8;
        yPos += 12;

        for (String line : contents.content) {
            mc.fontRenderer.drawString(line, xPos, yPos, -1);
            yPos += 8;
        }

    }

    @Override
    public String getName() {
        return data.name;
    }

    @Override
    public String getId() {
        return data.id;
    }

    @Override
    public boolean isScrollEnabled() {
        return false;
    }

    public void setData(TabContentsData data) {
        this.contents = data;
    }
}
