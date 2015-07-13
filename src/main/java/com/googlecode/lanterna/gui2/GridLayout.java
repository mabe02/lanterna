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
 * Copyright (C) 2010-2015 Martin
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;

import java.util.*;

/**
 * This emulates the behaviour of the GridLayout in SWT (as opposed to the one in AWT/Swing). I originally ported the
 * SWT class itself but due to licensing concerns (the eclipse license is not compatible with LGPL) I was advised not to
 * do that. This is a partial implementation and some of the semantics have changed, but in general it works the same
 * way.
 */
public class GridLayout implements LayoutManager {
    public enum Alignment {
        BEGINNING,
        CENTER,
        END,
        FILL,
        ;
    }

    static class GridLayoutData implements LayoutData {
        final Alignment horizontalAlignment;
        final Alignment verticalAlignment;
        final boolean grabExtraHorizontalSpace;
        final boolean grabExtraVerticalSpace;
        final int horizontalSpan;
        final int verticalSpan;

        private GridLayoutData(
                Alignment horizontalAlignment,
                Alignment verticalAlignment,
                boolean grabExtraHorizontalSpace,
                boolean grabExtraVerticalSpace,
                int horizontalSpan,
                int verticalSpan) {

            if(horizontalSpan < 1 || verticalSpan < 1) {
                throw new IllegalArgumentException("Horizontal/Vertical span must be 1 or greater");
            }

            this.horizontalAlignment = horizontalAlignment;
            this.verticalAlignment = verticalAlignment;
            this.grabExtraHorizontalSpace = grabExtraHorizontalSpace;
            this.grabExtraVerticalSpace = grabExtraVerticalSpace;
            this.horizontalSpan = horizontalSpan;
            this.verticalSpan = verticalSpan;
        }
    }

    private static GridLayoutData DEFAULT = new GridLayoutData(
            Alignment.BEGINNING,
            Alignment.BEGINNING,
            false,
            false,
            1,
            1);

    public static LayoutData createLayoutData(Alignment horizontalAlignment, Alignment verticalAlignment) {
        return createLayoutData(horizontalAlignment, verticalAlignment, false, false);
    }

    public static LayoutData createLayoutData(
            Alignment horizontalAlignment,
            Alignment verticalAlignment,
            boolean grabExtraHorizontalSpace,
            boolean grabExtraVerticalSpace) {

        return createLayoutData(horizontalAlignment, verticalAlignment, grabExtraHorizontalSpace, grabExtraVerticalSpace, 1, 1);
    }

    public static LayoutData createLayoutData(
            Alignment horizontalAlignment,
            Alignment verticalAlignment,
            boolean grabExtraHorizontalSpace,
            boolean grabExtraVerticalSpace,
            int horizontalSpan,
            int verticalSpan) {

        return new GridLayoutData(
                horizontalAlignment,
                verticalAlignment,
                grabExtraHorizontalSpace,
                grabExtraVerticalSpace,
                horizontalSpan,
                verticalSpan);
    }

    public static LayoutData createHorizontallyFilledLayoutData(int horizontalSpan) {
        return createLayoutData(
                Alignment.FILL,
                Alignment.CENTER,
                true,
                false,
                horizontalSpan,
                1);
    }

    public static LayoutData createHorizontallyEndAlignedLayoutData(int horizontalSpan) {
        return createLayoutData(
                Alignment.END,
                Alignment.CENTER,
                true,
                false,
                horizontalSpan,
                1);
    }

    private final int numberOfColumns;
    private int horizontalSpacing;
    private int verticalSpacing;
    private int topMarginSize;
    private int bottomMarginSize;
    private int leftMarginSize;
    private int rightMarginSize;

    private boolean changed;

    public GridLayout(int numberOfColumns) {
        this.numberOfColumns = numberOfColumns;
        this.horizontalSpacing = 1;
        this.verticalSpacing = 0;
        this.topMarginSize = 0;
        this.bottomMarginSize = 0;
        this.leftMarginSize = 1;
        this.rightMarginSize = 1;
        this.changed = true;
    }

    public int getHorizontalSpacing() {
        return horizontalSpacing;
    }

    public GridLayout setHorizontalSpacing(int horizontalSpacing) {
        if(horizontalSpacing < 0) {
            throw new IllegalArgumentException("Horizontal spacing cannot be less than 0");
        }
        this.horizontalSpacing = horizontalSpacing;
        this.changed = true;
        return this;
    }

    public int getVerticalSpacing() {
        return verticalSpacing;
    }

