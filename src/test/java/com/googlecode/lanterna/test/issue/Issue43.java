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
import com.googlecode.lanterna.gui.component.ActionListBox;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.component.EmptySpace;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.component.TextBox;
import com.googlecode.lanterna.gui.dialog.MessageBox;
import com.googlecode.lanterna.gui.layout.LinearLayout;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.test.TestTerminalFactory;

/**
 *
 * @author Martin
 */
public class Issue43 {
    public static void main(String[] args) {
        final GUIScreen guiScreen = new TestTerminalFactory(args).createGUIScreen();
        final Window window = new Window("Testing issue 43");
        window.addComponent(new Label("Here's an ActionListBox with fixed width"));
        final ActionListBox actionListBox = new ActionListBox(new TerminalSize(20, 10));
        window.addComponent(actionListBox);
        window.addComponent(new EmptySpace());
        window.addComponent(new Label("Add new item"));
        
        Panel panel = new Panel(Panel.Orientation.HORIZONTAL);
        final TextBox textBox = new TextBox();
        panel.addComponent(textBox, LinearLayout.GROWS_HORIZONTALLY);
        panel.addComponent(new Button("Add", new Action() {
            @Override
            public void doAction() {
                if(textBox.getText().trim().length() == 0)
                    return;
                
                final String text = textBox.getText().trim();
                actionListBox.addAction(text, new Action() {
                    @Override
                    public void doAction() {
                        MessageBox.showMessageBox(guiScreen, "Action selected", "You selected:\n" + text);
                    }
                });
            }
        }));
        window.addComponent(panel, LinearLayout.GROWS_HORIZONTALLY);
        
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
