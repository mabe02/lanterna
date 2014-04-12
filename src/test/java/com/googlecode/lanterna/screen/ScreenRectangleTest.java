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

import com.googlecode.lanterna.TestTerminalFactory;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.terminal.TextColor;
import java.io.IOException;
import java.util.Random;

/**
 *
 * @author martin
 */
public class ScreenRectangleTest {
    public static void main(String[] args) throws IOException {
        boolean useAnsiColors = false;
        for(String arg: args) {
            if(arg.equals("--ansi-colors")) {
                useAnsiColors = true;
            }
        }
        DefaultScreen screen = new TestTerminalFactory(args).createScreen();
        screen.startScreen();

        ScreenWriter writer = new ScreenWriter(screen);
        Random random = new Random();
        
        long startTime = System.currentTimeMillis();
        while(System.currentTimeMillis() - startTime < 1000 * 20) {
            KeyStroke keyStroke = screen.readInput();
            if(keyStroke != null && keyStroke.getKeyType() == KeyType.Escape) {
                break;
            }
            screen.doResizeIfNecessary();
            TerminalSize size = screen.getTerminalSize();
            TextColor color;
            if(useAnsiColors) {
                color = TextColor.ANSI.fromTerminalANSIColor(Terminal.ANSIColor.values()[random.nextInt(Terminal.ANSIColor.values().length)]);
            }
            else {
                //Draw a rectangle in random indexed color
                color = new TextColor.Indexed(random.nextInt(256));
            }
            
            TerminalPosition topLeft = new TerminalPosition(random.nextInt(size.getColumns()), random.nextInt(size.getRows()));
            TerminalSize rectangleSize = new TerminalSize(random.nextInt(size.getColumns() - topLeft.getColumn() + 1), random.nextInt(size.getRows() - topLeft.getRow() + 1));
            
            writer.setBackgroundColor(color);
            writer.setPosition(topLeft);
            writer.fillRectangle(rectangleSize, ' ');
            screen.refresh(Screen.RefreshType.DELTA);
        }
        screen.stopScreen();
    }
}
