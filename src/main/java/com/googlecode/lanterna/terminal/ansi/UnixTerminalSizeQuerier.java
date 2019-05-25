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
 * Copyright (C) 2010-2019 Martin Berglund
 */
package com.googlecode.lanterna.terminal.ansi;

import com.googlecode.lanterna.TerminalSize;

/**
 * This class allows you to override by what means Lanterna detects the size of
 * the terminal. You can implement this interface and pass it to the 
 * UnixTerminal constructor in order to use it.
 * @author martin
 */
@SuppressWarnings("WeakerAccess")
public interface UnixTerminalSizeQuerier {
    /**
     * Checks what the size of the terminal is, measured in number of rows and columns. The implementer of this
     * interface is expected to know which terminal we are querying for and have all it needs to figure out the size.
     * One way of implementing this could be to read of an external value or variable or calling IPCs or just return
     * a static size at all times.
     * @return Size of the terminal at this point in time
     */
    TerminalSize queryTerminalSize();
}
