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
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;

import java.util.*;

/**
 * This class is used to keep a 'map' of the usable area and note where all the interact:ables are. It can then be used
 * to find the next interactable in any direction. It is used inside the GUI system to drive arrow key navigation.
 * @author Martin
 */
public class InteractableLookupMap {
    private final int[][] lookupMap;
    private final List<Interactable> interactables;

    InteractableLookupMap(TerminalSize size) {
        lookupMap = new int[size.getRows()][size.getColumns()];
        interactables = new ArrayList<Interactable>();
        for (int[] aLookupMap : lookupMap) {
            Arrays.fill(aLookupMap, -1);
        }
    }

    void reset() {
        interactables.clear();
        for (int[] aLookupMap : lookupMap) {
            Arrays.fill(aLookupMap, -1);
        }
    }

    TerminalSize getSize() {
        if (lookupMap.length==0) { return TerminalSize.ZERO; }
        return new TerminalSize(lookupMap[0].length, lookupMap.length);
    }

    /**
     * Adds an interactable component to the lookup map
     * @param interactable Interactable to add to the lookup map
     */
    @SuppressWarnings("ConstantConditions")
    public synchronized void add(Interactable interactable) {
        TerminalPosition topLeft = interactable.toBasePane(TerminalPosition.TOP_LEFT_CORNER);
        TerminalSize size = interactable.getSize();
        interactables.add(interactable);
        int index = interactables.size() - 1;
        for(int y = topLeft.getRow(); y < topLeft.getRow() + size.getRows(); y++) {
            for(int x = topLeft.getColumn(); x < topLeft.getColumn() + size.getColumns(); x++) {
                //Make sure it's not outside the map
                if(y >= 0 && y < lookupMap.length &&
                        x >= 0 && x < lookupMap[y].length) {
                    lookupMap[y][x] = index;
                }
            }
        }
    }

    /**
     * Looks up what interactable component is as a particular location in the map
     * @param position Position to look up
     * @return The {@code Interactable} component at the specified location or {@code null} if there's nothing there
     */
    public synchronized Interactable getInteractableAt(TerminalPosition position) {
        if (position.getRow() < 0 || position.getColumn() < 0) {
            return null;
        }
        if(position.getRow() >= lookupMap.length) {
            return null;
        }
        else if(position.getColumn() >= lookupMap[0].length) {
            return null;
        }
        else if(lookupMap[position.getRow()][position.getColumn()] == -1) {
            return null;
        }
        return interactables.get(lookupMap[position.getRow()][position.getColumn()]);
    }

    /**
     * Starting from a particular {@code Interactable} and going up, which is the next interactable?
     * @param interactable What {@code Interactable} to start searching from
     * @return The next {@code Interactable} above the one specified or {@code null} if there are no more
     * {@code Interactable}:s above it
     */
    public synchronized Interactable findNextUp(Interactable interactable) {
        return findNextUpOrDown(interactable, false);
    }

    /**
     * Starting from a particular {@code Interactable} and going down, which is the next interactable?
     * @param interactable What {@code Interactable} to start searching from
     * @return The next {@code Interactable} below the one specified or {@code null} if there are no more
     * {@code Interactable}:s below it
     */
    public synchronized Interactable findNextDown(Interactable interactable) {
        return findNextUpOrDown(interactable, true);
    }

