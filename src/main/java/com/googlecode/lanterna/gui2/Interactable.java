/*
 * This file is part of lanterna (http://code.google.com/p/lanterna/).
 * 
 * lanterna is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright (C) 2010-2017 Martin Berglund
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.input.KeyStroke;

/**
 * This interface marks a component as able to receive keyboard input from the user. Components that do not implement
 * this interface in some way will not be able to receive input focus. Normally if you create a new component, you'll
 * probably want to extend from {@code AbstractInteractableComponent} instead of implementing this one directly.
 *
 * @see AbstractInteractableComponent
 * @author Martin
 */
public interface Interactable extends Component {
    /**
     * Returns, in local coordinates, where to put the cursor on the screen when this component has focus. If null, the
     * cursor should be hidden. If you component is 5x1 and you want to have the cursor in the middle (when in focus),
     * return [2,0]. The GUI system will convert the position to global coordinates.
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
     * component, there are the {@code Result.MOVE_FOCUS_*} values. This method should be invoking the input filter, if
     * it is set, to see if the input should be processed or not.
     * <p>
     * Notice that most of the built-in components in Lanterna extends from {@link AbstractInteractableComponent} which
     * has a final implementation of this method. The method to override to handle input in that case is
     * {@link AbstractInteractableComponent#handleKeyStroke(KeyStroke)}.
     * @param keyStroke What input was entered by the user
     * @return Result of processing the key-stroke
     */
    Result handleInput(KeyStroke keyStroke);

    /**
     * Moves focus in the {@code BasePane} to this component. If the component has not been added to a {@code BasePane}
     * (i.e. a {@code Window} most of the time), does nothing. If the component has been disabled through a call to
     * {@link Interactable#setEnabled(boolean)}, this call also does nothing.
     * @return Itself
     */
    Interactable takeFocus();

    /**
     * Method called when this component gained keyboard focus.
     * @param direction What direction did the focus come from
     * @param previouslyInFocus Which component had focus previously ({@code null} if none)
     */
    void onEnterFocus(FocusChangeDirection direction, Interactable previouslyInFocus);

    /**
     * Method called when keyboard focus moves away from this component
     * @param direction What direction is focus going in
     * @param nextInFocus Which component is receiving focus next (or {@code null} if none)
     */
    void onLeaveFocus(FocusChangeDirection direction, Interactable nextInFocus);

    /**
     * Returns {@code true} if this component currently has input focus in its root container.
     * @return {@code true} if the interactable has input focus, {@code false} otherwise
     */
    boolean isFocused();

    /**
     * Assigns an input filter to the interactable component. This will intercept any user input and decide if the input
     * should be passed on to the component or not. {@code null} means there is no filter.
     * @param inputFilter Input filter to assign to the interactable
     * @return Itself
     */
    Interactable setInputFilter(InputFilter inputFilter);

    /**
     * Returns the input filter currently assigned to the interactable component. This will intercept any user input and
     * decide if the input should be passed on to the component or not. {@code null} means there is no filter.
     * @return Input filter currently assigned to the interactable component
     */
    InputFilter getInputFilter();

    /**
     * Prevents the component from receiving input focus if this is called with a {@code false} value. The component
     * will then behave as a mainly non-interactable component. Input focus can be re-enabled by calling this with
     * {@code true}. If the component already has input focus when calling this method, it will release focus and no
     * component is focused until there is user action or code that chooses a new focus.
     * @param enabled If called with {@code false}, this interactable won't receive input focus until it's called again
     *                with {@code true}.
     * @return Itself
     */
    Interactable setEnabled(boolean enabled);

    /**
     * Returns {@code true} if this component is able to receive input as a regular interactable component. This will
     * return {@code false} if input focus has been disabled through calling {@link Interactable#setEnabled(boolean)}.
     * @return {@code true} if this component can receive input focus, {@code false} otherwise
     */
    boolean isEnabled();

    /**
     * Returns {@code true} if this interactable component is currently able to receive input focus. This is similar but
     * different from {@link #isEnabled()}, which tells lanterna that the entire component is disabled when it is
     * returning {@code false}, compared to this method which simply claims that the component is currently not ready
     * to handle input. The {@link AbstractInteractableComponent} implementation always return {@code true} here but
     * for example the list box components will override and return {@code false} here if they are empty. Note that you
     * can still programmatically force input focus onto the component, returning {@code false} here won't prevent that.
     *
     * @return {@code true} if this component wants to receive input focus, {@code false} otherwise.
     */
    boolean isFocusable();

    /**
     * Enum to represent the various results coming out of the handleKeyStroke method
     */
    enum Result {
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

    /**
     * When focus has changed, which direction.
     */
    enum FocusChangeDirection {
        /**
         * The next interactable component, going down. This direction usually comes from the user pressing down array.
         */
        DOWN,
        /**
         * The next interactable component, going right. This direction usually comes from the user pressing right array.
         */
        RIGHT,
        /**
         * The next interactable component, going up. This direction usually comes from the user pressing up array.
         */
        UP,
        /**
         * The next interactable component, going left. This direction usually comes from the user pressing left array.
         */
        LEFT,
        /**
         * The next interactable component, in layout manager order (usually left-&gt;right, up-&gt;down). This direction
         * usually comes from the user pressing tab key.
         */
        NEXT,
        /**
         * The previous interactable component, reversed layout manager order (usually right-&gt;left, down-&gt;up). This
         * direction usually comes from the user pressing shift and tab key (reverse tab).
         */
        PREVIOUS,
        /**
         * Focus was changed by calling the {@code RootContainer.setFocusedInteractable(..)} method directly.
         */
        TELEPORT,
        /**
         * Focus has gone away and no component is now in focus
         */
        RESET,
        ;
    }
}
