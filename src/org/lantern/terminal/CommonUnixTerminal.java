/*
 *  Copyright (C) 2010 mabe02
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
 *
 * @author mabe02
 */
public class CommonUnixTerminal extends CommonTerminal
{
    public CommonUnixTerminal(InputStream terminalInput, OutputStream terminalOutput, Charset terminalCharset)
    {
        super(terminalInput, terminalOutput, terminalCharset);
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
        TerminalSize terminalSize = TerminalStatus.querySize();
        if(terminalSize == null) {
            saveCursorPosition();
            moveCursor(5000, 5000);
            reportPosition();
            restoreCursorPosition();
            terminalSize = lastKnownSize;
        }
        if(terminalSize == null)
            return new TerminalSize(80, 24);
        
        return terminalSize;
    }
}
