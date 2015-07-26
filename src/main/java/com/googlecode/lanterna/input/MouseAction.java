package com.googlecode.lanterna.input;

import com.googlecode.lanterna.TerminalPosition;

/**
 * MouseAction, a KeyStroke in disguise, this class contains the information of a single mouse action event.
 */
public class MouseAction extends KeyStroke {
    private final MouseActionType actionType;
    private final int button;
    private final TerminalPosition position;

    /**
     * Constructs a MouseAction based on an action type, a button and a location on the screen
     * @param actionType The kind of mouse event
     * @param button Which button is involved (no button = 0, left button = 1, middle (wheel) button = 2,
     *               right button = 3, scroll wheel up = 4, scroll wheel down = 5)
     * @param position Where in the terminal is the mouse cursor located
     */
    public MouseAction(MouseActionType actionType, int button, TerminalPosition position) {
        super(KeyType.MouseEvent, false, false);
        this.actionType = actionType;
        this.button = button;
        this.position = position;
    }

    /**
     * Returns the mouse action type so the caller can determine which kind of action was performed.
     * @return The action type of the mouse event
     */
    public MouseActionType getActionType() {
        return actionType;
    }

    /**
     * Which button was involved in this event. Please note that for CLICK_RELEASE events, there is no button
     * information available (getButton() will return 0). The standard xterm mapping is:
     * <ul>
     *     <li>No button = 0</li>
     *     <li>Left button = 1</li>
     *     <li>Middle (wheel) button = 2</li>
     *     <li>Right button = 3</li>
     *     <li>Wheel up = 4</li>
     *     <li>Wheel down = 5</li>
     * </ul>
     * @return The button which is clicked down when this event was generated
     */
    public int getButton() {
        return button;
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
        return "MouseAction{actionType=" + actionType + ", button=" + button + ", position=" + position + '}';
    }
}
