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
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.IOSafeTerminal;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.terminal.TerminalResizeListener;

import java.awt.*;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * This class is similar to what SwingTerminal used to be before Lanterna 3.0; a Frame that contains a terminal
 * emulator. In Lanterna 3, this class is just an AWT Frame containing a {@link AWTTerminal} component, but it also
 * implements the {@link com.googlecode.lanterna.terminal.Terminal} interface and delegates all calls to the internal
 * {@link AWTTerminal}. You can tweak the class a bit to have special behaviours when exiting private mode or when the
 * user presses ESC key.
 *
 * <p>Please note that this is the AWT version and there is a Swing counterpart: {@link SwingTerminalFrame}
 * @see AWTTerminal
 * @see SwingTerminalFrame
 * @author martin
 */
@SuppressWarnings("serial")
public class AWTTerminalFrame extends Frame implements IOSafeTerminal {
    private final AWTTerminal awtTerminal;
    private final EnumSet<TerminalEmulatorAutoCloseTrigger> autoCloseTriggers;

    private boolean disposed;

    /**
     * Creates a new AWTTerminalFrame with an optional list of auto-close triggers
     * @param autoCloseTriggers What to trigger automatic disposal of the Frame
     */
    @SuppressWarnings({"SameParameterValue", "WeakerAccess"})
    public AWTTerminalFrame(TerminalEmulatorAutoCloseTrigger... autoCloseTriggers) {
        this("AwtTerminalFrame", autoCloseTriggers);
    }

    /**
     * Creates a new AWTTerminalFrame with a given window title and an optional list of auto-close triggers
     * @param title Title to use for the window
     * @param autoCloseTriggers What to trigger automatic disposal of the Frame
     */
    @SuppressWarnings("WeakerAccess")
    public AWTTerminalFrame(String title, TerminalEmulatorAutoCloseTrigger... autoCloseTriggers) throws HeadlessException {
        this(title, new AWTTerminal(), autoCloseTriggers);
    }

    /**
     * Creates a new AWTTerminalFrame using a specified title and a series of AWT terminal configuration objects
     * @param title What title to use for the window
     * @param deviceConfiguration Device configuration for the embedded AWTTerminal
     * @param fontConfiguration Font configuration for the embedded AWTTerminal
     * @param colorConfiguration Color configuration for the embedded AWTTerminal
     * @param autoCloseTriggers What to trigger automatic disposal of the Frame
     */
    public AWTTerminalFrame(String title,
                            TerminalEmulatorDeviceConfiguration deviceConfiguration,
                            AWTTerminalFontConfiguration fontConfiguration,
                            TerminalEmulatorColorConfiguration colorConfiguration,
                            TerminalEmulatorAutoCloseTrigger... autoCloseTriggers) {
        this(title, null, deviceConfiguration, fontConfiguration, colorConfiguration, autoCloseTriggers);
    }

    /**
     * Creates a new AWTTerminalFrame using a specified title and a series of AWT terminal configuration objects
     * @param title What title to use for the window
     * @param terminalSize Initial size of the terminal, in rows and columns. If null, it will default to 80x25.
     * @param deviceConfiguration Device configuration for the embedded AWTTerminal
     * @param fontConfiguration Font configuration for the embedded AWTTerminal
     * @param colorConfiguration Color configuration for the embedded AWTTerminal
     * @param autoCloseTriggers What to trigger automatic disposal of the Frame
     */
    public AWTTerminalFrame(String title,
                            TerminalSize terminalSize,
                            TerminalEmulatorDeviceConfiguration deviceConfiguration,
                            AWTTerminalFontConfiguration fontConfiguration,
                            TerminalEmulatorColorConfiguration colorConfiguration,
                            TerminalEmulatorAutoCloseTrigger... autoCloseTriggers) {
        this(title,
                new AWTTerminal(terminalSize, deviceConfiguration, fontConfiguration, colorConfiguration),
                autoCloseTriggers);
    }
    
    private AWTTerminalFrame(String title, AWTTerminal awtTerminal, TerminalEmulatorAutoCloseTrigger... autoCloseTrigger) {
        super(title != null ? title : "AWTTerminalFrame");
        this.awtTerminal = awtTerminal;
        this.autoCloseTriggers = EnumSet.copyOf(Arrays.asList(autoCloseTrigger));
        this.disposed = false;

        setLayout(new BorderLayout());
        add(awtTerminal, BorderLayout.CENTER);
        setBackground(Color.BLACK); //This will reduce white flicker when resizing the window
        pack();

        //Put input focus on the terminal component by default
        awtTerminal.requestFocusInWindow();
    }

    /**
     * Returns the auto-close triggers used by the AWTTerminalFrame
     * @return Current auto-close trigger
     */
    public Set<TerminalEmulatorAutoCloseTrigger> getAutoCloseTrigger() {
        return EnumSet.copyOf(autoCloseTriggers);
    }

    public void addAutoCloseTrigger(TerminalEmulatorAutoCloseTrigger autoCloseTrigger) {
        autoCloseTriggers.add(autoCloseTrigger);
    }

    @Override
    public void dispose() {
        super.dispose();
        disposed = true;
    }

    @Override
    public void close() {
        dispose();
    }

    /**
     * Takes a KeyStroke and puts it on the input queue of the terminal emulator. This way you can insert synthetic
     * input events to be processed as if they came from the user typing on the keyboard.
     * @param keyStroke Key stroke input event to put on the queue
     */
    public void addInput(KeyStroke keyStroke) {
        awtTerminal.addInput(keyStroke);
    }

    ///////////
    // Delegate all Terminal interface implementations to AWTTerminal
    ///////////
    @Override
    public KeyStroke pollInput() {
        if(disposed) {
            return new KeyStroke(KeyType.EOF);
        }
        KeyStroke keyStroke = awtTerminal.pollInput();
        if(autoCloseTriggers.contains(TerminalEmulatorAutoCloseTrigger.CloseOnEscape) &&
                keyStroke != null && 
                keyStroke.getKeyType() == KeyType.Escape) {
            dispose();
        }
        return keyStroke;
    }

    @Override
    public KeyStroke readInput() {
        return awtTerminal.readInput();
    }

    @Override
    public void enterPrivateMode() {
        awtTerminal.enterPrivateMode();
    }

    @Override
    public void exitPrivateMode() {
        awtTerminal.exitPrivateMode();
        if(autoCloseTriggers.contains(TerminalEmulatorAutoCloseTrigger.CloseOnExitPrivateMode)) {
            dispose();
        }
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
    public void setCursorPosition(TerminalPosition position) {
        awtTerminal.setCursorPosition(position);
    }

    @Override
    public TerminalPosition getCursorPosition() {
        return awtTerminal.getCursorPosition();
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
    public TextGraphics newTextGraphics() {
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
    public void bell() {
        awtTerminal.bell();
    }

    @Override
    public void flush() {
        awtTerminal.flush();
    }

    @Override
    public void addResizeListener(TerminalResizeListener listener) {
        awtTerminal.addResizeListener(listener);
    }

    @Override
    public void removeResizeListener(TerminalResizeListener listener) {
        awtTerminal.removeResizeListener(listener);
    }
}
