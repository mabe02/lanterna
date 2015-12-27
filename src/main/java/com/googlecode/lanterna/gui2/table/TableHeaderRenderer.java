package com.googlecode.lanterna.gui2.table;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.TextGUIGraphics;

/**
 * This interface can be implemented if you want to customize how table headers are drawn.
 * @param <V> Type of data stored in each table cell
 * @author Martin
 */
public interface TableHeaderRenderer<V> {
    /**
     * Called by the table when it wants to know how big a particular table header should be
     * @param table Table containing the header
     * @param label Label for this header
     * @param columnIndex Column index of the header
     * @return Size this renderer would like the header to have
     */
    TerminalSize getPreferredSize(Table<V> table, String label, int columnIndex);

    /**
     * Called by the table when it's time to draw a header, you can see how much size is available by checking the size
     * of the {@code textGUIGraphics}. The top-left position of the graphics object is the top-left position of this
     * header.
     * @param table Table containing the header
     * @param label Label for this header
     * @param index Column index of the header
     * @param textGUIGraphics Graphics object to header with
     */
    void drawHeader(Table<V> table, String label, int index, TextGUIGraphics textGUIGraphics);
}
