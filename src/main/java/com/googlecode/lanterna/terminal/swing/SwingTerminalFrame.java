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
import javax.swing.*;

/**
 * This class is similar to what SwingTerminal used to be before Lanterna 3.0; a JFrame that contains a terminal
 * emulator. In Lanterna 3, this class is just a JFrame containing a SwingTerminal component, but it also implements
 * the Terminal interface and delegates all calls to the internal SwingTerminal. You can tweak the class a bit to have
 * special behaviours when exiting private mode or when the user presses ESC key.
 * @author martin
 */
@SuppressWarnings("serial")
public class SwingTerminalFrame extends JFrame implements IOSafeTerminal {
    
    /**
     * This enum stored various ways the SwingTerminalFrame can automatically close (hide and dispose) itself when a
     * certain condition happens. By default, auto-close is not active.
     */
    public enum AutoCloseTrigger {
        /**
         * Auto-close disabled
         */
        DoNotAutoClose,
        /**
         * Close the frame when exiting from private mode
         */
        CloseOnExitPrivateMode,
        /**
         * Close if the user presses ESC key on the keyboard
         */
        CloseOnEscape,
        ;
    }

    private final SwingTerminal swingTerminal;
    private AutoCloseTrigger autoCloseTrigger;
    private boolean disposed;

    /**
     * Creates a new SwingTerminalFrame that doesn't automatically close.
     */
    public SwingTerminalFrame() throws HeadlessException {
        this(AutoCloseTrigger.DoNotAutoClose);
    }

    /**
     * Creates a new SwingTerminalFrame with a specified auto-close behaviour
     * @param autoCloseTrigger What to trigger automatic disposal of the JFrame
     */
    @SuppressWarnings({"SameParameterValue", "WeakerAccess"})
    public SwingTerminalFrame(AutoCloseTrigger autoCloseTrigger) {
        this("SwingTerminalFrame", autoCloseTrigger);
    }

    /**
     * Creates a new SwingTerminalFrame with a given title and no automatic closing.
     * @param title Title to use for the window
     */
    public SwingTerminalFrame(String title) throws HeadlessException {
        this(title, AutoCloseTrigger.DoNotAutoClose);
    }

    /**
     * Creates a new SwingTerminalFrame with a specified auto-close behaviour and specific title
     * @param title Title to use for the window
     * @param autoCloseTrigger What to trigger automatic disposal of the JFrame
     */
    @SuppressWarnings("WeakerAccess")
    public SwingTerminalFrame(String title, AutoCloseTrigger autoCloseTrigger) throws HeadlessException {
        this(title, new SwingTerminal(), autoCloseTrigger);
    }

    /**
     * Creates a new SwingTerminalFrame using a specified title and a series of swing terminal configuration objects
     * @param title What title to use for the window
     * @param deviceConfiguration Device configuration for the embedded SwingTerminal
     * @param fontConfiguration Font configuration for the embedded SwingTerminal
     * @param colorConfiguration Color configuration for the embedded SwingTerminal
     */
    public SwingTerminalFrame(String title,
            SwingTerminalDeviceConfiguration deviceConfiguration,
            SwingTerminalFontConfiguration fontConfiguration,
            SwingTerminalColorConfiguration colorConfiguration) {
        this(title, deviceConfiguration, fontConfiguration, colorConfiguration, AutoCloseTrigger.DoNotAutoClose);
    }

    /**
     * Creates a new SwingTerminalFrame using a specified title and a series of swing terminal configuration objects
     * @param title What title to use for the window
     * @param deviceConfiguration Device configuration for the embedded SwingTerminal
     * @param fontConfiguration Font configuration for the embedded SwingTerminal
     * @param colorConfiguration Color configuration for the embedded SwingTerminal
     * @param autoCloseTrigger What to trigger automatic disposal of the JFrame
     */
    public SwingTerminalFrame(String title,
            SwingTerminalDeviceConfiguration deviceConfiguration,
            SwingTerminalFontConfiguration fontConfiguration,
            SwingTerminalColorConfiguration colorConfiguration,
            AutoCloseTrigger autoCloseTrigger) {
        this(title, null, deviceConfiguration, fontConfiguration, colorConfiguration, autoCloseTrigger);
    }

    /**
     * Creates a new SwingTerminalFrame using a specified title and a series of swing terminal configuration objects
     * @param title What title to use for the window
     * @param terminalSize Initial size of the terminal, in rows and columns. If null, it will default to 80x25.
     * @param deviceConfiguration Device configuration for the embedded SwingTerminal
     * @param fontConfiguration Font configuration for the embedded SwingTerminal
     * @param colorConfiguration Color configuration for the embedded SwingTerminal
     * @param autoCloseTrigger What to trigger automatic disposal of the JFrame
     */
    public SwingTerminalFrame(String title,
                              TerminalSize terminalSize,
                              SwingTerminalDeviceConfiguration deviceConfiguration,
                              SwingTerminalFontConfiguration fontConfiguration,
                              SwingTerminalColorConfiguration colorConfiguration,
                              AutoCloseTrigger autoCloseTrigger) {
        this(title,
                new SwingTerminal(terminalSize, deviceConfiguration, fontConfiguration, colorConfiguration),
                autoCloseTrigger);
    }
    
    private SwingTerminalFrame(String title, SwingTerminal swingTerminal, AutoCloseTrigger autoCloseTrigger) {
        super(title);
        this.swingTerminal = swingTerminal;
        this.autoCloseTrigger = autoCloseTrigger;
        this.disposed = false;

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(swingTerminal, BorderLayout.CENTER);
        setBackground(Color.BLACK); //This will reduce white flicker when resizing the window
        pack();

        //Put input focus on the terminal component by default
        swingTerminal.requestFocusInWindow();
    }

    /**
     * Returns the auto-close trigger used by the SwingTerminalFrame
     * @return Current auto-close trigger
     */
    public AutoCloseTrigger getAutoCloseTrigger() {
        return autoCloseTrigger;
    }

    /**
     * Changes the current auto-close trigger used by this SwingTerminalFrame
     * @param autoCloseTrigger New auto-close trigger to use
     */
    public void setAutoCloseTrigger(AutoCloseTrigger autoCloseTrigger) {
        this.autoCloseTrigger = autoCloseTrigger;
    }

    @Override
    public void dispose() {
        super.dispose();
        disposed = true;
    }
    
    ///////////
    // Delegate all Terminal interface implementations to SwingTerminal
    ///////////
    @Override
    public KeyStroke pollInput() {
        if(disposed) {
            return new KeyStroke(KeyType.EOF);
        }
        KeyStroke keyStroke = swingTerminal.pollInput();
        if(autoCloseTrigger == AutoCloseTrigger.CloseOnEscape && 
                keyStroke != null && 
                keyStroke.getKeyType() == KeyType.Escape) {
            dispose();
        }
        return keyStroke;
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
        if(autoCloseTrigger == AutoCloseTrigger.CloseOnExitPrivateMode) {
            dispose();
        }
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
