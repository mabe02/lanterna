package com.googlecode.lanterna.gui2.textbox;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

/**
 * Extracted from TextBox (Phase 3 refactor — God Class).
 * Encapsulates system-clipboard read/write.
 */
public class TextBoxClipboard {

    public String read() {
        try {
            Transferable contents = Toolkit.getDefaultToolkit()
                    .getSystemClipboard()
                    .getContents(null);
            if (contents == null || !contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return "";
            }
            return (String) contents.getTransferData(DataFlavor.stringFlavor);
        } catch (Exception e) {
            return "";
        }
    }

    public boolean write(String text) {
        if (text == null) {
            return false;
        }
        try {
            Toolkit.getDefaultToolkit()
                    .getSystemClipboard()
                    .setContents(new StringSelection(text), null);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}