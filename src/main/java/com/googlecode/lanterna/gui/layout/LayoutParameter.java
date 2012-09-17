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
package com.googlecode.lanterna.gui.layout;

/**
 * This class is used for giving instructions to different layout managers of how they should
 * position various components. You never instantiate objects of this class directly, instead 
 * you will use static final constants belonging to the LayoutManager you have chosen.
 * @author Martin
 */
public class LayoutParameter {
    
    //Give some meaningful description we can use in toString()
    private final String description;

    LayoutParameter(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
