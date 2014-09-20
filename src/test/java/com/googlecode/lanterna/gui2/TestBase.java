package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TestTerminalFactory;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;

/**
 * Created by martin on 20/09/14.
 */
public abstract class TestBase {
    void run(String[] args) throws IOException, InterruptedException {
        Screen screen = new TestTerminalFactory(args).createScreen();
        screen.startScreen();
        WindowBasedTextGUI textGUI = new DefaultWindowTextGUI(screen);
        try {
            init(textGUI);
            TextGUIThread guiThread = textGUI.getGUIThread();
            guiThread.start();
            guiThread.waitForStop();
        }
        finally {
            screen.stopScreen();
        }
    }

    public abstract void init(WindowBasedTextGUI textGUI);
}
