package com.googlecode.lanterna.gui2.table;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.TextGUIGraphics;

/**
 * Created by martin on 23/08/15.
 */
public interface TableCellRenderer<V> {
    TerminalSize getPreferredSize(Table<V> table, V cell, int columnIndex, int rowIndex);

    void drawCell(Table<V> table, V cell, int columnIndex, int rowIndex, TextGUIGraphics textGUIGraphics);
}
