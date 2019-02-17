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
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.screen.Screen;

import java.util.Collection;

/**
 * Extension of the TextGUI interface, this is intended as the base interface for any TextGUI that intends to make use
 * of the Window class.
 * @author Martin
 */
public interface WindowBasedTextGUI extends TextGUI {
    /**
     * Returns the window manager that is currently controlling this TextGUI. The window manager is in charge of placing
     * the windows on the surface and also deciding how they behave and move around.
     * @return Window manager that is currently controlling the windows in the terminal
     */
    WindowManager getWindowManager();

    /**
     * Adds a window to the TextGUI system, depending on the window manager this window may or may not be immediately
     * visible. By adding a window to the GUI, it will be associated with this GUI and can receive focus and events from
     * it. This method call will return immediately, if you want the call to block until the window is closed, please
     * use {@code addWindowAndWait(..)}.
     *
     * Windows are internally stored as a stack and newer windows are added at the top of the stack. The GUI system will
     * render windows in a predictable order from bottom to top. You can modify the stack by using
     * {@code moveToTop(..)} to move a Window from its current position in the stack to the top.
     *
     * @param window Window to add to the GUI
     * @return The WindowBasedTextGUI Itself
     */
    WindowBasedTextGUI addWindow(Window window);

    /**
     * Adds a window to the TextGUI system, depending on the window manager this window may or may not be immediately
     * visible. By adding a window to the GUI, it will be associated with this GUI and can receive focus and events from
     * it. This method block until the added window is removed or closed, if you want the call to return immediately,
     * please use {@code addWindow(..)}. This method call is useful for modal dialogs that requires a certain user input
     * before the application can continue.
     *
     * Windows are internally stored as a stack and newer windows are added at the top of the stack. The GUI system will
     * render windows in a predictable order from bottom to top. You can modify the stack by using
     * {@code moveToTop(..)} to move a Window from its current position in the stack to the top.
     *
     * @param window Window to add to the GUI
     * @return The WindowBasedTextGUI Itself
     */
    WindowBasedTextGUI addWindowAndWait(Window window);

    /**
     * Removes a window from the TextGUI. This is effectively the same as closing the window. The window will be
     * unassociated from this TextGUI and will no longer receive any events for it. Any threads waiting on the window
     * to close will be resumed.
     *
     * @param window Window to close
     * @return The WindowBasedTextGUI itself
     */
    WindowBasedTextGUI removeWindow(Window window);

    /**
     * Returns a list of all windows currently in the TextGUI. The list is unmodifiable and just a snapshot of what the
     * state was when the method was invoked. If windows are added/removed after the method call, the list will not
     * reflect this.
     * @return Unmodifiable list of all windows in the TextGUI at the time of the call
     */
    Collection<Window> getWindows();

    /**
     * Selects a particular window to be considered 'active' and receive all input events
     * @param activeWindow Window to become active and receive input events
     * @return The WindowBasedTextGUI itself
     */
    WindowBasedTextGUI setActiveWindow(Window activeWindow);

    /**
     * Returns the window which the TextGUI considers the active one at the time of the method call. The active window
     * is generally the one which relieves all keyboard input.
     * @return Active window in the TextGUI or {@code null}
     */
    Window getActiveWindow();


    /**
     * Returns the container for the background, which works as a single large component that takes up the whole
     * terminal area and is always behind all windows.
     * @return The {@code BasePane} used by this {@code WindowBasedTextGUI}
     */
    BasePane getBackgroundPane();

    /**
     * Returns the {@link Screen} for this {@link WindowBasedTextGUI}
     * @return the {@link Screen} used by this {@link WindowBasedTextGUI}
     */
    Screen getScreen();

    /**
     * Returns the {@link WindowPostRenderer} for this {@link WindowBasedTextGUI}
     * @return the {@link WindowPostRenderer} for this {@link WindowBasedTextGUI}
     */
    WindowPostRenderer getWindowPostRenderer();

    /**
     * Windows are internally stored as a stack and newer windows are added at the top of the stack. The GUI system will
     * render windows in a predictable order from bottom to top. This method allows you to move a Window from its
     * current position in the stack to the top, meaning it will be rendered last. This mean it will overlap all other
     * windows and because of this visually appear on top.
     * @param window Window in the stack to move to the top position
     * @return The WindowBasedTextGUI Itself
     */
    WindowBasedTextGUI moveToTop(Window window);

    /**
     * Takes the previously active window and makes it active, or if in reverse mode, takes the window at the bottom of
     * the stack, moves it to the front and makes it active.
     * @param reverse Direction to cycle through the windows
     * @return The WindowBasedTextGUI Itself
     */
    WindowBasedTextGUI cycleActiveWindow(boolean reverse);

    /**
     * Waits for the specified window to be closed
     * @param abstractWindow Window to wait for
     */
    void waitForWindowToClose(Window abstractWindow);
}
