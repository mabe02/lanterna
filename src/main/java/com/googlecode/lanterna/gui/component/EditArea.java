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
 * Copyright (C) 2013 David Truman
 */
package com.googlecode.lanterna.gui.component;

import com.googlecode.lanterna.gui.Interactable;
import com.googlecode.lanterna.gui.TextGraphics;
import com.googlecode.lanterna.gui.Theme;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.Terminal.Color;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;
import java.util.Scanner;

/**
 *
 * @author David John Truman
 */
public class EditArea extends AbstractInteractableComponent {
    private TerminalSize prefferedSize;
    private TerminalSize requestedSize;
    private TerminalSize maximumSize;
    private TerminalSize minimumSize;
    private int numberOfCols;
    private int numberOfRows;
    private StringBuilder dataBuffer;
    private int minimumCols = 20;
    private int minimumRows = 5;
    private char nullChar = 1;
    private int charlimit = 0;
    private int curx = 0;
    private int cury = 0;
    private TextGrid grid;
    private int rowOffset = 0;
    private int colOffset = 0;
    private boolean lockPreferredSize = false;
    private boolean solution1 = false;

    public EditArea() {
        this(null);
    }

    public void fix_issue() {
        if (curx < 0) {
            curx = 0;
        }
    }

    public void setCharacterLimit(int limit) {
        charlimit = limit;
    }

    public TerminalSize getDataSize() {
        int dataWidth = 0;
        int dataHeight = 0;
        String Line;
        Scanner scan = new Scanner(dataBuffer.toString());

        while (scan.hasNext()) {
            Line = scan.nextLine();
            if (Line.length() > dataWidth) {
                dataWidth = Line.length();
            }
            dataHeight++;
        }

        return new TerminalSize(dataWidth, dataHeight);
    }

    public EditArea(TerminalSize preferredSize, String text) {
        //TextGrid.countLFchars(text);
        dataBuffer = new StringBuilder(text);
        requestedSize = preferredSize;

        if (preferredSize == null) {
            preferredSize = calculatePreferredSize();
        }
        this.prefferedSize = preferredSize;

        minimumSize = new TerminalSize(minimumCols, minimumRows);
        maximumSize = null;

        grid = new TextGrid(text);
    }

    public EditArea(TerminalSize preferredSize, String text, boolean lockPreferredSize) {
        this(preferredSize, text);
        this.lockPreferredSize = lockPreferredSize;
    }

    public EditArea(TerminalSize preferredSize) {
        this(preferredSize, "");
    }

    @Override
    protected TerminalSize calculatePreferredSize() {
        if (lockPreferredSize) {
            return requestedSize;
        }
        TerminalSize ts = getDataSize();
        if (ts.getColumns() < minimumCols) {
            ts.setColumns(minimumCols);
        }
        if (ts.getRows() < minimumRows) {
            ts.setRows(minimumRows);
        }

        if (ts.getColumns() < requestedSize.getColumns()) {
            ts.setColumns(requestedSize.getColumns());
        }
        if (ts.getRows() < requestedSize.getRows()) {
            ts.setRows(requestedSize.getRows());
        }

        return ts;
    }

    protected void drawDisplay(TextGraphics g) {
        int width = g.getWidth();
        int height = g.getHeight();

        Color fg = Theme.getDefaultTheme().getDefinition(Theme.Category.TEXTBOX).foreground();
        Color bg = Theme.getDefaultTheme().getDefinition(Theme.Category.TEXTBOX).background();

        if (hasFocus()) {
            fg = Theme.getDefaultTheme().getDefinition(Theme.Category.TEXTBOX_FOCUSED).foreground();
            bg = Theme.getDefaultTheme().getDefinition(Theme.Category.TEXTBOX_FOCUSED).background();
        }

        for (int r = 0; r < height; r++) {
            g.setForegroundColor(fg);
            g.setBackgroundColor(bg);
            String line = grid.getDisplayLine(r + rowOffset, colOffset, width);
            g.setBoldMask(false);
            g.drawString(0, r, line);
        }
    }

    @Override
    public void repaint(TextGraphics graphics) {
        numberOfCols = graphics.getWidth();
        numberOfRows = graphics.getHeight();
        drawDisplay(graphics);
        setHotspot(graphics.translateToGlobalCoordinates(new TerminalPosition(curx, cury)));
    }

