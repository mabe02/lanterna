package com.googlecode.lanterna.terminal.swing;

import com.googlecode.lanterna.TerminalTextUtils;
import com.googlecode.lanterna.TextCharacter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Martin on 2016-02-21.
 */
class TextBuffer {
    private static final TextCharacter DOUBLE_WIDTH_CHAR_PADDING = new TextCharacter(' ');

    private final LinkedList<List<TextCharacter>> lines;

    TextBuffer() {
        this.lines = new LinkedList<List<TextCharacter>>();
        newLine();
    }

    synchronized void newLine() {
        lines.add(new ArrayList<TextCharacter>(200));
    }

    synchronized void removeFirstLine() {
        lines.removeFirst();
    }

    synchronized void clear() {
        lines.clear();
        newLine();
    }

    synchronized int getLineCount() {
        return lines.size();
    }

    synchronized void setCharacter(int lineNumber, int columnIndex, TextCharacter textCharacter) {
        if(lineNumber < 0 || columnIndex < 0) {
            throw new IllegalArgumentException("Illegal argument to TextBuffer.setCharacter(..), lineNumber = " +
                    lineNumber + ", columnIndex = " + columnIndex);
        }
        if(textCharacter == null) {
            textCharacter = TextCharacter.DEFAULT_CHARACTER;
        }
        while(lineNumber >= lines.size()) {
            newLine();
        }
        List<TextCharacter> line = lines.get(lineNumber);
        while(line.size() <= columnIndex) {
            line.add(TextCharacter.DEFAULT_CHARACTER);
        }

        // Check if we are overwriting a double-width character, in that case we need to reset the other half
        if(line.get(columnIndex).isDoubleWidth()) {
            line.set(columnIndex + 1, TextCharacter.DEFAULT_CHARACTER);
        }
        if(line.get(columnIndex) == DOUBLE_WIDTH_CHAR_PADDING) {
            line.set(columnIndex - 1, TextCharacter.DEFAULT_CHARACTER);
        }
        line.set(columnIndex, textCharacter);

        if(textCharacter.isDoubleWidth()) {
            setCharacter(lineNumber, columnIndex + 1, DOUBLE_WIDTH_CHAR_PADDING);
        }
    }

    synchronized TextCharacter getCharacter(int lineNumber, int columnIndex) {
        if(lineNumber < 0 || columnIndex < 0) {
            throw new IllegalArgumentException("Illegal argument to TextBuffer.getCharacter(..), lineNumber = " +
                    lineNumber + ", columnIndex = " + columnIndex);
        }
        if(lineNumber >= lines.size()) {
            return TextCharacter.DEFAULT_CHARACTER;
        }
        List<TextCharacter> line = lines.get(lineNumber);
        if(line.size() <= columnIndex) {
            return TextCharacter.DEFAULT_CHARACTER;
        }
        TextCharacter textCharacter = line.get(columnIndex);
        if(textCharacter == DOUBLE_WIDTH_CHAR_PADDING) {
            return line.get(columnIndex - 1);
        }
        return textCharacter;
    }
}
