package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.graphics.Theme;

import java.io.IOException;

/**
 * This is the base interface for advanced text GUIs supported in Lanterna. You may want to use this in combination with
 * a TextGUIThread, that can be created/retrieved by using {@code getGUIThread()}.
 * @author Martin
 */
public interface TextGUI {
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
}
