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
package com.googlecode.lanterna;

import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminal;
import com.googlecode.lanterna.terminal.text.UnixTerminal;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * This class gives an easy facade over the whole Lanterna terminal construction
 * system
 * @author Martin
 */
public class Lanterna {
    private Lanterna() {}
    
    private static final Charset DEFAULT_CHARSET = Charset.forName(System.getProperty("file.encoding"));
    
    public static Terminal getTerminal() 
    {
        return getTerminal(DEFAULT_CHARSET);
    }
    
    public static Terminal getTerminal(Charset terminalCharset)
    {
        return getTerminal(System.in, System.out, terminalCharset);
    }
    
    public static Terminal getTerminal(
                                    InputStream terminalInput, 
                                    OutputStream terminalOutput)
    {
        return getTerminal(terminalInput, terminalOutput, DEFAULT_CHARSET);
    }
    
    public static Terminal getTerminal(
                                    InputStream terminalInput, 
                                    OutputStream terminalOutput,
                                    Charset terminalCharset)
    {
        if(GraphicsEnvironment.isHeadless())
            return getUnixTerminal(terminalInput, terminalOutput, terminalCharset);
        else
            return getSwingTerminal();
    }
    
    public static SwingTerminal getSwingTerminal()
    {
        return getSwingTerminal(100, 30);
    }
    
    public static SwingTerminal getSwingTerminal(int columns, int rows)
    {
        return new SwingTerminal(columns, rows);
    }
    
    public static UnixTerminal getUnixTerminal() 
    {
        return getUnixTerminal(DEFAULT_CHARSET);
    }
    
    public static UnixTerminal getUnixTerminal(Charset terminalCharset)
    {
        return getUnixTerminal(System.in, System.out, terminalCharset);
    }
    
    public static UnixTerminal getUnixTerminal(
                                    InputStream terminalInput, 
                                    OutputStream terminalOutput)
    {
        return getUnixTerminal(terminalInput, terminalOutput, DEFAULT_CHARSET);
    }
    
    public static UnixTerminal getUnixTerminal(
                                    InputStream terminalInput, 
                                    OutputStream terminalOutput,
                                    Charset terminalCharset)
    {
        return new UnixTerminal(terminalInput, terminalOutput, terminalCharset);
    }
    
    public static Screen getScreen() {
        return new Screen(getTerminal());
    }
    
    public static GUIScreen getGUIScreen() {
        return new GUIScreen(getScreen());
    }
}
