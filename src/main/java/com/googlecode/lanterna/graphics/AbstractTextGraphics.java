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
 * Copyright (C) 2010-2014 Martin
 */
package com.googlecode.lanterna.graphics;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.screen.TabBehaviour;
import com.googlecode.lanterna.terminal.Terminal;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;

/**
 * This class hold the default logic for drawing the basic text graphic as exposed by TextGraphic. All implementations
 * reply on a setCharacter method being implemented in subclasses.
 * @author Martin
 */
public abstract class AbstractTextGraphics implements TextGraphics {

    protected TerminalPosition currentPosition;
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
        this.currentPosition = new TerminalPosition(0, 0);
        this.shapeRenderer = new DefaultShapeRenderer(new DefaultShapeRenderer.Callback() {
            @Override
            public void onPoint(int column, int row, char character) {
                setCharacter(column, row, newScreenCharacter(character));
            }
        });
    }

    @Override
    public TextGraphics setPosition(int column, int row) {
        return setPosition(new TerminalPosition(column, row));
    }

    @Override
    public TextGraphics setPosition(TerminalPosition newPosition) {
        this.currentPosition = newPosition;
        return this;
    }

    @Override
    public TextGraphics movePosition(int columns, int rows) {
        setPosition(getPosition().withRelativeColumn(columns).withRelativeRow(rows));
        return this;
    }

    @Override
    public TerminalPosition getPosition() {
        return currentPosition;
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
    public TextGraphics fillScreen(char c) {
        TerminalPosition savedPosition = getPosition();
        setPosition(TerminalPosition.TOP_LEFT_CORNER);
        fillRectangle(getSize(), c);
        setPosition(savedPosition);
        return this;
    }

    @Override
    public TextGraphics setCharacter(char character) {
        setCharacter(getPosition(), newScreenCharacter(character));
        return this;
    }

    @Override
    public TextGraphics drawLine(TerminalPosition toPoint, char character) {
        shapeRenderer.drawLine(getPosition(), toPoint, character);
        setPosition(toPoint);
        return this;
    }

    @Override
    public TextGraphics drawTriangle(TerminalPosition p1, TerminalPosition p2, char character) {
        TerminalPosition position = getPosition();
        shapeRenderer.drawTriangle(position, p1, p2, character);
        setPosition(position);
        return this;
    }

    @Override
    public TextGraphics fillTriangle(TerminalPosition p1, TerminalPosition p2, char character) {
        TerminalPosition position = getPosition();
        shapeRenderer.fillTriangle(position, p1, p2, character);
        setPosition(position);
        return this;
    }

    @Override
    public TextGraphics drawRectangle(TerminalSize size, char character) {
        shapeRenderer.drawRectangle(getPosition(), size, character);
        return this;
    }

    @Override
    public TextGraphics fillRectangle(TerminalSize size, char character) {
        shapeRenderer.fillRectangle(getPosition(), size, character);
        return this;
    }

    @Override
    public TextGraphics putString(String string) {
        if(string.contains("\n")) {
            string = string.substring(0, string.indexOf("\n"));
        }
        if(string.contains("\r")) {
            string = string.substring(0, string.indexOf("\r"));
        }
        string = tabBehaviour.replaceTabs(string, currentPosition.getColumn());
        for(int i = 0; i < string.length(); i++) {
            char character = string.charAt(i);
            setCharacter(
                    currentPosition.withRelativeColumn(i),
                    new TextCharacter(
                            character,
                            foregroundColor,
                            backgroundColor,
                            activeModifiers.clone()));

            //CJK characters are twice the normal characters in width
            if(CJKUtils.isCharCJK(character)) {
                i++;
            }
        }
        currentPosition = currentPosition.withRelativeColumn(string.length());
        return this;
    }

    @Override
    public TextGraphics putString(int column, int row, String string) {
        putString(new TerminalPosition(column, row), string);
        return this;
    }

    @Override
    public TextGraphics putString(TerminalPosition position, String string) {
        setPosition(position);
        putString(string);
        return this;
    }

    @Override
    public TextGraphics putString(int column, int row, String string, SGR extraModifier, SGR... optionalExtraModifiers) {
        putString(new TerminalPosition(column, row), string, extraModifier, optionalExtraModifiers);
        return this;
    }

    @Override
    public TextGraphics putString(TerminalPosition position, String string, SGR extraModifier, SGR... optionalExtraModifiers) {
        setPosition(position);
        putString(string, extraModifier, optionalExtraModifiers);
        return this;
    }

    @Override
    public TextGraphics putString(String string, SGR extraModifier, SGR... optionalExtraModifiers) {
        clearModifiers();
        EnumSet<SGR> extraModifiers = EnumSet.of(extraModifier, optionalExtraModifiers);
        extraModifiers.removeAll(activeModifiers);
        enableModifiers(extraModifiers);
        putString(string);
        disableModifiers(extraModifiers);
        return this;
    }

    @Override
    public TextGraphics newTextGraphics(TerminalPosition topLeftCorner, TerminalSize size) throws IllegalArgumentException {
        TerminalSize writableArea = getSize();
        if(topLeftCorner.getColumn() + size.getColumns() > writableArea.getColumns() ||
                topLeftCorner.getRow() + size.getRows() > writableArea.getRows()) {
            throw new IllegalArgumentException("Cannot create a sub-text graphics with topLeft = " + topLeftCorner +
                    " and size = " + size + " when writable area of the current text graphics is " + writableArea);
        }
        return new SubTextGraphics(this, topLeftCorner, size);
    }

    private void setCharacter(TerminalPosition position, TextCharacter textCharacter) {
        setCharacter(position.getColumn(), position.getRow(), textCharacter);
    }

    protected abstract void setCharacter(int columnIndex, int rowIndex, TextCharacter textCharacter);

    private TextCharacter newScreenCharacter(char character) {
        return new TextCharacter(character, foregroundColor, backgroundColor, activeModifiers);
    }
}
