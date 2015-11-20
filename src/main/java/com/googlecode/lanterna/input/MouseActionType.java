package com.googlecode.lanterna.input;

/**
 * Enum type for the different kinds of mouse actions supported
 */
public enum MouseActionType {
    CLICK_DOWN,
    CLICK_RELEASE,
    SCROLL_UP,
    SCROLL_DOWN,
    /**
     * Moving the mouse cursor on the screen while holding a button down
     */
    DRAG,
    /**
     * Moving the mouse cursor on the screen without holding any buttons down
     */
    MOVE,
    ;
}
