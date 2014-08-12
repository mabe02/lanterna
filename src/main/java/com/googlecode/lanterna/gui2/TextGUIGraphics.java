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
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.*;
import com.googlecode.lanterna.screen.TabBehaviour;

import java.util.EnumSet;

/**
 * TextGraphics implementation used by TextGUI when doing any drawing operation. It's extending from
 * ImmutableThemedTextGraphics so you have theme support built in.
 * @author Martin
 */
public class TextGUIGraphics extends ImmutableThemedTextGraphics {
    private final TextGUI textGUI;

    public TextGUIGraphics(TextGUI textGUI, TextGraphics backend, Theme theme) {
        super(backend, theme);
        this.textGUI = textGUI;
    }

    public TextGUIGraphics withTheme(Theme theme) {
        return new TextGUIGraphics(textGUI, getBackend(), theme);
    }

    public TextGUI getTextGUI() {
        return textGUI;
    }

    @Override
    public TextGUIGraphics applyThemeStyle(ThemeStyle themeStyle) {
        super.applyThemeStyle(themeStyle);
        return this;
    }

    @Override
    public TextGUIGraphics newTextGraphics(TerminalPosition topLeftCorner, TerminalSize size) throws IllegalArgumentException {
        super.newTextGraphics(topLeftCorner, size);
        return this;
    }

    @Override
    public TextGUIGraphics setPosition(int column, int row) {
        super.setPosition(column, row);
        return this;
    }

    @Override
    public TextGUIGraphics setPosition(TerminalPosition newPosition) {
        super.setPosition(newPosition);
        return this;
    }

    @Override
    public TextGUIGraphics movePosition(int columns, int rows) {
        super.movePosition(columns, rows);
        return this;
    }

    @Override
    public TerminalPosition getPosition() {
        return super.getPosition();
    }

    @Override
    public TextColor getBackgroundColor() {
        return super.getBackgroundColor();
    }

    @Override
    public TextGUIGraphics setBackgroundColor(TextColor backgroundColor) {
        super.setBackgroundColor(backgroundColor);
        return this;
    }

    @Override
    public TextColor getForegroundColor() {
        return super.getForegroundColor();
    }

    @Override
    public TextGUIGraphics setForegroundColor(TextColor foregroundColor) {
        super.setForegroundColor(foregroundColor);
        return this;
    }

    @Override
    public TextGUIGraphics enableModifiers(SGR... modifiers) {
        super.enableModifiers(modifiers);
        return this;
    }

    @Override
    public TextGUIGraphics disableModifiers(SGR... modifiers) {
        super.disableModifiers(modifiers);
        return this;
    }

    @Override
    public TextGUIGraphics setModifiers(EnumSet<SGR> modifiers) {
        super.setModifiers(modifiers);
        return this;
    }

    @Override
    public TextGUIGraphics clearModifiers() {
        super.clearModifiers();
        return this;
    }

    @Override
    public EnumSet<SGR> getActiveModifiers() {
        return super.getActiveModifiers();
    }

    @Override
    public TabBehaviour getTabBehaviour() {
        return super.getTabBehaviour();
    }

    @Override
    public TextGUIGraphics setTabBehaviour(TabBehaviour tabBehaviour) {
        super.setTabBehaviour(tabBehaviour);
        return this;
    }

    @Override
    public TextGUIGraphics fillScreen(char c) {
        super.fillScreen(c);
        return this;
    }

    @Override
    public TextGUIGraphics setCharacter(char character) {
        super.setCharacter(character);
        return this;
    }

    @Override
    public TextGUIGraphics drawLine(TerminalPosition toPoint, char character) {
        super.drawLine(toPoint, character);
        return this;
    }

    @Override
    public TextGUIGraphics drawTriangle(TerminalPosition p1, TerminalPosition p2, char character) {
        super.drawTriangle(p1, p2, character);
        return this;
    }

    @Override
    public TextGUIGraphics fillTriangle(TerminalPosition p1, TerminalPosition p2, char character) {
        super.fillTriangle(p1, p2, character);
        return this;
    }

    @Override
    public TextGUIGraphics drawRectangle(TerminalSize size, char character) {
        super.drawRectangle(size, character);
        return this;
    }

    @Override
    public TextGUIGraphics fillRectangle(TerminalSize size, char character) {
        super.fillRectangle(size, character);
        return this;
    }

    @Override
    public TextGUIGraphics putString(String string) {
        super.putString(string);
        return this;
    }

    @Override
    public TextGUIGraphics putString(int column, int row, String string) {
        super.putString(column, row, string);
        return this;
    }

    @Override
    public TextGUIGraphics putString(TerminalPosition position, String string) {
        super.putString(position, string);
        return this;
    }

    @Override
    public TextGUIGraphics putString(int column, int row, String string, SGR extraModifier, SGR... optionalExtraModifiers) {
        super.putString(column, row, string, extraModifier, optionalExtraModifiers);
        return this;
    }

    @Override
    public TextGUIGraphics putString(TerminalPosition position, String string, SGR extraModifier, SGR... optionalExtraModifiers) {
        super.putString(position, string, extraModifier, optionalExtraModifiers);
        return this;
    }

    @Override
    public TextGUIGraphics putString(String string, SGR extraModifier, SGR... optionalExtraModifiers) {
        super.putString(string, extraModifier, optionalExtraModifiers);
        return this;
    }
}
