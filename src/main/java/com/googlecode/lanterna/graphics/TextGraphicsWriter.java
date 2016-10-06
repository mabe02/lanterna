package com.googlecode.lanterna.graphics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalTextUtils;
import com.googlecode.lanterna.TextColor;

public class TextGraphicsWriter implements StyleSet<TextGraphicsWriter> {
    private final TextGraphics backend;
    private TerminalPosition cursorPosition;
    private TextColor foregroundColor, backgroundColor;
    private EnumSet<SGR> style = EnumSet.noneOf(SGR.class);
    private StyleSet.Set originalStyle;
    
    public TextGraphicsWriter(TextGraphics backend) {
        this.backend = backend;
        setStyleFrom( backend );
        originalStyle = new StyleSet.Set(backend);
        cursorPosition = new TerminalPosition(0, 0);
    }

    public TextGraphicsWriter putString(String string) {
        StringBuilder wordpart = new StringBuilder();
        int wordlen = 0; // the whole column-length of the word.
        for (int i = 0; i < string.length(); i++) {
            char ch = string.charAt(i);
            switch (ch) {
            case '\n':
                flush(wordpart,wordlen); wordlen = 0;
                linefeed(-1);
                break;
            case '\t':
                flush(wordpart,wordlen); wordlen = 0;
                String repl = backend.getTabBehaviour()
                        .getTabReplacement(cursorPosition.getColumn());
                for(int j = 0; j < repl.length(); j++) {
                    backend.setCharacter(cursorPosition.withRelativeColumn(j), repl.charAt(j));
                }
                cursorPosition = cursorPosition.withRelativeColumn(repl.length());
                break;
            case ' ':
                flush(wordpart,wordlen); wordlen = 0;
                backend.setCharacter(cursorPosition, ch);
                cursorPosition = cursorPosition.withRelativeColumn(1);
                break;
            case '\033':
                stash(wordpart,wordlen);
                String seq = TerminalTextUtils.getANSIControlSequenceAt(string, i);
                TerminalTextUtils.updateModifiersFromCSICode(seq, this, originalStyle);
                i += seq.length() - 1;
                break;
            default:
                if (Character.isWhitespace(ch)) {
                    flush(wordpart,wordlen); wordlen = 0;
                    backend.setCharacter(cursorPosition, ch);
                    cursorPosition = cursorPosition.withRelativeColumn(1);
                } else if (TerminalTextUtils.isCharCJK(ch)) {
                    flush(wordpart, wordlen); wordlen = 0;
                    linefeed(2);
                    backend.setCharacter(cursorPosition, ch);
                    cursorPosition = cursorPosition.withRelativeColumn(2);
                } else {
                    wordpart.append(ch); wordlen++;
                }
            }
            linefeed(wordlen);
        }
        flush(wordpart,wordlen);
        return this;
    }
    private void linefeed(int lenToFit) {
        int spaceLeft = backend.getSize().getColumns() - cursorPosition.getColumn();
        if (lenToFit < 0 || lenToFit > spaceLeft) {
            // TODO: clear to end of current line?
            cursorPosition = cursorPosition.withColumn(0).withRelativeRow(1);
        }
    }

    // A word (a sequence of characters that is kept together when word-wrapping)
    // may consist of differently styled parts. This class describes one such
    // part.
    private static class WordPart extends StyleSet.Set {
        String word; int wordlen;
        WordPart(String word, int wordlen, StyleSet<?> style) {
            this.word = word; this.wordlen = wordlen;
            setStyleFrom(style);
        }
    }
    private List<WordPart> chunk_queue = new ArrayList<WordPart>();
    private void stash(StringBuilder word, int wordlen) {
        if (word.length() > 0) {
            WordPart chunk = new WordPart(word.toString(),wordlen, this);
            chunk_queue.add(chunk);
            // for convenience the StringBuilder is reset:
            word.setLength(0);
        }
    }
    private void flush(StringBuilder word, int wordlen) {
        stash(word, wordlen);
        if (chunk_queue.isEmpty()) {
            return;
        }
        int row = cursorPosition.getRow();
        int col = cursorPosition.getColumn();
        int offset = 0;
        for (WordPart chunk : chunk_queue) {
            backend.setStyleFrom(chunk);
            backend.putString(col+offset,row, chunk.word);
            offset = chunk.wordlen;
        }
        chunk_queue.clear(); // they're done.
        // set cursor right behind the word:
        cursorPosition = cursorPosition.withColumn(col+offset);
        backend.setStyleFrom(this);
    }

    /**
     * @return the cursor position
     */
    public TerminalPosition getCursorPosition() {
        return cursorPosition;
    }

    /**
     * @param cursorPosition the cursor position to set
     */
    public void setCursorPosition(TerminalPosition cursorPosition) {
        this.cursorPosition = cursorPosition;
    }

    /**
     * @return the fg color
     */
    public TextColor getForegroundColor() {
        return foregroundColor;
    }

    /**
     * @param fg the fg color to set
     */
    public TextGraphicsWriter setForegroundColor(TextColor fg) {
        this.foregroundColor = fg;
        return this;
    }

    /**
     * @return the bg color
     */
    public TextColor getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * @param bg the bg color to set
     */
    public TextGraphicsWriter setBackgroundColor(TextColor bg) {
        this.backgroundColor = bg;
        return this;
    }
    
    @Override
    public TextGraphicsWriter enableModifiers(SGR... modifiers) {
        style.addAll(Arrays.asList(modifiers));
        return this;
    }

    @Override
    public TextGraphicsWriter disableModifiers(SGR... modifiers) {
        style.removeAll(Arrays.asList(modifiers));
        return this;
    }

    @Override
    public TextGraphicsWriter setModifiers(EnumSet<SGR> modifiers) {
        style.clear(); style.addAll(modifiers);
        return this;
    }

    @Override
    public TextGraphicsWriter clearModifiers() {
        style.clear();
        return this;
    }

    @Override
    public EnumSet<SGR> getActiveModifiers() {
        return EnumSet.copyOf(style);
    }

    @Override
    public TextGraphicsWriter setStyleFrom(StyleSet<?> source) {
        setBackgroundColor(source.getBackgroundColor());
        setForegroundColor(source.getForegroundColor());
        setModifiers(source.getActiveModifiers());
        return this;
    }
}
