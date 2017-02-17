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
 * Copyright (C) 2010-2017 Martin Berglund
 */
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
