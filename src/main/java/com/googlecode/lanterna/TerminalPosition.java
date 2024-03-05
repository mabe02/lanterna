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
 * A 2-d position in 'terminal space'. Please note that the coordinates are 0-indexed, meaning 0x0 is the top left
 * corner of the terminal. This object is immutable so you cannot change it after it has been created. Instead, you
 * can easily create modified 'clones' by using the 'with' methods.
 *
 * @author Martin
 */
public class TerminalPosition implements Comparable<TerminalPosition> {

    /**
     * Constants for less objects memory churn, these are from the top-left corner (column x row)
     */
    public static final TerminalPosition OF_0x0 = new TerminalPosition(0, 0);
    public static final TerminalPosition OF_0x1 = new TerminalPosition(0, 1);
    public static final TerminalPosition OF_1x0 = new TerminalPosition(1, 0);
    public static final TerminalPosition OF_1x1 = new TerminalPosition(1, 1);

    private final int column;
    private final int row;
    /**
     * @return a new TerminalPosition instance with the supplied column and row
     */
    public static final TerminalPosition of(int column, int row) {
        if(column == 0 && row == 0) {
            return OF_0x0;
        } else if(column == 0 && row == 1) {
            return OF_0x1;
        } else if(column == 1 && row == 0) {
            return OF_1x0;
        } else if(column == 1 && row == 1) {
              return OF_1x1;
        }
        return new TerminalPosition(column, row);
    }
		/**
     * Returns a TerminalPositoin with the column and row supplied.
     * If either the column or row supplied is different than this instances column or row, then a new instance is returned.
     * If both column and row are the same as this instance's column and row, then this instance is returned.
     * @return Either this instance, or a new instance if column/row are different than this instance's column/row.
     */
		public TerminalPosition as(int column, int row) {
        return (column != this.column || row != this.row) ? of(column, row) : this;
    }
    /**
     * Returns itself if it is equal to the supplied position, otherwise the supplied position. You can use this if you
     * have a position field which is frequently recalculated but often resolves to the same; it will keep the same
     * object in memory instead of swapping it out every cycle.
     * @param position Position you want to return
     * @return Itself if this position equals the position passed in, otherwise the position passed in
     */
    public TerminalPosition as(TerminalPosition position) {
        return position == null ? this : as(position.column, position.row);
    }
    /**
     * Creates a new TerminalPosition object, which represents a location on the screen. There is no check to verify
     * that the position you specified is within the size of the current terminal and you can specify negative positions
     * as well.
     *
     * @param column Column of the location, or the "x" coordinate, zero indexed (the first column is 0)
     * @param row Row of the location, or the "y" coordinate, zero indexed (the first row is 0)
     */
    public TerminalPosition(int column, int row) {
        this.column = column;
        this.row = row;
    }
    /**
     * Returns the index of the column this position is representing, zero indexed (the first column has index 0).
     * @return Index of the column this position has
     */
    public int getColumn() {
        return column;
    }
    /**
     * Returns the index of the row this position is representing, zero indexed (the first row has index 0)
     * @return Index of the row this position has
     */
    public int getRow() {
        return row;
    }
    /**
     * Creates a new TerminalPosition object representing a position with the same column index as this but with a
     * supplied row index.
     * @param row Index of the row for the new position
     * @return A TerminalPosition object with the same column as this but with a specified row index
     */
    public TerminalPosition withRow(int row) {
        return as(column, row);
    }
    /**
     * Creates a new TerminalPosition object representing a position with the same row index as this but with a
     * supplied column index.
     * @param column Index of the column for the new position
     * @return A TerminalPosition object with the same row as this but with a specified column index
     */
    public TerminalPosition withColumn(int column) {
        return as(column, row);
    }
    /**
     * Creates a new TerminalPosition object representing a position on the same row, but with a column offset by a
     * supplied value. Calling this method with delta 0 will return this, calling it with a positive delta will return
     * a terminal position <i>delta</i> number of columns to the right and for negative numbers the same to the left.
     * @param delta Column offset
     * @return New terminal position based off this one but with an applied offset
     */
    public TerminalPosition withRelativeColumn(int delta) {
        return plus(delta, 0);
    }
    /**
     * Creates a new TerminalPosition object representing a position on the same column, but with a row offset by a
     * supplied value. Calling this method with delta 0 will return this, calling it with a positive delta will return
     * a terminal position <i>delta</i> number of rows to the down and for negative numbers the same up.
     * @param delta Row offset
     * @return New terminal position based off this one but with an applied offset
     */
    public TerminalPosition withRelativeRow(int delta) {
        return plus(0, delta);
    }
    /**
     * Creates a new TerminalPosition object that is 'translated' by an amount of rows and columns specified by another
     * TerminalPosition. Same as calling
     * <code>withRelativeRow(translate.getRow()).withRelativeColumn(translate.getColumn())</code>
     * @param translate How many columns and rows to translate
     * @return New TerminalPosition that is the result of the original with added translation
     */
    public TerminalPosition withRelative(TerminalPosition translate) {
        return plus(translate);
    }
    /**
     * Creates a new TerminalPosition object that is 'translated' by an amount of rows and columns specified by the two
     * parameters. Same as calling
     * <code>withRelativeRow(deltaRow).withRelativeColumn(deltaColumn)</code>
     * @param deltaColumn How many columns to move from the current position in the new TerminalPosition
     * @param deltaRow How many rows to move from the current position in the new TerminalPosition
     * @return New TerminalPosition that is the result of the original position with added translation
     */
    public TerminalPosition withRelative(int deltaColumn, int deltaRow) {
        return plus(deltaColumn, deltaRow);
    }
    /**
     * Returns itself if it is equal to the supplied position, otherwise the supplied position. You can use this if you
     * have a position field which is frequently recalculated but often resolves to the same; it will keep the same
     * object in memory instead of swapping it out every cycle.
     * @param position Position you want to return
     * @return Itself if this position equals the position passed in, otherwise the position passed in
     */
    public TerminalPosition with(TerminalPosition position) {
        return as(position);
    }
    public TerminalPosition plus(TerminalPosition other) {
        return plus(other.column, other.row);
    }
    public TerminalPosition minus(TerminalPosition other) {
        return minus(other.column, other.row);
    }
    public TerminalPosition multiply(TerminalPosition other) {
        return multiply(other.column, other.row);
    }
    public TerminalPosition divide(TerminalPosition denominator) {
        return divide(denominator.column, denominator.row);
    }
    public TerminalPosition plus(int column, int row) {
        return as(this.column + column, this.row + row);
    }
    public TerminalPosition minus(int column, int row) {
        return as(this.column - column, this.row - row);
    }
    public TerminalPosition multiply(int column, int row) {
        return as(this.column * column, this.row * row);
    }
    public TerminalPosition divide(int columnsDenominator, int rowsDenominator) {
        return as(column / columnsDenominator, row / rowsDenominator);
    }
    public TerminalPosition plus(int amount)        { return plus(amount, amount); }
    public TerminalPosition minus(int amount)       { return minus(amount, amount); }
    public TerminalPosition multiply(int amount)    { return multiply(amount, amount); }
    public TerminalPosition divide(int denominator) { return divide(denominator, denominator); }
    
    public TerminalPosition abs() {
        return as(Math.abs(column), Math.abs(row));
    }
    
    public TerminalPosition min(TerminalPosition position) {
        return as(Math.min(column, position.column), Math.min(row, position.row));
    }
    
    public TerminalPosition max(TerminalPosition position) {
        return as(Math.max(column, position.column), Math.max(row, position.row));
    }

    @Override
    public int compareTo(TerminalPosition other) {
        int result = Integer.compare(row, other.row);
        return result != 0 ? result : Integer.compare(column, other.column);
    }

    @Override
    public String toString() {
        return "[" + column + ":" + row + "]";
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + this.row;
        hash = 23 * hash + this.column;
        return hash;
    }

    public boolean equals(int columnIndex, int rowIndex) {
        return column == columnIndex && row == rowIndex;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null
            && obj.getClass() == getClass()
            && ((TerminalPosition) obj).column == column
            && ((TerminalPosition) obj).row == row
            ;
    }
}
