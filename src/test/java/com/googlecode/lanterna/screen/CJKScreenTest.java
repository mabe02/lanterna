/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.lanterna.screen;

import com.googlecode.lanterna.screen.DefaultScreen;
import com.googlecode.lanterna.screen.ScreenWriter;
import com.googlecode.lanterna.terminal.TextColor;
import com.googlecode.lanterna.TestTerminalFactory;
import java.io.IOException;

/**
 *
 * @author Martin
 */
public class CJKScreenTest {
    public static void main(String[] args) throws IOException {
        DefaultScreen screen = new TestTerminalFactory(args).createScreen();
        screen.startScreen();

        ScreenWriter writer = new ScreenWriter(screen);
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
        catch (InterruptedException e) {
        }
        screen.stopScreen();
    }
}
