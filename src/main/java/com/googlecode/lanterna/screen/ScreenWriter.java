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
import java.util.Comparator;
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

    /**
     * Draws a line from the screen writer's current position to a specified position, using a supplied character. After
     * the operation, this {@code ScreenWriter}'s position will be at the end-point of the line.
     * @param toPoint Where to draw the line
     * @param character Character to use for the line
     */
    public void drawLine(TerminalPosition toPoint, char character) {
        drawLine(currentPosition, toPoint, character);
        currentPosition = toPoint;
    }

    private void drawLine(TerminalPosition fromPoint, TerminalPosition toPoint, char character) {
        //http://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm
        ScreenCharacter screenCharacter = newScreenCharacter(character);
        int dx = toPoint.getColumn() - fromPoint.getColumn();
        int dy = toPoint.getRow() - fromPoint.getRow();
        int d = 2 * dy - dx;
        screen.setCharacter(fromPoint, screenCharacter);
        int y = fromPoint.getRow();
        for(int x = fromPoint.getColumn() + 1; x <= toPoint.getColumn(); x++) {
            if(d > 0) {
                y = y + 1;
                screen.setCharacter(new TerminalPosition(x, y), screenCharacter);
                d = d + (2 * dy - 2 * dx);
            }
            else {
                screen.setCharacter(new TerminalPosition(x, y), screenCharacter);
                d = d + (2 * dy);
            }
        }
    }

    /**
     * Draws the outline of a triangle on the screen, using a supplied character. The triangle will begin at this
     * writer's current position, go through both supplied points and then back again to the original position. The
     * position of this {@code ScreenWriter} after this operation will be the same as where it was before.
     * @param p1 First point on the screen to draw the triangle with, starting from the current position
     * @param p2 Second point on the screen to draw the triangle with, going from p1 and going back to the original start
     * @param character What character to use when drawing the lines of the triangle
     */
    void drawTriangle(TerminalPosition p1, TerminalPosition p2, char character) {
        TerminalPosition originalStart = currentPosition;
        drawLine(p1, character);
        drawLine(p2, character);
        drawLine(originalStart, character);
    }

    /**
     * Draws a filled triangle on the screen, using a supplied character. The triangle will begin at this
     * writer's current position, go through both supplied points and then back again to the original position. The
     * position of this {@code ScreenWriter} after this operation will be the same as where it was before.
     * @param p1 First point on the screen to draw the triangle with, starting from the current position
     * @param p2 Second point on the screen to draw the triangle with, going from p1 and going back to the original start
     * @param character What character to use when drawing the lines of the triangle
     */
    void fillTriangle(TerminalPosition p1, TerminalPosition p2, char character) {
        //Sort the points
        TerminalPosition[] points = new TerminalPosition[]{currentPosition, p1, p2};
        Arrays.sort(points, new Comparator<TerminalPosition>() {
            @Override
            public int compare(TerminalPosition o1, TerminalPosition o2) {
                return Integer.compare(o1.getRow(), o2.getRow());
            }
        });
        TerminalPosition A = points[0];
        TerminalPosition B = points[1];
        TerminalPosition C = points[2];

        int dx1, dx2, dx3;
        if (B.getRow() - A.getRow() > 0) {
            dx1 = (B.getColumn() - A.getColumn()) / (B.getRow() - A.getRow());
        }
        else {
            dx1 = 0;
        }
        if (C.getRow() - A.getRow() > 0) {
            dx2 = (C.getColumn() - A.getColumn()) / (C.getRow() - A.getRow());
        }
        else {
            dx2 = 0;
        }
        if (C.getRow() - B.getRow() > 0) {
            dx3 = (C.getColumn() - B.getColumn()) / (C.getRow() - B.getRow());
        }
        else {
            dx3 = 0;
        }

        int Sx, Sy, Ex, Ey;
        Sx = Ex = A.getColumn();
        Sy = Ey = A.getRow();
        if (dx1 > dx2) {
            for (; Sy <= B.getRow(); Sy++, Ey++, Sx += dx2, Ex += dx1) {
                drawLine(new TerminalPosition(Sx, Sy), new TerminalPosition(Ex, Ey), character);
            }
            Ex = B.getColumn();
            Ey = B.getRow();
            for (; Sy <= C.getRow(); Sy++, Ey++, Sx += dx2, Ex += dx3) {
                drawLine(new TerminalPosition(Sx, Sy), new TerminalPosition(Ex, Ey), character);
            }
        } else {
            for (; Sy <= B.getRow(); Sy++, Ey++, Sx += dx1, Ex += dx2) {
                drawLine(new TerminalPosition(Sx, Sy), new TerminalPosition(Ex, Ey), character);
            }
            Sx = B.getColumn();
            Sy = B.getRow();
            for (; Sy <= C.getRow(); Sy++, Ey++, Sx += dx3, Ex += dx2) {
                drawLine(new TerminalPosition(Sx, Sy), new TerminalPosition(Ex, Ey), character);
            }
        }
    }

    /**
     * Draws the outline of a rectangle in the {@code Screen} with a particular character (and the currently active colors and
     * modifiers). The top-left corner will be at the current position of this {@code ScreenWriter} (inclusive) and it
     * will extend to position + size (inclusive). The current position of this {@code ScreenWriter} after this
     * operation will be the same as where it started.
     * @param size Size (in columns and rows) of the area to draw
     * @param character What character to use when drawing the outline of the rectangle
     */
    void drawRectangle(TerminalSize size, char character) {
        TerminalPosition originalStart = currentPosition;
        drawLine(currentPosition.withRelativeColumn(size.getColumns()), character);
        drawLine(currentPosition.withRelativeRow(size.getRows()), character);
        drawLine(originalStart.withRelativeRow(size.getRows()), character);
        drawLine(originalStart, character);
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

    private ScreenCharacter newScreenCharacter(char character) {
        return new ScreenCharacter(character, foregroundColor, backgroundColor, activeModifiers);
    }
}
