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

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.Theme;
import com.googlecode.lanterna.graphics.ThemeDefinition;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.ScrollBar;
import com.googlecode.lanterna.gui2.TextGUIGraphics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Default implementation of {@code TableRenderer}
 * @param <V> Type of data stored in each table cell
 * @author Martin
 */
public class DefaultTableRenderer<V> implements TableRenderer<V> {

    private final ScrollBar verticalScrollBar;
    private final ScrollBar horizontalScrollBar;

    private TableCellBorderStyle headerVerticalBorderStyle;
    private TableCellBorderStyle headerHorizontalBorderStyle;
    private TableCellBorderStyle cellVerticalBorderStyle;
    private TableCellBorderStyle cellHorizontalBorderStyle;

    private int viewTopRow;
    private int viewLeftColumn;
    private int visibleRowsOnLastDraw;

    //So that we don't have to recalculate the size every time. This still isn't optimal but shouganai.
    private TerminalSize cachedSize;
    private final List<Integer> preferredColumnSizes;
    private final List<Integer> preferredRowSizes;
    private final Set<Integer> expandableColumns;
    private int headerSizeInRows;

    /**
     * Default constructor
     */
    public DefaultTableRenderer() {
        verticalScrollBar = new ScrollBar(Direction.VERTICAL);
        horizontalScrollBar = new ScrollBar(Direction.HORIZONTAL);

        headerVerticalBorderStyle = TableCellBorderStyle.None;
        headerHorizontalBorderStyle = TableCellBorderStyle.EmptySpace;
        cellVerticalBorderStyle = TableCellBorderStyle.None;
        cellHorizontalBorderStyle = TableCellBorderStyle.EmptySpace;

        viewTopRow = 0;
        viewLeftColumn = 0;
        visibleRowsOnLastDraw = 0;

        cachedSize = null;

        preferredColumnSizes = new ArrayList<Integer>();
        preferredRowSizes = new ArrayList<Integer>();
        expandableColumns = new TreeSet<Integer>();
        headerSizeInRows = 0;
    }

    /**
     * Sets the style to be used when separating the table header row from the actual "data" cells below. This will
     * cause a new line to be added under the header labels, unless set to {@code TableCellBorderStyle.None}.
     *
     * @param headerVerticalBorderStyle Style to use to separate Table header from body
     */
    public synchronized void setHeaderVerticalBorderStyle(TableCellBorderStyle headerVerticalBorderStyle) {
        this.headerVerticalBorderStyle = headerVerticalBorderStyle;
    }

    /**
     * Sets the style to be used when separating the table header labels from each other. This will cause a new
     * column to be added in between each label, unless set to {@code TableCellBorderStyle.None}.
     *
     * @param headerHorizontalBorderStyle Style to use when separating header columns horizontally
     */
    public synchronized void setHeaderHorizontalBorderStyle(TableCellBorderStyle headerHorizontalBorderStyle) {
        this.headerHorizontalBorderStyle = headerHorizontalBorderStyle;
    }

    /**
     * Sets the style to be used when vertically separating table cells from each other. This will cause a new line
     * to be added between every row, unless set to {@code TableCellBorderStyle.None}.
     *
     * @param cellVerticalBorderStyle Style to use to separate table cells vertically
     */
    public synchronized void setCellVerticalBorderStyle(TableCellBorderStyle cellVerticalBorderStyle) {
        this.cellVerticalBorderStyle = cellVerticalBorderStyle;
    }

    /**
     * Sets the style to be used when horizontally separating table cells from each other. This will cause a new
     * column to be added between every row, unless set to {@code TableCellBorderStyle.None}.
     *
     * @param cellHorizontalBorderStyle Style to use to separate table cells horizontally
     */
    public synchronized void setCellHorizontalBorderStyle(TableCellBorderStyle cellHorizontalBorderStyle) {
        this.cellHorizontalBorderStyle = cellHorizontalBorderStyle;
    }

