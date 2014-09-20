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

    public InteractableLookupMap(TerminalSize size) {
        lookupMap = new int[size.getRows()][size.getColumns()];
        interactables = new ArrayList<Interactable>();
    }

    public void reset() {
        interactables.clear();
        for(int y = 0; y < lookupMap.length; y++) {
            Arrays.fill(lookupMap[y], -1);
        }
    }

    public TerminalSize getSize() {
        return new TerminalSize(lookupMap[0].length, lookupMap.length);
    }

    public void add(Interactable interactable) {
        TerminalPosition topLeft = interactable.getPosition();
        TerminalSize size = interactable.getSize();
        interactables.add(interactable);
        int index = interactables.size() - 1;
        for(int y = topLeft.getRow(); y < topLeft.getRow() + size.getRows(); y++) {
            for(int x = topLeft.getColumn(); x < topLeft.getColumn() + size.getColumns(); x++) {
                lookupMap[y][x] = index;
            }
        }
    }

    public Interactable findNextUp(Interactable interactable) {
        return findNextUpOrDown(interactable, false);
    }

    public Interactable findNextDown(Interactable interactable) {
        return findNextUpOrDown(interactable, true);
    }

    //Avoid code duplication in above two methods
    private Interactable findNextUpOrDown(Interactable interactable, boolean isDown) {
        int directionTerm = isDown ? 1 : -1;
        TerminalPosition startPosition = interactable.toRootContainer(interactable.getCursorLocation());
        Set<Interactable> disqualified = getDisqualifiedInteractables(startPosition, true);
        TerminalSize size = getSize();
        int maxShift = Math.max(startPosition.getColumn(), size.getColumns() - startPosition.getColumn());
        for(int xShift = 0; xShift < maxShift; xShift++) {
            for(int modifier: new int[] { 1, -1 }) {
                if(xShift == 0 && modifier == -1) {
                    break;
                }
                int searchColumn = startPosition.getColumn() + (xShift * modifier);
                if(searchColumn < 0 || searchColumn >= size.getColumns()) {
                    continue;
                }
                for(int searchRow = startPosition.getRow() + directionTerm;
                        searchRow >= 0 && searchRow < size.getRows();
                        searchRow += directionTerm) {
                    int index = lookupMap[searchRow][searchColumn];
                    if(index != -1 && !disqualified.contains(interactables.get(index))) {
                        return interactables.get(index);
                    }
                }
            }
        }
        return null;
    }

    public Interactable findNextLeft(Interactable interactable) {
        return findNextLeftOrRight(interactable, true);
    }

    public Interactable findNextRight(Interactable interactable) {
        return findNextLeftOrRight(interactable, true);
    }

    //Avoid code duplication in above two methods
    private Interactable findNextLeftOrRight(Interactable interactable, boolean isRight) {
        int directionTerm = isRight ? 1 : -1;
        TerminalPosition startPosition = interactable.toRootContainer(interactable.getCursorLocation());
        Set<Interactable> disqualified = getDisqualifiedInteractables(startPosition, false);
        TerminalSize size = getSize();
        int maxShift = Math.max(startPosition.getRow(), size.getRows() - startPosition.getRow());
        for(int yShift = 0; yShift < maxShift; yShift++) {
            for(int modifier: new int[] { 1, -1 }) {
                if(yShift == 0 && modifier == -1) {
                    break;
                }
                int searchRow = startPosition.getRow() + (yShift * modifier);
                if(searchRow < 0 || searchRow >= size.getRows()) {
                    continue;
                }
                for(int searchColumn = startPosition.getColumn() + directionTerm;
                        searchColumn >= 0 && searchColumn < size.getColumns();
                        searchColumn += directionTerm) {
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
        TerminalSize size = getSize();
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
}
