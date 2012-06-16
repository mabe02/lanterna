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

import com.googlecode.lanterna.terminal.TerminalSize;

/**
 * Using this terminal size provider, your terminal will be set to a fixed size
 * and will never resize.
 * @author martin
 */
public class FixedTerminalSizeProvider implements UnixTerminalSizeQuerier {
    private final TerminalSize size;

    public FixedTerminalSizeProvider(TerminalSize size) {
        this.size = size;
    }

    public TerminalSize queryTerminalSize() {
        return size;
    }
}
