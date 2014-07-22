package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.ACS;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.terminal.*;

/**
 * Created by martin on 19/07/14.
 */
public class DefaultWindowDecorationRenderer implements WindowDecorationRenderer {
    @Override
    public TextGraphics draw(TextGUI textGUI, TextGraphics graphics, Window window) {
        String title = window.getTitle();
        if(title == null) {
            title = "";
        }

        //Resize if necessary
        TerminalSize drawableArea = graphics.getSize();
        title = title.substring(0, Math.min(title.length(), drawableArea.getColumns() - 3));
        graphics.setForegroundColor(TextColor.ANSI.WHITE)
                .setBackgroundColor(TextColor.ANSI.WHITE)
                .enableModifiers(Terminal.SGR.BOLD);

        graphics.setPosition(0, drawableArea.getRows() - 1)
                .setCharacter(ACS.SINGLE_LINE_BOTTOM_LEFT_CORNER);
        graphics.setPosition(0, drawableArea.getRows() - 2)
                .drawLine(new TerminalPosition(0, 1), ACS.SINGLE_LINE_VERTICAL);
        graphics.setPosition(0, 0)
                .setCharacter(ACS.SINGLE_LINE_TOP_LEFT_CORNER);
        graphics.setPosition(1, 0)
                .drawLine(new TerminalPosition(drawableArea.getColumns() - 2, 0), ACS.SINGLE_LINE_HORIZONTAL);

        graphics.setForegroundColor(TextColor.ANSI.BLACK)
                .setBackgroundColor(TextColor.ANSI.WHITE)
                .enableModifiers(Terminal.SGR.BOLD);

        graphics.setPosition(drawableArea.getColumns() - 1, 0)
                .setCharacter(ACS.SINGLE_LINE_TOP_RIGHT_CORNER);
        graphics.setPosition(drawableArea.getColumns() - 1, 1)
                .drawLine(new TerminalPosition(drawableArea.getColumns() - 1, drawableArea.getRows() - 2), ACS.SINGLE_LINE_VERTICAL);
        graphics.setPosition(drawableArea.getColumns() - 1, drawableArea.getRows() - 1)
                .setCharacter(ACS.SINGLE_LINE_BOTTOM_RIGHT_CORNER);
        graphics.setPosition(1, drawableArea.getRows() - 1)
                .drawLine(new TerminalPosition(drawableArea.getColumns() - 2, drawableArea.getRows() - 1), ACS.SINGLE_LINE_HORIZONTAL);

        if(title.length() > 0) {
            graphics.putString(2, 0, title);
        }

        return graphics.newTextGraphics(new TerminalPosition(1, 1), graphics.getSize().withRelativeColumns(-2).withRelativeRows(-2));
    }

    @Override
    public TerminalSize getDecoratedSize(TextGUI textGUI, Window window, TerminalSize componentSize) {
        return componentSize.withColumns(componentSize.getColumns() + 2).withRows(componentSize.getRows() + 2);
    }
}
