package com.googlecode.lanterna.terminal;

import com.googlecode.lanterna.TestTerminalFactory;

import java.io.IOException;

public class EmojiTest {
    public static void main(String[] args) throws IOException {
        System.out.println("\uD83E\uDD26\uD83C\uDFFC\u200D".codePoints().count());
        System.out.println("\uD83C\uDF55");
        System.out.println("\uD83C");
        System.out.println(Character.isSurrogate("\uD83C".charAt(0)));
        System.out.println("\uD83C\uDF55".length());
        System.out.println("あ".codePoints().count());
        System.out.println("บุ".codePoints().count());
        System.out.println("บุ".toCharArray().length);
        System.out.print("\\u" + Integer.toHexString("บุ".toCharArray()[0]));
        System.out.println("\\u" + Integer.toHexString("บุ".toCharArray()[1]));
        System.out.println("\uD83C\uDF55".codePoints().count());
    }
}
