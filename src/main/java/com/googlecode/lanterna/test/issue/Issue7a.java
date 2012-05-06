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
 * Copyright (C) 2010-2012 mabe02
 */

package com.googlecode.lanterna.test.issue;

import com.googlecode.lanterna.LanternException;
import com.googlecode.lanterna.LanternTerminal;
import com.googlecode.lanterna.gui.*;

/**
 * http://code.google.com/p/lanterna/issues/detail?id=7
 * 
 * Verifying that a panel with invisible border has no title
 * 
 * @author Martin
 */
public class Issue7a {
    public static void main(String[] args) throws LanternException {
        final LanternTerminal terminal = new LanternTerminal();
        terminal.start();
        final GUIScreen textGUI = terminal.getGUIScreen();

        textGUI.setTitle("GUI Test");
        final Window mainWindow = new Window("Testing issue 7");
        mainWindow.addComponent(new EmptySpace(16, 1));
        Panel panel = new Panel("Panel");
	panel.setBorder(new Border.Invisible());
	panel.addComponent(new Label("Label 1"));
	panel.addComponent(new Label("Label 2"));
	panel.addComponent(new Label("Label 3"));
        mainWindow.addComponent(panel);
        mainWindow.addComponent(new EmptySpace(16, 1));
        mainWindow.addComponent(new Button("Close", new Action() {
            public void doAction()
            {
                textGUI.closeWindow(mainWindow);
            }
        }));

        textGUI.showWindow(mainWindow, GUIScreen.Position.CENTER);
        terminal.stopAndRestoreTerminal();
    }
}
