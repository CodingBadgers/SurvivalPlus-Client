package uk.codingbadgers.survivalplus.utils;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;

public class ExceptionUtils {

    public static void logException(Logger logger, Marker marker, String message, Exception ex) {
        logger.error(marker, message);
        logger.error(marker, "Exception: {}", ex.getMessage());
        logger.error(marker, "StackTrace:", ex);
    }

    public static void logException(Logger logger,  String message, Exception ex) {
        logger.error(message);
        logger.error("Exception: {}", ex.getMessage());
        logger.error("StackTrace:", ex);
    }
}
