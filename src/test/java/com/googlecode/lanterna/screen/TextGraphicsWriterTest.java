package com.googlecode.lanterna.screen;

import java.io.IOException;

import com.googlecode.lanterna.TestTerminalFactory;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.graphics.TextGraphicsWriter;

public class TextGraphicsWriterTest {
    public static void main(String[] args) throws IOException {
        Screen screen = new TestTerminalFactory(args).createScreen();
        screen.startScreen();

        TextGraphics writer = new ScreenTextGraphics(screen);
        writer.setForegroundColor(TextColor.ANSI.DEFAULT);
        writer.setBackgroundColor(TextColor.ANSI.BLUE);
        writer.fill(' ');
        TextGraphicsWriter tw = new TextGraphicsWriter(writer);

        tw.putString("\n\033[4;1m");
        tw.putString("Chinese \033[33m(simplified)\033[39m: \t石室诗士施氏，嗜狮，誓食十狮。\n" +
                     "                      \t氏时时适市视狮。\n" +
                     "Chinese \033[33m(traditional)\033[39m:\t石室詩士施氏，嗜獅，誓食十獅。 \n" +
                     "                      \t氏時時適市視獅。\n");
        tw.putString("Japanese:             \t祇園精舎の鐘の声、諸行無常の響あり。\n" +
                     "                      \t沙羅双樹の花の色、盛者必衰の理をあらはす\n" +
                     "  \033[33m(katakana)\033[39m          \tランターナ バージョンアップ！\n" +
                     "  \033[33m(half-width)\033[39m        \tﾗﾝﾀｰﾅ ﾊﾞｰｼﾞｮﾝｱｯﾌﾟ\n" +
                     "Korean:               \t내 벗이 몇인가하니 수석과 송죽이라\n" +
                     "                      \t동산에 달오르니 그 더욱 반갑도다\n" +
                     "                      \t두어라, 이 다섯 밖에 또 더해야 무엇하리\n");
        tw.putString("still underlined, \033[31mbut now red\033[0m - reset.");
        screen.refresh();

        screen.readInput();
        screen.stopScreen();
    }
}
