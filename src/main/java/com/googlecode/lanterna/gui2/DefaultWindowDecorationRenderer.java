package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.ThemeDefinition;

/**
 * Default window decoration renderer
 * @author Martin
 */
public class DefaultWindowDecorationRenderer implements WindowDecorationRenderer {
    @Override
    public TextGUIGraphics draw(TextGUI textGUI, TextGUIGraphics graphics, Window window) {
        String title = window.getTitle();
        if(title == null) {
            title = "";
        }

        ThemeDefinition themeDefinition = graphics.getThemeDefinition(DefaultWindowDecorationRenderer.class);

        //Resize if necessary
        TerminalSize drawableArea = graphics.getSize();
        title = title.substring(0, Math.min(title.length(), drawableArea.getColumns() - 3));
        /*
        graphics.setForegroundColor(TextColor.ANSI.WHITE)
                .setBackgroundColor(TextColor.ANSI.WHITE)
                .enableModifiers(SGR.BOLD);
        */
        graphics.applyThemeStyle(themeDefinition.getPreLight());

        char horizontalLine = themeDefinition.getCharacter("HORIZONTAL_LINE", ACS.SINGLE_LINE_HORIZONTAL);
        char verticalLine = themeDefinition.getCharacter("VERTICAL_LINE", ACS.SINGLE_LINE_VERTICAL);
        char bottomLeftCorner = themeDefinition.getCharacter("BOTTOM_LEFT_CORNER", ACS.SINGLE_LINE_BOTTOM_LEFT_CORNER);
        char topLeftCorner = themeDefinition.getCharacter("TOP_LEFT_CORNER", ACS.SINGLE_LINE_TOP_LEFT_CORNER);
        char bottomRightCorner = themeDefinition.getCharacter("BOTTOM_RIGHT_CORNER", ACS.SINGLE_LINE_BOTTOM_RIGHT_CORNER);
        char topRightCorner = themeDefinition.getCharacter("TOP_RIGHT_CORNER", ACS.SINGLE_LINE_TOP_RIGHT_CORNER);

        graphics.setPosition(0, drawableArea.getRows() - 1)
                .setCharacter(bottomLeftCorner);
        graphics.setPosition(0, drawableArea.getRows() - 2)
                .drawLine(new TerminalPosition(0, 1), verticalLine);
        graphics.setPosition(0, 0)
                .setCharacter(topLeftCorner);
        graphics.setPosition(1, 0)
                .drawLine(new TerminalPosition(drawableArea.getColumns() - 2, 0), horizontalLine);

        /*
        graphics.setForegroundColor(TextColor.ANSI.BLACK)
                .setBackgroundColor(TextColor.ANSI.WHITE)
                .enableModifiers(SGR.BOLD);
        */
        graphics.applyThemeStyle(themeDefinition.getNormal());

        graphics.setPosition(drawableArea.getColumns() - 1, 0)
                .setCharacter(topRightCorner);
        graphics.setPosition(drawableArea.getColumns() - 1, 1)
                .drawLine(new TerminalPosition(drawableArea.getColumns() - 1, drawableArea.getRows() - 2), verticalLine);
        graphics.setPosition(drawableArea.getColumns() - 1, drawableArea.getRows() - 1)
                .setCharacter(bottomRightCorner);
        graphics.setPosition(1, drawableArea.getRows() - 1)
                .drawLine(new TerminalPosition(drawableArea.getColumns() - 2, drawableArea.getRows() - 1), horizontalLine);

        if(title.length() > 0) {
            graphics.putString(2, 0, title);
        }

        return graphics.newTextGraphics(new TerminalPosition(1, 1), graphics.getSize().withRelativeColumns(-2).withRelativeRows(-2));
    }

    @Override
    public TerminalSize getDecoratedSize(Window window, TerminalSize contentAreaSize) {
        return contentAreaSize.withColumns(contentAreaSize.getColumns() + 2).withRows(contentAreaSize.getRows() + 2);
    }

    private static final TerminalPosition OFFSET = new TerminalPosition(1, 1);

    @Override
    public TerminalPosition getOffset(Window window) {
        return OFFSET;
    }
}
