package com.googlecode.lanterna.issue;

import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.dialog.TextInputDialog;
import com.googlecode.lanterna.TestTerminalFactory;
import java.io.IOException;

public class Issue53 {
    public static void main(String[] args) throws IOException {
        final GUIScreen guiScreen = new TestTerminalFactory(args).createGUIScreen();
        guiScreen.getScreen().startScreen();
        //Ensuring that a TextInputBox doesn't throw an exception if the description is
        //shorter than the title
        TextInputDialog.showTextInputBox(guiScreen, "Testing issue 53", "Issue 53", "");
        guiScreen.getScreen().stopScreen();
    }
}
