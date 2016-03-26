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
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;

import java.io.IOException;

/**
 * Tests the terminal capability of displaying international character sets. Please note that CJK characters are not
 * here but in CJKScreenTest
 *
 * Created by martin on 05/11/14.
 */
public class InternationalCharactersTest {
    public static void main(String[] args) throws IOException {
        Screen screen = new TestTerminalFactory(args).createScreen();
        screen.startScreen();

        TextGraphics writer = new ScreenTextGraphics(screen);
        writer.setForegroundColor(TextColor.ANSI.DEFAULT);
        writer.setBackgroundColor(TextColor.ANSI.DEFAULT);
        writer.putString(4, 2,  "Armenian:   Ճանաչել զիմաստութիւն եւ զխրատ, իմանալ զբանս հանճարոյ");
        writer.putString(4, 3,  "Greek:      μὴ μου τοὺς κύκλους τάραττε");
        writer.putString(4, 4,  "Hebrew:     סְבָאלְבָּרְד הוא ארכיפלג הנמצא באוקיינוס הארקטי");
        writer.putString(4, 5,  "Icelandic:  þungur hnífur   þessi hnífur á að vera þungur");
        writer.putString(4, 6,  "Persian:    آنان که محیط فضل و آداب شدند  در جمع کمال شمع اصحاب شدند");
        writer.putString(4, 7,  "Russian:    Запорізькі козаки турецькому султану!");
        writer.putString(4, 8,  "Swedish:    Flygande bäckasiner söka hwila på mjuka tuvor");
        writer.putString(4, 9,  "Thai:       เสียงฦๅเสียงเล่าอ้าง    อันใด พี่เอย");
        screen.refresh();
        screen.readInput();
        screen.stopScreen();
    }
}
