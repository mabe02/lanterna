package com.googlecode.lanterna.issue;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public class Issue150 {
    public static void main(String... args) throws IOException {
        Terminal term = new DefaultTerminalFactory().createTerminal();
        Screen screen = new TerminalScreen(term);
        WindowManager windowManager = new DefaultWindowManager();
        Component background = new EmptySpace(TextColor.ANSI.DEFAULT);
        final WindowBasedTextGUI gui = new MultiWindowTextGUI(screen, windowManager, background);
        screen.startScreen();
        gui.addWindowAndWait(new BasicWindow("Issue150") {{
            setComponent(createUi());
        }});
        screen.stopScreen();
    }

    private static Component createUi() {
        ActionListBox actions = new ActionListBox();
        actions.addItem("Enter terminal in a strange state", new Runnable() {
            @Override public void run() {
                stub();
            }
        });
        return actions;
    }

    private static <T> T stub() {
        throw new UnsupportedOperationException("What a terrible failure!");
    }
}
