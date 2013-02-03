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
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.gui2.WindowManager.Hint;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.TextColor;

/**
 *
 * @author Martin
 */
public class DialogsTextGUI implements TextGUI {
    
    private final Screen screen;
    private final AreaRenderer backgroundRenderer;

    public DialogsTextGUI(Screen screen) {
        this.screen = screen;
        this.backgroundRenderer = new AreaRenderer() {
            @Override
            public void draw(TextGUIGraphics graphics) {
                graphics.setForegroundColor(TextColor.ANSI.CYAN);
                graphics.setBackgroundColor(TextColor.ANSI.BLUE);
                graphics.fill();
            }
        };
    }

    @Override
    public void start() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void stop() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setWindowManager() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public WindowManager getWindowManager() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addWindow(Window window, Hint... windowManagerHints) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeWindow(Window window) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
