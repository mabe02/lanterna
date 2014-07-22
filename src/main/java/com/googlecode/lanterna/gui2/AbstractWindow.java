package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;

/**
 * Created by martin on 19/07/14.
 */
public class AbstractWindow implements Window {
    private WindowDecorationRenderer windowDecorationRenderer;
    private String title;
    private WindowManager windowManager;
    private boolean visible;
    private boolean invalid;

    public AbstractWindow() {
        this("");
    }

    public AbstractWindow(String title) {
        this.windowDecorationRenderer = new DefaultWindowDecorationRenderer();
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
    public boolean isInvalid() {
        return invalid;
    }

    @Override
    public void draw(TextGUI textGUI, TextGraphics graphics) {
        graphics = windowDecorationRenderer.draw(textGUI, graphics, this);
        graphics.setBackgroundColor(TextColor.ANSI.WHITE);
        graphics.fillScreen(' ');
    }

    @Override
    public boolean handleInput(KeyStroke key) {
        return false;
    }

    @Override
    public TerminalSize getPreferredSize() {
        return new TerminalSize(40, 10);
    }

    @Override
    public void close() {
        if(windowManager == null) {
            throw new IllegalStateException("Cannot close " + toString() + " because it is not managed by any window manager");
        }
        windowManager.removeWindow(this);
    }
}
