package com.googlecode.lanterna.gui2;

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
     * Takes a component and a size specification for how large the component wants to be and returns a new size
     * specification for the component wrapped in this border. Normally, this is the incoming size plus one column on
     * each side and one row above and below.
     * @param component Component we are surrounding with a border
     * @param preferredSizeWithoutBorder Preferred size of the component without border
     * @return Size of the component, surrounded by this border
     */
    TerminalSize getBorderSize(Component component, TerminalSize preferredSizeWithoutBorder);
}