    public GridLayout setVerticalSpacing(int verticalSpacing) {
        if(verticalSpacing < 0) {
            throw new IllegalArgumentException("Vertical spacing cannot be less than 0");
        }
        this.verticalSpacing = verticalSpacing;
        this.changed = true;
        return this;
    }

    public int getTopMarginSize() {
        return topMarginSize;
    }

    public GridLayout setTopMarginSize(int topMarginSize) {
        if(topMarginSize < 0) {
            throw new IllegalArgumentException("Top margin size cannot be less than 0");
        }
        this.topMarginSize = topMarginSize;
        this.changed = true;
        return this;
    }

    public int getBottomMarginSize() {
        return bottomMarginSize;
    }

    public GridLayout setBottomMarginSize(int bottomMarginSize) {
        if(bottomMarginSize < 0) {
            throw new IllegalArgumentException("Bottom margin size cannot be less than 0");
        }
        this.bottomMarginSize = bottomMarginSize;
        this.changed = true;
        return this;
    }

    public int getLeftMarginSize() {
        return leftMarginSize;
    }

    public GridLayout setLeftMarginSize(int leftMarginSize) {
        if(leftMarginSize < 0) {
            throw new IllegalArgumentException("Left margin size cannot be less than 0");
        }
        this.leftMarginSize = leftMarginSize;
        this.changed = true;
        return this;
    }

    public int getRightMarginSize() {
        return rightMarginSize;
    }

    public GridLayout setRightMarginSize(int rightMarginSize) {
        if(rightMarginSize < 0) {
            throw new IllegalArgumentException("Right margin size cannot be less than 0");
        }
        this.rightMarginSize = rightMarginSize;
        this.changed = true;
        return this;
    }

    @Override
    public boolean hasChanged() {
        return this.changed;
    }

    @Override
    public TerminalSize getPreferredSize(List<Component> components) {
        TerminalSize preferredSize = TerminalSize.ZERO;
        if(components.isEmpty()) {
            return preferredSize.withRelative(
                    leftMarginSize + rightMarginSize,
                    topMarginSize + bottomMarginSize);
        }

        Component[][] table = buildTable(components);
        table = eliminateUnusedRowsAndColumns(table);

        //Figure out each column first, this can be done independently of the row heights
        int preferredWidth = 0;
        int preferredHeight = 0;
        for(int width: getPreferredColumnWidths(table)) {
            preferredWidth += width;
        }
        for(int height: getPreferredRowHeights(table)) {
            preferredHeight += height;
        }
        preferredSize = preferredSize.withRelative(preferredWidth, preferredHeight);
        preferredSize = preferredSize.withRelativeColumns(leftMarginSize + rightMarginSize + (table[0].length - 1) * horizontalSpacing);
        preferredSize = preferredSize.withRelativeRows(topMarginSize + bottomMarginSize + (table.length - 1) * verticalSpacing);
        return preferredSize;
    }

