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
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.screen.VirtualScreen;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Martin
 */
public class MultiWindowTextGUI extends AbstractTextGUI implements WindowBasedTextGUI {
    private final VirtualScreen virtualScreen;
    private final WindowManager windowManager;
    private final BasePane backgroundPane;
    private final List<Window> windows;
    private final WindowPostRenderer postRenderer;

    private Window activeWindow;
    private boolean eofWhenNoWindows;

    public MultiWindowTextGUI(Screen screen) {
        this(screen, TextColor.ANSI.BLUE);
    }

    public MultiWindowTextGUI(TextGUIThreadFactory guiThreadFactory, Screen screen) {
        this(guiThreadFactory,
                screen,
                new DefaultWindowManager(),
                new WindowShadowRenderer(),
                new EmptySpace(TextColor.ANSI.BLUE));
    }

    public MultiWindowTextGUI(
            Screen screen,
            TextColor backgroundColor) {

        this(screen, new DefaultWindowManager(), new EmptySpace(backgroundColor));
    }

    public MultiWindowTextGUI(
            Screen screen,
            WindowManager windowManager,
            Component background) {

        this(screen, windowManager, new WindowShadowRenderer(), background);
    }

    public MultiWindowTextGUI(
            Screen screen,
            WindowManager windowManager,
            WindowPostRenderer postRenderer,
            Component background) {

        this(new SameTextGUIThread.Factory(), screen, windowManager, postRenderer, background);
    }

    public MultiWindowTextGUI(
            TextGUIThreadFactory guiThreadFactory,
            Screen screen,
            WindowManager windowManager,
            WindowPostRenderer postRenderer,
            Component background) {

        this(guiThreadFactory, new VirtualScreen(screen), windowManager, postRenderer, background);
    }

    private MultiWindowTextGUI(
            TextGUIThreadFactory guiThreadFactory,
            VirtualScreen screen,
            WindowManager windowManager,
            WindowPostRenderer postRenderer,
            Component background) {

        super(guiThreadFactory, screen);
        if(windowManager == null) {
            throw new IllegalArgumentException("Creating a window-based TextGUI requires a WindowManager");
        }
        if(background == null) {
            //Use a sensible default instead of throwing
            background = new EmptySpace(TextColor.ANSI.BLUE);
        }
        this.virtualScreen = screen;
        this.windowManager = windowManager;
        this.backgroundPane = new AbstractBasePane() {
            @Override
            public TextGUI getTextGUI() {
                return MultiWindowTextGUI.this;
            }

            @Override
            public TerminalPosition toGlobal(TerminalPosition localPosition) {
                return localPosition;
            }

            public TerminalPosition fromGlobal(TerminalPosition globalPosition) {
                return globalPosition;
            }
        };
        this.backgroundPane.setComponent(background);
        this.windows = new ArrayList<Window>();
        this.postRenderer = postRenderer;
        this.eofWhenNoWindows = false;
    }

    @Override
    public synchronized boolean isPendingUpdate() {
        for(Window window: windows) {
            if(window.isInvalid()) {
                return true;
            }
        }
        return super.isPendingUpdate() || backgroundPane.isInvalid() || windowManager.isInvalid();
    }

    @Override
    public synchronized void updateScreen() throws IOException {
        TerminalSize minimumTerminalSize = TerminalSize.ZERO;
        for(Window window: windows) {
            if(window.getHints().contains(Window.Hint.FULL_SCREEN) ||
                    window.getHints().contains(Window.Hint.FIT_TERMINAL_WINDOW)) {
                //Don't take full screen windows or auto-sized windows into account
                continue;
            }
            TerminalPosition lastPosition = window.getPosition();
            minimumTerminalSize = minimumTerminalSize.max(
                    //Add position to size to get the bottom-right corner of the window
                    window.getDecoratedSize().withRelative(lastPosition.getColumn(), lastPosition.getRow()));
        }
        virtualScreen.setMinimumSize(minimumTerminalSize);
        super.updateScreen();
    }

    @Override
    protected synchronized KeyStroke readKeyStroke() throws IOException {
        KeyStroke keyStroke = super.pollInput();
        if(eofWhenNoWindows && keyStroke == null && windows.isEmpty()) {
            return new KeyStroke(KeyType.EOF);
        }
        else if(keyStroke != null) {
            return keyStroke;
        }
        else {
            return super.readKeyStroke();
        }
    }

    @Override
    protected synchronized void drawGUI(TextGUIGraphics graphics) {
        backgroundPane.draw(graphics);
        getWindowManager().prepareWindows(this, Collections.unmodifiableList(windows), graphics.getSize());
        for(Window window: windows) {
            TextGUIGraphics windowGraphics = graphics.newTextGraphics(window.getPosition(), window.getDecoratedSize());
            if(window.getHints().contains(Window.Hint.NO_DECORATIONS)) {
                window.draw(windowGraphics);
                window.setContentOffset(TerminalPosition.TOP_LEFT_CORNER);
            }
            else {
                WindowDecorationRenderer decorationRenderer = getWindowManager().getWindowDecorationRenderer(window);
                TextGUIGraphics windowInnerGraphics = decorationRenderer.draw(this, windowGraphics, window);
                window.draw(windowInnerGraphics);
                window.setContentOffset(decorationRenderer.getOffset(window));
                Borders.joinLinesWithFrame(windowGraphics);
            }
            if(postRenderer != null && !window.getHints().contains(Window.Hint.NO_POST_RENDERING)) {
                postRenderer.postRender(graphics, this, window);
            }
        }
    }

