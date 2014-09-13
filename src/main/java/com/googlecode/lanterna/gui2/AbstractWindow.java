package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyType;

import java.util.Collections;
import java.util.Set;

/**
 * Abstract Window implementation that contains much code that is shared between different concrete Window
 * implementations.
 * @author Martin
 */
public class AbstractWindow implements Window {
    private String title;
    private WindowManager windowManager;
    private boolean visible;
    private boolean invalid;

    public AbstractWindow() {
        this("");
    }

    public AbstractWindow(String title) {
        this.title = title;
        this.visible = true;
        this.invalid = false;
    }

    public void setWindowManager(WindowManager windowManager) {
        this.windowManager = windowManager;
    }

    public void setTitle(String title) {
        this.title = title;
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
    public boolean isEnabled() {
        return invalid;
    }

    @Override
    public void draw(TextGUIGraphics graphics) {
        graphics.setBackgroundColor(TextColor.ANSI.WHITE);
        graphics.fillScreen(' ');
    }

    @Override
    public boolean handleInput(KeyStroke key) {
        if(key.getKeyType() == KeyType.Escape) {
            close();
            return true;
        }
        return false;
    }

    @Override
    public TerminalSize getPreferredSize() {
        return new TerminalSize(40, 10);
    }

    @Override
    public Set<WindowManager.Hint> getWindowManagerHints() {
        return Collections.emptySet();
    }

    @Override
    public void close() {
        if(windowManager == null) {
            throw new IllegalStateException("Cannot close " + toString() + " because it is not managed by any window manager");
        }
        windowManager.removeWindow(this);
    }
}