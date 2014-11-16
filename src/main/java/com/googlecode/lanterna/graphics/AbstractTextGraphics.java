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

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;

/**
 * This class hold the default logic for drawing the basic text graphic as exposed by TextGraphic. All implementations
 * reply on a setCharacter method being implemented in subclasses.
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
            public void onPoint(int column, int row, char character) {
                setCharacter(column, row, newScreenCharacter(character));
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
        setCharacter(column, row, newScreenCharacter(character));
        return this;
    }

    @Override
    public TextGraphics setCharacter(TerminalPosition position, char character) {
        setCharacter(position, newScreenCharacter(character));
        return this;
    }

    @Override
    public TextGraphics drawLine(TerminalPosition fromPosition, TerminalPosition toPoint, char character) {
        shapeRenderer.drawLine(fromPosition, toPoint, character);
        return this;
    }

    @Override
    public TextGraphics drawLine(int fromX, int fromY, int toX, int toY, char character) {
        return drawLine(new TerminalPosition(fromX, fromY), new TerminalPosition(toX, toY), character);
    }

    @Override
    public TextGraphics drawTriangle(TerminalPosition p1, TerminalPosition p2, TerminalPosition p3, char character) {
        shapeRenderer.drawTriangle(p1, p2, p3, character);
        return this;
    }

    @Override
    public TextGraphics fillTriangle(TerminalPosition p1, TerminalPosition p2, TerminalPosition p3, char character) {
        shapeRenderer.fillTriangle(p1, p2, p3, character);
        return this;
    }

    @Override
    public TextGraphics drawRectangle(TerminalPosition topLeft, TerminalSize size, char character) {
        shapeRenderer.drawRectangle(topLeft, size, character);
        return this;
    }

    @Override
    public TextGraphics fillRectangle(TerminalPosition topLeft, TerminalSize size, char character) {
        shapeRenderer.fillRectangle(topLeft, size, character);
        return this;
    }

    @Override
    public TextGraphics drawImage(TerminalPosition topLeft, TextImage image) {
        for(int row = 0; row < image.getSize().getRows(); row++) {
            for(int column = 0; column < image.getSize().getColumns(); column++) {
                setCharacter(column + topLeft.getColumn(), row + topLeft.getRow(), image.getCharacterAt(column, row));
            }
        }
        return this;
    }

    @Override
    public TextGraphics putString(int column, int row, String string) {
        if(string.contains("\n")) {
            string = string.substring(0, string.indexOf("\n"));
        }
        if(string.contains("\r")) {
            string = string.substring(0, string.indexOf("\r"));
        }
        string = tabBehaviour.replaceTabs(string, column);
        int offset = 0;
        for(int i = 0; i < string.length(); i++) {
            char character = string.charAt(i);
            setCharacter(
                    column + offset,
                    row,
                    new TextCharacter(
                            character,
                            foregroundColor,
                            backgroundColor,
                            activeModifiers.clone()));
            
            if(CJKUtils.isCharCJK(character)) {
                //CJK characters are twice the normal characters in width, so next character position is two columns forward
                offset += 2;
            }
            else {
                //For "normal" characters we advance to the next column
                offset += 1;
            }
        }
        return this;
    }

    @Override
    public TextGraphics putString(TerminalPosition position, String string) {
        putString(position.getColumn(), position.getRow(), string);
        return this;
    }

    @Override
    public TextGraphics putString(int column, int row, String string, SGR extraModifier, SGR... optionalExtraModifiers) {clearModifiers();
        EnumSet<SGR> extraModifiers = EnumSet.of(extraModifier, optionalExtraModifiers);
        extraModifiers.removeAll(activeModifiers);
        enableModifiers(extraModifiers);
        putString(column, row, string);
        disableModifiers(extraModifiers);
        return this;
    }

    @Override
    public TextGraphics putString(TerminalPosition position, String string, SGR extraModifier, SGR... optionalExtraModifiers) {
        putString(position.getColumn(), position.getRow(), string, extraModifier, optionalExtraModifiers);
        return this;
    }

    @Override
    public TextGraphics newTextGraphics(TerminalPosition topLeftCorner, TerminalSize size) throws IllegalArgumentException {
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
