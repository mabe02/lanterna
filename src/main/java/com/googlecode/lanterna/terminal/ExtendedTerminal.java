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
package com.googlecode.lanterna.terminal;

import java.io.IOException;

import com.googlecode.lanterna.graphics.Scrollable;

/**
 * This class extends the normal Terminal interface and adds a few more methods that are considered rare and shouldn't
 * be encouraged to be used. Some of these may move into Terminal if it turns out that they are indeed well-supported.
 * Most of these extensions are picked up from here: http://invisible-island.net/xterm/ctlseqs/ctlseqs.html
 *
 * This class is <b>not</b> considered stable and may change within releases. Do not depend on methods in this interface
 * unless you are ok with occasionally having to fix broken code after minor library upgrades.
 * @author Martin
 */
public interface ExtendedTerminal extends Terminal, Scrollable {

    /**
     * Attempts to resize the terminal through dtterm extensions "CSI 8 ; rows ; columns ; t". This isn't widely
     * supported, which is why the method is not exposed through the common Terminal interface.
     * @param columns New size (columns)
     * @param rows New size (rows)
     * @throws java.io.IOException If the was an underlying I/O error
     */
    void setTerminalSize(int columns, int rows) throws IOException;

    /**
     * This methods sets the title of the terminal, which is normally only visible if you are running the application
     * in a terminal emulator in a graphical environment.
     * @param title Title to set on the terminal
     * @throws java.io.IOException If the was an underlying I/O error
     */
    void setTitle(String title) throws IOException;

    /**
     * Saves the current window title on a stack managed internally by the terminal.
     * @throws java.io.IOException If the was an underlying I/O error
     */
    void pushTitle() throws IOException;

    /**
     * Replaces the terminal title with the top element from the title stack managed by the terminal (the element is
     * removed from the stack as expected)
     * @throws java.io.IOException If the was an underlying I/O error
     */
    void popTitle() throws IOException;

    /**
     * Iconifies the terminal, this likely means minimizing the window with most window managers
     * @throws IOException If the was an underlying I/O error
     */
    void iconify() throws IOException;

    /**
     * De-iconifies the terminal, which likely means restoring it from minimized state with most window managers
     * @throws IOException If the was an underlying I/O error
     */
    void deiconify() throws IOException;

    /**
     * Maximizes the terminal, so that it takes up all available space
     * @throws IOException If the was an underlying I/O error
     */
    void maximize() throws IOException;

    /**
     * Restores the terminal back to its previous size, after having been maximized
     * @throws IOException If the was an underlying I/O error
     */
    void unmaximize() throws IOException;

    /**
     * Enabled or disables capturing of mouse event. This is not recommended to use as most users are not familiar with
     * the fact that terminal emulators allow capturing mouse input. You can decide which events you want to capture but
     * be careful since different terminal emulators will support these modes differently. Mouse capture mode will be
     * automatically disabled when the application exits through a shutdown hook.
     *
     * @param mouseCaptureMode Which mouse events to capture, pass in {@code null} to disable mouse input capturing
     * @throws IOException If the was an underlying I/O error
     */
    void setMouseCaptureMode(MouseCaptureMode mouseCaptureMode) throws IOException;
}
