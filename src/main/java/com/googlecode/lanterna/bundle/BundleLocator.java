/*
 * This file is part of lanterna (http://code.google.com/p/lanterna/).
 *
 * lanterna is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2010-2019 Martin Berglund
 */
package com.googlecode.lanterna.bundle;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.PropertyResourceBundle;
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
        return ResourceBundle.getBundle(bundleName, locale, loader, new UTF8Control());
    }

    // Taken from:
    // http://stackoverflow.com/questions/4659929/how-to-use-utf-8-in-resource-properties-with-resourcebundle
    // I politely refuse to use ISO-8859-1 in these *multi-lingual* property files
    // All credits to poster BalusC (http://stackoverflow.com/users/157882/balusc)
    private static class UTF8Control extends ResourceBundle.Control {
        public ResourceBundle newBundle
                (String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
                throws IllegalAccessException, InstantiationException, IOException
        {
            // The below is a copy of the default implementation.
            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName(bundleName, "properties");
            ResourceBundle bundle = null;
            InputStream stream = null;
            if (reload) {
                URL url = loader.getResource(resourceName);
                if (url != null) {
                    URLConnection connection = url.openConnection();
                    if (connection != null) {
                        connection.setUseCaches(false);
                        stream = connection.getInputStream();
                    }
                }
            } else {
                stream = loader.getResourceAsStream(resourceName);
            }
            if (stream != null) {
                try {
                    // Only this line is changed to make it to read properties files as UTF-8.
                    bundle = new PropertyResourceBundle(new InputStreamReader(stream, "UTF-8"));
                } finally {
                    stream.close();
                }
            }
            return bundle;
        }
    }
}
