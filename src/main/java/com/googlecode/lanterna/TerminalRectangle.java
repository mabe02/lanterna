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
public class TerminalRectangle {
    
    // one of the benefits of immutable: ease of usage
    public final TerminalPosition position;
    public final TerminalSize size;
    public final int x;
    public final int y;
    public final int width;
    public final int height;
    
    public final int xAndWidth;
    public final int yAndHeight;
    
    /**
     * Creates a new terminal rect representation at the supplied x y position with the supplied width and height.
     *
     * Both width and height must be at least zero (non negative) as checked in TerminalSize.
     *
     * @param width number of columns
     * @param height number of rows
     */
    public TerminalRectangle(int x, int y, int width, int height) {
        position = new TerminalPosition(x, y);
        size = new TerminalSize(width, height);
        
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.xAndWidth = x + width;
        this.yAndHeight = y + height;
    }
    
    /**
     * @return Returns the width of this rect, in number of columns
     */
    public int getColumns() {
        return width;
    }
    
    /**
     * @return Returns the height of this rect representation, in number of rows
     */
    public int getRows() {
        return height;
    }
    
    /**
     * Creates a new rect based on this rect, but with a different width
     * @param columns Width of the new rect, in columns
     * @return New rect based on this one, but with a new width
     */
    public TerminalRectangle withColumns(int columns) {
        return new TerminalRectangle(x, y, columns, height);
    }
    
    /**
     * Creates a new rect based on this rect, but with a different height
     * @param rows Height of the new rect, in rows
     * @return New rect based on this one, but with a new height
     */
    public TerminalRectangle withRows(int rows) {
        return new TerminalRectangle(x, y, width, rows);
    }

    public boolean whenContains(TerminalPosition p, Runnable op) {
        return whenContains(p.getColumn(), p.getRow(), op);
    }
    public boolean whenContains(int x, int y, Runnable op) {
        if (this.x <= x && x < this.xAndWidth && this.y <= y && y < this.yAndHeight) {
            op.run();
            return true;
        }
        return false;
    }


    @Override
    public String toString() {
        return "{x: " + x + ", y: " + y + ", width: " + width + ", height: " + height + "}";
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null
            && obj.getClass() == getClass()
            && Objects.equals(position, ((TerminalRectangle)obj).position)
            && Objects.equals(size, ((TerminalRectangle)obj).size);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, size);
    }
}
