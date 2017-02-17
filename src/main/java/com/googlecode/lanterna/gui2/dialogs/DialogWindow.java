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

import com.googlecode.lanterna.gui2.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Thin layer on top of the {@code AbstractWindow} class that automatically sets properties and hints to the window to
 * make it act more like a modal dialog window
 */
public abstract class DialogWindow extends AbstractWindow {

    private static final Set<Hint> GLOBAL_DIALOG_HINTS =
            Collections.unmodifiableSet(new HashSet<Hint>(Collections.singletonList(Hint.MODAL)));

    /**
     * Default constructor, takes a title for the dialog and runs code shared for dialogs
     * @param title Title of the window
     */
    protected DialogWindow(String title) {
        super(title);
        setHints(GLOBAL_DIALOG_HINTS);
    }

    /**
     * Opens the dialog by showing it on the GUI and doesn't return until the dialog has been closed
     * @param textGUI Text GUI to add the dialog to
     * @return Depending on the {@code DialogWindow} implementation, by default {@code null}
     */
    public Object showDialog(WindowBasedTextGUI textGUI) {
        textGUI.addWindow(this);

        //Wait for the window to close, in case the window manager doesn't honor the MODAL hint
        waitUntilClosed();
        return null;
    }
}