    private void right() {
        TextGrid.DataGridCel cel = grid.getCel(cury + rowOffset, curx + colOffset + 1);
        if (cel == null) {
            return;
        }


        if (cel.getBufferIndex() != -1) {
            if (curx < (numberOfCols - 1)) {
                curx++;
            }
            else {
                scroll_right();
            }
        }
        else {
        }
    }

    public void scroll_right() {
        if ((numberOfCols + colOffset) < grid.getLineLength(rowOffset + cury) + 1) {
            colOffset++;
        }
    }

    public void scroll_left() {
        if (colOffset > 0) {
            colOffset--;
        }
    }

    public void setData(String data) {
        grid.setBufferData(data);
        curx = 0;
        colOffset = 0;
        rowOffset = 0;
        cury = 0;
        invalidate();

    }

    public String getData() {
        return grid.getDataBuffer();
    }

    private void home() {
        colOffset = 0;
        curx = 0;
    }

    private void page_up() {
        TextGrid.DataGridCel cel;

        if (rowOffset < numberOfRows) {
            cury = 0;
            rowOffset = 0;
        }
        else {
            rowOffset = rowOffset - numberOfRows;
        }

        cel = grid.getCel(cury + rowOffset, curx + colOffset);
        if (cel.getCharater() == cel.NullChar) {
            end();
        }
    }

    private void page_down() {
        TextGrid.DataGridCel cel;
        if (rowOffset + numberOfRows > grid.getNumRows()) {
            cury = grid.getNumRows() - rowOffset;
            /* do nothing */
        }
        else {
            if (grid.getNumRows() < numberOfRows) {
                rowOffset = 0;
                cury = grid.getNumRows() - 1;
            }
            else {
                if ((rowOffset + numberOfRows) >= grid.getNumRows()) {
                    rowOffset = grid.getNumRows() - numberOfRows;
                    cury = numberOfRows - 1;
                }
                else {
                    rowOffset = rowOffset + numberOfRows;
                }
            }
        }

        cel = grid.getCel(cury + rowOffset, curx + colOffset);
        while (cel == null) {
            cury--;
            cel = grid.getCel(cury + rowOffset, curx + colOffset);
        }

        if (cel.getCharater() == cel.NullChar) {
            end();
        }
    }

    private void end() {
        int linelen = grid.getLineLength(cury + rowOffset);

        TextGrid.DataGridCel test = grid.getCel(cury + rowOffset, linelen);

        if (linelen < numberOfCols) {
            colOffset = 0;
            curx = linelen;
        }
        else {
            curx = (numberOfCols - 1);
            colOffset = (linelen - numberOfCols) + 1;
        }
    }

    private void left() {
        if (curx > 0) {
            curx--;
        }
        else {
            if (colOffset > 0) {
                scroll_left();
            }
        }
    }

    private boolean limitReached() {
        if ((charlimit > 0) && (grid.size() >= charlimit)) {
            return true;
        }
        return false;
    }

    private void enter(Key key) {
        if (limitReached()) {
            return;
        }

        TextGrid.DataGridCel cel = grid.getCel(cury + rowOffset, curx + colOffset);

        if (cel == null) {
            grid.appendChar((char) 0x0a);
        }
        else {
            if (cel.getBufferIndex() != -1) {
                grid.insertChar(cel.getBufferIndex(), (char) 0x0a);
            }
            else {
                grid.appendChar((char) 0x0a);
            }
        }
        colOffset = 0;
        curx = 0;
        if (cury == numberOfRows - 1) {
            scroll_down();
        }
        else {
            cury++;
        }
    }

    private void normal(Key key) {
        if (limitReached()) {
            return;
        }

        // If delete comes through don't print it out!
        if (key.getCharacter() == 127) {
            return;
        }

        TextGrid.DataGridCel cel = grid.getCel(cury + rowOffset, curx + colOffset);

        if ((cel != null) && (cel.getBufferIndex() != -1)) {
            grid.insertChar(cel.getBufferIndex(), key.getCharacter());
        }
        else {
            grid.appendChar(key.getCharacter());
        }

        if (curx == numberOfCols - 1) {
            colOffset++;
        }
        else {
            curx++;
        }
    }

