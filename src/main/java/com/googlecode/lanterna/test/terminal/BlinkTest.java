/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.lanterna.test.terminal;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.terminal.Terminal;

/**
 *
 * @author Martin
 */
public class BlinkTest {
    public static void main(String[] args) {
        Terminal rawTerminal = TerminalFacade.createTerminal();
        rawTerminal.enterPrivateMode();
        rawTerminal.clearScreen();
        rawTerminal.applyForegroundColor(Terminal.Color.RED);
        rawTerminal.applySGR(Terminal.SGR.ENTER_BLINK);
        rawTerminal.moveCursor(10, 10);
        rawTerminal.putCharacter('H');
        rawTerminal.putCharacter('e');
        rawTerminal.putCharacter('l');
        rawTerminal.putCharacter('l');
        rawTerminal.putCharacter('o');
        rawTerminal.putCharacter('!');
        rawTerminal.moveCursor(0, 0);
        rawTerminal.flush();
        try {
            Thread.sleep(5000);
        }
        catch(InterruptedException e) {}
        rawTerminal.exitPrivateMode();
    }
}
