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
 * Copyright (C) 2010-2020 Martin Berglund
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

    @Override
    public synchronized TerminalSize getPreferredSize(Table<V> table) {
        //Quick bypass if the table hasn't changed
        if(!table.isInvalid() && cachedSize != null) {
            return cachedSize;
        }

        TableModel<V> tableModel = table.getTableModel();
        int viewLeftColumn = table.getViewLeftColumn();
        int viewTopRow = table.getViewTopRow();
        int visibleColumns = table.getVisibleColumns();
        int visibleRows = table.getVisibleRows();
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

        // If there are no rows, base the column sizes off of the column labels
        if(rows.size() == 0) {
            for(int columnIndex = viewLeftColumn; columnIndex < viewLeftColumn + visibleColumns; columnIndex++) {
                int columnSize = tableHeaderRenderer.getPreferredSize(table, columnHeaders.get(columnIndex), columnIndex).getColumns();
                int listOffset = columnIndex - viewLeftColumn;
                if(preferredColumnSizes.size() == listOffset) {
                    preferredColumnSizes.add(columnSize);
                }
                else {
                    if(preferredColumnSizes.get(listOffset) < columnSize) {
                        preferredColumnSizes.set(listOffset, columnSize);
                    }
                }
            }
        }

        for(int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            List<V> row = rows.get(rowIndex);
            for(int columnIndex = viewLeftColumn; columnIndex < Math.min(row.size(), viewLeftColumn + visibleColumns); columnIndex++) {
                V cell = row.get(columnIndex);
                int columnSize = tableCellRenderer.getPreferredSize(table, cell, columnIndex, rowIndex).getColumns();
                int listOffset = columnIndex - viewLeftColumn;
                if(preferredColumnSizes.size() == listOffset) {
                    preferredColumnSizes.add(columnSize);
                }
                else {
                    if(preferredColumnSizes.get(listOffset) < columnSize) {
                        preferredColumnSizes.set(listOffset, columnSize);
                    }
                }
            }

            //Do the headers too, on the first iteration
            if(rowIndex == 0) {
                for(int columnIndex = viewLeftColumn; columnIndex < Math.min(row.size(), viewLeftColumn + visibleColumns); columnIndex++) {
                    int columnSize = tableHeaderRenderer.getPreferredSize(table, columnHeaders.get(columnIndex), columnIndex).getColumns();
                    int listOffset = columnIndex - viewLeftColumn;
                    if(preferredColumnSizes.size() == listOffset) {
                        preferredColumnSizes.add(columnSize);
                    }
                    else {
                        if(preferredColumnSizes.get(listOffset) < columnSize) {
                            preferredColumnSizes.set(listOffset, columnSize);
                        }
                    }
                }
            }
        }

        for(int columnIndex = 0; columnIndex < columnHeaders.size(); columnIndex++) {
            for(int rowIndex = viewTopRow; rowIndex < Math.min(rows.size(), viewTopRow + visibleRows); rowIndex++) {
                V cell = rows.get(rowIndex).get(columnIndex);
                int rowSize = tableCellRenderer.getPreferredSize(table, cell, columnIndex, rowIndex).getRows();
                int listOffset = rowIndex - viewTopRow;
                if(preferredRowSizes.size() == listOffset) {
                    preferredRowSizes.add(rowSize);
                }
                else {
                    if(preferredRowSizes.get(listOffset) < rowSize) {
                        preferredRowSizes.set(listOffset, rowSize);
                    }
                }
            }
        }

        int preferredRowSize = 0;
        int preferredColumnSize = 0;
        for(int size: preferredColumnSizes) {
            preferredColumnSize += size;
        }
        for(int size: preferredRowSizes) {
            preferredRowSize += size;
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

        //Add on space taken by scrollbars (if needed)
        if(visibleRows < rows.size()) {
            preferredColumnSize++;
        }
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

        List<Integer> columnSizes = fitColumnsInAvailableSpace(table, area);
        int topPosition = drawHeader(graphics, table, columnSizes);
        drawRows(graphics, table, topPosition, columnSizes);
    }

    private List<Integer> fitColumnsInAvailableSpace(Table<V> table, TerminalSize area) {
        List<Integer> columnSizes = new ArrayList<Integer>(preferredColumnSizes);
        int horizontalSpaceRequirement = 0;
        int viewLeftColumn = table.getViewLeftColumn();
        List<String> headers = table.getTableModel().getColumnLabels();
        int visibleColumns = table.getVisibleColumns();
        if(visibleColumns == 0) {
            visibleColumns = table.getTableModel().getColumnCount();
        }
        int endColumnIndex = Math.min(headers.size(), viewLeftColumn + visibleColumns);
        List<Integer> visibleExpandableColumns = new ArrayList<Integer>();
        for(int index = viewLeftColumn; index < endColumnIndex; index++) {
            horizontalSpaceRequirement += preferredColumnSizes.get(index - viewLeftColumn);
            if(headerHorizontalBorderStyle != TableCellBorderStyle.None && index < (endColumnIndex - 1)) {
                horizontalSpaceRequirement++;
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

    private int drawHeader(TextGUIGraphics graphics, Table<V> table, List<Integer> columnSizes) {
        Theme theme = table.getTheme();
        TableHeaderRenderer<V> tableHeaderRenderer = table.getTableHeaderRenderer();
        List<String> headers = table.getTableModel().getColumnLabels();
        int viewLeftColumn = table.getViewLeftColumn();
        int visibleColumns = table.getVisibleColumns();
        if(visibleColumns == 0) {
            visibleColumns = table.getTableModel().getColumnCount();
        }
        int topPosition = 0;
        int leftPosition = 0;
        int endColumnIndex = Math.min(headers.size(), viewLeftColumn + visibleColumns);
        for(int index = viewLeftColumn; index < endColumnIndex; index++) {
            String label = headers.get(index);
            TerminalSize size = new TerminalSize(columnSizes.get(index - viewLeftColumn), headerSizeInRows);
            tableHeaderRenderer.drawHeader(table, label, index, graphics.newTextGraphics(new TerminalPosition(leftPosition, 0), size));
            leftPosition += size.getColumns();
            if(headerHorizontalBorderStyle != TableCellBorderStyle.None && index < (endColumnIndex - 1)) {
                graphics.applyThemeStyle(theme.getDefinition(Table.class).getNormal());
                graphics.setCharacter(leftPosition, 0, getVerticalCharacter(headerHorizontalBorderStyle));
                leftPosition++;
            }
        }
        topPosition += headerSizeInRows;

        if(headerVerticalBorderStyle != TableCellBorderStyle.None) {
            leftPosition = 0;
            graphics.applyThemeStyle(theme.getDefinition(Table.class).getNormal());
            for(int i = 0; i < columnSizes.size(); i++) {
                if(i > 0) {
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
            topPosition++;
        }
        return topPosition;
    }

    private void drawRows(TextGUIGraphics graphics, Table<V> table, int topPosition, List<Integer> columnSizes) {
        Theme theme = table.getTheme();
        ThemeDefinition themeDefinition = theme.getDefinition(Table.class);
        TerminalSize area = graphics.getSize();
        TableCellRenderer<V> tableCellRenderer = table.getTableCellRenderer();
        TableModel<V> tableModel = table.getTableModel();
        List<List<V>> rows = tableModel.getRows();
        int viewTopRow = table.getViewTopRow();
        int viewLeftColumn = table.getViewLeftColumn();
        int visibleRows = table.getVisibleRows();
        int visibleColumns = table.getVisibleColumns();
        if(visibleColumns == 0) {
            visibleColumns = tableModel.getColumnCount();
        }
        if(visibleRows == 0) {
            visibleRows = tableModel.getRowCount();
        }

        //Draw scrollbars (if needed)
        if(visibleRows < rows.size()) {
            TerminalSize verticalScrollBarPreferredSize = verticalScrollBar.getPreferredSize();
            int scrollBarHeight = graphics.getSize().getRows() - topPosition;
            if(visibleColumns < tableModel.getColumnCount()) {
                scrollBarHeight--;
            }
            verticalScrollBar.setPosition(new TerminalPosition(graphics.getSize().getColumns() - verticalScrollBarPreferredSize.getColumns(), topPosition));
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
        if(visibleColumns < tableModel.getColumnCount()) {
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

        int leftPosition;
        for(int rowIndex = viewTopRow; rowIndex < Math.min(viewTopRow + visibleRows, rows.size()); rowIndex++) {
            leftPosition = 0;
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
                TerminalSize cellArea = new TerminalSize(columnSizes.get(columnIndex - viewLeftColumn), preferredRowSizes.get(rowIndex - viewTopRow));
                tableCellRenderer.drawCell(table, cell, columnIndex, rowIndex, graphics.newTextGraphics(cellPosition, cellArea));
                leftPosition += cellArea.getColumns();
                if(leftPosition > area.getColumns()) {
                    break;
                }
            }
            topPosition += preferredRowSizes.get(rowIndex - viewTopRow);
            if(cellVerticalBorderStyle != TableCellBorderStyle.None) {
                leftPosition = 0;
                graphics.applyThemeStyle(themeDefinition.getNormal());
                for(int i = 0; i < columnSizes.size(); i++) {
                    if(i > 0) {
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
                topPosition += 1;
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
