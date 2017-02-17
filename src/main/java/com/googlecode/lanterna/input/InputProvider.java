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
package com.googlecode.lanterna.input;

import java.io.IOException;

/**
 * Objects implementing this interface can read character streams and transform them into {@code Key} objects which can
 * be read in a FIFO manner.
 *
 * @author Martin
 */
public interface InputProvider {
    /**
     * Returns the next {@code Key} off the input queue or null if there is no more input events available. Note, this
     * method call is <b>not</b> blocking, it returns null immediately if there is nothing on the input stream.
     * @return Key object which represents a keystroke coming in through the input stream
     * @throws java.io.IOException Propagated error if the underlying stream gave errors
     */
    KeyStroke pollInput() throws IOException;

    /**
     * Returns the next {@code Key} off the input queue or blocks until one is available. <b>NOTE:</b> In previous
     * versions of Lanterna, this method was <b>not</b> blocking. From lanterna 3, it is blocking and you can call
     * {@code pollInput()} for the non-blocking version.
     * @return Key object which represents a keystroke coming in through the input stream
     * @throws java.io.IOException Propagated error if the underlying stream gave errors
     */
    KeyStroke readInput() throws IOException;

    //TODO: Add a version of readInput() that takes a timeout specification, in a future version of lanterna
}
