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
package com.googlecode.lanterna.input;


/**
 * This enum is a categorization of the various keys available on a normal computer keyboard that are usable 
 * (detectable) by a terminal environment. For ordinary numbers, letters and symbols, the enum value is <i>Character</i>
 * but please keep in mind that newline and tab, usually represented by \n and \t, are considered their own separate
 * values by this enum (<i>Enter</i> and <i>Tab</i>).
 * <p>
 * Previously (before Lanterna 3.0), this enum was embedded inside the Key class.
 *
 * @author Martin
 */
public enum KeyType {
    /**
     * This value corresponds to a regular character 'typed', usually alphanumeric or a symbol. The one special case
     * here is the enter key which could be expected to be returned as a '\n' character but is actually returned as a
     * separate {@code KeyType} (see below). Tab, backspace and some others works this way too.
     */
    Character,
    Escape,
    Backspace,
    ArrowLeft,
    ArrowRight,
    ArrowUp,
    ArrowDown,
    Insert,
    Delete,
    Home,
    End,
    PageUp,
    PageDown,
    Tab,
    ReverseTab,
    Enter,
    F1,
    F2,
    F3,
    F4,
    F5,
    F6,
    F7,
    F8,
    F9,
    F10,
    F11,
    F12,
    F13,
    F14,
    F15,
    F16,
    F17,
    F18,
    F19,
    Unknown,

    //"Virtual" KeyStroke types
    /**
     * This value is only internally within Lanterna to understand where the cursor currently is, it's not expected to
     * be returned by the API to an input read call.
     */
    CursorLocation,
    /**
     * This type is not really a key stroke but actually a 'catch-all' for mouse related events. Please note that mouse
     * event capturing must first be enabled and many terminals don't suppose this extension at all.
     */
    MouseEvent,
    /**
     * This value is returned when you try to read input and the input stream has been closed.
     */
    EOF,
    ;
}
