/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.lanterna.test.issue;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.swing.SwingTerminal;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 *
 * @author mberglun
 */
public class Issue95 {
    public static void main(String[] args) throws InterruptedException {
        SwingTerminal terminal = new SwingTerminal(80, 20);		
        terminal.setCursorVisible(false);

        Screen screen = TerminalFacade.createScreen(terminal);
        screen.startScreen();

        JFrame frame = terminal.getJFrame();		
        frame.setTitle("Freedom: An arena-battle roguelike");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        
        while(screen.readInput() == null) {
            if(screen.updateScreenSize()) {
                screen.refresh();
            }
            Thread.sleep(100);
        }
        screen.stopScreen();
    }
}
