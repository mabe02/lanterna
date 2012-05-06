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

package com.googlecode.lanterna;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;

/**
 * This is a helper class for creating the three types of terminal objects.
 * @author mabe02
 */
public class LanternTerminal
{
    private static final Charset DEFAULT_CHARSET = Charset.forName(System.getProperty("file.encoding"));
    private final Terminal terminal;
    private final Screen screen;
    private GUIScreen managedGUIScreen;

    public LanternTerminal() throws LanternException
    {
        this(DEFAULT_CHARSET);
    }

    public LanternTerminal(Charset terminalCharset) throws LanternException
    {
        this(new TerminalFactory.Default(), terminalCharset);
    }

    public LanternTerminal(TerminalFactory terminalFactory) throws LanternException
    {
        this(terminalFactory, DEFAULT_CHARSET);
    }

    public LanternTerminal(TerminalFactory terminalFactory, Charset terminalCharset) throws LanternException
    {
        this(terminalFactory, System.in, System.out, terminalCharset);
    }

    public LanternTerminal(TerminalFactory terminalFactory, InputStream terminalInput, 
            OutputStream terminalOutput, Charset terminalCharset) throws LanternException
    {
        this.terminal = terminalFactory.createTerminal(terminalInput, terminalOutput, terminalCharset);
        this.screen = new Screen(terminal);
        this.managedGUIScreen = null;
    }

    public void start() throws LanternException
    {
        screen.startScreen();
    }

    public void stopAndRestoreTerminal() throws LanternException
    {
        screen.stopScreen();
    }

    public Terminal getUnderlyingTerminal()
    {
        return terminal;
    }

    public Screen getScreen()
    {
        return screen;
    }

    public void refreshScreen() throws LanternException
    {
        screen.refresh();
    }
    
    public GUIScreen getGUIScreen()
    {
        if(managedGUIScreen != null)
            return managedGUIScreen;

        managedGUIScreen = new GUIScreen(screen);
        return managedGUIScreen;
    }
}
