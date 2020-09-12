package com.googlecode.lanterna.terminal;

import com.googlecode.lanterna.TestTerminalFactory;

import java.io.IOException;

public class EmojiTest {
    public static void main(String[] args) throws IOException {
        Terminal terminal = new TestTerminalFactory(args).setForceTextTerminal(true).createTerminal();
        terminal.putCharacter('\uD83C');
        terminal.putCharacter('\uDF55');
        terminal.putCharacter('\n');
        System.out.println("\uD83C\uDF55");
    }
}