    @Override
    public synchronized TerminalPosition getCursorPosition() {
        Window activeWindow = getActiveWindow();
        if(activeWindow != null) {
            return activeWindow.toGlobal(activeWindow.getCursorPosition());
        }
        else {
            return backgroundPane.getCursorPosition();
        }
    }

    /**
     * Sets whether the TextGUI should return EOF when you try to read input while there are no windows in the window
     * manager. Setting this to true (on by default) will make the GUI automatically exit when the last window has been
     * closed.
     * @param eofWhenNoWindows Should the GUI return EOF when there are no windows left
     */
    public void setEOFWhenNoWindows(boolean eofWhenNoWindows) {
        this.eofWhenNoWindows = eofWhenNoWindows;
    }

    /**
     * Returns whether the TextGUI should return EOF when you try to read input while there are no windows in the window
     * manager. When this is true (true by default) will make the GUI automatically exit when the last window has been
     * closed.
     * @return Should the GUI return EOF when there are no windows left
     */
    public boolean isEOFWhenNoWindows() {
        return eofWhenNoWindows;
    }

    @Override
    public synchronized Interactable getFocusedInteractable() {
        Window activeWindow = getActiveWindow();
        if(activeWindow != null) {
            return activeWindow.getFocusedInteractable();
        }
        else {
            return backgroundPane.getFocusedInteractable();
        }
    }

    @Override
    public synchronized boolean handleInput(KeyStroke keyStroke) {
        Window activeWindow = getActiveWindow();
        if(activeWindow != null) {
            return activeWindow.handleInput(keyStroke);
        }
        else {
            return backgroundPane.handleInput(keyStroke);
        }
    }

    @Override
    public WindowManager getWindowManager() {
        return windowManager;
    }

    @Override
    public synchronized WindowBasedTextGUI addWindow(Window window) {
        //To protect against NPE if the user forgot to set a content component
        if(window.getComponent() == null) {
            window.setComponent(new EmptySpace(TerminalSize.ONE));
        }

        if(window.getTextGUI() != null) {
            window.getTextGUI().removeWindow(window);
        }
        window.setTextGUI(this);
        windowManager.onAdded(this, window, windows);
        if(!windows.contains(window)) {
            windows.add(window);
        }
        if(!window.getHints().contains(Window.Hint.NO_FOCUS)) {
            activeWindow = window;
        }
        invalidate();
        return this;
    }

    @Override
    public WindowBasedTextGUI addWindowAndWait(Window window) {
        addWindow(window);
        window.waitUntilClosed();
        return this;
    }

    @Override
    public synchronized WindowBasedTextGUI removeWindow(Window window) {
        if(!windows.remove(window)) {
            //Didn't contain this window
            return this;
        }
        window.setTextGUI(null);
        windowManager.onRemoved(this, window, windows);
        if(activeWindow == window) {
            //Go backward in reverse and find the first suitable window
            for(int index = windows.size() - 1; index >= 0; index--) {
                Window candidate = windows.get(index);
                if(!candidate.getHints().contains(Window.Hint.NO_FOCUS)) {
                    activeWindow = candidate;
                    break;
                }
            }
        }
        invalidate();
        return this;
    }

    @Override
    public void waitForWindowToClose(Window window) {
        while(window.getTextGUI() != null) {
            boolean sleep = true;
            TextGUIThread guiThread = getGUIThread();
            if(Thread.currentThread() == guiThread.getThread()) {
                try {
                    sleep = !guiThread.processEventsAndUpdate();
                }
                catch(EOFException ignore) {
                    //The GUI has closed so allow exit
                    break;
                }
                catch(IOException e) {
                    throw new RuntimeException("Unexpected IOException while waiting for window to close", e);
                }
            }
            if(sleep) {
                try {
                    Thread.sleep(1);
                }
                catch(InterruptedException ignore) {}
            }
        }
    }

    @Override
    public synchronized Collection<Window> getWindows() {
        return Collections.unmodifiableList(new ArrayList<Window>(windows));
    }

    @Override
    public synchronized MultiWindowTextGUI setActiveWindow(Window activeWindow) {
        this.activeWindow = activeWindow;
        return this;
    }

    @Override
    public synchronized Window getActiveWindow() {
        return activeWindow;
    }

    public BasePane getBackgroundPane() {
        return backgroundPane;
    }

    @Override
    public synchronized WindowBasedTextGUI moveToTop(Window window) {
        if(!windows.contains(window)) {
            throw new IllegalArgumentException("Window " + window + " isn't in MultiWindowTextGUI " + this);
        }
        windows.remove(window);
        windows.add(window);
        invalidate();
        return this;
    }
}
