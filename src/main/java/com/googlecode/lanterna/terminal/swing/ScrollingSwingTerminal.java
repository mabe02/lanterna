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
 * Copyright (C) 2010-2015 Martin
 */
package com.googlecode.lanterna.terminal.swing;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.IOSafeTerminal;
import com.googlecode.lanterna.terminal.ResizeListener;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import java.awt.BorderLayout;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.swing.JComponent;
import javax.swing.JScrollBar;

/**
 * This is a Swing JComponent that carries a SwingTerminal with a scrollbar, effectively implementing a pseudo-terminal
 * with scrollback history. You can choose the same parameters are for SwingTerminal, they are forwarded, this class
 * mostly deals with linking the SwingTerminal with the scrollbar and having them update each other.
 * @author Martin
 */
@SuppressWarnings("serial")
public class ScrollingSwingTerminal extends JComponent implements IOSafeTerminal {

    private final SwingTerminal swingTerminal;
    private final JScrollBar scrollBar;

    /**
     * Creates a new ScrollingSwingTerminal with all default options
     */
    public ScrollingSwingTerminal() {
        this(SwingTerminalDeviceConfiguration.DEFAULT,
                SwingTerminalFontConfiguration.DEFAULT,
                SwingTerminalColorConfiguration.DEFAULT);
    }

    /**
     * Creates a new ScrollingSwingTerminal with customizable settings.
     * @param deviceConfiguration How to configure the terminal virtual device
     * @param fontConfiguration What kind of fonts to use
     * @param colorConfiguration Which color schema to use for ANSI colors
     */
    @SuppressWarnings({"SameParameterValue", "WeakerAccess"})
    public ScrollingSwingTerminal(
            SwingTerminalDeviceConfiguration deviceConfiguration,
            SwingTerminalFontConfiguration fontConfiguration,
            SwingTerminalColorConfiguration colorConfiguration) {

        this.scrollBar = new JScrollBar(JScrollBar.VERTICAL);
        this.swingTerminal = new SwingTerminal(
                deviceConfiguration,
                fontConfiguration,
                colorConfiguration,
                new ScrollController());

        setLayout(new BorderLayout());
        add(swingTerminal, BorderLayout.CENTER);
        add(scrollBar, BorderLayout.EAST);
        this.scrollBar.setMinimum(0);
        this.scrollBar.setMaximum(20);
        this.scrollBar.setValue(0);
        this.scrollBar.setVisibleAmount(20);
        this.scrollBar.addAdjustmentListener(new ScrollbarListener());
    }

    private class ScrollController implements TerminalScrollController {
        @Override
        public void updateModel(int totalSize, int screenSize) {
            if(scrollBar.getMaximum() != totalSize) {
                int lastMaximum = scrollBar.getMaximum();
                scrollBar.setMaximum(totalSize);
                if(lastMaximum < totalSize &&
                        lastMaximum - scrollBar.getVisibleAmount() - scrollBar.getValue() == 0) {
                    int adjustedValue = scrollBar.getValue() + (totalSize - lastMaximum);
                    scrollBar.setValue(adjustedValue);
                }
            }
            if(scrollBar.getVisibleAmount() != screenSize) {
                if(scrollBar.getValue() + screenSize > scrollBar.getMaximum()) {
                    scrollBar.setValue(scrollBar.getMaximum() - screenSize);
                }
                scrollBar.setVisibleAmount(screenSize);
            }
        }

        @Override
        public int getScrollingOffset() {
            return scrollBar.getMaximum() - scrollBar.getVisibleAmount() - scrollBar.getValue();
        }
    }

    private class ScrollbarListener implements AdjustmentListener {
        @Override
        public synchronized void adjustmentValueChanged(AdjustmentEvent e) {
            swingTerminal.repaint();
        }
    }

    ///////////
    // Delegate all Terminal interface implementations to SwingTerminal
    ///////////
    @Override
    public KeyStroke pollInput() {
        return swingTerminal.pollInput();
    }

    @Override
    public KeyStroke readInput() throws IOException {
        return swingTerminal.readInput();
    }

    @Override
    public void enterPrivateMode() {
        swingTerminal.enterPrivateMode();
    }

    @Override
    public void exitPrivateMode() {
        swingTerminal.exitPrivateMode();
    }

    @Override
    public void clearScreen() {
        swingTerminal.clearScreen();
    }

    @Override
    public void setCursorPosition(int x, int y) {
        swingTerminal.setCursorPosition(x, y);
    }

    @Override
    public void setCursorVisible(boolean visible) {
        swingTerminal.setCursorVisible(visible);
    }

    @Override
    public void putCharacter(char c) {
        swingTerminal.putCharacter(c);
    }

    @Override
    public TextGraphics newTextGraphics() throws IOException {
        return swingTerminal.newTextGraphics();
    }

    @Override
    public void enableSGR(SGR sgr) {
        swingTerminal.enableSGR(sgr);
    }

    @Override
    public void disableSGR(SGR sgr) {
        swingTerminal.disableSGR(sgr);
    }

    @Override
    public void resetColorAndSGR() {
        swingTerminal.resetColorAndSGR();
    }

    @Override
    public void setForegroundColor(TextColor color) {
        swingTerminal.setForegroundColor(color);
    }

    @Override
    public void setBackgroundColor(TextColor color) {
        swingTerminal.setBackgroundColor(color);
    }

    @Override
    public TerminalSize getTerminalSize() {
        return swingTerminal.getTerminalSize();
    }

    @Override
    public byte[] enquireTerminal(int timeout, TimeUnit timeoutUnit) {
        return swingTerminal.enquireTerminal(timeout, timeoutUnit);
    }

    @Override
    public void flush() {
        swingTerminal.flush();
    }

    @Override
    public void addResizeListener(ResizeListener listener) {
        swingTerminal.addResizeListener(listener);
    }

    @Override
    public void removeResizeListener(ResizeListener listener) {
        swingTerminal.removeResizeListener(listener);
    }
}
