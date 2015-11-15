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
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyType;

import java.util.*;

/**
 * Abstract Window implementation that contains much code that is shared between different concrete Window
 * implementations.
 * @author Martin
 */
public class AbstractWindow extends AbstractBasePane implements Window {
    private String title;
    private WindowBasedTextGUI textGUI;
    private boolean visible;
    private TerminalSize lastKnownSize;
    private TerminalSize lastKnownDecoratedSize;
    private TerminalPosition lastKnownPosition;
    private TerminalPosition contentOffset;
    private Set<Hint> hints;

    public AbstractWindow() {
        this("");
    }

    public AbstractWindow(String title) {
        super();
        this.title = title;
        this.textGUI = null;
        this.visible = false;
        this.lastKnownPosition = null;
        this.lastKnownSize = null;
        this.lastKnownDecoratedSize = null;

        this.hints = new HashSet<Hint>();
    }

    @Override
    public void setTextGUI(WindowBasedTextGUI textGUI) {
        //This is kind of stupid check, but might cause it to blow up on people using the library incorrectly instead of
        //just causing weird behaviour
        if(this.textGUI != null && textGUI != null) {
            throw new UnsupportedOperationException("Are you calling setTextGUI yourself? Please read the documentation"
                    + " in that case (this could also be a bug in Lanterna, please report it if you are sure you are "
                    + "not calling Window.setTextGUI(..) from your code)");
        }
        this.textGUI = textGUI;
    }

    @Override
    public WindowBasedTextGUI getTextGUI() {
        return textGUI;
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
        if(!graphics.getSize().equals(lastKnownSize)) {
            getComponent().invalidate();
        }
        lastKnownSize = graphics.getSize();
        super.draw(graphics);
    }

    @Override
    public boolean handleInput(KeyStroke key) {
        boolean handled = super.handleInput(key);
        if(!handled && key.getKeyType() == KeyType.Escape) {
            close();
            return true;
        }
        return handled;
    }

    @Override
    public TerminalPosition toGlobal(TerminalPosition localPosition) {
        if(localPosition == null) {
            return null;
        }
        return lastKnownPosition.withRelative(contentOffset.withRelative(localPosition));
    }

    @Override
    public TerminalPosition fromGlobal(TerminalPosition globalPosition) {
        if(globalPosition == null) {
            return null;
        }
        return globalPosition.withRelative(
                -lastKnownPosition.getColumn() - contentOffset.getColumn(),
                -lastKnownPosition.getRow() - contentOffset.getRow());
    }

    @Override
    public TerminalSize getPreferredSize() {
        return contentHolder.getPreferredSize();
    }

    @Override
    public void setHints(Collection<Hint> hints) {
        this.hints = new HashSet<Hint>(hints);
    }

    @Override
    public Set<Hint> getHints() {
        return Collections.unmodifiableSet(hints);
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
    public void setContentOffset(TerminalPosition offset) {
        this.contentOffset = offset;
    }

    @Override
    public void close() {
        if(textGUI != null) {
            textGUI.removeWindow(this);
        }
        setComponent(null);
    }

    @Override
    public void waitUntilClosed() {
        WindowBasedTextGUI textGUI = getTextGUI();
        if(textGUI != null) {
            textGUI.waitForWindowToClose(this);
        }
    }
}
