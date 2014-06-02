package uk.codingbadgers.survivalplus.icon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import uk.codingbadgers.survivalplus.SurvivalPlus;
import uk.codingbadgers.survivalplus.data.IconData;
import uk.codingbadgers.survivalplus.utils.CacheUtils;
import uk.codingbadgers.survivalplus.utils.ChecksumGenerator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.NoSuchAlgorithmException;

import static org.lwjgl.opengl.GL11.*;

@SideOnly(Side.CLIENT)
public class RemoteIcon extends AbstractIcon {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker ICON_REMOTE = MarkerManager.getMarker("ICON_REMOTE", ICON);
    private static final Marker ICON_REMOTE_ERROR = MarkerManager.getMarker("ICON_REMOTE_ERROR", ICON_REMOTE);

    private final URL url;
    private final String hash;

    private boolean loaded = false;
    private BufferedImage image;
    private int tick;

    public RemoteIcon(IconData icon) {
        this.url = icon.url;
        this.hash = icon.hash;
    }
    public RemoteIcon(URL url, String hash) {
        this.url = url;
        this.hash = hash;
    }

    @Override
    public void loadTexture(Minecraft mc) {
        try {
            File cached = CacheUtils.buildCacheFile(hash);

            if (!cached.exists()) {
                LOGGER.info(ICON_REMOTE, "Could not find cached file for {}", url.toExternalForm());
                LOGGER.info(ICON_REMOTE, "Downloading...");
                URLConnection connection = url.openConnection();
                InputStream input = connection.getInputStream();

                if (!cached.getParentFile().exists() && !cached.getParentFile().mkdirs()) {
                    throw new IOException("Could not created cache directory for asset " + hash);
                }

                image = ImageIO.read(input);
                ImageIO.write(image, "png", cached);

                String local = ChecksumGenerator.createSha1(new FileInputStream(cached));
                if (!hash.equalsIgnoreCase(local)) {
                    LOGGER.error(ICON_REMOTE_ERROR, "Hash of local file didn't match (local: {}; remote: {})", local, hash);

                    if (!CacheUtils.delete(cached)) {
                        throw new IOException("Could not delete invalid cache file.");
                    }
                    return;
                }

                input.close();
            } else {
                LOGGER.info(ICON_REMOTE, "Found cached file {} for {}", hash, url.toExternalForm());
                FileInputStream in = new FileInputStream(cached);
                image = ImageIO.read(in);
                in.close();
            }

            TextureUtil.uploadTextureImageAllocate(getGlTextureId(), image, false, false);
            loaded = true;
            LOGGER.info(ICON_REMOTE, "Loaded texture {} ({})", getGlTextureId(), url.toExternalForm());
        } catch (IOException e) {
            LOGGER.warn(ICON_REMOTE_ERROR, "Error loading icon from {}", url.toExternalForm());
            LOGGER.warn(ICON_REMOTE_ERROR, "Exception: {}", e.getMessage());
            LOGGER.warn(ICON_REMOTE_ERROR, "StackTrace:", e);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.warn(ICON_REMOTE_ERROR, "Error loading icon from {}", url.toExternalForm());
            LOGGER.warn(ICON_REMOTE_ERROR, "Exception: {}", e.getMessage());
            LOGGER.warn(ICON_REMOTE_ERROR, "StackTrace:", e);
        }
    }

    public void draw(Minecraft mc, int x, int y, int z, int w, int h) {
        if (!loaded) { // Draw animated loading animation - should never happen, but this is too awesome
            String string = "ooo";
            if (tick > 0 && tick <= 20) {
                string = "Ooo";
            } else if (tick > 20 && tick <= 40) {
                string = "oOo";
            } else if (tick > 40 && tick <= 60) {
                string = "ooO";
            } else if (tick > 60 && tick <= 80) {
                string = "oOo";
            } else {
                tick = 0;
            }

            tick++;
            mc.fontRenderer.drawString(string, x + 2, y + 10, -1);
            return;
        }

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
