package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by martin on 20/09/14.
 */
public class InteractableLookupMap {
    private final int[][] lookupMap;
    private final List<Interactable> interactables;

    public InteractableLookupMap(TerminalSize size) {
        lookupMap = new int[size.getRows()][size.getColumns()];
        interactables = new ArrayList<Interactable>();
    }

    public void reset() {
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
        int maxShift = Math.max(startPosition.getColumn(), getSize().getColumns() - startPosition.getColumn());
        for(int xShift = 0; xShift < maxShift; xShift++) {
            for(int modifier: new int[] { 1, -1 }) {
                if(xShift == 0 && modifier == -1) {
                    break;
                }
                int searchColumn = startPosition.getColumn() + (xShift * modifier);
                if(searchColumn < 0 || searchColumn >= getSize().getColumns()) {
                    continue;
                }
                for(int searchRow = startPosition.getRow() + directionTerm;
                        searchRow >= 0 && searchRow < getSize().getRows();
                        searchRow += directionTerm) {
                    int index = lookupMap[searchRow][searchColumn];
                    if(index != -1 && interactables.get(index) != interactable) {
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
        int maxShift = Math.max(startPosition.getRow(), getSize().getRows() - startPosition.getRow());
        for(int yShift = 0; yShift < maxShift; yShift++) {
            for(int modifier: new int[] { 1, -1 }) {
                if(yShift == 0 && modifier == -1) {
                    break;
                }
                int searchRow = startPosition.getRow() + (yShift * modifier);
                if(searchRow < 0 || searchRow >= getSize().getRows()) {
                    continue;
                }
                for(int searchColumn = startPosition.getColumn() + directionTerm;
                        searchColumn >= 0 && searchColumn < getSize().getColumns();
                        searchColumn += directionTerm) {
                    int index = lookupMap[searchColumn][searchColumn];
                    if(index != -1 && interactables.get(index) != interactable) {
                        return interactables.get(index);
                    }
                }
            }
        }
        return null;
    }
}
