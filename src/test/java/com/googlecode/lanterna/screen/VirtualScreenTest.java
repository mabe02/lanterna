package com.googlecode.lanterna.screen;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TestTerminalFactory;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.io.IOException;

/**
 * Test for VirtualScreen class
 * @author Martin
 */
public class VirtualScreenTest {

    public static void main(String[] args) throws InterruptedException, IOException {
        new VirtualScreenTest(args);
    }

    private Screen screen;

    public VirtualScreenTest(String[] args) throws InterruptedException, IOException {
        screen = new TestTerminalFactory(args).createScreen();
        screen = new VirtualScreen(screen);
        screen.startScreen();

        TextGraphics textGraphics = screen.newTextGraphics();
        textGraphics.setBackgroundColor(TextColor.ANSI.GREEN);
        textGraphics.setPosition(40, 0).fillTriangle(new TerminalPosition(25,19), new TerminalPosition(65, 19), ' ');
        textGraphics.setBackgroundColor(TextColor.ANSI.RED);
        textGraphics.setPosition(0, 0);
        textGraphics.drawRectangle(screen.getTerminalSize(), ' ');
        screen.refresh();

        while(true) {
            KeyStroke keyStroke = screen.pollInput();
            if(keyStroke != null) {
                if(keyStroke.getKeyType() == KeyType.Escape) {
                    break;
                }
            }
            else if(screen.doResizeIfNecessary() != null) {
                screen.refresh();
            }
            else {
                Thread.sleep(1);
            }
        }
        screen.stopScreen();
    }
}
