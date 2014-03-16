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
 * Copyright (C) 2010-2014 Martin
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
     * Adds a KeyDecodingProfile to be used when converting raw user input characters to {@code Key} objects.
     *
     * @see KeyDecodingProfile
     * @param profile
     */
    void addInputProfile(KeyDecodingProfile profile);

    /**
     * Returns the next {@code Key} off the input queue or null if there is no more input events available. Note, this
     * method call is <b>not</b> blocking, it returns null immediately if there is nothing on the input stream.
     * @return Key object which represents a keystroke coming in through the input stream
     * @throws java.io.IOException Propagated error if the underlying stream gave errors
     */
    Key readInput() throws IOException;
}
