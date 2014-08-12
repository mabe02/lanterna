package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.graphics.PropertiesTheme;
import com.googlecode.lanterna.graphics.Theme;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;
import java.util.Properties;

/**
 * This abstract implementation of TextGUI contains some basic management of the underlying Screen and translates the
 * input event queue into an abstract method call.
 * @author Martin
 */
public abstract class AbstractTextGUI implements TextGUI {

    private final Screen screen;
    private boolean dirty;
    private TextGUIThread textGUIThread;
    private Theme guiTheme;

    protected AbstractTextGUI(Screen screen) {
        this.screen = screen;
        this.dirty = false;
        this.textGUIThread = null;
        this.guiTheme = new PropertiesTheme(new Properties());
    }

    @Override
    public boolean processInput() throws IOException {
        KeyStroke keyStroke = screen.readInput();
        if(keyStroke != null) {
            dirty = handleInput(keyStroke) || dirty;
        }
        return keyStroke != null;
    }

    @Override
    public void setTheme(Theme theme) {
        this.guiTheme = theme;
    }

    @Override
    public void updateScreen() throws IOException {
        screen.doResizeIfNecessary();
        drawGUI(new TextGUIGraphics(this, screen.newTextGraphics(), guiTheme));
        screen.setCursorPosition(null);
        screen.refresh();
    }

    @Override
    public boolean isPendingUpdate() {
        return screen.doResizeIfNecessary() != null || dirty;
    }

    @Override
    public TextGUIThread getGUIThread() {
        if(textGUIThread != null) {
            return textGUIThread;
        }
        textGUIThread = new DefaultTextGUIThread(this);
        return textGUIThread;
    }

    protected abstract void drawGUI(TextGUIGraphics graphics);
    protected abstract boolean handleInput(KeyStroke key);
}
