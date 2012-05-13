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

package com.googlecode.lanterna.test;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;

/**
 * This class provides a unified way for the test program to get their terminal
 * objects
 * @author Martin
 */
public class TestTerminalFactory {
    
    private final boolean forceUnixTerminal;
    
    public TestTerminalFactory(String[] args) {
        forceUnixTerminal = args.length > 0 && "--no-swing".equals(args[0]);
    }
    
    public Terminal createTerminal() {
        if(forceUnixTerminal)
            return TerminalFacade.createTextTerminal();
        else
            return TerminalFacade.createTerminal();
    }
    
    public Screen createScreen() {
        return TerminalFacade.createScreen(createTerminal());
    }
    
    public GUIScreen createGUIScreen() {
        return TerminalFacade.createGUIScreen(createScreen());
    }
}
