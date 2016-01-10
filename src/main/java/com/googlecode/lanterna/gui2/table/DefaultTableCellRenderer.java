package com.googlecode.lanterna.gui2.table;

import com.googlecode.lanterna.TerminalTextUtils;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.ThemeDefinition;
import com.googlecode.lanterna.gui2.TextGUIGraphics;

/**
 * Default implementation of {@code TableCellRenderer}
 * @param <V> Type of data stored in each table cell
 * @author Martin
 */
public class DefaultTableCellRenderer<V> implements TableCellRenderer<V> {
    @Override
    public TerminalSize getPreferredSize(Table<V> table, V cell, int columnIndex, int rowIndex) {
        String[] lines = getContent(cell);
        int maxWidth = 0;
        for(String line: lines) {
            int length = TerminalTextUtils.getColumnWidth(line);
            if(maxWidth < length) {
                maxWidth = length;
            }
        }
        return new TerminalSize(maxWidth, lines.length);
    }

    @Override
    public void drawCell(Table<V> table, V cell, int columnIndex, int rowIndex, TextGUIGraphics textGUIGraphics) {
        ThemeDefinition themeDefinition = textGUIGraphics.getThemeDefinition(Table.class);
        if((table.getSelectedColumn() == columnIndex && table.getSelectedRow() == rowIndex) ||
                (table.getSelectedRow() == rowIndex && !table.isCellSelection())) {
            if(table.isFocused()) {
                textGUIGraphics.applyThemeStyle(themeDefinition.getActive());
            }
            else {
                textGUIGraphics.applyThemeStyle(themeDefinition.getSelected());
            }
            textGUIGraphics.fill(' ');  //Make sure to fill the whole cell first
        }
        else {
            textGUIGraphics.applyThemeStyle(themeDefinition.getNormal());
        }
        String[] lines = getContent(cell);
        int rowCount = 0;
        for(String line: lines) {
            textGUIGraphics.putString(0, rowCount++, line);
        }
    }

    private String[] getContent(V cell) {
        String[] lines;
        if(cell == null) {
            lines = new String[] { "" };
        }
        else {
            lines = cell.toString().split("\r?\n");
        }
        return lines;
    }
}
