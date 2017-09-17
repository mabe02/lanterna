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
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;

import java.util.*;

/**
 * This emulates the behaviour of the GridLayout in SWT (as opposed to the one in AWT/Swing). I originally ported the
 * SWT class itself but due to licensing concerns (the eclipse license is not compatible with LGPL) I was advised not to
 * do that. This is a partial implementation and some of the semantics have changed, but in general it works the same
 * way so the SWT documentation will generally match.
 * <p>
 * You use the {@code GridLayout} by specifying a number of columns you want your grid to have and then when you add
 * components, you assign {@code LayoutData} to these components using the different static methods in this class
 * ({@code createLayoutData(..)}). You can set components to span both rows and columns, as well as defining how to
 * distribute the available space.
 */
public class GridLayout implements LayoutManager {
    /**
     * The enum is used to specify where in a grid cell a component should be placed, in the case that the preferred
     * size of the component is smaller than the space in the cell. This class will generally use two alignments, one
     * for horizontal and one for vertical.
     */
    public enum Alignment {
        /**
         * Place the component at the start of the cell (horizontally or vertically) and leave whatever space is left
         * after the preferred size empty.
         */
        BEGINNING,
        /**
         * Place the component at the middle of the cell (horizontally or vertically) and leave the space before and
         * after empty.
         */
        CENTER,
        /**
         * Place the component at the end of the cell (horizontally or vertically) and leave whatever space is left
         * before the preferred size empty.
         */
        END,
        /**
         * Force the component to be the same size as the table cell
         */
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

    private static final GridLayoutData DEFAULT = new GridLayoutData(
            Alignment.BEGINNING,
            Alignment.BEGINNING,
            false,
            false,
            1,
            1);

    /**
     * Creates a layout data object for {@code GridLayout}:s that specify the horizontal and vertical alignment for the
     * component in case the cell space is larger than the preferred size of the component
     * @param horizontalAlignment Horizontal alignment strategy
     * @param verticalAlignment Vertical alignment strategy
     * @return The layout data object containing the specified alignments
     */
    public static LayoutData createLayoutData(Alignment horizontalAlignment, Alignment verticalAlignment) {
        return createLayoutData(horizontalAlignment, verticalAlignment, false, false);
    }

    /**
     * Creates a layout data object for {@code GridLayout}:s that specify the horizontal and vertical alignment for the
     * component in case the cell space is larger than the preferred size of the component. This method also has fields
     * for indicating that the component would like to take more space if available to the container. For example, if
     * the container is assigned is assigned an area of 50x15, but all the child components in the grid together only
     * asks for 40x10, the remaining 10 columns and 5 rows will be empty. If just a single component asks for extra
     * space horizontally and/or vertically, the grid will expand out to fill the entire area and the text space will be
     * assigned to the component that asked for it.
     *
     * @param horizontalAlignment Horizontal alignment strategy
     * @param verticalAlignment Vertical alignment strategy
     * @param grabExtraHorizontalSpace If set to {@code true}, this component will ask to be assigned extra horizontal
     *                                 space if there is any to assign
     * @param grabExtraVerticalSpace If set to {@code true}, this component will ask to be assigned extra vertical
     *                                 space if there is any to assign
     * @return The layout data object containing the specified alignments and size requirements
     */
    public static LayoutData createLayoutData(
            Alignment horizontalAlignment,
            Alignment verticalAlignment,
            boolean grabExtraHorizontalSpace,
            boolean grabExtraVerticalSpace) {

        return createLayoutData(horizontalAlignment, verticalAlignment, grabExtraHorizontalSpace, grabExtraVerticalSpace, 1, 1);
    }

    /**
     * Creates a layout data object for {@code GridLayout}:s that specify the horizontal and vertical alignment for the
     * component in case the cell space is larger than the preferred size of the component. This method also has fields
     * for indicating that the component would like to take more space if available to the container. For example, if
     * the container is assigned is assigned an area of 50x15, but all the child components in the grid together only
     * asks for 40x10, the remaining 10 columns and 5 rows will be empty. If just a single component asks for extra
     * space horizontally and/or vertically, the grid will expand out to fill the entire area and the text space will be
     * assigned to the component that asked for it. It also puts in data on how many rows and/or columns the component
     * should span.
     *
     * @param horizontalAlignment Horizontal alignment strategy
     * @param verticalAlignment Vertical alignment strategy
     * @param grabExtraHorizontalSpace If set to {@code true}, this component will ask to be assigned extra horizontal
     *                                 space if there is any to assign
     * @param grabExtraVerticalSpace If set to {@code true}, this component will ask to be assigned extra vertical
     *                                 space if there is any to assign
     * @param horizontalSpan How many "cells" this component wants to span horizontally
     * @param verticalSpan How many "cells" this component wants to span vertically
     * @return The layout data object containing the specified alignments, size requirements and cell spanning
     */
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

    /**
     * This is a shortcut method that will create a grid layout data object that will expand its cell as much as is can
     * horizontally and make the component occupy the whole area horizontally and center it vertically
     * @param horizontalSpan How many cells to span horizontally
     * @return Layout data object with the specified span and horizontally expanding as much as it can
     */
    public static LayoutData createHorizontallyFilledLayoutData(int horizontalSpan) {
        return createLayoutData(
                Alignment.FILL,
                Alignment.CENTER,
                true,
                false,
                horizontalSpan,
                1);
    }