    /**
     * Sets the list of columns (by index, where 0 is the first column) that can be expanded, should the drawable area
     * be larger than the table is requesting.
     * @param expandableColumns Collection of indexes for expandable columns
     */
    public synchronized void setExpandableColumns(Collection<Integer> expandableColumns) {
        this.expandableColumns.clear();
        this.expandableColumns.addAll(expandableColumns);
    }

    private boolean isHorizontallySpaced() {
        return headerHorizontalBorderStyle != TableCellBorderStyle.None ||
                cellHorizontalBorderStyle != TableCellBorderStyle.None;
    }

    /**
     * Returns the number of rows that could be drawn on the last draw operation. If the table doesn't have any visible
     * row count set through {@link Table#setVisibleRows(int)}, this is the only way to find out exactly how large the
     * table ended up. But even if you did set the number of visible rows explicitly, due to terminal size constraints
     * the actually drawn size might have been different.
     * @return Number of rows that could be drawn on the last UI update call, will also return 0 if the table has not
     * yet been drawn out
     */
    public int getVisibleRowsOnLastDraw() {
        return visibleRowsOnLastDraw;
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

    @Override
    public synchronized TerminalSize getPreferredSize(Table<V> table) {
        //Quick bypass if the table hasn't changed
        if(!table.isInvalid() && cachedSize != null) {
            return cachedSize;
        }

        TableModel<V> tableModel = table.getTableModel();

        // Copy these so we don't modify the renderers state
        int viewLeftColumn = this.viewLeftColumn;
        int viewTopRow = this.viewTopRow;
        int visibleColumns = table.getVisibleColumns();
        int visibleRows = table.getVisibleRows();
        int selectedRow = table.getSelectedRow();
        int selectedColumn = table.getSelectedColumn();
        List<List<V>> rows = tableModel.getRows();
        List<String> columnHeaders = tableModel.getColumnLabels();
        TableHeaderRenderer<V> tableHeaderRenderer = table.getTableHeaderRenderer();
        TableCellRenderer<V> tableCellRenderer = table.getTableCellRenderer();

        if(visibleColumns == 0) {
            visibleColumns = tableModel.getColumnCount();
        }
        if(visibleRows == 0) {
            visibleRows = tableModel.getRowCount();
        }

        preferredColumnSizes.clear();
        preferredRowSizes.clear();

        if(tableModel.getColumnCount() == 0) {
            return TerminalSize.ZERO;
        }

        // Adjust view port if necessary (although this is only for the preferred size calculation, we don't actually
        // update the view model)
        if(selectedColumn != -1 && viewLeftColumn > selectedColumn) {
            viewLeftColumn = selectedColumn;
        }
        else if(selectedColumn != 1 && viewLeftColumn <= selectedColumn - visibleColumns) {
            viewLeftColumn = Math.max(0, selectedColumn - visibleColumns + 1);
        }
        if(viewTopRow > selectedRow) {
            viewTopRow = selectedRow;
        }
        else if(viewTopRow <= selectedRow - visibleRows) {
            viewTopRow = Math.max(0, selectedRow - visibleRows + 1);
        }

        // If there are no rows, base the column sizes off of the column labels
        if(rows.size() == 0) {
            for(int columnIndex = 0; columnIndex < columnHeaders.size(); columnIndex++) {
                int columnSize = tableHeaderRenderer.getPreferredSize(table, columnHeaders.get(columnIndex), columnIndex).getColumns();
                if(preferredColumnSizes.size() == columnIndex) {
                    preferredColumnSizes.add(columnSize);
                }
                else {
                    if(preferredColumnSizes.get(columnIndex) < columnSize) {
                        preferredColumnSizes.set(columnIndex, columnSize);
                    }
                }
            }
        }

        for(int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            List<V> row = rows.get(rowIndex);
            for(int columnIndex = 0; columnIndex < row.size(); columnIndex++) {
                V cell = row.get(columnIndex);
                int columnSize = tableCellRenderer.getPreferredSize(table, cell, columnIndex, rowIndex).getColumns();
                if(preferredColumnSizes.size() == columnIndex) {
                    preferredColumnSizes.add(columnSize);
                }
                else {
                    if(preferredColumnSizes.get(columnIndex) < columnSize) {
                        preferredColumnSizes.set(columnIndex, columnSize);
                    }
                }
            }

            //Do the headers too, on the first iteration
            if(rowIndex == 0) {
                for(int columnIndex = 0; columnIndex < row.size(); columnIndex++) {
                    int columnSize = tableHeaderRenderer.getPreferredSize(table, columnHeaders.get(columnIndex), columnIndex).getColumns();
                    if(preferredColumnSizes.size() == columnIndex) {
                        preferredColumnSizes.add(columnSize);
                    }
                    else {
                        if(preferredColumnSizes.get(columnIndex) < columnSize) {
                            preferredColumnSizes.set(columnIndex, columnSize);
                        }
                    }
                }
            }
        }

        for(int columnIndex = 0; columnIndex < columnHeaders.size(); columnIndex++) {
            for(int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
                V cell = rows.get(rowIndex).get(columnIndex);
                int rowSize = tableCellRenderer.getPreferredSize(table, cell, columnIndex, rowIndex).getRows();
                if(preferredRowSizes.size() == rowIndex) {
                    preferredRowSizes.add(rowSize);
                }
                else {
                    if(preferredRowSizes.get(rowIndex) < rowSize) {
                        preferredRowSizes.set(rowIndex, rowSize);
                    }
                }
            }
        }

        int preferredRowSize = 0;
        int preferredColumnSize = 0;
        if (table.getVisibleColumns() == 0) {
            for (int columnIndex = 0; columnIndex < preferredColumnSizes.size(); columnIndex++) {
                preferredColumnSize += preferredColumnSizes.get(columnIndex);
            }
        }
        else {
            for (int columnIndex = viewLeftColumn; columnIndex < Math.min(preferredColumnSizes.size(), viewLeftColumn + visibleColumns); columnIndex++) {
                preferredColumnSize += preferredColumnSizes.get(columnIndex);
            }
        }

        if (table.getVisibleRows() == 0) {
            for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
                preferredRowSize += preferredRowSizes.get(rowIndex);
            }
        }
        else {
            for (int rowIndex = viewTopRow; rowIndex < Math.min(rows.size(), viewTopRow + visibleRows); rowIndex++) {
                preferredRowSize += preferredRowSizes.get(rowIndex);
            }
        }

        headerSizeInRows = 0;
        for(int columnIndex = 0; columnIndex < columnHeaders.size(); columnIndex++) {
            int headerRows = tableHeaderRenderer.getPreferredSize(table, columnHeaders.get(columnIndex), columnIndex).getRows();
            if(headerSizeInRows < headerRows) {
                headerSizeInRows = headerRows;
            }
        }
        preferredRowSize += headerSizeInRows;

        if(headerVerticalBorderStyle != TableCellBorderStyle.None) {
            preferredRowSize++;    //Spacing between header and body
        }
        if(cellVerticalBorderStyle != TableCellBorderStyle.None) {
            if(!rows.isEmpty()) {
                preferredRowSize += Math.min(rows.size(), visibleRows) - 1; //Vertical space between cells
            }
        }
        if(isHorizontallySpaced()) {
            if(!columnHeaders.isEmpty()) {
                preferredColumnSize += Math.min(tableModel.getColumnCount(), visibleColumns) - 1;    //Spacing between the columns
            }
        }

        //Add one space taken by scrollbars (we always add one for the vertical scrollbar but for the horizontal only if
        // we think we need one). Unfortunately we don't know the size constraints at this point so we don't know if the
        // table will need to force scrollbars or not. We might think that we don't need a horizontal scrollbar here but
        // it might turn out that we need it.
        preferredColumnSize++;
        if(visibleColumns < tableModel.getColumnCount()) {
            preferredRowSize++;
        }

        cachedSize = new TerminalSize(preferredColumnSize, preferredRowSize);
        return cachedSize;
    }

