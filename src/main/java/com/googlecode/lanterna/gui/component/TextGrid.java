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

import java.util.ArrayList;

/**
 * Used by EditArea
 * 
 * @author David John Truman
 * @author Nicolas Pellegrin
 */
public class TextGrid {

    public class DataGridCel {
        private char character;
        private char displayCharacter;
        private int bufferIndex;
        // Constants MUST be static final (otherwise it waste memory)
        private static final char NullDisplayChar = ' ';
        private static final char LFDisplayChar = ' ';
        // Will be accessed outside
        public static final char NullChar = 1;

        public DataGridCel() {
            this.character = NullChar;
            this.bufferIndex = -1;
            this.displayCharacter = NullDisplayChar;
        }

        public DataGridCel(char character, int bufferIndex) {
            this.character = character;
            this.bufferIndex = bufferIndex;

            switch (character) {
            case 0x0a:
                displayCharacter = LFDisplayChar;
                break;
            case NullChar:
                displayCharacter = NullDisplayChar;
                break;
            default:
                displayCharacter = character;
                break;
            }
        }

        public DataGridCel(int bufferIndex) {
            this.character = NullChar;
            this.bufferIndex = bufferIndex;
            this.displayCharacter = NullDisplayChar;
        }

        public int getBufferIndex() {
            return bufferIndex;
        }

        public char getCharacter() {
            return character;
        }

        public char getDisplayCharacter() {
            return displayCharacter;
        }
    }

    private static class LineScanner implements java.util.Iterator<Object> {
        private ArrayList<String> data;
        private int lineCount;
        private int current;

        LineScanner(String text) {
            int c = 0;
            data = new ArrayList<String>();
            String tmp = "";
            lineCount = 1;
            current = 0;

            while (c < text.length()) {
                tmp = tmp + Character.toString(text.charAt(c));
                if (text.charAt(c) == 0x0a) {
                    data.add(tmp);
                    tmp = "";
                    lineCount++;
                }
                c++;
            }
            if (!tmp.matches("")) {
                data.add(tmp);
            }
        }

        @Override
        public boolean hasNext() {
            if (current < lineCount) {
                return true;
            }
            return false;
        }

        @Override
        public Object next() {
            current++;
            if (data.size() < current) {
                return "";
            } else {
                return data.get(current - 1);
            }
        }

        public String nextLine() {
            return (String) next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    private StringBuilder dataBuffer;

    private DataGridCel[][] grid;
    private int numberOfCols;
    private int numberOfRows;
    private char NullDisplayChar = ' ';

    public TextGrid() {
        dataBuffer = new StringBuilder("");
        numberOfCols = 0;
        numberOfRows = 0;
        grid = new DataGridCel[numberOfRows][numberOfCols];
    }

    public TextGrid(String data) {
        dataBuffer = new StringBuilder(lineFeedConvert(data, true));
        setGrid();
    }

    void appendChar(char character) {
        if (dataBuffer.toString().length() != 0) {
            dataBuffer.append(character);
        } else {
            dataBuffer = new StringBuilder(Character.toString(character));
        }
        setGrid();
    }

    protected DataGridCel getCel(int row, int col) {
        try {
            return grid[row][col];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public String getDataBuffer() {
        return lineFeedConvert(dataBuffer.toString(), false);
    }

    public String getDisplayLine(int row, int offset, int length) {
        StringBuilder sb = new StringBuilder();
        char tmp;
        for (int c = offset; c < offset + length; c++) {

            if (row < numberOfRows && c < numberOfCols) {
                tmp = grid[row][c].getDisplayCharacter();
            } else {
                tmp = NullDisplayChar;
            }

            sb.append(tmp);
        }
        return sb.toString();
    }

    public int getLineLength(int row) {
        int count = 0;
        while (count < grid[row].length) {
            if (grid[row][count].getCharacter() == 0x0a
                    || grid[row][count].getCharacter() == DataGridCel.NullChar) {
                break;
            }
            count++;
        }
        return count;
    }

    public int getNumRows() {
        return numberOfRows;
    }

    public void insertChar(int bufferIndex, char character) {
        dataBuffer.insert(bufferIndex, character);
        setGrid();
    }

    public String lineFeedConvert(String str, boolean input) {
        if (input) {
            return str.replaceAll(System.getProperty("line.separator"),
                    Character.toString((char) 0x0a));
        } else {
            return str.replaceAll(Character.toString((char) 0x0a),
                    System.getProperty("line.separator"));
        }
    }

    public void removeChar(int bufferIndex) {
        dataBuffer.deleteCharAt(bufferIndex);
        setGrid();
    }

    public void setBufferData(String data) {
        dataBuffer = new StringBuilder(lineFeedConvert(data, true));
    }

    protected void setGrid() {
        String line;
        int count = 0;
        
        // update geometry
        this.updateDataSize();

        grid = new DataGridCel[numberOfRows][numberOfCols + 1];
        LineScanner scan = new LineScanner(dataBuffer.toString());

        for (int r = 0; r < numberOfRows; r++) {
            if (scan.hasNext()) {
                line = scan.nextLine();
            } else {
                line = new String();
            }

            for (int c = 0; c < numberOfCols; c++) {
                if (c < line.length()) {
                    grid[r][c] = new DataGridCel(line.charAt(c), count);
                    count++;
                } else {
                    if (r == (numberOfRows - 1) && c > 0 && c == line.length()) {
                        grid[r][c] = new DataGridCel(count);
                    } else {
                        grid[r][c] = new DataGridCel(-1);
                    }
                }
            }
        }
    }

    public int size() {
        return dataBuffer.length();
    }

    /**
     * This method updates grid geometry.
     * */
    private void updateDataSize() {
        int dataWidth = 0;
        int dataHeight = 0;
        String Line = null;
        /* Scanner split ignore blank lines on end.... */
        LineScanner scan = new LineScanner(dataBuffer.toString());

        while (scan.hasNext()) {
            Line = scan.nextLine();
            if (Line.length() > dataWidth) {
                dataWidth = Line.length();
            }
            dataHeight++;
        }

        numberOfCols = dataWidth + 1;
        numberOfRows = dataHeight;
    }
}
