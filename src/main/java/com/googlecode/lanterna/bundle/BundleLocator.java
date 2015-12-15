package com.googlecode.lanterna.bundle;

import java.security.PrivilegedAction;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * This class permits to deal easily with bundles.
 * @author silveryocha
 */
public abstract class BundleLocator {

    private final String bundleName;
    private static final ClassLoader loader = BundleLocator.class.getClassLoader();

    /**
     * Hidden constructor.
     * @param bundleName the name of the bundle.
     */
    protected BundleLocator(final String bundleName) {
        this.bundleName = bundleName;
    }

    /**
     * Method that centralizes the way to get the value associated to a bundle key.
     * @param locale the locale.
     * @param key the key searched for.
     * @param parameters the parameters to apply to the value associated to the key.
     * @return the formatted value associated to the given key. Empty string if no value exists for
     * the given key.
     */
    protected String getBundleKeyValue(Locale locale, String key, Object... parameters) {
        String value = null;
        try {
            value = getBundle(locale).getString(key);
        } catch (Exception ignore) {
        }
        return value != null ? MessageFormat.format(value, parameters) : null;
    }

    /**
     * Gets the right bundle.<br/>
     * A cache is handled as well as the concurrent accesses.
     * @param locale the locale.
     * @return the instance of the bundle.
     */
    private ResourceBundle getBundle(Locale locale) {
        return ResourceBundle.getBundle(bundleName, locale, loader);
    }
}
