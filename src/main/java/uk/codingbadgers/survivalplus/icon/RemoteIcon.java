package uk.codingbadgers.survivalplus.icon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static org.lwjgl.opengl.GL11.*;

@SideOnly(Side.CLIENT)
public class RemoteIcon extends AbstractIcon {

    private final URL url;

    private boolean loaded = false;
    private BufferedImage image;

    public RemoteIcon(URL url) {
        this.url = url;
    }

    @Override
    public void loadTexture(Minecraft mc) {
        try {
            URLConnection connection = url.openConnection();
            InputStream input = connection.getInputStream();

            image = ImageIO.read(input);
            TextureUtil.uploadTextureImageAllocate(getGlTextureId(), image, false, false);
            loaded = true;
            System.out.println("Loaded texture " + getGlTextureId());
        } catch (IOException e) {
             e.printStackTrace();
        }
    }

    public void draw(Minecraft mc, int x, int y, int z, int w, int h) {
        /*if (!loaded) {
            mc.fontRenderer.drawString("...", x + (w / 2), y + (h / 2), -1);
            return;
        }*/

        glBindTexture(GL_TEXTURE_2D, getGlTextureId());

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(x),     (double)(y + h),   (double)z,  0,  1);
        tessellator.addVertexWithUV((double)(x + w), (double)(y + h),   (double)z,  1,  1);
        tessellator.addVertexWithUV((double)(x + w), (double)(y),       (double) z, 1,  0);
        tessellator.addVertexWithUV((double)(x),     (double)(y),       (double)z,  0,  0);
        tessellator.draw();
    }

}
