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
package com.googlecode.lanterna.gui2;

/**
 * A Composite is a Container that contains only one (or zero) component. Normally it is a kind of decorator, like a
 * border, that wraps a single component for visualization purposes.
 * @author Martin
 */
public interface Composite {
    /**
     * Returns the component that this Composite is wrapping
     * @return Component the composite is wrapping
     */
    Component getComponent();
    
    /**
     * Sets the component which is inside this Composite. If you call this method with null, it removes the component
     * wrapped by this Composite.
     * @param component Component to wrap
     */
    void setComponent(Component component);
}
