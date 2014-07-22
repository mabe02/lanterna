package com.googlecode.lanterna.terminal;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.terminal.ansi.UnixTerminal;

import java.io.IOException;

/**
 * Test for the somewhat supported "CSI 8 ; rows ; columns; t" command
 */
public class ExtendedTerminalTests {
    public static void main(String[] args) throws IOException, InterruptedException {
        UnixTerminal unixTerminal = new UnixTerminal();
        unixTerminal.clearScreen();
        TextGraphics textGraphics = unixTerminal.newTextGraphics();
        textGraphics.setForegroundColor(TextColor.ANSI.CYAN);
        textGraphics.putString(4, 4, "Please wait four seconds and the terminal will be resized", SGR.BLINK);
        unixTerminal.flush();
        Thread.sleep(4 * 1000);
        unixTerminal.setTerminalSize(80, 40);
        unixTerminal.clearScreen();
        textGraphics.putString(4, 4, "There, did anything happen? Will set a title in 3 seconds...");
        unixTerminal.flush();
        Thread.sleep(3 * 1000);
        unixTerminal.setTitle("ExtendedTerminalTests");
        unixTerminal.clearScreen();
        textGraphics.putString(4, 4, "Check the title, did it change?");
        textGraphics.setForegroundColor(TextColor.ANSI.RED);
        textGraphics.putString(0, textGraphics.getSize().getRows() - 1, "Will terminate in 3 seconds...", SGR.BLINK);
        unixTerminal.setCursorPosition(0, 0);
        unixTerminal.flush();
        Thread.sleep(3 * 1000);
        unixTerminal.clearScreen();
        unixTerminal.resetColorAndSGR();
        unixTerminal.flush();

        //Drain the input queue (could hold the size reply)
        while(unixTerminal.readInput() != null);
    }
}
