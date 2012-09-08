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
import com.googlecode.lanterna.terminal.swing.TerminalAppearance;
import com.googlecode.lanterna.terminal.text.CygwinTerminal;
import com.googlecode.lanterna.terminal.text.UnixTerminal;
import java.awt.Font;
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
    
    /**
     * This method will return a {@code SwingTerminal} if
     * you are running the program on a system with a graphical environment
     * available, otherwise a suitable text-based {@code Terminal}, all with 
     * default settings.
     */
    public static Terminal createTerminal() 
    {
        return createTerminal(DEFAULT_CHARSET);
    }
    
    /**
     * Creates a default terminal with a specified character set. 
     * This method will return a {@code SwingTerminal} if
     * you are running the program on a system with a graphical environment
     * available, otherwise a suitable text-based {@code Terminal}.
     */
    public static Terminal createTerminal(Charset terminalCharset)
    {
        return createTerminal(System.in, System.out, terminalCharset);
    }
    
    /**
     * Creates a default terminal with a specified input/output streams. This 
     * method will return a {@code SwingTerminal} if
     * you are running the program on a system with a graphical environment
     * available, otherwise a suitable text-based {@code Terminal}.
     */
    public static Terminal createTerminal(
                                    InputStream terminalInput, 
                                    OutputStream terminalOutput)
    {
        return createTerminal(terminalInput, terminalOutput, DEFAULT_CHARSET);
    }
    
    /**
     * Creates a default terminal with a specified character set and 
     * input/output streams. This method will return a {@code SwingTerminal} if
     * you are running the program on a system with a graphical environment
     * available, otherwise a suitable text-based {@code Terminal}.
     */
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
    
    /**
     * Creates a new {@code SwingTerminal} object, a simple Swing terminal emulator,
     * 100 columns wide and 30 rows high.
     */
    public static SwingTerminal createSwingTerminal()
    {
        return createSwingTerminal(100, 30);
    }
    
    /**
     * Creates a new {@code SwingTerminal} object, a simple Swing terminal emulator,
     * with specified dimensions.
     * @param columns Width of the terminal window, in text columns <b>not</b> pixels
     * @param rows Height of the terminal window, in text rows <b>not</b> pixels
     */
    public static SwingTerminal createSwingTerminal(int columns, int rows)
    {
        return new SwingTerminal(columns, rows);
    }
    
    /**
     * Creates a new {@code SwingTerminal} object, a simple Swing terminal emulator,
     * 100 columns wide and 30 rows high.
     * @param appearance What kind of appearance to use for the terminal
     */
    public static SwingTerminal createSwingTerminal(TerminalAppearance appearance)
    {
        return createSwingTerminal(appearance, 100, 30);
    }
    
    /**
     * Creates a new {@code SwingTerminal} object, a simple Swing terminal emulator,
     * with specified dimensions.
     * @param appearance What kind of appearance to use for the terminal
     * @param columns Width of the terminal window, in text columns <b>not</b> pixels
     * @param rows Height of the terminal window, in text rows <b>not</b> pixels
     */
    public static SwingTerminal createSwingTerminal(TerminalAppearance appearance, int columns, int rows)
    {
        return new SwingTerminal(appearance, columns, rows);
    }
    
    /**
     * Creates a {@code UnixTerminal} object using the default character set
     * and {@code System.out} and {@code System.in} for input and output of the
     * terminal.
     */
    public static UnixTerminal createUnixTerminal() 
    {
        return createUnixTerminal(DEFAULT_CHARSET);
    }
    
    /**
     * Creates a {@code UnixTerminal} object that is using a supplied character
     * set when converting characters to bytes.
     */
    public static UnixTerminal createUnixTerminal(Charset terminalCharset)
    {
        return createUnixTerminal(System.in, System.out, terminalCharset);
    }
    
    /**
     * Creates a {@code UnixTerminal} object that is using supplied input and
     * output streams for standard out and standard in.
     */
    public static UnixTerminal createUnixTerminal(
                                    InputStream terminalInput, 
                                    OutputStream terminalOutput)
    {
        return createUnixTerminal(terminalInput, terminalOutput, DEFAULT_CHARSET);
    }
    
    /**
     * Creates a {@code UnixTerminal} object that is using supplied input and
     * output streams for standard out and standard in, as well as a character
     * set to be used when converting characters to bytes.
     */
    public static UnixTerminal createUnixTerminal(
                                    InputStream terminalInput, 
                                    OutputStream terminalOutput,
                                    Charset terminalCharset)
    {
        return new UnixTerminal(terminalInput, terminalOutput, terminalCharset);
    }
    
    /**
     * <b>Experimental</b> Cygwin support!
     */
    public static CygwinTerminal createCygwinTerminal() 
    {
        return createCygwinTerminal(DEFAULT_CHARSET);
    }
    
    /**
     * <b>Experimental</b> Cygwin support!
     */
    public static CygwinTerminal createCygwinTerminal(Charset terminalCharset)
    {
        return createCygwinTerminal(System.in, System.out, terminalCharset);
    }
    
    /**
     * <b>Experimental</b> Cygwin support!
     */
    public static CygwinTerminal createCygwinTerminal(
                                    InputStream terminalInput, 
                                    OutputStream terminalOutput)
    {
        return createCygwinTerminal(terminalInput, terminalOutput, DEFAULT_CHARSET);
    }
    
    /**
     * <b>Experimental</b> Cygwin support!
     */
    public static CygwinTerminal createCygwinTerminal(
                                    InputStream terminalInput, 
                                    OutputStream terminalOutput,
                                    Charset terminalCharset)
    {
        return new CygwinTerminal(terminalInput, terminalOutput, terminalCharset);
    }
    
    /*
     * Will create a suitable text terminal dependent on what environment you
     * are running from.
     */
    public static Terminal createTextTerminal() 
    {
        return createTextTerminal(System.in, System.out, DEFAULT_CHARSET);
    }
    
    /*
     * Will create a suitable text terminal dependent on what environment you
     * are running from.
     */
    public static Terminal createTextTerminal(InputStream terminalInput, 
                                    OutputStream terminalOutput,
                                    Charset terminalCharset) 
    {
        if(System.getProperty("os.name", "").toLowerCase().startsWith("windows"))
            return createCygwinTerminal(terminalInput, terminalOutput, terminalCharset);
        else
            return createUnixTerminal(terminalInput, terminalOutput, terminalCharset);
    }
    
    /**
     * Creates a {@code Screen} backed by a default terminal
     */
    public static Screen createScreen() {
        return createScreen(createTerminal());
    }
    
    /**
     * Creates a {@code Screen} backed by a supplied {@code Terminal}
     */
    public static Screen createScreen(Terminal terminal) {
        return new Screen(terminal);
    }
    
    /**
     * Creates a {@code GUIScreen} backed by a default terminal
     */
    public static GUIScreen createGUIScreen() {
        return new GUIScreen(createScreen());
    }
    
    /**
     * Creates a {@code GUIScreen} backed by a supplied {@code Terminal}
     */
    public static GUIScreen createGUIScreen(Terminal terminal) {
        return new GUIScreen(createScreen(terminal));
    }
    
    /**
     * Creates a {@code GUIScreen} backed by a supplied {@code Screen}
     */
    public static GUIScreen createGUIScreen(Screen screen) {
        return new GUIScreen(screen);
    }
}
