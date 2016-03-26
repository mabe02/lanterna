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

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TestTerminalFactory;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

/**
 * Created by martin on 13/03/16.
 */
public class SimpleScreenTest {

    private static final TextColor[] COLORS_TO_CYCLE = new TextColor[] {
            TextColor.ANSI.BLACK,
            TextColor.ANSI.WHITE,
            TextColor.ANSI.BLUE,
            TextColor.ANSI.CYAN,
            TextColor.ANSI.GREEN,
            TextColor.ANSI.MAGENTA,
            TextColor.ANSI.RED,
            TextColor.ANSI.YELLOW,
    };

    public static void main(String[] args) throws IOException {
        Terminal terminal = new TestTerminalFactory(args).createTerminal();
        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();
        screen.refresh();

        TextGraphics textGraphics = screen.newTextGraphics();

        int foregroundCycle = 1;
        int backgroundCycle = 0;

        mainLoop:
        while(true) {
            KeyStroke keyStroke = screen.readInput();
            switch(keyStroke.getKeyType()) {
                case EOF:
                case Escape:
                    break mainLoop;

                case ArrowUp:
                    screen.setCursorPosition(screen.getCursorPosition().withRelativeRow(-1));
                    break;

                case ArrowDown:
                    screen.setCursorPosition(screen.getCursorPosition().withRelativeRow(1));
                    break;

                case ArrowLeft:
                    screen.setCursorPosition(screen.getCursorPosition().withRelativeColumn(-1));
                    break;

                case ArrowRight:
                    screen.setCursorPosition(screen.getCursorPosition().withRelativeColumn(1));
                    break;

                case Character:
                    if(keyStroke.isCtrlDown()) {
                        switch(keyStroke.getCharacter()) {
                            case 'k':
                                screen.setCharacter(screen.getCursorPosition(), new TextCharacter('桜', COLORS_TO_CYCLE[foregroundCycle], COLORS_TO_CYCLE[backgroundCycle]));
                                screen.setCursorPosition(screen.getCursorPosition().withRelativeColumn(2));
                                break;

                            case 'f':
                                foregroundCycle++;
                                if(foregroundCycle >= COLORS_TO_CYCLE.length) {
                                    foregroundCycle = 0;
                                }
                                break;

                            case 'b':
                                backgroundCycle++;
                                if(backgroundCycle >= COLORS_TO_CYCLE.length) {
                                    backgroundCycle = 0;
                                }
                                break;
                        }
                        if(COLORS_TO_CYCLE[foregroundCycle] != TextColor.ANSI.BLACK) {
                            textGraphics.setBackgroundColor(TextColor.ANSI.BLACK);
                        }
                        else {
                            textGraphics.setBackgroundColor(TextColor.ANSI.WHITE);
                        }
                        textGraphics.setForegroundColor(COLORS_TO_CYCLE[foregroundCycle]);
                        textGraphics.putString(0, screen.getTerminalSize().getRows() - 2, "Foreground color");

                        if(COLORS_TO_CYCLE[backgroundCycle] != TextColor.ANSI.BLACK) {
                            textGraphics.setBackgroundColor(TextColor.ANSI.BLACK);
                        }
                        else {
                            textGraphics.setBackgroundColor(TextColor.ANSI.WHITE);
                        }
                        textGraphics.setForegroundColor(COLORS_TO_CYCLE[backgroundCycle]);
                        textGraphics.putString(0, screen.getTerminalSize().getRows() - 1, "Background color");
                    }
                    else {
                        screen.setCharacter(screen.getCursorPosition(), new TextCharacter(keyStroke.getCharacter(), COLORS_TO_CYCLE[foregroundCycle], COLORS_TO_CYCLE[backgroundCycle]));
                        screen.setCursorPosition(screen.getCursorPosition().withRelativeColumn(1));
                        break;
                    }
            }

            screen.refresh();
        }

        screen.stopScreen();
    }
}
