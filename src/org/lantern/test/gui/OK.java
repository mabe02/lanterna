/*
 * Copyright (C) 2012 Martin
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.lantern.test.gui;

import org.lantern.LanternException;
import org.lantern.LanternTerminal;
import org.lantern.gui.*;
import org.lantern.gui.theme.Theme;
import org.lantern.terminal.TerminalSize;

/**
 *
 * @author Martin
 */
public class OK {
    public static void main(String[] args) throws LanternException {
        LanternTerminal terminal = new LanternTerminal();
        final GUIScreen terminalGUIScreen = terminal.getGUIScreen();
        if(terminalGUIScreen == null) {
            System.err.println("Couldn't allocate a terminal!");
            return;
        }

        terminal.start();
        terminalGUIScreen.setTitle("GUI Test");

        final Window mainWindow = new Window("Lanterna");        
        mainWindow.addComponent(new Label("Welcome!"));
        terminalGUIScreen.showWindow(mainWindow, GUIScreen.Position.CENTER);
        terminal.stopAndRestoreTerminal();
    }
}
