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
 * Copyright (C) 2010-2014 Martin
 */
package com.googlecode.lanterna.screen;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.ResizeListener;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalSize;
import java.io.IOException;
import java.util.LinkedList;

/**
 * This class keeps some simple code dealing with handling the Terminal interface that the Screen sits on top of.
 * @author martin
 */
public abstract class TerminalScreen implements Screen {
    
    private final Terminal terminal;
    
    private TerminalSize terminalSize;
    private TerminalSize latestResizeRequest;

    protected TerminalScreen(Terminal terminal, TerminalSize terminalSize) throws IOException {
        this.terminal = terminal;
        
        if(terminalSize != null) {
            this.terminalSize = terminalSize; 
        }
        else {
            this.terminalSize = terminal.getTerminalSize();
        }
        this.latestResizeRequest = null;
        
        this.terminal.addResizeListener(new TerminalResizeListener());
    }
    
    /**
     * Returns the underlying {@code Terminal} interface that this Screen is using. 
     * </p>
     * <b>Be aware:</b> directly modifying the underlying terminal will most likely result in unexpected behaviour if
     * you then go on and try to interact with the Screen. The Screen's back-buffer/front-buffer will not know about
     * the operations you are going on the Terminal and won't be able to properly generate a refresh unless you enforce
     * a {@code Screen.RefreshType.COMPLETE}, at which the entire terminal area will be repainted according to the 
     * back-buffer of the {@code Screen}.
     * @return 
     */
    public Terminal getTerminal() {
        return terminal;
    }

    @Override
    public KeyStroke readInput() throws IOException {
        return terminal.readInput();
    }

    @Override
    public TerminalSize getTerminalSize() {
        return terminalSize;
    }
    
    @Override
    public void addResizeListener(ResizeListener listener) {
        terminal.addResizeListener(listener);
    }

    @Override
    public void removeResizeListener(ResizeListener listener) {
        terminal.removeResizeListener(listener);
    }
    
    protected synchronized TerminalSize getAndClearPendingResize() {
        TerminalSize size = latestResizeRequest;
        latestResizeRequest = null;
        return size;
    }
    
    private class TerminalResizeListener implements ResizeListener {
        @Override
        public void onResized(Terminal terminal, TerminalSize newSize) {
            latestResizeRequest = newSize;
        }
    }
}
