/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.lanterna.test.gui;

import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.DefaultBackgroundRenderer;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.component.ComboBox;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.text.UnixTerminal;
import com.googlecode.lanterna.test.TestTerminalFactory;
import java.nio.charset.Charset;

/**
 *
 * @author smolyakov
 */
public class ComboBoxTest {

    public static void main(String[] args) {
        Terminal terminal = new TestTerminalFactory(args).createTerminal();
        if (terminal instanceof UnixTerminal) {
            terminal = new UnixTerminal(System.in, System.out, Charset.forName("UTF-8"),
                    null, UnixTerminal.Behaviour.CTRL_C_KILLS_APPLICATION);
        }
        final GUIScreen guiScreen = new GUIScreen(new Screen(terminal));
        guiScreen.getScreen().startScreen();
        guiScreen.setBackgroundRenderer(new DefaultBackgroundRenderer("GUI Test"));

        final Window mainWindow = new Window("Window with panels");

        final Label label = new Label("Selected:");
        final ComboBox comboBox = new ComboBox(guiScreen, "Empty", "test title");
        comboBox.selectedChanged(new Action() {

            @Override
            public void doAction() {
                label.setText("Selected: " + comboBox.getSelectedItem());
            }
        });
        Panel mainPanel = new Panel(Panel.Orientation.VERTICAL);

        Panel buttonPanel = new Panel(Panel.Orientation.HORISONTAL);
        Button btnClose = new Button("Close", new Action() {
            @Override
            public void doAction() {
                mainWindow.close();
            }
        });

        Button btnAddItems = new Button("Add Items", new Action() {

            int lastIndex = 0;

            @Override
            public void doAction() {
                for (int i = 0; i < 10; i++) {
                    lastIndex++;
                    comboBox.addItem("Item " + lastIndex);
                }
            }
        });
        Button btnClear = new Button("Clear", new Action() {

            @Override
            public void doAction() {
                comboBox.removeAllItems();
            }
        });
        
        Button btnSelectIndex = new Button("Select index -1", new Action() {

            @Override
            public void doAction() {
                comboBox.setSelectedIndex(-1);
            }
        });
        
         Button btnSelectItem = new Button("Select item null", new Action() {

            @Override
            public void doAction() {
               comboBox.setSelectedItem(null);
            }
        });
        
       // buttonPanel.addComponent(btnClose);
        buttonPanel.addComponent(btnAddItems);
        buttonPanel.addComponent(btnClear);
        buttonPanel.addComponent(btnSelectIndex);
        buttonPanel.addComponent(btnSelectItem);

        mainPanel.addComponent(buttonPanel);
        mainPanel.addComponent(comboBox);
        mainPanel.addComponent(label);

        mainWindow.addComponent(mainPanel);

        guiScreen.showWindow(mainWindow, GUIScreen.Position.CENTER);
        guiScreen.getScreen().stopScreen();
    }
}
