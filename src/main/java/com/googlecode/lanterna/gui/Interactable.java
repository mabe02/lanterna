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
 * Copyright (C) 2010-2012 Martin
 */

package com.googlecode.lanterna.gui;

import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.TerminalPosition;

/**
 * Any component which wants to receive keyboard events must implement this
 * interface.
 * @author Martin
 */
public interface Interactable
{
    /**
     * This method is called when this component has focus and the user has
     * pressed a key on the keyboard.
     * @param key Key pressed on the keyboard
     * @return Result of this keyboard interaction, this can for example be a hint to the 
     * parent container to move input focus to a different component or telling that the event was
     * processed by the component and no extra action is required
     */
    public Result keyboardInteraction(Key key);
    
    /**
     * Method called when this component gained keyboard focus.
     * @param direction What direction did the focus come from
     */
    public void onEnterFocus(FocusChangeDirection direction);
    /**
     * Method called when this component leaves keyboard focus.
     * @param direction What direction is focus going to
     */
    public void onLeaveFocus(FocusChangeDirection direction);
    
    /**
     * When the component has keyboard focus, this method is called by the GUI
     * system to figure out where should the cursor be positioned.
     * @return position of the cursor, in global coordinates, or {@code null} if the cursor should
     * be hidden
     */
    public TerminalPosition getHotspot();

    /**
     * The available results from a keyboard interaction
     */
    public enum Result
    {
        /**
         * The event has been dealt with by this component, shouldn't be sent to the parent.
         */
        EVENT_HANDLED,
        /**
         * The component didn't have any action for this event, send it to the parent.
         */
        EVENT_NOT_HANDLED,
        /**
         * Move keyboard focus to the next component, going down.
         */
        NEXT_INTERACTABLE_DOWN,
        /**
         * Move keyboard focus to the next component, going right.
         */
        NEXT_INTERACTABLE_RIGHT,
        /**
         * Move keyboard focus to the previous component, going up.
         */
        PREVIOUS_INTERACTABLE_UP,
        /**
         * Move keyboard focus to the previous component, going left.
         */
        PREVIOUS_INTERACTABLE_LEFT,
        ;
        
        boolean isNextInteractable() {
            return this == NEXT_INTERACTABLE_DOWN || this == NEXT_INTERACTABLE_RIGHT;
        }
        
        boolean isPreviousInteractable() {
            return this == PREVIOUS_INTERACTABLE_LEFT || this == PREVIOUS_INTERACTABLE_UP;
        }

        FocusChangeDirection asFocusChangeDirection() {
            if(this == NEXT_INTERACTABLE_DOWN)
                return FocusChangeDirection.DOWN;
            if(this == NEXT_INTERACTABLE_RIGHT)
                return FocusChangeDirection.RIGHT;
            if(this == PREVIOUS_INTERACTABLE_LEFT)
                return FocusChangeDirection.LEFT;
            if(this == PREVIOUS_INTERACTABLE_UP)
                return FocusChangeDirection.UP;
            return null;
        }
    }

    /**
     * When focus has changed, which direction.
     */
    public enum FocusChangeDirection
    {
        /**
         * The next interactable component, going down.
         */
        DOWN,
        /**
         * The next interactable component, going right.
         */
        RIGHT,
        /**
         * The previous interactable component, going up.
         */
        UP,
        /**
         * The previous interactable component, going left.
         */
        LEFT,
        
    }
}