    public void delete() {
        TextGrid.DataGridCel cel;
        TextGrid.DataGridCel nextCel;
        cel = grid.getCel(cury + rowOffset, curx + colOffset);


        if (cury + rowOffset == numberOfRows - 1) {
            nextCel = grid.getCel(cury + rowOffset, curx + colOffset + 1);
            if ((nextCel != null) && (nextCel.getCharater() != nextCel.NullChar)) {
                grid.removeChar(cel.getBufferIndex());
            }
        }
        else {
            if (cel.getCharater() == cel.NullChar) {
                // Don't do anything
            }
            else {
                grid.removeChar(cel.getBufferIndex());
            }
        }

    }

    public void backspace() {
        TextGrid.DataGridCel cel;

        if (rowOffset + colOffset + curx + cury == 0) {
            return;
        }

        if (((curx + colOffset) == 0) && ((cury + rowOffset) != 0)) {
            
            cel = grid.getCel(cury + rowOffset - 1, grid.getLineLength(cury + rowOffset - 1));
            cury--;
            end();

            int hack = colOffset;


            if (cel == null) {
                cel = grid.getCel(cury + rowOffset, curx - 1);
            }


            while (cel.getCharater() != 0x0a) {
                try {
                    cel = grid.getCel(cury + rowOffset, hack);
                }
                catch (Exception e) {
                }
                hack++;
            }
        }
        else {
            cel = grid.getCel(cury + rowOffset, curx + colOffset - 1);
            if (colOffset > 0) {
                colOffset--;
            }
            else {
                curx--;
            }
        }
        grid.removeChar(cel.getBufferIndex());
    }

    @Override
    public Interactable.Result keyboardInteraction(Key key) {
        try {
            switch (key.getKind()) {
                case Tab:
                    return Interactable.Result.NEXT_INTERACTABLE_RIGHT;
                case Backspace:
                    backspace();
                    break;
                case ArrowDown:
                    down();
                    break;
                case ArrowUp:
                    up();
                    break;
                case ArrowLeft:
                    left();
                    break;
                case ArrowRight:
                    right();
                    break;
                case Enter:
                    enter(key);
                    break;
                case Delete:
                    delete();
                    break;
                case End:
                    end();
                    break;
                case Home:
                    home();
                    break;
                case PageDown:
                    page_down();
                    break;
                case PageUp:
                    page_up();
                    break;
                case NormalKey:
                    normal(key);
                    break;
                default:
                    return Interactable.Result.EVENT_NOT_HANDLED;

            } // switch  
            return Interactable.Result.EVENT_HANDLED;
        } // try
        finally {
            fix_issue();
            invalidate();
        }
    }

    private void scroll_up() {
        if (rowOffset > 0) {
            rowOffset--;
        }
    }

    private void up() {
        if (cury > 0) {
            cury--;
        }
        else {
            if (rowOffset > 0) {
                scroll_up();
            }
        }

        if (grid.getLineLength(cury + rowOffset) <= curx + colOffset) {

            if (colOffset > grid.getLineLength(cury + rowOffset)) {
                colOffset = grid.getLineLength(cury + rowOffset) - numberOfCols;
                if (colOffset < 0) {
                    colOffset = 0;
                    curx = grid.getLineLength(cury + rowOffset) - 1;
                }
                else {
                    curx = numberOfCols - 1;
                }
            }
            else {
                curx = grid.getLineLength(cury + rowOffset) - colOffset - 1;
            }
        }

    }

    private void down() {
        if ((cury + rowOffset) < grid.getNumRows() - 1) {
            if (grid.getLineLength(cury + rowOffset + 1) <= curx + colOffset) {

                if (colOffset > grid.getLineLength(cury + rowOffset + 1)) {
                    colOffset = grid.getLineLength(cury + rowOffset + 1) - numberOfCols;
                    if (colOffset < 0) {
                        colOffset = 0;
                        curx = grid.getLineLength(cury + rowOffset + 1) - 1;
                    }
                    else {
                        curx = numberOfCols - 1;
                    }
                }
                else {
                    curx = grid.getLineLength(cury + rowOffset + 1) - colOffset - 1;
                }
            }
            if (cury < (numberOfRows - 1)) {
                cury++;
            }
            else {
                scroll_down();
            }

        }
    }

    private void scroll_down() {
        if ((numberOfRows + rowOffset) < grid.getNumRows()) {
            rowOffset++;
        }
    }
}