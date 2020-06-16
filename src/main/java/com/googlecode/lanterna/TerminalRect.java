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
package com.googlecode.lanterna;

import java.util.Objects;

/**
 * This class is immutable and cannot change its internal state after creation.
 *
 * @author ginkoblongata
 */
public class TerminalRect {
    
    private final TerminalPosition position;
    private final TerminalSize size;
    
    /**
     * Creates a new terminal rect representation with a given width (columns) and height (rows)
     * @param columns Width, in number of columns
     * @param rows Height, in number of columns
     */
    public TerminalRect(int x, int y, int w, int h) {
        if (w < 0) {
            throw new IllegalArgumentException("TerminalSize.columns cannot be less than 0!");
        }
        if (h < 0) {
            throw new IllegalArgumentException("TerminalSize.rows cannot be less than 0!");
        }
        position = new TerminalPosition(x, y);
        size = new TerminalSize(w, h);
    }
    
    public TerminalPosition getPosition() {
        return position;
    }
    
    public int getX() {
        return position.getColumn();
    }
    public int getY() {
        return position.getRow();
    }
    public int getWidth() {
        return size.getColumns();
    }
    public int getHeight() {
        return size.getRows();
    }
    
    public TerminalSize getSize() {
        return size;
    }
    
    /**
     * @return Returns the width of this rect, in number of columns
     */
    public int getColumns() {
        return getSize().getColumns();
    }

    
    /**
     * Creates a new rect based on this rect, but with a different width
     * @param columns Width of the new rect, in columns
     * @return New rect based on this one, but with a new width
     */
    public TerminalRect withColumns(int columns) {
        return new TerminalRect(position.getColumn(), position.getRow(), columns, size.getRows());
    }

    /**
     * @return Returns the height of this rect representation, in number of rows
     */
    public int getRows() {
        return getSize().getRows();
    }

    /**
     * Creates a new rect based on this rect, but with a different height
     * @param rows Height of the new rect, in rows
     * @return New rect based on this one, but with a new height
     */
    public TerminalRect withRows(int rows) {
        return new TerminalRect(position.getColumn(), position.getRow(), size.getColumns(), rows);
    }

    @Override
    public String toString() {
        return "{x: " + position.getColumn() + ", y: " + position.getRow() + ", w: " + size.getColumns() + ", h: " + size.getRows() + "}";
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null
            && obj.getClass() == getClass()
            && Objects.equals(position, ((TerminalRect)obj).position)
            && Objects.equals(size, ((TerminalRect)obj).size);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, size);
    }
}
