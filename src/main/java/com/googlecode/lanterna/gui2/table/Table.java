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
package com.googlecode.lanterna.gui2.table;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.input.KeyStroke;

import java.util.List;

/**
 * The table class is an interactable component that displays a grid of cells containing data along with a header of
 * labels. It supports scrolling when the number of rows and/or columns gets too large to fit and also supports
 * user selection which is either row-based or cell-based. User will move the current selection by using the arrow keys
 * on the keyboard.
 * @param <V> Type of data to store in the table cells, presented through {@code toString()}
 * @author Martin
 */
public class Table<V> extends AbstractInteractableComponent<Table<V>> {
    private TableModel<V> tableModel;
    private TableModel.Listener<V> tableModelListener;  // Used to invalidate the table whenever the model changes
    private TableHeaderRenderer<V> tableHeaderRenderer;
    private TableCellRenderer<V> tableCellRenderer;
    private Runnable selectAction;
    private boolean cellSelection;
    private int visibleRows;
    private int visibleColumns;
    private int selectedRow;
    private int selectedColumn;
    private boolean escapeByArrowKey;

    /**
     * Creates a new {@code Table} with the number of columns as specified by the array of labels
     * @param columnLabels Creates one column per label in the array, must be more than one
     */
    public Table(String... columnLabels) {
        if(columnLabels.length == 0) {
            throw new IllegalArgumentException("Table needs at least one column");
        }
        this.tableHeaderRenderer = new DefaultTableHeaderRenderer<V>();
        this.tableCellRenderer = new DefaultTableCellRenderer<V>();
        this.tableModel = new TableModel<V>(columnLabels);
        this.selectAction = null;
        this.visibleColumns = 0;
        this.visibleRows = 0;
        this.cellSelection = false;
        this.selectedRow = 0;
        this.selectedColumn = -1;
        this.escapeByArrowKey = true;

        this.tableModelListener = new TableModel.Listener<V>() {
            @Override
            public void onRowAdded(TableModel<V> model, int index) {
                invalidate();
            }

            @Override
            public void onRowRemoved(TableModel<V> model, int index, List<V> oldRow) {
                invalidate();
            }

            @Override
            public void onColumnAdded(TableModel<V> model, int index) {
                invalidate();
            }

            @Override
            public void onColumnRemoved(TableModel<V> model, int index, String oldHeader, List<V> oldColumn) {
                invalidate();
            }

            @Override
            public void onCellChanged(TableModel<V> model, int row, int column, V oldValue, V newValue) {
                invalidate();
            }
        };
        this.tableModel.addListener(tableModelListener);
    }

    /**
     * Returns the underlying table model
     * @return Underlying table model
     */
    public TableModel<V> getTableModel() {
        return tableModel;
    }

    /**
     * Updates the table with a new table model, effectively replacing the content of the table completely
     * @param tableModel New table model
     * @return Itself
     */
    public synchronized Table<V> setTableModel(TableModel<V> tableModel) {
        if(tableModel == null) {
            throw new IllegalArgumentException("Cannot assign a null TableModel");
        }
        this.tableModel.removeListener(tableModelListener);
        this.tableModel = tableModel;
        this.tableModel.addListener(tableModelListener);
        invalidate();
        return this;
    }

    /**
     * Returns the {@code TableCellRenderer} used by this table when drawing cells
     * @return {@code TableCellRenderer} used by this table when drawing cells
     */
    public TableCellRenderer<V> getTableCellRenderer() {
        return tableCellRenderer;
    }

    /**
     * Replaces the {@code TableCellRenderer} used by this table when drawing cells
     * @param tableCellRenderer New {@code TableCellRenderer} to use
     * @return Itself
     */
    public synchronized Table<V> setTableCellRenderer(TableCellRenderer<V> tableCellRenderer) {
        this.tableCellRenderer = tableCellRenderer;
        invalidate();
        return this;
    }

    /**
     * Returns the {@code TableHeaderRenderer} used by this table when drawing the table's header
     * @return {@code TableHeaderRenderer} used by this table when drawing the table's header
     */
    public TableHeaderRenderer<V> getTableHeaderRenderer() {
        return tableHeaderRenderer;
    }

    /**
     * Replaces the {@code TableHeaderRenderer} used by this table when drawing the table's header
     * @param tableHeaderRenderer New {@code TableHeaderRenderer} to use
     * @return Itself
     */
    public synchronized Table<V> setTableHeaderRenderer(TableHeaderRenderer<V> tableHeaderRenderer) {
        this.tableHeaderRenderer = tableHeaderRenderer;
        invalidate();
        return this;
    }

