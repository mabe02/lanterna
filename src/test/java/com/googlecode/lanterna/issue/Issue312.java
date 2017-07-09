package com.googlecode.lanterna.issue;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

/**
 * Created by Martin on 2017-07-09.
 */
public class Issue312 {
    public static void main(String[] args) throws IOException {
        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        TextGraphics textGraphics = terminal.newTextGraphics();
        int row = 0;
        while(true) {
            KeyStroke keyStroke = terminal.pollInput();
            if (keyStroke == null) {
                terminal.setCursorPosition(0, 0);
                textGraphics.putString(0, terminal.getCursorPosition().getRow(), " > ");
                terminal.flush();
                keyStroke = terminal.readInput();
                row = 1;
                terminal.clearScreen();
            }
            if (keyStroke.getKeyType() == KeyType.Escape || keyStroke.getKeyType() == KeyType.EOF) {
                break;
            }
            textGraphics.putString(0, row++, "Read KeyStroke: " + keyStroke + "\n");
            terminal.flush();
        }
        terminal.close();
    }
}
