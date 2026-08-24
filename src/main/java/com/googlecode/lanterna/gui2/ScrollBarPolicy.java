package com.googlecode.lanterna.gui2;

public enum ScrollBarPolicy {
    /**
     * No scrollbar is ever shown, and scrolling is not allowed in this dimension.
     */
    DISABLED,

    /**
     * The scrollbar is shown only when the content exceeds the size of hte viewport
     * in this dimension.
     */
    AUTO,

    /**
     * No scrollbar is ever shown, and no space will be reserved for it, though
     * scrolling may still occur by other means.
     */
    HIDDEN,

    /**
     * The scrollbar will always be visible, and space always reserved for it.
     */
    VISIBLE
}
