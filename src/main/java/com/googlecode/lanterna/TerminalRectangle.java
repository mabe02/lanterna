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
    
    /**
     * Constants for less objects memory churn, these are from the top-left corner (column x row)
     */
    public static final TerminalRectangle OF_0x0 = new TerminalRectangle(TerminalPosition.of(0, 0), TerminalSize.of(0, 0));
    public static final TerminalRectangle OF_0x1 = new TerminalRectangle(TerminalPosition.of(0, 0), TerminalSize.of(0, 1));
    public static final TerminalRectangle OF_1x0 = new TerminalRectangle(TerminalPosition.of(0, 0), TerminalSize.of(1, 0));
    public static final TerminalRectangle OF_1x1 = new TerminalRectangle(TerminalPosition.of(0, 0), TerminalSize.of(1, 1));
    
    private final TerminalPosition position;
    private final TerminalSize size;
    
    public TerminalPosition position() { return position; }
    public TerminalSize size()         { return size; }
    public int x()                     { return position.getColumn(); }
    public int y()                     { return position.getRow(); }
    public int width()                 { return size.getColumns(); }
    public int height()                { return size.getRows(); }
    
    /**
     * Obtain a TerminalRectangle with the supplied x y position and the supplied width and height.
     *
     * Both width and height must be at least zero (non negative) as checked in TerminalSize.
     * @param x column index of top left corner of the TerminalRectangle
     * @param y row index of the top left corner of the TerminalRectangle
     * @param width number of columns
     * @param height number of rows
     */
    public static final TerminalRectangle of(int x, int y, int width, int height) {
        return of(TerminalPosition.of(x, y), TerminalSize.of(width, height));
    }
    public static final TerminalRectangle of(TerminalPosition position, TerminalSize size) {
        if (OF_0x0.equals(position, size)) { return OF_0x0; }
        if (OF_0x1.equals(position, size)) { return OF_0x1; }
        if (OF_1x0.equals(position, size)) { return OF_1x0; }
        if (OF_1x1.equals(position, size)) { return OF_1x1; }
        
        return new TerminalRectangle(position, size);
    }
    public TerminalRectangle as(int x, int y, int width, int height) {
        return equals(x, y, width, height) ? this : of(x, y, width, height);
    }
    public TerminalRectangle as(TerminalPosition position, TerminalSize size) {
        return equals(position, size) ? this : of(position, size);
    }
    public TerminalRectangle as(TerminalRectangle rectangle) {
        return rectangle == null ? null : as(rectangle.position, rectangle.size);
    }
    protected TerminalRectangle(TerminalPosition position, TerminalSize size) {
        this.position = position;
        this.size = size;
    }
    /**
     * Obtain a TerminalRectangle based on this TerminalRectangle, but with the supplied x position
     * @param x position of the resulting TerminalRectangle
     * @return a TerminalRectangle based on this one, but with the supplied x position
     */
    public TerminalRectangle withX(int x) {
        return TerminalRectangle.of(position.withColumn(x), size);
    }
    /**
     * Obtain a TerminalRectangle based on this TerminalRectangle, but with the supplied y position
     * @param y position of the resulting TerminalRectangle
     * @return a TerminalRectangle based on this one, but with the supplied y position
     */
    public TerminalRectangle withY(int y) {
        return TerminalRectangle.of(position.withRow(y), size);
    }

    /**
     * Obtain a TerminalRectangle based on this TerminalRectangle, but with the supplied width
     * @param Width of the resulting TerminalRectangle, in columns
     * @return a TerminalRectangle based on this one, but with the supplied width
     */
    public TerminalRectangle withWidth(int width) {
        return TerminalRectangle.of(position, size.withColumns(width));
    }
    /**
     * Obtain a TerminalRectangle based on this rect, but with the supplied height
     * @param Height of the resulting rect, in rows
     * @return a TerminalRectangle based on this one, but with the supplied height
     */
    public TerminalRectangle withHeight(int height) {
        return TerminalRectangle.of(position, size.withRows(height));
    }
    /**
     * Obtain a TerminalRectangle based on this TerminalRectangle, but with the supplied position
     * @param position of the resulting TerminalRectangle
     * @return a TerminalRectangle based on this one, but with the supplied position
     */
    public TerminalRectangle withPosition(TerminalPosition position) {
        return TerminalRectangle.of(position, size);
    }
    /**
     * Obtain a TerminalRectangle based on this TerminalRectangle, but with the supplied position
     * @param x position of the resulting TerminalRectangle
     * @param y position of the resulting TerminalRectangle
     * @return a TerminalRectangle based on this one, but with the supplied position
     */
    public TerminalRectangle withPosition(int x, int y) {
        return TerminalRectangle.of(x, y, size.getColumns(), size.getRows());
    }
    /**
     * Obtain a TerminalRectangle based on this TerminalRectangle, but with the supplied size
     * @param size of the resulting TerminalRectangle
     * @return a TerminalRectangle based on this one, but with the supplied size
     */
    public TerminalRectangle withSize(TerminalSize size) {
        return TerminalRectangle.of(position, size);
    }
    /**
     * Obtain a TerminalRectangle based on this TerminalRectangle, but with the supplied size
     * @param columns size of the resulting TerminalRectangle
     * @param rows size of the resulting TerminalRectangle
     * @return a TerminalRectangle based on this one, but with the supplied size
     */
    public TerminalRectangle withSize(int columns, int rows) {
        return TerminalRectangle.of(position.getColumn(), position.getRow(), columns, rows);
    }

    public boolean whenContains(TerminalPosition p, Runnable op) {
        return whenContains(p.getColumn(), p.getRow(), op);
    }
    public boolean whenContains(int x, int y, Runnable op) {
        return whenContains(x(), y(), width(), height(), x, y, op);
    }
    public static final boolean whenContains(int rx, int ry, int rw, int rh, int x, int y, Runnable op) {
        if (rx <= x && x < (rx + rw) && ry <= y && y < (ry + rh)) {
            op.run();
            return true;
        }
        return false;
    }
    @Override
    public String toString() {
        return "{x: " + x() + ", y: " + y() + ", width: " + width() + ", height: " + height() + "}";
    }
    public boolean equals(int x, int y, int width, int height) {
        return position.equals(x, y) && size.equals(width, height);
    }
    public boolean equals(TerminalPosition position, TerminalSize size) {
        return this.position.equals(position) && this.size.equals(size);
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
