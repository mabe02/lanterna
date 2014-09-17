package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TestTerminalFactory;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;

/**
 * Created by martin on 17/09/14.
 */
public class MultiButtonTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        Screen screen = new TestTerminalFactory(args).createScreen();
        screen.startScreen();
        WindowBasedTextGUI textGUI = new DefaultWindowTextGUI(screen);
        try {
            BasicWindow window = new BasicWindow("Button test");
            window.getContentArea().addComponent(new Button("Button"));
            window.getContentArea().addComponent(new Button("Another button"));
            window.getContentArea().addComponent(new EmptySpace(new TerminalSize(5, 1)));
            //window.getContentArea().addComponent(new Button("Here is a\nmulti-line\ntext segment that is using \\n"));

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
