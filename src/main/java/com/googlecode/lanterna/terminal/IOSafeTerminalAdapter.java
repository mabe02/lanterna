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
 * Copyright (C) 2010-2015 Martin
 */
package com.googlecode.lanterna.terminal;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * This class exposes methods for converting a terminal into an IOSafeTerminal. There are two options available, either
 * one that will convert any IOException to a RuntimeException (and re-throw it) or one that will silently swallow any
 * IOException (and return null in those cases the method has a non-void return type).
 * @author Martin
 */
public class IOSafeTerminalAdapter implements IOSafeTerminal {
    private interface ExceptionHandler {
        void onException(IOException e);
    }
    
    private static class ConvertToRuntimeException implements ExceptionHandler {
        @Override
        public void onException(IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static class DoNothingAndOrReturnNull implements ExceptionHandler {
        @Override
        public void onException(IOException e) { }
    }
    
    /**
     * Creates a wrapper around a Terminal that exposes it as a IOSafeTerminal. If any IOExceptions occur, they will be
     * wrapped by a RuntimeException and re-thrown.
     * @param terminal Terminal to wrap
     * @return IOSafeTerminal wrapping the supplied terminal
     */
    public static IOSafeTerminal createRuntimeExceptionConvertingAdapter(Terminal terminal) {
        return new IOSafeTerminalAdapter(terminal, new ConvertToRuntimeException());
    }
    
    /**
     * Creates a wrapper around a Terminal that exposes it as a IOSafeTerminal. If any IOExceptions occur, they will be
     * silently ignored and for those method with a non-void return type, null will be returned.
     * @param terminal Terminal to wrap
     * @return IOSafeTerminal wrapping the supplied terminal
     */
    public static IOSafeTerminal createDoNothingOnExceptionAdapter(Terminal terminal) {
        return new IOSafeTerminalAdapter(terminal, new DoNothingAndOrReturnNull());
    }
        
    private final Terminal backend;
    private final ExceptionHandler exceptionHandler;

    @SuppressWarnings("WeakerAccess")
    public IOSafeTerminalAdapter(Terminal backend, ExceptionHandler exceptionHandler) {
        this.backend = backend;
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public void enterPrivateMode() {
        try {
            backend.enterPrivateMode();
        }
        catch(IOException e) {
            exceptionHandler.onException(e);
        }
    }

    @Override
    public void exitPrivateMode() {
        try {
            backend.exitPrivateMode();
        }
        catch(IOException e) {
            exceptionHandler.onException(e);
        }
    }

    @Override
    public void clearScreen() {
        try {
            backend.clearScreen();
        }
        catch(IOException e) {
            exceptionHandler.onException(e);
        }
    }

    @Override
    public void setCursorPosition(int x, int y) {
        try {
            backend.setCursorPosition(x, y);
        }
        catch(IOException e) {
            exceptionHandler.onException(e);
        }
    }

    @Override
    public void setCursorVisible(boolean visible) {
        try {
            backend.setCursorVisible(visible);
        }
        catch(IOException e) {
            exceptionHandler.onException(e);
        }
    }

    @Override
    public void putCharacter(char c) {
        try {
            backend.putCharacter(c);
        }
        catch(IOException e) {
            exceptionHandler.onException(e);
        }
    }

    @Override
    public TextGraphics newTextGraphics() throws IOException {
        return backend.newTextGraphics();
    }

    @Override
    public void enableSGR(SGR sgr) {
        try {
            backend.enableSGR(sgr);
        }
        catch(IOException e) {
            exceptionHandler.onException(e);
        }
    }

    @Override
    public void disableSGR(SGR sgr) {
        try {
            backend.disableSGR(sgr);
        }
        catch(IOException e) {
            exceptionHandler.onException(e);
        }
    }

    @Override
    public void resetColorAndSGR() {
        try {
            backend.resetColorAndSGR();
        }
        catch(IOException e) {
            exceptionHandler.onException(e);
        }
    }

    @Override
    public void setForegroundColor(TextColor color) {
        try {
            backend.setForegroundColor(color);
        }
        catch(IOException e) {
            exceptionHandler.onException(e);
        }
    }

    @Override
    public void setBackgroundColor(TextColor color) {
        try {
            backend.setBackgroundColor(color);
        }
        catch(IOException e) {
            exceptionHandler.onException(e);
        }
    }

    @Override
    public void addResizeListener(ResizeListener listener) {
        backend.addResizeListener(listener);
    }

    @Override
    public void removeResizeListener(ResizeListener listener) {
        backend.removeResizeListener(listener);
    }

    @Override
    public TerminalSize getTerminalSize() {
        try {
            return backend.getTerminalSize();
        }
        catch(IOException e) {
            exceptionHandler.onException(e);
        }
        return null;
    }

    @Override
    public byte[] enquireTerminal(int timeout, TimeUnit timeoutUnit) {
        try {
            return backend.enquireTerminal(timeout, timeoutUnit);
        }
        catch(IOException e) {
            exceptionHandler.onException(e);
        }
        return null;
    }

    @Override
    public void flush() {
        try {
            backend.flush();
        }
        catch(IOException e) {
            exceptionHandler.onException(e);
        }
    }

    @Override
    public KeyStroke pollInput() {
        try {
            return backend.pollInput();
        }
        catch(IOException e) {
            exceptionHandler.onException(e);
        }
        return null;
    }

    @Override
    public KeyStroke readInput() {
        try {
            return backend.readInput();
        }
        catch(IOException e) {
            exceptionHandler.onException(e);
        }
        return null;
    }
}
