/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.lanterna.issue;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.screen.DefaultScreen;
import com.googlecode.lanterna.terminal.swing.OldSwingTerminal;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 *
 * @author mberglun
 */
public class Issue95 {
    public static void main(String[] args) throws InterruptedException, IOException {
        OldSwingTerminal terminal = new OldSwingTerminal(80, 20);
        terminal.setCursorVisible(false);

        DefaultScreen screen = TerminalFacade.createScreen(terminal);
        screen.startScreen();

        JFrame frame = terminal.getJFrame();
        frame.setTitle("Freedom: An arena-battle roguelike");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);

        while(screen.readInput() == null) {
            if(screen.doResizeIfNecessary() != null) {
                screen.refresh();
            }
            Thread.sleep(100);
        }
        screen.stopScreen();
    }
}
