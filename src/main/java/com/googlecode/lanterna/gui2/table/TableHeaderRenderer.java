package com.googlecode.lanterna.gui2.table;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.TextGUIGraphics;

/**
 * Created by martin on 23/08/15.
 */
public interface TableHeaderRenderer<V> {
    TerminalSize getPreferredSize(Table<V> table, String label, int columnIndex);

    void drawHeader(Table<V> table, String label, int index, TextGUIGraphics textGUIGraphics);
}
