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

package com.googlecode.lanterna.test.issue;

import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.Border;
import com.googlecode.lanterna.gui.DefaultBackgroundRenderer;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.component.EmptySpace;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.test.TestTerminalFactory;

/**
 * http://code.google.com/p/lanterna/issues/detail?id=7
 * 
 * Verifying that a panel with invisible border has no title
 * 
 * @author Martin
 */
public class Issue7a {
    public static void main(String[] args) {
        final GUIScreen textGUI = new TestTerminalFactory(args).createGUIScreen();
        textGUI.getScreen().startScreen();
        textGUI.setBackgroundRenderer(new DefaultBackgroundRenderer("GUI Test"));
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
                textGUI.closeWindow();
            }
        }));

        textGUI.showWindow(mainWindow, GUIScreen.Position.CENTER);
        textGUI.getScreen().stopScreen();
    }
}
