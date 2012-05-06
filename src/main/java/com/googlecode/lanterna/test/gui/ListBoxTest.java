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
 * Copyright (C) 2010-2011 mabe02
 */

package com.googlecode.lanterna.test.gui;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.googlecode.lanterna.LanternException;
import com.googlecode.lanterna.LanternTerminal;
import com.googlecode.lanterna.TerminalFactory;
import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.Border;
import com.googlecode.lanterna.gui.Button;
import com.googlecode.lanterna.gui.EmptySpace;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.ListBox;
import com.googlecode.lanterna.gui.Panel;
import com.googlecode.lanterna.gui.Window;

/**
 *
 * @author mabe02
 */
public class ListBoxTest
{
    public static boolean cancelThread = false;
    
    public static void main(String[] args) throws LanternException
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
        final Window window1 = new Window("List box window");
        //window1.addComponent(new Widget(1, 1));

        Panel mainPanel = new Panel(new Border.Invisible(), Panel.Orientation.VERTICAL);
        final ListBox listBox = new ListBox(10, 5);

        Thread thread = new Thread() {
            @Override
            public void run()
            {
                for(int i = 0; i < 15; i++) {
                    try {
                        Thread.sleep(1000);
                    }
                    catch(InterruptedException e) {}
                    final Integer count = i + 1;
                    terminalGUIScreen.runInEventThread(new Action() {
                        public void doAction() throws LanternException
                        {
                            listBox.addItem("Item #" + count.intValue());
                        }
                    });
                    if(cancelThread)
                        break;
                }
                Random random = new Random();
                while(!cancelThread) {
                    try {
                        Thread.sleep(2000);
                    }
                    catch(InterruptedException e) {}
                    listBox.setSelectedItem(random.nextInt(15));
                }
            }
        };
        
        mainPanel.addComponent(listBox);
        window1.addComponent(mainPanel);

        Panel buttonPanel = new Panel(new Border.Invisible(), Panel.Orientation.HORISONTAL);
        Button exitButton = new Button("Exit", new Action() {
                public void doAction()  {
                    terminalGUIScreen.closeWindow(window1);
                }
            });
        buttonPanel.addComponent(new EmptySpace(20, 1));
        buttonPanel.addComponent(exitButton);
        window1.addComponent(buttonPanel);
        thread.start();
        terminalGUIScreen.showWindow(window1, GUIScreen.Position.CENTER);
        cancelThread = true;
        terminal.stopAndRestoreTerminal();
    }
}
