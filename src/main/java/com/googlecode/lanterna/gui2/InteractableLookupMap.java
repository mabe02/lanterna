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
        TerminalPosition startPosition = interactable.toRootContainer(interactable.getCursorLocation());
        for(int xShift = 0; xShift < Math.max(startPosition.getColumn(), getSize().getColumns() - startPosition.getColumn()); xShift++) {
            for(int modifier: new int[] { 1, -1 }) {
                if(xShift == 0 && modifier == -1) {
                    break;
                }
                xShift *= modifier;
                int searchColumn = startPosition.getColumn() + xShift;
                if(searchColumn < 0 || searchColumn >= getSize().getColumns()) {
                    continue;
                }
                for(int searchRow = startPosition.getRow() - 1; searchRow >= 0; searchRow--) {
                    int index = lookupMap[searchRow][searchColumn];
                    if(index != -1 && interactables.get(index) != interactable) {
                        return interactables.get(index);
                    }
                }
            }
        }
        return null;
    }

    public Interactable findNextDown(Interactable interactable) {
        TerminalPosition startPosition = interactable.toRootContainer(interactable.getCursorLocation());
        for(int xShift = 0; xShift < Math.max(startPosition.getColumn(), getSize().getColumns() - startPosition.getColumn()); xShift++) {
            for(int modifier: new int[] { 1, -1 }) {
                if(xShift == 0 && modifier == -1) {
                    break;
                }
                xShift *= modifier;
                int searchColumn = startPosition.getColumn() + xShift;
                if(searchColumn < 0 || searchColumn >= getSize().getColumns()) {
                    continue;
                }
                for(int searchRow = startPosition.getRow() + 1; searchRow < getSize().getRows(); searchRow++) {
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
        return null;
    }

    public Interactable findNextRight(Interactable interactable) {
        return null;
    }
}
