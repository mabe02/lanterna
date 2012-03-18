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
import org.lantern.input.GnomeTerminalProfile;
import org.lantern.input.PuttyProfile;
import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * A common ANSI terminal extention with support for Unix resize signals
 * @author mabe02
 */
public class CommonUnixTerminal extends CommonTerminal
{
    private final TerminalSizeQuerier terminalSizeQuerier;
            
    public CommonUnixTerminal(
            InputStream terminalInput, 
            OutputStream terminalOutput, 
            Charset terminalCharset)
    {
        this(terminalInput, terminalOutput, terminalCharset, null);
    }
            
    public CommonUnixTerminal(
            InputStream terminalInput, 
            OutputStream terminalOutput, 
            Charset terminalCharset,
            TerminalSizeQuerier customSizeQuerier)
    {
        super(terminalInput, terminalOutput, terminalCharset);
        this.terminalSizeQuerier = customSizeQuerier;
        addInputProfile(new GnomeTerminalProfile());
        addInputProfile(new PuttyProfile());

        Signal.handle(new Signal("WINCH"), new SignalHandler() {
            public void handle(Signal signal)
            {
                try {
                    onResized();
                }
                catch(LanternException e) {
                }
            }
        });
    }

    public TerminalSize queryTerminalSize() throws LanternException
    {
        if(terminalSizeQuerier != null)
            return terminalSizeQuerier.queryTerminalSize();
        
        saveCursorPosition();
        moveCursor(5000, 5000);
        reportPosition();
        restoreCursorPosition();        
        return lastKnownSize;
    }
}
