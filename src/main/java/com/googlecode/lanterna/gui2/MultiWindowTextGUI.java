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
 * Copyright (C) 2010-2017 Martin Berglund
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.graphics.BasicTextImage;
import com.googlecode.lanterna.graphics.TextImage;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.screen.VirtualScreen;

import java.io.EOFException;
import java.io.IOException;
import java.util.*;

/**
 * This is the main Text GUI implementation built into Lanterna, supporting multiple tiled windows and a dynamic
 * background area that can be fully customized. If you want to create a text-based GUI with windows and controls,
 * it's very likely this is what you want to use.
 *
 * @author Martin
 */
public class MultiWindowTextGUI extends AbstractTextGUI implements WindowBasedTextGUI {
    private final VirtualScreen virtualScreen;
    private final WindowManager windowManager;
    private final BasePane backgroundPane;
    private final List<Window> windows;
    private final IdentityHashMap<Window, TextImage> windowRenderBufferCache;
    private final WindowPostRenderer postRenderer;

    private Window activeWindow;
    private boolean eofWhenNoWindows;

    /**
     * Creates a new {@code MultiWindowTextGUI} that uses the specified {@code Screen} as the backend for all drawing
     * operations. The screen will be automatically wrapped in a {@code VirtualScreen} in order to deal with GUIs
     * becoming too big to fit the terminal. The background area of the GUI will be solid blue.
     * @param screen Screen to use as the backend for drawing operations
     */
    public MultiWindowTextGUI(Screen screen) {
        this(screen, TextColor.ANSI.BLUE);
    }

    /**
     * Creates a new {@code MultiWindowTextGUI} that uses the specified {@code Screen} as the backend for all drawing
     * operations. The screen will be automatically wrapped in a {@code VirtualScreen} in order to deal with GUIs
     * becoming too big to fit the terminal. The background area of the GUI will be solid blue
     * @param guiThreadFactory Factory implementation to use when creating the {@code TextGUIThread}
     * @param screen Screen to use as the backend for drawing operations
     */
    public MultiWindowTextGUI(TextGUIThreadFactory guiThreadFactory, Screen screen) {
        this(guiThreadFactory,
                screen,
                new DefaultWindowManager(),
                null,
                new GUIBackdrop());
    }

    /**
     * Creates a new {@code MultiWindowTextGUI} that uses the specified {@code Screen} as the backend for all drawing
     * operations. The screen will be automatically wrapped in a {@code VirtualScreen} in order to deal with GUIs
     * becoming too big to fit the terminal. The background area of the GUI is a solid color as decided by the
     * {@code backgroundColor} parameter.
     * @param screen Screen to use as the backend for drawing operations
     * @param backgroundColor Color to use for the GUI background
     */
    public MultiWindowTextGUI(
            Screen screen,
            TextColor backgroundColor) {

        this(screen, new DefaultWindowManager(), new EmptySpace(backgroundColor));
    }

    /**
     * Creates a new {@code MultiWindowTextGUI} that uses the specified {@code Screen} as the backend for all drawing
     * operations. The screen will be automatically wrapped in a {@code VirtualScreen} in order to deal with GUIs
     * becoming too big to fit the terminal. The background area of the GUI is the component passed in as the
     * {@code background} parameter, forced to full size.
     * @param screen Screen to use as the backend for drawing operations
     * @param windowManager Window manager implementation to use
     * @param background Component to use as the background of the GUI, behind all the windows
     */
    public MultiWindowTextGUI(
            Screen screen,
            WindowManager windowManager,
            Component background) {

        this(screen, windowManager, null, background);
    }

