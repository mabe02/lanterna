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
 * Copyright (C) 2010-2016 Martin
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.*;
import com.googlecode.lanterna.screen.TabBehaviour;

import java.util.Collection;
import java.util.EnumSet;

/**
 * TextGraphics implementation used by TextGUI when doing any drawing operation.
 * @author Martin
 */
public final class TextGUIGraphics implements ThemedTextGraphics, TextGraphics {
    private final TextGUI textGUI;
    private final ImmutableThemedTextGraphics backend;

    TextGUIGraphics(TextGUI textGUI, TextGraphics backend, Theme theme) {
        this.backend = new ImmutableThemedTextGraphics(backend, theme);
        this.textGUI = textGUI;
    }

    @Override
    public Theme getTheme() {
        return backend.getTheme();
    }

    /**
     * Returns a new {@code TextGUIGraphics} object that has another theme attached to it
     * @param theme Theme to be used with the new {@code TextGUIGraphics}
     * @return New {@code TextGUIGraphics} that has the specified theme
     */
    public TextGUIGraphics withTheme(Theme theme) {
        return new TextGUIGraphics(textGUI, backend.getUnderlyingTextGraphics(), theme);
    }

    /**
     * Returns the {@code TextGUI} this {@code TextGUIGraphics} belongs to
     * @return {@code TextGUI} this {@code TextGUIGraphics} belongs to
     */
    public TextGUI getTextGUI() {
        return textGUI;
    }

    @Override
    public TextGUIGraphics newTextGraphics(TerminalPosition topLeftCorner, TerminalSize size) throws IllegalArgumentException {
        return new TextGUIGraphics(textGUI, backend.getUnderlyingTextGraphics().newTextGraphics(topLeftCorner, size), backend.getTheme());
    }

    @Override
    public TextGUIGraphics applyThemeStyle(ThemeStyle themeStyle) {
        backend.applyThemeStyle(themeStyle);
        return this;
    }

    @Override
    public ThemeDefinition getThemeDefinition(Class<?> clazz) {
        return backend.getThemeDefinition(clazz);
    }

    @Override
    public TerminalSize getSize() {
        return backend.getSize();
    }

    @Override
    public TextColor getBackgroundColor() {
        return backend.getBackgroundColor();
    }

    @Override
    public TextGUIGraphics setBackgroundColor(TextColor backgroundColor) {
        backend.setBackgroundColor(backgroundColor);
        return this;
    }

    @Override
    public TextColor getForegroundColor() {
        return backend.getForegroundColor();
    }

    @Override
    public TextGUIGraphics setForegroundColor(TextColor foregroundColor) {
        backend.setForegroundColor(foregroundColor);
        return this;
    }

    @Override
    public TextGUIGraphics enableModifiers(SGR... modifiers) {
        backend.enableModifiers(modifiers);
        return this;
    }

    @Override
    public TextGUIGraphics disableModifiers(SGR... modifiers) {
        backend.disableModifiers(modifiers);
        return this;
    }

    @Override
    public TextGUIGraphics setModifiers(EnumSet<SGR> modifiers) {
        backend.setModifiers(modifiers);
        return this;
    }

    @Override
    public TextGUIGraphics clearModifiers() {
        backend.clearModifiers();
        return this;
    }

    @Override
    public EnumSet<SGR> getActiveModifiers() {
        return backend.getActiveModifiers();
    }

    @Override
    public TabBehaviour getTabBehaviour() {
        return backend.getTabBehaviour();
    }

    @Override
    public TextGUIGraphics setTabBehaviour(TabBehaviour tabBehaviour) {
        backend.setTabBehaviour(tabBehaviour);
        return this;
    }

    @Override
    public TextGUIGraphics fill(char c) {
        backend.fill(c);
        return this;
    }

    @Override
    public TextGraphics fillRectangle(TerminalPosition topLeft, TerminalSize size, char character) {
        backend.fillRectangle(topLeft, size, character);
        return this;
    }

    @Override
    public TextGraphics fillRectangle(TerminalPosition topLeft, TerminalSize size, TextCharacter character) {
        backend.fillRectangle(topLeft, size, character);
        return this;
    }

