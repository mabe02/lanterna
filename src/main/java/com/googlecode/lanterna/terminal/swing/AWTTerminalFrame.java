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
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.IOSafeTerminal;
import com.googlecode.lanterna.terminal.ResizeListener;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;

import java.awt.*;
import java.io.IOException;
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
    private TerminalEmulatorAutoCloseTrigger autoCloseTrigger;
    private boolean disposed;

    /**
     * Creates a new AWTTerminalFrame that doesn't automatically close.
     */
    public AWTTerminalFrame() throws HeadlessException {
        this(TerminalEmulatorAutoCloseTrigger.DoNotAutoClose);
    }

    /**
     * Creates a new AWTTerminalFrame with a specified auto-close behaviour
     * @param autoCloseTrigger What to trigger automatic disposal of the Frame
     */
    @SuppressWarnings({"SameParameterValue", "WeakerAccess"})
    public AWTTerminalFrame(TerminalEmulatorAutoCloseTrigger autoCloseTrigger) {
        this("AwtTerminalFrame", autoCloseTrigger);
    }

    /**
     * Creates a new AWTTerminalFrame with a given title and no automatic closing.
     * @param title Title to use for the window
     */
    public AWTTerminalFrame(String title) throws HeadlessException {
        this(title, TerminalEmulatorAutoCloseTrigger.DoNotAutoClose);
    }

    /**
     * Creates a new AWTTerminalFrame with a specified auto-close behaviour and specific title
     * @param title Title to use for the window
     * @param autoCloseTrigger What to trigger automatic disposal of the Frame
     */
    @SuppressWarnings("WeakerAccess")
    public AWTTerminalFrame(String title, TerminalEmulatorAutoCloseTrigger autoCloseTrigger) throws HeadlessException {
        this(title, new AWTTerminal(), autoCloseTrigger);
    }

    /**
     * Creates a new AWTTerminalFrame using a specified title and a series of AWT terminal configuration objects
     * @param title What title to use for the window
     * @param deviceConfiguration Device configuration for the embedded AWTTerminal
     * @param fontConfiguration Font configuration for the embedded AWTTerminal
     * @param colorConfiguration Color configuration for the embedded AWTTerminal
     */
    public AWTTerminalFrame(String title,
                            TerminalEmulatorDeviceConfiguration deviceConfiguration,
                            AWTTerminalFontConfiguration fontConfiguration,
                            TerminalEmulatorColorConfiguration colorConfiguration) {
        this(title, deviceConfiguration, fontConfiguration, colorConfiguration, TerminalEmulatorAutoCloseTrigger.DoNotAutoClose);
    }

    /**
     * Creates a new AWTTerminalFrame using a specified title and a series of AWT terminal configuration objects
     * @param title What title to use for the window
     * @param deviceConfiguration Device configuration for the embedded AWTTerminal
     * @param fontConfiguration Font configuration for the embedded AWTTerminal
     * @param colorConfiguration Color configuration for the embedded AWTTerminal
     * @param autoCloseTrigger What to trigger automatic disposal of the Frame
     */
    public AWTTerminalFrame(String title,
                            TerminalEmulatorDeviceConfiguration deviceConfiguration,
                            AWTTerminalFontConfiguration fontConfiguration,
                            TerminalEmulatorColorConfiguration colorConfiguration,
                            TerminalEmulatorAutoCloseTrigger autoCloseTrigger) {
        this(title, null, deviceConfiguration, fontConfiguration, colorConfiguration, autoCloseTrigger);
    }

    /**
     * Creates a new AWTTerminalFrame using a specified title and a series of AWT terminal configuration objects
     * @param title What title to use for the window
     * @param terminalSize Initial size of the terminal, in rows and columns. If null, it will default to 80x25.
     * @param deviceConfiguration Device configuration for the embedded AWTTerminal
     * @param fontConfiguration Font configuration for the embedded AWTTerminal
     * @param colorConfiguration Color configuration for the embedded AWTTerminal
     * @param autoCloseTrigger What to trigger automatic disposal of the Frame
     */
    public AWTTerminalFrame(String title,
                            TerminalSize terminalSize,
                            TerminalEmulatorDeviceConfiguration deviceConfiguration,
                            AWTTerminalFontConfiguration fontConfiguration,
                            TerminalEmulatorColorConfiguration colorConfiguration,
                            TerminalEmulatorAutoCloseTrigger autoCloseTrigger) {
        this(title,
                new AWTTerminal(terminalSize, deviceConfiguration, fontConfiguration, colorConfiguration),
                autoCloseTrigger);
    }
    
    private AWTTerminalFrame(String title, AWTTerminal awtTerminal, TerminalEmulatorAutoCloseTrigger autoCloseTrigger) {
        super(title != null ? title : "AWTTerminalFrame");
        this.awtTerminal = awtTerminal;
        this.autoCloseTrigger = autoCloseTrigger;
        this.disposed = false;

        setLayout(new BorderLayout());
        add(awtTerminal, BorderLayout.CENTER);
        setBackground(Color.BLACK); //This will reduce white flicker when resizing the window
        pack();

        //Put input focus on the terminal component by default
        awtTerminal.requestFocusInWindow();
    }

    /**
     * Returns the auto-close trigger used by the AWTTerminalFrame
     * @return Current auto-close trigger
     */
    public TerminalEmulatorAutoCloseTrigger getAutoCloseTrigger() {
        return autoCloseTrigger;
    }

    /**
     * Changes the current auto-close trigger used by this AWTTerminalFrame
     * @param autoCloseTrigger New auto-close trigger to use
     */
    public void setAutoCloseTrigger(TerminalEmulatorAutoCloseTrigger autoCloseTrigger) {
        this.autoCloseTrigger = autoCloseTrigger;
    }

    @Override
    public void dispose() {
        super.dispose();
        disposed = true;
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
        if(autoCloseTrigger == TerminalEmulatorAutoCloseTrigger.CloseOnEscape &&
                keyStroke != null && 
                keyStroke.getKeyType() == KeyType.Escape) {
            dispose();
        }
        return keyStroke;
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
        if(autoCloseTrigger == TerminalEmulatorAutoCloseTrigger.CloseOnExitPrivateMode) {
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
