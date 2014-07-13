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
 * Copyright (C) 2010-2014 Martin
 */
package com.googlecode.lanterna.screen;

import com.googlecode.lanterna.TestTerminalFactory;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TextColor;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import java.io.IOException;

/**
 * Test that demonstrates switching between two different screens
 * @author martin
 */
public class MultiScreenTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        Terminal terminal = new TestTerminalFactory(args, SwingTerminalFrame.AutoCloseTrigger.DontAutoClose).createTerminal();
        Screen redScreen = new DefaultScreen(terminal);
        Screen greenScreen = new DefaultScreen(terminal);
        
        if(terminal instanceof SwingTerminalFrame) {
            ((SwingTerminalFrame)terminal).setVisible(true);
        }
        
        ScreenWriter screenWriter = new ScreenWriter(redScreen);
        screenWriter.setForegroundColor(TextColor.ANSI.BLACK);
        screenWriter.setBackgroundColor(TextColor.ANSI.RED);
        screenWriter.fillScreen(' ');
        screenWriter.putString(2, 2, "Press space to switch screen or ESC to exit");
        
        
        screenWriter = new ScreenWriter(greenScreen);
        screenWriter.setBackgroundColor(TextColor.ANSI.GREEN);
        screenWriter.fillScreen(' ');
        screenWriter.putString(4, 4, "Press space to switch screen or ESC to exit");
        
        mainLoop:
        while(true) {
            redScreen.startScreen();
            redScreen.refresh();
            while(true) {
                KeyStroke keyStroke = terminal.readInput();
                if(keyStroke == null) {
                    Thread.sleep(1);
                }
                else if(keyStroke.getKeyType() == KeyType.Escape) {
                    break mainLoop;
                }
                else if(keyStroke.getCharacter() == ' ') {
                    break;
                }
            }
            redScreen.stopScreen();
            greenScreen.startScreen();
            greenScreen.refresh();
            while(true) {
                KeyStroke keyStroke = terminal.readInput();
                if(keyStroke == null) {
                    Thread.sleep(1);
                }
                else if(keyStroke.getKeyType() == KeyType.Escape) {
                    break mainLoop;
                }
                else if(keyStroke.getCharacter() == ' ') {
                    break;
                }
            }
            greenScreen.stopScreen();
        }
        terminal.clearScreen();
        if(terminal instanceof SwingTerminalFrame) {
            ((SwingTerminalFrame)terminal).setVisible(false);
            ((SwingTerminalFrame)terminal).dispose();
        }
    }
}
