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
 * Copyright (C) 2010-2014 Martin
 */
package com.googlecode.lanterna.screen;

import com.googlecode.lanterna.terminal.Terminal;
import java.util.Arrays;

import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.terminal.TextColor;
import java.util.EnumSet;

/**
 * This is a helper class to assist you in composing your output on a {@code Screen}. It provides methods for drawing
 * full strings as well as keeping a color and modifier state so that you don't have to specify them for every operation.
 * It also has a position state which moves as you as putting characters, so you can think of this as a pen.
 * @author Martin
 */
public class ScreenWriter {

    private final Screen screen;
    private TerminalPosition currentPosition;
    private TextColor foregroundColor;
    private TextColor backgroundColor;
    private final EnumSet<Terminal.SGR> activeModifiers;

    public ScreenWriter(final DefaultScreen screen) {
        this.foregroundColor = TextColor.ANSI.DEFAULT;
        this.backgroundColor = TextColor.ANSI.DEFAULT;
        this.screen = screen;
        this.currentPosition = new TerminalPosition(0, 0);
        this.activeModifiers = EnumSet.noneOf(Terminal.SGR.class);
    }

    public ScreenWriter setPosition(TerminalPosition newPosition) {
        this.currentPosition = newPosition;
        return this;
    }

    public TerminalPosition getPosition() {
        return currentPosition;
    }

    public TextColor getBackgroundColor() {
        return backgroundColor;
    }

    public ScreenWriter setBackgroundColor(final TextColor backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public TextColor getForegroundColor() {
        return foregroundColor;
    }

    public ScreenWriter setForegroundColor(final TextColor foregroundColor) {
        this.foregroundColor = foregroundColor;
        return this;
    }
    
    public ScreenWriter enableModifiers(Terminal.SGR... modifiers) {
        this.activeModifiers.addAll(Arrays.asList(modifiers));
        return this;
    }
    
    public ScreenWriter disableModifiers(Terminal.SGR... modifiers) {
        this.activeModifiers.removeAll(Arrays.asList(modifiers));
        return this;
    }
    
    public ScreenWriter clearModifiers() {
        this.activeModifiers.clear();
        return this;
    }

    /**
     * Fills the entire screen with a single character
     * @param c 
     */
    public void fillScreen(char c) {
        TerminalPosition savedPosition = getPosition();
        setPosition(TerminalPosition.TOP_LEFT_CORNER);
        fillRectangle(screen.getTerminalSize(), c);
        setPosition(savedPosition);
    }
    
    void drawLine(TerminalPosition toPoint, char character) {
        
    }
    
    /**
     * Takes a rectangle on the screen and fills it with a particular character (and the currently active colors and 
     * modifiers). The top-left corner will be at the current position of this {@code ScreenWriter} (inclusive) and it
     * will extend to position + size (inclusive).
     * @param size Size (in columns and rows) of the area to draw
     * @param character What character to use when filling the rectangle
     */
    void drawTriangle(TerminalSize size, char character) {
        
    }
    
    /**
     * Takes a rectangle on the screen and fills it with a particular character (and the currently active colors and 
     * modifiers). The top-left corner will be at the current position of this {@code ScreenWriter} (inclusive) and it
     * will extend to position + size (inclusive).
     * @param size Size (in columns and rows) of the area to draw
     * @param character What character to use when filling the rectangle
     */
    void fillTriangle(TerminalSize size, char character) {
        
    }
    
    /**
     * Draws the outline of a rectangle in the {@code Screen} with a particular character (and the currently active colors and 
     * modifiers). The top-left corner will be at the current position of this {@code ScreenWriter} (inclusive) and it
     * will extend to position + size (inclusive).
     * @param size Size (in columns and rows) of the area to draw
     * @param character What character to use when filling the rectangle
     */
    void drawRectangle(TerminalSize size, char character) {
        
    }
    
    /**
     * Takes a rectangle on the screen and fills it with a particular character (and the currently active colors and 
     * modifiers). The top-left corner will be at the current position of this {@code ScreenWriter} (inclusive) and it
     * will extend to position + size (inclusive).
     * @param size Size (in columns and rows) of the area to draw
     * @param character What character to use when filling the rectangle
     */
    void fillRectangle(TerminalSize size, char character) {
        for(int y = 0; y < size.getRows(); y++) {
            for(int x = 0; x < size.getColumns(); x++) {
                screen.setCharacter(
                    currentPosition.withRelativeColumn(x).withRelativeRow(y), 
                    new ScreenCharacter(
                            character, 
                            foregroundColor, 
                            backgroundColor, 
                            activeModifiers.clone()));
            }
        }
    }

    /**
     * Puts a string on the screen at the current position with the current colors and modifiers. If the string 
     * contains newlines (\r and/or \n), the method will stop at the character before that; you have to manage 
     * multi-line strings yourself!
     *
     * @param string Text to put on the screen
     */
    public void putString(String string) {
        if(string.contains("\n")) {
            string = string.substring(0, string.indexOf("\n"));
        }
        if(string.contains("\r")) {
            string = string.substring(0, string.indexOf("\r"));
        }
        for(int i = 0; i < string.length(); i++) {
            screen.setCharacter(
                    currentPosition.withRelativeColumn(i), 
                    new ScreenCharacter(
                            string.charAt(i), 
                            foregroundColor, 
                            backgroundColor, 
                            activeModifiers.clone()));
        }
        currentPosition = currentPosition.withRelativeColumn(string.length());
    }
}
