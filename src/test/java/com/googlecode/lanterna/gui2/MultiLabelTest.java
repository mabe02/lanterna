package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TestTerminalFactory;
import com.googlecode.lanterna.TextColor;
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
            window.getContentArea().addComponent(new Label("This is a single line label"));
            window.getContentArea().addComponent(new Label("This is another label on the second line"));
            window.getContentArea().addComponent(new EmptySpace(new TerminalSize(5, 1)));
            window.getContentArea().addComponent(new Label("Here is a\nmulti-line\ntext segment that is using \\n"));
            Label label = new Label("We can change foreground color...");
            label.setForegroundColor(TextColor.ANSI.BLUE);
            window.getContentArea().addComponent(label);
            label = new Label("...and background color...");
            label.setBackgroundColor(TextColor.ANSI.MAGENTA);
            window.getContentArea().addComponent(label);
            label = new Label("...and add custom SGR styles!");
            label.addStyle(SGR.BOLD);
            label.addStyle(SGR.UNDERLINE);
            window.getContentArea().addComponent(label);
            window.getContentArea().addComponent(new EmptySpace(new TerminalSize(5, 1)));
            window.getContentArea().addComponent(new Label("Here is an animated label:"));
            window.getContentArea().addComponent(AnimatedLabel.createClassicSpinningLine());

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
