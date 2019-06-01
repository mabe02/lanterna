/*
 * This file is part of lanterna (http://code.google.com/p/lanterna/).
 *
 * lanterna is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2010-2019 Martin Berglund
 */
package com.googlecode.lanterna.graphics;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.screen.TabBehaviour;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;

/**
 * This class hold the default logic for drawing the basic text graphic as exposed by TextGraphic. All implementations
 * rely on a setCharacter method being implemented in subclasses.
 * @author Martin
 */
public abstract class AbstractTextGraphics implements TextGraphics {
    protected TextColor foregroundColor;
    protected TextColor backgroundColor;
    protected TabBehaviour tabBehaviour;
    protected final EnumSet<SGR> activeModifiers;
    private final ShapeRenderer shapeRenderer;

    protected AbstractTextGraphics() {
        this.activeModifiers = EnumSet.noneOf(SGR.class);
        this.tabBehaviour = TabBehaviour.ALIGN_TO_COLUMN_4;
        this.foregroundColor = TextColor.ANSI.DEFAULT;
        this.backgroundColor = TextColor.ANSI.DEFAULT;
        this.shapeRenderer = new DefaultShapeRenderer(new DefaultShapeRenderer.Callback() {
            @Override
            public void onPoint(int column, int row, TextCharacter character) {
                setCharacter(column, row, character);
            }
        });
    }

