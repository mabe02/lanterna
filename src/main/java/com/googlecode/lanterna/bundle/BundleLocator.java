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
 * Copyright (C) 2010-2016 Martin
 */
package com.googlecode.lanterna.bundle;

import java.text.MessageFormat;
import java.util.Locale;
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
