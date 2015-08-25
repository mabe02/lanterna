package com.googlecode.lanterna.gui2.dialogs;

import com.googlecode.lanterna.gui2.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Thin layer on top of the BasicWindow class that automatically sets properties and hints to the window to make it
 * act more like a modal dialog window
 */
public abstract class DialogWindow extends BasicWindow {

    private static final Set<Hint> GLOBAL_DIALOG_HINTS =
            Collections.unmodifiableSet(new HashSet<Hint>(Collections.singletonList(Hint.MODAL)));

    protected DialogWindow(String title) {
        super(title);
        setHints(GLOBAL_DIALOG_HINTS);
    }

    public Object showDialog(WindowBasedTextGUI textGUI) {
        textGUI.addWindow(this);

        //Wait for the window to close, in case the window manager doesn't honor the MODAL hint
        waitUntilClosed();
        return null;
    }
}
