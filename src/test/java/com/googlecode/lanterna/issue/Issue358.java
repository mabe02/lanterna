package com.googlecode.lanterna.issue;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public class Issue358 {
    public static void main(String[] args) throws IOException {
        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();
        MultiWindowTextGUI textGUI = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));

        String title = "Issue358";

        int nbColumns = 5;
        final Window window = new BasicWindow(title);

        final GridLayout layoutManager = new GridLayout(nbColumns);
        layoutManager.setVerticalSpacing(0);
        layoutManager.setHorizontalSpacing(1);
        final Panel contentPanel = new Panel(layoutManager);
        contentPanel.addComponent(
                new EmptySpace(TextColor.ANSI.CYAN).setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER, false, false,3, 1)));
        window.setComponent(contentPanel);
        textGUI.addWindowAndWait(window);
    }
}
