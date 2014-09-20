/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;

import java.util.List;

/**
 * Created by martin on 20/09/14.
 */
public class GridLayout implements LayoutManager {

    public static enum Alignment {
        BEGINNING(SWT.BEGINNING),
        CENTER(SWT.CENTER),
        END(SWT.END),
        FILL(SWT.FILL),
        ;

        private final int swtCode;

        Alignment(int swtCode) {
            this.swtCode = swtCode;
        }
    }

    private static class Point {
        int x, y;

        Point(TerminalSize size) {
            x = size.getColumns();
            y = size.getRows();
        }

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private static class SWT {
        static final int FILL = 4;
        static final int BEGINNING = 1;
        static final int TOP = 128;
        static final int END = 16777224;
        static final int BOTTOM = 1024;
        static final int CENTER = 16777216;
        static final int LEFT = 16384;
        static final int RIGHT = 131072;
        static final int DEFAULT = -1;
    }

    private GridData getGridData(Component control) {
        Object data = control.getLayoutData();
        if(data instanceof GridData) {
            return (GridData)data;
        }
        setLayoutData(control, new GridData());
        return getGridData(control);
    }

    private void setLayoutData(Component child, GridData gridData) {
        child.setLayoutData(gridData);
    }

    public static Object createLayoutData(
            Alignment horizontalAlignment,
            Alignment verticalAlignment,
            boolean expandHorizontally,
            boolean expandVertically) {
        return createLayoutData(horizontalAlignment, verticalAlignment, expandHorizontally, expandVertically, 1, 1);
    }

    public static Object createLayoutData(
            Alignment horizontalAlignment,
            Alignment verticalAlignment,
            boolean expandHorizontally,
            boolean expandVertically,
            int horizontalSpan,
            int verticalSpan) {
        return new GridData(
                horizontalAlignment.swtCode,
                verticalAlignment.swtCode,
                expandHorizontally,
                expandVertically,
                horizontalSpan,
                verticalSpan);
    }

    /**
     * numColumns specifies the number of cell columns in the layout.
     * If numColumns has a value less than 1, the layout will not
     * set the size and position of any controls.
     * <p/>
     * The default value is 1.
     */
    public int numColumns = 1;

    /**
     * makeColumnsEqualWidth specifies whether all columns in the layout
     * will be forced to have the same width.
     * <p/>
     * The default value is false.
     */
    public boolean makeColumnsEqualWidth = false;

    /**
     * marginWidth specifies the number of columns of horizontal margin
     * that will be placed along the left and right edges of the layout.
     * <p/>
     * The default value is 0.
     */
    public int marginWidth = 0;

    /**
     * marginHeight specifies the number of rows of vertical margin
     * that will be placed along the top and bottom edges of the layout.
     * <p/>
     * The default value is 0.
     */
    public int marginHeight = 0;

    /**
     * marginLeft specifies the number of columns of horizontal margin
     * that will be placed along the left edge of the layout.
     * <p/>
     * The default value is 0.
     *
     * @since 3.1
     */
    public int marginLeft = 0;

    /**
     * marginTop specifies the number of rows of vertical margin
     * that will be placed along the top edge of the layout.
     * <p/>
     * The default value is 0.
     *
     * @since 3.1
     */
    public int marginTop = 0;

    /**
     * marginRight specifies the number of columns of horizontal margin
     * that will be placed along the right edge of the layout.
     * <p/>
     * The default value is 0.
     *
     * @since 3.1
     */
    public int marginRight = 0;

    /**
     * marginBottom specifies the number of rows of vertical margin
     * that will be placed along the bottom edge of the layout.
     * <p/>
     * The default value is 0.
     *
     * @since 3.1
     */
    public int marginBottom = 0;

    /**
     * horizontalSpacing specifies the number of columns between the right
     * edge of one cell and the left edge of its neighbouring cell to
     * the right.
     * <p/>
     * The default value is 1.
     */
    public int horizontalSpacing = 1;

    /**
     * verticalSpacing specifies the number of rows between the bottom
     * edge of one cell and the top edge of its neighbouring cell underneath.
     * <p/>
     * The default value is 1.
     */
    public int verticalSpacing = 1;

    /**
     * Constructs a new instance of this class
     * with a single column.
     */
    public GridLayout() {
    }

    /**
     * Constructs a new instance of this class given the
     * number of columns, and whether or not the columns
     * should be forced to have the same width.
     * If numColumns has a value less than 1, the layout will not
     * set the size and position of any controls.
     *
     * @param numColumns            the number of columns in the grid
     * @param makeColumnsEqualWidth whether or not the columns will have equal width
     * @since 2.0
     */
    public GridLayout(int numColumns, boolean makeColumnsEqualWidth) {
        this.numColumns = numColumns;
        this.makeColumnsEqualWidth = makeColumnsEqualWidth;
    }

    @Override
    public TerminalSize getPreferredSize(List<Component> components) {
        Component[] children = components.toArray(new Component[components.size()]);
        Point size = layout(children, false, 0, 0, SWT.DEFAULT, SWT.DEFAULT, true);
        return new TerminalSize(size.x, size.y);
    }

