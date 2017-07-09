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
 * Copyright (C) 2010-2017 Martin Berglund
 */
package com.googlecode.lanterna.terminal.swing;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.IOSafeTerminal;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.terminal.TerminalResizeListener;

import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.concurrent.TimeUnit;
import javax.swing.*;

/**
 * This is a Swing JComponent that carries a {@link SwingTerminal} with a scrollbar, effectively implementing a
 * pseudo-terminal with scrollback history. You can choose the same parameters are for {@link SwingTerminal}, they are
 * forwarded, this class mostly deals with linking the {@link SwingTerminal} with the scrollbar and having them update
 * each other.
 * @author Martin
 */
@SuppressWarnings("serial")
public class ScrollingSwingTerminal extends JComponent implements IOSafeTerminal {

    private final SwingTerminal swingTerminal;
    private final JScrollBar scrollBar;

    // Used to prevent unnecessary repaints (the component is re-adjusting the scrollbar as part of the repaint
    // operation, we don't need the scrollbar listener to trigger another repaint of the terminal when that happens
    private volatile boolean scrollModelUpdateBySystem;

    /**
     * Creates a new {@code ScrollingSwingTerminal} with all default options
     */
    public ScrollingSwingTerminal() {
        this(TerminalEmulatorDeviceConfiguration.getDefault(),
                SwingTerminalFontConfiguration.getDefault(),
                TerminalEmulatorColorConfiguration.getDefault());
    }

    /**
     * Creates a new {@code ScrollingSwingTerminal} with customizable settings.
     * @param deviceConfiguration How to configure the terminal virtual device
     * @param fontConfiguration What kind of fonts to use
     * @param colorConfiguration Which color schema to use for ANSI colors
     */
    @SuppressWarnings({"SameParameterValue", "WeakerAccess"})
    public ScrollingSwingTerminal(
            TerminalEmulatorDeviceConfiguration deviceConfiguration,
            SwingTerminalFontConfiguration fontConfiguration,
            TerminalEmulatorColorConfiguration colorConfiguration) {

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
        this.scrollModelUpdateBySystem = false;
    }

    private class ScrollController implements TerminalScrollController {
        private int scrollValue;

        @Override
        public void updateModel(final int totalSize, final int screenHeight) {
            if(!SwingUtilities.isEventDispatchThread()) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        updateModel(totalSize, screenHeight);
                    }
                });
                return;
            }
            try {
                scrollModelUpdateBySystem = true;
                int value = scrollBar.getValue();
                int maximum = scrollBar.getMaximum();
                int visibleAmount = scrollBar.getVisibleAmount();

                if(maximum != totalSize) {
                    int lastMaximum = maximum;
                    maximum = totalSize > screenHeight ? totalSize : screenHeight;
                    if(lastMaximum < maximum &&
                            lastMaximum - visibleAmount - value == 0) {
                        value = scrollBar.getValue() + (maximum - lastMaximum);
                    }
                }
                if(value + screenHeight > maximum) {
                    value = maximum - screenHeight;
                }
                if(visibleAmount != screenHeight) {
                    if(visibleAmount > screenHeight) {
                        value += visibleAmount - screenHeight;
                    }
                    visibleAmount = screenHeight;
                }
                if(value > maximum - visibleAmount) {
                    value = maximum - visibleAmount;
                }
                if(value < 0) {
                    value = 0;
                }

                this.scrollValue = value;

                if(scrollBar.getMaximum() != maximum) {
                    scrollBar.setMaximum(maximum);
                }
                if(scrollBar.getVisibleAmount() != visibleAmount) {
                    scrollBar.setVisibleAmount(visibleAmount);
                }
                if(scrollBar.getValue() != value) {
                    scrollBar.setValue(value);
                }
            }
            finally {
                scrollModelUpdateBySystem = false;
            }
        }

        @Override
        public int getScrollingOffset() {
            return scrollValue;
        }
    }

    private class ScrollbarListener implements AdjustmentListener {
        @Override
        public synchronized void adjustmentValueChanged(AdjustmentEvent e) {
            if(!scrollModelUpdateBySystem) {
                // Only repaint if this was the user adjusting the scrollbar
                swingTerminal.repaint();
            }
        }
    }

    /**
     * Takes a KeyStroke and puts it on the input queue of the terminal emulator. This way you can insert synthetic
     * input events to be processed as if they came from the user typing on the keyboard.
     * @param keyStroke Key stroke input event to put on the queue
     */
    public void addInput(KeyStroke keyStroke) {
        swingTerminal.addInput(keyStroke);
    }

    ///////////
    // Delegate all Terminal interface implementations to SwingTerminal
    ///////////
    @Override
    public KeyStroke pollInput() {
        return swingTerminal.pollInput();
    }

    @Override
    public KeyStroke readInput() {
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
    public void setCursorPosition(TerminalPosition position) {
        swingTerminal.setCursorPosition(position);
    }

    @Override
    public TerminalPosition getCursorPosition() {
        return swingTerminal.getCursorPosition();
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
    public TextGraphics newTextGraphics() {
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
    public void bell() {
        swingTerminal.bell();
    }

    @Override
    public void flush() {
        swingTerminal.flush();
    }

    @Override
    public void close() {
        swingTerminal.close();
    }

    @Override
    public void addResizeListener(TerminalResizeListener listener) {
        swingTerminal.addResizeListener(listener);
    }

    @Override
    public void removeResizeListener(TerminalResizeListener listener) {
        swingTerminal.removeResizeListener(listener);
    }
}
