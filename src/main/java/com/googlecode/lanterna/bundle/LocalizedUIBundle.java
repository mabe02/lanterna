package com.googlecode.lanterna.bundle;

import java.util.Locale;

/**
 * This class permits to get easily localized strings about the UI.
 * @author silveryocha
 */
public class LocalizedUIBundle extends BundleLocator {

    private static final LocalizedUIBundle MY_BUNDLE = new LocalizedUIBundle("multilang.lanterna-ui");

    public static String get(String key, String... parameters) {
        return get(Locale.getDefault(), key, parameters);
    }

    public static String get(Locale locale, String key, String... parameters) {
        return MY_BUNDLE.getBundleKeyValue(locale, key, (Object[])parameters);
    }

    private LocalizedUIBundle(final String bundleName) {
        super(bundleName);
    }
}
