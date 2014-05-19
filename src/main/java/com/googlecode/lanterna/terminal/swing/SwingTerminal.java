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
 * Copyright (C) 2010-2014 Martin
 */
package com.googlecode.lanterna.terminal.swing;

import com.googlecode.lanterna.input.KeyDecodingProfile;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.AbstractTerminal;
import com.googlecode.lanterna.terminal.IOSafeTerminal;
import com.googlecode.lanterna.terminal.ResizeListener;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.terminal.TextColor;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.swing.JComponent;

/**
 * This class provides a Swing implementation of the Terminal interface that is an embeddable component you can put into
 * a Swing container. The class has static helper methods for opening a new frame with a SwingTerminal as its content,
 * similar to how the SwingTerminal used to work in earlier versions of lanterna.
 * @author martin
 */
public class SwingTerminal extends JComponent {

    private final SwingTerminalFontConfiguration fontConfiguration;
    private final SwingTerminalColorConfiguration colorConfiguration;
    private final TextBuffer mainBuffer;
    private final TextBuffer privateModeBuffer;
    private final TerminalImplementation terminalImplementation;

    private TextBuffer currentBuffer;

    public SwingTerminal() {
        this(SwingTerminalFontConfiguration.DEFAULT,
                SwingTerminalColorConfiguration.DEFAULT,
                0);
    }

    /**
     * Creates a new SwingTerminal component.
     * @param fontConfiguration
     * @param colorConfiguration
     * @param lineBufferScrollbackSize How many lines of scrollback to save
     */
    public SwingTerminal(
            SwingTerminalFontConfiguration fontConfiguration,
            SwingTerminalColorConfiguration colorConfiguration,
            int lineBufferScrollbackSize) {

                //This is kind of meaningless since we don't know how large the
                //component is at this point, but we should set it to something
        this.terminalImplementation = new TerminalImplementation(new TerminalSize(80, 20));
        this.fontConfiguration = fontConfiguration;
        this.colorConfiguration = colorConfiguration;

        this.mainBuffer = new TextBuffer(lineBufferScrollbackSize, terminalImplementation.getTerminalSize());
        this.privateModeBuffer = new TextBuffer(0, terminalImplementation.getTerminalSize());
        this.currentBuffer = mainBuffer;    //Always start with the active buffer

        //Prevent us from shrinking beyond one character
        setMinimumSize(new Dimension(fontConfiguration.getFontWidth(), fontConfiguration.getFontHeight()));
    }

    ///////////
    // First implement all the Swing-related methods
    ///////////
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(fontConfiguration.getFontWidth() * terminalImplementation.getTerminalSize().getColumns(),
                fontConfiguration.getFontHeight() * terminalImplementation.getTerminalSize().getRows());
    }

    @Override
    protected void paintComponent(Graphics g) {
        //First, resize the buffer width/height if necessary
        int fontWidth = fontConfiguration.getFontWidth();
        int fontHeight = fontConfiguration.getFontHeight();
        int widthInNumberOfCharacters = getWidth() / fontWidth;
        int visibleRows = getHeight() / fontHeight;

        currentBuffer.readjust(widthInNumberOfCharacters, visibleRows);
        terminalImplementation.setTerminalSize(terminalImplementation.getTerminalSize().withColumns(widthInNumberOfCharacters).withRows(visibleRows));

        System.out.println("Painting with width = " + getWidth() + " and height = " + getHeight());

        //Draw line by line, character by character
        int rowIndex = 0;
        for(List<TerminalCharacter> row: currentBuffer.getVisibleLines(visibleRows, 0)) {
            for(int columnIndex = 0; columnIndex < row.size(); columnIndex++) {
                TerminalCharacter character = row.get(columnIndex);
                g.setColor(character.getBackgroundColor());
                g.fillRect(columnIndex * fontWidth, rowIndex * fontHeight, fontWidth, fontHeight);
                g.setColor(character.getForegroundColor());
                g.setFont(fontConfiguration.getFontForCharacter(character.getCharacter()));
                g.drawString(Character.toString(character.getCharacter()), columnIndex * fontWidth, (rowIndex + 1) * fontHeight);
            }
            rowIndex++;
        }

        g.dispose();
    }

    public IOSafeTerminal getTerminal() {
        return terminalImplementation;
    }

    private class TerminalImplementation extends AbstractTerminal implements IOSafeTerminal {
        private TerminalSize terminalSize;
        private TerminalPosition currentPosition;
        private Color foregroundColor;
        private Color backgroundColor;

        public TerminalImplementation(TerminalSize terminalSize) {
            this.terminalSize = terminalSize;
            this.currentPosition = TerminalPosition.TOP_LEFT_CORNER;
            this.foregroundColor = Color.WHITE;
            this.backgroundColor = Color.BLACK;

            //Initialize lastKnownSize in AbstractTerminal
            onResized(terminalSize.getColumns(), terminalSize.getRows());
        }

        ///////////
        // Now implement all Terminal-related methods
        ///////////
        @Override
        public KeyStroke readInput() throws IOException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void addKeyDecodingProfile(KeyDecodingProfile profile) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void enterPrivateMode() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void exitPrivateMode() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void clearScreen() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void moveCursor(int x, int y) {
            TerminalSize size = getTerminalSize();
            if(x < 0) {
                x = 0;
            }
            else if(x >= size.getColumns()) {
                x = size.getColumns() - 1;
            }
            if(y < 0) {
                y = 0;
            }
            else if(y >= size.getRows()) {
                y = size.getRows() - 1;
            }
            currentPosition = currentPosition.withColumn(x).withRow(y);
        }

        @Override
        public void setCursorVisible(boolean visible) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void putCharacter(char c) {
            currentBuffer.setCharacter(getTerminalSize(), currentPosition, new TerminalCharacter(c, foregroundColor, backgroundColor));
            repaint();
        }

        @Override
        public void enableSGR(SGR sgr) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void disableSGR(SGR sgr) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void resetAllSGR() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void applyForegroundColor(ANSIColor color) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void applyForegroundColor(int index) {
            backgroundColor = Color.WHITE;
        }

        @Override
        public void applyForegroundColor(int r, int g, int b) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void applyBackgroundColor(ANSIColor color) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void applyBackgroundColor(int index) {
            backgroundColor = Color.BLACK;
        }

        @Override
        public void applyBackgroundColor(int r, int g, int b) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        private void setTerminalSize(TerminalSize terminalSize) {
            onResized(terminalSize.getColumns(), terminalSize.getRows());
            this.terminalSize = terminalSize;
        }

        @Override
        public TerminalSize getTerminalSize() {
            return terminalSize;
        }

        @Override
        public byte[] enquireTerminal(int timeout, TimeUnit timeoutUnit) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void flush() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
}
