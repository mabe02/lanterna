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

/**
 * Terminal dimensions in 2-d space, measured in number of rows and columns. This class is immutable and cannot change
 * its internal state after creation.
 *
 * @author Martin
 */
public class TerminalSize implements Comparable<TerminalSize> {

    public static final TerminalSize OF_0x0 = new TerminalSize(0, 0);
    public static final TerminalSize OF_0x1 = new TerminalSize(0, 1);
    public static final TerminalSize OF_1x0 = new TerminalSize(1, 0);
    public static final TerminalSize OF_1x1 = new TerminalSize(1, 1);

    private final int columns;
    private final int rows;
    
    public int width() { return columns; }
    public int height() { return rows; }

    public static final TerminalSize of(int columns, int rows) {
        if (OF_0x0.equals(columns, rows)) { return OF_0x0; }
        if (OF_0x1.equals(columns, rows)) { return OF_0x1; }
        if (OF_1x0.equals(columns, rows)) { return OF_1x0; }
        if (OF_1x1.equals(columns, rows)) { return OF_1x1; }
        
        return new TerminalSize(columns, rows);
    }
    public TerminalSize as(int columns, int rows) {
        return equals(columns, rows) ? this : of(columns, rows);
    }
    public TerminalSize as(TerminalSize size) {
        return size == null ? null : as(size.columns, size.rows);
    }
    /**
     * Creates a new terminal size representation with a given width (columns) and height (rows)
     * @param columns Width as number of columns
     * @param rows Height as number or rows
     */
    public TerminalSize(int columns, int rows) {
        if (columns < 0 || rows < 0) {
            throw new IllegalArgumentException("TerminalSize dimensions cannot be less than 0: [columns: " + columns + ", rows: " + rows + "]");
        }
        this.columns = columns;
        this.rows = rows;
    }
    /**
     * @return Width, the number of columns of this TerminalSize
     */
    public int getColumns() {
        return columns;
    }
    /**
     * @return Height, the number of rows of this TerminalSize
     */
    public int getRows() {
        return rows;
    }
    /**
     * Obtain a TerminalSize with the supplied number of columns and this instance's number of rows
     * @param columns, the number of columns, or Width, of the resulting TerminalSize
     * @return a TerminalSize with the supplied number of columns and this instance's number of rows
     */
    public TerminalSize withColumns(int columns) {
        return as(columns, rows);
    }
    /**
     * Obtain a TerminalSize with this instance's number of columns and the supplied number of rows
     * @param rows, the number or rows, or Height, of the resulting TerminalSize
     * @return a TerminalSize with this instance's number of columns and the supplied number of rows
     */
    public TerminalSize withRows(int rows) {
        return as(columns, rows);
    }
    /**
     * Obtain a TerminalSize with the same number of rows, but with a columns size offset by a
     * supplied value. Calling this method with delta 0 will return this, calling it with a positive delta will return
     * a TerminalSize <i>delta</i> number of columns wider and for negative numbers shorter.
     * @param delta Column offset
     * @return a TerminalSize based off this one but with an applied translation
     */
    public TerminalSize withRelativeColumns(int delta) {
        // Prevent going below 0 (which would throw an exception)
        return withColumns(Math.max(0, columns + delta));
    }
    /**
     * Obtain a TerminalSize with the same number of columns, but with a row size offset by a
     * supplied value. Calling this method with delta 0 will return this, calling it with a positive delta will return
     * a TerminalSize <i>delta</i> number of rows longer and for negative numbers shorter.
     * @param delta Row offset
     * @return a TerminalSize resulting from translating this instance by the supplied delta rows
     */
    public TerminalSize withRelativeRows(int delta) {
        // Prevent going below 0 (which would throw an exception)
        return withRows(Math.max(0, rows + delta));
    }
    /**
     * Obtain a TerminalSize based on this object's size but with a delta applied.
     * This is the same as calling
     * <code>withRelativeColumns(delta.getColumns()).withRelativeRows(delta.getRows())</code>
     * @param delta Column and row offset
     * @return a TerminalSize based off this one but with an applied resize
     */
    public TerminalSize withRelative(TerminalSize delta) {
        return plus(delta);
    }
    /**
     * Obtain a TerminalSize based on this object's size but with a delta applied.
     * This is the same as calling
     * <code>withRelativeColumns(deltaColumns).withRelativeRows(deltaRows)</code>
     * @param deltaColumns How many extra columns the resulting TerminalSize will have (negative delta values are allowed)
     * @param deltaRows How many extra rows the resulting TerminalSize will have (negative delta values are allowed)
     * @return a TerminalSize based off this one but with an applied resize
     */
    public TerminalSize withRelative(int deltaColumns, int deltaRows) {
        return plus(deltaColumns, deltaRows);
    }
    /**
     * Obtain a TerminalSize having the max columns and max rows of this instance and the supplied one.
     * Example: calling 3x5 on a 5x3 will return 5x5.
     * @param other TerminalSize to compare with
     * @return a TerminalSize that combines the maximum width and maximum height between the two
     */
    public TerminalSize max(TerminalSize other) {
        return as(Math.max(columns, other.columns), Math.max(rows, other.rows));
    }
    /**
     * Obtain a TerminalSize having the min columns and min rows of this instance and the supplied one.
     * Example: calling 3x5 on a 5x3 will return 3x3.
     * @param other TerminalSize to compare with
     * @return a TerminalSize that combines the minimum width and minimum height between the two
     */
    public TerminalSize min(TerminalSize other) {
        return as(Math.min(columns, other.columns), Math.min(rows, other.rows));
    }

