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
package com.googlecode.lanterna.terminal;

import com.googlecode.lanterna.TestTerminalFactory;
import com.googlecode.lanterna.graphics.AbstractTextGraphics;
import com.googlecode.lanterna.graphics.DoublePrintingTextGraphics;
import com.googlecode.lanterna.graphics.TextGraphics;

import javax.swing.*;
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
        textGraphics.drawLine(textGraphics.getPosition().withRelativeColumn(2).withRelativeRow(6), ACS.BLOCK_SOLID);
        textGraphics.setForegroundColor(TextColor.ANSI.RED);
        textGraphics.drawRectangle(new TerminalSize(5, 3), ACS.BULLET);
        textGraphics.setForegroundColor(TextColor.ANSI.MAGENTA);
        textGraphics.drawTriangle(
                textGraphics.getPosition().withColumn(0).withRelativeRow(-1),
                textGraphics.getPosition().withColumn(5).withRelativeRow(3),
                ACS.SPADES);
        textGraphics.setPosition(30, 1);
        textGraphics.setForegroundColor(TextColor.ANSI.YELLOW);
        textGraphics.fillRectangle(new TerminalSize(8, 5), ACS.DIAMOND);
        textGraphics.movePosition(0, 5);
        textGraphics.setForegroundColor(TextColor.ANSI.GREEN);
        textGraphics.fillTriangle(
                textGraphics.getPosition().withRelativeRow(5).withRelativeColumn(-2),
                textGraphics.getPosition().withRelativeRow(5).withRelativeColumn(4),
                ACS.CLUB);

        terminal.resetColorAndSGR();
        terminal.flush();

        Thread.sleep(4000);

        if(terminal instanceof JFrame) {
            ((JFrame)terminal).dispose();
        }
    }
}