    //Avoid code duplication in above two methods
    private Interactable findNextUpOrDown(Interactable interactable, boolean isDown) {
        int directionTerm = isDown ? 1 : -1;
        TerminalPosition startPosition = interactable.getCursorLocation();
        if (startPosition == null) {
            // If the currently active interactable component is not showing the cursor, use the top-left position
            // instead if we're going up, or the bottom-left position if we're going down
            if(isDown) {
                startPosition = new TerminalPosition(0, interactable.getSize().getRows() - 1);
            }
            else {
                startPosition = TerminalPosition.TOP_LEFT_CORNER;
            }
        }
        else {
            //Adjust position so that it's at the bottom of the component if we're going down or at the top of the
            //component if we're going right. Otherwise the lookup might product odd results in certain cases.
            if(isDown) {
                startPosition = startPosition.withRow(interactable.getSize().getRows() - 1);
            }
            else {
                startPosition = startPosition.withRow(0);
            }
        }
        startPosition = interactable.toBasePane(startPosition);
        if(startPosition == null) {
            // The structure has changed, our interactable is no longer inside the base pane!
            return null;
        }
        Set<Interactable> disqualified = getDisqualifiedInteractables(startPosition, true);
        TerminalSize size = getSize();
        int maxShiftLeft = interactable.toBasePane(TerminalPosition.TOP_LEFT_CORNER).getColumn();
        maxShiftLeft = Math.max(maxShiftLeft, 0);
        int maxShiftRight = interactable.toBasePane(new TerminalPosition(interactable.getSize().getColumns() - 1, 0)).getColumn();
        maxShiftRight = Math.min(maxShiftRight, size.getColumns() - 1);
        int maxShift = Math.max(startPosition.getColumn() - maxShiftLeft, maxShiftRight - startPosition.getRow());
        for (int searchRow = startPosition.getRow() + directionTerm;
             searchRow >= 0 && searchRow < size.getRows();
             searchRow += directionTerm) {

            for (int xShift = 0; xShift <= maxShift; xShift++) {
                for (int modifier : new int[]{1, -1}) {
                    if (xShift == 0 && modifier == -1) {
                        break;
                    }
                    int searchColumn = startPosition.getColumn() + (xShift * modifier);
                    if (searchColumn < maxShiftLeft || searchColumn > maxShiftRight) {
                        continue;
                    }

                    int index = lookupMap[searchRow][searchColumn];
                    if (index != -1 && !disqualified.contains(interactables.get(index))) {
                        return interactables.get(index);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Starting from a particular {@code Interactable} and going left, which is the next interactable?
     * @param interactable What {@code Interactable} to start searching from
     * @return The next {@code Interactable} left of the one specified or {@code null} if there are no more
     * {@code Interactable}:s left of it
     */
    public synchronized Interactable findNextLeft(Interactable interactable) {
        return findNextLeftOrRight(interactable, false);
    }

    /**
     * Starting from a particular {@code Interactable} and going right, which is the next interactable?
     * @param interactable What {@code Interactable} to start searching from
     * @return The next {@code Interactable} right of the one specified or {@code null} if there are no more
     * {@code Interactable}:s right of it
     */
    public synchronized Interactable findNextRight(Interactable interactable) {
        return findNextLeftOrRight(interactable, true);
    }

    //Avoid code duplication in above two methods
    private Interactable findNextLeftOrRight(Interactable interactable, boolean isRight) {
        int directionTerm = isRight ? 1 : -1;
        TerminalPosition startPosition = interactable.getCursorLocation();
        if(startPosition == null) {
            // If the currently active interactable component is not showing the cursor, use the top-left position
            // instead if we're going left, or the top-right position if we're going right
            if(isRight) {
                startPosition = new TerminalPosition(interactable.getSize().getColumns() - 1, 0);
            }
            else {
                startPosition = TerminalPosition.TOP_LEFT_CORNER;
            }
        }
        else {
            //Adjust position so that it's on the left-most side if we're going left or right-most side if we're going
            //right. Otherwise the lookup might product odd results in certain cases
            if(isRight) {
                startPosition = startPosition.withColumn(interactable.getSize().getColumns() - 1);
            }
            else {
                startPosition = startPosition.withColumn(0);
            }
        }
        startPosition = interactable.toBasePane(startPosition);
        if(startPosition == null) {
            // The structure has changed, our interactable is no longer inside the base pane!
            return null;
        }
        Set<Interactable> disqualified = getDisqualifiedInteractables(startPosition, false);
        TerminalSize size = getSize();
        int maxShiftUp = interactable.toBasePane(TerminalPosition.TOP_LEFT_CORNER).getRow();
        maxShiftUp = Math.max(maxShiftUp, 0);
        int maxShiftDown = interactable.toBasePane(new TerminalPosition(0, interactable.getSize().getRows() - 1)).getRow();
        maxShiftDown = Math.min(maxShiftDown, size.getRows() - 1);
        int maxShift = Math.max(startPosition.getRow() - maxShiftUp, maxShiftDown - startPosition.getRow());
        for(int searchColumn = startPosition.getColumn() + directionTerm;
            searchColumn >= 0 && searchColumn < size.getColumns();
            searchColumn += directionTerm) {

            for(int yShift = 0; yShift <= maxShift; yShift++) {
                for(int modifier: new int[] { 1, -1 }) {
                    if(yShift == 0 && modifier == -1) {
                        break;
                    }
                    int searchRow = startPosition.getRow() + (yShift * modifier);
                    if(searchRow < maxShiftUp || searchRow > maxShiftDown) {
                        continue;
                    }
                    int index = lookupMap[searchRow][searchColumn];
                    if (index != -1 && !disqualified.contains(interactables.get(index))) {
                        return interactables.get(index);
                    }
                }
            }
        }
        return null;
    }

    private Set<Interactable> getDisqualifiedInteractables(TerminalPosition startPosition, boolean scanHorizontally) {
        Set<Interactable> disqualified = new HashSet<Interactable>();
        if (lookupMap.length == 0) { return disqualified; } // safeguard

        TerminalSize size = getSize();

        //Adjust start position if necessary
        if(startPosition.getRow() < 0) {
            startPosition = startPosition.withRow(0);
        }
        else if(startPosition.getRow() >= lookupMap.length) {
            startPosition = startPosition.withRow(lookupMap.length - 1);
        }
        if(startPosition.getColumn() < 0) {
            startPosition = startPosition.withColumn(0);
        }
        else if(startPosition.getColumn() >= lookupMap[startPosition.getRow()].length) {
            startPosition = startPosition.withColumn(lookupMap[startPosition.getRow()].length - 1);
        }

        if(scanHorizontally) {
            for(int column = 0; column < size.getColumns(); column++) {
                int index = lookupMap[startPosition.getRow()][column];
                if(index != -1) {
                    disqualified.add(interactables.get(index));
                }
            }
        }
        else {
            for(int row = 0; row < size.getRows(); row++) {
                int index = lookupMap[row][startPosition.getColumn()];
                if(index != -1) {
                    disqualified.add(interactables.get(index));
                }
            }
        }
        return disqualified;
    }

    void debug() {
        for(int[] row: lookupMap) {
            for(int value: row) {
                if(value >= 0) {
                    System.out.print(" ");
                }
                System.out.print(value);
            }
            System.out.println();
        }
        System.out.println();
    }
}
