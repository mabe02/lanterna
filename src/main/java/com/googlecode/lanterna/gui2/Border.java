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
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;

/**
 * Main interface for different border classes, with additional methods to help lanterna figure out the size and offset
 * of components wrapped by borders.
 * @author Martin
 */
public interface Border extends Container, Composite {
    interface BorderRenderer extends ComponentRenderer<Border> {
        /**
         * How large is the offset from the top left corner of the border to the top left corner of the wrapped component?
         * @return Position of the wrapped components top left position, relative to the top left corner of the border
         */
        TerminalPosition getWrappedComponentTopLeftOffset();

        /**
         * Given a total size of the border composite and it's wrapped component, how large would the actual wrapped
         * component be?
         * @param borderSize Size to calculate for, this should be the total size of the border and the inner component
         * @return Size of the inner component if the total size of inner + border is borderSize
         */
        TerminalSize getWrappedComponentSize(TerminalSize borderSize);
    }
}