    @Override
    public TerminalPosition getCursorLocation(Table<V> component) {
        return null;
    }

    @Override
    public synchronized void drawComponent(TextGUIGraphics graphics, Table<V> table) {
        //Get the size
        TerminalSize area = graphics.getSize();

        //Don't even bother
        if(area.getRows() == 0 || area.getColumns() == 0) {
            return;
        }

        // Get preferred size if the table model has changed
        if(table.isInvalid()) {
            getPreferredSize(table);
        }

        int headerSizeIncludingBorder = headerSizeInRows + headerVerticalBorderStyle.getSize();
        int selectedColumn = table.getSelectedColumn();
        int selectedRow = table.getSelectedRow();

        // Update view port if necessary
        if(selectedColumn != -1 && viewLeftColumn > selectedColumn) {
            viewLeftColumn = selectedColumn;
        }
        if(viewTopRow > selectedRow) {
            viewTopRow = selectedRow;
        }

        TerminalSize areaWithoutScrollBars = area.withRelativeRows(-headerSizeIncludingBorder);
        int preferredVisibleRows = table.getVisibleRows();
        if(preferredVisibleRows == 0) {
            preferredVisibleRows = table.getTableModel().getRowCount();
        }
        int preferredVisibleColumns = table.getVisibleColumns();
        if(preferredVisibleColumns == 0) {
            preferredVisibleColumns = table.getTableModel().getColumnCount();
        }

        int visibleRows = calculateVisibleRows(areaWithoutScrollBars, viewTopRow, preferredVisibleRows);
        boolean needVerticalScrollBar = visibleRows < table.getTableModel().getRowCount();
        if(needVerticalScrollBar) {
            areaWithoutScrollBars = areaWithoutScrollBars.withRelativeColumns(-verticalScrollBar.getPreferredSize().getColumns());
        }
        int visibleColumns = calculateVisibleColumns(areaWithoutScrollBars, viewLeftColumn, preferredVisibleColumns);
        boolean needHorizontalScrollBar = visibleColumns < table.getTableModel().getColumnCount();
        if(needHorizontalScrollBar) {
            areaWithoutScrollBars = areaWithoutScrollBars.withRelativeRows(-horizontalScrollBar.getPreferredSize().getRows());

            // As we have now a horizontal scrollbar, we need to re-evaluate how many rows are visible
            visibleRows = calculateVisibleRows(areaWithoutScrollBars, viewTopRow, preferredVisibleRows);
            if(!needVerticalScrollBar && visibleRows < table.getTableModel().getRowCount()) {
                // Previously we didn't need a scrollbar but now we do because the horizontal scrollbar took one row
                needVerticalScrollBar = true;
                areaWithoutScrollBars = areaWithoutScrollBars.withRelativeColumns(-verticalScrollBar.getPreferredSize().getColumns());

                // Also recalculate visible columns to take into consideration the new vertical scrollbar
                visibleColumns = calculateVisibleColumns(areaWithoutScrollBars, viewLeftColumn, preferredVisibleColumns);
            }
        }

        // Now that we know (roughly) how many rows fit, update view port again if necessary
        while(selectedColumn != 1 && viewLeftColumn <= selectedColumn - visibleColumns) {
            viewLeftColumn = Math.max(0, selectedColumn - visibleColumns + 1);
            visibleColumns = calculateVisibleColumns(areaWithoutScrollBars, viewLeftColumn, preferredVisibleColumns);
        }
        while(viewTopRow <= selectedRow - visibleRows) {
            viewTopRow = Math.max(0, selectedRow - visibleRows + 1);
            visibleRows = calculateVisibleRows(areaWithoutScrollBars, viewTopRow, preferredVisibleRows);
        }

        List<Integer> columnSizes = fitColumnsInAvailableSpace(table, areaWithoutScrollBars, visibleColumns);
        drawHeader(graphics, table, columnSizes);
        drawRows(graphics.newTextGraphics(
                        new TerminalPosition(0, headerSizeIncludingBorder),
                        // Can't use areaWithoutScrollBars here because we need to draw the scrollbar too!
                        area.withRelativeRows(-headerSizeIncludingBorder)),
                table,
                columnSizes,
                visibleRows,
                visibleColumns,
                needVerticalScrollBar,
                needHorizontalScrollBar);

        visibleRowsOnLastDraw = visibleRows;
    }

