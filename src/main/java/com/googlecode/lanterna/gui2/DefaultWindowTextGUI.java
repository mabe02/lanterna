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
 * Copyright (C) 2010-2014 Martin
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;

/**
 *
 * @author Martin
 */
public class DefaultWindowTextGUI extends AbstractTextGUI implements WindowBasedTextGUI {
    private final WindowManager windowManager;
    private final TextGUIElement background;
    private final WindowPostRenderer postRenderer;

    public DefaultWindowTextGUI(Screen screen) {
        this(screen, TextColor.ANSI.BLUE);
    }

    public DefaultWindowTextGUI(Screen screen, TextColor backgroundColor) {
        this(screen, new StackedModalWindowManager(), new SolidColorComponent(backgroundColor));
    }


    public DefaultWindowTextGUI(Screen screen, WindowManager windowManager, TextGUIElement background) {
        this(screen, windowManager, new WindowShadowRenderer(), background);
    }

    public DefaultWindowTextGUI(Screen screen, WindowManager windowManager, WindowPostRenderer postRenderer, TextGUIElement background) {
        super(screen);
        this.windowManager = windowManager;
        this.background = background;
        this.postRenderer = postRenderer;
    }

    @Override
    public boolean isPendingUpdate() {
        return super.isPendingUpdate() || background.isInvalid() || windowManager.isInvalid();
    }

    @Override
    protected void drawGUI(TextGUIGraphics graphics) {
        background.draw(graphics);
        for(Window window: getWindowManager().getWindows()) {
            WindowDecorationRenderer decorationRenderer = getWindowManager().getWindowDecorationRenderer(window);
            TerminalPosition topLeft = getWindowManager().getTopLeftPosition(window, graphics.getSize());
            TerminalSize windowSize = getWindowManager().getSize(window, topLeft, graphics.getSize());
            window.setPosition(topLeft.withRelative(decorationRenderer.getOffset(window)));
            window.setDecoratedSize(windowSize);
            TextGUIGraphics windowGraphics = decorationRenderer.draw(this, graphics.newTextGraphics(topLeft, windowSize), window);
            window.draw(windowGraphics);
            if(postRenderer != null) {
                postRenderer.postRender(graphics, this, window, topLeft, windowSize);
            }
        }
    }

    @Override
    protected TerminalPosition getCursorPosition() {
        Window activeWindow = windowManager.getActiveWindow();
        if(activeWindow == null) {
            return null;
        }
        return activeWindow.toGlobal(activeWindow.getCursorPosition());
    }

    @Override
    public Interactable getFocusedInteractable() {
        Window activeWindow = windowManager.getActiveWindow();
        if(activeWindow == null) {
            return null;
        }
        return activeWindow.getFocusedInteractable();
    }

    @Override
    protected boolean handleInput(KeyStroke keyStroke) {
        Window activeWindow = windowManager.getActiveWindow();
        if(activeWindow == null) {
            return false;
        }
        return activeWindow.handleInput(keyStroke);
    }

    @Override
    public WindowManager getWindowManager() {
        return windowManager;
    }
}
