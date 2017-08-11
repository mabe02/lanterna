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
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyType;

import java.util.*;

/**
 * Abstract Window has most of the code requiring for a window to function, all concrete window implementations extends
 * from this in one way or another. You can define your own window by extending from this, as an alternative to building
 * up the GUI externally by constructing a {@code BasicWindow} and adding components to it.
 * @author Martin
 */
public abstract class AbstractWindow extends AbstractBasePane<Window> implements Window {
    private String title;
    private WindowBasedTextGUI textGUI;
    private boolean visible;
    private TerminalSize lastKnownSize;
    private TerminalSize lastKnownDecoratedSize;
    private TerminalPosition lastKnownPosition;
    private TerminalPosition contentOffset;
    private Set<Hint> hints;
    private WindowPostRenderer windowPostRenderer;
    private boolean closeWindowWithEscape;

    /**
     * Default constructor, this creates a window with no title
     */
    public AbstractWindow() {
        this("");
    }

    /**
     * Creates a window with a specific title that will (probably) be drawn in the window decorations
     * @param title Title of this window
     */
    public AbstractWindow(String title) {
        super();
        this.title = title;
        this.textGUI = null;
        this.visible = true;
        this.contentOffset = TerminalPosition.TOP_LEFT_CORNER;
        this.lastKnownPosition = null;
        this.lastKnownSize = null;
        this.lastKnownDecoratedSize = null;
        this.closeWindowWithEscape = false;

        this.hints = new HashSet<Hint>();
    }

    /**
     * Setting this property to {@code true} will cause pressing the ESC key to close the window. This used to be the
     * default behaviour of lanterna 3 during the development cycle but is not longer the case. You are encouraged to
     * put proper buttons or other kind of components to clearly mark to the user how to close the window instead of
     * magically taking ESC, but sometimes it can be useful (when doing testing, for example) to enable this mode.
     * @param closeWindowWithEscape If {@code true}, this window will self-close if you press ESC key
     */
    public void setCloseWindowWithEscape(boolean closeWindowWithEscape) {
        this.closeWindowWithEscape = closeWindowWithEscape;
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

    /**
     * Alters the title of the window to the supplied string
     * @param title New title of the window
     */
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
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    @Override
    public void draw(TextGUIGraphics graphics) {
        if(!graphics.getSize().equals(lastKnownSize)) {
            getComponent().invalidate();
        }
        setSize(graphics.getSize(), false);
        super.draw(graphics);
    }

    @Override
    public boolean handleInput(KeyStroke key) {
        boolean handled = super.handleInput(key);
        if(!handled && closeWindowWithEscape && key.getKeyType() == KeyType.Escape) {
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
        if(globalPosition == null || lastKnownPosition == null) {
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
        invalidate();
    }

    @Override
    public Set<Hint> getHints() {
        return Collections.unmodifiableSet(hints);
    }

    @Override
    public WindowPostRenderer getPostRenderer() {
        return  windowPostRenderer;
    }

    @Override
    public void addWindowListener(WindowListener windowListener) {
        addBasePaneListener(windowListener);
    }

    @Override
    public void removeWindowListener(WindowListener windowListener) {
        removeBasePaneListener(windowListener);
    }

    /**
     * Sets the post-renderer to use for this window. This will override the default from the GUI system (if there is
     * one set, otherwise from the theme).
     * @param windowPostRenderer Window post-renderer to assign to this window
     */
    public void setWindowPostRenderer(WindowPostRenderer windowPostRenderer) {
        this.windowPostRenderer = windowPostRenderer;
    }

    @Override
    public final TerminalPosition getPosition() {
        return lastKnownPosition;
    }

    @Override
    public final void setPosition(TerminalPosition topLeft) {
        TerminalPosition oldPosition = this.lastKnownPosition;
        this.lastKnownPosition = topLeft;

        // Fire listeners
        for(BasePaneListener<?> listener: getBasePaneListeners()) {
            if(listener instanceof WindowListener) {
                ((WindowListener)listener).onMoved(this, oldPosition, topLeft);
            }
        }
    }

    @Override
    public final TerminalSize getSize() {
        return lastKnownSize;
    }

    @Override
    public void setSize(TerminalSize size) {
        setSize(size, true);
    }

    private void setSize(TerminalSize size, boolean invalidate) {
        TerminalSize oldSize = this.lastKnownSize;
        this.lastKnownSize = size;
        if(invalidate) {
            invalidate();
        }

        // Fire listeners
        for(BasePaneListener<?> listener: getBasePaneListeners()) {
            if(listener instanceof WindowListener) {
                ((WindowListener)listener).onResized(this, oldSize, size);
            }
        }
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

    Window self() { return this; }
}
