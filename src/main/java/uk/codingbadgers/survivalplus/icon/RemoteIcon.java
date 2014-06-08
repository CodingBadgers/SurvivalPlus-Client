package uk.codingbadgers.survivalplus.icon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureUtil;

import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import uk.codingbadgers.survivalplus.ModConstants;
import uk.codingbadgers.survivalplus.data.IconData;
import uk.codingbadgers.survivalplus.utils.CacheUtils;
import uk.codingbadgers.survivalplus.utils.ChecksumGenerator;
import uk.codingbadgers.survivalplus.utils.ExceptionUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static org.lwjgl.opengl.GL11.*;

@SideOnly(Side.CLIENT)
public class RemoteIcon extends AbstractIcon {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker ICON_REMOTE = MarkerManager.getMarker("ICON_REMOTE", ICON);
    private static final Marker ICON_REMOTE_ERROR = MarkerManager.getMarker("ICON_REMOTE_ERROR", ICON_REMOTE);

    private static final ResourceLocation ERROR_ICON = new ResourceLocation(ModConstants.MOD_ID, "textures/icons/error.png");
    private static AtomicInteger threadDownloadCount = new AtomicInteger(0);

    private final URL url;
    private final String hash;

    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private State state = State.LOADING;
    private int tick;
    private BufferedImage image;

    public RemoteIcon(IconData icon) {
        this.url = icon.url;
        this.hash = icon.hash;
    }

    public RemoteIcon(URL url, String hash) {
        this.url = url;
        this.hash = hash;
    }

    @Override
    public void deleteGlTexture() {
        state = State.LOADING;
        super.deleteGlTexture();
    }

    @Override
    public void loadTexture(Minecraft mc) {
        if (state == State.LOADING) {
            final File cached = CacheUtils.buildCacheFile(hash);
            if (cached.exists()) {
                try {
                    LOGGER.info(ICON_REMOTE, "Found cached file {} for {}", hash, url.toExternalForm());
                    setImage(ImageIO.read(cached));
                } catch (IOException ex) {
                    ExceptionUtils.logException(LOGGER, ICON_REMOTE_ERROR, "Error loading icon from cache " + hash, ex);
                    setState(State.ERRORED);
                }
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LOGGER.info(ICON_REMOTE, "Could not find cached file for {}", url.toExternalForm());
                            LOGGER.info(ICON_REMOTE, "Downloading...");
                            URLConnection connection = url.openConnection();
                            InputStream input = connection.getInputStream();

                            if (!cached.getParentFile().exists() && !cached.getParentFile().mkdirs()) {
                                throw new IOException("Could not created cache directory for asset " + hash);
                            }

                            setImage(ImageIO.read(input));
                            ImageIO.write(image, "png", cached);

                            String local = ChecksumGenerator.createSha1(new FileInputStream(cached));
                            if (!hash.equalsIgnoreCase(local)) {
                                LOGGER.error(ICON_REMOTE_ERROR, "Hash of local file didn't match (local: {}; remote: {})", local, hash);

                                if (!CacheUtils.delete(cached)) {
                                    throw new IOException("Could not delete invalid cache file.");
                                }

                                setState(State.ERRORED);
                                return;
                            }

                            input.close();
                        } catch (IOException e) {
                            ExceptionUtils.logException(LOGGER, ICON_REMOTE_ERROR, "Error loading icon from " + url.toExternalForm(), e);
                            setState(State.ERRORED);
                        } catch (NoSuchAlgorithmException e) {
                            ExceptionUtils.logException(LOGGER, ICON_REMOTE_ERROR, "Error loading icon from " + url.toExternalForm(), e);
                            setState(State.ERRORED);
                        }
                    }
                }, "Icon-Download-" + threadDownloadCount.incrementAndGet()).start();
            }
        }
    }

    public void draw(Minecraft mc, int x, int y, int z, int w, int h) {
        readWriteLock.readLock().lock();
        this.checkTextureUploaded();

        if (state == State.LOADING) { // Draw animated loading animation - should never happen, but this is too awesome
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
        } else if (state == State.ERRORED) {
            mc.renderEngine.bindTexture(ERROR_ICON);
        } else {
            glBindTexture(GL_TEXTURE_2D, getGlTextureId());
        }

        readWriteLock.readLock().unlock();

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double) (x), (double) (y + h), (double) z, 0, 1);
        tessellator.addVertexWithUV((double) (x + w), (double) (y + h), (double) z, 1, 1);
        tessellator.addVertexWithUV((double) (x + w), (double) (y), (double) z, 1, 0);
        tessellator.addVertexWithUV((double) (x), (double) (y), (double) z, 0, 0);
        tessellator.draw();
    }

    private void checkTextureUploaded() {
        readWriteLock.readLock().lock();

        if (state == State.LOADING && image != null) {
            if (this.hasTextureId()) {
                this.deleteGlTexture();
            }

            TextureUtil.uploadTextureImageAllocate(getGlTextureId(), image, false, false);
            state = State.LOADED;
            LOGGER.info(ICON_REMOTE, "Loaded texture {} ({})", getGlTextureId(), url.toExternalForm());
        }

        readWriteLock.readLock().unlock();
    }

    public void setImage(BufferedImage image) {
        readWriteLock.writeLock().lock();
        this.image = image;
        readWriteLock.writeLock().unlock();
    }

    public void setState(State state) {
        readWriteLock.writeLock().lock();
        this.state = state;
        readWriteLock.writeLock().unlock();
    }

    private enum State {
        LOADING,
        LOADED,
        ERRORED;
    }
}
