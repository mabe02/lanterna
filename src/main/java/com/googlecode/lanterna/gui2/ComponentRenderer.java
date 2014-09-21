package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalSize;

/**
 * @author Martin
 */
public interface ComponentRenderer<T extends Component> {
    TerminalSize getPreferredSize(T component);
    void drawComponent(TextGUIGraphics graphics, T component);
}
