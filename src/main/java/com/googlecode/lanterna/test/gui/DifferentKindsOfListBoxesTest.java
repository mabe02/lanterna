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
import com.googlecode.lanterna.gui.dialog.MessageBox;
import com.googlecode.lanterna.gui.layout.LinearLayout;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.test.TestTerminalFactory;

/**
 *
 * @author Martin
 */
public class DifferentKindsOfListBoxesTest {
    public static void main(String[] args) {
        final GUIScreen guiScreen = new TestTerminalFactory(args).createGUIScreen();
        guiScreen.getScreen().startScreen();
        
        final Window window1 = new Window("List boxes window");
        
        Panel mainPanel = new Panel(new Border.Invisible(), Panel.Orientation.HORISONTAL);
        ((LinearLayout)mainPanel.getLayoutManager()).setPadding(1);
        
        final CheckBoxList checkBoxList = new CheckBoxList();
        checkBoxList.addItem("First");
        checkBoxList.addItem("Second");
        checkBoxList.addItem("Third");
        checkBoxList.addItem("Fourth");
        checkBoxList.addItem("Fifth");
        checkBoxList.addItem("Första");
        checkBoxList.addItem("Andra");
        checkBoxList.addItem("Tredje");
        checkBoxList.addItem("Fjärde");
        checkBoxList.addItem("Femte");
        checkBoxList.addItem("Ichi");
        checkBoxList.addItem("Ni");
        checkBoxList.addItem("San");
        checkBoxList.addItem("Yon");
        checkBoxList.addItem("Go");
        checkBoxList.setPreferredSize(new TerminalSize(0, 8));
        
        RadioCheckBoxList radioCheckBoxList = new RadioCheckBoxList();
        radioCheckBoxList.addItem("First");
        radioCheckBoxList.addItem("Second");
        radioCheckBoxList.addItem("Third");
        radioCheckBoxList.addItem("Fourth");
        radioCheckBoxList.addItem("Fifth");
        radioCheckBoxList.addItem("Första");
        radioCheckBoxList.addItem("Andra");
        radioCheckBoxList.addItem("Tredje");
        radioCheckBoxList.addItem("Fjärde");
        radioCheckBoxList.addItem("Femte");
        radioCheckBoxList.addItem("Ichi");
        radioCheckBoxList.addItem("Ni");
        radioCheckBoxList.addItem("San");
        radioCheckBoxList.addItem("Yon");
        radioCheckBoxList.addItem("Go");
        radioCheckBoxList.setPreferredSize(new TerminalSize(0, 8));
        
        ActionListBox actionListBox = new ActionListBox();
        actionListBox.addAction(new RandomAction(guiScreen));
        actionListBox.addAction(new RandomAction(guiScreen));
        actionListBox.addAction(new RandomAction(guiScreen));
        actionListBox.addAction(new RandomAction(guiScreen));
        actionListBox.addAction(new RandomAction(guiScreen));
        actionListBox.addAction(new RandomAction(guiScreen));
        actionListBox.addAction(new RandomAction(guiScreen));
        actionListBox.addAction(new RandomAction(guiScreen));
        actionListBox.addAction(new RandomAction(guiScreen));
        actionListBox.addAction(new RandomAction(guiScreen));
        actionListBox.addAction(new RandomAction(guiScreen));
        actionListBox.addAction(new RandomAction(guiScreen));
        actionListBox.addAction(new RandomAction(guiScreen));
        actionListBox.setPreferredSize(new TerminalSize(0, 8));   
        
        mainPanel.addComponent(checkBoxList);
        mainPanel.addComponent(radioCheckBoxList);
        mainPanel.addComponent(actionListBox);
        window1.addComponent(mainPanel);        
        window1.addComponent(new EmptySpace());


        Panel buttonPanel = new Panel(new Border.Invisible(), Panel.Orientation.HORISONTAL);
        Button exitButton = new Button("Exit", new Action() {
                @Override
                public void doAction()  {
                    guiScreen.closeWindow();
                }
            });
        buttonPanel.addComponent(new EmptySpace(1, 1), LinearLayout.GROWS_HORIZONTALLY);
        buttonPanel.addComponent(exitButton);
        window1.addComponent(buttonPanel, LinearLayout.GROWS_HORIZONTALLY);
        guiScreen.showWindow(window1, GUIScreen.Position.CENTER);
        guiScreen.getScreen().stopScreen();
    }

    private static class RandomAction implements Action {

        private static int counter = 1;
        private final String label;
        private final GUIScreen guiScreen;

        public RandomAction(GUIScreen guiScreen) {
            this.label = "Action #" + (counter++);
            this.guiScreen = guiScreen;
        }
        
        @Override
        public void doAction() {
            MessageBox.showMessageBox(guiScreen, "Action", label + " selected");
        }

        @Override
        public String toString() {
            return label;
        }
    }
}
