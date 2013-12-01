/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.lanterna.test.issue;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;

/**
 *
 * @author martin
 */
public class Issue78 {
    public static void main(String[] args) {
        Terminal t = TerminalFacade.createTextTerminal();
        t.enterPrivateMode();
        Screen s = new Screen(t);
        s.startScreen();
        try {
            Thread.sleep(1000);
        }
        catch(InterruptedException e) {}
        s.stopScreen();
        t.exitPrivateMode();
    }
}