    /**
     * Creates a new {@code MultiWindowTextGUI} that uses the specified {@code Screen} as the backend for all drawing
     * operations. The screen will be automatically wrapped in a {@code VirtualScreen} in order to deal with GUIs
     * becoming too big to fit the terminal. The background area of the GUI is the component passed in as the
     * {@code background} parameter, forced to full size.
     * @param screen Screen to use as the backend for drawing operations
     * @param windowManager Window manager implementation to use
     * @param postRenderer {@code WindowPostRenderer} object to invoke after each window has been drawn
     * @param background Component to use as the background of the GUI, behind all the windows
     */
    public MultiWindowTextGUI(
            Screen screen,
            WindowManager windowManager,
            WindowPostRenderer postRenderer,
            Component background) {

        this(new SameTextGUIThread.Factory(), screen, windowManager, postRenderer, background);
    }

    /**
     * Creates a new {@code MultiWindowTextGUI} that uses the specified {@code Screen} as the backend for all drawing
     * operations. The screen will be automatically wrapped in a {@code VirtualScreen} in order to deal with GUIs
     * becoming too big to fit the terminal. The background area of the GUI is the component passed in as the
     * {@code background} parameter, forced to full size.
     * @param guiThreadFactory Factory implementation to use when creating the {@code TextGUIThread}
     * @param screen Screen to use as the backend for drawing operations
     * @param windowManager Window manager implementation to use
     * @param postRenderer {@code WindowPostRenderer} object to invoke after each window has been drawn
     * @param background Component to use as the background of the GUI, behind all the windows
     */
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
        this.backgroundPane = new AbstractBasePane<BasePane>() {
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

            BasePane self() { return this; }
        };
        this.backgroundPane.setComponent(background);
        this.windows = new LinkedList<Window>();
        this.windowRenderBufferCache = new IdentityHashMap<Window, TextImage>();
        this.postRenderer = postRenderer;
        this.eofWhenNoWindows = false;
    }

    @Override
    public synchronized boolean isPendingUpdate() {
        for(Window window: windows) {
            if(window.isVisible() && window.isInvalid()) {
                return true;
            }
        }
        return super.isPendingUpdate() || backgroundPane.isInvalid() || windowManager.isInvalid();
    }

    @Override
    public synchronized void updateScreen() throws IOException {
        TerminalSize minimumTerminalSize = TerminalSize.ZERO;
        for(Window window: windows) {
            if(window.isVisible()) {
                if (window.getHints().contains(Window.Hint.FULL_SCREEN) ||
                        window.getHints().contains(Window.Hint.FIT_TERMINAL_WINDOW) ||
                        window.getHints().contains(Window.Hint.EXPANDED)) {
                    //Don't take full screen windows or auto-sized windows into account
                    continue;
                }
                TerminalPosition lastPosition = window.getPosition();
                minimumTerminalSize = minimumTerminalSize.max(
                        //Add position to size to get the bottom-right corner of the window
                        window.getDecoratedSize().withRelative(
                                Math.max(lastPosition.getColumn(), 0),
                                Math.max(lastPosition.getRow(), 0)));
            }
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
        drawBackgroundPane(graphics);
        getWindowManager().prepareWindows(this, Collections.unmodifiableList(windows), graphics.getSize());
        for(Window window: windows) {
            if (window.isVisible()) {
                // First draw windows to a buffer, then copy it to the real destination. This is to make physical off-screen
                // drawing work better. Store the buffers in a cache so we don't have to re-create them every time.
                TextImage textImage = windowRenderBufferCache.get(window);
                if (textImage == null || !textImage.getSize().equals(window.getDecoratedSize())) {
                    textImage = new BasicTextImage(window.getDecoratedSize());
                    windowRenderBufferCache.put(window, textImage);
                }
                TextGUIGraphics windowGraphics = new DefaultTextGUIGraphics(this, textImage.newTextGraphics());
                TerminalPosition contentOffset = TerminalPosition.TOP_LEFT_CORNER;
                if (!window.getHints().contains(Window.Hint.NO_DECORATIONS)) {
                    WindowDecorationRenderer decorationRenderer = getWindowManager().getWindowDecorationRenderer(window);
                    windowGraphics = decorationRenderer.draw(this, windowGraphics, window);
                    contentOffset = decorationRenderer.getOffset(window);
                }

                window.draw(windowGraphics);
                window.setContentOffset(contentOffset);
                Borders.joinLinesWithFrame(windowGraphics);

                graphics.drawImage(window.getPosition(), textImage);

                if(!window.getHints().contains(Window.Hint.NO_POST_RENDERING)) {
                    if (window.getPostRenderer() != null) {
                        window.getPostRenderer().postRender(graphics, this, window);
                    }
                    else if (postRenderer != null) {
                        postRenderer.postRender(graphics, this, window);
                    }
                    else if (getTheme().getWindowPostRenderer() != null) {
                        getTheme().getWindowPostRenderer().postRender(graphics, this, window);
                    }
                }
            }
        }

        // Purge the render buffer cache from windows that have been removed
        windowRenderBufferCache.keySet().retainAll(windows);
    }

    private void drawBackgroundPane(TextGUIGraphics graphics) {
        backgroundPane.draw(new DefaultTextGUIGraphics(this, graphics));
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
            setActiveWindow(window);
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
        changeWindow: if(activeWindow == window) {
            //Go backward in reverse and find the first suitable window
            for(int index = windows.size() - 1; index >= 0; index--) {
                Window candidate = windows.get(index);
                if(!candidate.getHints().contains(Window.Hint.NO_FOCUS)) {
                    setActiveWindow(candidate);
                    break changeWindow;
                }
            }
            // No suitable window was found, so pass control back
            // to the background pane
            setActiveWindow(null);
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
        if (activeWindow != null) moveToTop(activeWindow);
        return this;
    }

    @Override
    public synchronized Window getActiveWindow() {
        return activeWindow;
    }

    @Override
    public BasePane getBackgroundPane() {
        return backgroundPane;
    }

    @Override
    public Screen getScreen() {
        return virtualScreen;
    }

    @Override
    public WindowPostRenderer getWindowPostRenderer() {
        return postRenderer;
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

    /**
     * Switches the active window by cyclically shuffling the window list. If {@code reverse} parameter is {@code false}
     * then the current top window is placed at the bottom of the stack and the window immediately behind it is the new
     * top. If {@code reverse} is set to {@code true} then the window at the bottom of the stack is moved up to the
     * front and the previous top window will be immediately below it
     * @param reverse Direction to cycle through the windows
     * @return Itself
     */
    public synchronized WindowBasedTextGUI cycleActiveWindow(boolean reverse) {
        if(windows.isEmpty() || windows.size() == 1 || (activeWindow != null && activeWindow.getHints().contains(Window.Hint.MODAL))) {
            return this;
        }
        Window originalActiveWindow = activeWindow;
        Window nextWindow;
        if(activeWindow == null) {
            // Cycling out of active background pane
            nextWindow = reverse ? windows.get(windows.size() - 1) : windows.get(0);
        }
        else {
            // Switch to the next window
            nextWindow = getNextWindow(reverse, activeWindow);
        }

        int noFocusWindows = 0;
        while(nextWindow.getHints().contains(Window.Hint.NO_FOCUS)) {
            ++noFocusWindows;
            if(noFocusWindows == windows.size()) {
                // All windows are NO_FOCUS, so give up
                return this;
            }
            nextWindow = getNextWindow(reverse, nextWindow);
            if(nextWindow == originalActiveWindow) {
                return this;
            }
        }

        if(reverse) {
            moveToTop(nextWindow);
        }
        else if (originalActiveWindow != null) {
            windows.remove(originalActiveWindow);
            windows.add(0, originalActiveWindow);
        }
        setActiveWindow(nextWindow);
        return this;
    }

    private Window getNextWindow(boolean reverse, Window window) {
        int index = windows.indexOf(window);
        if(reverse) {
            if(++index >= windows.size()) {
                index = 0;
            }
        }
        else {
            if(--index < 0) {
                index = windows.size() - 1;
            }
        }
        return windows.get(index);
    }
}
