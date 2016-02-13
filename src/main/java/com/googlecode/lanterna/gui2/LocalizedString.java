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
    public final static LocalizedString OK = new LocalizedString("short.label.ok");
    /**
     * "Cancel"
     */
    public final static LocalizedString Cancel = new LocalizedString("short.label.cancel");
    /**
     * "Yes"
     */
    public final static LocalizedString Yes = new LocalizedString("short.label.yes");
    /**
     * "No"
     */
    public final static LocalizedString No = new LocalizedString("short.label.no");
    /**
     * "Close"
     */
    public final static LocalizedString Close = new LocalizedString("short.label.close");
    /**
     * "Abort"
     */
    public final static LocalizedString Abort = new LocalizedString("short.label.abort");
    /**
     * "Ignore"
     */
    public final static LocalizedString Ignore = new LocalizedString("short.label.ignore");
    /**
     * "Retry"
     */
    public final static LocalizedString Retry = new LocalizedString("short.label.retry");
    /**
     * "Continue"
     */
    public final static LocalizedString Continue = new LocalizedString("short.label.continue");
    /**
     * "Open"
     */
    public final static LocalizedString Open = new LocalizedString("short.label.open");
    /**
     * "Save"
     */
    public final static LocalizedString Save = new LocalizedString("short.label.save");

    private final String bundleKey;

    private LocalizedString(final String bundleKey) {
        this.bundleKey = bundleKey;
    }

    @Override
    public String toString() {
        return LocalizedUIBundle.get(Locale.getDefault(), bundleKey);
    }
}
