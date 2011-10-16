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

package org.lantern;

import java.awt.GraphicsEnvironment;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import org.lantern.terminal.CommonUnixTerminal;
import org.lantern.terminal.SwingTerminal;
import org.lantern.terminal.Terminal;

/**
 *
 * @author mabe02
 */
public abstract class TerminalFactory
{
    public abstract Terminal createTerminal(InputStream terminalInput, OutputStream terminalOutput, Charset terminalCharset);

    public static class Default extends TerminalFactory
    {
        @Override
        public Terminal createTerminal(InputStream terminalInput, OutputStream terminalOutput, Charset terminalCharset)
        {
            if(GraphicsEnvironment.isHeadless())
                return new Common().createTerminal(terminalInput, terminalOutput, terminalCharset);
            else
                return new Swing().createTerminal(terminalInput, terminalOutput, terminalCharset);
        }
    }

    public static class Common extends TerminalFactory
    {
        @Override
        public Terminal createTerminal(InputStream terminalInput, OutputStream terminalOutput, Charset terminalCharset)
        {
            return new CommonUnixTerminal(terminalInput, terminalOutput, terminalCharset);
        }
    }

    public static class Swing extends TerminalFactory
    {
        @Override
        public Terminal createTerminal(InputStream terminalInput, OutputStream terminalOutput, Charset terminalCharset)
        {
            return new SwingTerminal();
        }
    }
}
