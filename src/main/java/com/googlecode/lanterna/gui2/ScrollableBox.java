/*
 * This file is part of lanterna (https://github.com/mabe02/lanterna).
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
 * Copyright (C) 2010-2020 Martin Berglund
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.Theme;
import com.googlecode.lanterna.graphics.ThemeDefinition;

/**
 * 
 * @author ginkoblongata
 */
public interface ScrollableBox extends Component {

    void setIsWithinScrollPanel(boolean isWithinScrollPanel);
    
    default boolean isWithinScrollPanel() {
        return false;
    }
    
    default boolean isVerticalScrollCapable() {
        return false;
    }
    
    default boolean isHorizontalScrollCapable() {
        return false;
    }
    
    default boolean isVerticalScrollVisible() {
        return false;
    }
    
    default boolean isHorizontalScrollVisible() {
        return false;
    }
    
    default int getVerticalScrollMaximum() {
        return 0;
    }
    
    default int getHorizontalScrollMaximum() {
        return 0;
    }
    
    default int getVerticalScrollPosition() {
        return 0;
    }
    
    default int getHorizontalScrollPosition() {
        return 0;
    }
}
