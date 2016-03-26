package com.googlecode.lanterna.terminal.virtual;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.IOSafeTerminal;
import com.googlecode.lanterna.terminal.Terminal;

/**
 * Created by martin on 26/03/16.
 */
public interface VirtualTerminal extends IOSafeTerminal {
    void setTerminalSize(TerminalSize newSize);

    void addVirtualTerminalListener(VirtualTerminalListener listener);

    void removeVirtualTerminalListener(VirtualTerminalListener listener);

    void setBacklogSize(int backlogSize);

    boolean isCursorVisible();

    void addInput(KeyStroke keyStroke);

    TerminalPosition getCursorBufferPosition();

    TextCharacter getBufferCharacter(TerminalPosition position);

    TextCharacter getBufferCharacter(int column, int row);

    TextCharacter getCharacter(TerminalPosition position);

    TextCharacter getCharacter(int column, int row);

    int getBufferLineCount();

    void forEachLine(int startRow, int endRow, BufferWalker bufferWalker);

    interface BufferLine {
        TextCharacter getCharacterAt(int column);
    }

    interface BufferWalker {
        void drawLine(int rowNumber, BufferLine bufferLine);
    }
}
