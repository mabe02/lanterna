package com.googlecode.lanterna.terminal;

import java.io.IOException;

/**
 * This class extends the normal Terminal interface and adds a few more methods that are considered rare and shouldn't
 * be encouraged to be used. Some of these may move into Terminal if it turns out that they are indeed well-supported.
 * Most of these extensions are picked up from here: http://invisible-island.net/xterm/ctlseqs/ctlseqs.html
 * @author Martin
 */
public interface ExtendedTerminal extends Terminal {

    /**
     * Attempts to resize the terminal through dtterm extensions "CSI 8 ; rows ; columns ; t". This isn't widely
     * supported, which is why the method is not exposed through the common Terminal interface.
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
     * If set to true, will listen for mouse motions and add them to the input queue
     * @param enabled Whether to enable mouse motion capturing or not
     * @throws IOException If the was an underlying I/O error
     */
    void setMouseMovementCapturingEnabled(boolean enabled) throws IOException;

    /**
     * If set to true, will listen for mouse clicks and add those to the input queue
     * @param enable Whether to enable mouse click capturing or not
     * @throws IOException If the was an underlying I/O error
     */
    void setMouseClicksCapturingEnabled(boolean enable) throws IOException;
}
