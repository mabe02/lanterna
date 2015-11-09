package com.googlecode.lanterna.gui2.table;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.input.KeyStroke;

/**
 *
 */
public class Table<V> extends AbstractInteractableComponent<Table<V>> {
    private TableModel<V> tableModel;
    private TableHeaderRenderer<V> tableHeaderRenderer;
    private TableCellRenderer<V> tableCellRenderer;
    private Runnable selectAction;
    private boolean cellSelection;
    private int visibleRows;
    private int visibleColumns;
    private int viewTopRow;
    private int viewLeftColumn;
    private int selectedRow;
    private int selectedColumn;
    private boolean escapeByArrowKey;

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
        this.viewTopRow = 0;
        this.viewLeftColumn = 0;
        this.cellSelection = false;
        this.selectedRow = 0;
        this.selectedColumn = -1;
        this.escapeByArrowKey = true;
    }

    public TableModel<V> getTableModel() {
        return tableModel;
    }

    public Table<V> setTableModel(TableModel<V> tableModel) {
        if(tableModel == null) {
            throw new IllegalArgumentException("Cannot assign a null TableModel");
        }
        this.tableModel = tableModel;
        invalidate();
        return this;
    }

    public TableCellRenderer<V> getTableCellRenderer() {
        return tableCellRenderer;
    }

    public Table<V> setTableCellRenderer(TableCellRenderer<V> tableCellRenderer) {
        this.tableCellRenderer = tableCellRenderer;
        invalidate();
        return this;
    }

    public TableHeaderRenderer<V> getTableHeaderRenderer() {
        return tableHeaderRenderer;
    }

    public Table<V> setTableHeaderRenderer(TableHeaderRenderer<V> tableHeaderRenderer) {
        this.tableHeaderRenderer = tableHeaderRenderer;
        invalidate();
        return this;
    }

    public void setVisibleColumns(int visibleColumns) {
        this.visibleColumns = visibleColumns;
        invalidate();
    }

    public int getVisibleColumns() {
        return visibleColumns;
    }

    public void setVisibleRows(int visibleRows) {
        this.visibleRows = visibleRows;
        invalidate();
    }

    public int getVisibleRows() {
        return visibleRows;
    }

    public int getViewTopRow() {
        return viewTopRow;
    }

    public void setViewTopRow(int viewTopRow) {
        this.viewTopRow = viewTopRow;
    }

    public int getViewLeftColumn() {
        return viewLeftColumn;
    }

    public void setViewLeftColumn(int viewLeftColumn) {
        this.viewLeftColumn = viewLeftColumn;
    }

    public int getSelectedColumn() {
        return selectedColumn;
    }

    public int getSelectedRow() {
        return selectedRow;
    }

    public void setSelectedColumn(int selectedColumn) {
        if(cellSelection) {
            this.selectedColumn = selectedColumn;
            ensureSelectedItemIsVisible();
        }
    }

    public void setSelectedRow(int selectedRow) {
        this.selectedRow = selectedRow;
        ensureSelectedItemIsVisible();
    }

    public void setCellSelection(boolean cellSelection) {
        this.cellSelection = cellSelection;
        if(cellSelection && selectedColumn == -1) {
            selectedColumn = 0;
        }
        else if(!cellSelection) {
            selectedColumn = -1;
        }
    }

    public void setSelectAction(Runnable selectAction) {
        this.selectAction = selectAction;
    }

    public boolean isCellSelection() {
        return cellSelection;
    }

    public boolean isEscapeByArrowKey() {
        return escapeByArrowKey;
    }

    public void setEscapeByArrowKey(boolean escapeByArrowKey) {
        this.escapeByArrowKey = escapeByArrowKey;
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
        ensureSelectedItemIsVisible();
        invalidate();
        return Result.HANDLED;
    }

    private void ensureSelectedItemIsVisible() {
        if(visibleRows > 0 && selectedRow < viewTopRow) {
            viewTopRow = selectedRow;
        }
        else if(visibleRows > 0 && selectedRow >= viewTopRow + visibleRows) {
            viewTopRow = Math.max(0, selectedRow - visibleRows + 1);
        }
        if(selectedColumn != -1) {
            if(visibleColumns > 0 && selectedColumn < viewLeftColumn) {
                viewLeftColumn = selectedColumn;
            }
            else if(visibleColumns > 0 && selectedColumn >= viewLeftColumn + visibleColumns) {
                viewLeftColumn = Math.max(0, selectedColumn - visibleColumns + 1);
            }
        }
    }
}
