package com.googlecode.lanterna.terminal.swing;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by martin on 27/02/16.
 */
public class VirtualTerminalTest {
    @Test
    public void singleLineWriteAndReadBackWorks() {
        VirtualTerminal virtualTerminal = new VirtualTerminal(new TerminalSize(80, 24));
        assertEquals(TerminalPosition.TOP_LEFT_CORNER, virtualTerminal.getTranslatedCursorPosition());
        virtualTerminal.putCharacter(new TextCharacter('H'));
        virtualTerminal.putCharacter(new TextCharacter('E'));
        virtualTerminal.putCharacter(new TextCharacter('L'));
        virtualTerminal.putCharacter(new TextCharacter('L'));
        virtualTerminal.putCharacter(new TextCharacter('O'));
        assertEquals(TerminalPosition.TOP_LEFT_CORNER.withColumn(5), virtualTerminal.getCursorPosition());
        assertEquals('H', virtualTerminal.getCharacter(new TerminalPosition(0, 0)).getCharacter());
        assertEquals('E', virtualTerminal.getCharacter(new TerminalPosition(1, 0)).getCharacter());
        assertEquals('L', virtualTerminal.getCharacter(new TerminalPosition(2, 0)).getCharacter());
        assertEquals('L', virtualTerminal.getCharacter(new TerminalPosition(3, 0)).getCharacter());
        assertEquals('O', virtualTerminal.getCharacter(new TerminalPosition(4, 0)).getCharacter());
    }
}
