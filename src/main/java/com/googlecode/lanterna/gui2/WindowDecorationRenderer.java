package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;

/**
 * Interface that defines a class that draws window decorations
 * @author Martin
 */
public interface WindowDecorationRenderer {
    /**
     * Draws the window decorations for a particular window and returns a new TextGraphics that is locked to the inside
     * of the window decorations
     * @param textGUI Which TextGUI is calling
     * @param graphics Graphics to use for drawing
     * @param window Window to draw
     * @return A new TextGraphics that is limited to the area inside the decorations just drawn
     */
    TextGUIGraphics draw(TextGUI textGUI, TextGUIGraphics graphics, Window window);

    /**
     * Retrieves the full size of the window, including all window decorations, given all components inside the window.
     * @param window Window to calculate size for
     * @param componentSize Size of the components
     * @return Full size of the window, including decorations
     */
    TerminalSize getDecoratedSize(Window window, TerminalSize componentSize);

    /**
     * Returns how much to step right and down from the top left position of the window decorations to the top left
     * position of the actual window
     * @param window Window to get the offset for
     * @return Position of the top left corner of the window, relative to the top left corner of the window decoration
     */
    TerminalPosition getOffset(Window window);
}
