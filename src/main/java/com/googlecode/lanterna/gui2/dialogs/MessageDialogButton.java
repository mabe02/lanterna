package com.googlecode.lanterna.gui2.dialogs;

import com.googlecode.lanterna.gui2.LocalizedString;

/**
 * This enum has the available selection of buttons that you can add to a {@code MessageDialog}. They are used both for
 * specifying which buttons the dialog will have but is also returned when the user makes a selection
 *
 * @author Martin
 */
public enum MessageDialogButton {
    /**
     * "OK"
     */
    OK(LocalizedString.OK),
    /**
     * "Cancel"
     */
    Cancel(LocalizedString.Cancel),
    /**
     * "Yes"
     */
    Yes(LocalizedString.Yes),
    /**
     * "No"
     */
    No(LocalizedString.No),
    /**
     * "Close"
     */
    Close(LocalizedString.Close),
    /**
     * "Abort"
     */
    Abort(LocalizedString.Abort),
    /**
     * "Ignore"
     */
    Ignore(LocalizedString.Ignore),
    /**
     * "Retry"
     */
    Retry(LocalizedString.Retry),

    /**
     * "Continue"
     */
    Continue(LocalizedString.Continue);

    private final LocalizedString label;

    MessageDialogButton(final LocalizedString label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label.toString();
    }
}
