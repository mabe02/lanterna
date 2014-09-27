package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;

/**
 * This WindowPostRenderer implementation draws a shadow under the previously rendered window
 * @author Martin
 */
public class WindowShadowRenderer implements WindowPostRenderer {
    @Override
    public void postRender(
            TextGraphics textGraphics,
            TextGUI textGUI,
            Window window,
            TerminalPosition windowPosition,
            TerminalSize windowSize) {

        textGraphics.setForegroundColor(TextColor.ANSI.BLACK);
        textGraphics.setBackgroundColor(TextColor.ANSI.BLACK);
        textGraphics.enableModifiers(SGR.BOLD);
        TerminalPosition start = windowPosition.withRelativeColumn(2).withRelativeRow(windowSize.getRows());
        textGraphics.drawLine(start, start.withRelativeColumn(windowSize.getColumns() - 1), ' ');
        textGraphics.drawLine(start, start.withRelativeRow(-windowSize.getRows() + 1), ' ');
        start = start.withRelativeColumn(-1);
        textGraphics.drawLine(start, start.withRelativeRow(windowSize.getRows() - 2), ' ');
    }
}
