package com.googlecode.lanterna.issue;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.List;

/**
 * Created by martin on 28/02/16.
 */
public class Issue212 {
    public static void main(String[] args) throws IOException {
        final Table<String> table = new Table<String>("Column 1", "Column 2",
                "Column 3");
        table.getTableModel().addRow("1", "2", "3");
        table.getTableModel().addRow("1", "2", "3");
        table.getTableModel().addRow("1", "2", "3");
        table.getTableModel().addRow("1", "2", "3");
        table.getTableModel().addRow("1", "2", "3");
        table.setSelectAction(new Runnable() {
            @Override
            public void run() {
                List<String> data = table.getTableModel().getRow(
                        table.getSelectedRow());
                for (int i = 0; i < data.size(); i++) {
                    System.out.println(data.get(i));
                }
            }
        });

        Window win = new BasicWindow();
        win.setComponent(table);

        DefaultTerminalFactory factory = new DefaultTerminalFactory();
        Terminal terminal = factory.createTerminal();

        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();

        // Create gui and start gui
        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen,
                new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));
        gui.addWindowAndWait(win);

        screen.stopScreen();
    }
}
