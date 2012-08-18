/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.lanterna.test.gui;

import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.component.CheckBox;
import com.googlecode.lanterna.gui.component.EmptySpace;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.component.ProgressBar;
import com.googlecode.lanterna.gui.component.Table;
import com.googlecode.lanterna.gui.component.TextBox;
import com.googlecode.lanterna.gui.layout.LinearLayout;
import com.googlecode.lanterna.test.TestTerminalFactory;
import java.util.Random;

/**
 *
 * @author Martin
 */
public class TableTest {
    
    public static void main(String[] args)
    {
        final GUIScreen guiScreen = new TestTerminalFactory(args).createGUIScreen();
        guiScreen.getScreen().startScreen();
        final Window window1 = new Window("Text box window");
        
        final Table table = new Table(5, "My Test Table");
        table.addRow(new Label("Column 1 "),
                        new Label("Column 2 "),
                        new Label("Column 3 "),
                        new Label("Column 4 "),
                        new Label("Column 5"));
        table.addRow(new TextBox("Here's a text box"),
                        new CheckBox("checkbox", false),
                        new ProgressBar(10),
                        new EmptySpace(),
                        new Button("Progress", new Action() {
            @Override
            public void doAction() {
                ((ProgressBar)table.getRow(1)[2]).setProgress(new Random().nextDouble());
            }
        }));
        
        
        window1.addComponent(table);
        Panel buttonPanel = new Panel(Panel.Orientation.HORISONTAL);
        buttonPanel.addComponent(new EmptySpace(1, 1), LinearLayout.MAXIMIZES_HORIZONTALLY);
        Button exitButton = new Button("Exit", new Action() {            
            @Override
            public void doAction() {
                guiScreen.closeWindow();
            }
        });
        buttonPanel.addComponent(exitButton);
        window1.addComponent(buttonPanel);
        guiScreen.showWindow(window1, GUIScreen.Position.CENTER);
        guiScreen.getScreen().stopScreen();
    }
}
