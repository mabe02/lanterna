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
import com.googlecode.lanterna.terminal.text.CygwinTerminal;
import com.googlecode.lanterna.terminal.text.UnixTerminal;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * This class gives an easy facade over the whole Lanterna terminal construction
 * system. You can invoke methods in this class if you quickly want a Terminal,
 * Screen or GUIScreen object with default settings applied.
 * @author Martin
 */
public class TerminalFacade {

    private TerminalFacade() {}
    
    private static final Charset DEFAULT_CHARSET = Charset.forName(System.getProperty("file.encoding"));
    
    public static Terminal createTerminal() 
    {
        return createTerminal(DEFAULT_CHARSET);
    }
    
    public static Terminal createTerminal(Charset terminalCharset)
    {
        return createTerminal(System.in, System.out, terminalCharset);
    }
    
    public static Terminal createTerminal(
                                    InputStream terminalInput, 
                                    OutputStream terminalOutput)
    {
        return createTerminal(terminalInput, terminalOutput, DEFAULT_CHARSET);
    }
    
    public static Terminal createTerminal(
                                    InputStream terminalInput, 
                                    OutputStream terminalOutput,
                                    Charset terminalCharset)
    {
        if(GraphicsEnvironment.isHeadless())
            return createTextTerminal(terminalInput, terminalOutput, terminalCharset);
        else
            return createSwingTerminal();
    }
    
    public static SwingTerminal createSwingTerminal()
    {
        return createSwingTerminal(100, 30);
    }
    
    public static SwingTerminal createSwingTerminal(int columns, int rows)
    {
        return new SwingTerminal(columns, rows);
    }
    
    public static UnixTerminal createUnixTerminal() 
    {
        return createUnixTerminal(DEFAULT_CHARSET);
    }
    
    public static UnixTerminal createUnixTerminal(Charset terminalCharset)
    {
        return createUnixTerminal(System.in, System.out, terminalCharset);
    }
    
    public static UnixTerminal createUnixTerminal(
                                    InputStream terminalInput, 
                                    OutputStream terminalOutput)
    {
        return createUnixTerminal(terminalInput, terminalOutput, DEFAULT_CHARSET);
    }
    
    public static UnixTerminal createUnixTerminal(
                                    InputStream terminalInput, 
                                    OutputStream terminalOutput,
                                    Charset terminalCharset)
    {
        return new UnixTerminal(terminalInput, terminalOutput, terminalCharset);
    }
    
    public static CygwinTerminal createCygwinTerminal() 
    {
        return createCygwinTerminal(DEFAULT_CHARSET);
    }
    
    public static CygwinTerminal createCygwinTerminal(Charset terminalCharset)
    {
        return createCygwinTerminal(System.in, System.out, terminalCharset);
    }
    
    public static CygwinTerminal createCygwinTerminal(
                                    InputStream terminalInput, 
                                    OutputStream terminalOutput)
    {
        return createCygwinTerminal(terminalInput, terminalOutput, DEFAULT_CHARSET);
    }
    
    public static CygwinTerminal createCygwinTerminal(
                                    InputStream terminalInput, 
                                    OutputStream terminalOutput,
                                    Charset terminalCharset)
    {
        return new CygwinTerminal(terminalInput, terminalOutput, terminalCharset);
    }
    
    
    
    public static Terminal createTextTerminal() 
    {
        return createTextTerminal(System.in, System.out, DEFAULT_CHARSET);
    }
    
    public static Terminal createTextTerminal(InputStream terminalInput, 
                                    OutputStream terminalOutput,
                                    Charset terminalCharset) 
    {
        if(System.getProperty("os.name", "").toLowerCase().startsWith("windows"))
            return createCygwinTerminal(terminalInput, terminalOutput, terminalCharset);
        else
            return createUnixTerminal(terminalInput, terminalOutput, terminalCharset);
    }
    
    public static Screen createScreen() {
        return createScreen(createTerminal());
    }
    
    public static Screen createScreen(Terminal terminal) {
        return new Screen(terminal);
    }
    
    public static GUIScreen createGUIScreen() {
        return new GUIScreen(createScreen());
    }
    
    public static GUIScreen createGUIScreen(Terminal terminal) {
        return new GUIScreen(createScreen(terminal));
    }
    
    public static GUIScreen createGUIScreen(Screen screen) {
        return new GUIScreen(screen);
    }
}
