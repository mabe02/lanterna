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

import java.util.Scanner;

import com.googlecode.lanterna.gui.Interactable;
import com.googlecode.lanterna.gui.TextGraphics;
import com.googlecode.lanterna.gui.Theme;
import com.googlecode.lanterna.gui.component.TextGrid.DataGridCel;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.Terminal.Color;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;

/**
 * Multi-line text edit component
 * @author David John Truman
 * @author Nicolas Pellegrin
 */
public class EditArea extends AbstractInteractableComponent {
    private int minimumCols = 20;
    private int minimumRows = 5;
    private int charlimit = 0;
    private int currentCol = 0;
    private int currentRow = 0;
    private int rowOffset = 0;
    private int colOffset = 0;
    private boolean lockPreferredSize = false;

    private TerminalSize requestedSize;
    private int numberOfCols;
    private int numberOfRows;
    private StringBuilder dataBuffer;
    private TextGrid grid;

    public EditArea() {
        this(null);
    }

    public EditArea(TerminalSize preferredSize) {
        this(preferredSize, "");
    }

    public EditArea(TerminalSize preferredSize, String text) {
        dataBuffer = new StringBuilder(text);
        requestedSize = preferredSize;

        if (preferredSize == null) {
            preferredSize = calculatePreferredSize();
        }
        grid = new TextGrid(text);
    }

    public EditArea(TerminalSize preferredSize, String text,
            boolean lockPreferredSize) {
        this(preferredSize, text);
        this.lockPreferredSize = lockPreferredSize;
    }

