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
import com.googlecode.lanterna.terminal.IOSafeTerminal;
import com.googlecode.lanterna.terminal.ResizeListener;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.terminal.TextColor;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.swing.JComponent;
import javax.swing.Timer;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

/**
 * This class provides a Swing implementation of the Terminal interface that is an embeddable component you can put into
 * a Swing container. The class has static helper methods for opening a new frame with a SwingTerminal as its content,
 * similar to how the SwingTerminal used to work in earlier versions of lanterna.
 * @author martin
 */
public class SwingTerminal extends JComponent implements IOSafeTerminal {

    private final SwingTerminalDeviceConfiguration deviceConfiguration;
    private final SwingTerminalFontConfiguration fontConfiguration;
    private final SwingTerminalColorConfiguration colorConfiguration;
    private final TextBuffer mainBuffer;
    private final TextBuffer privateModeBuffer;
    private final VirtualTerminalImplementation terminalImplementation;
    private final Timer blinkTimer;

    private TextBuffer currentBuffer;
    private String enquiryString;

    private volatile boolean cursorIsVisible;
    private volatile boolean blinkOn;

    public SwingTerminal() {
        this(SwingTerminalDeviceConfiguration.DEFAULT,
                SwingTerminalFontConfiguration.DEFAULT,
                SwingTerminalColorConfiguration.DEFAULT);
    }

    /**
     * Creates a new SwingTerminal component.
     * @param deviceConfiguration
     * @param fontConfiguration
     * @param colorConfiguration
     */
    public SwingTerminal(
            SwingTerminalDeviceConfiguration deviceConfiguration,
            SwingTerminalFontConfiguration fontConfiguration,
            SwingTerminalColorConfiguration colorConfiguration) {

        //This is kind of meaningless since we don't know how large the
        //component is at this point, but we should set it to something
        this.terminalImplementation = new VirtualTerminalImplementation(new TerminalDeviceEmulator(), new TerminalSize(80, 20));
        this.deviceConfiguration = deviceConfiguration == null ? SwingTerminalDeviceConfiguration.DEFAULT : deviceConfiguration;
        this.fontConfiguration = fontConfiguration == null ? SwingTerminalFontConfiguration.DEFAULT : fontConfiguration;
        this.colorConfiguration = colorConfiguration == null ? SwingTerminalColorConfiguration.DEFAULT : colorConfiguration;

        this.mainBuffer = new TextBuffer(deviceConfiguration.getLineBufferScrollbackSize(), terminalImplementation.getTerminalSize());
        this.privateModeBuffer = new TextBuffer(0, terminalImplementation.getTerminalSize());
        this.currentBuffer = mainBuffer;    //Always start with the active buffer
        this.cursorIsVisible = true;        //Always start with an activate and visible cursor
        this.enquiryString = "SwingTerminal";
        this.blinkTimer = new Timer(deviceConfiguration.getBlinkLengthInMilliSeconds(), new BlinkTimerCallback());

        //Prevent us from shrinking beyond one character
        setMinimumSize(new Dimension(fontConfiguration.getFontWidth(), fontConfiguration.getFontHeight()));
        addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                blinkTimer.start();
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
                blinkTimer.stop();
            }

