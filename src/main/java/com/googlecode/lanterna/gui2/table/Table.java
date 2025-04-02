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
 * Copyright (C) 2010-2024 Martin Berglund
 */
package com.googlecode.lanterna.gui2.table;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.gui2.AbstractInteractableComponent;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.input.MouseAction;
import com.googlecode.lanterna.input.MouseActionType;

import java.sql.*;
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
        this(new TableModel<>(columnLabels));
    }

    /**
     * Creates a new {@code Table} with the specified table model
     * @param tableModel Table model
     */
    public Table(final TableModel tableModel) {
        this.tableHeaderRenderer = new DefaultTableHeaderRenderer<>();
        this.tableCellRenderer = new DefaultTableCellRenderer<>();
        this.tableModel = tableModel;

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
                if (index <= selectedRow) {
                    selectedRow = Math.min(model.getRowCount() - 1, selectedRow + 1);
                }
                invalidate();
            }

            @Override
            public void onRowRemoved(TableModel<V> model, int index, List<V> oldRow) {
                if (index < selectedRow) {
                    selectedRow = Math.max(0, selectedRow-1);
                } else {
                    // We may have deleted the selected row
                    int rowCount = model.getRowCount();
                    if (selectedRow > rowCount - 1) {
                        selectedRow = Math.max(0, rowCount - 1);
                    }
                }
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
     * Returns the index of the first row that is currently visible.
     * @return the index of the first row that is currently visible
     */
    public int getFirstViewedRowIndex() {
        return getRenderer().getViewTopRow();
    }
    
    /**
     * Returns the index of the last row that is currently visible.
     * @return the index of the last row that is currently visible
     */
    public int getLastViewedRowIndex() {
        int visibleRows = getRenderer().getVisibleRowsOnLastDraw();
        return Math.min(getRenderer().getViewTopRow() + visibleRows -1, tableModel.getRowCount() -1);
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
        if (selectedRow < 0) {
            throw new IllegalArgumentException("selectedRow must be >= 0 but was " + selectedRow);
        }
        int rowCount = tableModel.getRowCount();
        if (rowCount == 0) {
            selectedRow = 0;
        } else if (selectedRow > rowCount - 1) {
            selectedRow = rowCount - 1;
        }
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
     * Assigns an action to run whenever the user presses the enter or space key while focused on the table. If called with
     * {@code null}, no action will be run.
     * @param selectAction Action to perform when user presses the enter or space key
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
        return new DefaultTableRenderer<>();
    }

    @Override
    public TableRenderer<V> getRenderer() {
        return (TableRenderer<V>)super.getRenderer();
    }

    @Override
    public Result handleKeyStroke(KeyStroke keyStroke) {
        switch(keyStroke.getKeyType()) {
            case ARROW_UP:
                if(selectedRow > 0) {
                    selectedRow--;
                }
                else if(escapeByArrowKey) {
                    return Result.MOVE_FOCUS_UP;
                }
                break;
            case ARROW_DOWN:
                if(selectedRow < tableModel.getRowCount() - 1) {
                    selectedRow++;
                }
                else if(escapeByArrowKey) {
                    return Result.MOVE_FOCUS_DOWN;
                }
                break;
            case PAGE_UP:
                if(getRenderer().getVisibleRowsOnLastDraw() > 0 && selectedRow > 0) {
                    selectedRow -= Math.min(getRenderer().getVisibleRowsOnLastDraw() - 1, selectedRow);
                }
                break;
            case PAGE_DOWN:
                if(getRenderer().getVisibleRowsOnLastDraw() > 0 && selectedRow < tableModel.getRowCount() - 1) {
                    int toEndDistance = tableModel.getRowCount() - 1 - selectedRow;
                    selectedRow += Math.min(getRenderer().getVisibleRowsOnLastDraw() - 1, toEndDistance);
                }
                break;
            case HOME:
                selectedRow = 0;
                break;
            case END:
                selectedRow = tableModel.getRowCount() - 1;
                break;
            case ARROW_LEFT:
                if(cellSelection && selectedColumn > 0) {
                    selectedColumn--;
                }
                else if(escapeByArrowKey) {
                    return Result.MOVE_FOCUS_LEFT;
                }
                break;
            case ARROW_RIGHT:
                if(cellSelection && selectedColumn < tableModel.getColumnCount() - 1) {
                    selectedColumn++;
                }
                else if(escapeByArrowKey) {
                    return Result.MOVE_FOCUS_RIGHT;
                }
                break;
            case CHARACTER:
            case ENTER:
                if (isKeyboardActivationStroke(keyStroke)) {
                    Runnable runnable = selectAction;   //To avoid synchronizing
                    if(runnable != null) {
                        runnable.run();
                    } else {
                        return Result.HANDLED;
                    }
                    break;
                } else {
                    return super.handleKeyStroke(keyStroke);
                }
            case MOUSE_EVENT:
                MouseAction action = (MouseAction)keyStroke;
                MouseActionType actionType = action.getActionType();
                if (actionType == MouseActionType.MOVE) {
                    // do nothing
                    return Result.UNHANDLED;
                } 
                if (!isFocused()) {
                    super.handleKeyStroke(keyStroke);
                }
                int mouseRow = getRowByMouseAction((MouseAction) keyStroke);
                int mouseColumn = getColumnByMouseAction((MouseAction) keyStroke);
                boolean isDifferentCell = mouseRow != selectedRow || mouseColumn != selectedColumn;
                selectedRow = mouseRow;
                selectedColumn = mouseColumn;
                if (isDifferentCell) {
                    return handleKeyStroke(new KeyStroke(KeyType.ENTER));
                }
                break;
            default:
                return super.handleKeyStroke(keyStroke);
        }
        invalidate();
        return Result.HANDLED;
    }
    
    /**
     * By converting {@link TerminalPosition}s to
     * {@link #toGlobal(TerminalPosition)} gets row clicked on by mouse action.
     * 
     * @return row of a table that was clicked on with {@link MouseAction}
     */
    protected int getRowByMouseAction(MouseAction mouseAction) {
        int minPossible = getFirstViewedRowIndex();
        int maxPossible = getLastViewedRowIndex();
        int mouseSpecified = mouseAction.getPosition().getRow() - getGlobalPosition().getRow() - 1;
        
        return Math.max(minPossible, Math.min(mouseSpecified, maxPossible));
    }
    
    /**
     * By converting {@link TerminalPosition}s to
     * {@link #toGlobal(TerminalPosition)} and by comparing widths of column
     * headers, gets column clicked on by mouse action.
     * 
     * @return row of a table that was clicked on with {@link MouseAction}
     */
    protected int getColumnByMouseAction(MouseAction mouseAction) {
        int maxColumnIndex = tableModel.getColumnCount() -1;
        int column = 0;
        int columnSize = tableHeaderRenderer.getPreferredSize(this, tableModel.getColumnLabel(column), column).getColumns();
        int globalColumnMoused = mouseAction.getPosition().getColumn() - getGlobalPosition().getColumn();
        while (globalColumnMoused - columnSize - 1 >= 0 && column < maxColumnIndex) {
            globalColumnMoused -= columnSize;
            column++;
            columnSize = tableHeaderRenderer.getPreferredSize(this, tableModel.getColumnLabel(column), column).getColumns();
        }
        return column;
    }

    /**
     * saves table to SQL database where each column from the table will be type TEXT with one type INTEGER as the first for an index
     * @param conn jdbc connection using a jdbc driver
     * conn will close after data has been inserted
     * @param tableName the SQL table name to save as
     * CAUTION will overwrite any current table of that name
     */

    public void toSQL(Connection conn , String tableName) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("DROP TABLE IF EXISTS [" + tableName + "]");

        StringBuilder tableBuilder = new StringBuilder();
        tableBuilder.append("CREATE TABLE IF NOT EXISTS [")
                .append(tableName)
                .append("] (")
                .append("LANTERNA_ROW_ID INTEGER PRIMARY KEY AUTOINCREMENT, ");

        int columnCount = this.getTableModel().getColumnCount();
        int rowCount = this.getTableModel().getRowCount();

        for (int i = 0; i < columnCount; i++) {
            tableBuilder.append("[").append(this.getTableModel().getColumnLabel(i)).append("]");
            tableBuilder.append(" TEXT");
            if (i < columnCount - 1) {
                tableBuilder.append(", ");
            }
        }
        tableBuilder.append(");");
        stmt.execute(tableBuilder.toString());

        for (int j = 0; j < rowCount; j++) {
            StringBuilder insertSQL = new StringBuilder("INSERT INTO [" + tableName + "] (");
            //header names
            for (int i = 0; i < columnCount; i++) {
                insertSQL.append("[").append(this.getTableModel().getColumnLabel(i)).append("]");
                if (i < columnCount - 1) {
                    insertSQL.append(" ,");
                }
            }
            insertSQL.append(")");
            //values
            insertSQL.append(" VALUES (");
            for (int i = 0; i < columnCount; i++) {
                insertSQL.append("'").append(this.getTableModel().getCell(i , j)).append("'");
                if (i < columnCount - 1) {
                    insertSQL.append(" ,");
                }
            }
            insertSQL.append(")");
            stmt.execute(String.valueOf(insertSQL));
        }
        conn.close();
    }

    /**
     * retrieves a table from a SQL database where each column from the table will be type TEXT with one type INTEGER as the first for an index
     * @param conn jdbc connection using a jdbc driver
     * conn will close after data has been inserted
     * @param tableName the SQL table to retreive
     */
    public void fromSQL(Connection conn, String tableName) throws SQLException {
        this.getTableModel().clear();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM " + "[" + tableName + "]");
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        String[] headers = new String[columnCount];
        for (int i = 1; i <= columnCount; i++) {
            headers[i - 1] = metaData.getColumnName(i);
        }
        Table<V> tableData = new Table<>(headers);

        while (rs.next()) {
            Object[] rowArray = new Object[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                rowArray[i - 1] = rs.getString(i);
            }
            tableData.getTableModel().addRow((V[]) rowArray);
        }
        this.setTableModel(tableData.getTableModel());
    }
}
