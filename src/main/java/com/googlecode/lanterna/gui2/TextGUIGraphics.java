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
 * Copyright (C) 2010-2017 Martin Berglund
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
public interface TextGUIGraphics extends ThemedTextGraphics, TextGraphics {
    /**
     * Returns the {@code TextGUI} this {@code TextGUIGraphics} belongs to
     * @return {@code TextGUI} this {@code TextGUIGraphics} belongs to
     */
    TextGUI getTextGUI();

    @Override
    TextGUIGraphics newTextGraphics(TerminalPosition topLeftCorner, TerminalSize size) throws IllegalArgumentException;

    @Override
    TextGUIGraphics applyThemeStyle(ThemeStyle themeStyle);

    @Override
    TextGUIGraphics setBackgroundColor(TextColor backgroundColor);

    @Override
    TextGUIGraphics setForegroundColor(TextColor foregroundColor);

    @Override
    TextGUIGraphics enableModifiers(SGR... modifiers);

    @Override
    TextGUIGraphics disableModifiers(SGR... modifiers);

    @Override
    TextGUIGraphics setModifiers(EnumSet<SGR> modifiers);

    @Override
    TextGUIGraphics clearModifiers();

    @Override
    TextGUIGraphics setTabBehaviour(TabBehaviour tabBehaviour);

    @Override
    TextGUIGraphics fill(char c);

    @Override
    TextGUIGraphics fillRectangle(TerminalPosition topLeft, TerminalSize size, char character);

    @Override
    TextGUIGraphics fillRectangle(TerminalPosition topLeft, TerminalSize size, TextCharacter character);

    @Override
    TextGUIGraphics drawRectangle(TerminalPosition topLeft, TerminalSize size, char character);

    @Override
    TextGUIGraphics drawRectangle(TerminalPosition topLeft, TerminalSize size, TextCharacter character);

    @Override
    TextGUIGraphics fillTriangle(TerminalPosition p1, TerminalPosition p2, TerminalPosition p3, char character);

    @Override
    TextGUIGraphics fillTriangle(TerminalPosition p1, TerminalPosition p2, TerminalPosition p3, TextCharacter character);

    @Override
    TextGUIGraphics drawTriangle(TerminalPosition p1, TerminalPosition p2, TerminalPosition p3, char character);

    @Override
    TextGUIGraphics drawTriangle(TerminalPosition p1, TerminalPosition p2, TerminalPosition p3, TextCharacter character);

    @Override
    TextGUIGraphics drawLine(TerminalPosition fromPoint, TerminalPosition toPoint, char character);

    @Override
    TextGUIGraphics drawLine(TerminalPosition fromPoint, TerminalPosition toPoint, TextCharacter character);

    @Override
    TextGUIGraphics drawLine(int fromX, int fromY, int toX, int toY, char character);

    @Override
    TextGUIGraphics drawLine(int fromX, int fromY, int toX, int toY, TextCharacter character);

    @Override
    TextGUIGraphics drawImage(TerminalPosition topLeft, TextImage image);

    @Override
    TextGUIGraphics drawImage(TerminalPosition topLeft, TextImage image, TerminalPosition sourceImageTopLeft, TerminalSize sourceImageSize);

    @Override
    TextGUIGraphics setCharacter(TerminalPosition position, char character);

    @Override
    TextGUIGraphics setCharacter(TerminalPosition position, TextCharacter character);

    @Override
    TextGUIGraphics setCharacter(int column, int row, char character);

    @Override
    TextGUIGraphics setCharacter(int column, int row, TextCharacter character);

    @Override
    TextGUIGraphics putString(int column, int row, String string);

    @Override
    TextGUIGraphics putString(TerminalPosition position, String string);

    @Override
    TextGUIGraphics putString(int column, int row, String string, SGR extraModifier, SGR... optionalExtraModifiers);

    @Override
    TextGUIGraphics putString(TerminalPosition position, String string, SGR extraModifier, SGR... optionalExtraModifiers);

    @Override
    TextGUIGraphics putString(int column, int row, String string, Collection<SGR> extraModifiers);

    @Override
    TextGUIGraphics putCSIStyledString(int column, int row, String string);

    @Override
    TextGUIGraphics putCSIStyledString(TerminalPosition position, String string);

    @Override
    TextGUIGraphics setStyleFrom(StyleSet<?> source);

}
