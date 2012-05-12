/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.lanterna.test;

import com.googlecode.lanterna.LanternTerminal;
import com.googlecode.lanterna.LanternaException;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.ScreenCharacterStyle;
import com.googlecode.lanterna.screen.ScreenWriter;
import com.googlecode.lanterna.terminal.Terminal;

/**
 *
 * @author Martin
 */
public class BlinkTest {
    public static void main(String[] args) throws LanternaException {
        Terminal rawTerminal = new LanternTerminal().getUnderlyingTerminal();
        Screen screen = new Screen(rawTerminal);
        ScreenWriter writer = new ScreenWriter(screen);
        screen.startScreen();
        try {
            Thread.sleep(500);
        }
        catch(InterruptedException e) {}
        writer.setForegroundColor(Terminal.Color.RED);
        writer.drawString(10, 10, "Hello world!", ScreenCharacterStyle.Blinking);
        screen.refresh();
        try {
            Thread.sleep(5000);
        }
        catch(InterruptedException e) {}
        screen.stopScreen();
    }
}
