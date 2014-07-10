/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.lanterna.issue;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 *
 * @author martin
 */
public class Issue95 {
    public static void main(String[] args) throws InterruptedException, IOException {
        SwingTerminalFrame terminal = new SwingTerminalFrame(SwingTerminalFrame.AutoCloseTrigger.CloseOnExitPrivateMode);
        terminal.setCursorVisible(false);

        Screen screen = TerminalFacade.createScreen(terminal);
        screen.startScreen();
        
        terminal.setTitle("Freedom: An arena-battle roguelike");
        terminal.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        terminal.setResizable(false);
        terminal.setVisible(true);

        while(screen.readInput() == null) {
            if(screen.doResizeIfNecessary() != null) {
                screen.refresh();
            }
            Thread.sleep(100);
        }
        screen.stopScreen();
    }
}
