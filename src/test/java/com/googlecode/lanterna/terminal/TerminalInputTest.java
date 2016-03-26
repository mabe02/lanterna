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
package com.googlecode.lanterna.terminal;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.ansi.StreamBasedTerminal;
import com.googlecode.lanterna.TestTerminalFactory;
import java.io.IOException;

/**
 *
 * @author martin
 */
public class TerminalInputTest {

    public static void main(String[] args) throws InterruptedException, IOException {
        final Terminal rawTerminal = new TestTerminalFactory(args).createTerminal();
        rawTerminal.enterPrivateMode();

        for(String arg: args) {
            if("--mouse-click".equals(arg)) {
                if (rawTerminal instanceof ExtendedTerminal)
                    ((ExtendedTerminal)rawTerminal).setMouseCaptureMode(MouseCaptureMode.CLICK_RELEASE);
            }
            else if("--mouse-drag".equals(arg)) {
                if (rawTerminal instanceof ExtendedTerminal)
                    ((ExtendedTerminal)rawTerminal).setMouseCaptureMode(MouseCaptureMode.CLICK_RELEASE_DRAG);
            }
            else if("--mouse-move".equals(arg)) {
                if (rawTerminal instanceof ExtendedTerminal)
                    ((ExtendedTerminal)rawTerminal).setMouseCaptureMode(MouseCaptureMode.CLICK_RELEASE_DRAG_MOVE);
            }
            else if("--with-timeout".equals(arg)) {
                if (rawTerminal instanceof StreamBasedTerminal)
                    ((StreamBasedTerminal)rawTerminal).getInputDecoder().setTimeoutUnits(40); // 10s
            }
        }

        int currentRow = 0;
        rawTerminal.setCursorPosition(0, 0);
        while(true) {
            KeyStroke key = rawTerminal.pollInput();
            if(key == null) {
                Thread.sleep(1);
                continue;
            }

            if(key.getKeyType() == KeyType.Escape) {
                break;
            }

            if(currentRow == 0) {
                rawTerminal.clearScreen();
            }

            rawTerminal.setCursorPosition(0, currentRow++);
            putString(rawTerminal, key.toString());

            if(currentRow >= rawTerminal.getTerminalSize().getRows()) {
                currentRow = 0;
            }
        }

        if (rawTerminal instanceof ExtendedTerminal)
            ((ExtendedTerminal)rawTerminal).setMouseCaptureMode(null);

        rawTerminal.exitPrivateMode();
    }

    private static void putString(Terminal rawTerminal, String string) throws IOException {
        for(int i = 0; i < string.length(); i++) {
            rawTerminal.putCharacter(string.charAt(i));
        }
        rawTerminal.flush();
    }
}
