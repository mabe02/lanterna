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
import com.googlecode.lanterna.graphics.AbstractTextGraphics;
import com.googlecode.lanterna.graphics.DoublePrintingTextGraphics;
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
public class ScreenTriangleTest {

    private static final double oneThirdOf2PI = (Math.PI * 2.0) / 3.0;
    private static final double twoThirdsOf2PI = oneThirdOf2PI * 2.0;

    public static void main(String[] args) throws IOException, InterruptedException {
        boolean useAnsiColors = false;
        boolean useFilled = false;
        boolean slow = false;
        boolean rotating = false;
        boolean square = false;
        for(String arg : args) {
            if(arg.equals("--ansi-colors")) {
                useAnsiColors = true;
            }
            if(arg.equals("--filled")) {
                useFilled = true;
            }
            if(arg.equals("--slow")) {
                slow = true;
            }
            if(arg.equals("--rotating")) {
                rotating = true;
            }
            if(arg.equals("--square")) {
                square = true;
            }
        }
        Screen screen = new TestTerminalFactory(args).createScreen();
        screen.startScreen();

        TextGraphics graphics = new ScreenTextGraphics(screen);
        if(square) {
            graphics = new DoublePrintingTextGraphics((AbstractTextGraphics)graphics);
        }
        Random random = new Random();

        TextColor color = null;
        double rad = 0.0;
        while(true) {
            KeyStroke keyStroke = screen.pollInput();
            if(keyStroke != null &&
                    (keyStroke.getKeyType() == KeyType.Escape || keyStroke.getKeyType() == KeyType.EOF)) {
                break;
            }
            screen.doResizeIfNecessary();
            TerminalSize size = graphics.getSize();
            if(useAnsiColors) {
                if(color == null || !rotating) {
                    color = TextColor.ANSI.values()[random.nextInt(TextColor.ANSI.values().length)];
                }
            }
            else {
                if(color == null || !rotating) {
                    //Draw a rectangle in random indexed color
                    color = new TextColor.Indexed(random.nextInt(256));
                }
            }

            TerminalPosition p1;
            TerminalPosition p2;
            TerminalPosition p3;
            if(rotating) {
                screen.clear();
                double triangleSize = 15.0;
                int x0 = (size.getColumns() / 2) + (int) (Math.cos(rad) * triangleSize);
                int y0 = (size.getRows() / 2) + (int) (Math.sin(rad) * triangleSize);
                int x1 = (size.getColumns() / 2) + (int) (Math.cos(rad + oneThirdOf2PI) * triangleSize);
                int y1 = (size.getRows() / 2) + (int) (Math.sin(rad + oneThirdOf2PI) * triangleSize);
                int x2 = (size.getColumns() / 2) + (int) (Math.cos(rad + twoThirdsOf2PI) * triangleSize);
                int y2 = (size.getRows() / 2) + (int) (Math.sin(rad + twoThirdsOf2PI) * triangleSize);
                p1 = new TerminalPosition(x0, y0);
                p2 = new TerminalPosition(x1, y1);
                p3 = new TerminalPosition(x2, y2);
                rad += Math.PI / 90.0;
            }
            else {
                p1 = new TerminalPosition(random.nextInt(size.getColumns()), random.nextInt(size.getRows()));
                p2 = new TerminalPosition(random.nextInt(size.getColumns()), random.nextInt(size.getRows()));
                p3 = new TerminalPosition(random.nextInt(size.getColumns()), random.nextInt(size.getRows()));
            }

            graphics.setBackgroundColor(color);
            if(useFilled) {
                graphics.fillTriangle(p1, p2, p3, ' ');
            }
            else {
                graphics.drawTriangle(p1, p2, p3, ' ');
            }
            screen.refresh(Screen.RefreshType.DELTA);
            if(slow) {
                Thread.sleep(500);
            }
        }
        screen.stopScreen();
    }
}