    @Override
    public void doLayout(TerminalSize area, List<Component> components) {
        //Sanity check, if the area is way too small, just return
        Component[][] table = buildTable(components);
        table = eliminateUnusedRowsAndColumns(table);

        if(area.equals(TerminalSize.ZERO) ||
                table.length == 0 ||
                area.getColumns() <= leftMarginSize + rightMarginSize + ((table[0].length - 1) * horizontalSpacing) ||
                area.getRows() <= bottomMarginSize + topMarginSize + ((table.length - 1) * verticalSpacing)) {
            return;
        }

        //Adjust area to the margins
        area = area.withRelative(-leftMarginSize - rightMarginSize, -topMarginSize - bottomMarginSize);

        Map<Component, TerminalSize> sizeMap = new IdentityHashMap<Component, TerminalSize>();
        Map<Component, TerminalPosition> positionMap = new IdentityHashMap<Component, TerminalPosition>();

        //Figure out each column first, this can be done independently of the row heights
        int[] columnWidths = getPreferredColumnWidths(table);

        //Take notes of which columns we can expand if the usable area is larger than what the components want
        Set<Integer> expandableColumns = getExpandableColumns(table);

        //Next, start shrinking to make sure it fits the size of the area we are trying to lay out on.
        //Notice we subtract the horizontalSpacing to take the space between components into account
        TerminalSize areaWithoutHorizontalSpacing = area.withRelativeColumns(-horizontalSpacing * (table[0].length - 1));
        int totalWidth = shrinkWidthToFitArea(areaWithoutHorizontalSpacing, columnWidths);

        //Finally, if there is extra space, make the expandable columns larger
        while(areaWithoutHorizontalSpacing.getColumns() > totalWidth && !expandableColumns.isEmpty()) {
            totalWidth = grabExtraHorizontalSpace(areaWithoutHorizontalSpacing, columnWidths, expandableColumns, totalWidth);
        }

        //Now repeat for rows
        int[] rowHeights = getPreferredRowHeights(table);
        Set<Integer> expandableRows = getExpandableRows(table);
        TerminalSize areaWithoutVerticalSpacing = area.withRelativeRows(-verticalSpacing * (table.length - 1));
        int totalHeight = shrinkHeightToFitArea(areaWithoutVerticalSpacing, rowHeights);
        while(areaWithoutVerticalSpacing.getRows() > totalHeight && !expandableRows.isEmpty()) {
            totalHeight = grabExtraVerticalSpace(areaWithoutVerticalSpacing, rowHeights, expandableRows, totalHeight);
        }

        //Ok, all constraints are in place, we can start placing out components. To simplify, do it horizontally first
        //and vertically after
        TerminalPosition tableCellTopLeft = TerminalPosition.TOP_LEFT_CORNER;
        for(int y = 0; y < table.length; y++) {
            tableCellTopLeft = tableCellTopLeft.withColumn(0);
            for(int x = 0; x < table[y].length; x++) {
                Component component = table[y][x];
                if(component != null && !positionMap.containsKey(component)) {
                    GridLayoutData layoutData = getLayoutData(component);
                    TerminalSize size = component.getPreferredSize();
                    TerminalPosition position = tableCellTopLeft;

                    int availableHorizontalSpace = 0;
                    int availableVerticalSpace = 0;
                    for (int i = 0; i < layoutData.horizontalSpan; i++) {
                        availableHorizontalSpace += columnWidths[x + i] + (i > 0 ? horizontalSpacing : 0);
                    }
                    for (int i = 0; i < layoutData.verticalSpan; i++) {
                        availableVerticalSpace += rowHeights[y + i]  + (i > 0 ? verticalSpacing : 0);
                    }

                    //Make sure to obey the size restrictions
                    size = size.withColumns(Math.min(size.getColumns(), availableHorizontalSpace));
                    size = size.withRows(Math.min(size.getRows(), availableVerticalSpace));

                    switch (layoutData.horizontalAlignment) {
                        case CENTER:
                            position = position.withRelativeColumn((availableHorizontalSpace - size.getColumns()) / 2);
                            break;
                        case END:
                            position = position.withRelativeColumn(availableHorizontalSpace - size.getColumns());
                            break;
                        case FILL:
                            size = size.withColumns(availableHorizontalSpace);
                            break;
                        default:
                            break;
                    }
                    switch (layoutData.verticalAlignment) {
                        case CENTER:
                            position = position.withRelativeRow((availableVerticalSpace - size.getRows()) / 2);
                            break;
                        case END:
                            position = position.withRelativeRow(availableVerticalSpace - size.getRows());
                            break;
                        case FILL:
                            size = size.withRows(availableVerticalSpace);
                            break;
                        default:
                            break;
                    }

                    sizeMap.put(component, size);
                    positionMap.put(component, position);
                }
                tableCellTopLeft = tableCellTopLeft.withRelativeColumn(columnWidths[x] + horizontalSpacing);
            }
            tableCellTopLeft = tableCellTopLeft.withRelativeRow(rowHeights[y] + verticalSpacing);
        }

        //Apply the margins here
        for(Component component: components) {
            component.setPosition(positionMap.get(component).withRelative(leftMarginSize, topMarginSize));
            component.setSize(sizeMap.get(component));
        }
        this.changed = false;
    }

    private int[] getPreferredColumnWidths(Component[][] table) {
        //actualNumberOfColumns may be different from this.numberOfColumns since some columns may have been eliminated
        int actualNumberOfColumns = table[0].length;
        int columnWidths[] = new int[actualNumberOfColumns];

        //Start by letting all span = 1 columns take what they need
        for(Component[] row: table) {
            for(int i = 0; i < actualNumberOfColumns; i++) {
                Component component = row[i];
                if(component == null) {
                    continue;
                }
                GridLayoutData layoutData = getLayoutData(component);
                if (layoutData.horizontalSpan == 1) {
                    columnWidths[i] = Math.max(columnWidths[i], component.getPreferredSize().getColumns());
                }
            }
        }

        //Next, do span > 1 and enlarge if necessary
        for(Component[] row: table) {
            for(int i = 0; i < actualNumberOfColumns; ) {
                Component component = row[i];
                if(component == null) {
                    i++;
                    continue;
                }
                GridLayoutData layoutData = getLayoutData(component);
                if(layoutData.horizontalSpan > 1) {
                    int accumWidth = 0;
                    for(int j = i; j < i + layoutData.horizontalSpan; j++) {
                        accumWidth += columnWidths[j];
                    }

                    int preferredWidth = component.getPreferredSize().getColumns();
                    if(preferredWidth > accumWidth) {
                        int columnOffset = 0;
                        do {
                            columnWidths[i + columnOffset++]++;
                            accumWidth++;
                            if(columnOffset == layoutData.horizontalSpan) {
                                columnOffset = 0;
                            }
                        }
                        while(preferredWidth > accumWidth);
                    }
                }
                i += layoutData.horizontalSpan;
            }
        }
        return columnWidths;
    }

