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
import com.googlecode.lanterna.gui.Border;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.*;
import com.googlecode.lanterna.gui.dialog.DialogButtons;
import com.googlecode.lanterna.gui.dialog.MessageBox;
import com.googlecode.lanterna.gui.layout.LinearLayout;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.test.TestTerminalFactory;

/**
 *
 * @author Martin
 */
public class TextAreaTest {
    public static void main(String[] args) {
        final GUIScreen guiScreen = new TestTerminalFactory(args).createGUIScreen();
        guiScreen.getScreen().startScreen();
        final Window window1 = new Window("Text window");
        //window1.addComponent(new Widget(1, 1));

        final TextArea textArea = new TextArea("TextArea");
        textArea.setMaximumSize(new TerminalSize(80, 10));
        window1.addComponent(textArea);
        
        window1.addComponent(new EmptySpace(1, 1));
        
        Panel appendPanel = new Panel(new Border.Invisible(), Panel.Orientation.HORISONTAL);
        ((LinearLayout)appendPanel.getLayoutManager()).setPadding(1);
        final TextBox appendBox = new TextBox("", 30);
        Button appendButton = new Button("Append", new Action() {
            @Override
            public void doAction() {
                textArea.appendLine(appendBox.getText());
            }
        });
        appendPanel.addComponent(appendBox);
        appendPanel.addComponent(appendButton);
        window1.addComponent(appendPanel);
        
        Panel removePanel = new Panel(new Border.Invisible(), Panel.Orientation.HORISONTAL);
        ((LinearLayout)removePanel.getLayoutManager()).setPadding(1);
        final TextBox removeBox = new TextBox("0", 5);
        Button removeButton = new Button("Remove line", new Action() {
            @Override
            public void doAction() {
                try {
                    int lineNumber = Integer.parseInt(removeBox.getText());
                    textArea.removeLine(lineNumber);
                }
                catch(Exception e) {
                    MessageBox.showMessageBox(guiScreen, e.getClass().getSimpleName(), e.getMessage(), DialogButtons.OK);
                }
            }
        });
        Button clearButton = new Button("Clear text", new Action() {
            @Override
            public void doAction() {
                textArea.clear();
            }
        });
        removePanel.addComponent(removeBox);
        removePanel.addComponent(removeButton);
        removePanel.addComponent(clearButton);
        window1.addComponent(removePanel);
        
        window1.addComponent(new EmptySpace(1, 1));
        
        Panel lastPanel = new Panel(new Border.Invisible(), Panel.Orientation.HORISONTAL);
        Button exitButton = new Button("Exit", new Action() {            
            @Override
            public void doAction() {
                guiScreen.closeWindow();
            }
        });
        lastPanel.addComponent(new EmptySpace(1, 1), LinearLayout.GROWS_HORIZONTALLY);
        lastPanel.addComponent(exitButton);
        window1.addComponent(lastPanel);
        guiScreen.showWindow(window1, GUIScreen.Position.CENTER);
        guiScreen.getScreen().stopScreen();
    }
}
