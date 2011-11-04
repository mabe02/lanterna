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
 * Copyright (C) 2010-2011 mabe02
 */

package org.lantern.gui;

import org.lantern.LanternException;
import org.lantern.input.Key;
import org.lantern.terminal.TerminalPosition;

/**
 *
 * @author mabe02
 */
public interface Interactable
{
    public void keyboardInteraction(Key key, InteractableResult result) throws LanternException;
    public void onEnterFocus(FocusChangeDirection direction);
    public void onLeaveFocus(FocusChangeDirection direction);
    public TerminalPosition getHotspot();

    public static class Result
    {
        public static final int DO_NOTHING_ID = 1;
        public static final int NEXT_INTERACTABLE_ID = 2;
        public static final int PREVIOUS_INTERACTABLE_ID = 3;
        
        public static final Result DO_NOTHING = new Result(DO_NOTHING_ID);
        public static final Result NEXT_INTERACTABLE = new Result(NEXT_INTERACTABLE_ID);
        public static final Result PREVIOUS_INTERACTABLE = new Result(PREVIOUS_INTERACTABLE_ID);
        
        private final int index;

        private Result(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }

    public static class FocusChangeDirection
    {
        public static final int DOWN_OR_RIGHT_ID = 1;
        public static final int UP_OR_LEFT_ID = 2;
        
        public static final FocusChangeDirection DOWN_OR_RIGHT = new FocusChangeDirection(DOWN_OR_RIGHT_ID);
        public static final FocusChangeDirection UP_OR_LEFT = new FocusChangeDirection(UP_OR_LEFT_ID);
        
        private final int index;

        private FocusChangeDirection(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }
}