    /**
     * Sets the number of columns this table should show. If there are more columns in the table model, a scrollbar will
     * be used to allow the user to scroll left and right and view all columns.
     * @param visibleColumns Number of columns to display at once
     */
    public synchronized void setVisibleColumns(int visibleColumns) {
        this.visibleColumns = visibleColumns;
        invalidate();
    }

    /**
     * Returns the number of columns this table will show. If there are more columns in the table model, a scrollbar
     * will be used to allow the user to scroll left and right and view all columns.
     * @return Number of visible columns for this table
     */
    public int getVisibleColumns() {
        return visibleColumns;
    }

    /**
     * Sets the number of rows this table will show. If there are more rows in the table model, a scrollbar will be used
     * to allow the user to scroll up and down and view all rows.
     * @param visibleRows Number of rows to display at once
     */
    public synchronized void setVisibleRows(int visibleRows) {
        this.visibleRows = visibleRows;
        invalidate();
    }

    /**
     * Returns the number of rows this table will show. If there are more rows in the table model, a scrollbar will be
     * used to allow the user to scroll up and down and view all rows.
     * @return Number of rows to display at once
     */
    public int getVisibleRows() {
        return visibleRows;
    }

    /**
     * Returns the index of the row that is currently the first row visible. This is always 0 unless scrolling has been
     * enabled and either the user or the software (through {@code setViewTopRow(..)}) has scrolled down.
     * @return Index of the row that is currently the first row visible
     * @deprecated Use the table renderers method instead
     */
    @Deprecated
    public int getViewTopRow() {
        return getRenderer().getViewTopRow();
    }

    /**
     * Sets the view row offset for the first row to display in the table. Calling this with 0 will make the first row
     * in the model be the first visible row in the table.
     *
     * @param viewTopRow Index of the row that is currently the first row visible
     * @return Itself
     * @deprecated Use the table renderers method instead
     */
    @Deprecated
    public synchronized Table<V> setViewTopRow(int viewTopRow) {
        getRenderer().setViewTopRow(viewTopRow);
        return this;
    }

    /**
     * Returns the index of the column that is currently the first column visible. This is always 0 unless scrolling has
     * been enabled and either the user or the software (through {@code setViewLeftColumn(..)}) has scrolled to the
     * right.
     * @return Index of the column that is currently the first column visible
     * @deprecated Use the table renderers method instead
     */
    @Deprecated
    public int getViewLeftColumn() {
        return getRenderer().getViewLeftColumn();
    }

    /**
     * Sets the view column offset for the first column to display in the table. Calling this with 0 will make the first
     * column in the model be the first visible column in the table.
     *
     * @param viewLeftColumn Index of the column that is currently the first column visible
     * @return Itself
     * @deprecated Use the table renderers method instead
     */
    @Deprecated
    public synchronized Table<V> setViewLeftColumn(int viewLeftColumn) {
        getRenderer().setViewLeftColumn(viewLeftColumn);
        return this;
    }

    /**
     * Returns the currently selection column index, if in cell-selection mode. Otherwise it returns -1.
     * @return In cell-selection mode returns the index of the selected column, otherwise -1
     */
    public int getSelectedColumn() {
        return selectedColumn;
    }

    /**
     * If in cell selection mode, updates which column is selected and ensures the selected column is visible in the
     * view. If not in cell selection mode, does nothing.
     * @param selectedColumn Index of the column that should be selected
     * @return Itself
     */
    public synchronized Table<V> setSelectedColumn(int selectedColumn) {
        if(cellSelection) {
            this.selectedColumn = selectedColumn;
        }
        return this;
    }

    /**
     * Returns the index of the currently selected row
     * @return Index of the currently selected row
     */
    public int getSelectedRow() {
        return selectedRow;
    }

    /**
     * Sets the index of the selected row and ensures the selected row is visible in the view
     * @param selectedRow Index of the row to select
     * @return Itself
     */
    public synchronized Table<V> setSelectedRow(int selectedRow) {
        this.selectedRow = selectedRow;
        return this;
    }

