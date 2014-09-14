package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TestTerminalFactory;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;

/**
 * Created by martin on 13/09/14.
 */
public class MultiLabelTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        Screen screen = new TestTerminalFactory(args).createScreen();
        screen.startScreen();
        WindowBasedTextGUI textGUI = new DefaultWindowTextGUI(screen);
        try {
            BasicWindow window = new BasicWindow("Label test");
            new Label(window.getContentArea(), "This is a single line label");
            new Label(window.getContentArea(), "This is another label on the second line");

            textGUI.getWindowManager().addWindow(window);
            textGUI.updateScreen();
            while(!textGUI.getWindowManager().getWindows().isEmpty()) {
                textGUI.processInput();
                if(textGUI.isPendingUpdate()) {
                    textGUI.updateScreen();
                }
                else {
                    Thread.sleep(1);
                }
            }
        }
        finally {
            screen.stopScreen();
        }
    }
}