    private int calculateVisibleRows(TerminalSize area, int viewTopRow, int preferredVisibleRows) {
        int remainingVerticalSpace = area.getRows();
        int visibleRows = 0;
        int borderAdjustment = cellVerticalBorderStyle.getSize();
        for (int row = viewTopRow; row < preferredRowSizes.size(); row++) {
            if (preferredVisibleRows == visibleRows) {
                break;
            }
            int rowSize = preferredRowSizes.get(row) + borderAdjustment;
            if (remainingVerticalSpace < rowSize) {
                break;
            }
            remainingVerticalSpace -= rowSize;
            visibleRows++;
        }
        return visibleRows;
    }

    private int calculateVisibleColumns(TerminalSize area, int viewLeftColumn, int preferredVisibleColumns) {
        int remainingHorizontalSpace = area.getColumns();
        int visibleColumns = 0;
        int borderAdjustment = cellHorizontalBorderStyle.getSize();
        for (int column = viewLeftColumn; column < preferredColumnSizes.size(); column++) {
            if (preferredVisibleColumns == visibleColumns) {
                break;
            }
            int columnSize = preferredColumnSizes.get(column) + (column > viewLeftColumn ? borderAdjustment : 0);
            if (remainingHorizontalSpace < columnSize) {
                break;
            }
            remainingHorizontalSpace -= columnSize;
            visibleColumns++;
        }
        return visibleColumns;
    }

