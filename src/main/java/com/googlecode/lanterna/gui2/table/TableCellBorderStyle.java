package com.googlecode.lanterna.gui2.table;

/**
 * Describing how table cells are separated when drawn
 */
public enum TableCellBorderStyle {
    /**
     * There is no separation between table cells, they are drawn immediately next to each other
     */
    None,
    /**
     * There is a single space of separation between the cells, drawn as a single line
     */
    SingleLine,
    /**
     * There is a single space of separation between the cells, drawn as a double line
     */
    DoubleLine,
    /**
     * There is a single space of separation between the cells, kept empty
     */
    EmptySpace,
}
