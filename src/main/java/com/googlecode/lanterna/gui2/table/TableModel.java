package com.googlecode.lanterna.gui2.table;

import java.util.*;

/**
 * Created by martin on 22/08/15.
 */
public class TableModel<V> {
    private final List<String> columns;
    private final List<List<V>> rows;

    public TableModel(String... columnLabels) {
        this.columns = new ArrayList<String>(Arrays.asList(columnLabels));
        this.rows = new ArrayList<List<V>>();
    }

    public synchronized int getColumnCount() {
        return columns.size();
    }

    public synchronized int getRowCount() {
        return rows.size();
    }

    public synchronized List<List<V>> getRows() {
        List<List<V>> copy = new ArrayList<List<V>>();
        for(List<V> row: rows) {
            copy.add(new ArrayList<V>(row));
        }
        return copy;
    }

    public List<String> getColumnLabels() {
        return new ArrayList<String>(columns);
    }

    public synchronized List<V> getRow(int index) {
        return Collections.unmodifiableList(new ArrayList<V>(rows.get(index)));
    }

    public synchronized TableModel<V> addRow(V... values) {
        addRow(Arrays.asList(values));
        return this;
    }

    public synchronized TableModel<V> addRow(Collection<V> values) {
        insertRow(getRowCount(), values);
        return this;
    }

    public synchronized TableModel<V> insertRow(int index, Collection<V> components) {
        ArrayList<V> list = new ArrayList<V>(components);
        rows.add(index, list);
        return this;
    }

    public synchronized TableModel<V> removeRow(int index) {
        rows.remove(index);
        return this;
    }

    public synchronized String getColumnLabel(int index) {
        return columns.get(index);
    }

    public synchronized TableModel<V> addColumn(String label, V[] newColumnValues) {
        return insertColumn(getColumnCount(), label, newColumnValues);
    }

    public synchronized TableModel<V> insertColumn(int index, String label, V[] newColumnValues) {
        columns.add(index, label);
        for(int i = 0; i < rows.size(); i++) {
            List<V> row = rows.get(i);

            //Pad row with null if necessary
            for(int j = row.size(); j < index; j++) {
                row.add(null);
            }

            if(i < newColumnValues.length && newColumnValues[i] != null) {
                row.add(index, newColumnValues[i]);
            }
            else {
                row.add(index, null);
            }
        }
        return this;
    }

    public synchronized TableModel<V> setColumnLabel(int index, String newLabel) {
        columns.set(index, newLabel);
        return this;
    }

    public synchronized TableModel<V> removeColumn(int index) {
        columns.remove(index);
        for(List<V> row : rows) {
            row.remove(index);
        }
        return this;
    }

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
        return this;
    }
}
