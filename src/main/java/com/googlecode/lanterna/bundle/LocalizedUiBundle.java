package com.googlecode.lanterna.bundle;

import java.util.Locale;

/**
 * This class permits to get easily localized strings about the UI.
 * @author silveryocha
 */
public class LocalizedUiBundle extends BundleLocator {

    private static final LocalizedUiBundle me = new LocalizedUiBundle("multilang.lanterna-ui");

    public static String get(String key, String... parameters) {
        return get(Locale.getDefault(), key, parameters);
    }

    public static String get(Locale locale, String key, String... parameters) {
        return me.getBundleKeyValue(locale, key, parameters);
    }

    private LocalizedUiBundle(final String bundleName) {
        super(bundleName);
    }
}
