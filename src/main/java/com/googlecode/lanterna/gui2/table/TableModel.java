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
package com.googlecode.lanterna.gui2.table;

import java.util.*;

/**
 * A {@code TableModel} contains the data model behind a table, here is where all the action cell values and header
 * labels are stored.
 *
 * @author Martin
 */
public class TableModel<V> {

    /**
     * Listener interface for the {@link TableModel} class which can be attached to a {@link TableModel} to be notified
     * of changes to the table model.
     * @param <V> Value type stored in the table
     */
    public interface Listener<V> {
        /**
         * Called when a new row has been added to the model
         * @param model Model the row was added to
         * @param index Index of the new row
         */
        void onRowAdded(TableModel<V> model, int index);
        /**
         * Called when a row has been removed from the model
         * @param model Model the row was removed from
         * @param index Index of the removed row
         * @param oldRow Content of the row that was removed
         */
        void onRowRemoved(TableModel<V> model, int index, List<V> oldRow);
        /**
         * Called when a new column has been added to the model
         * @param model Model the column was added to
         * @param index Index of the new column
         */
        void onColumnAdded(TableModel<V> model, int index);

        /**
         * Called when a column has been removed from the model
         * @param model Model the column was removed from
         * @param index Index of the removed column
         * @param oldHeader Header the removed column had
         * @param oldColumn Values in the removed column
         */
        void onColumnRemoved(TableModel<V> model, int index, String oldHeader, List<V> oldColumn);

        /**
         * Called when an existing cell had its content updated
         * @param model Model that was modified
         * @param row Row index of the modified cell
         * @param column Column index of the modified cell
         * @param oldValue Previous value of the cell
         * @param newValue New value of the cell
         */
        void onCellChanged(TableModel<V> model, int row, int column, V oldValue, V newValue);
    }

    private final List<String> columns;
    private final List<List<V>> rows;
    private final List<Listener<V>> listeners;

    /**
     * Default constructor, creates a new model with same number of columns as labels supplied
     * @param columnLabels Labels for the column headers
     */
    public TableModel(String... columnLabels) {
        this.columns = new ArrayList<String>(Arrays.asList(columnLabels));
        this.rows = new ArrayList<List<V>>();
        this.listeners = new ArrayList<Listener<V>>();
    }

    /**
     * Returns the number of columns in the model
     * @return Number of columns in the model
     */
    public synchronized int getColumnCount() {
        return columns.size();
    }

    /**
     * Returns number of rows in the model
     * @return Number of rows in the model
     */
    public synchronized int getRowCount() {
        return rows.size();
    }

    /**
     * Returns all rows in the model as a list of lists containing the data as elements
     * @return All rows in the model as a list of lists containing the data as elements
     */
    public synchronized List<List<V>> getRows() {
        List<List<V>> copy = new ArrayList<List<V>>();
        for(List<V> row: rows) {
            copy.add(new ArrayList<V>(row));
        }
        return copy;
    }

    /**
     * Returns all column header label as a list of strings
     * @return All column header label as a list of strings
     */
    public synchronized List<String> getColumnLabels() {
        return new ArrayList<String>(columns);
    }

    /**
     * Returns a row from the table as a list of the cell data
     * @param index Index of the row to return
     * @return Row from the table as a list of the cell data
     */
    public synchronized List<V> getRow(int index) {
        return new ArrayList<V>(rows.get(index));
    }

    /**
     * Adds a new row to the table model at the end
     * @param values Data to associate with the new row, mapped column by column in order
     * @return Itself
     */
    public synchronized TableModel<V> addRow(V... values) {
        addRow(Arrays.asList(values));
        return this;
    }

    /**
     * Adds a new row to the table model at the end
     * @param values Data to associate with the new row, mapped column by column in order
     * @return Itself
     */
    public synchronized TableModel<V> addRow(Collection<V> values) {
        insertRow(getRowCount(), values);
        return this;
    }

    /**
     * Inserts a new row to the table model at a particular index
     * @param index Index the new row should have, 0 means the first row and <i>row count</i> will append the row at the
     *              end
     * @param values Data to associate with the new row, mapped column by column in order
     * @return Itself
     */
    public synchronized TableModel<V> insertRow(int index, Collection<V> values) {
        ArrayList<V> list = new ArrayList<V>(values);
        rows.add(index, list);
        for(Listener<V> listener: listeners) {
            listener.onRowAdded(this, index);
        }
        return this;
    }

    /**
     * Removes a row at a particular index from the table model
     * @param index Index of the row to remove
     * @return Itself
     */
    public synchronized TableModel<V> removeRow(int index) {
        List<V> removedRow = rows.remove(index);
        for(Listener<V> listener: listeners) {
            listener.onRowRemoved(this, index, removedRow);
        }
        return this;
    }