            @Override
            public void ancestorMoved(AncestorEvent event) { }
        });
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
        TerminalPosition cursorPosition = terminalImplementation.getCurrentPosition();

        //Fill with black to remove any previous content
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        //Draw line by line, character by character
        int rowIndex = 0;
        for(List<TerminalCharacter> row: currentBuffer.getVisibleLines(visibleRows, 0)) {
            for(int columnIndex = 0; columnIndex < row.size(); columnIndex++) {
                TerminalCharacter character = row.get(columnIndex);
                boolean atCursorLocation = cursorPosition.equals(columnIndex, rowIndex);

                Color foregroundColor = deriveTrueForegroundColor(character, atCursorLocation);
                Color backgroundColor = deriveTrueBackgroundColor(character, atCursorLocation);

                g.setColor(backgroundColor);
                g.fillRect(columnIndex * fontWidth, rowIndex * fontHeight, fontWidth, fontHeight);

                g.setColor(foregroundColor);
                g.setFont(fontConfiguration.getFontForCharacter(character));
                g.drawString(Character.toString(character.getCharacter()), columnIndex * fontWidth, (rowIndex + 1) * fontHeight);

                if(atCursorLocation && deviceConfiguration.getCursorStyle() == SwingTerminalDeviceConfiguration.CursorStyle.DOUBLE_UNDERBAR) {
                    g.setColor(colorConfiguration.toAWTColor(deviceConfiguration.getCursorColor(), false, false));
                    g.fillRect(columnIndex * fontWidth, (rowIndex * fontHeight) + fontHeight - 2, fontWidth, 2);
                }
            }
            rowIndex++;
        }

        g.dispose();
    }

    private Color deriveTrueForegroundColor(TerminalCharacter character, boolean atCursorLocation) {
        TextColor foregroundColor = character.getForegroundColor();
        TextColor backgroundColor = character.getBackgroundColor();
        boolean reverse = character.isReverse();
        boolean blink = character.isBlink();

        if(cursorIsVisible && atCursorLocation) {
            if(deviceConfiguration.getCursorStyle() == SwingTerminalDeviceConfiguration.CursorStyle.REVERSED &&
                    (!deviceConfiguration.isCursorBlinking() || (deviceConfiguration.isCursorBlinking() && blinkOn))) {
                reverse = true;
            }
        }

        if(reverse && (!blink || (blink && !blinkOn))) {
            return colorConfiguration.toAWTColor(backgroundColor, backgroundColor != TextColor.ANSI.DEFAULT, character.isBold());
        }
        else if(!reverse && blink && blinkOn) {
            return colorConfiguration.toAWTColor(backgroundColor, false, character.isBold());
        }
        else {
            return colorConfiguration.toAWTColor(foregroundColor, true, character.isBold());
        }
    }

    private Color deriveTrueBackgroundColor(TerminalCharacter character, boolean atCursorLocation) {
        TextColor foregroundColor = character.getForegroundColor();
        TextColor backgroundColor = character.getBackgroundColor();
        boolean reverse = character.isReverse();

        if(cursorIsVisible && atCursorLocation) {
            if(deviceConfiguration.getCursorStyle() == SwingTerminalDeviceConfiguration.CursorStyle.REVERSED &&
                    (!deviceConfiguration.isCursorBlinking() || (deviceConfiguration.isCursorBlinking() && blinkOn))) {
                reverse = true;
            }
            else if(deviceConfiguration.getCursorStyle() == SwingTerminalDeviceConfiguration.CursorStyle.FIXED_BACKGROUND) {
                backgroundColor = deviceConfiguration.getCursorColor();
            }
        }

        if(reverse) {
            return colorConfiguration.toAWTColor(foregroundColor, backgroundColor == TextColor.ANSI.DEFAULT, character.isBold());
        }
        else {
            return colorConfiguration.toAWTColor(backgroundColor, false, character.isBold());
        }
    }

    ///////////
    // Then delegate all Terminal interface methods to the virtual terminal implementation
    ///////////
    @Override
    public KeyStroke readInput() {
        return terminalImplementation.readInput();
    }

    @Override
    public void addKeyDecodingProfile(KeyDecodingProfile profile) {
        terminalImplementation.addKeyDecodingProfile(profile);
    }

    @Override
    public void enterPrivateMode() {
        terminalImplementation.enterPrivateMode();
    }

    @Override
    public void exitPrivateMode() {
        terminalImplementation.exitPrivateMode();
    }

    @Override
    public void clearScreen() {
        terminalImplementation.clearScreen();
    }

    @Override
    public void moveCursor(int x, int y) {
        terminalImplementation.moveCursor(x, y);
    }

    @Override
    public void setCursorVisible(boolean visible) {
        terminalImplementation.setCursorVisible(visible);
    }

    @Override
    public void putCharacter(char c) {
        terminalImplementation.putCharacter(c);
    }

    @Override
    public void enableSGR(SGR sgr) {
        terminalImplementation.enableSGR(sgr);
    }

    @Override
    public void disableSGR(SGR sgr) {
        terminalImplementation.disableSGR(sgr);
    }

    @Override
    public void resetAllSGR() {
        terminalImplementation.resetAllSGR();
    }

    @Override
    public void applyForegroundColor(TextColor color) {
        terminalImplementation.applyForegroundColor(color);
    }

    @Override
    public void applyBackgroundColor(TextColor color) {
        terminalImplementation.applyBackgroundColor(color);
    }

    @Override
    public TerminalSize getTerminalSize() {
        return terminalImplementation.getTerminalSize();
    }

    @Override
    public byte[] enquireTerminal(int timeout, TimeUnit timeoutUnit) {
        return terminalImplementation.enquireTerminal(timeout, timeoutUnit);
    }

    @Override
    public void flush() {
        terminalImplementation.flush();
    }

    @Override
    public void addResizeListener(ResizeListener listener) {
        terminalImplementation.addResizeListener(listener);
    }

    @Override
    public void removeResizeListener(ResizeListener listener) {
        terminalImplementation.removeResizeListener(listener);
    }

    ///////////
    // Remaining are private internal classes used by SwingTerminal
    ///////////
    private class BlinkTimerCallback implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            blinkOn = !blinkOn;
            repaint();
        }
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
