package com.googlecode.lanterna.gui2.table;

import com.googlecode.lanterna.TerminalTextUtils;
import com.googlecode.lanterna.TerminalSize;
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
        textGUIGraphics.applyThemeStyle(textGUIGraphics.getThemeDefinition(Table.class).getCustom("HEADER"));
        textGUIGraphics.putString(0, 0, label);
    }
}
