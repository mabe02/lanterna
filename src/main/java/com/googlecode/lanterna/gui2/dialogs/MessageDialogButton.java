package com.googlecode.lanterna.gui2.dialogs;

import com.googlecode.lanterna.gui2.LocalizedString;

/**
 * Created by martin on 21/06/15.
 */
public enum MessageDialogButton {
    OK(LocalizedString.OK),
    Cancel(LocalizedString.Cancel),
    Yes(LocalizedString.Yes),
    No(LocalizedString.No),
    Close(LocalizedString.Close),
    Abort(LocalizedString.Abort),
    Ignore(LocalizedString.Ignore),
    Retry(LocalizedString.Retry),
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
