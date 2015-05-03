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
 * Copyright (C) 2010-2015 Martin
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.input.KeyStroke;
import java.util.Collection;

/**
 * Container is a component that contains a collection of child components. The basic example of an implementation of 
 * this is the {@code Panel} class which uses a layout manager to size and position the children over its area.
 * @author Martin
 */
public interface Container extends Component {
    /**
     * Returns collection that is to be considered a copy of the list of children contained inside of this object. 
     * Modifying this list will not affect any internal state.
     * @return Child-components inside of this Container
     */
    Collection<Component> getChildren();
    
    /**
     * Given an interactable, find the next one in line to receive focus. If the interactable isn't inside this 
     * container, this method should return {@code null}.
     *
     * @param fromThis Component from which to get the next interactable, or if
     *                 null, pick the first available interactable
     * @return The next interactable component, or null if there are no more
     * interactables in the list
     */
    Interactable nextFocus(Interactable fromThis);

    /**
     * Given an interactable, find the previous one in line to receive focus. If the interactable isn't inside this 
     * container, this method should return {@code null}.
     *
     * @param fromThis Component from which to get the previous interactable,
     *                 or if null, pick the last interactable in the list
     * @return The previous interactable component, or null if there are no more
     * interactables in the list
     */
    Interactable previousFocus(Interactable fromThis);
    
    /**
     * If an interactable component inside this container received a keyboard event that wasn't handled, the GUI system
     * will recursively send the event to each parent container to give each of them a chance to consume the event. 
     * Return {@code false} if the implementer doesn't care about this particular keystroke and it will be automatically
     * sent up the hierarchy the to next container. If you return {@code true}, the event will stop here and won't be 
     * reported as unhandled.
     * @param key Keystroke that was ignored by the interactable inside this container
     * @return {@code true} if this event was handled by this container and shouldn't be processed anymore, 
     * {@code false} if the container didn't take any action on the event and want to pass it on
     */
    boolean handleInput(KeyStroke key);
    
    /**
     * Takes a lookup map and updates it with information about where all the interactables inside of this container
     * are located.
     * @param interactableLookupMap Interactable map to update
     */
    void updateLookupMap(InteractableLookupMap interactableLookupMap);
}
