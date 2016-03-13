package com.googlecode.lanterna.terminal;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TestTerminalFactory;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;

import java.io.IOException;

/**
 * Created by martin on 12/03/16.
 */
public class SimpleTerminalTest {

    private static final TextColor[] COLORS_TO_CYCLE = new TextColor[] {
            TextColor.ANSI.BLUE,
            TextColor.ANSI.CYAN,
            TextColor.ANSI.GREEN,
            TextColor.ANSI.MAGENTA,
            TextColor.ANSI.RED,
            TextColor.ANSI.WHITE,
            TextColor.ANSI.YELLOW,
    };

    public static void main(String[] args) throws IOException {
        Terminal terminal = new TestTerminalFactory(args).createTerminal();
        TextGraphics textGraphics = terminal.newTextGraphics();
        boolean inPrivateMode = false;
        int colorIndex = 0;
        terminal.clearScreen();
        printHelp(textGraphics);
        terminal.putCharacter('\n');
        terminal.setBackgroundColor(COLORS_TO_CYCLE[0]);
        TerminalPosition cursorPosition = resetCursorPositionAfterHelp(terminal);
        terminal.flush();

        mainLoop:
        while(true) {
            KeyStroke keyStroke = terminal.readInput();
            switch(keyStroke.getKeyType()) {
                case Escape:
                case EOF:
                    break mainLoop;

                case ArrowDown:
                    if(terminal.getTerminalSize().getRows() > cursorPosition.getRow() + 1) {
                        cursorPosition = cursorPosition.withRelativeRow(1);
                        terminal.setCursorPosition(cursorPosition.getColumn(), cursorPosition.getRow());
                    }
                    break;
                case ArrowUp:
                    if(cursorPosition.getRow() > 0) {
                        cursorPosition = cursorPosition.withRelativeRow(-1);
                        terminal.setCursorPosition(cursorPosition.getColumn(), cursorPosition.getRow());
                    }
                    break;
                case ArrowRight:
                    if(cursorPosition.getColumn() + 1 < terminal.getTerminalSize().getColumns()) {
                        cursorPosition = cursorPosition.withRelativeColumn(1);
                        terminal.setCursorPosition(cursorPosition.getColumn(), cursorPosition.getRow());
                    }
                    break;
                case ArrowLeft:
                    if(cursorPosition.getColumn() > 0) {
                        cursorPosition = cursorPosition.withRelativeColumn(-1);
                        terminal.setCursorPosition(cursorPosition.getColumn(), cursorPosition.getRow());
                    }
                    break;

                case Character:
                    switch(keyStroke.getCharacter()) {
                        case '?':
                            terminal.putCharacter('\n');
                            printHelp(textGraphics);
                            cursorPosition = resetCursorPositionAfterHelp(terminal);
                            break;
                        case 'm':
                            if(inPrivateMode) {
                                terminal.exitPrivateMode();
                                inPrivateMode = false;
                            }
                            else {
                                terminal.enterPrivateMode();
                                inPrivateMode = true;
                            }
                            break;
                        case 'n':
                            terminal.putCharacter('\n');
                            cursorPosition = cursorPosition.withRelativeRow(1).withColumn(0);
                            break;
                        case 'c':
                            colorIndex++;
                            if(colorIndex >= COLORS_TO_CYCLE.length) {
                                colorIndex = 0;
                            }
                            terminal.setBackgroundColor(COLORS_TO_CYCLE[colorIndex]);
                            break;

                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            for(int i = 0; i < Integer.parseInt(Character.toString(keyStroke.getCharacter())); i++) {
                                terminal.putCharacter(' ');
                                cursorPosition = cursorPosition.withRelativeColumn(1);
                            }
                            break;
                    }
                    break;
            }
            terminal.flush();
        }
        if(inPrivateMode) {
            terminal.exitPrivateMode();
        }
        terminal.setBackgroundColor(TextColor.ANSI.DEFAULT);
        terminal.setForegroundColor(TextColor.ANSI.DEFAULT);
        terminal.flush();
        System.out.println();
    }

    private static TerminalPosition resetCursorPositionAfterHelp(Terminal terminal) throws IOException {
        TerminalPosition cursorPosition = new TerminalPosition(0, 8);
        terminal.setCursorPosition(cursorPosition.getColumn(), cursorPosition.getRow());
        return cursorPosition;
    }

    private static void printHelp(TextGraphics textGraphics) {
        textGraphics.setBackgroundColor(TextColor.ANSI.DEFAULT);
        textGraphics.setForegroundColor(TextColor.ANSI.DEFAULT);
        textGraphics.putString(1, 0, "Commands available:");
        textGraphics.putString(1, 1, "?            - Print this message");
        textGraphics.putString(1, 2, "m            - Toggle private mode on/off");
        textGraphics.putString(1, 3, "n            - Newline");
        textGraphics.putString(1, 4, "c            - Cycle color");
        textGraphics.putString(1, 5, "<arrow keys> - Move cursor");
        textGraphics.putString(1, 6, "1-9          - Print X number of blocks at cursor");
    }
}
