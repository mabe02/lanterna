package com.googlecode.lanterna.graphics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalTextUtils;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.screen.TabBehaviour;
import com.googlecode.lanterna.screen.WrapBehaviour;

public class TextGraphicsWriter implements StyleSet<TextGraphicsWriter> {
    private final TextGraphics backend;
    private TerminalPosition cursorPosition;
    private TextColor foregroundColor, backgroundColor;
    private EnumSet<SGR> style = EnumSet.noneOf(SGR.class);
    private WrapBehaviour wrapBehaviour = WrapBehaviour.WORD;
    private boolean styleable = true;
    
    public TextGraphicsWriter(TextGraphics backend) {
        this.backend = backend;
        setStyleFrom( backend );
        cursorPosition = new TerminalPosition(0, 0);
    }

    public TextGraphicsWriter putString(String string) {
        StringBuilder wordpart = new StringBuilder();
        StyleSet.Set originalStyle = new StyleSet.Set(backend);
        backend.setStyleFrom(this);

        int wordlen = 0; // the whole column-length of the word.
        for (int i = 0; i < string.length(); i++) {
            char ch = string.charAt(i);
            switch (ch) {
            case '\n':
                flush(wordpart,wordlen); wordlen = 0;
                linefeed(-1); // -1 means explicit.
                break;
            case '\t':
                flush(wordpart,wordlen); wordlen = 0;
                if (backend.getTabBehaviour() != TabBehaviour.IGNORE) {
                    String repl = backend.getTabBehaviour()
                            .getTabReplacement(cursorPosition.getColumn());
                    for(int j = 0; j < repl.length(); j++) {
                        backend.setCharacter(cursorPosition.withRelativeColumn(j), repl.charAt(j));
                    }
                    cursorPosition = cursorPosition.withRelativeColumn(repl.length());
                } else {
                    linefeed(2); putControlChar(ch);
                }
                break;
            case '\033':
                if (isStyleable()) {
                    stash(wordpart,wordlen);
                    String seq = TerminalTextUtils.getANSIControlSequenceAt(string, i);
                    TerminalTextUtils.updateModifiersFromCSICode(seq, this, originalStyle);
                    backend.setStyleFrom(this);
                    i += seq.length() - 1;
                } else {
                    flush(wordpart,wordlen); wordlen = 0;
                    linefeed(2); putControlChar(ch);
                }
                break;
            default:
                if (Character.isISOControl(ch)) {
                    flush(wordpart,wordlen); wordlen = 0;
                    linefeed(1); putControlChar(ch);
                } else if (Character.isWhitespace(ch)) {
                    flush(wordpart,wordlen); wordlen = 0;
                    backend.setCharacter(cursorPosition, ch);
                    cursorPosition = cursorPosition.withRelativeColumn(1);
                } else if (TerminalTextUtils.isCharCJK(ch)) {
                    flush(wordpart, wordlen); wordlen = 0;
                    linefeed(2);
                    backend.setCharacter(cursorPosition, ch);
                    cursorPosition = cursorPosition.withRelativeColumn(2);
                } else {
                    if (wrapBehaviour.keepWords()) {
                        // TODO: if at end of line despite starting at col 0, then split word.
                        wordpart.append(ch); wordlen++;
                    } else {
                        linefeed(1);
                        backend.setCharacter(cursorPosition, ch);
                        cursorPosition = cursorPosition.withRelativeColumn(1);
                    }
                }
            }
            linefeed(wordlen);
        }
        flush(wordpart,wordlen);
        backend.setStyleFrom(originalStyle);
        return this;
    }
    private void linefeed(int lenToFit) {
        int curCol = cursorPosition.getColumn();
        int spaceLeft = backend.getSize().getColumns() - curCol;
        if (wrapBehaviour.allowLineFeed()) {
            boolean wantWrap = curCol > 0 && lenToFit > spaceLeft;
            if (lenToFit < 0 || ( wantWrap && wrapBehaviour.autoWrap() ) ) {
                // TODO: clear to end of current line?
                cursorPosition = cursorPosition.withColumn(0).withRelativeRow(1);
            }
        } else {
            if (lenToFit < 0) { // encode explicit line feed
                putControlChar('\n');
            }
        }
    }
    public void putControlChar(char ch) {
        char subst;
        switch (ch) {
        case '\033': subst = '['; break;
        case '\034': subst = '\\'; break;
        case '\035': subst = ']'; break;
        case '\036': subst = '^'; break;
        case '\037': subst = '_'; break;
        case '\177': subst = '?'; break;
        default:
            if (ch <= 26) {
                subst = (char)(ch + '@');
            } else { // normal character - or 0x80-0x9F
                // just write it out, anyway:
                backend.setCharacter(cursorPosition, ch);
                cursorPosition = cursorPosition.withRelativeColumn(1);
                return;
            }
        }
        EnumSet<SGR> style = getActiveModifiers();
        if (style.contains(SGR.REVERSE)) {
            style.remove(SGR.REVERSE);
        } else {
            style.add(SGR.REVERSE);
        }
        TextCharacter tc = new TextCharacter('^',
                getForegroundColor(), getBackgroundColor(), style);
        backend.setCharacter(cursorPosition, tc);
        cursorPosition = cursorPosition.withRelativeColumn(1);
        tc = tc.withCharacter(subst);
        backend.setCharacter(cursorPosition, tc);
        cursorPosition = cursorPosition.withRelativeColumn(1);
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
     * @return the foreground color
     */
    public TextColor getForegroundColor() {
        return foregroundColor;
    }

    /**
     * @param foreground the foreground color to set
     */
    public TextGraphicsWriter setForegroundColor(TextColor foreground) {
        this.foregroundColor = foreground;
        return this;
    }

    /**
     * @return the background color
     */
    public TextColor getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * @param background the background color to set
     */
    public TextGraphicsWriter setBackgroundColor(TextColor background) {
        this.backgroundColor = background;
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

    /**
     * @return the wrapBehaviour
     */
    public WrapBehaviour getWrapBehaviour() {
        return wrapBehaviour;
    }

    /**
     * @param wrapBehaviour the wrapBehaviour to set
     */
    public void setWrapBehaviour(WrapBehaviour wrapBehaviour) {
        this.wrapBehaviour = wrapBehaviour;
    }

    /**
     * @return whether styles in strings are handled.
     */
    public boolean isStyleable() {
        return styleable;
    }

    /**
     * @param styleable whether styles in strings should be handled.
     */
    public void setStyleable(boolean styleable) {
        this.styleable = styleable;
    }
}
