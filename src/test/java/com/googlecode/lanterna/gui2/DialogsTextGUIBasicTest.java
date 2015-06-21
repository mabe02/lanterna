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
 * Copyright (C) 2010-2015 Martin
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TestTerminalFactory;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialog;
import com.googlecode.lanterna.screen.Screen;

import java.io.EOFException;
import java.io.IOException;

/**
 *
 * @author Martin
 */
public class DialogsTextGUIBasicTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        Screen screen = new TestTerminalFactory(args).createScreen();
        screen.startScreen();
        final WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);
        try {
            final BasicWindow window = new BasicWindow("Dialog test");

            Panel mainPanel = new Panel();
            ActionListBox dialogsListBox = new ActionListBox();
            dialogsListBox.addItem("TextInputDialog", new Runnable() {
                @Override
                public void run() {
                    String result = TextInputDialog.showDialog(textGUI, "TextInputDialog sample", "This is the description", "initialContent");
                    System.out.println("Result was: " + result);
                }
            });

            mainPanel.addComponent(dialogsListBox);
            mainPanel.addComponent(new EmptySpace(TerminalSize.ONE));
            mainPanel.addComponent(new Button("Exit", new Runnable() {
                @Override
                public void run() {
                    window.close();
                }
            }));
            window.setComponent(mainPanel);

            textGUI.addWindow(window);
            textGUI.updateScreen();
            while(!textGUI.getWindows().isEmpty()) {
                if(!textGUI.processInputAndUpdateScreen()) {
                    Thread.sleep(1);
                }
            }
        }
        catch (EOFException ignore) {
            //Window was closed
        }
        finally {
            screen.stopScreen();
        }
    }
}
