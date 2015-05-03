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
package com.googlecode.lanterna.input;


/**
 * This enum is a categorization of the various keys available on a normal computer keyboard that are usable 
 * (detectable) by a terminal environment. For ordinary numbers, letters and symbols, the enum value is <i>Character</i>
 * but please keep in mind that newline and tab, usually represented by \n and \t, are considered their own separate
 * values by this enum (<i>Enter</i> and <i>Tab</i>).
 * <p/>
 * Previously (before Lanterna 3.0), this enum was embedded inside the Key class.
 *
 * @author Martin
 */
public enum KeyType {
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
    CursorLocation,
    EOF,
    ;
}
