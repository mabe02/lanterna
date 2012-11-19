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

package com.googlecode.lanterna.terminal.text;

import com.googlecode.lanterna.input.CommonProfile;
import com.googlecode.lanterna.terminal.TerminalSize;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * A common ANSI text terminal implementation
 * @see <a href="http://en.wikipedia.org/wiki/ANSI_escape_code">Wikipedia</a>
 * @author Martin
 */
public abstract class ANSITerminal extends StreamBasedTerminal
{
    ANSITerminal(InputStream terminalInput, OutputStream terminalOutput, Charset terminalCharset)
    {
        super(terminalInput, terminalOutput, terminalCharset);
        addInputProfile(new CommonProfile());
    }

    private void CSI()
    {
        writeToTerminal((byte)0x1b, (byte)'[');
    }
    
    @Deprecated
    @Override
    public TerminalSize queryTerminalSize()
    {        
        synchronized(writerMutex) {
            saveCursorPosition();
            moveCursor(5000, 5000);
            reportPosition();
            restoreCursorPosition();
        }
        return getLastKnownSize();
    }

    @Override
    public TerminalSize getTerminalSize() {
        queryTerminalSize();
        return waitForTerminalSizeReport(1000); //Wait 1 second for the terminal size report to come, is this reasonable?
    }

    @Override
    public void applyBackgroundColor(Color color)
    {
        synchronized(writerMutex) {
            CSI();
            writeToTerminal((byte)'4', (byte)((color.getIndex() + "").charAt(0)), (byte)'m');
        }
    }

    @Override
    public void applyBackgroundColor(int r, int g, int b) {
        if(r < 0 || r > 255)
            throw new IllegalArgumentException("applyForegroundColor: r is outside of valid range (0-255)");
        if(g < 0 || g > 255)
            throw new IllegalArgumentException("applyForegroundColor: g is outside of valid range (0-255)");
        if(b < 0 || b > 255)
            throw new IllegalArgumentException("applyForegroundColor: b is outside of valid range (0-255)");
        
        synchronized(writerMutex) {
            CSI();
            String asString = "48;2;" + r + ";" + g + ";" + b + "m";
            for(int i = 0; i < asString.length(); i++)
                writeToTerminal((byte)asString.charAt(i));
        }
    }

    @Override
    public void applyBackgroundColor(int index) {
        if(index < 0 || index > 255)
            throw new IllegalArgumentException("applyBackgroundColor: index is outside of valid range (0-255)");
        
        synchronized(writerMutex) {
            CSI();
            String asString = "48;5;" + index + "m";
            for(int i = 0; i < asString.length(); i++)
                writeToTerminal((byte)asString.charAt(i));
        }
    }

    @Override
    public void applyForegroundColor(Color color)
    {
        synchronized(writerMutex) {
            CSI();
            writeToTerminal((byte)'3', (byte)((color.getIndex() + "").charAt(0)), (byte)'m');
        }
    }

    @Override
    public void applyForegroundColor(int r, int g, int b) {
        if(r < 0 || r > 255)
            throw new IllegalArgumentException("applyForegroundColor: r is outside of valid range (0-255)");
        if(g < 0 || g > 255)
            throw new IllegalArgumentException("applyForegroundColor: g is outside of valid range (0-255)");
        if(b < 0 || b > 255)
            throw new IllegalArgumentException("applyForegroundColor: b is outside of valid range (0-255)");
        
        synchronized(writerMutex) {
            CSI();
            String asString = "38;2;" + r + ";" + g + ";" + b + "m";
            for(int i = 0; i < asString.length(); i++)
                writeToTerminal((byte)asString.charAt(i));
        }
    }

    @Override
    public void applyForegroundColor(int index) {
        if(index < 0 || index > 255)
            throw new IllegalArgumentException("applyForegroundColor: index is outside of valid range (0-255)");
        
        synchronized(writerMutex) {
            CSI();
            String asString = "38;5;" + index + "m";
            for(int i = 0; i < asString.length(); i++)
                writeToTerminal((byte)asString.charAt(i));
        }
    }

