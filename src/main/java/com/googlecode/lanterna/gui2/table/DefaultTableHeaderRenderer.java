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
package com.googlecode.lanterna.gui2.table;

import com.googlecode.lanterna.TerminalTextUtils;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.ThemeDefinition;
import com.googlecode.lanterna.gui2.TextGUIGraphics;

/**
 * Default implementation of {@code TableHeaderRenderer}
 * @author Martin
 */
public class DefaultTableHeaderRenderer<V> implements TableHeaderRenderer<V> {
    @Override
    public TerminalSize getPreferredSize(Table<V> table, String label, int columnIndex) {
        if(label == null) {
            return TerminalSize.ZERO;
        }
        return new TerminalSize(TerminalTextUtils.getColumnWidth(label), 1);
    }

    @Override
    public void drawHeader(Table<V> table, String label, int index, TextGUIGraphics textGUIGraphics) {
        ThemeDefinition themeDefinition = table.getThemeDefinition();
        textGUIGraphics.applyThemeStyle(themeDefinition.getCustom("HEADER", themeDefinition.getNormal()));
        textGUIGraphics.putString(0, 0, label);
    }
}
