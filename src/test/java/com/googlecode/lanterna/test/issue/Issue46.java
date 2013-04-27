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
import com.googlecode.lanterna.gui.Component;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.component.EmptySpace;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.layout.LinearLayout;
import com.googlecode.lanterna.gui.layout.VerticalLayout;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.test.TestTerminalFactory;

public class Issue46 {
    public static void main(String[] args) {
        final GUIScreen guiScreen = new TestTerminalFactory(args).createGUIScreen();
        final Window window = new Window("Testing issue 46");
        Panel panel = new Panel("Panel with a right-aligned button");
        panel.setPreferredSize(new TerminalSize(50, 3));
        panel.setLayoutManager(new VerticalLayout());
        Button button = new Button("Button");
        button.setAlignment(Component.Alignment.RIGHT_CENTER);
        panel.addComponent(button, LinearLayout.GROWS_HORIZONTALLY);
        
        window.addComponent(panel);
        window.addComponent(new EmptySpace());
        
        Button quitButton = new Button("Exit", new Action() {
            @Override
            public void doAction() {
                window.close();
            }
        });
        quitButton.setAlignment(Component.Alignment.RIGHT_CENTER);
        window.addComponent(quitButton, LinearLayout.GROWS_HORIZONTALLY);
        
        guiScreen.getScreen().startScreen();
        guiScreen.showWindow(window);
        guiScreen.getScreen().stopScreen();
    } 
}
