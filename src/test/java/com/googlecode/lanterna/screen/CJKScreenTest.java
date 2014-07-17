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

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.terminal.TextColor;
import com.googlecode.lanterna.TestTerminalFactory;
import java.io.IOException;

/**
 *
 * @author Martin
 */
public class CJKScreenTest {
    public static void main(String[] args) throws IOException {
        Screen screen = new TestTerminalFactory(args).createScreen();
        screen.startScreen();

        TextGraphics writer = new ScreenTextGraphics(screen);
        writer.setForegroundColor(TextColor.ANSI.DEFAULT);
        writer.setBackgroundColor(TextColor.ANSI.DEFAULT);
        writer.putString(5, 5,  "Chinese (simplified):  斯瓦尔巴群岛是位于北极地区的群岛，为挪威最北界的国土范围。");
        writer.putString(5, 7,  "Chinese (traditional): 斯瓦巴群島是位於北極地區的群島，為挪威最北界的國土範圍。");
        writer.putString(5, 9,  "Japanese:              スヴァールバル諸島は、北極圏のバレンツ海にある群島。");
        writer.putString(5, 11, "Korean:                스발바르 제도 는 유럽 본토의 북부, 대서양에 위치한 군도이다.");
        screen.refresh();

        try {
            Thread.sleep(5000);
        }
        catch (InterruptedException ignored) {
        }
        screen.stopScreen();
    }
}
