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
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.bundle.LocalizedUIBundle;

import java.util.Locale;

/**
 * Set of predefined localized string.<br>
 * All this strings are localized by using {@link LocalizedUIBundle}.<br>
 * Changing the locale by calling {@link Locale#setDefault(Locale)}.
 * @author silveryocha.
 */
public final class LocalizedString {

    /**
     * "OK"
     */
    public final static LocalizedString OK = new LocalizedString("short.label.ok", "OK");
    /**
     * "Cancel"
     */
    public final static LocalizedString Cancel = new LocalizedString("short.label.cancel", "Cancel");
    /**
     * "Yes"
     */
    public final static LocalizedString Yes = new LocalizedString("short.label.yes", "Yes");
    /**
     * "No"
     */
    public final static LocalizedString No = new LocalizedString("short.label.no", "No");
    /**
     * "Close"
     */
    public final static LocalizedString Close = new LocalizedString("short.label.close", "Close");
    /**
     * "Abort"
     */
    public final static LocalizedString Abort = new LocalizedString("short.label.abort", "Abort");
    /**
     * "Ignore"
     */
    public final static LocalizedString Ignore = new LocalizedString("short.label.ignore", "Ignore");
    /**
     * "Retry"
     */
    public final static LocalizedString Retry = new LocalizedString("short.label.retry", "Retry");
    /**
     * "Continue"
     */
    public final static LocalizedString Continue = new LocalizedString("short.label.continue", "Continue");
    /**
     * "Open"
     */
    public final static LocalizedString Open = new LocalizedString("short.label.open", "Open");
    /**
     * "Save"
     */
    public final static LocalizedString Save = new LocalizedString("short.label.save", "Save");

    private final String defaultValue;
    private final String bundleKey;

    private LocalizedString(final String bundleKey, final String defaultValue) {
        this.bundleKey = bundleKey;
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        String localizedString = LocalizedUIBundle.get(Locale.getDefault(), bundleKey);
        if (localizedString == null) {
            localizedString = defaultValue;
        }
        return localizedString;
    }
}
