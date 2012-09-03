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

import com.googlecode.lanterna.terminal.TerminalPosition;

/**
 * A default background renderer implementation that draws the background as
 * a solid color and, if set, prints a title and the JVM memory usage.
 * @author Martin
 */
public class DefaultBackgroundRenderer implements GUIScreenBackgroundRenderer {

    private String title;
    private boolean showMemoryUsage;

    /**
     * Solid-color background with no title
     */
    public DefaultBackgroundRenderer() {
        this("");
    }

    /**
     * Solid-color background with user-specified title
     *
     * @param title Title to display in the top-left corner of the window
     */
    public DefaultBackgroundRenderer(String title) {
        this.title = title;
        this.showMemoryUsage = false;
    }

    @Override
    public void drawBackground(TextGraphics textGraphics) {

        textGraphics.applyTheme(Theme.Category.SCREEN_BACKGROUND);

        //Clear the background
        textGraphics.fillRectangle(' ', new TerminalPosition(0, 0), textGraphics.getSize());

        //Write the title
        textGraphics.drawString(3, 0, title);

        //Write memory usage
        if (showMemoryUsage) {
            Runtime runtime = Runtime.getRuntime();
            long freeMemory = runtime.freeMemory();
            long totalMemory = runtime.totalMemory();
            long usedMemory = totalMemory - freeMemory;

            usedMemory /= (1024 * 1024);
            totalMemory /= (1024 * 1024);

            String memUsageString = "Memory usage: " + usedMemory + " MB of " + totalMemory + " MB";
            textGraphics.drawString(textGraphics.getSize().getColumns() - memUsageString.length() - 1,
                    textGraphics.getSize().getRows() - 1, memUsageString);
        }
    }

    /**
     * @param title Title to be displayed in the top-left corner of the
     * GUIScreen
     */
    public void setTitle(String title) {
        if (title == null) {
            title = "";
        }

        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    /**
     * If true, will display the current memory usage in the bottom right
     * corner, updated on every screen refresh
     */
    public void setShowMemoryUsage(boolean showMemoryUsage) {
        this.showMemoryUsage = showMemoryUsage;
    }

    public boolean isShowingMemoryUsage() {
        return showMemoryUsage;
    }
}
