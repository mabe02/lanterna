package com.googlecode.lanterna.terminal;

import com.googlecode.lanterna.TestTerminalFactory;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;

import java.io.IOException;

public class UnicodeTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        Terminal terminal = new TestTerminalFactory(args).createTerminal();
        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();
        screen.newTextGraphics().putString(4, 2, "Hello!");
        screen.setCharacter(4, 2, TextCharacter.fromString("·")[0]);
        screen.refresh();
        Thread.sleep(5000);
        screen.stopScreen();
    }
}
