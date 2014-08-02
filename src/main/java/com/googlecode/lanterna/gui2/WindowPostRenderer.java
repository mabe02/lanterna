package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;

/**
 * Classes implementing this interface can be used along with DefaultWindowManagerTextGUI to put some extra processing
 * after a window has been rendered. This is used for making window shadows but can be used for anything.
 * @author Martin
 */
public interface WindowPostRenderer {
    /**
     * Called by DefaultWindowTextGUI after a Window has been rendered, to let you do any post-rendering. You will have
     * a TextGraphics object that can draw to the whole screen.
     * @param textGraphics Graphics object you can use to draw with
     * @param textGUI TextGUI that we are in
     * @param window Window that was just rendered
     * @param windowPosition The top-left position of the window, as decided earlier by the window manager
     * @param windowSize The size of the window, as decided earlier by the window manager
     */
    void postRender(
            TextGraphics textGraphics,
            TextGUI textGUI,
            Window window,
            TerminalPosition windowPosition,
            TerminalSize windowSize);
}
