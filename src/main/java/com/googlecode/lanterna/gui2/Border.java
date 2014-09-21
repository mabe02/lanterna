package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;

/**
 * Special interface for borders, which can be used to surround components
 * @author Martin
 */
public interface Border {
    /**
     * Draws the border and returns a new TextGUIGraphics that is reduced to the usable area inside the border.
     * @param graphics Graphics object to draw with
     * @return The graphics that can be used to draw the actual component
     */
    TextGUIGraphics draw(TextGUIGraphics graphics);

    /**
     * Takes a size specification for how large a component wants to be and returns a new size
     * specification for the component wrapped in this border. Normally, this is the incoming size plus one column on
     * each side and one row above and below.
     * @param preferredSizeWithoutBorder Preferred size of the component without border
     * @return Size of the component, surrounded by this border
     */
    TerminalSize getBorderSize(TerminalSize preferredSizeWithoutBorder);

    /**
     * Returns the offset position from the top left corner of the border to the top left position of the inner area
     * that the surrounded component can use.
     * @return Distance from the top left corner of the border to the top left corner of the component
     */
    TerminalPosition getOffset();
}
