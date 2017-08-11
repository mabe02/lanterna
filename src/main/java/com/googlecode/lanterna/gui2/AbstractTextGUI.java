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
import com.googlecode.lanterna.bundle.LanternaThemes;
import com.googlecode.lanterna.graphics.Theme;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

import java.io.EOFException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This abstract implementation of TextGUI contains some basic management of the underlying Screen and other common code
 * that can be shared between different implementations.
 * @author Martin
 */
public abstract class AbstractTextGUI implements TextGUI {

    private final Screen screen;
    private final List<Listener> listeners;
    private boolean blockingIO;
    private boolean dirty;
    private TextGUIThread textGUIThread;
    private Theme guiTheme;

    /**
     * Constructor for {@code AbstractTextGUI} that requires a {@code Screen} and a factory for creating the GUI thread
     * @param textGUIThreadFactory Factory class to use for creating the {@code TextGUIThread} class
     * @param screen What underlying {@code Screen} to use for this text GUI
     */
    protected AbstractTextGUI(TextGUIThreadFactory textGUIThreadFactory, Screen screen) {
        if(screen == null) {
            throw new IllegalArgumentException("Creating a TextGUI requires an underlying Screen");
        }
        this.screen = screen;
        this.listeners = new CopyOnWriteArrayList<Listener>();
        this.blockingIO = false;
        this.dirty = false;
        this.guiTheme = LanternaThemes.getDefaultTheme();
        this.textGUIThread = textGUIThreadFactory.createTextGUIThread(this);
    }

    /**
     * Reads one key from the input queue, blocking or non-blocking depending on if blocking I/O has been enabled. To
     * enable blocking I/O (disabled by default), use {@code setBlockingIO(true)}.
     * @return One piece of user input as a {@code KeyStroke} or {@code null} if blocking I/O is disabled and there was
     *         no input waiting
     * @throws IOException In case of an I/O error while reading input
     */
    protected KeyStroke readKeyStroke() throws IOException {
        return blockingIO ? screen.readInput() : pollInput();
    }

    /**
     * Polls the underlying input queue for user input, returning either a {@code KeyStroke} or {@code null}
     * @return {@code KeyStroke} representing the user input or {@code null} if there was none
     * @throws IOException In case of an I/O error while reading input
     */
    protected KeyStroke pollInput() throws IOException {
        return screen.pollInput();
    }

    @Override
    public synchronized boolean processInput() throws IOException {
        boolean gotInput = false;
        KeyStroke keyStroke = readKeyStroke();
        if(keyStroke != null) {
            gotInput = true;
            do {
                if (keyStroke.getKeyType() == KeyType.EOF) {
                    throw new EOFException();
                }
                boolean handled = handleInput(keyStroke);
                if(!handled) {
                    handled = fireUnhandledKeyStroke(keyStroke);
                }
                dirty = handled || dirty;
                keyStroke = pollInput();
            } while(keyStroke != null);
        }
        return gotInput;
    }

    @Override
    public void setTheme(Theme theme) {
        if(theme != null) {
            this.guiTheme = theme;
        }
    }

    @Override
    public Theme getTheme() {
        return guiTheme;
    }

    @Override
    public synchronized void updateScreen() throws IOException {
        screen.doResizeIfNecessary();
        drawGUI(new DefaultTextGUIGraphics(this, screen.newTextGraphics()));
        screen.setCursorPosition(getCursorPosition());
        screen.refresh();
        dirty = false;
    }

    @Override
    public boolean isPendingUpdate() {
        return screen.doResizeIfNecessary() != null || dirty;
    }

    @Override
    public TextGUIThread getGUIThread() {
        return textGUIThread;
    }

    @Override
    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    /**
     * Enables blocking I/O, causing calls to {@code readKeyStroke()} to block until there is input available. Notice
     * that you can still poll for input using {@code pollInput()}.
     * @param blockingIO Set this to {@code true} if blocking I/O should be enabled, otherwise {@code false}
     */
    public void setBlockingIO(boolean blockingIO) {
        this.blockingIO = blockingIO;
    }

    /**
     * Checks if blocking I/O is enabled or not
     * @return {@code true} if blocking I/O is enabled, otherwise {@code false}
     */
    public boolean isBlockingIO() {
        return blockingIO;
    }

    /**
     * This method should be called when there was user input that wasn't handled by the GUI. It will fire the
     * {@code onUnhandledKeyStroke(..)} method on any registered listener.
     * @param keyStroke The {@code KeyStroke} that wasn't handled by the GUI
     * @return {@code true} if at least one of the listeners handled the key stroke, this will signal to the GUI that it
     * needs to be redrawn again.
     */
    protected final boolean fireUnhandledKeyStroke(KeyStroke keyStroke) {
        boolean handled = false;
        for(Listener listener: listeners) {
            handled = listener.onUnhandledKeyStroke(this, keyStroke) || handled;
        }
        return handled;
    }

    /**
     * Marks the whole text GUI as invalid and that it needs to be redrawn at next opportunity
     */
    protected void invalidate() {
        dirty = true;
    }

    /**
     * Draws the entire GUI using a {@code TextGUIGraphics} object
     * @param graphics Graphics object to draw using
     */
    protected abstract void drawGUI(TextGUIGraphics graphics);

    /**
     * Top-level method for drilling in to the GUI and figuring out, in global coordinates, where to place the text
     * cursor on the screen at this time.
     * @return Where to place the text cursor, or {@code null} if the cursor should be hidden
     */
    protected abstract TerminalPosition getCursorPosition();

    /**
     * This method should take the user input and feed it to the focused component for handling.
     * @param key {@code KeyStroke} representing the user input
     * @return {@code true} if the input was recognized and handled by the GUI, indicating that the GUI should be redrawn
     */
    protected abstract boolean handleInput(KeyStroke key);
}
