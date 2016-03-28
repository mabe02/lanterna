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
 * Copyright (C) 2010-2016 Martin
 */
package com.googlecode.lanterna.terminal.ansi;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

/**
 * This interface is used to abstract the functionality required to modify certain elements of the terminal which are
 * not exposed through ANSI escape codes. Lanterna provides one implementation of this that is using the <b>stty</b>
 * program (available on Unix-systems only) to alter these settings but for other platforms (windows, most notably)
 * you'll need to implement this yourself and handle these call by using JNI or some other way to make calls into native
 * libraries.
 */
public interface TerminalDeviceControlStrategy {

    /**
     * Stores the current terminal device settings (the ones that are modified through this interface) so that they can
     * be restored later using {@link #restoreTerminalSettings()}
     * @throws IOException If there was an I/O error
     */
    void saveTerminalSettings() throws IOException;

    /**
     * Restores the terminal settings from last time {@link #saveTerminalSettings()} was called
     * @throws IOException If there was an I/O error
     */
    void restoreTerminalSettings() throws IOException;

    /**
     * Enables or disable key echo mode, which means when the user press a key, the terminal will immediately print that
     * key to the terminal. Normally for Lanterna, this should be turned off so the software can take the key as an
     * input event, put it on the input queue and then depending on the code decide what to do with it.
     * @param enabled {@code true} if key echo should be enabled, {@code false} otherwise
     * @throws IOException If there was an I/O error
     */
    void keyEchoEnabled(boolean enabled) throws IOException;

    /**
     * In canonical mode, data are accumulated in a line editing buffer, and do not become "available for reading" until
     * line editing has been terminated by the user sending a line delimiter character. This is usually the default mode
     * for a terminal. Lanterna wants to read each character as they are typed, without waiting for the final newline,
     * so it will attempt to turn canonical mode off on initialization.
     * @param enabled {@code true} if canonical input mode should be enabled, {@code false} otherwise
     * @throws IOException If there was an I/O error
     */
    void canonicalMode(boolean enabled) throws IOException;

    /**
     * This method causes certain keystrokes (at the moment only ctrl+c) to be passed in to the program as a regular
     * {@link com.googlecode.lanterna.input.KeyStroke} instead of as a signal to the JVM process. For example,
     * <i>ctrl+c</i> will normally send an interrupt that causes the JVM to shut down, but this method will make it pass
     * in <i>ctrl+c</i> as a regular {@link com.googlecode.lanterna.input.KeyStroke} instead. You can of course still
     * make <i>ctrl+c</i> kill the application through your own input handling if you like.
     * <p>
     * Please note that this method is called automatically by lanterna to disable signals unless you define a system
     * property "com.googlecode.lanterna.terminal.UnixTerminal.catchSpecialCharacters" and set it to the string "false".
     * @throws IOException If there was an I/O error when attempting to disable special characters
     * @see com.googlecode.lanterna.terminal.ansi.UnixLikeTerminal.CtrlCBehaviour
     */
    void keyStrokeSignalsEnabled(boolean enabled) throws IOException;

    /**
     * Returns the size of the terminal, if this {@link TerminalDeviceControlStrategy} is able to read it, otherwise
     * {@code null}. This is similar to how {@link UnixTerminalSizeQuerier} can be injected to use a different method
     * for figuring out how large the terminal window is, instead of falling back to the {@link ANSITerminal}
     * implementation which uses a bit of a hack with the cursor position.
     * @return The current size of the terminal, or {@code null} if unable to retrieve
     * @throws IOException If there was an I/O error
     */
    TerminalSize getTerminalSize() throws IOException;

    /**
     * If supported, registers a listener of some sort that will watch for the terminal window changing size. It does
     * not have to figure out the new size (lanterna will automatically call {@link Terminal#getTerminalSize()} to
     * find out), just that the size has changed. The supplied {@code Runnable} should be invoked when a change in size
     * was detected. Usually, the resize listener will be running asynchronously on a separate thread, but be careful
     * as there is no close method for the control strategy so you'll have to detect for yourself when the thread should
     * shut down is you manage it yourself.
     * @throws IOException If there was an I/O error
     */
    void registerTerminalResizeListener(Runnable onResize) throws IOException;
}