    private List<Integer> fitColumnsInAvailableSpace(Table<V> table, TerminalSize area, int visibleColumns) {
        List<Integer> columnSizes = new ArrayList<Integer>(preferredColumnSizes);
        int horizontalSpaceRequirement = 0;
        int viewLeftColumn = table.getViewLeftColumn();
        List<String> headers = table.getTableModel().getColumnLabels();
        int endColumnIndex = Math.min(headers.size(), viewLeftColumn + visibleColumns);
        List<Integer> visibleExpandableColumns = new ArrayList<Integer>();
        for(int index = viewLeftColumn; index < endColumnIndex; index++) {
            horizontalSpaceRequirement += preferredColumnSizes.get(index);
            if(headerHorizontalBorderStyle != TableCellBorderStyle.None && index < (endColumnIndex - 1)) {
                horizontalSpaceRequirement += headerHorizontalBorderStyle.getSize();
            }
            if(expandableColumns.contains(index)) {
                visibleExpandableColumns.add(index);
            }
        }
        int extraHorizontalSpace = area.getColumns() - horizontalSpaceRequirement;
        while(extraHorizontalSpace > 0 && !visibleExpandableColumns.isEmpty()) {
            for(int expandableColumnIndex: visibleExpandableColumns) {
                columnSizes.set(expandableColumnIndex, columnSizes.get(expandableColumnIndex) + 1);
                extraHorizontalSpace--;
                if(extraHorizontalSpace == 0) {
                    break;
                }
            }
        }
        return columnSizes;
    }