    private int[] getPreferredRowHeights(Component[][] table) {
        int numberOfRows = table.length;
        int rowHeights[] = new int[numberOfRows];

        //Start by letting all span = 1 rows take what they need
        int rowIndex = 0;
        for(Component[] row: table) {
            for(int i = 0; i < row.length; i++) {
                Component component = row[i];
                if(component == null) {
                    continue;
                }
                GridLayoutData layoutData = getLayoutData(component);
                if(layoutData.verticalSpan == 1) {
                    rowHeights[rowIndex] = Math.max(rowHeights[rowIndex], component.getPreferredSize().getRows());
                }
            }
            rowIndex++;
        }

        //Next, do span > 1 and enlarge if necessary
        for(int x = 0; x < numberOfColumns; x++) {
            for(int y = 0; y < numberOfRows && y < table.length; ) {
                if(x >= table[y].length) {
                    y++;
                    continue;
                }
                Component component = table[y][x];
                if(component == null) {
                    y++;
                    continue;
                }
                GridLayoutData layoutData = getLayoutData(component);
                if(layoutData.verticalSpan > 1) {
                    int accumulatedHeight = 0;
                    for(int i = y; i < y + layoutData.verticalSpan; i++) {
                        accumulatedHeight += rowHeights[i];
                    }

                    int preferredHeight = component.getPreferredSize().getRows();
                    if(preferredHeight > accumulatedHeight) {
                        int rowOffset = 0;
                        do {
                            rowHeights[y + rowOffset++]++;
                            accumulatedHeight++;
                            if(rowOffset == layoutData.verticalSpan) {
                                rowOffset = 0;
                            }
                        }
                        while(preferredHeight > accumulatedHeight);
                    }
                }
                y += layoutData.verticalSpan;
            }
        }
        return rowHeights;
    }

    private Set<Integer> getExpandableColumns(Component[][] table) {
        Set<Integer> expandableColumns = new TreeSet<Integer>();
        for(Component[] row: table) {
            for (int i = 0; i < row.length; i++) {
                if(row[i] == null) {
                    continue;
                }
                GridLayoutData layoutData = getLayoutData(row[i]);
                if(layoutData.grabExtraHorizontalSpace) {
                    expandableColumns.add(i);
                }
            }
        }
        return expandableColumns;
    }

    private Set<Integer> getExpandableRows(Component[][] table) {
        Set<Integer> expandableRows = new TreeSet<Integer>();
        for(Component[] row: table) {
            for (int i = 0; i < row.length; i++) {
                if(row[i] == null) {
                    continue;
                }
                GridLayoutData layoutData = getLayoutData(row[i]);
                if(layoutData.grabExtraVerticalSpace) {
                    expandableRows.add(i);
                }
            }
        }
        return expandableRows;
    }

    private int shrinkWidthToFitArea(TerminalSize area, int[] columnWidths) {
        int totalWidth = 0;
        for(int width: columnWidths) {
            totalWidth += width;
        }
        if(totalWidth > area.getColumns()) {
            int columnOffset = 0;
            do {
                if(columnWidths[columnOffset] > 0) {
                    columnWidths[columnOffset]--;
                    totalWidth--;
                }
                if(++columnOffset == numberOfColumns) {
                    columnOffset = 0;
                }
            }
            while(totalWidth > area.getColumns());
        }
        return totalWidth;
    }

    private int shrinkHeightToFitArea(TerminalSize area, int[] rowHeights) {
        int totalHeight = 0;
        for(int height: rowHeights) {
            totalHeight += height;
        }
        if(totalHeight > area.getRows()) {
            int rowOffset = 0;
            do {
                if(rowHeights[rowOffset] > 0) {
                    rowHeights[rowOffset]--;
                    totalHeight--;
                }
                if(++rowOffset == rowHeights.length) {
                    rowOffset = 0;
                }
            }
            while(totalHeight > area.getRows());
        }
        return totalHeight;
    }

