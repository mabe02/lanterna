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
 * Copyright (C) 2010-2016 Martin
 */
package com.googlecode.lanterna.screen;

import com.googlecode.lanterna.TestTerminalFactory;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import java.io.IOException;
import java.util.Random;

/**
 *
 * @author martin
 */
public class ScreenLineTest {
    private static TerminalPosition CIRCLE_LAST_POSITION = null;
    public static void main(String[] args) throws IOException, InterruptedException {
        boolean useAnsiColors = false;
        boolean slow = false;
        boolean circle = false;
        for(String arg: args) {
            if(arg.equals("--ansi-colors")) {
                useAnsiColors = true;
            }
            if(arg.equals("--slow")) {
                slow = true;
            }
            if(arg.equals("--circle")) {
                circle = true;
            }
        }
        Screen screen = new TestTerminalFactory(args).createScreen();
        screen.startScreen();

        TextGraphics textGraphics = new ScreenTextGraphics(screen);
        Random random = new Random();
        while(true) {
            KeyStroke keyStroke = screen.pollInput();
            if(keyStroke != null &&
                    (keyStroke.getKeyType() == KeyType.Escape || keyStroke.getKeyType() == KeyType.EOF)) {
                break;
            }
            screen.doResizeIfNecessary();
            TerminalSize size = textGraphics.getSize();
            TextColor color;
            if(useAnsiColors) {
                color = TextColor.ANSI.values()[random.nextInt(TextColor.ANSI.values().length)];
            }
            else {
                //Draw a rectangle in random indexed color
                color = new TextColor.Indexed(random.nextInt(256));
            }

            TerminalPosition p1;
            TerminalPosition p2;
            if(circle) {
                p1 = new TerminalPosition(size.getColumns() / 2, size.getRows() / 2);
                if(CIRCLE_LAST_POSITION == null) {
                    CIRCLE_LAST_POSITION = new TerminalPosition(0, 0);
                }
                else if(CIRCLE_LAST_POSITION.getRow() == 0) {
                    if(CIRCLE_LAST_POSITION.getColumn() < size.getColumns() - 1) {
                        CIRCLE_LAST_POSITION = CIRCLE_LAST_POSITION.withRelativeColumn(1);
                    }
                    else {
                        CIRCLE_LAST_POSITION = CIRCLE_LAST_POSITION.withRelativeRow(1);
                    }
                }
                else if(CIRCLE_LAST_POSITION.getRow() < size.getRows() - 1) {
                    if(CIRCLE_LAST_POSITION.getColumn() == 0) {
                        CIRCLE_LAST_POSITION = CIRCLE_LAST_POSITION.withRelativeRow(-1);
                    }
                    else {
                        CIRCLE_LAST_POSITION = CIRCLE_LAST_POSITION.withRelativeRow(1);
                    }
                }
                else {
                    if(CIRCLE_LAST_POSITION.getColumn() > 0) {
                        CIRCLE_LAST_POSITION = CIRCLE_LAST_POSITION.withRelativeColumn(-1);
                    }
                    else {
                        CIRCLE_LAST_POSITION = CIRCLE_LAST_POSITION.withRelativeRow(-1);
                    }
                }
                p2 = CIRCLE_LAST_POSITION;
            }
            else {
                p1 = new TerminalPosition(random.nextInt(size.getColumns()), random.nextInt(size.getRows()));
                p2 = new TerminalPosition(random.nextInt(size.getColumns()), random.nextInt(size.getRows()));
            }
            textGraphics.setBackgroundColor(color);
            textGraphics.drawLine(p1, p2, ' ');
            textGraphics.setBackgroundColor(TextColor.ANSI.BLACK);
            textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
            textGraphics.putString(4, size.getRows() - 1, "P1 " + p1 + " -> P2 " + p2);
            screen.refresh(Screen.RefreshType.DELTA);
            if(slow) {
                Thread.sleep(500);
            }
        }
        screen.stopScreen();
    }
}
