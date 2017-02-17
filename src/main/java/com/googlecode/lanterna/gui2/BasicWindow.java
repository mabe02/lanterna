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

/**
 * Simple AbstractWindow implementation that you can use as a building block when creating new windows without having
 * to create new classes.
 *
 * @author Martin
 */
public class BasicWindow extends AbstractWindow {

    /**
     * Default constructor, creates a new window with no title
     */
    public BasicWindow() {
        super();
    }

    /**
     * This constructor creates a window with a specific title, that is (probably) going to be displayed in the window
     * decoration
     * @param title Title of the window
     */
    public BasicWindow(String title) {
        super(title);
    }
}
