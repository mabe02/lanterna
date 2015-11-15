package com.googlecode.lanterna.input;

import com.googlecode.lanterna.TerminalPosition;

/**
 * ScreenInfoAction, a KeyStroke in disguise, this class contains the reported position of the screen cursor.
 */
public class ScreenInfoAction extends KeyStroke {
    private final TerminalPosition position;

    /**
     * Constructs a ScreenInfoAction based on a location on the screen
     * @param position the TerminalPosition reported from terminal
     */
    public ScreenInfoAction(TerminalPosition position) {
        super(KeyType.CursorLocation);
        this.position = position;
    }

    /**
     * The location of the mouse cursor when this event was generated.
     * @return Location of the mouse cursor
     */
    public TerminalPosition getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "ScreenInfoAction{position=" + position + '}';
    }
}
