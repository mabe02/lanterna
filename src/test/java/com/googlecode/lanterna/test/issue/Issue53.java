package com.googlecode.lanterna.test.issue;

import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.dialog.TextInputDialog;
import com.googlecode.lanterna.test.TestTerminalFactory;

public class Issue53 {
    
    public static void main(String[] args) {
        final GUIScreen guiScreen = new TestTerminalFactory(args).createGUIScreen();
        guiScreen.getScreen().startScreen();
        TextInputDialog.showTextInputBox(guiScreen, "Testing issue 53", "This is a test for issue 53", "");
        guiScreen.getScreen().stopScreen();
    }
}
