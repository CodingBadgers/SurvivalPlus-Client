package uk.codingbadgers.survivalplus.utils;

import com.google.common.base.Preconditions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CacheUtils {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker CACHE = MarkerManager.getMarker("CACHE");

    private static final long CACHE_EXPIRE_TIME = TimeUnit.DAYS.toMillis(7);
    private static File cacheBase = null;

    private CacheUtils() {}

    public static void setBase(File base) {
        Preconditions.checkState(cacheBase == null, "Cannot redeclare cache base");
        Preconditions.checkNotNull(base);
        cacheBase = base;
    }

    public static void cleanCache() {
        cleanDirectory(cacheBase);
    }

    public static void cleanDirectory(File file) {

        File[] files = file.listFiles();

        if (files == null || files.length == 0) {
            LOGGER.info(CACHE, "Found empty directory {}, deleting...", file.getName());

            if (!delete(file)) {
                LOGGER.error(CACHE, "Could not delete directory {}.", file.toString());
            }

            return;
        }

        for (File current : files) {
            if (current.isDirectory()) {
                cleanDirectory(current);
                continue;
            }

            try {
                BasicFileAttributes attr = Files.readAttributes(current.toPath(), BasicFileAttributes.class);

                if (hasExpired(attr.creationTime())) {
                    LOGGER.info(CACHE, "Found expired current {} deleting...", current.getName());

                    if (!delete(current)) {
                        LOGGER.warn(CACHE, "Could not delete cached file {}. Scheduling deletion on exit.", current.getName());
                        current.deleteOnExit();
                    }
                }
            } catch (IOException e) {
                LOGGER.error(CACHE, "A error has occurred cleaning the survival plus cache");
                LOGGER.error(CACHE, "Error: {}", e.getMessage());
                LOGGER.error(CACHE, "Stacktrace: ", e);
                e.printStackTrace();
            }
        }
    }

    private static boolean hasExpired(FileTime fileTime) {
        return new Date(fileTime.toMillis() + CACHE_EXPIRE_TIME).before(new Date());
    }

    public static boolean delete(File file) {
        do {

            if (!file.delete()) {
                return false;
            }

            file = file.getParentFile();
        } while(!file.equals(cacheBase));

        return true;
    }

    public static File buildCacheFile(String hash) {
        return new File(cacheBase, hash.substring(0, 1) + File.separatorChar + hash.substring(1, 2) + File.separatorChar + hash);
    }

}
