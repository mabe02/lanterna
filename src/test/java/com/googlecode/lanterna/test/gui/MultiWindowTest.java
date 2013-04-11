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
 * Copyright (C) 2010-2013 Martin
 */
package com.googlecode.lanterna.test.gui;

import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.dialog.MessageBox;
import com.googlecode.lanterna.test.TestTerminalFactory;

public class MultiWindowTest {
    public static void main(String[] args) {        
        final GUIScreen guiScreen = new TestTerminalFactory(args).createGUIScreen();
        guiScreen.getScreen().startScreen();
        guiScreen.showWindow(new MyWindow());
        guiScreen.getScreen().stopScreen();
    }

    private static class MyWindow extends Window {
        public MyWindow() {
            super("My Window!");
            addComponent(new Button("Button with no action"));
            addComponent(new Button("Button with action", new Action() {
                @Override
                public void doAction() {
                    MessageBox.showMessageBox(getOwner(), "Hello", "You selected the button with an action attached to it!");
                }
            }));
            addComponent(new Button("MessageBox close", new Action() {
                @Override
                public void doAction() {
                    MessageBox.showMessageBox(getOwner(), "Information", "When you close this dialog, the owner window will close too");
                    MyWindow.this.close();
                }
            }));
            addComponent(new Button("New window", new Action() {
                @Override
                public void doAction() {
                    getOwner().showWindow(new MyWindow());
                }
            }));
            addComponent(new Button("Close window", new Action() {
                @Override
                public void doAction() {
                    close();
                }
            }));
        }
    }
}
