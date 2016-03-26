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
package com.googlecode.lanterna.terminal;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.AbstractTextGraphics;
import com.googlecode.lanterna.graphics.DoublePrintingTextGraphics;
import com.googlecode.lanterna.graphics.TextGraphics;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * This class tests the Terminal-implementation of TextGraphics
 * @author Martin
 */
public class TerminalTextGraphicsTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        Terminal terminal = new TestTerminalFactory(args).createTerminal();
        TextGraphics textGraphics = terminal.newTextGraphics();
        if((args.length > 0 && args[0].equals("--square")) ||
                (args.length > 1 && args[1].equals("--square"))) {
            textGraphics = new DoublePrintingTextGraphics((AbstractTextGraphics)textGraphics);
        }
        textGraphics.setForegroundColor(TextColor.ANSI.BLUE);
        textGraphics.putString(3, 3, "Hello World!");
        textGraphics.setForegroundColor(TextColor.ANSI.CYAN);
        TerminalPosition lineStart = new TerminalPosition(3 + "Hello World!".length(), 3);
        textGraphics.drawLine(lineStart, lineStart.withRelativeColumn(2).withRelativeRow(6), Symbols.BLOCK_SOLID);
        textGraphics.setForegroundColor(TextColor.ANSI.RED);
        textGraphics.drawRectangle(lineStart.withRelativeColumn(2).withRelativeRow(6), new TerminalSize(5, 3), Symbols.BULLET);
        textGraphics.setForegroundColor(TextColor.ANSI.MAGENTA);
        TerminalPosition triangleStart = lineStart.withRelativeColumn(7).withRelativeRow(9);
        textGraphics.drawTriangle(
                triangleStart,
                triangleStart.withColumn(0).withRelativeRow(-1),
                triangleStart.withColumn(5).withRelativeRow(3),
                Symbols.SPADES);
        textGraphics.setForegroundColor(TextColor.ANSI.YELLOW);
        textGraphics.fillRectangle(new TerminalPosition(30, 1), new TerminalSize(8, 5), Symbols.DIAMOND);
        textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
        triangleStart = new TerminalPosition(30, 6);
        textGraphics.fillTriangle(
                triangleStart,
                triangleStart.withRelativeRow(5).withRelativeColumn(-2),
                triangleStart.withRelativeRow(5).withRelativeColumn(4),
                Symbols.CLUB);

        terminal.resetColorAndSGR();
        terminal.flush();

        Thread.sleep(4000);

        if(terminal instanceof Window) {
            ((Window)terminal).dispose();
        }
    }
}
