/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.lanterna.test.issue;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.screen.DefaultScreen;
import com.googlecode.lanterna.terminal.Terminal;
import java.io.IOException;

/**
 *
 * @author martin
 */
public class Issue78 {
    public static void main(String[] args) throws IOException {
        Terminal t = TerminalFacade.createTextTerminal();
        t.enterPrivateMode();
        DefaultScreen s = new DefaultScreen(t);
        s.startScreen();
        try {
            Thread.sleep(1000);
        }
        catch(InterruptedException e) {}
        s.stopScreen();
        t.exitPrivateMode();
    }
}
