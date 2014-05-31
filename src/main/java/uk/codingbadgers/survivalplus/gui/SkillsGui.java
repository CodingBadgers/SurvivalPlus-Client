package uk.codingbadgers.survivalplus.gui;

import com.google.common.collect.Lists;

import com.google.common.collect.Maps;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.Rectangle;

import uk.codingbadgers.survivalplus.ModConstants;
import uk.codingbadgers.survivalplus.SurvivalPlus;
import uk.codingbadgers.survivalplus.data.TabContentsData;
import uk.codingbadgers.survivalplus.data.TabsData.Tab;
import uk.codingbadgers.survivalplus.gui.tabs.HomeTab;
import uk.codingbadgers.survivalplus.gui.tabs.ProgressTab;
import uk.codingbadgers.survivalplus.gui.tabs.RemoteTab;
import uk.codingbadgers.survivalplus.gui.tabs.SkillsTab;
import uk.codingbadgers.survivalplus.network.packets.SkillsPacket;
import uk.codingbadgers.survivalplus.network.packets.TabDataPacket;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.glColor4f;

public class SkillsGui extends SurvivalPlusGui {

    private static final ResourceLocation guiTexture = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/skills.png");

    private static List<SkillsTab> tabs = Lists.newLinkedList();
    private int selectedTab = 0;

    private static final int WIDTH = 256;
    private static final int HEIGHT = 164;

    private static final int SCROLL_LENGTH = 132;
    private static final float SCROLL_AMOUNT = 0.1f;

    private boolean scrollingEnabled = true;
    private float scrollPos = 0f;

    static {
        addDefaultTabs();
    }

    public static void clearTabs() {
        tabs.clear();
        addDefaultTabs();
    }

    private static void addDefaultTabs() {
        tabs.add(new HomeTab());
        tabs.add(new ProgressTab());
    }

    public static void registerCustomTab(Tab tab) {
        tabs.add(new RemoteTab(tab));
    }

    public static void setContents(TabContentsData data) {
        for (SkillsTab tab : tabs) {
            if (tab instanceof RemoteTab && tab.getId().equalsIgnoreCase(data.tab)) {
                ((RemoteTab) tab).setData(data);
            }
        }
    }

    @Override
    public void initGui() {
        this.scrollingEnabled = tabs.get(selectedTab).isScrollEnabled();
        this.scrollPos = 0;

        SurvivalPlus.INSTANCE.networkHandler.sendPacket(new SkillsPacket());
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        int startX = (this.width / 2) - (WIDTH / 2);
        int startY = (this.height / 2) - (HEIGHT / 2);
        super.mouseClicked(mouseX, mouseY, button);

        for (int i = 0; i < tabs.size(); i++) {
            if (isMouseInside(mouseX, mouseY, startX + (i * 28), startY - 28, 28, 28)) {
                setSelectedTab(i);
                break;
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        this.drawDefaultBackground();
        drawContainer(mouseX, mouseY);
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();

        if (this.scrollingEnabled) {
            int dWheel = Mouse.getDWheel();
            if (dWheel < 0) { // SCROLL DOWN
                if (scrollPos < 1f) {
                    scrollPos += SCROLL_AMOUNT;
                }
            } else if (dWheel > 0) { // SCROLL UP
                if (scrollPos > 0f + SCROLL_AMOUNT) {
                    scrollPos -= SCROLL_AMOUNT;
                }
            }
        }
    }

    private void drawContainer(int mouseX, int mouseY) {
        mc.mcProfiler.startSection("skills");
        int startX = (this.width / 2) - (WIDTH / 2);
        int startY = (this.height / 2) - (HEIGHT / 2);

        mc.mcProfiler.startSection("tabs");

        int x = startX;
        int y = startY - 28;

        for (int i = 0; i < tabs.size(); i++) {
            if (i != selectedTab) {
                renderTab(i, x, y);
            }
            x += 28;
        }

        mc.mcProfiler.endSection();
        SkillsTab tab = tabs.get(selectedTab);

        ResourceLocation background = guiTexture;

        if (tab.getBackground() != null) {
            background = tab.getBackground();
        }

        mc.getTextureManager().bindTexture(background);
        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        this.drawTexturedModalRect(startX, startY, 0, 0, WIDTH, HEIGHT);
        renderTab(selectedTab, startX + ((selectedTab) * 28), y);
        mc.getTextureManager().bindTexture(guiTexture);
        this.drawTexturedModalRect(startX + 239, (int) (startY + 9 + Math.floor(scrollPos * SCROLL_LENGTH)), scrollingEnabled ? 0 : 12, 164, 12, 15);
        tabs.get(selectedTab).drawTabContent(mc, startX + 8, startY + 10, scrollPos);

        for (int i = 0; i < tabs.size(); i++) {
            if (isMouseInside(mouseX, mouseY, startX + (i * 28), startY - 28, 28, 28)) {
                this.drawHoveringText(Arrays.asList(tabs.get(i).getName()), mouseX, mouseY, mc.fontRenderer);
                break;
            }
        }

        mc.mcProfiler.endSection();
    }

    private boolean isMouseInside(int mouseX, int mouseY, int x, int y, int w, int h) {
        Rectangle bb = new Rectangle(x, y, w, h);
        return bb.contains(mouseX, mouseY);
    }

    private void renderTab(int id, int x, int y) {
        mc.mcProfiler.startSection(tabs.get(id).getName());
        tabs.get(id).renderTab(mc, x, y, selectedTab == id, id == 0 ? 0 : 1);
        mc.mcProfiler.endSection();
    }

    public void setSelectedTab(int i) {
        this.selectedTab = i;
        this.scrollingEnabled = tabs.get(i).isScrollEnabled();
        this.scrollPos = 0;

        if (tabs.get(selectedTab) instanceof RemoteTab) {
            SurvivalPlus.INSTANCE.networkHandler.sendPacket(TabDataPacket.build(tabs.get(selectedTab).getId()));
        }
    }

}
