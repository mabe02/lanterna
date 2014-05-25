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

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.IOSafeTerminal;
import com.googlecode.lanterna.terminal.TerminalSize;
import java.awt.Dimension;
import java.awt.Graphics;
import java.nio.charset.Charset;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.Timer;

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
    private final VirtualTerminalImplementation terminalImplementation;
    private final Timer blinkTimer;

    private TextBuffer currentBuffer;
    private boolean cursorIsVisible;
    private String enquiryString;

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
        this.terminalImplementation = new VirtualTerminalImplementation(new TerminalDeviceEmulator(), new TerminalSize(80, 20));
        this.fontConfiguration = fontConfiguration;
        this.colorConfiguration = colorConfiguration;

        this.mainBuffer = new TextBuffer(lineBufferScrollbackSize, terminalImplementation.getTerminalSize());
        this.privateModeBuffer = new TextBuffer(0, terminalImplementation.getTerminalSize());
        this.currentBuffer = mainBuffer;    //Always start with the active buffer
        this.cursorIsVisible = true;        //Always start with an activate and visible cursor
        this.enquiryString = "SwingTerminal";

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

        //Draw line by line, character by character
        int rowIndex = 0;
        for(List<TerminalCharacter> row: currentBuffer.getVisibleLines(visibleRows, 0)) {
            for(int columnIndex = 0; columnIndex < row.size(); columnIndex++) {
                TerminalCharacter character = row.get(columnIndex);
                g.setColor(colorConfiguration.toAWTColor(character.getBackgroundColor(), false, character.isBold()));
                g.fillRect(columnIndex * fontWidth, rowIndex * fontHeight, fontWidth, fontHeight);
                g.setColor(colorConfiguration.toAWTColor(character.getForegroundColor(), true, character.isBold()));
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

    private class TerminalDeviceEmulator implements VirtualTerminalImplementation.DeviceEmulator {
        @Override
        public KeyStroke readInput() {
            return null;
        }

        @Override
        public void enterPrivateMode() {
            SwingTerminal.this.currentBuffer = SwingTerminal.this.privateModeBuffer;
        }

        @Override
        public void exitPrivateMode() {
            SwingTerminal.this.currentBuffer = SwingTerminal.this.mainBuffer;
        }

        @Override
        public TextBuffer getBuffer() {
            return currentBuffer;
        }

        @Override
        public void setCursorVisible(boolean visible) {
            SwingTerminal.this.cursorIsVisible = visible;
        }

        @Override
        public void flush() {
            SwingTerminal.this.repaint();
        }

        @Override
        public byte[] equireTerminal() {
            return SwingTerminal.this.enquiryString.getBytes(Charset.defaultCharset());
        }
    }
}
