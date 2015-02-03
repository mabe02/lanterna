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
        WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);
        try {
            BasicWindow window = new BasicWindow("Label test");
            Panel contentArea = new Panel();
            contentArea.setLayoutManager(new LinearLayout(Direction.VERTICAL));
            contentArea.addComponent(new Label("This is a single line label"));
            contentArea.addComponent(new Label("This is another label on the second line"));
            contentArea.addComponent(new EmptySpace(new TerminalSize(5, 1)));
            contentArea.addComponent(new Label("Here is a\nmulti-line\ntext segment that is using \\n"));
            Label label = new Label("We can change foreground color...");
            label.setForegroundColor(TextColor.ANSI.BLUE);
            contentArea.addComponent(label);
            label = new Label("...and background color...");
            label.setBackgroundColor(TextColor.ANSI.MAGENTA);
            contentArea.addComponent(label);
            label = new Label("...and add custom SGR styles!");
            label.addStyle(SGR.BOLD);
            label.addStyle(SGR.UNDERLINE);
            contentArea.addComponent(label);
            contentArea.addComponent(new EmptySpace(new TerminalSize(5, 1)));
            contentArea.addComponent(new Label("Here is an animated label:"));
            contentArea.addComponent(AnimatedLabel.createClassicSpinningLine());

            window.setComponent(contentArea);
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
