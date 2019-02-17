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
 * Copyright (C) 2010-2019 Martin Berglund
 */
package com.googlecode.lanterna.terminal.swing;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.IOSafeTerminal;
import com.googlecode.lanterna.terminal.TerminalResizeListener;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.TimeUnit;

/**
 * This class provides an Swing implementation of the {@link com.googlecode.lanterna.terminal.Terminal} interface that
 * is an embeddable component you can put into a Swing container. The class has static helper methods for opening a new
 * frame with a {@link SwingTerminal} as its content, similar to how the SwingTerminal used to work in earlier versions
 * of lanterna. This version supports private mode and non-private mode with a scrollback history. You can customize
 * many of the properties by supplying device configuration, font configuration and color configuration when you
 * construct the object.
 * @author martin
 */
@SuppressWarnings("serial")
public class SwingTerminal extends JComponent implements IOSafeTerminal {

    private final SwingTerminalImplementation terminalImplementation;

    /**
     * Creates a new SwingTerminal with all the defaults set and no scroll controller connected.
     */
    public SwingTerminal() {
        this(new TerminalScrollController.Null());
    }


    /**
     * Creates a new SwingTerminal with a particular scrolling controller that will be notified when the terminals
     * history size grows and will be called when this class needs to figure out the current scrolling position.
     * @param scrollController Controller for scrolling the terminal history
     */
    @SuppressWarnings("WeakerAccess")
    public SwingTerminal(TerminalScrollController scrollController) {
        this(TerminalEmulatorDeviceConfiguration.getDefault(),
                SwingTerminalFontConfiguration.getDefault(),
                TerminalEmulatorColorConfiguration.getDefault(),
                scrollController);
    }

    /**
     * Creates a new SwingTerminal component using custom settings and no scroll controller.
     * @param deviceConfiguration Device configuration to use for this SwingTerminal
     * @param fontConfiguration Font configuration to use for this SwingTerminal
     * @param colorConfiguration Color configuration to use for this SwingTerminal
     */
    public SwingTerminal(
            TerminalEmulatorDeviceConfiguration deviceConfiguration,
            SwingTerminalFontConfiguration fontConfiguration,
            TerminalEmulatorColorConfiguration colorConfiguration) {

        this(null, deviceConfiguration, fontConfiguration, colorConfiguration);
    }

    /**
     * Creates a new SwingTerminal component using custom settings and no scroll controller.
     * @param initialTerminalSize Initial size of the terminal, which will be used when calculating the preferred size
     *                            of the component. If null, it will default to 80x25. If the AWT layout manager forces
     *                            the component to a different size, the value of this parameter won't have any meaning
     * @param deviceConfiguration Device configuration to use for this SwingTerminal
     * @param fontConfiguration Font configuration to use for this SwingTerminal
     * @param colorConfiguration Color configuration to use for this SwingTerminal
     */
    public SwingTerminal(
            TerminalSize initialTerminalSize,
            TerminalEmulatorDeviceConfiguration deviceConfiguration,
            SwingTerminalFontConfiguration fontConfiguration,
            TerminalEmulatorColorConfiguration colorConfiguration) {

        this(initialTerminalSize,
                deviceConfiguration,
                fontConfiguration,
                colorConfiguration,
                new TerminalScrollController.Null());
    }

    /**
     * Creates a new SwingTerminal component using custom settings and a custom scroll controller. The scrolling
     * controller will be notified when the terminal's history size grows and will be called when this class needs to
     * figure out the current scrolling position.
     * @param deviceConfiguration Device configuration to use for this SwingTerminal
     * @param fontConfiguration Font configuration to use for this SwingTerminal
     * @param colorConfiguration Color configuration to use for this SwingTerminal
     * @param scrollController Controller to use for scrolling, the object passed in will be notified whenever the
     *                         scrollable area has changed
     */
    public SwingTerminal(
            TerminalEmulatorDeviceConfiguration deviceConfiguration,
            SwingTerminalFontConfiguration fontConfiguration,
            TerminalEmulatorColorConfiguration colorConfiguration,
            TerminalScrollController scrollController) {

        this(null, deviceConfiguration, fontConfiguration, colorConfiguration, scrollController);
    }



