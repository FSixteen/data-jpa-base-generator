package io.github.fsixteen.base.modules.dictionaries.utils;

import org.slf4j.LoggerFactory;

/**
 * Information about the Dictionaries module version.
 * 
 * @author FSixteen
 * @since 1.0.1
 */
public final class Version {

    private static final String VERSION = initVersion();

    private static String initVersion() {
        final String title = Version.class.getPackage().getImplementationTitle();
        final String version = Version.class.getPackage().getImplementationVersion();
        return null != version ? String.format("%s version %s", title, version) : "[WORKING]";
    }

    private Version() {
    }

    /**
     * Access to the Dictionaries module version.
     *
     * @return The Dictionaries module version
     */
    public static String getVersionString() {
        return VERSION;
    }

    /**
     * Logs the Dictionaries module version (using {@link #getVersionString()})
     * to the logging system.
     */
    public static void logVersion() {
        LoggerFactory.getLogger(Version.class).info(getVersionString());
    }

}
