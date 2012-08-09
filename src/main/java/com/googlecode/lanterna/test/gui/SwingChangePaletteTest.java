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
import com.googlecode.lanterna.gui.component.ActionListBox;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.component.EmptySpace;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.dialog.MessageBox;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.swing.SwingTerminal;
import com.googlecode.lanterna.terminal.swing.TerminalPalette;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 *
 * @author Martin
 */
public class SwingChangePaletteTest {
    public static void main(String[] args) throws Exception
    {
        final SwingTerminal swingTerminal = new SwingTerminal();
        final GUIScreen guiScreen = new GUIScreen(new Screen(swingTerminal));
        guiScreen.getScreen().startScreen();
        final Window window1 = new Window("Palette Switcher");

        Panel mainPanel = new Panel(new Border.Invisible(), Panel.Orientation.VERTICAL);
        ActionListBox actionListBox = new ActionListBox();
        
        Field[] fields = TerminalPalette.class.getFields();
        for(Field field: fields) {
            if(field.getType() != TerminalPalette.class)
                continue;
            
            if((field.getModifiers() & Modifier.STATIC) != 0)
                actionListBox.addAction(new ActionListBoxItem(guiScreen, field));
        }
        
        mainPanel.addComponent(actionListBox);
        window1.addComponent(mainPanel);

        Panel buttonPanel = new Panel(new Border.Invisible(), Panel.Orientation.HORISONTAL);
        Button exitButton = new Button("Exit", new Action() {
                public void doAction()  {
                    guiScreen.closeWindow();
                }
            });
        buttonPanel.addComponent(new EmptySpace(20, 1));
        buttonPanel.addComponent(exitButton);
        window1.addComponent(buttonPanel);
        guiScreen.showWindow(window1, GUIScreen.Position.CENTER);
        guiScreen.getScreen().stopScreen();
    }
    
    private static class ActionListBoxItem implements Action {
        private final GUIScreen owner;
        private final TerminalPalette palette;
        private final String label;

        private ActionListBoxItem(GUIScreen owner, Field field) throws Exception {
            this.owner = owner;
            this.label = field.getName();
            this.palette = (TerminalPalette)field.get(null);
        }
        
        @Override
        public String toString() {
            return label;
        }

        @Override
        public void doAction() {
            MessageBox.showMessageBox(owner, "Palette", "Will change palette to " + label + "...");
            ((SwingTerminal)owner.getScreen().getTerminal()).setTerminalPalette(palette);
            MessageBox.showMessageBox(owner, "Palette", "Palette changed to " + label + "!");
        }
    }
}