    /**
     * This is a shortcut method that will create a grid layout data object that will expand its cell as much as is can
     * vertically and make the component occupy the whole area vertically and center it horizontally
     * @param horizontalSpan How many cells to span vertically
     * @return Layout data object with the specified span and vertically expanding as much as it can
     */
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

    /**
     * Creates a new {@code GridLayout} with the specified number of columns. Initially, this layout will have a
     * horizontal spacing of 1 and vertical spacing of 0, with a left and right margin of 1.
     * @param numberOfColumns Number of columns in this grid
     */
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

    /**
     * Returns the horizontal spacing, i.e. the number of empty columns between each cell
     * @return Horizontal spacing
     */
    public int getHorizontalSpacing() {
        return horizontalSpacing;
    }

    /**
     * Sets the horizontal spacing, i.e. the number of empty columns between each cell
     * @param horizontalSpacing New horizontal spacing
     * @return Itself
     */
    public GridLayout setHorizontalSpacing(int horizontalSpacing) {
        if(horizontalSpacing < 0) {
            throw new IllegalArgumentException("Horizontal spacing cannot be less than 0");
        }
        this.horizontalSpacing = horizontalSpacing;
        this.changed = true;
        return this;
    }

    /**
     * Returns the vertical spacing, i.e. the number of empty columns between each row
     * @return Vertical spacing
     */
    public int getVerticalSpacing() {
        return verticalSpacing;
    }

    /**
     * Sets the vertical spacing, i.e. the number of empty columns between each row
     * @param verticalSpacing New vertical spacing
     * @return Itself
     */
    public GridLayout setVerticalSpacing(int verticalSpacing) {
        if(verticalSpacing < 0) {
            throw new IllegalArgumentException("Vertical spacing cannot be less than 0");
        }
        this.verticalSpacing = verticalSpacing;
        this.changed = true;
        return this;
    }

    /**
     * Returns the top margin, i.e. number of empty rows above the first row in the grid
     * @return Top margin, in number of rows
     */
    public int getTopMarginSize() {
        return topMarginSize;
    }

    /**
     * Sets the top margin, i.e. number of empty rows above the first row in the grid
     * @param topMarginSize Top margin, in number of rows
     * @return Itself
     */
    public GridLayout setTopMarginSize(int topMarginSize) {
        if(topMarginSize < 0) {
            throw new IllegalArgumentException("Top margin size cannot be less than 0");
        }
        this.topMarginSize = topMarginSize;
        this.changed = true;
        return this;
    }

    /**
     * Returns the bottom margin, i.e. number of empty rows below the last row in the grid
     * @return Bottom margin, in number of rows
     */
    public int getBottomMarginSize() {
        return bottomMarginSize;
    }

    /**
     * Sets the bottom margin, i.e. number of empty rows below the last row in the grid
     * @param bottomMarginSize Bottom margin, in number of rows
     * @return Itself
     */
    public GridLayout setBottomMarginSize(int bottomMarginSize) {
        if(bottomMarginSize < 0) {
            throw new IllegalArgumentException("Bottom margin size cannot be less than 0");
        }
        this.bottomMarginSize = bottomMarginSize;
        this.changed = true;
        return this;
    }

    /**
     * Returns the left margin, i.e. number of empty columns left of the first column in the grid
     * @return Left margin, in number of columns
     */
    public int getLeftMarginSize() {
        return leftMarginSize;
    }

    /**
     * Sets the left margin, i.e. number of empty columns left of the first column in the grid
     * @param leftMarginSize Left margin, in number of columns
     * @return Itself
     */
    public GridLayout setLeftMarginSize(int leftMarginSize) {
        if(leftMarginSize < 0) {
            throw new IllegalArgumentException("Left margin size cannot be less than 0");
        }
        this.leftMarginSize = leftMarginSize;
        this.changed = true;
        return this;
    }

    /**
     * Returns the right margin, i.e. number of empty columns right of the last column in the grid
     * @return Right margin, in number of columns
     */
    public int getRightMarginSize() {
        return rightMarginSize;
    }

    /**
     * Sets the right margin, i.e. number of empty columns right of the last column in the grid
     * @param rightMarginSize Right margin, in number of columns
     * @return Itself
     */
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
            changed = false;
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
            for(Component component : row) {
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
        for(int rowIndex = 0; rowIndex < table.length; rowIndex++) {
            Component[] row = table[rowIndex];
            for(Component cell : row) {
                if(cell == null) {
                    continue;
                }
                GridLayoutData layoutData = getLayoutData(cell);
                if(layoutData.grabExtraVerticalSpace) {
                    expandableRows.add(rowIndex);
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
            for(Component[] row : table) {
                if(row[column] != row[column - 1]) {
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
        for(Component[] row : table) {
            Component[] newColumn = new Component[tableColumns - columnsToRemove.size()];
            int insertedColumnCounter = 0;
            for(int column = 0; column < tableColumns; column++) {
                if(columnsToRemove.contains(column)) {
                    continue;
                }
                newColumn[insertedColumnCounter++] = row[column];
            }
            newTable[insertedRowCounter++] = newColumn;
        }
        return newTable;
    }

    private GridLayoutData getLayoutData(Component component) {
        LayoutData layoutData = component.getLayoutData();
        if(layoutData instanceof GridLayoutData) {
            return (GridLayoutData)layoutData;
        }
        else {
            return DEFAULT;
        }
    }
}
