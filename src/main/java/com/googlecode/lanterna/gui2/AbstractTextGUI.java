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
import com.googlecode.lanterna.graphics.PropertiesTheme;
import com.googlecode.lanterna.graphics.Theme;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

import java.io.EOFException;
import java.io.FileInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This abstract implementation of TextGUI contains some basic management of the underlying Screen and translates the
 * input event queue into an abstract method call.
 * @author Martin
 */
public abstract class AbstractTextGUI implements TextGUI {

    private final Screen screen;
    private final List<Listener> listeners;
    private boolean blockingIO;
    private boolean dirty;
    private TextGUIThread textGUIThread;
    private Theme guiTheme;

    protected AbstractTextGUI(TextGUIThreadFactory textGUIThreadFactory, Screen screen) {
        if(screen == null) {
            throw new IllegalArgumentException("Creating a TextGUI requires an underlying Screen");
        }
        this.screen = screen;
        this.listeners = new CopyOnWriteArrayList<Listener>();
        this.blockingIO = false;
        this.dirty = false;
        this.guiTheme = new PropertiesTheme(loadDefaultThemeProperties());
        this.textGUIThread = textGUIThreadFactory.createTextGUIThread(this);
    }

    private static Properties loadDefaultThemeProperties() {
        Properties properties = new Properties();
        try {
            ClassLoader classLoader = AbstractTextGUI.class.getClassLoader();
            InputStream resourceAsStream = classLoader.getResourceAsStream("default-theme.properties");
            if(resourceAsStream == null) {
                resourceAsStream = new FileInputStream("src/main/resources/default-theme.properties");
            }
            properties.load(resourceAsStream);
            resourceAsStream.close();
            return properties;
        }
        catch(IOException e) {
            return properties;
        }
    }

    protected KeyStroke readKeyStroke() throws IOException {
        return blockingIO ? screen.readInput() : pollInput();
    }

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
        this.guiTheme = theme;
    }

    @Override
    public synchronized void updateScreen() throws IOException {
        screen.doResizeIfNecessary();
        drawGUI(new TextGUIGraphics(this, screen.newTextGraphics(), guiTheme));
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

    public void setBlockingIO(boolean blockingIO) {
        this.blockingIO = blockingIO;
    }

    public boolean isBlockingIO() {
        return blockingIO;
    }
    
    protected boolean fireUnhandledKeyStroke(KeyStroke keyStroke) {
        boolean handled = false;
        for(Listener listener: listeners) {
            handled = listener.onUnhandledKeyStroke(this, keyStroke) || handled;
        }
        return handled;
    }

    protected void invalidate() {
        dirty = true;
    }

    protected abstract void drawGUI(TextGUIGraphics graphics);
    protected abstract TerminalPosition getCursorPosition();
    protected abstract boolean handleInput(KeyStroke key);
}
