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

import com.googlecode.lanterna.terminal.TerminalSize;
import java.util.ArrayList;

/**
 * Used by EditArea
 * @author David John Truman
 */
public class TextGrid {

    public void insertChar(int bufferIndex, char character) {
        DataBuffer.insert(bufferIndex, character);
        setGrid();
    }

    public int size() {
        return DataBuffer.length();
    }

    public void removeChar(int bufferIndex) {
        DataBuffer.deleteCharAt(bufferIndex);
        setGrid();
    }
    private StringBuilder DataBuffer;
    private DataGridCel[][] grid;
    private int numberOfCols;
    private int numberOfRows;
    private char NullDisplayChar = ' ';

    public int getLineLength(int row) {
        int count = 0;
        while (count < grid[row].length) {
            if ((grid[row][count].getCharater() == 0x0a) || (grid[row][count].getCharater() == grid[row][count].NullChar)) {
                break;
            }
            count++;
        }
        return count;
    }

    public String lineFeedConvert(String str, boolean input) {
        if (input) {
            return str.replaceAll(System.getProperty("line.separator"), Character.toString((char) 0x0a));
        }
        else {
            return str.replaceAll(Character.toString((char) 0x0a), System.getProperty("line.separator"));
        }
    }

    void appendChar(char character) {
        if (DataBuffer.toString().length() != 0) {
            DataBuffer.append(character);
        }
        else {
            DataBuffer = new StringBuilder(Character.toString(character));
        }
        setGrid();
    }

    public class DataGridCel {

        private char character;
        private char displayCharacter;
        private int bufferIndex;
        private boolean debug = false;      // Setting to true will show chars for line feed and padding.
        private char NullDisplayChar = ' ';
        private char LFDisplayChar = ' ';
        final char NullChar = 1;

        public void enableDebugMode() {
            if (debug) {
                NullDisplayChar = (char) 45;
                LFDisplayChar = (char) 126;
            }
        }

        public DataGridCel() {
            enableDebugMode();
            this.character = NullChar;
            this.bufferIndex = -1;
            this.displayCharacter = NullDisplayChar; //' ';
        }

        public DataGridCel(int bufferIndex) {
            //this(NullChar, bufferIndex);
            enableDebugMode();
            this.character = NullChar;
            this.bufferIndex = bufferIndex;
            this.displayCharacter = NullDisplayChar;
        }

        public DataGridCel(char character, int bufferIndex) {
            enableDebugMode();
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

        public int getBufferIndex() {
            return bufferIndex;
        }

        public char getCharater() {
            return character;
        }

        public char getDisplayCharacter() {
            return displayCharacter;
        }
    }

    /* Scanner split ignore blank lines on end.... */
    public TerminalSize getDataSize() {
        int dataWidth = 0;
        int dataHeight = 0;

        String Line = null;
        LineScanner scan = new LineScanner(DataBuffer.toString());

        while (scan.hasNext()) {
            Line = scan.nextLine();

            if (Line.length() > dataWidth) {
                dataWidth = Line.length();
            }
            dataHeight++;
        }

        numberOfCols = dataWidth + 1;
        numberOfRows = dataHeight;

        return new TerminalSize(dataWidth + 1, dataHeight);
    }

    public TextGrid() {
        DataBuffer = new StringBuilder("");
        numberOfCols = 0;
        numberOfRows = 0;
        grid = new DataGridCel[numberOfRows][numberOfCols];
    }

    public TextGrid(String data) {
        //countLFchars(data);

        DataBuffer = new StringBuilder(lineFeedConvert(data, true));
        setGrid();
    }

    public String getDataBuffer() {
        return lineFeedConvert(DataBuffer.toString(), false);
    }

    public void setBufferData(String data) {
        DataBuffer = new StringBuilder(lineFeedConvert(data, true));
    }

    public int getNumRows() {
        return numberOfRows;
    }

    public String getDisplayLine(int row, int offset, int length) {
        StringBuilder sb = new StringBuilder();
        char tmp;
        for (int c = offset; c < offset + length; c++) {

            if ((row < numberOfRows) && (c < numberOfCols)) {
                tmp = grid[row][c].getDisplayCharacter();
            }
            else {
                tmp = NullDisplayChar;
            }

            sb.append(tmp);
        }
        return sb.toString();
    }

    protected DataGridCel getCel(int row, int col) {
        try {
            return grid[row][col];
        }
        catch (Exception e) {
            return null;
        }
    }

    protected void setGrid() {
        int count = 0;
        TerminalSize ts = getDataSize();

        grid = new DataGridCel[numberOfRows][numberOfCols + 1];

        String Line;
        LineScanner scan = new LineScanner(DataBuffer.toString());

        DataGridCel lastCell = null;

        for (int r = 0; r < numberOfRows; r++) {


            if (scan.hasNext()) {
                Line = scan.nextLine();
            }
            else {
                Line = new String();
            }


            for (int c = 0; c < numberOfCols; c++) {
                if (c < Line.length()) {
                    grid[r][c] = new DataGridCel(Line.charAt(c), count);
                    lastCell = grid[r][c];
                    count++;
                }
                else {
                    if ((r == (numberOfRows - 1)) && (c > 0) && (c == Line.length())) {
                        grid[r][c] = new DataGridCel(count);
                    }
                    else {
                        grid[r][c] = new DataGridCel(-1);
                    }
                }
            }
        }
        if (lastCell != null) {
            //index
        }
    }

    private static class LineScanner implements java.util.Iterator {
        private ArrayList<String> data;
        private int lineCount;
        private int current;

        public void dump() {
            System.err.println("DUMP:" + data.size());
            for (int i = 0; i < data.size(); i++) {
                System.err.println("[" + data.get(i) + "]");
            }

        }

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
            }
            else {
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
}
