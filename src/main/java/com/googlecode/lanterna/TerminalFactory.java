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

package com.googlecode.lanterna;

import com.googlecode.lanterna.terminal.CommonUnixTerminal;
import com.googlecode.lanterna.terminal.SwingTerminal;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalSizeQuerier;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * This factory class will create terminal objects suitable for a specific environment
 * @author mabe02
 */
public abstract class TerminalFactory
{
    public abstract Terminal createTerminal(InputStream terminalInput, OutputStream terminalOutput, Charset terminalCharset);

    /**
     * This factory implementation returns a Swing terminal if GraphicsEnvironment.isHeadless() returns false,
     * otherwise a common Unix text terminal.
     */
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

    /**
     * This factory implementation returns a common unix text terminal.
     */
    public static class Common extends TerminalFactory
    {
        private TerminalSizeQuerier sizeQuerier;

        public Common() {
            sizeQuerier = null;
        }
        
        @Override
        public Terminal createTerminal(InputStream terminalInput, OutputStream terminalOutput, Charset terminalCharset)
        {
            return new CommonUnixTerminal(terminalInput, terminalOutput, terminalCharset, sizeQuerier);
        }

        public void setSizeQuerier(TerminalSizeQuerier sizeQuerier) {
            this.sizeQuerier = sizeQuerier;
        }
    }

    /**
     * This factory implementation returns a Swing-based text terminal emulator
     */
    public static class Swing extends TerminalFactory
    {
        @Override
        public Terminal createTerminal(InputStream terminalInput, OutputStream terminalOutput, Charset terminalCharset)
        {
            return new SwingTerminal();
        }
    }
}
