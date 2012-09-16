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

/**
 * Containers containing interactable components must implement this interface
 * so that the GUI system knows how to switch between the different components.
 * @author Martin
 */
public interface InteractableContainer
{
    /**
     * Returns true if this container contains the {@code interactable} passed in as the parameter
     * @param interactable {@code interactable} to look for
     * @return {@code true} if the container has {@code interactable}, otherwise {@code false}
     */
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
    
    /**
     * Adds a keyboard shortcut to be invoked when the {@code Interactable} component in focus 
     * within this container didn't handle the keyboard event and the event matches the supplied
     * {@code Key.Kind}. Please note that calling {@code addShortcut} with 
     * {@code Key.Kind.NormalKey} will throw {@code IllegalArgumentException}; if you want to add
     * a keyboard shortcut for a non-special key, please use 
     * {@code addShortcut(char, boolean, boolean, Action)}.
     * 
     * @param key Kind of key to trigger the shortcut
     * @param action Action to run, on the event thread, when the shortcut is triggered
     */
    void addShortcut(Key.Kind key, Action action);
    
    /**
     * Adds a keyboard shortcut to be invoked when the {@code Interactable} component in focus 
     * within this container didn't handle the keyboard event and the event matches the supplied
     * character and control key status. If you want to add a keyboard shortcut for a special keys, 
     * please use {@code addShortcut(Key.Kind, Action)}.
     * 
     * @param character Character types on the keyboard to trigger the shortcut
     * @param withCtrl If {@code true}, ctrl key must be down when the key is typed to trigger; 
     * if {@code false} the ctrl key must be up to trigger
     * @param withAlt  If {@code true}, alt key must be down when the key is typed to trigger; 
     * if {@code false} the alt key must be up to trigger
     * @param action Action to run, on the event thread, when the shortcut is triggered
     */
    void addShortcut(char character, boolean withCtrl, boolean withAlt, Action action);
    
    /**
     * Looks for a shortcut that matches this {@code key} and, if one is found, executes it. 
     * @param key {@code Key} to check for matching shortcuts
     * @return {@code true} if a shortcut was triggered and executed, {@code false} otherwise.
     */
    boolean triggerShortcut(Key key);
}