    @Override
    public TextGraphics drawRectangle(TerminalPosition topLeft, TerminalSize size, char character) {
        backend.drawRectangle(topLeft, size, character);
        return this;
    }

    @Override
    public TextGraphics drawRectangle(TerminalPosition topLeft, TerminalSize size, TextCharacter character) {
        backend.drawRectangle(topLeft, size, character);
        return this;
    }

    @Override
    public TextGraphics fillTriangle(TerminalPosition p1, TerminalPosition p2, TerminalPosition p3, char character) {
        backend.fillTriangle(p1, p2, p3, character);
        return this;
    }

    @Override
    public TextGraphics fillTriangle(TerminalPosition p1, TerminalPosition p2, TerminalPosition p3, TextCharacter character) {
        backend.fillTriangle(p1, p2, p3, character);
        return this;
    }

    @Override
    public TextGraphics drawTriangle(TerminalPosition p1, TerminalPosition p2, TerminalPosition p3, char character) {
        backend.drawTriangle(p1, p2, p3, character);
        return this;
    }

    @Override
    public TextGraphics drawTriangle(TerminalPosition p1, TerminalPosition p2, TerminalPosition p3, TextCharacter character) {
        backend.drawTriangle(p1, p2, p3, character);
        return this;
    }

    @Override
    public TextGraphics drawLine(TerminalPosition fromPoint, TerminalPosition toPoint, char character) {
        backend.drawLine(fromPoint, toPoint, character);
        return this;
    }

    @Override
    public TextGraphics drawLine(TerminalPosition fromPoint, TerminalPosition toPoint, TextCharacter character) {
        backend.drawLine(fromPoint, toPoint, character);
        return this;
    }

    @Override
    public TextGraphics drawLine(int fromX, int fromY, int toX, int toY, char character) {
        backend.drawLine(fromX, fromY, toX, toY, character);
        return this;
    }

    @Override
    public TextGraphics drawLine(int fromX, int fromY, int toX, int toY, TextCharacter character) {
        backend.drawLine(fromX, fromY, toX, toY, character);
        return this;
    }

    @Override
    public TextGraphics drawImage(TerminalPosition topLeft, TextImage image) {
        backend.drawImage(topLeft, image);
        return this;
    }

    @Override
    public TextGraphics drawImage(TerminalPosition topLeft, TextImage image, TerminalPosition sourceImageTopLeft, TerminalSize sourceImageSize) {
        backend.drawImage(topLeft, image, sourceImageTopLeft, sourceImageSize);
        return this;
    }

    @Override
    public TextGraphics setCharacter(TerminalPosition position, char character) {
        backend.setCharacter(position, character);
        return this;
    }

    @Override
    public TextGraphics setCharacter(TerminalPosition position, TextCharacter character) {
        backend.setCharacter(position, character);
        return this;
    }

    @Override
    public TextGraphics setCharacter(int column, int row, char character) {
        backend.setCharacter(column, row, character);
        return this;
    }

    @Override
    public TextGraphics setCharacter(int column, int row, TextCharacter character) {
        backend.setCharacter(column, row, character);
        return this;
    }

    @Override
    public TextGUIGraphics putString(int column, int row, String string) {
        backend.putString(column, row, string);
        return this;
    }

    @Override
    public TextGUIGraphics putString(TerminalPosition position, String string) {
        backend.putString(position, string);
        return this;
    }

    @Override
    public TextGUIGraphics putString(int column, int row, String string, SGR extraModifier, SGR... optionalExtraModifiers) {
        backend.putString(column, row, string, extraModifier, optionalExtraModifiers);
        return this;
    }

    @Override
    public TextGUIGraphics putString(TerminalPosition position, String string, SGR extraModifier, SGR... optionalExtraModifiers) {
        backend.putString(position, string, extraModifier, optionalExtraModifiers);
        return this;
    }

    @Override
    public TextGraphics putString(int column, int row, String string, Collection<SGR> extraModifiers) {
        backend.putString(column, row, string, extraModifiers);
        return this;
    }

    @Override
    public TextCharacter getCharacter(int column, int row) {
        return backend.getCharacter(column, row);
    }

    @Override
    public TextCharacter getCharacter(TerminalPosition position) {
        return backend.getCharacter(position);
    }
}
