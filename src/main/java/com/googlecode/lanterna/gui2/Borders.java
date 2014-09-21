package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.ACS;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;

/**
 * This class containers a couple of border implementation
 */
public class Borders {
    private Borders() {
    }

    public static Border singleLine() {
        return new SingleLine("");
    }

    public static Border singleLine(String title) {
        return new SingleLine(title);
    }

    public static Border doubleLine() {
        return new DoubleLine("");
    }

    public static Border doubleLine(String title) {
        return new DoubleLine(title);
    }

    private static abstract class StandardBorder implements Border {
        private final String title;

        protected StandardBorder(String title) {
            if(title == null) {
                throw new IllegalArgumentException("Cannot create a border with null title");
            }
            this.title = title;
        }

        @Override
        public TerminalSize getBorderSize(TerminalSize preferredSizeWithoutBorder) {
            return preferredSizeWithoutBorder.withRelativeColumns(2).withRelativeRows(2);
        }

        @Override
        public TerminalPosition getOffset() {
            return TerminalPosition.OFFSET_1x1;
        }

        @Override
        public TextGUIGraphics draw(TextGUIGraphics graphics) {
            TerminalSize drawableArea = graphics.getSize();
            graphics.applyThemeStyle(graphics.getThemeDefinition(StandardBorder.class).getNormal());

            char horizontalLine = getHorizontalLine(graphics);
            char verticalLine = getVerticalLine(graphics);
            char bottomLeftCorner = getBottomLeftCorner(graphics);
            char topLeftCorner = getTopLeftCorner(graphics);
            char bottomRightCorner = getBottomRightCorner(graphics);
            char topRightCorner = getTopRightCorner(graphics);

            graphics.setPosition(0, drawableArea.getRows() - 1)
                    .setCharacter(bottomLeftCorner);
            graphics.setPosition(0, drawableArea.getRows() - 2)
                    .drawLine(new TerminalPosition(0, 1), verticalLine);
            graphics.setPosition(0, 0)
                    .setCharacter(topLeftCorner);
            graphics.setPosition(1, 0)
                    .drawLine(new TerminalPosition(drawableArea.getColumns() - 2, 0), horizontalLine);

            graphics.setPosition(drawableArea.getColumns() - 1, 0)
                    .setCharacter(topRightCorner);
            graphics.setPosition(drawableArea.getColumns() - 1, 1)
                    .drawLine(new TerminalPosition(drawableArea.getColumns() - 1, drawableArea.getRows() - 2), verticalLine);
            graphics.setPosition(drawableArea.getColumns() - 1, drawableArea.getRows() - 1)
                    .setCharacter(bottomRightCorner);
            graphics.setPosition(1, drawableArea.getRows() - 1)
                    .drawLine(new TerminalPosition(drawableArea.getColumns() - 2, drawableArea.getRows() - 1), horizontalLine);

            if(drawableArea.getColumns() > title.length() + 4) {
                graphics.putString(2, 0, title);
            }

            return graphics.newTextGraphics(TerminalPosition.OFFSET_1x1, drawableArea.withRelativeColumns(-2).withRelativeRows(-2));
        }

        protected abstract char getHorizontalLine(TextGUIGraphics graphics);
        protected abstract char getVerticalLine(TextGUIGraphics graphics);
        protected abstract char getBottomLeftCorner(TextGUIGraphics graphics);
        protected abstract char getTopLeftCorner(TextGUIGraphics graphics);
        protected abstract char getBottomRightCorner(TextGUIGraphics graphics);
        protected abstract char getTopRightCorner(TextGUIGraphics graphics);
    }

    private static class SingleLine extends StandardBorder {
        private SingleLine(String title) {
            super(title);
        }

        @Override
        protected char getTopRightCorner(TextGUIGraphics graphics) {
            return graphics.getThemeDefinition(SingleLine.class).getCharacter("TOP_RIGHT_CORNER", ACS.SINGLE_LINE_TOP_RIGHT_CORNER);
        }

        @Override
        protected char getBottomRightCorner(TextGUIGraphics graphics) {
            return graphics.getThemeDefinition(SingleLine.class).getCharacter("BOTTOM_RIGHT_CORNER", ACS.SINGLE_LINE_BOTTOM_RIGHT_CORNER);
        }

        @Override
        protected char getTopLeftCorner(TextGUIGraphics graphics) {
            return graphics.getThemeDefinition(SingleLine.class).getCharacter("TOP_LEFT_CORNER", ACS.SINGLE_LINE_TOP_LEFT_CORNER);
        }

        @Override
        protected char getBottomLeftCorner(TextGUIGraphics graphics) {
            return graphics.getThemeDefinition(SingleLine.class).getCharacter("BOTTOM_LEFT_CORNER", ACS.SINGLE_LINE_BOTTOM_LEFT_CORNER);
        }

        @Override
        protected char getVerticalLine(TextGUIGraphics graphics) {
            return graphics.getThemeDefinition(SingleLine.class).getCharacter("VERTICAL_LINE", ACS.SINGLE_LINE_VERTICAL);
        }

        @Override
        protected char getHorizontalLine(TextGUIGraphics graphics) {
            return graphics.getThemeDefinition(SingleLine.class).getCharacter("HORIZONTAL_LINE", ACS.SINGLE_LINE_HORIZONTAL);
        }
    }

    private static class DoubleLine extends StandardBorder {
        private DoubleLine(String title) {
            super(title);
        }

        @Override
        protected char getTopRightCorner(TextGUIGraphics graphics) {
            return graphics.getThemeDefinition(DoubleLine.class).getCharacter("TOP_RIGHT_CORNER", ACS.DOUBLE_LINE_TOP_RIGHT_CORNER);
        }

        @Override
        protected char getBottomRightCorner(TextGUIGraphics graphics) {
            return graphics.getThemeDefinition(DoubleLine.class).getCharacter("BOTTOM_RIGHT_CORNER", ACS.DOUBLE_LINE_BOTTOM_RIGHT_CORNER);
        }

        @Override
        protected char getTopLeftCorner(TextGUIGraphics graphics) {
            return graphics.getThemeDefinition(DoubleLine.class).getCharacter("TOP_LEFT_CORNER", ACS.DOUBLE_LINE_TOP_LEFT_CORNER);
        }

        @Override
        protected char getBottomLeftCorner(TextGUIGraphics graphics) {
            return graphics.getThemeDefinition(DoubleLine.class).getCharacter("BOTTOM_LEFT_CORNER", ACS.DOUBLE_LINE_BOTTOM_LEFT_CORNER);
        }

        @Override
        protected char getVerticalLine(TextGUIGraphics graphics) {
            return graphics.getThemeDefinition(DoubleLine.class).getCharacter("VERTICAL_LINE", ACS.DOUBLE_LINE_VERTICAL);
        }

        @Override
        protected char getHorizontalLine(TextGUIGraphics graphics) {
            return graphics.getThemeDefinition(DoubleLine.class).getCharacter("HORIZONTAL_LINE", ACS.DOUBLE_LINE_HORIZONTAL);
        }
    }
}
