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
