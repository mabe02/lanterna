package com.googlecode.lanterna.terminal;

import com.googlecode.lanterna.TerminalSize;

/**
 * Listener interface that can be used to be alerted on terminal resizing
 */
public interface TerminalResizeListener {
    /**
     * The terminal has changed its size, most likely because the user has resized the window. This callback is
     * invoked by something inside the lanterna library, it could be a signal handler thread, it could be the AWT
     * thread, it could be something else, so please be careful with what kind of operation you do in here. Also,
     * make sure not to take too long before returning. Best practice would be to update an internal status in your
     * program to mark that the terminal has been resized (possibly along with the new size) and then in your main
     * loop you deal with this at the beginning of each redraw.
     * @param terminal Terminal that was resized
     * @param newSize Size of the terminal after the resize
     */
    @SuppressWarnings("UnusedParameters")
    void onResized(Terminal terminal, TerminalSize newSize);
}
