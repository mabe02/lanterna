package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.graphics.TextGraphics;
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
    TextGraphics draw(TextGUI textGUI, TextGraphics graphics, Window window);

    /**
     * Retrieves the full size of the window, including all window decorations, given all components inside the window.
     * @param textGUI Which TextGUI is calling
     * @param window Window to calculate size for
     * @param componentSize Size of the components
     * @return Full size of the window, including decorations
     */
    TerminalSize getDecoratedSize(TextGUI textGUI, Window window, TerminalSize componentSize);
}