    @Override
    public TextColor getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public TextGraphics setBackgroundColor(final TextColor backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    @Override
    public TextColor getForegroundColor() {
        return foregroundColor;
    }

    @Override
    public TextGraphics setForegroundColor(final TextColor foregroundColor) {
        this.foregroundColor = foregroundColor;
        return this;
    }

    @Override
    public TextGraphics enableModifiers(SGR... modifiers) {
        enableModifiers(Arrays.asList(modifiers));
        return this;
    }

    private void enableModifiers(Collection<SGR> modifiers) {
        this.activeModifiers.addAll(modifiers);
    }

    @Override
    public TextGraphics disableModifiers(SGR... modifiers) {
        disableModifiers(Arrays.asList(modifiers));
        return this;
    }

    private void disableModifiers(Collection<SGR> modifiers) {
        this.activeModifiers.removeAll(modifiers);
    }

    @Override
    public synchronized TextGraphics setModifiers(EnumSet<SGR> modifiers) {
        activeModifiers.clear();
        activeModifiers.addAll(modifiers);
        return this;
    }

    @Override
    public TextGraphics clearModifiers() {
        this.activeModifiers.clear();
        return this;
    }

    @Override
    public EnumSet<SGR> getActiveModifiers() {
        return activeModifiers;
    }

    @Override
    public TabBehaviour getTabBehaviour() {
        return tabBehaviour;
    }

    @Override
    public TextGraphics setTabBehaviour(TabBehaviour tabBehaviour) {
        if(tabBehaviour != null) {
            this.tabBehaviour = tabBehaviour;
        }
        return this;
    }

    @Override
    public TextGraphics fill(char c) {
        fillRectangle(TerminalPosition.TOP_LEFT_CORNER, getSize(), c);
        return this;
    }

    @Override
    public TextGraphics setCharacter(int column, int row, char character) {
        return setCharacter(column, row, newTextCharacter(character));
    }

    @Override
    public TextGraphics setCharacter(TerminalPosition position, TextCharacter textCharacter) {
        setCharacter(position.getColumn(), position.getRow(), textCharacter);
        return this;
    }

    @Override
    public TextGraphics setCharacter(TerminalPosition position, char character) {
        return setCharacter(position.getColumn(), position.getRow(), character);
    }

    @Override
    public TextGraphics drawLine(TerminalPosition fromPosition, TerminalPosition toPoint, char character) {
        return drawLine(fromPosition, toPoint, newTextCharacter(character));
    }

    @Override
    public TextGraphics drawLine(TerminalPosition fromPoint, TerminalPosition toPoint, TextCharacter character) {
        shapeRenderer.drawLine(fromPoint, toPoint, character);
        return this;
    }

    @Override
    public TextGraphics drawLine(int fromX, int fromY, int toX, int toY, char character) {
        return drawLine(fromX, fromY, toX, toY, newTextCharacter(character));
    }

    @Override
    public TextGraphics drawLine(int fromX, int fromY, int toX, int toY, TextCharacter character) {
        return drawLine(new TerminalPosition(fromX, fromY), new TerminalPosition(toX, toY), character);
    }

    @Override
    public TextGraphics drawTriangle(TerminalPosition p1, TerminalPosition p2, TerminalPosition p3, char character) {
        return drawTriangle(p1, p2, p3, newTextCharacter(character));
    }

    @Override
    public TextGraphics drawTriangle(TerminalPosition p1, TerminalPosition p2, TerminalPosition p3, TextCharacter character) {
        shapeRenderer.drawTriangle(p1, p2, p3, character);
        return this;
    }

    @Override
    public TextGraphics fillTriangle(TerminalPosition p1, TerminalPosition p2, TerminalPosition p3, char character) {
        return fillTriangle(p1, p2, p3, newTextCharacter(character));
    }

    @Override
    public TextGraphics fillTriangle(TerminalPosition p1, TerminalPosition p2, TerminalPosition p3, TextCharacter character) {
        shapeRenderer.fillTriangle(p1, p2, p3, character);
        return this;
    }

    @Override
    public TextGraphics drawRectangle(TerminalPosition topLeft, TerminalSize size, char character) {
        return drawRectangle(topLeft, size, newTextCharacter(character));
    }

    @Override
    public TextGraphics drawRectangle(TerminalPosition topLeft, TerminalSize size, TextCharacter character) {
        shapeRenderer.drawRectangle(topLeft, size, character);
        return this;
    }

    @Override
    public TextGraphics fillRectangle(TerminalPosition topLeft, TerminalSize size, char character) {
        return fillRectangle(topLeft, size, newTextCharacter(character));
    }

    @Override
    public TextGraphics fillRectangle(TerminalPosition topLeft, TerminalSize size, TextCharacter character) {
        shapeRenderer.fillRectangle(topLeft, size, character);
        return this;
    }

    @Override
    public TextGraphics drawImage(TerminalPosition topLeft, TextImage image) {
        return drawImage(topLeft, image, TerminalPosition.TOP_LEFT_CORNER, image.getSize());
    }

    @Override
    public TextGraphics drawImage(
            TerminalPosition topLeft,
            TextImage image,
            TerminalPosition sourceImageTopLeft,
            TerminalSize sourceImageSize) {

        // If the source image position is negative, offset the whole image
        if(sourceImageTopLeft.getColumn() < 0) {
            topLeft = topLeft.withRelativeColumn(-sourceImageTopLeft.getColumn());
            sourceImageSize = sourceImageSize.withRelativeColumns(sourceImageTopLeft.getColumn());
            sourceImageTopLeft = sourceImageTopLeft.withColumn(0);
        }
        if(sourceImageTopLeft.getRow() < 0) {
            topLeft = topLeft.withRelativeRow(-sourceImageTopLeft.getRow());
            sourceImageSize = sourceImageSize.withRelativeRows(sourceImageTopLeft.getRow());
            sourceImageTopLeft = sourceImageTopLeft.withRow(0);
        }

        // cropping specified image-subrectangle to the image itself:
        int fromRow = Math.max(sourceImageTopLeft.getRow(), 0);
        int untilRow = Math.min(sourceImageTopLeft.getRow() + sourceImageSize.getRows(), image.getSize().getRows());
        int fromColumn = Math.max(sourceImageTopLeft.getColumn(), 0);
        int untilColumn = Math.min(sourceImageTopLeft.getColumn() + sourceImageSize.getColumns(), image.getSize().getColumns());

        // difference between position in image and position on target:
        int diffRow = topLeft.getRow() - sourceImageTopLeft.getRow();
        int diffColumn = topLeft.getColumn() - sourceImageTopLeft.getColumn();

        // top/left-crop at target(TextGraphics) rectangle: (only matters, if topLeft has a negative coordinate)
        fromRow = Math.max(fromRow, -diffRow);
        fromColumn = Math.max(fromColumn, -diffColumn);

        // bot/right-crop at target(TextGraphics) rectangle: (only matters, if topLeft has a negative coordinate)
        untilRow = Math.min(untilRow, getSize().getRows() - diffRow);
        untilColumn = Math.min(untilColumn, getSize().getColumns() - diffColumn);

        if (fromRow >= untilRow || fromColumn >= untilColumn) {
            return this;
        }
        for (int row = fromRow; row < untilRow; row++) {
            for (int column = fromColumn; column < untilColumn; column++) {
                setCharacter(column + diffColumn, row + diffRow, image.getCharacterAt(column, row));
            }
        }
        return this;
    }

    @Override
    public TextGraphics putString(int column, int row, String string) {
        string = prepareStringForPut(column, string);
        int offset = 0;
        for(int i = 0; i < string.length(); i++) {
            char character = string.charAt(i);
            setCharacter(column + offset, row, newTextCharacter(character));
            offset += getOffsetToNextCharacter(character);
        }
        return this;
    }

    @Override
    public TextGraphics putString(TerminalPosition position, String string) {
        putString(position.getColumn(), position.getRow(), string);
        return this;
    }

    @Override
    public TextGraphics putString(int column, int row, String string, SGR extraModifier, SGR... optionalExtraModifiers) {
        clearModifiers();
        return putString(column, row, string, EnumSet.of(extraModifier, optionalExtraModifiers));
    }

    @Override
    public TextGraphics putString(int column, int row, String string, Collection<SGR> extraModifiers) {
        Collection<SGR> newModifiers = EnumSet.copyOf(extraModifiers);
        newModifiers.removeAll(activeModifiers);
        enableModifiers(newModifiers);
        putString(column, row, string);
        disableModifiers(newModifiers);
        return this;
    }

    @Override
    public TextGraphics putString(TerminalPosition position, String string, SGR extraModifier, SGR... optionalExtraModifiers) {
        putString(position.getColumn(), position.getRow(), string, extraModifier, optionalExtraModifiers);
        return this;
    }

    @Override
    public synchronized TextGraphics putCSIStyledString(int column, int row, String string) {
        StyleSet.Set original = new StyleSet.Set(this);
        string = prepareStringForPut(column, string);
        int offset = 0;
        for(int i = 0; i < string.length(); i++) {
            char character = string.charAt(i);
            String controlSequence = TerminalTextUtils.getANSIControlSequenceAt(string, i);
            if(controlSequence != null) {
                TerminalTextUtils.updateModifiersFromCSICode(controlSequence, this, original);

                // Skip the control sequence, leaving one extra, since we'll add it when we loop
                i += controlSequence.length() - 1;
                continue;
            }

            setCharacter(column + offset, row, newTextCharacter(character));
            offset += getOffsetToNextCharacter(character);
        }

        setStyleFrom(original);
        return this;
    }

    @Override
    public TextGraphics putCSIStyledString(TerminalPosition position, String string) {
        return putCSIStyledString(position.getColumn(), position.getRow(), string);
    }

    @Override
    public TextCharacter getCharacter(TerminalPosition position) {
        return getCharacter(position.getColumn(), position.getRow());
    }

    @Override
    public TextGraphics newTextGraphics(TerminalPosition topLeftCorner, TerminalSize size) throws IllegalArgumentException {
        TerminalSize writableArea = getSize();
        if(topLeftCorner.getColumn() + size.getColumns() <= 0 ||
                topLeftCorner.getColumn() >= writableArea.getColumns() ||
                topLeftCorner.getRow() + size.getRows() <= 0 ||
                topLeftCorner.getRow() >= writableArea.getRows()) {
            //The area selected is completely outside of this TextGraphics, so we can return a "null" object that doesn't
            //do anything because it is impossible to change anything anyway
            return new NullTextGraphics(size);
        }
        return new SubTextGraphics(this, topLeftCorner, size);
    }

    private TextCharacter newTextCharacter(char character) {
        return new TextCharacter(character, foregroundColor, backgroundColor, activeModifiers);
    }

    private String prepareStringForPut(int column, String string) {
        if(string.contains("\n")) {
            string = string.substring(0, string.indexOf("\n"));
        }
        if(string.contains("\r")) {
            string = string.substring(0, string.indexOf("\r"));
        }
        string = tabBehaviour.replaceTabs(string, column);
        return string;
    }

    private int getOffsetToNextCharacter(char character) {
        if(TerminalTextUtils.isCharDoubleWidth(character)) {
            //CJK characters are twice the normal characters in width, so next character position is two columns forward
            return 2;
        }
        else {
            //For "normal" characters we advance to the next column
            return 1;
        }
    }

    @Override
    public TextGraphics setStyleFrom(StyleSet<?> source) {
        setBackgroundColor(source.getBackgroundColor());
        setForegroundColor(source.getForegroundColor());
        setModifiers(source.getActiveModifiers());
        return this;
    }

}
