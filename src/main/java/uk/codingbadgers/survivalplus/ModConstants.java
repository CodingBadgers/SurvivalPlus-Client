package uk.codingbadgers.survivalplus;

import com.google.common.base.Splitter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public final class ModConstants {

    private static final Logger logger = LogManager.getLogger();

    public static final String MOD_NAME = "Survival Plus Client";
    public static final String MOD_ID = "survival-plus";
    public static final String MOD_VERSION;
    public static final ModVersionType MOD_VERSION_TYPE;

    private static final String PROXY_PACKAGE = "uk.codingbadgers.survivalplus.proxy.";
    public static final String PROXY_CLIENT = PROXY_PACKAGE + "ClientProxy";
    public static final String PROXY_SEVER = PROXY_PACKAGE + "ServerProxy";

    public static final String NETWORK_CHANNEL_ID = "survival-plus";
    public static final short NETWORK_PROTOCOL_VERSION = 1;

    static {
        if (ModConstants.class.getPackage().getImplementationVersion() != null) {
            MOD_VERSION = ModConstants.class.getPackage().getImplementationVersion();
            MOD_VERSION_TYPE = ModVersionType.matchType(MOD_VERSION);
        } else {
            MOD_VERSION = "dev-build";
            MOD_VERSION_TYPE = ModVersionType.DEV;
        }

        logger.info("Loaded SurvivalPlus {} ({})", MOD_VERSION, MOD_VERSION_TYPE.name().toLowerCase());
    }

    public static enum ModVersionType {
        DEV     (new String[] {"SNAPSHOT"},     "This is a dev build, try not break anything\nto badly, please?"),
        ALPHA   (new String[] {"ALPHA", "a"},   "Please note this is a alpha version of the\nmod, things will be rapidly changing."),
        BETA    (new String[] {"BETA", "b"},    "Please note this is a alpha version of the\nmod, things should be stable but there are likely to be bugs."),
        RELEASE (new String[] {""},             "");

        private static final Splitter splitter = Splitter.on("-");
        private final List<String> classifiers;
        private final String message;

        ModVersionType(String[] classifiers, String message) {
            this.message = message;
            this.classifiers = Arrays.asList(classifiers);
        }

        public String getMessage() {
            return message;
        }

        public static ModVersionType matchType(String version) {
            if (!version.contains("-")) {
                return RELEASE;
            }

            Iterator<String> itr =  splitter.split(version).iterator();
            itr.next();
            String classifier = itr.next();

            for (ModVersionType value : values()) {
                if (value.classifiers.contains(classifier)) {
                    return value;
                }
            }

            return DEV;
        }

    }
}
