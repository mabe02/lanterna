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

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyType;

import java.util.Collections;
import java.util.Set;

/**
 * Abstract Window implementation that contains much code that is shared between different concrete Window
 * implementations.
 * @author Martin
 */
public class AbstractWindow extends AbstractBasePane implements Window {
    private String title;
    private TextGUI textGUI;
    private WindowManager windowManager;
    private boolean visible;
    private TerminalSize lastKnownSize;
    private TerminalSize lastKnownDecoratedSize;
    private TerminalPosition lastKnownPosition;

    public AbstractWindow() {
        this("");
    }

    public AbstractWindow(String title) {
        super();
        this.title = title;
        this.textGUI = null;
        this.windowManager = null;
        this.visible = true;
        this.lastKnownPosition = null;
        this.lastKnownSize = null;
        this.lastKnownDecoratedSize = null;
    }

    @Override
    public void setTextGUI(TextGUI textGUI) {
        this.textGUI = textGUI;
    }

    @Override
    public TextGUI getTextGUI() {
        return textGUI;
    }

    public void setWindowManager(WindowManager windowManager) {
        this.windowManager = windowManager;
    }

    public void setTitle(String title) {
        this.title = title;
        invalidate();
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void draw(TextGUIGraphics graphics) {
        lastKnownSize = graphics.getSize();
        super.draw(graphics);
    }

    @Override
    public boolean handleInput(KeyStroke key) {
        if(key.getKeyType() == KeyType.Escape) {
            close();
            return true;
        }
        return super.handleInput(key);
    }

    @Override
    public TerminalPosition toGlobal(TerminalPosition localPosition) {
        if(localPosition == null) {
            return null;
        }
        return lastKnownPosition.withRelative(localPosition);
    }

    @Override
    public TerminalSize getPreferredSize() {
        return contentHolder.getPreferredSize();
    }

    @Override
    public Set<Hint> getHints() {
        return Collections.emptySet();
    }

    @Override
    public final TerminalPosition getPosition() {
        return lastKnownPosition;
    }

    @Override
    public final void setPosition(TerminalPosition topLeft) {
        this.lastKnownPosition = topLeft;
    }

    @Override
    public final TerminalSize getSize() {
        return lastKnownSize;
    }

    @Override
    public final TerminalSize getDecoratedSize() {
        return lastKnownDecoratedSize;
    }

    @Override
    public final void setDecoratedSize(TerminalSize decoratedSize) {
        this.lastKnownDecoratedSize = decoratedSize;
    }

    @Override
    public void close() {
        if(windowManager == null) {
            throw new IllegalStateException("Cannot close " + toString() + " because it is not managed by any window manager");
        }
        windowManager.removeWindow(this);
        setComponent(null);
    }
}
