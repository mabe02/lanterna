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
import java.awt.Container;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * This is a AWT Container that carries an {@link AWTTerminal} with a scrollbar, effectively implementing a
 * pseudo-terminal with scrollback history. You can choose the same parameters are for {@link AWTTerminal}, they are
 * forwarded, this class mostly deals with linking the {@link AWTTerminal} with the scrollbar and having them update
 * each other.
 * @author Martin
 */
@SuppressWarnings("serial")
public class ScrollingAWTTerminal extends Container implements IOSafeTerminal {

    private final AWTTerminal awtTerminal;
    private final Scrollbar scrollBar;

    /**
     * Creates a new {@code ScrollingAWTTerminal} with all default options
     */
    public ScrollingAWTTerminal() {
        this(TerminalEmulatorDeviceConfiguration.getDefault(),
                SwingTerminalFontConfiguration.getDefault(),
                TerminalEmulatorColorConfiguration.getDefault());
    }

    /**
     * Creates a new {@code ScrollingAWTTerminal} with customizable settings.
     * @param deviceConfiguration How to configure the terminal virtual device
     * @param fontConfiguration What kind of fonts to use
     * @param colorConfiguration Which color schema to use for ANSI colors
     */
    @SuppressWarnings({"SameParameterValue", "WeakerAccess"})
    public ScrollingAWTTerminal(
            TerminalEmulatorDeviceConfiguration deviceConfiguration,
            SwingTerminalFontConfiguration fontConfiguration,
            TerminalEmulatorColorConfiguration colorConfiguration) {

        this.scrollBar = new Scrollbar(Scrollbar.VERTICAL);
        this.awtTerminal = new AWTTerminal(
                deviceConfiguration,
                fontConfiguration,
                colorConfiguration,
                new ScrollController());

        setLayout(new BorderLayout());
        add(awtTerminal, BorderLayout.CENTER);
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
            awtTerminal.repaint();
        }
    }

    ///////////
    // Delegate all Terminal interface implementations to SwingTerminal
    ///////////
    @Override
    public KeyStroke pollInput() {
        return awtTerminal.pollInput();
    }

    @Override
    public KeyStroke readInput() throws IOException {
        return awtTerminal.readInput();
    }

    @Override
    public void enterPrivateMode() {
        awtTerminal.enterPrivateMode();
    }

    @Override
    public void exitPrivateMode() {
        awtTerminal.exitPrivateMode();
    }

    @Override
    public void clearScreen() {
        awtTerminal.clearScreen();
    }

    @Override
    public void setCursorPosition(int x, int y) {
        awtTerminal.setCursorPosition(x, y);
    }

    @Override
    public void setCursorVisible(boolean visible) {
        awtTerminal.setCursorVisible(visible);
    }

    @Override
    public void putCharacter(char c) {
        awtTerminal.putCharacter(c);
    }

    @Override
    public TextGraphics newTextGraphics() throws IOException {
        return awtTerminal.newTextGraphics();
    }

    @Override
    public void enableSGR(SGR sgr) {
        awtTerminal.enableSGR(sgr);
    }

    @Override
    public void disableSGR(SGR sgr) {
        awtTerminal.disableSGR(sgr);
    }

    @Override
    public void resetColorAndSGR() {
        awtTerminal.resetColorAndSGR();
    }

    @Override
    public void setForegroundColor(TextColor color) {
        awtTerminal.setForegroundColor(color);
    }

    @Override
    public void setBackgroundColor(TextColor color) {
        awtTerminal.setBackgroundColor(color);
    }

    @Override
    public TerminalSize getTerminalSize() {
        return awtTerminal.getTerminalSize();
    }

    @Override
    public byte[] enquireTerminal(int timeout, TimeUnit timeoutUnit) {
        return awtTerminal.enquireTerminal(timeout, timeoutUnit);
    }

    @Override
    public void flush() {
        awtTerminal.flush();
    }

    @Override
    public void addResizeListener(ResizeListener listener) {
        awtTerminal.addResizeListener(listener);
    }

    @Override
    public void removeResizeListener(ResizeListener listener) {
        awtTerminal.removeResizeListener(listener);
    }
}