    /**
     * Action for "Backspace" key
     * */
    public void backspace() {
        TextGrid.DataGridCel cel;

        // Do nothing if we are at the beginning
        if (rowOffset + colOffset + currentCol + currentRow == 0) {
            return;
        }

        if ((currentCol + colOffset) == 0 && (currentRow + rowOffset) != 0) {
            // Get a \n in the previous line (user pressed backspace at the first character of the line)
            cel = grid.getCel(currentRow + rowOffset - 1, grid.getLineLength(currentRow + rowOffset - 1));

            if (currentRow > 0) {
                // Move cursor upwards
                currentRow--;
            } else {
                // Move up if we are at the top
                scrollUp();
            }

            // Go at the end of line
            end();

            int hack = colOffset;

            if (cel == null) {
                cel = grid.getCel(currentRow + rowOffset, currentCol - 1);
            }

            while (cel.getCharacter() != 0x0a) {
                cel = grid.getCel(currentRow + rowOffset, hack);
                hack++;
            }
        } else {
            // Get a single character
            cel = grid.getCel(currentRow + rowOffset, currentCol + colOffset - 1);
            if (colOffset > 0) {
                colOffset--;
            } else {
                currentCol--;
            }
        }
        // Remove the character
        grid.removeChar(cel.getBufferIndex());
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

    /**
     * Action for "Del" key.
     * */
    public void delete() {
        TextGrid.DataGridCel cel;
        TextGrid.DataGridCel nextCel;
        cel = grid.getCel(currentRow + rowOffset, currentCol + colOffset);

        if (currentRow + rowOffset == numberOfRows - 1) {
            nextCel = grid.getCel(currentRow + rowOffset, currentCol
                    + colOffset + 1);
            if (nextCel != null
                    && nextCel.getCharacter() != DataGridCel.NullChar) {
                grid.removeChar(cel.getBufferIndex());
            }
        } else {
            if (cel.getCharacter() != DataGridCel.NullChar) {
                grid.removeChar(cel.getBufferIndex());
            }
        }
    }

    /**
     * Action for "Arrow down" key.
     * */
    private void down() {
        if ((currentRow + rowOffset) < grid.getNumRows() - 1) {
            if (grid.getLineLength(currentRow + rowOffset + 1) <= currentCol
                    + colOffset) {

                if (colOffset > grid.getLineLength(currentRow + rowOffset + 1)) {
                    colOffset = grid.getLineLength(currentRow + rowOffset + 1)
                            - numberOfCols;
                    if (colOffset < 0) {
                        colOffset = 0;
                        currentCol = grid.getLineLength(currentRow + rowOffset
                                + 1) - 1;
                    } else {
                        currentCol = numberOfCols - 1;
                    }
                } else {
                    currentCol = grid.getLineLength(currentRow + rowOffset + 1)
                            - colOffset - 1;
                }
            }
            if (currentRow < (numberOfRows - 1)) {
                currentRow++;
            } else {
                scrollDown();
            }

        }
    }

    protected void drawDisplay(TextGraphics g) {
        int width = g.getWidth();
        int height = g.getHeight();

        Color fg = Theme.getDefaultTheme()
                .getDefinition(Theme.Category.TEXTBOX).foreground();
        Color bg = Theme.getDefaultTheme()
                .getDefinition(Theme.Category.TEXTBOX).background();

        if (hasFocus()) {
            fg = Theme.getDefaultTheme()
                    .getDefinition(Theme.Category.TEXTBOX_FOCUSED).foreground();
            bg = Theme.getDefaultTheme()
                    .getDefinition(Theme.Category.TEXTBOX_FOCUSED).background();
        }

        for (int r = 0; r < height; r++) {
            g.setForegroundColor(fg);
            g.setBackgroundColor(bg);
            String line = grid.getDisplayLine(r + rowOffset, colOffset, width);
            g.setBoldMask(false);
            g.drawString(0, r, line);
        }
    }

    /**
     * Action for "End" key
     * */
    private void end() {
        int linelen = grid.getLineLength(currentRow + rowOffset);

        grid.getCel(currentRow + rowOffset, linelen);

        if (linelen < numberOfCols) {
            colOffset = 0;
            currentCol = linelen;
        } else {
            currentCol = numberOfCols - 1;
            colOffset = linelen - numberOfCols + 1;
        }
    }

    /**
     * Action for "Enter" key
     * */
    private void enter(Key key) {
        if (limitReached()) {
            return;
        }

        TextGrid.DataGridCel cel = grid.getCel(currentRow + rowOffset,
                currentCol + colOffset);

        if (cel == null) {
            grid.appendChar((char) 0x0a);
        } else {
            if (cel.getBufferIndex() != -1) {
                grid.insertChar(cel.getBufferIndex(), (char) 0x0a);
            } else {
                grid.appendChar((char) 0x0a);
            }
        }
        colOffset = 0;
        currentCol = 0;
        if (currentRow == numberOfRows - 1) {
            scrollDown();
        } else {
            currentRow++;
        }
    }

    // FIXME: why this ?
    public void fix_issue() {
        if (currentCol < 0) {
            currentCol = 0;
        }
    }

    public String getData() {
        return grid.getDataBuffer();
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

        // Resource leaked
        scan.close();

        return new TerminalSize(dataWidth, dataHeight);
    }

    /**
     * Action for "Home" key
     * */
    private void home() {
        colOffset = 0;
        currentCol = 0;
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
                pageDown();
                break;
            case PageUp:
                pageUp();
                break;
            case NormalKey:
                normal(key);
                break;
            default:
                return Interactable.Result.EVENT_NOT_HANDLED;

            } // End switch
            return Interactable.Result.EVENT_HANDLED;
        } // try
        finally {
            fix_issue();
            invalidate();
        }
    }

    /**
     * Action for the "Arrow left" key.
     * */
    private void left() {
        if (currentCol > 0) {
            currentCol--;
        } else {
            // Unnecessary colOffset > 0 : done in scrollLeft.
            scrollLeft();
        }
    }

    /**
     * Checks if the line limit is reached.
     * */
    private boolean limitReached() {
        if (charlimit > 0 && grid.size() >= charlimit) {
            return true;
        }
        return false;
    }

    /**
     * Action for "normal" key (a-z, 0-9, and symbols).
     * */
    private void normal(Key key) {
        if (limitReached()) {
            return;
        }

        // If delete comes through don't print it out!
        if (key.getCharacter() == 127) {
            return;
        }

        TextGrid.DataGridCel cel = grid.getCel(currentRow + rowOffset,
                currentCol + colOffset);

        if (cel != null && cel.getBufferIndex() != -1) {
            grid.insertChar(cel.getBufferIndex(), key.getCharacter());
        } else {
            grid.appendChar(key.getCharacter());
        }

        if (currentCol == numberOfCols - 1) {
            colOffset++;
        } else {
            currentCol++;
        }
    }

    /**
     * Action for the "Page down" key.
     * */
    private void pageDown() {
        TextGrid.DataGridCel cel;
        if (rowOffset + numberOfRows > grid.getNumRows()) {
            currentRow = grid.getNumRows() - rowOffset;
        } else {
            if (grid.getNumRows() < numberOfRows) {
                rowOffset = 0;
                currentRow = grid.getNumRows() - 1;
            } else {
                if ((rowOffset + numberOfRows) >= grid.getNumRows()) {
                    rowOffset = grid.getNumRows() - numberOfRows;
                    currentRow = numberOfRows - 1;
                } else {
                    rowOffset = rowOffset + numberOfRows;
                }
            }
        }

        cel = grid.getCel(currentRow + rowOffset, currentCol + colOffset);
        while (cel == null) {
            currentRow--;
            cel = grid.getCel(currentRow + rowOffset, currentCol + colOffset);
        }

        if (cel.getCharacter() == DataGridCel.NullChar) {
            end();
        }
    }

    /**
     * Action for the "Page up" key.
     * */
    private void pageUp() {
        TextGrid.DataGridCel cel;

        if (rowOffset < numberOfRows) {
            currentRow = 0;
            rowOffset = 0;
        } else {
            rowOffset = rowOffset - numberOfRows;
        }

        cel = grid.getCel(currentRow + rowOffset, currentCol + colOffset);
        if (cel.getCharacter() == DataGridCel.NullChar) {
            end();
        }
    }

    @Override
    public void repaint(TextGraphics graphics) {
        numberOfCols = graphics.getWidth();
        numberOfRows = graphics.getHeight();
        drawDisplay(graphics);
        setHotspot(graphics.translateToGlobalCoordinates(new TerminalPosition(
                currentCol, currentRow)));
    }

    /**
     * Action for the "Arrow right" key.
     * */
    private void right() {
        TextGrid.DataGridCel cel = grid.getCel(currentRow + rowOffset,
                currentCol + colOffset + 1);
        if (cel == null) {
            return;
        }

        if (cel.getBufferIndex() != -1) {
            if (currentCol < (numberOfCols - 1)) {
                currentCol++;
            } else {
                scrollRight();
            }
        }
    }

    /**
     * Scroll down all text in the area.
     * */
    private void scrollDown() {
        if ((numberOfRows + rowOffset) < grid.getNumRows()) {
            rowOffset++;
        }
    }

    /**
     * Scroll left all text in the area.
     * */
    public void scrollLeft() {
        if (colOffset > 0) {
            colOffset--;
        }
    }

    /**
     * Scroll right all text in the area.
     * */
    public void scrollRight() {
        if ((numberOfCols + colOffset) < grid.getLineLength(rowOffset + currentRow) + 1) {
            colOffset++;
        }
    }

    /**
     * Scroll up all text in the area.
     * */
    private void scrollUp() {
        if (rowOffset > 0) {
            rowOffset--;
        }
    }

   /**
    * Set the line limit.
    */
    public void setCharacterLimit(int limit) {
        charlimit = limit;
    }

    /**
     * Change all the text in the area.
     * */
    public void setData(String data) {
        grid.setBufferData(data);
        currentCol = 0;
        colOffset = 0;
        rowOffset = 0;
        currentRow = 0;
        invalidate();
    }

    /**
     * Action for the "Arrow up" key.
     * */
    private void up() {
        if (currentRow > 0) {
            currentRow--;
        } else {
            // Unnecessary check rowOffset > 0 : done in scrollUp.
            scrollUp();
        }

        if (grid.getLineLength(currentRow + rowOffset) <= currentCol + colOffset) {

            if (colOffset > grid.getLineLength(currentRow + rowOffset)) {
                colOffset = grid.getLineLength(currentRow + rowOffset) - numberOfCols;
                if (colOffset < 0) {
                    colOffset = 0;
                    currentCol = grid.getLineLength(currentRow + rowOffset) - 1;
                } else {
                    currentCol = numberOfCols - 1;
                }
            } else {
                currentCol = grid.getLineLength(currentRow + rowOffset) - colOffset - 1;
            }
        }
    }
}