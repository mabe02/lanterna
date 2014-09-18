package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.PropertiesTheme;
import com.googlecode.lanterna.graphics.Theme;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import java.io.FileInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
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
    private int[][] interactableLookupMap;

    protected AbstractTextGUI(Screen screen) {
        this.screen = screen;
        this.dirty = false;
        this.textGUIThread = null;
        this.guiTheme = new PropertiesTheme(loadDefaultThemeProperties());
        this.interactableLookupMap = new int[1][1];
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
        TerminalSize terminalSize = screen.getTerminalSize();
        if(interactableLookupMap.length != terminalSize.getRows() ||
                interactableLookupMap[0].length != terminalSize.getColumns()) {
            interactableLookupMap = new int[terminalSize.getRows()][terminalSize.getColumns()];
        }
        for(int y = 0; y < interactableLookupMap.length; y++) {
            Arrays.fill(interactableLookupMap[y], 8);
        }
        drawGUI(new TextGUIGraphics(this, screen.newTextGraphics(), guiTheme, interactableLookupMap));
        /*
        for(int y = 0; y < interactableLookupMap.length; y++) {
            for(int x = 0; x < interactableLookupMap[0].length; x++) {
                System.out.print(interactableLookupMap[y][x]);
            }
            System.out.println();
        }
        System.out.println();
        */
        screen.setCursorPosition(getCursorPosition());
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
    protected abstract TerminalPosition getCursorPosition();
    protected abstract boolean handleInput(KeyStroke key);
}