    private int grabExtraHorizontalSpace(TerminalSize area, int[] columnWidths, Set<Integer> expandableColumns, int totalWidth) {
        for(int columnIndex: expandableColumns) {
            columnWidths[columnIndex]++;
            totalWidth++;
            if(area.getColumns() == totalWidth) {
                break;
            }
        }
        return totalWidth;
    }

    private int grabExtraVerticalSpace(TerminalSize area, int[] rowHeights, Set<Integer> expandableRows, int totalHeight) {
        for(int rowIndex: expandableRows) {
            rowHeights[rowIndex]++;
            totalHeight++;
            if(area.getColumns() == totalHeight) {
                break;
            }
        }
        return totalHeight;
    }

    private Component[][] buildTable(List<Component> components) {
        List<Component[]> rows = new ArrayList<Component[]>();
        List<int[]> hspans = new ArrayList<int[]>();
        List<int[]> vspans = new ArrayList<int[]>();

        int rowCount = 0;
        int rowsExtent = 1;
        Queue<Component> toBePlaced = new LinkedList<Component>(components);
        while(!toBePlaced.isEmpty() || rowCount < rowsExtent) {
            //Start new row
            Component[] row = new Component[numberOfColumns];
            int[] hspan = new int[numberOfColumns];
            int[] vspan = new int[numberOfColumns];

            for(int i = 0; i < numberOfColumns; i++) {
                if(i > 0 && hspan[i - 1] > 1) {
                    row[i] = row[i-1];
                    hspan[i] = hspan[i - 1] - 1;
                    vspan[i] = vspan[i - 1];
                }
                else if(rowCount > 0 && vspans.get(rowCount - 1)[i] > 1) {
                    row[i] = rows.get(rowCount - 1)[i];
                    hspan[i] = hspans.get(rowCount - 1)[i];
                    vspan[i] = vspans.get(rowCount - 1)[i] - 1;
                }
                else if(!toBePlaced.isEmpty()) {
                    Component component = toBePlaced.poll();
                    GridLayoutData gridLayoutData = getLayoutData(component);

                    row[i] = component;
                    hspan[i] = gridLayoutData.horizontalSpan;
                    vspan[i] = gridLayoutData.verticalSpan;
                    rowsExtent = Math.max(rowsExtent, rowCount + gridLayoutData.verticalSpan);
                }
                else {
                    row[i] = null;
                    hspan[i] = 1;
                    vspan[i] = 1;
                }
            }

            rows.add(row);
            hspans.add(hspan);
            vspans.add(vspan);
            rowCount++;
        }
        return rows.toArray(new Component[rows.size()][]);
    }

    private Component[][] eliminateUnusedRowsAndColumns(Component[][] table) {
        if(table.length == 0) {
            return table;
        }
        //Could make this into a Set, but I doubt there will be any real gain in performance as these are probably going
        //to be very small.
        List<Integer> rowsToRemove = new ArrayList<Integer>();
        List<Integer> columnsToRemove = new ArrayList<Integer>();

        final int tableRows = table.length;
        final int tableColumns = table[0].length;

        //Scan for unnecessary columns
        columnLoop:
        for(int column = tableColumns - 1; column > 0; column--) {
            for(int row = 0; row < tableRows; row++) {
                if(table[row][column] != table[row][column - 1]) {
                   continue columnLoop;
                }
            }
            columnsToRemove.add(column);
        }

        //Scan for unnecessary rows
        rowLoop:
        for(int row = tableRows - 1; row > 0; row--) {
            for(int column = 0; column < tableColumns; column++) {
                if(table[row][column] != table[row - 1][column]) {
                    continue rowLoop;
                }
            }
            rowsToRemove.add(row);
        }

        //If there's nothing to remove, just return the same
        if(rowsToRemove.isEmpty() && columnsToRemove.isEmpty()) {
            return table;
        }

        //Build a new table with rows & columns eliminated
        Component[][] newTable = new Component[tableRows - rowsToRemove.size()][];
        int insertedRowCounter = 0;
        for(int row = 0; row < tableRows; row++) {
            Component[] newColumn = new Component[tableColumns - columnsToRemove.size()];
            int insertedColumnCounter = 0;
            for(int column = 0; column < tableColumns; column++) {
                if(columnsToRemove.contains(column)) {
                    continue;
                }
                newColumn[insertedColumnCounter++] = table[row][column];
            }
            newTable[insertedRowCounter++] = newColumn;
        }
        return newTable;
    }

    private GridLayoutData getLayoutData(Component component) {
        LayoutData layoutData = component.getLayoutData();
        if(layoutData == null || !(layoutData instanceof GridLayoutData)) {
            return DEFAULT;
        }
        else {
            return (GridLayoutData)layoutData;
        }
    }
}
