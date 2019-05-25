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
 * Copyright (C) 2010-2019 Martin Berglund
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.input.KeyStroke;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Base listener interface having callback methods for events relating to {@link BasePane} (and {@link Window}, which
 * extends {@link BasePane}) so that you can be notified by a callback when certain events happen. Assume it is the GUI
 * thread that will call these methods. You typically use this through {@link WindowListener} and calling
 * {@link Window#addWindowListener(WindowListener)}
 */
public interface BasePaneListener<T extends BasePane> {
    /**
     * Called when a user input is about to be delivered to the focused {@link Interactable} inside the
     * {@link BasePane}, but before it is actually delivered. You can catch it and prevent it from being passed into
     * the component by using the {@code deliverEvent} parameter and setting it to {@code false}.
     *
     * @param basePane Base pane that got the input event
     * @param keyStroke The actual input event
     * @param deliverEvent Set to {@code true} automatically, if you change it to {@code false} it will prevent the GUI
     *                     from passing the input event on to the focused {@link Interactable}
     */
    void onInput(T basePane, KeyStroke keyStroke, AtomicBoolean deliverEvent);

    /**
     * Called when a user entered some input which wasn't handled by the focused component. This allows you to catch it
     * at a {@link BasePane} (or {@link Window}) level and prevent it from being reported to the {@link TextGUI} as an
     * unhandled input event.
     * @param basePane {@link BasePane} that got the input event
     * @param keyStroke The unhandled input event
     * @param hasBeenHandled Initially set to {@code false}, if you change it to {@code true} then the event
     *                                   will not be reported as an unhandled input to the {@link TextGUI}
     */
    void onUnhandledInput(T basePane, KeyStroke keyStroke, AtomicBoolean hasBeenHandled);
}
