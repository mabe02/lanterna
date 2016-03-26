package com.googlecode.lanterna.terminal.virtual;

import com.googlecode.lanterna.terminal.TerminalResizeListener;

public interface VirtualTerminalListener extends TerminalResizeListener {
    void onFlush();
    void onBeep();
}
