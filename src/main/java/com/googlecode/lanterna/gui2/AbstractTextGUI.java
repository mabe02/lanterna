package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;

/**
 * This abstract implementation of TextGUI contains some basic management of the underlying Screen and translates the
 * input event queue into an abstract method call.
 * @author Martin
 */
public abstract class AbstractTextGUI implements TextGUI {

    private final Screen screen;
    private boolean dirty;
    private TextGUIThread textGUIThread;

    protected AbstractTextGUI(Screen screen) {
        this.screen = screen;
        this.dirty = false;
        this.textGUIThread = null;
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
    public void updateScreen() throws IOException {
        screen.doResizeIfNecessary();
        drawGUI(screen.newTextGraphics());
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

    protected abstract void drawGUI(TextGraphics graphics);
    protected abstract boolean handleInput(KeyStroke key);
}
