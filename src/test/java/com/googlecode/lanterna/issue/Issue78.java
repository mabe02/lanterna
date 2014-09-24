/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.lanterna.issue;

import com.googlecode.lanterna.TestTerminalFactory;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.Terminal;
import java.io.IOException;

/**
 *
 * @author martin
 */
public class Issue78 {
    public static void main(String[] args) throws IOException {
        Terminal t = new TestTerminalFactory(args).createTerminal();
        t.enterPrivateMode();
        TerminalScreen s = new TerminalScreen(t);
        s.startScreen();
        try {
            Thread.sleep(1000);
        }
        catch(InterruptedException e) {}
        s.stopScreen();
        t.exitPrivateMode();
    }
}
