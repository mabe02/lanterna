package com.googlecode.lanterna.terminal.swing;

/**
 * Created by Martin on 2016-02-21.
 */
class VirtualTerminal2 {
    private final TextBuffer2 regularTextBuffer;
    private final TextBuffer2 privateModeTextBuffer;

    public VirtualTerminal2() {
        this.regularTextBuffer = new TextBuffer2();
        this.privateModeTextBuffer = new TextBuffer2();
    }
}
