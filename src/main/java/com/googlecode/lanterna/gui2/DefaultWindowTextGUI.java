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
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.screen.Screen;

/**
 *
 * @author Martin
 */
public class DefaultWindowTextGUI extends AbstractTextGUI implements WindowBasedTextGUI {
    private final WindowManager windowManager;
    private final GUIElement background;

    public DefaultWindowTextGUI(Screen screen, WindowManager windowManager, GUIElement background) {
        super(screen);
        this.windowManager = windowManager;
        this.background = background;
    }

    @Override
    protected boolean isInvalid() {
        return background.isInvalid();
    }

    @Override
    protected void drawGUI(TextGUIGraphics graphics) {
        background.draw(graphics);
    }

    @Override
    protected boolean handleInput(Key key) {
        return windowManager.handleInput(key);
    }

    @Override
    public void addWindow(Window window, Hint... windowManagerHints) {
        windowManager.addWindow(window, windowManagerHints);
    }

    @Override
    public void removeWindow(Window window) {
        windowManager.removeWindow(window);
    }
}
