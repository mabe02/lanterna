package com.googlecode.lanterna.gui2.table;

import com.googlecode.lanterna.CJKUtils;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.TextGUIGraphics;

/**
 * Created by martin on 23/08/15.
 */
public class DefaultTableHeaderRenderer<V> implements TableHeaderRenderer<V> {
    @Override
    public TerminalSize getPreferredSize(Table<V> table, String label, int columnIndex) {
        if(label == null) {
            return TerminalSize.ZERO;
        }
        return new TerminalSize(CJKUtils.getColumnWidth(label), 1);
    }

    @Override
    public void drawHeader(Table<V> table, String label, int index, TextGUIGraphics textGUIGraphics) {
        textGUIGraphics.applyThemeStyle(textGUIGraphics.getThemeDefinition(Table.class).getCustom("HEADER"));
        textGUIGraphics.putString(0, 0, label);
    }
}
