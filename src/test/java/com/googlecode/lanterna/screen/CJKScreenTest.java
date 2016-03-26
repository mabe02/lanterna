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

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.TestTerminalFactory;
import java.io.IOException;

/**
 * Tests the terminal capability of displaying Chinese (simplified and traditional), Japanese (katakana, hiragana,
 * kanji) and Korean
 * @author Martin
 */
public class CJKScreenTest {
    public static void main(String[] args) throws IOException {
        Screen screen = new TestTerminalFactory(args).createScreen();
        screen.startScreen();

        TextGraphics writer = new ScreenTextGraphics(screen);
        writer.setForegroundColor(TextColor.ANSI.DEFAULT);
        writer.setBackgroundColor(TextColor.ANSI.DEFAULT);
        writer.putString(4, 2,  "Chinese (simplified):  石室诗士施氏，嗜狮，誓食十狮。");
        writer.putString(4, 3,  "                       氏时时适市视狮。");
        writer.putString(4, 5,  "Chinese (traditional): 石室詩士施氏，嗜獅，誓食十獅。 ");
        writer.putString(4, 6,  "                       氏時時適市視獅。");
        writer.putString(4, 8,  "Japanese:              祇園精舎の鐘の声、諸行無常の響あり。");
        writer.putString(4, 9,  "                       沙羅双樹の花の色、盛者必衰の理をあらはす");
        writer.putString(4, 11, "  (katakana)           ランターナ バージョンアップ！");
        writer.putString(4, 12, "  (half-width)         ﾗﾝﾀｰﾅ ﾊﾞｰｼﾞｮﾝｱｯﾌﾟ");
        writer.putString(4, 14, "Korean:                내 벗이 몇인가하니 수석과 송죽이라");
        writer.putString(4, 15, "                       동산에 달오르니 그 더욱 반갑도다");
        writer.putString(4, 16, "                       두어라, 이 다섯 밖에 또 더해야 무엇하리");
        screen.refresh();

        screen.readInput();
        screen.stopScreen();
    }
}
