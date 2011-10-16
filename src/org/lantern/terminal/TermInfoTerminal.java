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

package org.lantern.terminal;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import org.lantern.LanternException;
import org.lantern.input.TermInfoProfile;

/**
 *
 * @author mabe02
 */
public abstract class TermInfoTerminal extends AbstractTerminal
{
    private final TerminalProperties terminalProperties;

    TermInfoTerminal(final InputStream terminalInput, final OutputStream terminalOutput,
            final Charset terminalCharset, final TerminalProperties terminalProperties)
    {
        super(terminalInput, terminalOutput, terminalCharset);
        this.terminalProperties = terminalProperties;
        addInputProfile(new TermInfoProfile(terminalProperties));
    }

    public void applyBackgroundColor(final Color color) throws LanternException
    {
        String backgroundColorString = terminalProperties.getSetBackgroundColorString();
        backgroundColorString = backgroundColorString.replace("%p1%d", color.getIndex() + "");
        writeToTerminal(decodeTerminfoString(backgroundColorString));
    }

    public void applyForegroundColor(final Color color) throws LanternException
    {
        String foregroundColorString = terminalProperties.getSetForegroundColorString();
        foregroundColorString = foregroundColorString.replace("%p1%d", color.getIndex() + "");
        writeToTerminal(decodeTerminfoString(foregroundColorString));
    }

    public void applySGR(final SGR... options) throws LanternException
    {
        for(SGR sgr: options) {
            switch(sgr) {
                case ENTER_BOLD:
                    writeToTerminal(decodeTerminfoString(terminalProperties.getEnterBoldModeString()));
                    break;

                case EXIT_BOLD:
                    //writeToTerminal(decodeTerminfoString(terminalProperties.getExitBoldModeString()));
                    writeToTerminal(decodeTerminfoString("\\E[22m"));
                    break;

                case ENTER_UNDERLINE:
                    writeToTerminal(decodeTerminfoString(terminalProperties.getEnterUnderlineModeString()));
                    break;

                case EXIT_UNDERLINE:
                    writeToTerminal(decodeTerminfoString(terminalProperties.getExitUnderlineModeString()));
                    break;

                case ENTER_REVERSE:
                    writeToTerminal(decodeTerminfoString(terminalProperties.getEnterReverseModeString()));
                    break;

                case EXIT_REVERSE:
                    writeToTerminal(decodeTerminfoString(terminalProperties.getExitReverseModeString()));
                    break;

                case RESET_ALL:
                    writeToTerminal(decodeTerminfoString("\\E0m"));
                    break;
            }
        }
    }

    public void clearScreen() throws LanternException
    {
        writeToTerminal(decodeTerminfoString(terminalProperties.getClearScreenString()));
    }

    public void enterPrivateMode() throws LanternException
    {
        writeToTerminal(decodeTerminfoString(terminalProperties.getEnterPrivateModeString()));
    }

    public void exitPrivateMode() throws LanternException
    {
        writeToTerminal(decodeTerminfoString(terminalProperties.getExitPrivateModeString()));
    }

    public void moveCursor(final int x, final int y) throws LanternException
    {
        String moveString = terminalProperties.getCursorPositionString();
        moveString = moveString.replace("%i%p1%d", (y+1) + "");
        moveString = moveString.replace("%p2%d", (x+1) + "");
        writeToTerminal(decodeTerminfoString(moveString));
    }

    private byte[] decodeTerminfoString(final String terminfoString)
    {
        terminfoString.replace("\\E", new String(new char[] { 0x1b }));
        return terminfoString.getBytes(); //Bytes? Chars? See TermInfoProfile
    }
}
