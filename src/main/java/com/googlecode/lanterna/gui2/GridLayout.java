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
 * do that. This is a partial implementation and some of the symantics have changed, but in general it works the same 
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

    private static class GridLayoutData implements LayoutData {
        final Alignment horizontalAlignment;
        final Alignment verticalAlignment;
        final boolean grabExtraHorizontalSpace;
        final boolean grabExtraVerticalSpace;
        final int horizontalSpan;
        final int verticalSpan;

        public GridLayoutData(
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

    private final int numberOfColumns;

    public GridLayout(int numberOfColumns) {
        this.numberOfColumns = numberOfColumns;

    }
    
    @Override
    public TerminalSize getPreferredSize(List<Component> components) {
        TerminalSize preferredSize = TerminalSize.ZERO;
        Component[][] table = buildTable(components);
        for(int row = 0; row < table.length; row++) {
            int rowPreferredHeight = 0;
            int rowPreferredWidth = 0;
            for(int column = 0; column < table[row].length; column++) {
                Component component = table[row][column];
                if(component == null) {
                    continue;
                }
                else if(row > 0 && table[row - 1][column] == component) {
                    continue;
                }
                else if(column > 0 && table[row][column - 1] == component) {
                    continue;
                }
                else {
                    TerminalSize componentPreferredSize = component.getPreferredSize();
                    rowPreferredHeight = Math.max(rowPreferredHeight, componentPreferredSize.getRows());
                    rowPreferredWidth += componentPreferredSize.getColumns();
                }
            }
            preferredSize = preferredSize.withColumns(Math.max(preferredSize.getColumns(), rowPreferredWidth));
            preferredSize = preferredSize.withRelativeRows(rowPreferredHeight);
        }
        return preferredSize;
    }

    @Override
    public void doLayout(TerminalSize area, List<Component> components) {
        Component[][] table = buildTable(components);
        Map<Component, TerminalSize> sizeMap = new IdentityHashMap<Component, TerminalSize>();
        Map<Component, TerminalPosition> positionMap = new IdentityHashMap<Component, TerminalPosition>();


        //Figure out each column first, this can be done independently of the row heights
        int[] columnWidths = getPreferredColumnWidths(table);

        //Take notes of which columns we can expand if the usable area is larger than what the components want
        Set<Integer> expandableColumns = getExpandableColumns(table);

        //Next, start shrinking to make sure it fits the size of the area we are trying to lay out on
        int totalWidth = shrinkWidthToFitArea(area, columnWidths);

        //Finally, if there is extra space, make the expandable columns larger
        while(area.getColumns() > totalWidth && !expandableColumns.isEmpty()) {
            totalWidth = grabExtraHorizontalSpace(area, columnWidths, expandableColumns, totalWidth);
        }

        //Now repeat for rows
        int[] rowHeights = getPreferredRowHeights(table);
        Set<Integer> expandableRows = getExpandableRows(table);
        int totalHeight = shrinkHeightToFitArea(area, rowHeights);
        while(area.getRows() > totalHeight && !expandableRows.isEmpty()) {
            totalHeight = grabExtraVerticalSpace(area, rowHeights, expandableRows, totalHeight);
        }

        //Ok, all constraints are in place, we can start placing out components. To simplify, do it horizontally first
        //and vertically after
        TerminalPosition tableCellTopLeft = TerminalPosition.TOP_LEFT_CORNER;
        for(int y = 0; y < table.length; y++) {
            tableCellTopLeft = tableCellTopLeft.withColumn(0);
            for(int x = 0; x < numberOfColumns; x++) {
                Component component = table[y][x];
                if(component != null && !positionMap.containsKey(component)) {
                    GridLayoutData layoutData = getLayoutData(component);
                    TerminalSize size = component.getPreferredSize();
                    TerminalPosition position = tableCellTopLeft;

                    int availableHorizontalSpace = 0;
                    int availableVerticalSpace = 0;
                    for (int i = 0; i < layoutData.horizontalSpan; i++) {
                        availableHorizontalSpace += columnWidths[x + i];
                    }
                    for (int i = 0; i < layoutData.verticalSpan; i++) {
                        availableVerticalSpace += rowHeights[y + i];
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
                    }
                    sizeMap.put(component, size);
                    positionMap.put(component, position);
                }

                tableCellTopLeft = tableCellTopLeft.withRelativeColumn(columnWidths[x]);
            }
            tableCellTopLeft = tableCellTopLeft.withRelativeRow(rowHeights[y]);
        }

        for(Component component: components) {
            component.setPosition(positionMap.get(component));
            component.setSize(sizeMap.get(component));
        }
    }

    private int[] getPreferredColumnWidths(Component[][] table) {
        int columnWidths[] = new int[numberOfColumns];

        //Start by letting all span = 1 columns take what they need
        for(Component[] row: table) {
            for(int i = 0; i < numberOfColumns; i++) {
                Component component = row[i];
                if(component == null) {
                    columnWidths[i] = 0;
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
            for(int i = 0; i < numberOfColumns; ) {
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
            for(int i = 0; i < numberOfColumns; i++) {
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
            for(int y = 0; y < numberOfRows; ) {
                Component component = table[y][x];
                if(component == null) {
                    y++;
                    continue;
                }
                GridLayoutData layoutData = getLayoutData(component);
                if(layoutData.verticalSpan > 1) {
                    int accumHeight = 0;
                    for(int i = y; i < y + layoutData.verticalSpan; i++) {
                        accumHeight += rowHeights[i];
                    }

                    int preferredHeight = component.getPreferredSize().getRows();
                    if(preferredHeight > accumHeight) {
                        int rowOffset = 0;
                        do {
                            rowHeights[y + rowOffset++]++;
                            accumHeight++;
                            if(rowOffset == layoutData.verticalSpan) {
                                rowOffset = 0;
                            }
                        }
                        while(preferredHeight > accumHeight);
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
            for (int i = 0; i < numberOfColumns; i++) {
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
            for (int i = 0; i < numberOfColumns; i++) {
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
        for(int width: rowHeights) {
            totalHeight += width;
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

    private GridLayoutData getLayoutData(Component component) {
        LayoutData layoutData = component.getLayoutData();
        if(layoutData == null || layoutData instanceof GridLayoutData == false) {
            return DEFAULT;
        }
        else {
            return (GridLayoutData)layoutData;
        }
    }
}
