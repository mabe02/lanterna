package com.googlecode.lanterna.gui2.table;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.InteractableRenderer;
import com.googlecode.lanterna.gui2.TextGUIGraphics;

/**
 * Created by martin on 23/08/15.
 */
public interface TableRenderer<V> extends InteractableRenderer<Table<V>> {
    @Override
    void drawComponent(TextGUIGraphics graphics, Table<V> component);

    @Override
    TerminalSize getPreferredSize(Table<V> component);
}