    private GridData getData(Component[][] grid, int row, int column, int rowCount, int columnCount, boolean first) {
        Component control = grid[row][column];
        if (control != null) {
            GridData data = getGridData(control);
            int hSpan = Math.max(1, Math.min(data.horizontalSpan, columnCount));
            int vSpan = Math.max(1, data.verticalSpan);
            int i = first ? row + vSpan - 1 : row - vSpan + 1;
            int j = first ? column + hSpan - 1 : column - hSpan + 1;
            if (0 <= i && i < rowCount) {
                if (0 <= j && j < columnCount) {
                    if (control == grid[i][j]) {
                        return data;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void doLayout(TerminalSize area, List<Component> components) {
        Component[] children = components.toArray(new Component[components.size()]);
        layout(children, true, 0, 0, area.getColumns(), area.getRows(), true);
    }

    private Point layout(Component[] children, boolean move, int x, int y, int width, int height, boolean flushCache) {
        if (numColumns < 1) {
            return new Point(marginLeft + marginWidth * 2 + marginRight, marginTop + marginHeight * 2 + marginBottom);
        }
        int count = 0;
        for (int i = 0; i < children.length; i++) {
            Component control = children[i];
            GridData data = getGridData(control);
            if (data == null || !data.exclude) {
                children[count++] = children[i];
            }
        }
        if (count == 0) {
            return new Point(marginLeft + marginWidth * 2 + marginRight, marginTop + marginHeight * 2 + marginBottom);
        }
        for (int i = 0; i < count; i++) {
            Component child = children[i];
            GridData data = getGridData(child);
            if (data == null) {
                setLayoutData(child, new GridData());
            }
            if (flushCache) {
                data.flushCache();
            }
            data.computeSize(child, data.widthHint, data.heightHint, flushCache);
            if (data.grabExcessHorizontalSpace && data.minimumWidth > 0) {
                if (data.cacheWidth < data.minimumWidth) {
                    int trim = 0;
                    data.cacheWidth = data.cacheHeight = SWT.DEFAULT;
                    data.computeSize(child, Math.max(0, data.minimumWidth - trim), data.heightHint, false);
                }
            }
            if (data.grabExcessVerticalSpace && data.minimumHeight > 0) {
                data.cacheHeight = Math.max(data.cacheHeight, data.minimumHeight);
            }
        }

	/* Build the grid */
        int row = 0, column = 0, rowCount = 0, columnCount = numColumns;
        Component[][] grid = new Component[4][columnCount];
        for (int i = 0; i < count; i++) {
            Component child = children[i];
            GridData data = getGridData(child);
            int hSpan = Math.max(1, Math.min(data.horizontalSpan, columnCount));
            int vSpan = Math.max(1, data.verticalSpan);
            while (true) {
                int lastRow = row + vSpan;
                if (lastRow >= grid.length) {
                    Component[][] newGrid = new Component[lastRow + 4][columnCount];
                    System.arraycopy(grid, 0, newGrid, 0, grid.length);
                    grid = newGrid;
                }
                if (grid[row] == null) {
                    grid[row] = new Component[columnCount];
                }
                while (column < columnCount && grid[row][column] != null) {
                    column++;
                }
                int endCount = column + hSpan;
                if (endCount <= columnCount) {
                    int index = column;
                    while (index < endCount && grid[row][index] == null) {
                        index++;
                    }
                    if (index == endCount) {
                        break;
                    }
                    column = index;
                }
                if (column + hSpan >= columnCount) {
                    column = 0;
                    row++;
                }
            }
            for (int j = 0; j < vSpan; j++) {
                if (grid[row + j] == null) {
                    grid[row + j] = new Component[columnCount];
                }
                for (int k = 0; k < hSpan; k++) {
                    grid[row + j][column + k] = child;
                }
            }
            rowCount = Math.max(rowCount, row + vSpan);
            column += hSpan;
        }

	/* Column widths */
        int availableWidth = width - horizontalSpacing * (columnCount - 1) - (marginLeft + marginWidth * 2 + marginRight);
        int expandCount = 0;
        int[] widths = new int[columnCount];
        int[] minWidths = new int[columnCount];
        boolean[] expandColumn = new boolean[columnCount];
        for (int j = 0; j < columnCount; j++) {
            for (int i = 0; i < rowCount; i++) {
                GridData data = getData(grid, i, j, rowCount, columnCount, true);
                if (data != null) {
                    int hSpan = Math.max(1, Math.min(data.horizontalSpan, columnCount));
                    if (hSpan == 1) {
                        int w = data.cacheWidth + data.horizontalIndent;
                        widths[j] = Math.max(widths[j], w);
                        if (data.grabExcessHorizontalSpace) {
                            if (!expandColumn[j]) {
                                expandCount++;
                            }
                            expandColumn[j] = true;
                        }
                        if (!data.grabExcessHorizontalSpace || data.minimumWidth != 0) {
                            w = !data.grabExcessHorizontalSpace || data.minimumWidth == SWT.DEFAULT ? data.cacheWidth : data.minimumWidth;
                            w += data.horizontalIndent;
                            minWidths[j] = Math.max(minWidths[j], w);
                        }
                    }
                }
            }
            for (int i = 0; i < rowCount; i++) {
                GridData data = getData(grid, i, j, rowCount, columnCount, false);
                if (data != null) {
                    int hSpan = Math.max(1, Math.min(data.horizontalSpan, columnCount));
                    if (hSpan > 1) {
                        int spanWidth = 0, spanMinWidth = 0, spanExpandCount = 0;
                        for (int k = 0; k < hSpan; k++) {
                            spanWidth += widths[j - k];
                            spanMinWidth += minWidths[j - k];
                            if (expandColumn[j - k]) {
                                spanExpandCount++;
                            }
                        }
                        if (data.grabExcessHorizontalSpace && spanExpandCount == 0) {
                            expandCount++;
                            expandColumn[j] = true;
                        }
                        int w = data.cacheWidth + data.horizontalIndent - spanWidth - (hSpan - 1) * horizontalSpacing;
                        if (w > 0) {
                            if (makeColumnsEqualWidth) {
                                int equalWidth = (w + spanWidth) / hSpan;
                                int remainder = (w + spanWidth) % hSpan, last = -1;
                                for (int k = 0; k < hSpan; k++) {
                                    widths[last = j - k] = Math.max(equalWidth, widths[j - k]);
                                }
                                if (last > -1) {
                                    widths[last] += remainder;
                                }
                            } else {
                                if (spanExpandCount == 0) {
                                    widths[j] += w;
                                } else {
                                    int delta = w / spanExpandCount;
                                    int remainder = w % spanExpandCount, last = -1;
                                    for (int k = 0; k < hSpan; k++) {
                                        if (expandColumn[j - k]) {
                                            widths[last = j - k] += delta;
                                        }
                                    }
                                    if (last > -1) {
                                        widths[last] += remainder;
                                    }
                                }
                            }
                        }
                        if (!data.grabExcessHorizontalSpace || data.minimumWidth != 0) {
                            w = !data.grabExcessHorizontalSpace || data.minimumWidth == SWT.DEFAULT ? data.cacheWidth : data.minimumWidth;
                            w += data.horizontalIndent - spanMinWidth - (hSpan - 1) * horizontalSpacing;
                            if (w > 0) {
                                if (spanExpandCount == 0) {
                                    minWidths[j] += w;
                                } else {
                                    int delta = w / spanExpandCount;
                                    int remainder = w % spanExpandCount, last = -1;
                                    for (int k = 0; k < hSpan; k++) {
                                        if (expandColumn[j - k]) {
                                            minWidths[last = j - k] += delta;
                                        }
                                    }
                                    if (last > -1) {
                                        minWidths[last] += remainder;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (makeColumnsEqualWidth) {
            int minColumnWidth = 0;
            int columnWidth = 0;
            for (int i = 0; i < columnCount; i++) {
                minColumnWidth = Math.max(minColumnWidth, minWidths[i]);
                columnWidth = Math.max(columnWidth, widths[i]);
            }
            columnWidth = width == SWT.DEFAULT || expandCount == 0 ? columnWidth : Math.max(minColumnWidth, availableWidth / columnCount);
            for (int i = 0; i < columnCount; i++) {
                expandColumn[i] = expandCount > 0;
                widths[i] = columnWidth;
            }
        } else {
            if (width != SWT.DEFAULT && expandCount > 0) {
                int totalWidth = 0;
                for (int i = 0; i < columnCount; i++) {
                    totalWidth += widths[i];
                }
                int c = expandCount;
                int delta = (availableWidth - totalWidth) / c;
                int remainder = (availableWidth - totalWidth) % c;
                int last = -1;
                while (totalWidth != availableWidth) {
                    for (int j = 0; j < columnCount; j++) {
                        if (expandColumn[j]) {
                            if (widths[j] + delta > minWidths[j]) {
                                widths[last = j] = widths[j] + delta;
                            } else {
                                widths[j] = minWidths[j];
                                expandColumn[j] = false;
                                c--;
                            }
                        }
                    }
                    if (last > -1) {
                        widths[last] += remainder;
                    }

                    for (int j = 0; j < columnCount; j++) {
                        for (int i = 0; i < rowCount; i++) {
                            GridData data = getData(grid, i, j, rowCount, columnCount, false);
                            if (data != null) {
                                int hSpan = Math.max(1, Math.min(data.horizontalSpan, columnCount));
                                if (hSpan > 1) {
                                    if (!data.grabExcessHorizontalSpace || data.minimumWidth != 0) {
                                        int spanWidth = 0, spanExpandCount = 0;
                                        for (int k = 0; k < hSpan; k++) {
                                            spanWidth += widths[j - k];
                                            if (expandColumn[j - k]) {
                                                spanExpandCount++;
                                            }
                                        }
                                        int w = !data.grabExcessHorizontalSpace || data.minimumWidth == SWT.DEFAULT ? data.cacheWidth : data.minimumWidth;
                                        w += data.horizontalIndent - spanWidth - (hSpan - 1) * horizontalSpacing;
                                        if (w > 0) {
                                            if (spanExpandCount == 0) {
                                                widths[j] += w;
                                            } else {
                                                int delta2 = w / spanExpandCount;
                                                int remainder2 = w % spanExpandCount, last2 = -1;
                                                for (int k = 0; k < hSpan; k++) {
                                                    if (expandColumn[j - k]) {
                                                        widths[last2 = j - k] += delta2;
                                                    }
                                                }
                                                if (last2 > -1) {
                                                    widths[last2] += remainder2;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (c == 0) {
                        break;
                    }
                    totalWidth = 0;
                    for (int i = 0; i < columnCount; i++) {
                        totalWidth += widths[i];
                    }
                    delta = (availableWidth - totalWidth) / c;
                    remainder = (availableWidth - totalWidth) % c;
                    last = -1;
                }
            }
        }

	/* Wrapping */
        GridData[] flush = null;
        int flushLength = 0;
        if (width != SWT.DEFAULT) {
            for (int j = 0; j < columnCount; j++) {
                for (int i = 0; i < rowCount; i++) {
                    GridData data = getData(grid, i, j, rowCount, columnCount, false);
                    if (data != null) {
                        if (data.heightHint == SWT.DEFAULT) {
                            Component child = grid[i][j];
                            //TEMPORARY CODE
                            int hSpan = Math.max(1, Math.min(data.horizontalSpan, columnCount));
                            int currentWidth = 0;
                            for (int k = 0; k < hSpan; k++) {
                                currentWidth += widths[j - k];
                            }
                            currentWidth += (hSpan - 1) * horizontalSpacing - data.horizontalIndent;
                            if ((currentWidth != data.cacheWidth && data.horizontalAlignment == SWT.FILL) || (data.cacheWidth > currentWidth)) {
                                int trim = 0;
                                data.cacheWidth = data.cacheHeight = SWT.DEFAULT;
                                data.computeSize(child, Math.max(0, currentWidth - trim), data.heightHint, false);
                                if (data.grabExcessVerticalSpace && data.minimumHeight > 0) {
                                    data.cacheHeight = Math.max(data.cacheHeight, data.minimumHeight);
                                }
                                if (flush == null) {
                                    flush = new GridData[count];
                                }
                                flush[flushLength++] = data;
                            }
                        }
                    }
                }
            }
        }

	/* Row heights */
        int availableHeight = height - verticalSpacing * (rowCount - 1) - (marginTop + marginHeight * 2 + marginBottom);
        expandCount = 0;
        int[] heights = new int[rowCount];
        int[] minHeights = new int[rowCount];
        boolean[] expandRow = new boolean[rowCount];
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                GridData data = getData(grid, i, j, rowCount, columnCount, true);
                if (data != null) {
                    int vSpan = Math.max(1, Math.min(data.verticalSpan, rowCount));
                    if (vSpan == 1) {
                        int h = data.cacheHeight + data.verticalIndent;
                        heights[i] = Math.max(heights[i], h);
                        if (data.grabExcessVerticalSpace) {
                            if (!expandRow[i]) {
                                expandCount++;
                            }
                            expandRow[i] = true;
                        }
                        if (!data.grabExcessVerticalSpace || data.minimumHeight != 0) {
                            h = !data.grabExcessVerticalSpace || data.minimumHeight == SWT.DEFAULT ? data.cacheHeight : data.minimumHeight;
                            h += data.verticalIndent;
                            minHeights[i] = Math.max(minHeights[i], h);
                        }
                    }
                }
            }
            for (int j = 0; j < columnCount; j++) {
                GridData data = getData(grid, i, j, rowCount, columnCount, false);
                if (data != null) {
                    int vSpan = Math.max(1, Math.min(data.verticalSpan, rowCount));
                    if (vSpan > 1) {
                        int spanHeight = 0, spanMinHeight = 0, spanExpandCount = 0;
                        for (int k = 0; k < vSpan; k++) {
                            spanHeight += heights[i - k];
                            spanMinHeight += minHeights[i - k];
                            if (expandRow[i - k]) {
                                spanExpandCount++;
                            }
                        }
                        if (data.grabExcessVerticalSpace && spanExpandCount == 0) {
                            expandCount++;
                            expandRow[i] = true;
                        }
                        int h = data.cacheHeight + data.verticalIndent - spanHeight - (vSpan - 1) * verticalSpacing;
                        if (h > 0) {
                            if (spanExpandCount == 0) {
                                heights[i] += h;
                            } else {
                                int delta = h / spanExpandCount;
                                int remainder = h % spanExpandCount, last = -1;
                                for (int k = 0; k < vSpan; k++) {
                                    if (expandRow[i - k]) {
                                        heights[last = i - k] += delta;
                                    }
                                }
                                if (last > -1) {
                                    heights[last] += remainder;
                                }
                            }
                        }
                        if (!data.grabExcessVerticalSpace || data.minimumHeight != 0) {
                            h = !data.grabExcessVerticalSpace || data.minimumHeight == SWT.DEFAULT ? data.cacheHeight : data.minimumHeight;
                            h += data.verticalIndent - spanMinHeight - (vSpan - 1) * verticalSpacing;
                            if (h > 0) {
                                if (spanExpandCount == 0) {
                                    minHeights[i] += h;
                                } else {
                                    int delta = h / spanExpandCount;
                                    int remainder = h % spanExpandCount, last = -1;
                                    for (int k = 0; k < vSpan; k++) {
                                        if (expandRow[i - k]) {
                                            minHeights[last = i - k] += delta;
                                        }
                                    }
                                    if (last > -1) {
                                        minHeights[last] += remainder;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (height != SWT.DEFAULT && expandCount > 0) {
            int totalHeight = 0;
            for (int i = 0; i < rowCount; i++) {
                totalHeight += heights[i];
            }
            int c = expandCount;
            int delta = (availableHeight - totalHeight) / c;
            int remainder = (availableHeight - totalHeight) % c;
            int last = -1;
            while (totalHeight != availableHeight) {
                for (int i = 0; i < rowCount; i++) {
                    if (expandRow[i]) {
                        if (heights[i] + delta > minHeights[i]) {
                            heights[last = i] = heights[i] + delta;
                        } else {
                            heights[i] = minHeights[i];
                            expandRow[i] = false;
                            c--;
                        }
                    }
                }
                if (last > -1) {
                    heights[last] += remainder;
                }

                for (int i = 0; i < rowCount; i++) {
                    for (int j = 0; j < columnCount; j++) {
                        GridData data = getData(grid, i, j, rowCount, columnCount, false);
                        if (data != null) {
                            int vSpan = Math.max(1, Math.min(data.verticalSpan, rowCount));
                            if (vSpan > 1) {
                                if (!data.grabExcessVerticalSpace || data.minimumHeight != 0) {
                                    int spanHeight = 0, spanExpandCount = 0;
                                    for (int k = 0; k < vSpan; k++) {
                                        spanHeight += heights[i - k];
                                        if (expandRow[i - k]) {
                                            spanExpandCount++;
                                        }
                                    }
                                    int h = !data.grabExcessVerticalSpace || data.minimumHeight == SWT.DEFAULT ? data.cacheHeight : data.minimumHeight;
                                    h += data.verticalIndent - spanHeight - (vSpan - 1) * verticalSpacing;
                                    if (h > 0) {
                                        if (spanExpandCount == 0) {
                                            heights[i] += h;
                                        } else {
                                            int delta2 = h / spanExpandCount;
                                            int remainder2 = h % spanExpandCount, last2 = -1;
                                            for (int k = 0; k < vSpan; k++) {
                                                if (expandRow[i - k]) {
                                                    heights[last2 = i - k] += delta2;
                                                }
                                            }
                                            if (last2 > -1) {
                                                heights[last2] += remainder2;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (c == 0) {
                    break;
                }
                totalHeight = 0;
                for (int i = 0; i < rowCount; i++) {
                    totalHeight += heights[i];
                }
                delta = (availableHeight - totalHeight) / c;
                remainder = (availableHeight - totalHeight) % c;
                last = -1;
            }
        }

	/* Position the controls */
        if (move) {
            int gridY = y + marginTop + marginHeight;
            for (int i = 0; i < rowCount; i++) {
                int gridX = x + marginLeft + marginWidth;
                for (int j = 0; j < columnCount; j++) {
                    GridData data = getData(grid, i, j, rowCount, columnCount, true);
                    if (data != null) {
                        int hSpan = Math.max(1, Math.min(data.horizontalSpan, columnCount));
                        int vSpan = Math.max(1, data.verticalSpan);
                        int cellWidth = 0, cellHeight = 0;
                        for (int k = 0; k < hSpan; k++) {
                            cellWidth += widths[j + k];
                        }
                        for (int k = 0; k < vSpan; k++) {
                            cellHeight += heights[i + k];
                        }
                        cellWidth += horizontalSpacing * (hSpan - 1);
                        int childX = gridX + data.horizontalIndent;
                        int childWidth = Math.min(data.cacheWidth, cellWidth);
                        switch (data.horizontalAlignment) {
                            case SWT.CENTER:
                            case GridData.CENTER:
                                childX += Math.max(0, (cellWidth - data.horizontalIndent - childWidth) / 2);
                                break;
                            case SWT.RIGHT:
                            case SWT.END:
                            case GridData.END:
                                childX += Math.max(0, cellWidth - data.horizontalIndent - childWidth);
                                break;
                            case SWT.FILL:
                                childWidth = cellWidth - data.horizontalIndent;
                                break;
                        }
                        cellHeight += verticalSpacing * (vSpan - 1);
                        int childY = gridY + data.verticalIndent;
                        int childHeight = Math.min(data.cacheHeight, cellHeight);
                        switch (data.verticalAlignment) {
                            case SWT.CENTER:
                            case GridData.CENTER:
                                childY += Math.max(0, (cellHeight - data.verticalIndent - childHeight) / 2);
                                break;
                            case SWT.BOTTOM:
                            case SWT.END:
                            case GridData.END:
                                childY += Math.max(0, cellHeight - data.verticalIndent - childHeight);
                                break;
                            case SWT.FILL:
                                childHeight = cellHeight - data.verticalIndent;
                                break;
                        }
                        Component child = grid[i][j];
                        if (child != null) {
                            child.setPosition(new TerminalPosition(childX, childY));
                            child.setSize(new TerminalSize(childWidth, childHeight));
                        }
                    }
                    gridX += widths[j] + horizontalSpacing;
                }
                gridY += heights[i] + verticalSpacing;
            }
        }

        // clean up cache
        for (int i = 0; i < flushLength; i++) {
            flush[i].cacheWidth = flush[i].cacheHeight = -1;
        }

        int totalDefaultWidth = 0;
        int totalDefaultHeight = 0;
        for (int i = 0; i < columnCount; i++) {
            totalDefaultWidth += widths[i];
        }
        for (int i = 0; i < rowCount; i++) {
            totalDefaultHeight += heights[i];
        }
        totalDefaultWidth += horizontalSpacing * (columnCount - 1) + marginLeft + marginWidth * 2 + marginRight;
        totalDefaultHeight += verticalSpacing * (rowCount - 1) + marginTop + marginHeight * 2 + marginBottom;
        return new Point(totalDefaultWidth, totalDefaultHeight);
    }

    private String getName() {
        String string = getClass().getName();
        int index = string.lastIndexOf('.');
        if (index == -1) {
            return string;
        }
        return string.substring(index + 1, string.length());
    }

    /**
     * Returns a string containing a concise, human-readable
     * description of the receiver.
     *
     * @return a string representation of the layout
     */
    @Override
    public String toString() {
        String string = getName() + " {";
        if (numColumns != 1) {
            string += "numColumns=" + numColumns + " ";
        }
        if (makeColumnsEqualWidth) {
            string += "makeColumnsEqualWidth=" + makeColumnsEqualWidth + " ";
        }
        if (marginWidth != 0) {
            string += "marginWidth=" + marginWidth + " ";
        }
        if (marginHeight != 0) {
            string += "marginHeight=" + marginHeight + " ";
        }
        if (marginLeft != 0) {
            string += "marginLeft=" + marginLeft + " ";
        }
        if (marginRight != 0) {
            string += "marginRight=" + marginRight + " ";
        }
        if (marginTop != 0) {
            string += "marginTop=" + marginTop + " ";
        }
        if (marginBottom != 0) {
            string += "marginBottom=" + marginBottom + " ";
        }
        if (horizontalSpacing != 0) {
            string += "horizontalSpacing=" + horizontalSpacing + " ";
        }
        if (verticalSpacing != 0) {
            string += "verticalSpacing=" + verticalSpacing + " ";
        }
        string = string.trim();
        string += "}";
        return string;
    }



    /**
     * <code>GridData</code> is the layout data object associated with
     * <code>GridLayout</code>. To set a <code>GridData</code> object into a
     * control, you use the <code>Control.setLayoutData(Object)</code> method.
     * <p>
     * There are two ways to create a <code>GridData</code> object with certain
     * fields set. The first is to set the fields directly, like this:
     * <pre>
     * 		GridData gridData = new GridData();
     * 		gridData.horizontalAlignment = GridData.FILL;
     * 		gridData.grabExcessHorizontalSpace = true;
     * 		button1.setLayoutData(gridData);
     *
     * 		gridData = new GridData();
     * 		gridData.horizontalAlignment = GridData.FILL;
     * 		gridData.verticalAlignment = GridData.FILL;
     * 		gridData.grabExcessHorizontalSpace = true;
     * 		gridData.grabExcessVerticalSpace = true;
     * 		gridData.horizontalSpan = 2;
     * 		button2.setLayoutData(gridData);
     * </pre>
     * The second is to take advantage of <code>GridData</code> convenience constructors, for example:
     * <pre>
     *      button1.setLayoutData(new GridData (SWT.FILL, SWT.CENTER, true, false));
     *      button2.setLayoutData(new GridData (SWT.FILL, SWT.FILL, true, true, 2, 1));
     * </pre>
     * </p>
     * <p>
     * NOTE: Do not reuse <code>GridData</code> objects. Every control in a
     * <code>Composite</code> that is managed by a <code>GridLayout</code>
     * must have a unique <code>GridData</code> object. If the layout data
     * for a control in a <code>GridLayout</code> is null at layout time,
     * a unique <code>GridData</code> object is created for it.
     * </p>
     *
     * @see GridLayout
     * @see <a href="http://www.eclipse.org/swt/">Sample code and further information</a>
     */
    private static final class GridData {
        /**
         * verticalAlignment specifies how controls will be positioned
         * vertically within a cell.
         *
         * The default value is CENTER.
         *
         * Possible values are: <ul>
         *    <li>SWT.BEGINNING (or SWT.TOP): Position the control at the top of the cell</li>
         *    <li>SWT.CENTER: Position the control in the vertical center of the cell</li>
         *    <li>SWT.END (or SWT.BOTTOM): Position the control at the bottom of the cell</li>
         *    <li>SWT.FILL: Resize the control to fill the cell vertically</li>
         * </ul>
         */
        public int verticalAlignment = CENTER;

        /**
         * horizontalAlignment specifies how controls will be positioned
         * horizontally within a cell.
         *
         * The default value is BEGINNING.
         *
         * Possible values are: <ul>
         *    <li>SWT.BEGINNING (or SWT.LEFT): Position the control at the left of the cell</li>
         *    <li>SWT.CENTER: Position the control in the horizontal center of the cell</li>
         *    <li>SWT.END (or SWT.RIGHT): Position the control at the right of the cell</li>
         *    <li>SWT.FILL: Resize the control to fill the cell horizontally</li>
         * </ul>
         */
        public int horizontalAlignment = BEGINNING;

        /**
         * widthHint specifies the preferred width in pixels. This value
         * is the wHint passed into Control.computeSize(int, int, boolean)
         * to determine the preferred size of the control.
         *
         * The default value is SWT.DEFAULT.
         *
         */
        public int widthHint = SWT.DEFAULT;

        /**
         * heightHint specifies the preferred height in pixels. This value
         * is the hHint passed into Control.computeSize(int, int, boolean)
         * to determine the preferred size of the control.
         *
         * The default value is SWT.DEFAULT.
         *
         */
        public int heightHint = SWT.DEFAULT;

        /**
         * horizontalIndent specifies the number of pixels of indentation
         * that will be placed along the left side of the cell.
         *
         * The default value is 0.
         */
        public int horizontalIndent = 0;

        /**
         * verticalIndent specifies the number of pixels of indentation
         * that will be placed along the top side of the cell.
         *
         * The default value is 0.
         *
         * @since 3.1
         */
        public int verticalIndent = 0;

        /**
         * horizontalSpan specifies the number of column cells that the control
         * will take up.
         *
         * The default value is 1.
         */
        public int horizontalSpan = 1;

        /**
         * verticalSpan specifies the number of row cells that the control
         * will take up.
         *
         * The default value is 1.
         */
        public int verticalSpan = 1;

        /**
         * <p>grabExcessHorizontalSpace specifies whether the width of the cell
         * changes depending on the size of the parent Composite.  If
         * grabExcessHorizontalSpace is <code>true</code>, the following rules
         * apply to the width of the cell:</p>
         * <ul>
         * <li>If extra horizontal space is available in the parent, the cell will
         * grow to be wider than its preferred width.  The new width
         * will be "preferred width + delta" where delta is the extra
         * horizontal space divided by the number of grabbing columns.</li>
         * <li>If there is not enough horizontal space available in the parent, the
         * cell will shrink until it reaches its minimum width as specified by
         * GridData.minimumWidth. The new width will be the maximum of
         * "minimumWidth" and "preferred width - delta", where delta is
         * the amount of space missing divided by the number of grabbing columns.</li>
         * <li>If the parent is packed, the cell will be its preferred width
         * as specified by GridData.widthHint.</li>
         * <li>If the control spans multiple columns and there are no other grabbing
         * controls in any of the spanned columns, the last column in the span will
         * grab the extra space.  If there is at least one other grabbing control
         * in the span, the grabbing will be spread over the columns already
         * marked as grabExcessHorizontalSpace.</li>
         * </ul>
         *
         * <p>The default value is false.</p>
         *
         * @see GridData#minimumWidth
         * @see GridData#widthHint
         */
        public boolean grabExcessHorizontalSpace = false;

        /**
         * <p>grabExcessVerticalSpace specifies whether the height of the cell
         * changes depending on the size of the parent Composite.  If
         * grabExcessVerticalSpace is <code>true</code>, the following rules
         * apply to the height of the cell:</p>
         * <ul>
         * <li>If extra vertical space is available in the parent, the cell will
         * grow to be taller than its preferred height.  The new height
         * will be "preferred height + delta" where delta is the extra
         * vertical space divided by the number of grabbing rows.</li>
         * <li>If there is not enough vertical space available in the parent, the
         * cell will shrink until it reaches its minimum height as specified by
         * GridData.minimumHeight. The new height will be the maximum of
         * "minimumHeight" and "preferred height - delta", where delta is
         * the amount of space missing divided by the number of grabbing rows.</li>
         * <li>If the parent is packed, the cell will be its preferred height
         * as specified by GridData.heightHint.</li>
         * <li>If the control spans multiple rows and there are no other grabbing
         * controls in any of the spanned rows, the last row in the span will
         * grab the extra space.  If there is at least one other grabbing control
         * in the span, the grabbing will be spread over the rows already
         * marked as grabExcessVerticalSpace.</li>
         * </ul>
         *
         * <p>The default value is false.</p>
         *
         * @see GridData#minimumHeight
         * @see GridData#heightHint
         */
        public boolean grabExcessVerticalSpace = false;

        /**
         * minimumWidth specifies the minimum width in pixels.  This value
         * applies only if grabExcessHorizontalSpace is true. A value of
         * SWT.DEFAULT means that the minimum width will be the result
         * of Control.computeSize(int, int, boolean) where wHint is
         * determined by GridData.widthHint.
         *
         * The default value is 0.
         *
         * @since 3.1
         * @see GridData#widthHint
         */
        public int minimumWidth = 0;

        /**
         * minimumHeight specifies the minimum height in pixels.  This value
         * applies only if grabExcessVerticalSpace is true.  A value of
         * SWT.DEFAULT means that the minimum height will be the result
         * of Control.computeSize(int, int, boolean) where hHint is
         * determined by GridData.heightHint.
         *
         * The default value is 0.
         *
         * @since 3.1
         * @see GridData#heightHint
         */
        public int minimumHeight = 0;

        /**
         * exclude informs the layout to ignore this control when sizing
         * and positioning controls.  If this value is <code>true</code>,
         * the size and position of the control will not be managed by the
         * layout.  If this	value is <code>false</code>, the size and
         * position of the control will be computed and assigned.
         *
         * The default value is <code>false</code>.
         *
         * @since 3.1
         */
        public boolean exclude = false;

        /**
         * Value for horizontalAlignment or verticalAlignment.
         * Position the control at the top or left of the cell.
         * Not recommended. Use SWT.BEGINNING, SWT.TOP or SWT.LEFT instead.
         */
        public static final int BEGINNING = SWT.BEGINNING;

        /**
         * Value for horizontalAlignment or verticalAlignment.
         * Position the control in the vertical or horizontal center of the cell
         * Not recommended. Use SWT.CENTER instead.
         */
        public static final int CENTER = 2;

        /**
         * Value for horizontalAlignment or verticalAlignment.
         * Position the control at the bottom or right of the cell
         * Not recommended. Use SWT.END, SWT.BOTTOM or SWT.RIGHT instead.
         */
        public static final int END = 3;

        /**
         * Value for horizontalAlignment or verticalAlignment.
         * Resize the control to fill the cell horizontally or vertically.
         * Not recommended. Use SWT.FILL instead.
         */
        public static final int FILL = SWT.FILL;

        /**
         * Style bit for <code>new GridData(int)</code>.
         * Position the control at the top of the cell.
         * Not recommended. Use
         * <code>new GridData(int, SWT.BEGINNING, boolean, boolean)</code>
         * instead.
         */
        public static final int VERTICAL_ALIGN_BEGINNING =  1 << 1;

        /**
         * Style bit for <code>new GridData(int)</code> to position the
         * control in the vertical center of the cell.
         * Not recommended. Use
         * <code>new GridData(int, SWT.CENTER, boolean, boolean)</code>
         * instead.
         */
        public static final int VERTICAL_ALIGN_CENTER = 1 << 2;

        /**
         * Style bit for <code>new GridData(int)</code> to position the
         * control at the bottom of the cell.
         * Not recommended. Use
         * <code>new GridData(int, SWT.END, boolean, boolean)</code>
         * instead.
         */
        public static final int VERTICAL_ALIGN_END = 1 << 3;

        /**
         * Style bit for <code>new GridData(int)</code> to resize the
         * control to fill the cell vertically.
         * Not recommended. Use
         * <code>new GridData(int, SWT.FILL, boolean, boolean)</code>
         * instead
         */
        public static final int VERTICAL_ALIGN_FILL = 1 << 4;

        /**
         * Style bit for <code>new GridData(int)</code> to position the
         * control at the left of the cell.
         * Not recommended. Use
         * <code>new GridData(SWT.BEGINNING, int, boolean, boolean)</code>
         * instead.
         */
        public static final int HORIZONTAL_ALIGN_BEGINNING =  1 << 5;

        /**
         * Style bit for <code>new GridData(int)</code> to position the
         * control in the horizontal center of the cell.
         * Not recommended. Use
         * <code>new GridData(SWT.CENTER, int, boolean, boolean)</code>
         * instead.
         */
        public static final int HORIZONTAL_ALIGN_CENTER = 1 << 6;

        /**
         * Style bit for <code>new GridData(int)</code> to position the
         * control at the right of the cell.
         * Not recommended. Use
         * <code>new GridData(SWT.END, int, boolean, boolean)</code>
         * instead.
         */
        public static final int HORIZONTAL_ALIGN_END = 1 << 7;

        /**
         * Style bit for <code>new GridData(int)</code> to resize the
         * control to fill the cell horizontally.
         * Not recommended. Use
         * <code>new GridData(SWT.FILL, int, boolean, boolean)</code>
         * instead.
         */
        public static final int HORIZONTAL_ALIGN_FILL = 1 << 8;

        /**
         * Style bit for <code>new GridData(int)</code> to resize the
         * control to fit the remaining horizontal space.
         * Not recommended. Use
         * <code>new GridData(int, int, true, boolean)</code>
         * instead.
         */
        public static final int GRAB_HORIZONTAL = 1 << 9;

        /**
         * Style bit for <code>new GridData(int)</code> to resize the
         * control to fit the remaining vertical space.
         * Not recommended. Use
         * <code>new GridData(int, int, boolean, true)</code>
         * instead.
         */
        public static final int GRAB_VERTICAL = 1 << 10;

        /**
         * Style bit for <code>new GridData(int)</code> to resize the
         * control to fill the cell vertically and to fit the remaining
         * vertical space.
         * FILL_VERTICAL = VERTICAL_ALIGN_FILL | GRAB_VERTICAL
         * Not recommended. Use
         * <code>new GridData(int, SWT.FILL, boolean, true)</code>
         * instead.
         */
        public static final int FILL_VERTICAL = VERTICAL_ALIGN_FILL | GRAB_VERTICAL;

        /**
         * Style bit for <code>new GridData(int)</code> to resize the
         * control to fill the cell horizontally and to fit the remaining
         * horizontal space.
         * FILL_HORIZONTAL = HORIZONTAL_ALIGN_FILL | GRAB_HORIZONTAL
         * Not recommended. Use
         * <code>new GridData(SWT.FILL, int, true, boolean)</code>
         * instead.
         */
        public static final int FILL_HORIZONTAL = HORIZONTAL_ALIGN_FILL | GRAB_HORIZONTAL;

        /**
         * Style bit for <code>new GridData(int)</code> to resize the
         * control to fill the cell horizontally and vertically and
         * to fit the remaining horizontal and vertical space.
         * FILL_BOTH = FILL_VERTICAL | FILL_HORIZONTAL
         * Not recommended. Use
         * <code>new GridData(SWT.FILL, SWT.FILL, true, true)</code>
         * instead.
         */
        public static final int FILL_BOTH = FILL_VERTICAL | FILL_HORIZONTAL;

        int cacheWidth = -1, cacheHeight = -1;
        int defaultWidthHint, defaultHeightHint, defaultWidth = -1, defaultHeight = -1;
        int currentWidthHint, currentHeightHint, currentWidth = -1, currentHeight = -1;

        /**
         * Constructs a new instance of GridData using
         * default values.
         */
        public GridData () {
            super ();
        }

        /**
         * Constructs a new instance based on the GridData style.
         * This constructor is not recommended.
         *
         * @param style the GridData style
         */
        public GridData (int style) {
            super ();
            if ((style & VERTICAL_ALIGN_BEGINNING) != 0) verticalAlignment = BEGINNING;
            if ((style & VERTICAL_ALIGN_CENTER) != 0) verticalAlignment = CENTER;
            if ((style & VERTICAL_ALIGN_FILL) != 0) verticalAlignment = FILL;
            if ((style & VERTICAL_ALIGN_END) != 0) verticalAlignment = END;
            if ((style & HORIZONTAL_ALIGN_BEGINNING) != 0) horizontalAlignment = BEGINNING;
            if ((style & HORIZONTAL_ALIGN_CENTER) != 0) horizontalAlignment = CENTER;
            if ((style & HORIZONTAL_ALIGN_FILL) != 0) horizontalAlignment = FILL;
            if ((style & HORIZONTAL_ALIGN_END) != 0) horizontalAlignment = END;
            grabExcessHorizontalSpace = (style & GRAB_HORIZONTAL) != 0;
            grabExcessVerticalSpace = (style & GRAB_VERTICAL) != 0;
        }

        /**
         * Constructs a new instance of GridData according to the parameters.
         *
         * @param horizontalAlignment how control will be positioned horizontally within a cell,
         * 		one of: SWT.BEGINNING (or SWT.LEFT), SWT.CENTER, SWT.END (or SWT.RIGHT), or SWT.FILL
         * @param verticalAlignment how control will be positioned vertically within a cell,
         * 		one of: SWT.BEGINNING (or SWT.TOP), SWT.CENTER, SWT.END (or SWT.BOTTOM), or SWT.FILL
         * @param grabExcessHorizontalSpace whether cell will be made wide enough to fit the remaining horizontal space
         * @param grabExcessVerticalSpace whether cell will be made high enough to fit the remaining vertical space
         *
         * @since 3.0
         */
        public GridData (int horizontalAlignment, int verticalAlignment, boolean grabExcessHorizontalSpace, boolean grabExcessVerticalSpace) {
            this (horizontalAlignment, verticalAlignment, grabExcessHorizontalSpace, grabExcessVerticalSpace, 1, 1);
        }

        /**
         * Constructs a new instance of GridData according to the parameters.
         *
         * @param horizontalAlignment how control will be positioned horizontally within a cell,
         * 		one of: SWT.BEGINNING (or SWT.LEFT), SWT.CENTER, SWT.END (or SWT.RIGHT), or SWT.FILL
         * @param verticalAlignment how control will be positioned vertically within a cell,
         * 		one of: SWT.BEGINNING (or SWT.TOP), SWT.CENTER, SWT.END (or SWT.BOTTOM), or SWT.FILL
         * @param grabExcessHorizontalSpace whether cell will be made wide enough to fit the remaining horizontal space
         * @param grabExcessVerticalSpace whether cell will be made high enough to fit the remaining vertical space
         * @param horizontalSpan the number of column cells that the control will take up
         * @param verticalSpan the number of row cells that the control will take up
         *
         * @since 3.0
         */
        public GridData (int horizontalAlignment, int verticalAlignment, boolean grabExcessHorizontalSpace, boolean grabExcessVerticalSpace, int horizontalSpan, int verticalSpan) {
            super ();
            this.horizontalAlignment = horizontalAlignment;
            this.verticalAlignment = verticalAlignment;
            this.grabExcessHorizontalSpace = grabExcessHorizontalSpace;
            this.grabExcessVerticalSpace = grabExcessVerticalSpace;
            this.horizontalSpan = horizontalSpan;
            this.verticalSpan = verticalSpan;
        }

        /**
         * Constructs a new instance of GridData according to the parameters.
         * A value of SWT.DEFAULT indicates that no minimum width or
         * no minimum height is specified.
         *
         * @param width a minimum width for the column
         * @param height a minimum height for the row
         *
         * @since 3.0
         */
        public GridData (int width, int height) {
            super ();
            this.widthHint = width;
            this.heightHint = height;
        }

        void computeSize (Component control, int wHint, int hHint, boolean flushCache) {
            if (cacheWidth != -1 && cacheHeight != -1) return;
            if (wHint == this.widthHint && hHint == this.heightHint) {
                if (defaultWidth == -1 || defaultHeight == -1 || wHint != defaultWidthHint || hHint != defaultHeightHint) {
                    Point size = new Point(control.getPreferredSize());
                    defaultWidthHint = wHint;
                    defaultHeightHint = hHint;
                    defaultWidth = size.x;
                    defaultHeight = size.y;
                }
                cacheWidth = defaultWidth;
                cacheHeight = defaultHeight;
                return;
            }
            if (currentWidth == -1 || currentHeight == -1 || wHint != currentWidthHint || hHint != currentHeightHint) {
                Point size = new Point(control.getPreferredSize());
                currentWidthHint = wHint;
                currentHeightHint = hHint;
                currentWidth = size.x;
                currentHeight = size.y;
            }
            cacheWidth = currentWidth;
            cacheHeight = currentHeight;
        }

        void flushCache () {
            cacheWidth = cacheHeight = -1;
            defaultWidth = defaultHeight = -1;
            currentWidth = currentHeight = -1;
        }

        String getName () {
            String string = getClass ().getName ();
            int index = string.lastIndexOf ('.');
            if (index == -1) return string;
            return string.substring (index + 1, string.length ());
        }

        /**
         * Returns a string containing a concise, human-readable
         * description of the receiver.
         *
         * @return a string representation of the GridData object
         */
        @Override
        public String toString () {
            String hAlign;
            switch (horizontalAlignment) {
                case SWT.FILL: hAlign = "SWT.FILL"; break;
                case SWT.BEGINNING: hAlign = "SWT.BEGINNING"; break;
                case SWT.LEFT: hAlign = "SWT.LEFT"; break;
                case SWT.END: hAlign = "SWT.END"; break;
                case END: hAlign = "GridData.END"; break;
                case SWT.RIGHT: hAlign = "SWT.RIGHT"; break;
                case SWT.CENTER: hAlign = "SWT.CENTER"; break;
                case CENTER: hAlign = "GridData.CENTER"; break;
                default: hAlign = "Undefined "+horizontalAlignment; break;
            }
            String vAlign;
            switch (verticalAlignment) {
                case SWT.FILL: vAlign = "SWT.FILL"; break;
                case SWT.BEGINNING: vAlign = "SWT.BEGINNING"; break;
                case SWT.TOP: vAlign = "SWT.TOP"; break;
                case SWT.END: vAlign = "SWT.END"; break;
                case END: vAlign = "GridData.END"; break;
                case SWT.BOTTOM: vAlign = "SWT.BOTTOM"; break;
                case SWT.CENTER: vAlign = "SWT.CENTER"; break;
                case CENTER: vAlign = "GridData.CENTER"; break;
                default: vAlign = "Undefined "+verticalAlignment; break;
            }
            String string = getName()+" {";
            string += "horizontalAlignment="+hAlign+" ";
            if (horizontalIndent != 0) string += "horizontalIndent="+horizontalIndent+" ";
            if (horizontalSpan != 1) string += "horizontalSpan="+horizontalSpan+" ";
            if (grabExcessHorizontalSpace) string += "grabExcessHorizontalSpace="+grabExcessHorizontalSpace+" ";
            if (widthHint != SWT.DEFAULT) string += "widthHint="+widthHint+" ";
            if (minimumWidth != 0) string += "minimumWidth="+minimumWidth+" ";
            string += "verticalAlignment="+vAlign+" ";
            if (verticalIndent != 0) string += "verticalIndent="+verticalIndent+" ";
            if (verticalSpan != 1) string += "verticalSpan="+verticalSpan+" ";
            if (grabExcessVerticalSpace) string += "grabExcessVerticalSpace="+grabExcessVerticalSpace+" ";
            if (heightHint != SWT.DEFAULT) string += "heightHint="+heightHint+" ";
            if (minimumHeight != 0) string += "minimumHeight="+minimumHeight+" ";
            if (exclude) string += "exclude="+exclude+" ";
            string = string.trim();
            string += "}";
            return string;
        }
    }
}
