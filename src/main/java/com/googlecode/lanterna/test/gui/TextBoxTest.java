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

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.gui.*;

/**
 *
 * @author Martin
 */
public class TextBoxTest
{
    public static void main(String[] args)
    {
        final GUIScreen guiScreen = TerminalFacade.getGUIScreen();
        guiScreen.getScreen().startScreen();
        final Window window1 = new Window("Text box window");
        //window1.addComponent(new Widget(1, 1));

        Panel mainPanel = new Panel(new Border.Invisible(), Panel.Orientation.VERTICAL);
        for(int i = 0; i < 5; i++) {
            Panel editPanel = new Panel(new Border.Invisible(), Panel.Orientation.HORISONTAL);
            editPanel.addComponent(new Label("TextBox " + (i+1) + ":"));
            editPanel.addComponent(new TextBox(20, ""));
            mainPanel.addComponent(editPanel);
        }
        window1.addComponent(mainPanel);

        Panel buttonPanel = new Panel(new Border.Invisible(), Panel.Orientation.HORISONTAL);
        Button exitButton = new Button("Exit", new Action() {
                public void doAction()  {
                    guiScreen.closeWindow(window1);
                }
            });
        buttonPanel.addComponent(new EmptySpace(20, 1));
        buttonPanel.addComponent(exitButton);
        window1.addComponent(buttonPanel);
        guiScreen.showWindow(window1, GUIScreen.Position.CENTER);
        guiScreen.getScreen().stopScreen();
    }
}
