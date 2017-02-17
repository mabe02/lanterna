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
package com.googlecode.lanterna.terminal.swing;

/**
 * This interface can be used to control the backlog scrolling of a SwingTerminal. It's used as a callback by the
 * {@code SwingTerminal} when it needs to fetch the scroll position and also used whenever the backlog changes to that
 * some view class, like a scrollbar for example, can update its view accordingly.
 * @author Martin
 */
public interface TerminalScrollController {
    /**
     * Called by the SwingTerminal when the terminal has changed or more lines are entered into the terminal
     * @param totalSize Total number of lines in the backlog currently
     * @param screenSize Number of lines covered by the terminal window at its current size
     */
    void updateModel(int totalSize, int screenSize);

    /**
     * Called by the SwingTerminal to know the 'offset' into the backlog. Returning 0 here will always draw the latest
     * lines; if you return 5, it will draw from five lines into the backlog and skip the 5 most recent lines.
     * @return According to this scroll controller, how far back into the backlog are we?
     */
    int getScrollingOffset();

    /**
     * Implementation of {@link TerminalScrollController} that does nothing
     */
    final class Null implements TerminalScrollController {
        @Override
        public void updateModel(int totalSize, int screenSize) {
        }

        @Override
        public int getScrollingOffset() {
            return 0;
        }
    }
}