    /**
     * Creates a new SwingTerminal component using custom settings and a custom scroll controller. The scrolling
     * controller will be notified when the terminal's history size grows and will be called when this class needs to
     * figure out the current scrolling position.
     * @param initialTerminalSize Initial size of the terminal, which will be used when calculating the preferred size
     *                            of the component. If null, it will default to 80x25. If the AWT layout manager forces
     *                            the component to a different size, the value of this parameter won't have any meaning
     * @param deviceConfiguration Device configuration to use for this SwingTerminal
     * @param fontConfiguration Font configuration to use for this SwingTerminal
     * @param colorConfiguration Color configuration to use for this SwingTerminal
     * @param scrollController Controller to use for scrolling, the object passed in will be notified whenever the
     *                         scrollable area has changed
     */
    public SwingTerminal(
            TerminalSize initialTerminalSize,
            TerminalEmulatorDeviceConfiguration deviceConfiguration,
            SwingTerminalFontConfiguration fontConfiguration,
            TerminalEmulatorColorConfiguration colorConfiguration,
            TerminalScrollController scrollController) {

        //Enforce valid values on the input parameters
        if(deviceConfiguration == null) {
            deviceConfiguration = TerminalEmulatorDeviceConfiguration.getDefault();
        }
        if(fontConfiguration == null) {
            fontConfiguration = SwingTerminalFontConfiguration.getDefault();
        }
        if(colorConfiguration == null) {
            colorConfiguration = TerminalEmulatorColorConfiguration.getDefault();
        }

        terminalImplementation = new SwingTerminalImplementation(
                this,
                fontConfiguration,
                initialTerminalSize,
                deviceConfiguration,
                colorConfiguration,
                scrollController);
    }

    /**
     * Overridden method from Swing's {@code JComponent} class that returns the preferred size of the terminal (in
     * pixels)
     * @return The terminal's preferred size in pixels
     */
    @Override
    public synchronized Dimension getPreferredSize() {
        return terminalImplementation.getPreferredSize();
    }

    /**
     * Overridden method from Swing's {@code JComponent} class that is called by OS window system when the component
     * needs to be redrawn
     * @param componentGraphics {@code Graphics} object to use when drawing the component
     */
    @Override
    protected synchronized void paintComponent(Graphics componentGraphics) {
        terminalImplementation.paintComponent(componentGraphics);
    }

    /**
     * Takes a KeyStroke and puts it on the input queue of the terminal emulator. This way you can insert synthetic
     * input events to be processed as if they came from the user typing on the keyboard.
     * @param keyStroke Key stroke input event to put on the queue
     */
    public void addInput(KeyStroke keyStroke) {
        terminalImplementation.addInput(keyStroke);
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Terminal methods below here, just forward to the implementation

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
    public void setCursorPosition(int x, int y) {
        terminalImplementation.setCursorPosition(x, y);
    }

    @Override
    public void setCursorPosition(TerminalPosition position) {
        terminalImplementation.setCursorPosition(position);
    }

    @Override
    public TerminalPosition getCursorPosition() {
        return terminalImplementation.getCursorPosition();
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
    public void resetColorAndSGR() {
        terminalImplementation.resetColorAndSGR();
    }

    @Override
    public void setForegroundColor(TextColor color) {
        terminalImplementation.setForegroundColor(color);
    }

    @Override
    public void setBackgroundColor(TextColor color) {
        terminalImplementation.setBackgroundColor(color);
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
    public void bell() {
        terminalImplementation.bell();
    }

    @Override
    public void flush() {
        terminalImplementation.flush();
    }

    @Override
    public void close() {
        terminalImplementation.close();
    }

    @Override
    public KeyStroke pollInput() {
        return terminalImplementation.pollInput();
    }

    @Override
    public KeyStroke readInput() {
        return terminalImplementation.readInput();
    }

    @Override
    public TextGraphics newTextGraphics() {
        return terminalImplementation.newTextGraphics();
    }

    @Override
    public void addResizeListener(TerminalResizeListener listener) {
        terminalImplementation.addResizeListener(listener);
    }

    @Override
    public void removeResizeListener(TerminalResizeListener listener) {
        terminalImplementation.removeResizeListener(listener);
    }
}