    /**
     * If {@code true}, the user will be able to select and navigate individual cells, otherwise the user can only
     * select full rows.
     * @param cellSelection {@code true} if cell selection should be enabled, {@code false} for row selection
     * @return Itself
     */
    public synchronized Table<V> setCellSelection(boolean cellSelection) {
        this.cellSelection = cellSelection;
        if(cellSelection && selectedColumn == -1) {
            selectedColumn = 0;
        }
        else if(!cellSelection) {
            selectedColumn = -1;
        }
        return this;
    }

    /**
     * Returns {@code true} if this table is in cell-selection mode, otherwise {@code false}
     * @return {@code true} if this table is in cell-selection mode, otherwise {@code false}
     */
    public boolean isCellSelection() {
        return cellSelection;
    }

    /**
     * Assigns an action to run whenever the user presses the enter key while focused on the table. If called with
     * {@code null}, no action will be run.
     * @param selectAction Action to perform when user presses the enter key
     * @return Itself
     */
    public synchronized Table<V> setSelectAction(Runnable selectAction) {
        this.selectAction = selectAction;
        return this;
    }

    /**
     * Returns {@code true} if this table can be navigated away from when the selected row is at one of the extremes and
     * the user presses the array key to continue in that direction. With {@code escapeByArrowKey} set to {@code true},
     * this will move focus away from the table in the direction the user pressed, if {@code false} then nothing will
     * happen.
     * @return {@code true} if user can switch focus away from the table using arrow keys, {@code false} otherwise
     */
    public boolean isEscapeByArrowKey() {
        return escapeByArrowKey;
    }

    /**
     * Sets the flag for if this table can be navigated away from when the selected row is at one of the extremes and
     * the user presses the array key to continue in that direction. With {@code escapeByArrowKey} set to {@code true},
     * this will move focus away from the table in the direction the user pressed, if {@code false} then nothing will
     * happen.
     * @param escapeByArrowKey {@code true} if user can switch focus away from the table using arrow keys, {@code false} otherwise
     * @return Itself
     */
    public synchronized Table<V> setEscapeByArrowKey(boolean escapeByArrowKey) {
        this.escapeByArrowKey = escapeByArrowKey;
        return this;
    }

    @Override
    protected TableRenderer<V> createDefaultRenderer() {
        return new DefaultTableRenderer<V>();
    }

    @Override
    public TableRenderer<V> getRenderer() {
        return (TableRenderer<V>)super.getRenderer();
    }

    @Override
    public Result handleKeyStroke(KeyStroke keyStroke) {
        switch(keyStroke.getKeyType()) {
            case ArrowUp:
                if(selectedRow > 0) {
                    selectedRow--;
                }
                else if(escapeByArrowKey) {
                    return Result.MOVE_FOCUS_UP;
                }
                break;
            case ArrowDown:
                if(selectedRow < tableModel.getRowCount() - 1) {
                    selectedRow++;
                }
                else if(escapeByArrowKey) {
                    return Result.MOVE_FOCUS_DOWN;
                }
                break;
            case PageUp:
                if(getRenderer().getVisibleRowsOnLastDraw() > 0 && selectedRow > 0) {
                    selectedRow -= Math.min(getRenderer().getVisibleRowsOnLastDraw() - 1, selectedRow);
                }
                break;
            case PageDown:
                if(getRenderer().getVisibleRowsOnLastDraw() > 0 && selectedRow < tableModel.getRowCount() - 1) {
                    int toEndDistance = tableModel.getRowCount() - 1 - selectedRow;
                    selectedRow += Math.min(getRenderer().getVisibleRowsOnLastDraw() - 1, toEndDistance);
                }
                break;
            case Home:
                selectedRow = 0;
                break;
            case End:
                selectedRow = tableModel.getRowCount() - 1;
                break;
            case ArrowLeft:
                if(cellSelection && selectedColumn > 0) {
                    selectedColumn--;
                }
                else if(escapeByArrowKey) {
                    return Result.MOVE_FOCUS_LEFT;
                }
                break;
            case ArrowRight:
                if(cellSelection && selectedColumn < tableModel.getColumnCount() - 1) {
                    selectedColumn++;
                }
                else if(escapeByArrowKey) {
                    return Result.MOVE_FOCUS_RIGHT;
                }
                break;
            case Enter:
                Runnable runnable = selectAction;   //To avoid synchronizing
                if(runnable != null) {
                    runnable.run();
                }
                else {
                    return Result.MOVE_FOCUS_NEXT;
                }
                break;
            default:
                return super.handleKeyStroke(keyStroke);
        }
        invalidate();
        return Result.HANDLED;
    }

}
