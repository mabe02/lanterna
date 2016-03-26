/*
 * This file is part of lanterna (http://code.google.com/p/lanterna/).
 *
 * lanterna is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2010-2016 Martin
 */
package com.googlecode.lanterna.issue;

import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import com.googlecode.lanterna.terminal.swing.TerminalEmulatorAutoCloseTrigger;

import java.io.IOException;
import javax.swing.WindowConstants;

/**
 *
 * @author martin
 */
public class Issue95 {
    public static void main(String[] args) throws InterruptedException, IOException {
        SwingTerminalFrame terminal = new SwingTerminalFrame(TerminalEmulatorAutoCloseTrigger.CloseOnExitPrivateMode);
        terminal.setCursorVisible(false);

        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();
        
        terminal.setTitle("Freedom: An arena-battle roguelike");
        terminal.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        terminal.setResizable(false);
        terminal.setVisible(true);

        while(screen.pollInput() == null) {
            if(screen.doResizeIfNecessary() != null) {
                screen.refresh();
            }
            Thread.sleep(100);
        }
        screen.stopScreen();
    }
}
