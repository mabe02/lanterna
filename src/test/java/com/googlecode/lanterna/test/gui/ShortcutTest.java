/*
 * This file is part of lanterna (http://code.google.com/p/lanterna/).
 * 
 * lanterna is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright (C) 2010-2012 Martin
 */
package com.googlecode.lanterna.test.gui;

import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.Component;
import com.googlecode.lanterna.gui.DefaultBackgroundRenderer;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.TextGraphics;
import com.googlecode.lanterna.gui.Theme;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.AbstractComponent;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.dialog.MessageBox;
import com.googlecode.lanterna.gui.layout.LinearLayout;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.terminal.text.UnixTerminal;
import com.googlecode.lanterna.test.TestTerminalFactory;
import java.nio.charset.Charset;

/**
 *
 * @author Martin
 */
public class ShortcutTest {
    public static void main(String[] args)
    {
        Terminal terminal = new TestTerminalFactory(args).createTerminal();        
        if(terminal instanceof UnixTerminal) {
            terminal = new UnixTerminal(System.in, System.out, Charset.forName("UTF-8"), 
                                            null, UnixTerminal.Behaviour.CTRL_C_KILLS_APPLICATION);            
        }        
        final GUIScreen guiScreen = new GUIScreen(new Screen(terminal));
        guiScreen.getScreen().startScreen();
        guiScreen.setBackgroundRenderer(new DefaultBackgroundRenderer("GUI Test"));

        final Window mainWindow = new Window("Window with panels");
        mainWindow.addComponent(new Label("Shortcuts to try:"));
        mainWindow.addComponent(new Label("m"));
        mainWindow.addComponent(new Label("ctrl + c"));
        mainWindow.addComponent(new Label("alt + v"));
        mainWindow.addComponent(new Label("ctrl + alt + x"));
        Panel buttonPanel = new Panel(Panel.Orientation.HORIZONTAL);
        Button button1 = new Button("Exit", new Action() {
            @Override
            public void doAction()
            {
                mainWindow.close();
            }
        });
        button1.setAlignment(Component.Alignment.CENTER);
        buttonPanel.addComponent(button1, LinearLayout.GROWS_HORIZONTALLY);
        buttonPanel.addShortcut(Key.Kind.Home, new Action() {
            @Override
            public void doAction() {
                MessageBox.showMessageBox(guiScreen, "Shortcut triggered", "You triggered a shortcut by pressing home!");
            }
        });
        buttonPanel.addShortcut('m', false, false, new Action() {
            @Override
            public void doAction() {
                MessageBox.showMessageBox(guiScreen, "Shortcut triggered", "You triggered a shortcut by pressing 'm'!");
            }
        });
        buttonPanel.addShortcut('c', true, false, new Action() {
            @Override
            public void doAction() {
                MessageBox.showMessageBox(guiScreen, "Shortcut triggered", "You triggered a shortcut by pressing ctrl+c!");
            }
        });
        buttonPanel.addShortcut('v', false, true, new Action() {
            @Override
            public void doAction() {
                MessageBox.showMessageBox(guiScreen, "Shortcut triggered", "You triggered a shortcut by pressing alt+v!");
            }
        });
        buttonPanel.addShortcut('x', true, true, new Action() {
            @Override
            public void doAction() {
                MessageBox.showMessageBox(guiScreen, "Shortcut triggered", "You triggered a shortcut by pressing ctrl+alt+x!");
            }
        });
        mainWindow.addComponent(buttonPanel, LinearLayout.GROWS_HORIZONTALLY);

        guiScreen.showWindow(mainWindow, GUIScreen.Position.CENTER);
        guiScreen.getScreen().stopScreen();
    }
}
