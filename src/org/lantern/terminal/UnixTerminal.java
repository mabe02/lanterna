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

/**
 * A term-info driven terminal with Unix resize capabilities.
 * Warning, this probably won't be too accurate, use CommonUnixTerminal instead.
 * @author mabe02
 */
public class UnixTerminal extends TermInfoTerminal
{

    public UnixTerminal(InputStream terminalInput, OutputStream terminalOutput,
            Charset terminalCharset, TerminalProperties terminalProperties)
    {
        super(terminalInput, terminalOutput, terminalCharset, terminalProperties);
    }

    @Override
    public TerminalSize queryTerminalSize() throws LanternException
    {
        return TerminalStatus.querySize();
    }
}
