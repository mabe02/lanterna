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
        this.guiTheme = new PropertiesTheme(loadDefaultThemeProperties());
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
        return screen.readInput();
    }

    @Override
    public boolean processInput() throws IOException {
        KeyStroke keyStroke = readKeyStroke();
        if(keyStroke != null) {
            if(keyStroke.getKeyType() == KeyType.EOF) {
                throw new EOFException();
            }
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
        if(textGUIThread != null) {
            return textGUIThread;
        }
        textGUIThread = new DefaultTextGUIThread(this);
        return textGUIThread;
    }

    protected abstract void drawGUI(TextGUIGraphics graphics);
    protected abstract TerminalPosition getCursorPosition();
    protected abstract boolean handleInput(KeyStroke key);
}
