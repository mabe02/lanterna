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

package com.googlecode.lanterna.terminal;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import com.googlecode.lanterna.LanternException;
import com.googlecode.lanterna.input.CommonProfile;

/**
 * A common ANSI text terminal implementation
 * @author mabe02
 */
public abstract class CommonTerminal extends AbstractTerminal
{
    CommonTerminal(InputStream terminalInput, OutputStream terminalOutput, Charset terminalCharset)
    {
        super(terminalInput, terminalOutput, terminalCharset);
        addInputProfile(new CommonProfile());
    }

    private void CSI() throws LanternException
    {
        writeToTerminal((byte)0x1b, (byte)'[');
    }

    public void applyBackgroundColor(Color color) throws LanternException
    {
        CSI();
        writeToTerminal((byte)'4', (byte)((color.getIndex() + "").charAt(0)), (byte)'m');
    }

    public void applyForegroundColor(Color color) throws LanternException
    {
        CSI();
        writeToTerminal((byte)'3', (byte)((color.getIndex() + "").charAt(0)), (byte)'m');
    }

    public void applySGR(SGR... options) throws LanternException
    {
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

    public void clearScreen() throws LanternException
    {
        CSI();
        writeToTerminal((byte)'2', (byte)'J');
    }

    public void enterPrivateMode() throws LanternException
    {
        CSI();
        writeToTerminal((byte)'?', (byte)'1', (byte)'0', (byte)'4', (byte)'9', (byte)'h');
        TerminalStatus.setCBreak(true);
        TerminalStatus.setKeyEcho(false);
        TerminalStatus.setMinimumCharacterForRead(1);
    }

    public void exitPrivateMode() throws LanternException
    {
        applySGR(SGR.RESET_ALL);
        CSI();
        writeToTerminal((byte)'?', (byte)'1', (byte)'0', (byte)'4', (byte)'9', (byte)'l');
        TerminalStatus.setCBreak(false);
        TerminalStatus.setKeyEcho(true);
    }

    public void moveCursor(int x, int y) throws LanternException
    {
        CSI();
        writeToTerminal(((y+1) + "").getBytes());
        writeToTerminal((byte)';');
        writeToTerminal(((x+1) + "").getBytes());
        writeToTerminal((byte)'H');
    }

    public void reportPosition() throws LanternException
    {
        CSI();
        writeToTerminal("6n".getBytes());
    }
    
    public void restoreCursorPosition() throws LanternException
    {
        CSI();
        writeToTerminal("u".getBytes());
    }

    public void saveCursorPosition() throws LanternException
    {
        CSI();
        writeToTerminal("s".getBytes());
    }
}
