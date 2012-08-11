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

/**
 * Containers containing interactable components must implement this interface
 * so that the GUI system knows how to switch between the different components.
 * @author Martin
 */
public interface InteractableContainer
{
    boolean hasInteractable(Interactable interactable);
    
    /**
     * Given an interactable, find the next one in line to receive focus.
     * @param fromThis Component from which to get the next interactable, or if 
     * null, pick the first available interactable
     * @return The next interactable component, or null if there are no more 
     * interactables in the list
     */
    Interactable nextFocus(Interactable fromThis);
    
    /**
     * Given an interactable, find the previous one in line to receive focus.
     * @param fromThis Component from which to get the previous interactable, 
     * or if null, pick the last interactable in the list
     * @return The previous interactable component, or null if there are no more 
     * interactables in the list
     */
    Interactable previousFocus(Interactable fromThis);
}