    private void drawHeader(TextGUIGraphics graphics, Table<V> table, List<Integer> columnSizes) {
        Theme theme = table.getTheme();
        TableHeaderRenderer<V> tableHeaderRenderer = table.getTableHeaderRenderer();
        List<String> headers = table.getTableModel().getColumnLabels();
        int viewLeftColumn = table.getViewLeftColumn();
        int visibleColumns = table.getVisibleColumns();
        if(visibleColumns == 0) {
            visibleColumns = table.getTableModel().getColumnCount();
        }
        int leftPosition = 0;
        int endColumnIndex = Math.min(headers.size(), viewLeftColumn + visibleColumns);
        for(int index = viewLeftColumn; index < endColumnIndex; index++) {
            String label = headers.get(index);
            TerminalSize size = new TerminalSize(columnSizes.get(index), headerSizeInRows);
            tableHeaderRenderer.drawHeader(table, label, index, graphics.newTextGraphics(new TerminalPosition(leftPosition, 0), size));
            leftPosition += size.getColumns();
            if(headerHorizontalBorderStyle != TableCellBorderStyle.None && index < (endColumnIndex - 1)) {
                graphics.applyThemeStyle(theme.getDefinition(Table.class).getNormal());
                graphics.setCharacter(leftPosition, 0, getVerticalCharacter(headerHorizontalBorderStyle));
                leftPosition++;
            }
        }

        if(headerVerticalBorderStyle != TableCellBorderStyle.None) {
            leftPosition = 0;
            int topPosition = headerSizeInRows;
            graphics.applyThemeStyle(theme.getDefinition(Table.class).getNormal());
            for(int i = viewLeftColumn; i < endColumnIndex; i++) {
                if(i > viewLeftColumn) {
                    graphics.setCharacter(
                            leftPosition,
                            topPosition,
                            getJunctionCharacter(
                                    headerVerticalBorderStyle,
                                    headerHorizontalBorderStyle,
                                    cellHorizontalBorderStyle));
                    leftPosition++;
                }
                int columnWidth = columnSizes.get(i);
                graphics.drawLine(leftPosition, topPosition, leftPosition + columnWidth - 1, topPosition, getHorizontalCharacter(headerVerticalBorderStyle));
                leftPosition += columnWidth;
            }
            //Expand out the line in case the area is bigger
            if(leftPosition < graphics.getSize().getColumns()) {
                graphics.drawLine(leftPosition, topPosition, graphics.getSize().getColumns() - 1, topPosition, getHorizontalCharacter(headerVerticalBorderStyle));
            }
        }
    }

