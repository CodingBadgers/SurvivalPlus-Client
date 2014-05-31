package uk.codingbadgers.survivalplus.icon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

@SideOnly(Side.CLIENT)
public interface Icon {

    public void draw(Minecraft mc, int x, int y, int z, int w, int h);

    public void loadTexture(Minecraft mc);

    public int getGlTextureId();
}
