package uk.codingbadgers.survivalplus.icon;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.Tessellator;

public class PlayerHeadIcon implements Icon {

    private static final float SCALE_X = 0.015625f;
    private static final float SCALE_Y = 0.03125f;

    @Override
    public void draw(Minecraft mc, int x, int y, int z, int w, int h) { // TODO convert to 3D head
        AbstractClientPlayer player = mc.thePlayer;
        mc.renderEngine.bindTexture(player.getLocationSkin());

        w = (int) (w * 0.75f);
        h = (int) (h * 0.75f);

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();

        tessellator.addVertexWithUV(x + 3,      y + 4 + h,  z,  8  * SCALE_X,  16 * SCALE_Y);
        tessellator.addVertexWithUV(x + 3 + w,  y + 4 + h,  z,  16 * SCALE_X,  16 * SCALE_Y);
        tessellator.addVertexWithUV(x + 3 + w,  y + 4,      z,  16 * SCALE_X,  8  * SCALE_Y);
        tessellator.addVertexWithUV(x + 3, y + 4, z, 8 * SCALE_X, 8 * SCALE_Y);

        tessellator.draw();
    }

    @Override
    public void loadTexture(Minecraft mc) {

    }

    @Override
    public int getGlTextureId() {
        return 0;
    }
}