    private void drawRows(
            TextGUIGraphics graphics,
            Table<V> table,
            List<Integer> columnSizes,
            int visibleRows,
            int visibleColumns,
            boolean needVerticalScrollBar,
            boolean needHorizontalScrollBar) {
        Theme theme = table.getTheme();
        ThemeDefinition themeDefinition = theme.getDefinition(Table.class);
        TerminalSize area = graphics.getSize();
        TableCellRenderer<V> tableCellRenderer = table.getTableCellRenderer();
        TableModel<V> tableModel = table.getTableModel();
        List<List<V>> rows = tableModel.getRows();
        int viewTopRow = table.getViewTopRow();
        int viewLeftColumn = table.getViewLeftColumn();

        //Draw scrollbars (if needed)
        if(needVerticalScrollBar) {
            TerminalSize verticalScrollBarPreferredSize = verticalScrollBar.getPreferredSize();
            int scrollBarHeight = graphics.getSize().getRows();
            if(needHorizontalScrollBar) {
                scrollBarHeight--;
            }
            verticalScrollBar.setPosition(new TerminalPosition(graphics.getSize().getColumns() - verticalScrollBarPreferredSize.getColumns(), 0));
            verticalScrollBar.setSize(verticalScrollBarPreferredSize.withRows(scrollBarHeight));
            verticalScrollBar.setScrollMaximum(rows.size());
            verticalScrollBar.setViewSize(visibleRows);
            verticalScrollBar.setScrollPosition(viewTopRow);

            // Ensure the parent is correct
            if(table.getParent() != verticalScrollBar.getParent()) {
                if(verticalScrollBar.getParent() != null) {
                    verticalScrollBar.onRemoved(verticalScrollBar.getParent());
                }
                if(table.getParent() != null) {
                    verticalScrollBar.onAdded(table.getParent());
                }
            }

            // Finally draw the thing
            verticalScrollBar.draw(graphics.newTextGraphics(verticalScrollBar.getPosition(), verticalScrollBar.getSize()));

            // Adjust graphics object to the remaining area when the vertical scrollbar is subtracted
            graphics = graphics.newTextGraphics(TerminalPosition.TOP_LEFT_CORNER, graphics.getSize().withRelativeColumns(-verticalScrollBarPreferredSize.getColumns()));
        }
        if(needHorizontalScrollBar) {
            TerminalSize horizontalScrollBarPreferredSize = horizontalScrollBar.getPreferredSize();
            int scrollBarWidth = graphics.getSize().getColumns();
            horizontalScrollBar.setPosition(new TerminalPosition(0, graphics.getSize().getRows() - horizontalScrollBarPreferredSize.getRows()));
            horizontalScrollBar.setSize(horizontalScrollBarPreferredSize.withColumns(scrollBarWidth));
            horizontalScrollBar.setScrollMaximum(tableModel.getColumnCount());
            horizontalScrollBar.setViewSize(visibleColumns);
            horizontalScrollBar.setScrollPosition(viewLeftColumn);

            // Ensure the parent is correct
            if(table.getParent() != horizontalScrollBar.getParent()) {
                if(horizontalScrollBar.getParent() != null) {
                    horizontalScrollBar.onRemoved(horizontalScrollBar.getParent());
                }
                if(table.getParent() != null) {
                    horizontalScrollBar.onAdded(table.getParent());
                }
            }

            // Finally draw the thing
            horizontalScrollBar.draw(graphics.newTextGraphics(horizontalScrollBar.getPosition(), horizontalScrollBar.getSize()));

            // Adjust graphics object to the remaining area when the horizontal scrollbar is subtracted
            graphics = graphics.newTextGraphics(TerminalPosition.TOP_LEFT_CORNER, graphics.getSize().withRelativeRows(-horizontalScrollBarPreferredSize.getRows()));
        }

        int topPosition = 0;
        for(int rowIndex = viewTopRow; rowIndex < Math.min(viewTopRow + visibleRows, rows.size()); rowIndex++) {
            int leftPosition = 0;
            List<V> row = rows.get(rowIndex);
            for(int columnIndex = viewLeftColumn; columnIndex < Math.min(viewLeftColumn + visibleColumns, row.size()); columnIndex++) {
                if(columnIndex > viewLeftColumn) {
                    if(table.getSelectedRow() == rowIndex && !table.isCellSelection()) {
                        if(table.isFocused()) {
                            graphics.applyThemeStyle(themeDefinition.getActive());
                        }
                        else {
                            graphics.applyThemeStyle(themeDefinition.getSelected());
                        }
                    }
                    else {
                        graphics.applyThemeStyle(themeDefinition.getNormal());
                    }
                    graphics.setCharacter(leftPosition, topPosition, getVerticalCharacter(cellHorizontalBorderStyle));
                    leftPosition++;
                }
                V cell = row.get(columnIndex);
                TerminalPosition cellPosition = new TerminalPosition(leftPosition, topPosition);
                TerminalSize cellArea = new TerminalSize(columnSizes.get(columnIndex), preferredRowSizes.get(rowIndex));
                tableCellRenderer.drawCell(table, cell, columnIndex, rowIndex, graphics.newTextGraphics(cellPosition, cellArea));
                leftPosition += cellArea.getColumns();

                if(columnIndex < row.size() - 1) {
                    if (table.getSelectedRow() == rowIndex && !table.isCellSelection()) {
                        if (table.isFocused()) {
                            graphics.applyThemeStyle(themeDefinition.getActive());
                        } else {
                            graphics.applyThemeStyle(themeDefinition.getSelected());
                        }
                    } else {
                        graphics.applyThemeStyle(themeDefinition.getNormal());
                    }
                    graphics.setCharacter(leftPosition, topPosition, getVerticalCharacter(cellHorizontalBorderStyle));
                }

                if(leftPosition > area.getColumns()) {
                    break;
                }
            }
            topPosition += preferredRowSizes.get(rowIndex);
            if(cellVerticalBorderStyle != TableCellBorderStyle.None) {
                leftPosition = 0;
                graphics.applyThemeStyle(themeDefinition.getNormal());
                for(int i = viewLeftColumn; i < Math.min(viewLeftColumn + visibleColumns + 1, row.size()); i++) {
                    if(i > viewLeftColumn) {
                        graphics.setCharacter(
                                leftPosition,
                                topPosition,
                                getJunctionCharacter(
                                        cellVerticalBorderStyle,
                                        cellHorizontalBorderStyle,
                                        cellHorizontalBorderStyle));
                        leftPosition++;
                    }
                    int columnWidth = columnSizes.get(i);
                    graphics.drawLine(leftPosition, topPosition, leftPosition + columnWidth - 1, topPosition, getHorizontalCharacter(cellVerticalBorderStyle));
                    leftPosition += columnWidth;
                }
                topPosition += cellVerticalBorderStyle.getSize();
            }
            if(topPosition > area.getRows()) {
                break;
            }
        }
    }

