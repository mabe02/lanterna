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

import com.googlecode.lanterna.LanternTerminal;
import com.googlecode.lanterna.LanternaException;
import com.googlecode.lanterna.gui.*;
import com.googlecode.lanterna.terminal.TerminalSize;

/**
 *
 * @author Martin
 */
public class TooMuchTextTest
{
    public static void main(String[] args) throws LanternaException
    {
        if(args.length > 0) {
            try {
                Thread.sleep(15000);
            }
            catch(InterruptedException e) {
            }
        }

        LanternTerminal terminal = new LanternTerminal();
        final GUIScreen terminalGUIScreen = terminal.getGUIScreen();
        if(terminalGUIScreen == null) {
            System.err.println("Couldn't allocate a terminal!");
            return;
        }

        terminal.start();
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
                    terminalGUIScreen.closeWindow(window1);
                }
            });
        buttonPanel.addComponent(new EmptySpace(20, 1));
        buttonPanel.addComponent(exitButton);
        window1.addComponent(buttonPanel);
        terminalGUIScreen.showWindow(window1, GUIScreen.Position.CENTER);
        terminal.stopAndRestoreTerminal();
    }
}
