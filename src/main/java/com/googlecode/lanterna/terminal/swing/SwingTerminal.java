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
import com.googlecode.lanterna.terminal.ResizeListener;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.terminal.TextColor;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.swing.JComponent;

/**
 * This class provides a Swing implementation of the Terminal interface that is an embeddable component you can put into
 * a Swing container. The class has static helper methods for opening a new frame with a SwingTerminal as its content,
 * similar to how the SwingTerminal used to work in earlier versions of lanterna.
 * @author martin
 */
public class SwingTerminal extends JComponent implements Terminal {

    private static final TerminalCharacter DEFAULT_CHARACTER = new TerminalCharacter();

    private final SwingTerminalFontConfiguration fontConfiguration;
    private final SwingTerminalColorConfiguration colorConfiguration;
    private final int lineBufferScrollbackSize;
    private final LinkedList<List<TerminalCharacter>> lineBuffer;

    private TerminalSize terminalSize;

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

        this.fontConfiguration = fontConfiguration;
        this.colorConfiguration = colorConfiguration;
        this.lineBufferScrollbackSize = lineBufferScrollbackSize;
        this.lineBuffer = new LinkedList<List<TerminalCharacter>>();

        this.terminalSize = new TerminalSize(80, 20);   //This is kind of meaningless since we don't know how large the
                                                        //component is at this point, but we should set it to something

        //Initialize the content to empty
        for(int y = 0; y < terminalSize.getRows(); y++) {
            lineBuffer.add(newEmptyLine());
        }

        //Prevent us from shrinking beyond one character
        setMinimumSize(new Dimension(fontConfiguration.getFontWidth(), fontConfiguration.getFontHeight()));
    }

    ///////////
    // First implement all the Swing-related methods
    ///////////
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(fontConfiguration.getFontWidth() * terminalSize.getColumns(),
                fontConfiguration.getFontHeight() * terminalSize.getRows());
    }

    @Override
    protected void paintComponent(Graphics g) {
        //First, resize the buffer width if necessary
        int widthInNumberOfCharacters = getWidth() / fontConfiguration.getFontWidth();
        if(widthInNumberOfCharacters > terminalSize.getColumns()) {
            for(List<TerminalCharacter> line: lineBuffer) {
                for(int i = 0; i < widthInNumberOfCharacters - terminalSize.getColumns(); i++) {
                    line.add(DEFAULT_CHARACTER);
                }
            }
            terminalSize = terminalSize.withColumns(widthInNumberOfCharacters);
        }
        else if(widthInNumberOfCharacters < terminalSize.getColumns()) {
            for(List<TerminalCharacter> line: lineBuffer) {
                for(int i = 0; i < terminalSize.getColumns() - widthInNumberOfCharacters; i++) {
                    line.remove(line.size() - 1);
                }
            }
            terminalSize = terminalSize.withColumns(widthInNumberOfCharacters);
        }

        System.out.println("Painting with width = " + getWidth() + " and height = " + getHeight());

        //Fill the buffer height if necessary
        int visibleRows = getHeight() / fontConfiguration.getFontHeight();
        if(visibleRows > lineBuffer.size()) {
            while(visibleRows > lineBuffer.size()) {
                lineBuffer.addFirst(newEmptyLine());
            }
            terminalSize = terminalSize.withRows(visibleRows);
        }

        //Draw line by line, character by character
        for(int rowIndex = lineBuffer.size() - terminalSize.getRows(); rowIndex < lineBuffer.size(); rowIndex++) {
            for(int columnIndex = 0; columnIndex < terminalSize.getColumns(); columnIndex++) {
                
            }
        }

        g.dispose();
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setCursorVisible(boolean visible) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void putCharacter(char c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    public void applyForegroundColor(TextColor color) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void applyForegroundColor(ANSIColor color) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void applyForegroundColor(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void applyForegroundColor(int r, int g, int b) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void applyBackgroundColor(TextColor color) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void applyBackgroundColor(ANSIColor color) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void applyBackgroundColor(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void applyBackgroundColor(int r, int g, int b) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addResizeListener(ResizeListener listener) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeResizeListener(ResizeListener listener) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TerminalSize getTerminalSize() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public byte[] enquireTerminal(int timeout, TimeUnit timeoutUnit) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void flush() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private List<TerminalCharacter> newEmptyLine() {
        List<TerminalCharacter> row = new ArrayList<TerminalCharacter>();
        for(int x = 0; x < terminalSize.getColumns(); x++) {
            row.add(DEFAULT_CHARACTER);
        }
        return row;
    }

    private static class TerminalCharacter {

    }
}