    public TerminalSize plus(TerminalSize other) {
        return plus(other.columns, other.rows);
    }
    public TerminalSize minus(TerminalSize other) {
        return minus(other.columns, other.rows);
    }
    public TerminalSize multiply(TerminalSize other) {
        return multiply(other.columns, other.rows);
    }
    public TerminalSize divide(TerminalSize denominator) {
        return divide(denominator.columns, denominator.rows);
    }
    public TerminalSize plus(int columns, int rows) {
        return as(this.columns + columns, this.rows + rows);
    }
    public TerminalSize minus(int columns, int rows) {
        return as(this.columns - columns, this.rows - rows);
    }
    public TerminalSize multiply(int columns, int rows) {
        return as(this.columns * columns, this.rows * rows);
    }
    public TerminalSize divide(int columnsDenominator, int rowsDenominator) {
        return as(columns / columnsDenominator, rows / rowsDenominator);
    }
    public TerminalSize plus(int amount)        { return plus(amount, amount); }
    public TerminalSize minus(int amount)       { return minus(amount, amount); }
    public TerminalSize multiply(int amount)    { return multiply(amount, amount); }
    public TerminalSize divide(int denominator) { return divide(denominator, denominator); }

    /**
     * Returns itself if it is equal to the supplied size, otherwise the supplied size. You can use this if you have a
     * size field which is frequently recalculated but often resolves to the same size; it will keep the same object
     * in memory instead of swapping it out every cycle.
     * @param size Size you want to return
     * @return Itself if this size equals the size passed in, otherwise the size passed in
     */
    public TerminalSize with(TerminalSize size) {
        return as(size);
    }
    
    @Override
    public int compareTo(TerminalSize other) {
        return Integer.compare(columns * rows, other.columns * other.rows);
    }

    public boolean equals(int columns, int rows) {
        return this.columns == columns && this.rows == rows;
    }

    @Override
    public String toString() {
        return "{" + columns + "x" + rows + "}";
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null
            && obj.getClass() == getClass()
            && ((TerminalSize) obj).columns == columns
            && ((TerminalSize) obj).rows == rows
            ;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + this.columns;
        hash = 53 * hash + this.rows;
        return hash;
    }
}
