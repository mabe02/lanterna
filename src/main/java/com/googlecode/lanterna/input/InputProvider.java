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

package com.googlecode.lanterna.input;



/**
 * Objects implementing this interface can read character streams and transform
 * them into {@code Key} objects which can be read in a FIFO manner.
 * @author Martin
 */
public interface InputProvider {
    /**
     * Adds a KeyMappingProfile to be used when converting raw user input bytes
     * to {@code Key} objects.
     * @see KeyMappingProfile
     * @param profile 
     */
    void addInputProfile(KeyMappingProfile profile);
    
    /**
     * Returns the next {@code Key} off the input queue or null if there is no
     * more input events available
     */
    Key readInput();
}