    @Override
    public void applySGR(SGR... options)
    {
        synchronized(writerMutex) {
            CSI();
            int index = 0;
            for(SGR sgr: options) {
                switch(sgr) {
                    case ENTER_BOLD:
                        writeToTerminal((byte)'1');
                        break;
                    case ENTER_REVERSE:
                        writeToTerminal((byte)'7');
                        break;
                    case ENTER_UNDERLINE:
                        writeToTerminal((byte)'4');
                        break;
                    case EXIT_BOLD:
                        writeToTerminal((byte)'2', (byte)'2');
                        break;
                    case EXIT_REVERSE:
                        writeToTerminal((byte)'2', (byte)'7');
                        break;
                    case EXIT_UNDERLINE:
                        writeToTerminal((byte)'2', (byte)'4');
                        break;
                    case ENTER_BLINK:
                        writeToTerminal((byte)'5');
                        break;
                    case RESET_ALL:
                        writeToTerminal((byte)'0');
                        break;
                }
                if(index++ < options.length - 1)
                    writeToTerminal((byte)';');
            }
            writeToTerminal((byte)'m');
        }
    }

    @Override
    public void clearScreen()
    {
        synchronized(writerMutex) {
            CSI();
            writeToTerminal((byte)'2', (byte)'J');
        }
    }

    @Override
    public void enterPrivateMode()
    {
        synchronized(writerMutex) {
            CSI();
            writeToTerminal((byte)'?', (byte)'1', (byte)'0', (byte)'4', (byte)'9', (byte)'h');
        }
    }

    @Override
    public void exitPrivateMode()
    {
        synchronized(writerMutex) {
            applySGR(SGR.RESET_ALL);
            setCursorVisible(true);
            CSI();
            writeToTerminal((byte)'?', (byte)'1', (byte)'0', (byte)'4', (byte)'9', (byte)'l');
        }
    }
    
    /**
     * Enables or disables keyboard echo, meaning the immediate output of the
     * characters you type on your keyboard. If your users are going to interact
     * with this application through the keyboard, you probably want to disable
     * echo mode.
     * @param echoOn true if keyboard input will immediately echo, false if it's hidden
     * @throws LanternaException
     */
    public abstract void setEcho(boolean echoOn);

    /**
     * Enabling cbreak mode will allow you to read user input immediately as the
     * user enters the characters, as opposed to reading the data in lines as
     * the user presses enter. If you want your program to respond to user input
     * by the keyboard, you probably want to enable cbreak mode.
     * @see <a href="http://en.wikipedia.org/wiki/POSIX_terminal_interface">POSIX terminal interface</a>
     * @param cbreakOn
     * @throws LanternaException
     */
    public abstract void setCBreak(boolean cbreakOn);

    @Override
    public void moveCursor(int x, int y)
    {
        synchronized(writerMutex) {
            CSI();
            writeToTerminal(((y+1) + "").getBytes());
            writeToTerminal((byte)';');
            writeToTerminal(((x+1) + "").getBytes());
            writeToTerminal((byte)'H');
        }
    }

    @Override
    public void setCursorVisible(boolean visible) {
        synchronized(writerMutex) {
            CSI();
            writeToTerminal((byte)'?');
            writeToTerminal((byte)'2');
            writeToTerminal((byte)'5');
            if(visible)
                writeToTerminal((byte)'h');
            else
                writeToTerminal((byte)'l');
        }
    }
    
    /**
     * Synchronize with writerMutex externally!!!
     */
    protected void reportPosition()
    {
        CSI();
        writeToTerminal("6n".getBytes());
    }

    /**
     * Synchronize with writerMutex externally!!!
     */
    protected void restoreCursorPosition()
    {
        CSI();
        writeToTerminal("u".getBytes());
    }

    /**
     * Synchronize with writerMutex externally!!!
     */
    protected void saveCursorPosition()
    {
        CSI();
        writeToTerminal("s".getBytes());
    }
}
