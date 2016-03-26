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
public class ScreenRectangleTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        boolean useAnsiColors = false;
        boolean useFilled = false;
        boolean slow = false;
        for(String arg: args) {
            if(arg.equals("--ansi-colors")) {
                useAnsiColors = true;
            }
            if(arg.equals("--filled")) {
                useFilled = true;
            }
            if(arg.equals("--slow")) {
                slow = true;
            }
        }
        Screen screen = new TestTerminalFactory(args).createScreen();
        screen.startScreen();

        TextGraphics textGraphics = new ScreenTextGraphics(screen);
        Random random = new Random();

        long startTime = System.currentTimeMillis();
        while(System.currentTimeMillis() - startTime < 1000 * 20) {
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

            TerminalPosition topLeft = new TerminalPosition(random.nextInt(size.getColumns()), random.nextInt(size.getRows()));
            TerminalSize rectangleSize = new TerminalSize(random.nextInt(size.getColumns() - topLeft.getColumn()), random.nextInt(size.getRows() - topLeft.getRow()));

            textGraphics.setBackgroundColor(color);
            if(useFilled) {
                textGraphics.fillRectangle(topLeft, rectangleSize, ' ');
            }
            else {
                textGraphics.drawRectangle(topLeft, rectangleSize, ' ');
            }
            screen.refresh(Screen.RefreshType.DELTA);
            if(slow) {
                Thread.sleep(500);
            }
        }
        screen.stopScreen();
    }
}
