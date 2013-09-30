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
    public static void main(String[] args) throws InterruptedException {
        Terminal terminal = TerminalFacade.createTextTerminal();
        Screen screen = new Screen(terminal);
        Thread.sleep(1000);
        while(terminal.readInput() != null) {
            ;
        }
    }
}
