package com.googlecode.lanterna.issue;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.menu.Menu;
import com.googlecode.lanterna.gui2.menu.MenuBar;
import com.googlecode.lanterna.gui2.menu.MenuItem;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public class Issue446  {

    public static void main(String[] args) throws IOException {
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Terminal terminal = terminalFactory.createTerminal();
        TerminalScreen screen = new TerminalScreen(terminal);
        screen.startScreen();
        WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);
        textGUI.addWindowAndWait(buildWindow());
    }

    public static BasicWindow buildWindow() {
        MenuBar menuBar = new MenuBar();
        menuBar.add(new Menu("Menu 1").add(new MenuItem("MenuItem 1.1")));

        BasicWindow basicWindow = new BasicWindow();

        TextBox textBox = new TextBox("A");

        Panel mainPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        mainPanel.addComponent(textBox);
        mainPanel.addComponent(new Button("Quit", basicWindow::close));

        basicWindow.setComponent(mainPanel);
        basicWindow.setMenuBar(menuBar);
        return basicWindow;
    }
}
