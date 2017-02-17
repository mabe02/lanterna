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
package com.googlecode.lanterna.terminal;

import java.io.IOException;

/**
 * This interface is for abstracting the creation of your Terminal object. The bundled implementation is 
 * DefaultTerminalFactory, which will use a simple auto-detection mechanism for figuring out which terminal 
 * implementation to create based on characteristics of the system the program is running on.
 * <p>
 * @author martin
 */
@SuppressWarnings("WeakerAccess")
public interface TerminalFactory {
    /**
     * Instantiates a Terminal according to the factory implementation.
     * @return Terminal implementation
     * @throws IOException If there was an I/O error with the underlying input/output system
     */
    Terminal createTerminal() throws IOException;
}
