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

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.IOSafeTerminal;
import com.googlecode.lanterna.terminal.ResizeListener;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;

/**
 *
 * @author martin
 */
public class SwingTerminalFrame extends JFrame implements IOSafeTerminal {
    
    /**
     * This enum stored various ways the SwingTerminalFrame can automatically close (hide and dispose) itself when a
     * certain condition happens. By default, auto-close is not active.
     */
    public enum AutoCloseTrigger {
        /**
         * Auto-close disabled
         */
        DontAutoClose,
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

    public SwingTerminalFrame() throws HeadlessException {
        this(AutoCloseTrigger.DontAutoClose);
    }
    
    @SuppressWarnings({"SameParameterValue", "WeakerAccess"})
    public SwingTerminalFrame(AutoCloseTrigger autoCloseTrigger) {
        this("SwingTerminalFrame", autoCloseTrigger);
    }

    public SwingTerminalFrame(String title) throws HeadlessException {
        this(title, AutoCloseTrigger.DontAutoClose);
    }
    
    @SuppressWarnings("WeakerAccess")
    public SwingTerminalFrame(String title, AutoCloseTrigger autoCloseTrigger) throws HeadlessException {
        this(title, new SwingTerminal(), autoCloseTrigger);
    }

    public SwingTerminalFrame(String title,
            SwingTerminalDeviceConfiguration deviceConfiguration,
            SwingTerminalFontConfiguration fontConfiguration,
            SwingTerminalColorConfiguration colorConfiguration) {
        this(title, deviceConfiguration, fontConfiguration, colorConfiguration, AutoCloseTrigger.DontAutoClose);
    }
    
    public SwingTerminalFrame(String title,
            SwingTerminalDeviceConfiguration deviceConfiguration,
            SwingTerminalFontConfiguration fontConfiguration,
            SwingTerminalColorConfiguration colorConfiguration,
            AutoCloseTrigger autoCloseTrigger) {
        this(title, new SwingTerminal(deviceConfiguration, fontConfiguration, colorConfiguration), autoCloseTrigger);
    }
    
    private SwingTerminalFrame(String title, SwingTerminal swingTerminal, AutoCloseTrigger autoCloseTrigger) {
        super(title);
        this.swingTerminal = swingTerminal;
        this.autoCloseTrigger = autoCloseTrigger;

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(swingTerminal, BorderLayout.CENTER);
        pack();
        
        //Put input focus on the terminal component by default
        swingTerminal.requestFocusInWindow();
    }

    public AutoCloseTrigger getAutoCloseTrigger() {
        return autoCloseTrigger;
    }

    public void setAutoCloseTrigger(AutoCloseTrigger autoCloseTrigger) {
        this.autoCloseTrigger = autoCloseTrigger;
    }

    ///////////
    // Delegate all Terminal interface implementations to SwingTerminal
    ///////////
    @Override
    public KeyStroke readInput() {
        KeyStroke keyStroke = swingTerminal.readInput();
        if(autoCloseTrigger == AutoCloseTrigger.CloseOnEscape && 
                keyStroke != null && 
                keyStroke.getKeyType() == KeyType.Escape) {
            dispose();
        }
        return keyStroke;
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
