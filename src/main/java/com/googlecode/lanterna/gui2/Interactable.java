package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.input.KeyStroke;

/**
 * This interface marks a component as able to receive keyboard input from the user.
 * @author Martin
 */
public interface Interactable extends Component {
    /**
     * Returns, in global coordinates, where to put the cursor on the screen when this component has focus. If null, the
     * cursor should be hidden.
     * @return Coordinates of where to place the cursor when this component has focus
     */
    TerminalPosition getCursorLocation();

    /**
     * Accepts a KeyStroke as input and processes this as a user input. Depending on what the component does with this
     * key-stroke, there are several results passed back to the GUI system that will decide what to do next. If the
     * event was not handled or ignored, {@code Result.UNHANDLED} should be returned. This will tell the GUI system that
     * the key stroke was not understood by this component and may be dealt with in another way. If event was processed
     * properly, it should return {@code Result.HANDLED}, which will make the GUI system stop processing this particular
     * key-stroke. Furthermore, if the component understood the key-stroke and would like to move focus to a different
     * component, there are the {@code Result.MOVE_FOCUS_*} values.
     * @param keyStroke What input was entered by the user
     * @return Result of processing the key-stroke
     */
    Result handleKeyStroke(KeyStroke keyStroke);

    /**
     * Enum to represent the various results coming out of the handleKeyStroke method
     */
    public static enum Result {
        /**
         * This component didn't handle the key-stroke, either because it was not recognized or because it chose to
         * ignore it.
         */
        UNHANDLED,
        /**
         * This component has handled the key-stroke and it should be considered consumed.
         */
        HANDLED,
        /**
         * This component has handled the key-stroke and requests the GUI system to switch focus to next component in
         * an ordered list of components. This should generally be returned if moving focus by using the tab key.
         */
        MOVE_FOCUS_NEXT,
        /**
         * This component has handled the key-stroke and requests the GUI system to switch focus to previous component
         * in an ordered list of components. This should generally be returned if moving focus by using the reverse tab
         * key.
         */
        MOVE_FOCUS_PREVIOUS,
        /**
         * This component has handled the key-stroke and requests the GUI system to switch focus to next component in
         * the general left direction. By convention in Lanterna, if there is no component to the left, it will move up
         * instead. This should generally be returned if moving focus by using the left array key.
         */
        MOVE_FOCUS_LEFT,
        /**
         * This component has handled the key-stroke and requests the GUI system to switch focus to next component in
         * the general right direction. By convention in Lanterna, if there is no component to the right, it will move
         * down instead. This should generally be returned if moving focus by using the right array key.
         */
        MOVE_FOCUS_RIGHT,
        /**
         * This component has handled the key-stroke and requests the GUI system to switch focus to next component in
         * the general up direction. By convention in Lanterna, if there is no component above, it will move left
         * instead. This should generally be returned if moving focus by using the up array key.
         */
        MOVE_FOCUS_UP,
        /**
         * This component has handled the key-stroke and requests the GUI system to switch focus to next component in
         * the general down direction. By convention in Lanterna, if there is no component below, it will move up
         * instead. This should generally be returned if moving focus by using the down array key.
         */
        MOVE_FOCUS_DOWN,
        ;
    }
}
