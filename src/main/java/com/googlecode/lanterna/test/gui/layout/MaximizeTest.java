/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.lanterna.test.gui.layout;

import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.TextGraphics;
import com.googlecode.lanterna.gui.Theme;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.AbstractComponent;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.component.EmptySpace;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.layout.SizePolicy;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.test.TestTerminalFactory;

/**
 *
 * @author Martin
 */
public class MaximizeTest {
    public static void main(String[] args)
    {
        final GUIScreen guiScreen = new TestTerminalFactory(args).createGUIScreen();
        guiScreen.getScreen().startScreen();
        guiScreen.setTitle("GUI Test");

        final Window mainWindow = new Window("Window");
        
        Label maximizedLabel = new Label("This label is taking up all horizontal space in the window");
        Panel horizontalPanel = new Panel(Panel.Orientation.HORISONTAL);
        horizontalPanel.addComponent(maximizedLabel, SizePolicy.MAXIMUM);
        mainWindow.addComponent(horizontalPanel);
        
        Panel buttonPanel = new Panel(Panel.Orientation.HORISONTAL);
        buttonPanel.addComponent(new EmptySpace(), SizePolicy.GROWING);
        Button button1 = new Button("Exit", new Action() {
            @Override
            public void doAction()
            {
                guiScreen.closeWindow();
            }
        });
        buttonPanel.addComponent(button1);
        mainWindow.addComponent(buttonPanel);

        guiScreen.showWindow(mainWindow, GUIScreen.Position.CENTER);
        guiScreen.getScreen().stopScreen();
    }
}
