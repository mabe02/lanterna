/*
 * This file is part of lanterna (https://github.com/mabe02/lanterna).
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
 * Copyright (C) 2010-2020 Martin Berglund
 */
package com.googlecode.lanterna.screen;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.BasicTextImage;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.graphics.TextImage;

import java.io.IOException;

/**
 * Test to try out drawImage in TextGraphics
 */
public class DrawImageTest {
    public static void main(String[] args) throws IOException {
        //Setup a standard Screen
        Screen screen = new TestTerminalFactory(args).createScreen();
        screen.startScreen();
        screen.setCursorPosition(null);

        //Create an 'image' that we fill with recognizable characters
        TextImage image = new BasicTextImage(5, 5);
        TextCharacter imageCharacter = new TextCharacter('X');
        TextGraphics textGraphics = image.newTextGraphics();
        textGraphics.drawRectangle(
                TerminalPosition.OF_0x0,
                new TerminalSize(5, 5),
                imageCharacter.withBackgroundColor(TextColor.ANSI.RED));
        textGraphics.drawRectangle(
                TerminalPosition.OF_1x1,
                new TerminalSize(3, 3),
                imageCharacter.withBackgroundColor(TextColor.ANSI.MAGENTA));
        textGraphics.setCharacter(2, 2,
                imageCharacter.withBackgroundColor(TextColor.ANSI.CYAN));

        TextGraphics screenGraphics = screen.newTextGraphics();
        screenGraphics.setBackgroundColor(TextColor.Indexed.fromRGB(50, 50, 50));
        screenGraphics.fill(' ');
        screenGraphics.drawImage(TerminalPosition.of(1, 1), image);
        screenGraphics.drawImage(TerminalPosition.of(8, 1), image, TerminalPosition.of(0, 0), image.getSize().withRelativeColumns(-4));
        screenGraphics.drawImage(TerminalPosition.of(10, 1), image, TerminalPosition.of(0, 0), image.getSize().withRelativeColumns(-3));
        screenGraphics.drawImage(TerminalPosition.of(13, 1), image, TerminalPosition.of(0, 0), image.getSize().withRelativeColumns(-2));
        screenGraphics.drawImage(TerminalPosition.of(17, 1), image, TerminalPosition.of(0, 0), image.getSize().withRelativeColumns(-1));
        screenGraphics.drawImage(TerminalPosition.of(22, 1), image);
        screenGraphics.drawImage(TerminalPosition.of(28, 1), image, TerminalPosition.of(1, 0), image.getSize());
        screenGraphics.drawImage(TerminalPosition.of(33, 1), image, TerminalPosition.of(2, 0), image.getSize());
        screenGraphics.drawImage(TerminalPosition.of(37, 1), image, TerminalPosition.of(3, 0), image.getSize());
        screenGraphics.drawImage(TerminalPosition.of(40, 1), image, TerminalPosition.of(4, 0), image.getSize());

        //Try to draw bigger than the image size, this should ignore the extra size
        screenGraphics.drawImage(TerminalPosition.of(1, 7), image, TerminalPosition.of(0, 0), image.getSize().withRelativeColumns(10));

        //0 size should draw nothing
        screenGraphics.drawImage(TerminalPosition.of(8, 7), image, TerminalPosition.of(0, 0), TerminalSize.ZERO);

        //Drawing with a negative source image offset will move the target position
        screenGraphics.drawImage(TerminalPosition.of(8, 7), image, TerminalPosition.of(-2, -2), image.getSize());

        screen.refresh();
        screen.readInput();
        screen.stopScreen();
    }
}
