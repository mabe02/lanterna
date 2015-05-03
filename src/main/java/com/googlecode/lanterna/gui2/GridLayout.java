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

    }

    private Component[][] buildTable(List<Component> components) {
        List<Component[]> rows = new ArrayList<Component[]>();
        List<int[]> hspans = new ArrayList<int[]>();
        List<int[]> vspans = new ArrayList<int[]>();

        int rowCount = 0;
        int rowsExtent = 1;
        Queue<Component> toBePlaced = new LinkedList<Component>(components);
        while(!toBePlaced.isEmpty() && rowCount < rowsExtent) {
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