    /**
     * Returns the label of a column header
     * @param index Index of the column to retrieve the header label for
     * @return Label of the column selected
     */
    public synchronized String getColumnLabel(int index) {
        return columns.get(index);
    }

    /**
     * Updates the label of a column header
     * @param index Index of the column to update the header label for
     * @param newLabel New label to assign to the column header
     * @return Itself
     */
    public synchronized TableModel<V> setColumnLabel(int index, String newLabel) {
        columns.set(index, newLabel);
        return this;
    }

    /**
     * Adds a new column into the table model as the last column. You can optionally supply values for the existing rows
     * through the {@code newColumnValues}.
     * @param label Label for the header of the new column
     * @param newColumnValues Optional values to assign to the existing rows, where the first element in the array will
     *                        be the value of the first row and so on...
     * @return Itself
     */
    public synchronized TableModel<V> addColumn(String label, V[] newColumnValues) {
        return insertColumn(getColumnCount(), label, newColumnValues);
    }

    /**
     * Adds a new column into the table model at a specified index. You can optionally supply values for the existing
     * rows through the {@code newColumnValues}.
     * @param index Index for the new column
     * @param label Label for the header of the new column
     * @param newColumnValues Optional values to assign to the existing rows, where the first element in the array will
     *                        be the value of the first row and so on...
     * @return Itself
     */
    public synchronized TableModel<V> insertColumn(int index, String label, V[] newColumnValues) {
        columns.add(index, label);
        for(int i = 0; i < rows.size(); i++) {
            List<V> row = rows.get(i);

            //Pad row with null if necessary
            for(int j = row.size(); j < index; j++) {
                row.add(null);
            }

            if(newColumnValues != null && i < newColumnValues.length && newColumnValues[i] != null) {
                row.add(index, newColumnValues[i]);
            }
            else {
                row.add(index, null);
            }
        }

        for(Listener<V> listener: listeners) {
            listener.onColumnAdded(this, index);
        }
        return this;
    }

    /**
     * Removes a column from the table model
     * @param index Index of the column to remove
     * @return Itself
     */
    public synchronized TableModel<V> removeColumn(int index) {
        String removedColumnHeader = columns.remove(index);
        List<V> removedColumn = new ArrayList<V>();
        for(List<V> row : rows) {
            removedColumn.add(row.remove(index));
        }
        for(Listener<V> listener: listeners) {
            listener.onColumnRemoved(this, index, removedColumnHeader, removedColumn);
        }
        return this;
    }

    /**
     * Returns the cell value stored at a specific column/row coordinate.
     * @param columnIndex Column index of the cell
     * @param rowIndex Row index of the cell
     * @return The data value stored in this cell
     */
    public synchronized V getCell(int columnIndex, int rowIndex) {
        if(rowIndex < 0 || columnIndex < 0) {
            throw new IndexOutOfBoundsException("Invalid row or column index: " + rowIndex + " " + columnIndex);
        }
        else if (rowIndex >= getRowCount()) {
            throw new IndexOutOfBoundsException("TableModel has " + getRowCount() + " rows, invalid access at rowIndex " + rowIndex);
        }
        if(columnIndex >= getColumnCount()) {
            throw new IndexOutOfBoundsException("TableModel has " + columnIndex + " columns, invalid access at columnIndex " + columnIndex);
        }
        return rows.get(rowIndex).get(columnIndex);
    }

    /**
     * Updates the call value stored at a specific column/row coordinate.
     * @param columnIndex Column index of the cell
     * @param rowIndex Row index of the cell
     * @param value New value to assign to the cell
     * @return Itself
     */
    public synchronized TableModel<V> setCell(int columnIndex, int rowIndex, V value) {
        getCell(columnIndex, rowIndex);
        List<V> row = rows.get(rowIndex);

        //Pad row with null if necessary
        for(int j = row.size(); j < columnIndex; j++) {
            row.add(null);
        }

        V existingValue = row.get(columnIndex);
        if(existingValue == value) {
            return this;
        }
        row.set(columnIndex, value);
        for(Listener<V> listener: listeners) {
            listener.onCellChanged(this, rowIndex, columnIndex, existingValue, value);
        }
        return this;
    }

    /**
     * Adds a listener to this table model that will be notified whenever the model changes
     * @param listener {@link Listener} to register with this model
     * @return Itself
     */
    public TableModel<V> addListener(Listener<V> listener) {
        listeners.add(listener);
        return this;
    }

    /**
     * Removes a listener from this model so that it will no longer receive any notifications when the model changes
     * @param listener {@link Listener} to deregister from this model
     * @return Itself
     */
    public TableModel<V> removeListener(Listener<V> listener) {
        listeners.remove(listener);
        return this;
    }
}
