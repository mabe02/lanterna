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
 * Copyright (C) 2010-2012 Martin
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.screen.Screen;

/**
 *
 * @author Martin
 */
public abstract class AbstractTextGUI implements TextGUI {
    private final Screen screen;
    private Status status;
    private Thread textGUIThread;

    public AbstractTextGUI(Screen screen) {
        this.screen = screen;
        this.status = Status.CREATED;
        this.textGUIThread = null;
    }
    
    @Override
    public synchronized void start() {
        if(status == Status.STARTED) {
            throw new IllegalStateException("TextGUI is already started");
        }
        
        status = Status.STARTED;
        textGUIThread = Thread.currentThread();
        
        //Draw initial screen, after this only draw when the GUI is marked as invalid
        drawGUI();
        while(status == Status.STARTED) {
            Key key = screen.readInput();
            boolean needsRefresh = false;
            if(screen.resizePending()) {
                screen.updateScreenSize();
                needsRefresh = true;
            }
            if(key != null) {
                //Handle input
                if(key.getKind() == Key.Kind.Escape) {
                    stop();
                }
                if(handleInput(key)) {
                    needsRefresh = true;
                }
            }
            if(isInvalid()) {
                needsRefresh = true;
            }
            
            if(needsRefresh) {
                drawGUI();
            }
            else {
                try {
                    Thread.sleep(1);
                }
                catch(InterruptedException e) {}
            }
        }
    }

    @Override
    public synchronized void stop() {
        if(status == Status.CREATED || status == Status.STOPPED) {
            return;
        }
        
        status = Status.STOPPED;
    }

    private void drawGUI() {
        TextGUIGraphics graphics = new ScreenBackendTextGUIGraphics(screen);
        drawGUI(graphics);
        screen.refresh();
    }

    protected Thread getTextGUIThread() {
        return textGUIThread;
    }

    protected abstract boolean isInvalid();
    protected abstract void drawGUI(TextGUIGraphics graphics);
    protected abstract boolean handleInput(Key key);
}
