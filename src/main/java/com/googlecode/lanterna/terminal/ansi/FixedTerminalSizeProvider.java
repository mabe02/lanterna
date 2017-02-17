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
 * Copyright (C) 2010-2017 Martin Berglund
 */

package com.googlecode.lanterna.terminal.ansi;

import com.googlecode.lanterna.TerminalSize;

/**
 * Using this terminal size provider, your terminal will be set to a fixed size and will never receive any resize
 * events. Of course if the physical terminal is resized, in reality it will have a different size, but the application
 * won't know about it. The size reported to the user is always the size attached to this object.
 * @author martin
 */
public class FixedTerminalSizeProvider implements UnixTerminalSizeQuerier {
    private final TerminalSize size;

    /**
     * Creating a {@code FixedTerminalSizeProvider} set to a particular size that it will always report whenever the
     * associated {@code Terminal} interface queries.
     * @param size Size the terminal should be statically initialized to
     */
    public FixedTerminalSizeProvider(TerminalSize size) {
        this.size = size;
    }

    @Override
    public TerminalSize queryTerminalSize() {
        return size;
    }
}
