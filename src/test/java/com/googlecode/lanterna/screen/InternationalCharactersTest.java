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
