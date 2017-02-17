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
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.graphics.Theme;
import com.googlecode.lanterna.input.KeyStroke;

import java.io.IOException;

/**
 * This is the base interface for advanced text GUIs supported in Lanterna. You may want to use this in combination with
 * a TextGUIThread, that can be created/retrieved by using {@code getGUIThread()}.
 * @author Martin
 */
public interface TextGUI {
    /**
     * Returns the theme currently assigned to this {@link TextGUI}
     * @return Currently active {@link Theme}
     */
    Theme getTheme();

    /**
     * Sets the global theme to be used by this TextGUI. This value will be set on every TextGUIGraphics object created
     * for drawing the GUI, but individual components can override this if they want. If you don't call this method
     * you should assume that a default theme is assigned by the library.
     * @param theme Theme to use as the default theme for this TextGUI
     */
    void setTheme(Theme theme);

    /**
     * Drains the input queue and passes the key strokes to the GUI system for processing. For window-based system, it
     * will send each key stroke to the active window for processing. If the input read gives an EOF, it will throw
     * EOFException and this is normally the signal to shut down the GUI (any command coming in before the EOF will be
     * processed as usual before this).
     * @return {@code true} if at least one key stroke was read and processed, {@code false} if there was nothing on the
     * input queue (only for non-blocking IO)
     * @throws java.io.IOException In case there was an underlying I/O error
     * @throws java.io.EOFException In the input stream received an EOF marker
     */
    boolean processInput() throws IOException;

    /**
     * Updates the screen, to make any changes visible to the user.
     * @throws java.io.IOException In case there was an underlying I/O error
     */
    void updateScreen() throws IOException;

    /**
     * This method can be used to determine if any component has requested a redraw. If this method returns
     * {@code true}, you may want to call {@code updateScreen()}.
     * @return {@code true} if this TextGUI has a change and is waiting for someone to call {@code updateScreen()}
     */
    boolean isPendingUpdate();

    /**
     * The first time this method is called, it will create a new TextGUIThread object that you can use to automatically
     * manage this TextGUI instead of manually calling {@code processInput()} and {@code updateScreen()}. After the
     * initial call, it will return the same object as it was originally returning.
     * @return A {@code TextGUIThread} implementation that can be used to asynchronously manage the GUI
     */
    TextGUIThread getGUIThread();

    /**
     * Returns the interactable component currently in focus
     * @return Component that is currently in input focus
     */
    Interactable getFocusedInteractable();

    /**
     * Adds a listener to this TextGUI to fire events on.
     * @param listener Listener to add
     */
    void addListener(Listener listener);
    
    /**
     * Removes a listener from this TextGUI so that it will no longer receive events
     * @param listener Listener to remove
     */
    void removeListener(Listener listener);
    
    /**
     * Listener interface for TextGUI, firing on events related to the overall GUI
     */
    interface Listener {
        /**
         * Fired either when no component was in focus during a keystroke or if the focused component and all its parent
         * containers chose not to handle the event. This event listener should also return {@code true} if the event
         * was processed in any way that requires the TextGUI to update itself, otherwise {@code false}.
         * @param textGUI TextGUI that had the event
         * @param keyStroke Keystroke that was unhandled
         * @return If the outcome of this KeyStroke processed by the implementer requires the TextGUI to re-draw, return
         * {@code true} here, otherwise {@code false}
         */
        boolean onUnhandledKeyStroke(TextGUI textGUI, KeyStroke keyStroke);
    }
}
