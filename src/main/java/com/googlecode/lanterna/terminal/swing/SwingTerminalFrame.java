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
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.terminal.TextColor;
import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;

/**
 *
 * @author martin
 */
public class SwingTerminalFrame extends JFrame implements IOSafeTerminal {

    private final SwingTerminal swingTerminal;

    public SwingTerminalFrame() throws HeadlessException {
        this("SwingTerminalFrame");
    }

    public SwingTerminalFrame(String title) throws HeadlessException {
        this(title, new SwingTerminal());
    }

    public SwingTerminalFrame(String title,
            SwingTerminalDeviceConfiguration deviceConfiguration,
            SwingTerminalFontConfiguration fontConfiguration,
            SwingTerminalColorConfiguration colorConfiguration) {
        this(title, new SwingTerminal(deviceConfiguration, fontConfiguration, colorConfiguration));
    }

    private SwingTerminalFrame(String title, SwingTerminal swingTerminal) {
        super(title);
        this.swingTerminal = swingTerminal;

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(swingTerminal, BorderLayout.CENTER);
        pack();
    }

    ///////////
    // Delegate all Terminal interface implementations to SwingTerminal
    ///////////
    @Override
    public KeyStroke readInput() {
        return swingTerminal.readInput();
    }

    @Override
    public void addKeyDecodingProfile(KeyDecodingProfile profile) {
        swingTerminal.addKeyDecodingProfile(profile);
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
    public void moveCursor(int x, int y) {
        swingTerminal.moveCursor(x, y);
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
    public void enableSGR(SGR sgr) {
        swingTerminal.enableSGR(sgr);
    }

    @Override
    public void disableSGR(SGR sgr) {
        swingTerminal.disableSGR(sgr);
    }

    @Override
    public void resetAllSGR() {
        swingTerminal.resetAllSGR();
    }

    @Override
    public void applyForegroundColor(TextColor color) {
        swingTerminal.applyForegroundColor(color);
    }

    @Override
    public void applyBackgroundColor(TextColor color) {
        swingTerminal.applyBackgroundColor(color);
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
