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
package com.googlecode.lanterna.common;

import com.googlecode.lanterna.screen.TabBehaviour;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.terminal.TextColor;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;

/**
 * Created by martin on 15/07/14.
 */
public abstract class AbstractTextGraphics implements TextGraphics {

    protected TerminalPosition currentPosition;
    protected TextColor foregroundColor;
    protected TextColor backgroundColor;
    protected TabBehaviour tabBehaviour;
    protected final EnumSet<Terminal.SGR> activeModifiers;

    protected AbstractTextGraphics() {
        this.activeModifiers = EnumSet.noneOf(Terminal.SGR.class);
        this.tabBehaviour = TabBehaviour.ALIGN_TO_COLUMN_4;
        this.foregroundColor = TextColor.ANSI.DEFAULT;
        this.backgroundColor = TextColor.ANSI.DEFAULT;
        this.currentPosition = new TerminalPosition(0, 0);
    }

    @Override
    public TextGraphics setPosition(int column, int row) {
        return setPosition(new TerminalPosition(column, row));
    }

    @Override
    public TextGraphics setPosition(TerminalPosition newPosition) {
        this.currentPosition = newPosition;
        return this;
    }

    @Override
    public TerminalPosition getPosition() {
        return currentPosition;
    }

    @Override
    public TextColor getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public TextGraphics setBackgroundColor(final TextColor backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    @Override
    public TextColor getForegroundColor() {
        return foregroundColor;
    }

    @Override
    public TextGraphics setForegroundColor(final TextColor foregroundColor) {
        this.foregroundColor = foregroundColor;
        return this;
    }

    @Override
    public TextGraphics enableModifiers(Terminal.SGR... modifiers) {
        enableModifiers(Arrays.asList(modifiers));
        return this;
    }

    private void enableModifiers(Collection<Terminal.SGR> modifiers) {
        this.activeModifiers.addAll(modifiers);
    }

    @Override
    public TextGraphics disableModifiers(Terminal.SGR... modifiers) {
        disableModifiers(Arrays.asList(modifiers));
        return this;
    }

    private void disableModifiers(Collection<Terminal.SGR> modifiers) {
        this.activeModifiers.removeAll(modifiers);
    }

    @Override
    public TextGraphics clearModifiers() {
        this.activeModifiers.clear();
        return this;
    }

    @Override
    public TabBehaviour getTabBehaviour() {
        return tabBehaviour;
    }

    @Override
    public void setTabBehaviour(TabBehaviour tabBehaviour) {
        if(tabBehaviour != null) {
            this.tabBehaviour = tabBehaviour;
        }
    }

    @Override
    public void fillScreen(char c) {
        TerminalPosition savedPosition = getPosition();
        setPosition(TerminalPosition.TOP_LEFT_CORNER);
        fillRectangle(getSize(), c);
        setPosition(savedPosition);
    }

    @Override
    public void drawLine(TerminalPosition toPoint, char character) {
        drawLine(currentPosition, toPoint, character);
        currentPosition = toPoint;
    }

    private void drawLine(TerminalPosition fromPoint, TerminalPosition toPoint, char character) {
        //http://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm
        //Implementation from Graphics Programming Black Book by Michael Abrash
        //Available at http://www.gamedev.net/page/resources/_/technical/graphics-programming-and-theory/graphics-programming-black-book-r1698
        if(fromPoint.getRow() > toPoint.getRow()) {
            TerminalPosition temp = fromPoint;
            fromPoint = toPoint;
            toPoint = temp;
        }
        int deltaX = toPoint.getColumn() - fromPoint.getColumn();
        int deltaY = toPoint.getRow() - fromPoint.getRow();
        if(deltaX > 0) {
            if(deltaX > deltaY) {
                drawLine0(fromPoint, deltaX, deltaY, true, character);
            }
            else {
                drawLine1(fromPoint, deltaX, deltaY, true, character);
            }
        }
        else {
            deltaX = Math.abs(deltaX);
            if(deltaX > deltaY) {
                drawLine0(fromPoint, deltaX, deltaY, false, character);
            }
            else {
                drawLine1(fromPoint, deltaX, deltaY, false, character);
            }
        }
    }

    private void drawLine0(TerminalPosition start, int deltaX, int deltaY, boolean leftToRight, char character) {
        TextCharacter screenCharacter = newScreenCharacter(character);
        int x = start.getColumn();
        int y = start.getRow();
        int deltaYx2 = deltaY * 2;
        int deltaYx2MinusDeltaXx2 = deltaYx2 - (deltaX * 2);
        int errorTerm = deltaYx2 - deltaX;
        setCharacter(start, screenCharacter);
        while(deltaX-- > 0) {
            if(errorTerm >= 0) {
                y++;
                errorTerm += deltaYx2MinusDeltaXx2;
            }
            else {
                errorTerm += deltaYx2;
            }
            x += leftToRight ? 1 : -1;
            setCharacter(x, y, screenCharacter);
        }
    }

    private void drawLine1(TerminalPosition start, int deltaX, int deltaY, boolean leftToRight, char character) {
        TextCharacter screenCharacter = newScreenCharacter(character);
        int x = start.getColumn();
        int y = start.getRow();
        int deltaXx2 = deltaX * 2;
        int deltaXx2MinusDeltaYx2 = deltaXx2 - (deltaY * 2);
        int errorTerm = deltaXx2 - deltaY;
        setCharacter(start, screenCharacter);
        while(deltaY-- > 0) {
            if(errorTerm >= 0) {
                x += leftToRight ? 1 : -1;
                errorTerm += deltaXx2MinusDeltaYx2;
            }
            else {
                errorTerm += deltaXx2;
            }
            y++;
            setCharacter(x, y, screenCharacter);
        }
    }

    @Override
    public void drawTriangle(TerminalPosition p1, TerminalPosition p2, char character) {
        TerminalPosition originalStart = currentPosition;
        drawLine(p1, character);
        drawLine(p2, character);
        drawLine(originalStart, character);
    }

    @Override
    public void fillTriangle(TerminalPosition p1, TerminalPosition p2, char character) {
        //I've used the algorithm described here:
        //http://www-users.mat.uni.torun.pl/~wrona/3d_tutor/tri_fillers.html
        TerminalPosition[] points = new TerminalPosition[]{currentPosition, p1, p2};
        Arrays.sort(points, new Comparator<TerminalPosition>() {
            @Override
            public int compare(TerminalPosition o1, TerminalPosition o2) {
                return (o1.getRow() < o2.getRow()) ? -1 : ((o1.getRow() == o2.getRow()) ? 0 : 1);
            }
        });

        float dx1, dx2, dx3;
        if (points[1].getRow() - points[0].getRow() > 0) {
            dx1 = (float)(points[1].getColumn() - points[0].getColumn()) / (float)(points[1].getRow() - points[0].getRow());
        }
        else {
            dx1 = 0;
        }
        if (points[2].getRow() - points[0].getRow() > 0) {
            dx2 = (float)(points[2].getColumn() - points[0].getColumn()) / (float)(points[2].getRow() - points[0].getRow());
        }
        else {
            dx2 = 0;
        }
        if (points[2].getRow() - points[1].getRow() > 0) {
            dx3 = (float)(points[2].getColumn() - points[1].getColumn()) / (float)(points[2].getRow() - points[1].getRow());
        }
        else {
            dx3 = 0;
        }

        float startX, startY, endX, endY;
        startX = endX = points[0].getColumn();
        startY = endY = points[0].getRow();
        if (dx1 > dx2) {
            for (; startY <= points[1].getRow(); startY++, endY++, startX += dx2, endX += dx1) {
                drawLine(new TerminalPosition((int)startX, (int)startY), new TerminalPosition((int)endX, (int)startY), character);
            }
            endX = points[1].getColumn();
            endY = points[1].getRow();
            for (; startY <= points[2].getRow(); startY++, endY++, startX += dx2, endX += dx3) {
                drawLine(new TerminalPosition((int)startX, (int)startY), new TerminalPosition((int)endX, (int)startY), character);
            }
        } else {
            for (; startY <= points[1].getRow(); startY++, endY++, startX += dx1, endX += dx2) {
                drawLine(new TerminalPosition((int)startX, (int)startY), new TerminalPosition((int)endX, (int)startY), character);
            }
            startX = points[1].getColumn();
            startY = points[1].getRow();
            for (; startY <= points[2].getRow(); startY++, endY++, startX += dx3, endX += dx2) {
                drawLine(new TerminalPosition((int)startX, (int)startY), new TerminalPosition((int)endX, (int)startY), character);
            }
        }
    }

    @Override
    public void drawRectangle(TerminalSize size, char character) {
        TerminalPosition originalStart = currentPosition;
        drawLine(currentPosition.withRelativeColumn(size.getColumns() - 1), character);
        drawLine(currentPosition.withRelativeRow(size.getRows() - 1), character);
        drawLine(originalStart.withRelativeRow(size.getRows() - 1), character);
        drawLine(originalStart, character);
    }

    @Override
    public void fillRectangle(TerminalSize size, char character) {
        for(int y = 0; y < size.getRows(); y++) {
            for(int x = 0; x < size.getColumns(); x++) {
                setCharacter(
                    currentPosition.withRelativeColumn(x).withRelativeRow(y),
                    new TextCharacter(
                            character,
                            foregroundColor,
                            backgroundColor,
                            activeModifiers.clone()));
            }
        }
    }

    @Override
    public TextGraphics putString(String string) {
        if(string.contains("\n")) {
            string = string.substring(0, string.indexOf("\n"));
        }
        if(string.contains("\r")) {
            string = string.substring(0, string.indexOf("\r"));
        }
        string = tabBehaviour.replaceTabs(string, currentPosition.getColumn());
        for(int i = 0; i < string.length(); i++) {
            char character = string.charAt(i);
            setCharacter(
                    currentPosition.withRelativeColumn(i),
                    new TextCharacter(
                            character,
                            foregroundColor,
                            backgroundColor,
                            activeModifiers.clone()));

            //CJK characters are twice the normal characters in width
            if(CJKUtils.isCharCJK(character)) {
                i++;
            }
        }
        currentPosition = currentPosition.withRelativeColumn(string.length());
        return this;
    }

    @Override
    public TextGraphics putString(int column, int row, String string) {
        putString(new TerminalPosition(column, row), string);
        return this;
    }

    @Override
    public TextGraphics putString(TerminalPosition position, String string) {
        setPosition(position);
        putString(string);
        return this;
    }

    @Override
    public TextGraphics putString(int column, int row, String string, Terminal.SGR extraModifier, Terminal.SGR... optionalExtraModifiers) {
        putString(new TerminalPosition(column, row), string, extraModifier, optionalExtraModifiers);
        return this;
    }

    @Override
    public TextGraphics putString(TerminalPosition position, String string, Terminal.SGR extraModifier, Terminal.SGR... optionalExtraModifiers) {
        setPosition(position);
        putString(string, extraModifier, optionalExtraModifiers);
        return this;
    }

    @Override
    public TextGraphics putString(String string, Terminal.SGR extraModifier, Terminal.SGR... optionalExtraModifiers) {
        clearModifiers();
        EnumSet<Terminal.SGR> extraModifiers = EnumSet.of(extraModifier, optionalExtraModifiers);
        extraModifiers.removeAll(activeModifiers);
        enableModifiers(extraModifiers);
        putString(string);
        disableModifiers(extraModifiers);
        return this;
    }

    @Override
    public TextGraphics newTextGraphics(TerminalPosition topLeftCorner, TerminalSize size) throws IllegalArgumentException {
        TerminalSize writableArea = getSize();
        if(topLeftCorner.getColumn() + size.getColumns() > writableArea.getColumns() ||
                topLeftCorner.getRow() + size.getRows() > writableArea.getRows()) {
            throw new IllegalArgumentException("Cannot create a sub-text graphics with topLeft = " + topLeftCorner +
                    " and size = " + size + " when writable area of the current text graphics is " + writableArea);
        }
        return new SubTextGraphics(this, topLeftCorner, size);
    }

    protected void setCharacter(TerminalPosition position, TextCharacter textCharacter) {
        setCharacter(position.getColumn(), position.getRow(), textCharacter);
    }

    protected abstract void setCharacter(int columnIndex, int rowIndex, TextCharacter textCharacter);

    private TextCharacter newScreenCharacter(char character) {
        return new TextCharacter(character, foregroundColor, backgroundColor, activeModifiers);
    }
}
