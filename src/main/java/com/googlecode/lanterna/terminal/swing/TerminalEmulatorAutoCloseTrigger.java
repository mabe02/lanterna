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
 * Copyright (C) 2010-2018 Martin Berglund
 */
package com.googlecode.lanterna.terminal.swing;

/**
 * This enum stored various ways the AWTTerminalFrame and SwingTerminalFrame can automatically close (hide and dispose)
 * themselves when a certain condition happens.
 */
public enum TerminalEmulatorAutoCloseTrigger {
    /**
     * Close the frame when exiting from private mode
     */
    CloseOnExitPrivateMode,
    /**
     * Close if the user presses ESC key on the keyboard
     */
    CloseOnEscape,
    ;
}
