package com.googlecode.lanterna.issue;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

public class Issue460 {
    public static void main(String[] args) throws Exception {
        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();

        final BasicWindow window1 = new BasicWindow();
        Panel contentPanel = new Panel(new GridLayout(1));
        contentPanel.addComponent(new Label("VERTICAL"), GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER,
                true,
                true,
                1,
                4
        ));
        contentPanel.addComponent(new Button("Close", new Runnable() {
            @Override
            public void run() {
                window1.close();
            }
        }), GridLayout.createHorizontallyFilledLayoutData(2));
        window1.setComponent(contentPanel);

        // Create gui and start gui
        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);
        gui.addWindowAndWait(window1);
        screen.stopScreen();
    }
}
