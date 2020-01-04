package com.googlecode.lanterna.issue;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.IOException;

public class Issue387 {

    public static void main(String[] args) {
        try {
            Screen screen = new DefaultTerminalFactory().createScreen();
            screen.startScreen();

            Window window = new BasicWindow();

            Table<String> table = new Table<>("Column");
            table.setVisibleRows(3);

            table.getTableModel().addRow("row 1");
            table.getTableModel().addRow("row 2");
            table.getTableModel().addRow("row 3");
            table.getTableModel().addRow("row 4");
            table.getTableModel().addRow("row 5");
            table.getTableModel().addRow("row 6");
            table.getTableModel().addRow("row 7");

            Panel panel = new Panel();
            panel.addComponent(new TextBox());
            panel.addComponent(new EmptySpace(new TerminalSize(15, 1)));
            panel.addComponent(table);
            panel.addComponent(new EmptySpace(new TerminalSize(15, 1)));
            panel.addComponent(new TextBox());

            window.setComponent(panel);

            MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);
            gui.addWindowAndWait(window);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