    private char getHorizontalCharacter(TableCellBorderStyle style) {
        switch(style) {
            case SingleLine:
                return Symbols.SINGLE_LINE_HORIZONTAL;
            case DoubleLine:
                return Symbols.DOUBLE_LINE_HORIZONTAL;
            default:
                return ' ';
        }
    }

    private char getVerticalCharacter(TableCellBorderStyle style) {
        switch(style) {
            case SingleLine:
                return Symbols.SINGLE_LINE_VERTICAL;
            case DoubleLine:
                return Symbols.DOUBLE_LINE_VERTICAL;
            default:
                return ' ';
        }
    }

    private char getJunctionCharacter(TableCellBorderStyle mainStyle, TableCellBorderStyle styleAbove, TableCellBorderStyle styleBelow) {
        if(mainStyle == TableCellBorderStyle.SingleLine) {
            if(styleAbove == TableCellBorderStyle.SingleLine) {
                if(styleBelow == TableCellBorderStyle.SingleLine) {
                    return Symbols.SINGLE_LINE_CROSS;
                }
                else if(styleBelow == TableCellBorderStyle.DoubleLine) {
                    //There isn't any character for this, give upper side priority
                    return Symbols.SINGLE_LINE_T_UP;
                }
                else {
                    return Symbols.SINGLE_LINE_T_UP;
                }
            }
            else if(styleAbove == TableCellBorderStyle.DoubleLine) {
                if(styleBelow == TableCellBorderStyle.SingleLine) {
                    //There isn't any character for this, give upper side priority
                    return Symbols.SINGLE_LINE_T_DOUBLE_UP;
                }
                else if(styleBelow == TableCellBorderStyle.DoubleLine) {
                    return Symbols.DOUBLE_LINE_VERTICAL_SINGLE_LINE_CROSS;
                }
                else {
                    return Symbols.SINGLE_LINE_T_DOUBLE_UP;
                }
            }
            else {
                if(styleBelow == TableCellBorderStyle.SingleLine) {
                    return Symbols.SINGLE_LINE_T_DOWN;
                }
                else if(styleBelow == TableCellBorderStyle.DoubleLine) {
                    return Symbols.SINGLE_LINE_T_DOUBLE_DOWN;
                }
                else {
                    return Symbols.SINGLE_LINE_HORIZONTAL;
                }
            }
        }
        else if(mainStyle == TableCellBorderStyle.DoubleLine) {
            if(styleAbove == TableCellBorderStyle.SingleLine) {
                if(styleBelow == TableCellBorderStyle.SingleLine) {
                    return Symbols.DOUBLE_LINE_HORIZONTAL_SINGLE_LINE_CROSS;
                }
                else if(styleBelow == TableCellBorderStyle.DoubleLine) {
                    //There isn't any character for this, give upper side priority
                    return Symbols.DOUBLE_LINE_T_SINGLE_UP;
                }
                else {
                    return Symbols.DOUBLE_LINE_T_SINGLE_UP;
                }
            }
            else if(styleAbove == TableCellBorderStyle.DoubleLine) {
                if(styleBelow == TableCellBorderStyle.SingleLine) {
                    //There isn't any character for this, give upper side priority
                    return Symbols.DOUBLE_LINE_T_UP;
                }
                else if(styleBelow == TableCellBorderStyle.DoubleLine) {
                    return Symbols.DOUBLE_LINE_CROSS;
                }
                else {
                    return Symbols.DOUBLE_LINE_T_UP;
                }
            }
            else {
                if(styleBelow == TableCellBorderStyle.SingleLine) {
                    return Symbols.DOUBLE_LINE_T_SINGLE_DOWN;
                }
                else if(styleBelow == TableCellBorderStyle.DoubleLine) {
                    return Symbols.DOUBLE_LINE_T_DOWN;
                }
                else {
                    return Symbols.DOUBLE_LINE_HORIZONTAL;
                }
            }
        }
        else {
            return ' ';
        }
    }
}
