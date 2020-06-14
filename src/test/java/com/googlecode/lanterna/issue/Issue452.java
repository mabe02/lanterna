/*
 * This file is part of lanterna (https://github.com/mabe02/lanterna).
 *
 * lanterna is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2010-2020 Martin Berglund
 */
package com.googlecode.lanterna.issue;

import java.io.IOException;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.ActionListBox;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Button.Listener;
import com.googlecode.lanterna.gui2.CheckBox;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Interactable;
import com.googlecode.lanterna.gui2.LayoutData;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.RadioBoxList;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.TextBox.Style;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.menu.Menu;
import com.googlecode.lanterna.gui2.menu.MenuBar;
import com.googlecode.lanterna.gui2.menu.MenuItem;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.MouseCaptureMode;

/**
 * <p>
 * Serves as showcase for all {@link Interactable} components for manual testing
 * during development of mouse support. Uses Telnet port 23000 as you need
 * something different than swing terminal provided by IDE. After launching main
 * method you can connect to it via terminal "telnet localhost 23000" (or
 * something of that nature)
 * <p>
 * Automatic tests can be found in {@link Issue452Test}.
 */
public class Issue452 {

    private static final int GRID_WIDTH = 100;
    private static final LayoutData LAYOUT_NEW_ROW = GridLayout.createHorizontallyFilledLayoutData(GRID_WIDTH);
    private static int buttonTriggeredCounter = 0;
    private static TextBox actionListTextBox;
    private static TextBox menuTextBox;
    private static TextBox tableTextBox;
    private static int tableTriggeredCounter = 0;

    public static void main(String[] args) throws IOException {
        try (Screen screen = new DefaultTerminalFactory().setTelnetPort(23000)
                .setMouseCaptureMode(MouseCaptureMode.CLICK_RELEASE_DRAG_MOVE).setInitialTerminalSize(new TerminalSize(100, 100))
                .createScreen()) {
            screen.startScreen();
            WindowBasedTextGUI gui = new MultiWindowTextGUI(screen);
            Window window = new BasicWindow("Issue452");
            Panel content = new Panel(new GridLayout(GRID_WIDTH));
            GridLayout gridLayout = (GridLayout) content.getLayoutManager();
            gridLayout.setVerticalSpacing(1);
            addInteractableComponentsToContent(content);
            addMenuBar(window);
            window.setComponent(content);
            gui.addWindowAndWait(window);
        }
    }

    private static void addInteractableComponentsToContent(Panel content) {
        // for menu bar so you know which menu you have triggered
        menuTextBox = new TextBox("Try menu above");
        content.addComponent(menuTextBox, LAYOUT_NEW_ROW);

        // single line textbox
        content.addComponent(new TextBox("Single line TextBox"), LAYOUT_NEW_ROW);

        // multi line textbox
        content.addComponent(new TextBox(
                "First line of multi line TextBox" + System.lineSeparator() + "Second line of multi line TextBox",
                Style.MULTI_LINE), LAYOUT_NEW_ROW);

        // checkbox
        content.addComponent(new CheckBox("CheckBox"), LAYOUT_NEW_ROW);

        // button
        TextBox textBoxButton = new TextBox("Click the button!");
        Button button = new Button("Button");
        button.addListener(new Listener() {
            @Override
            public void onTriggered(Button button) {
                textBoxButton.setText("Button triggered " + Issue452.buttonTriggeredCounter++ + " times");
            }
        });
        content.addComponent(button, GridLayout.createHorizontallyFilledLayoutData(1));
        content.addComponent(textBoxButton, GridLayout.createHorizontallyFilledLayoutData(GRID_WIDTH - 1));

        // action list box
        actionListTextBox = new TextBox("Click on something in the action list!");
        ActionListBox actionMenu = new ActionListBox();
        actionMenu.addItem("First menu", new Runnable() {
            @Override
            public void run() {
                actionListTextBox.setText("First menu clicked");
            }
        });
        actionMenu.addItem("Second menu", new Runnable() {
            @Override
            public void run() {
                actionListTextBox.setText("Second menu clicked");
            }
        });
        actionMenu.addItem("Third menu", new Runnable() {
            @Override
            public void run() {
                actionListTextBox.setText("Third menu clicked");
            }
        });
        content.addComponent(actionListTextBox, LAYOUT_NEW_ROW);
        content.addComponent(actionMenu, LAYOUT_NEW_ROW);

        // radiobox list
        RadioBoxList<String> list = new RadioBoxList<>();
        list.addItem("RadioGaga");
        list.addItem("RadioGogo");
        list.addItem("RadioBlaBla");
        content.addComponent(list, LAYOUT_NEW_ROW);

        // Table
        tableTextBox = new TextBox("Try table bellow");
        Table<String> table = new Table<>("Column0000000", "Column111", "Column22222");
        table.getTableModel().addRow("0", "0", "0");
        table.getTableModel().addRow("1", "1", "1");
        table.getTableModel().addRow("2", "2", "2");
        table.setSelectAction(() -> {
            tableTriggeredCounter++;
            tableTextBox.setText("Table's action runned " + tableTriggeredCounter + " times");
        });
        table.setCellSelection(true);
        content.addComponent(tableTextBox, LAYOUT_NEW_ROW);
        content.addComponent(table, LAYOUT_NEW_ROW);
    }

    private static void addMenuBar(Window window) {
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("Settings");
        menu.add(new MenuItem("Menu1", new Runnable() {
            @Override
            public void run() {
                menuTextBox.setText("Menu1 clicked");
                menuTextBox.invalidate();
            }
        }));
        menu.add(new MenuItem("Menu2", new Runnable() {

            @Override
            public void run() {
                menuTextBox.setText("Menu2 clicked");
                menuTextBox.invalidate();
            }
        }));
        menu.add(new MenuItem("Menu3", new Runnable() {

            @Override
            public void run() {
                menuTextBox.setText("Menu3 clicked");
                menuTextBox.invalidate();
            }
        }));
        menuBar.add(menu);
        window.setMenuBar(menuBar);
    }

}