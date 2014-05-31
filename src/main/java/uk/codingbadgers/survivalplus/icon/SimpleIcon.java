package uk.codingbadgers.survivalplus.icon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class SimpleIcon extends AbstractIcon {

    private final ResourceLocation resource;

    public SimpleIcon(ResourceLocation loc) {
        this.resource = loc;
    }

    @Override
    public void draw(Minecraft mc, int x, int y, int z, int w, int h) {
        mc.renderEngine.bindTexture(resource);

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(x),     (double)(y + h),   (double)z,  0,  1);
        tessellator.addVertexWithUV((double)(x + w), (double)(y + h),   (double)z,  1,  1);
        tessellator.addVertexWithUV((double)(x + w), (double)(y),       (double) z, 1,  0);
        tessellator.addVertexWithUV((double)(x),     (double)(y),       (double)z,  0,  0);
        tessellator.draw();
    }

    @Override
    public void loadTexture(Minecraft mc) {}

}
