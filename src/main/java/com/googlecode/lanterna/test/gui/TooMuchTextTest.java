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
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.component.EmptySpace;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.component.StaticTextArea;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.test.TestTerminalFactory;

/**
 *
 * @author Martin
 */
public class TooMuchTextTest
{
    public static void main(String[] args)
    {
        final GUIScreen guiScreen = new TestTerminalFactory(args).createGUIScreen();
        guiScreen.getScreen().startScreen();
        final Window window1 = new Window("Text window");
        //window1.addComponent(new Widget(1, 1));

        StaticTextArea staticTextArea = new StaticTextArea(new TerminalSize(80, 10),
                "This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide.\n" + 
                "This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide.\n" + 
                "This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide.\n" + 
                "This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide.\n" + 
                "This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide.\n" + 
                "This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide.\n" + 
                "This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide.\n" + 
                "This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide.\n" + 
                "This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide.\n" + 
                "This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide.\n" + 
                "This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide.\n" + 
                "This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide.\n" + 
                "This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide.\n" + 
                "This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide.\n" + 
                "This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide.\n" + 
                "This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide.\n" + 
                "This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide.\n" + 
                "This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide.\n" + 
                "This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide.\n" + 
                "This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide.\n" + 
                "This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide.\n" + 
                "This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide.\n" + 
                "This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide.\n" + 
                "This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide.\n" + 
                "This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide.\n" + 
                "This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide.\n" + "This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide.\n" + 
                "This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide.\n" + 
                "This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide.\n" + 
                "This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide.\n" + 
                "This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide.\n" + 
                "This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide.\n" + 
                "This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide.\n" + 
                "This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide.\n" + 
                "This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide.\n" + 
                "This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide.\n" + 
                "This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide.\n" + "This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide. This text is very wide.\n" + 
                "Almost at the bottom\n" + 
                "Last"
                );
        
        window1.addComponent(staticTextArea);

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
}
