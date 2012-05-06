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
 * Copyright (C) 2010-2012 mabe02
 */

package com.googlecode.lanterna.terminal.text;

import com.googlecode.lanterna.LanternaException;
import com.googlecode.lanterna.input.CommonProfile;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * A common ANSI text terminal implementation
 * @see <a href="http://en.wikipedia.org/wiki/ANSI_escape_code">Wikipedia</a>
 * @author mabe02
 */
public abstract class ANSITerminal extends StreamBasedTerminal
{
    ANSITerminal(InputStream terminalInput, OutputStream terminalOutput, Charset terminalCharset)
    {
        super(terminalInput, terminalOutput, terminalCharset);
        addInputProfile(new CommonProfile());
    }

    private void CSI() throws LanternaException
    {
        writeToTerminal((byte)0x1b, (byte)'[');
    }

    public void applyBackgroundColor(Color color) throws LanternaException
    {
        synchronized(writerMutex) {
            CSI();
            writeToTerminal((byte)'4', (byte)((color.getIndex() + "").charAt(0)), (byte)'m');
        }
    }

    public void applyForegroundColor(Color color) throws LanternaException
    {
        synchronized(writerMutex) {
            CSI();
            writeToTerminal((byte)'3', (byte)((color.getIndex() + "").charAt(0)), (byte)'m');
        }
    }

    public void applySGR(SGR... options) throws LanternaException
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

    public void clearScreen() throws LanternaException
    {
        synchronized(writerMutex) {
            CSI();
            writeToTerminal((byte)'2', (byte)'J');
        }
    }

    public void enterPrivateMode() throws LanternaException
    {
        synchronized(writerMutex) {
            CSI();
            writeToTerminal((byte)'?', (byte)'1', (byte)'0', (byte)'4', (byte)'9', (byte)'h');
        }
    }

    public void exitPrivateMode() throws LanternaException
    {
        synchronized(writerMutex) {
            applySGR(SGR.RESET_ALL);
            CSI();
            writeToTerminal((byte)'?', (byte)'1', (byte)'0', (byte)'4', (byte)'9', (byte)'l');
        }
    }

    public void moveCursor(int x, int y) throws LanternaException
    {
        synchronized(writerMutex) {
            CSI();
            writeToTerminal(((y+1) + "").getBytes());
            writeToTerminal((byte)';');
            writeToTerminal(((x+1) + "").getBytes());
            writeToTerminal((byte)'H');
        }
    }

    /**
     * Synchronize with writerMutex externally!!!
     */
    protected void reportPosition() throws LanternaException
    {
        CSI();
        writeToTerminal("6n".getBytes());
    }

    /**
     * Synchronize with writerMutex externally!!!
     */
    protected void restoreCursorPosition() throws LanternaException
    {
        CSI();
        writeToTerminal("u".getBytes());
    }

    /**
     * Synchronize with writerMutex externally!!!
     */
    protected void saveCursorPosition() throws LanternaException
    {
        CSI();
        writeToTerminal("s".getBytes());
    }
}
