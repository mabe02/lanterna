/*
 * This file is part of lanterna (https://github.com/mabe02/lanterna).
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
 * Copyright (C) 2010-2020 Martin Berglund
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.graphics.BasicTextImage;
import com.googlecode.lanterna.graphics.TextImage;
import com.googlecode.lanterna.input.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.*;
import com.googlecode.lanterna.screen.VirtualScreen;
import com.googlecode.lanterna.gui2.Window.Hint;

import java.io.EOFException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean
;

/**
 * This is the main Text GUI implementation built into Lanterna, supporting multiple tiled windows and a dynamic
 * background area that can be fully customized. If you want to create a text-based GUI with windows and controls,
 * it's very likely this is what you want to use.
 * <p>
 * Note: This class used to always wrap the {@link Screen} object with a {@link VirtualScreen} to ensure that the UI
 * always fits. As of 3.1.0, we don't do this anymore so when you create the {@link MultiWindowTextGUI} you can wrap
 * the screen parameter yourself if you want to keep this behavior.
 * @author Martin
 */
public class MultiWindowTextGUI extends AbstractTextGUI implements WindowBasedTextGUI {
    private final WindowManager windowManager;
    private final BasePane backgroundPane;
    private final WindowList windowList;
    private final IdentityHashMap<Window, TextImage> windowRenderBufferCache;
    private final WindowPostRenderer postRenderer;
    
    private boolean eofWhenNoWindows;
    
    private Window titleBarDragWindow;
    private TerminalPosition originWindowPosition;
    private TerminalPosition dragStart;

    /**
     * Creates a new {@code MultiWindowTextGUI} that uses the specified {@code Screen} as the backend for all drawing
     * operations. The background area of the GUI will be a solid color, depending on theme (default is blue). The
     * current thread will be used as the GUI thread for all Lanterna library operations.
     * @param screen Screen to use as the backend for drawing operations
     */
    public MultiWindowTextGUI(Screen screen) {
        this(new SameTextGUIThread.Factory(), screen);
    }

    /**
     * Creates a new {@code MultiWindowTextGUI} that uses the specified {@code Screen} as the backend for all drawing
     * operations. The background area of the GUI will be a solid color, depending on theme (default is blue). This
     * constructor allows you control the threading model for the UI.
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
     * operations. The background area of the GUI will be a solid color, depending on theme (default is blue). This
     * constructor allows you control the threading model for the UI and set a custom {@link WindowManager}.
     * @param guiThreadFactory Factory implementation to use when creating the {@code TextGUIThread}
     * @param screen Screen to use as the backend for drawing operations
     * @param windowManager Custom window manager to use
     */
    public MultiWindowTextGUI(TextGUIThreadFactory guiThreadFactory, Screen screen, WindowManager windowManager) {
        this(guiThreadFactory,
                screen,
                windowManager,
                null,
                new GUIBackdrop());
    }

    /**
     * Creates a new {@code MultiWindowTextGUI} that uses the specified {@code Screen} as the backend for all drawing
     * operations. The background area of the GUI is a solid color as decided by the {@code backgroundColor} parameter.
     * @param screen Screen to use as the backend for drawing operations
     * @param backgroundColor Color to use for the GUI background
     * @deprecated It's preferred to use a custom background component if you want to customize the background color,
     * or you should change the theme. Using this constructor won't work well with theming.
     */
    @Deprecated
    public MultiWindowTextGUI(
            Screen screen,
            TextColor backgroundColor) {

        this(screen, new DefaultWindowManager(), new EmptySpace(backgroundColor));
    }

    /**
     * Creates a new {@code MultiWindowTextGUI} that uses the specified {@code Screen} as the backend for all drawing
     * operations. The background area of the GUI will be the component supplied instead of the usual backdrop. This
     * constructor allows you to set a custom {@link WindowManager} instead of {@link DefaultWindowManager}.
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
     * operations. The background area of the GUI will be the component supplied instead of the usual backdrop. This
     * constructor allows you to set a custom {@link WindowManager} instead of {@link DefaultWindowManager} as well
     * as a custom {@link WindowPostRenderer} that can be used to tweak the appearance of any window.
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
     * operations. The background area of the GUI will be the component supplied instead of the usual backdrop. This
     * constructor allows you to set a custom {@link WindowManager} instead of {@link DefaultWindowManager} as well
     * as a custom {@link WindowPostRenderer} that can be used to tweak the appearance of any window. This constructor
     * also allows you to control the threading model for the UI.
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

        super(guiThreadFactory, screen);
        windowList = new WindowList();
        if(windowManager == null) {
            throw new IllegalArgumentException("Creating a window-based TextGUI requires a WindowManager");
        }
        if(background == null) {
            //Use a sensible default instead of throwing
            background = new GUIBackdrop();
        }
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
        this.windowRenderBufferCache = new IdentityHashMap<>();
        this.postRenderer = postRenderer;
        this.eofWhenNoWindows = false;
    }

    @Override
    public synchronized boolean isPendingUpdate() {
        for(Window window: getWindows()) {
            if(window.isVisible() && window.isInvalid()) {
                return true;
            }
        }
        return super.isPendingUpdate() || backgroundPane.isInvalid() || windowManager.isInvalid();
    }

    @Override
    public synchronized void updateScreen() throws IOException {
        if (getScreen() instanceof VirtualScreen) {
            // If the user has passed in a virtual screen, we should calculate the minimum size required and tell it.
            // Previously the constructor always wrapped the screen in a VirtualScreen, but now we need to check.
            TerminalSize minimumTerminalSize = TerminalSize.ZERO;
            for (Window window : getWindows()) {
                if (window.isVisible()) {
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
            ((VirtualScreen) getScreen()).setMinimumSize(minimumTerminalSize);
        }
        super.updateScreen();
    }

    @Override
    protected synchronized KeyStroke readKeyStroke() throws IOException {
        KeyStroke keyStroke = super.pollInput();
        if(windowList.isHadWindowAtSomePoint() && eofWhenNoWindows && keyStroke == null && getWindows().isEmpty()) {
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
        windowManager.prepareWindows(this, windowList.getWindowsInStableOrder(), graphics.getSize());
        for(Window window: getWindows()) {
            if (window.isVisible()) {
                // First draw windows to a buffer, then copy it to the real destination. This is to make physical off-screen
                // drawing work better. Store the buffers in a cache so we don't have to re-create them every time.
                TextImage textImage = windowRenderBufferCache.get(window);
                if (textImage == null || !textImage.getSize().equals(window.getDecoratedSize())) {
                    textImage = new BasicTextImage(window.getDecoratedSize());
                    windowRenderBufferCache.put(window, textImage);
                }
                TextGUIGraphics windowGraphics = new DefaultTextGUIGraphics(this, textImage.newTextGraphics());
                TextGUIGraphics insideWindowDecorationsGraphics = windowGraphics;
                TerminalPosition contentOffset = TerminalPosition.TOP_LEFT_CORNER;
                if (!window.getHints().contains(Window.Hint.NO_DECORATIONS)) {
                    WindowDecorationRenderer decorationRenderer = windowManager.getWindowDecorationRenderer(window);
                    insideWindowDecorationsGraphics = decorationRenderer.draw(this, windowGraphics, window);
                    contentOffset = decorationRenderer.getOffset(window);
                }

                window.draw(insideWindowDecorationsGraphics);
                window.setContentOffset(contentOffset);
                if (windowGraphics != insideWindowDecorationsGraphics) {
                    Borders.joinLinesWithFrame(windowGraphics);
                }

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
        windowRenderBufferCache.keySet().retainAll(getWindows());
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
     * manager. Setting this to true (off by default) will make the GUI automatically exit when the last window has been
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

    /**
     * This method used to exist to control if the virtual screen should by bypassed or not. Since 3.1.0 calling this
     * has no effect since we don't force a VirtualScreen anymore and you control it yourself when you create the GUI.
     * @param virtualScreenEnabled Not used anymore
     * @deprecated This method don't do anything anymore (as of 3.1.0)
     */
    @Override
    @Deprecated
    public void setVirtualScreenEnabled(boolean virtualScreenEnabled) {
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
        ifMouseDownPossiblyChangeActiveWindow(keyStroke);
        ifMouseDownPossiblyStartTitleDrag(keyStroke);
        ifMouseDragPossiblyMoveWindow(keyStroke);
        Window activeWindow = getActiveWindow();
        if(activeWindow != null) {
            return activeWindow.handleInput(keyStroke);
        }
        else {
            return backgroundPane.handleInput(keyStroke);
        }
    }
    
    protected synchronized void ifMouseDownPossiblyChangeActiveWindow(KeyStroke keyStroke) {
        if (!(keyStroke instanceof MouseAction)) {
            return;
        }
        MouseAction mouse = (MouseAction)keyStroke;
        if(mouse.isMouseDown()) {
            // for now, active windows do not overlap?
            // by happenstance, the last in the list in case of many overlapping will be active
            Window priorActiveWindow = getActiveWindow();
            AtomicBoolean anyHit = new AtomicBoolean(false);
            List<Window> snapshot = new ArrayList<>(getWindows());
            for (Window w : snapshot) {
                w.getBounds().whenContains(mouse.getPosition(), () -> {
                    setActiveWindow(w);
                    anyHit.set(true);
                });
            }
            // clear popup menus if they clicked onto another window or missed all windows
            if (priorActiveWindow != null && ( priorActiveWindow != getActiveWindow() || !anyHit.get() ) ) {
                if (priorActiveWindow.getHints().contains(Hint.MENU_POPUP)) {
                    priorActiveWindow.close();
                }
            }
        }
    }
    
    protected void ifMouseDownPossiblyStartTitleDrag(KeyStroke keyStroke) {
        if (!(keyStroke instanceof MouseAction)) {
            return;
        }
        MouseAction mouse = (MouseAction)keyStroke;
        if(mouse.isMouseDown()) {
            titleBarDragWindow = null;
            dragStart = null;
            Window window = getActiveWindow();
            if (window == null) {
                return;
            }
            
            if (window.getHints().contains(Hint.MENU_POPUP)) {
                // popup windows are not draggable
                return;
            }
            
            WindowDecorationRenderer decorator = windowManager.getWindowDecorationRenderer(window);
            TerminalRectangle titleBarRectangle = decorator.getTitleBarRectangle(window);
            TerminalPosition local = window.fromGlobalToDecoratedRelative(mouse.getPosition());
            titleBarRectangle.whenContains(local, () -> {
                titleBarDragWindow = window;
                originWindowPosition = titleBarDragWindow.getPosition();
                dragStart = mouse.getPosition();
            });
        }
        
    }
    protected void ifMouseDragPossiblyMoveWindow(KeyStroke keyStroke) {
        if (titleBarDragWindow == null) {
            return;
        }
        if (!(keyStroke instanceof MouseAction)) {
            return;
        }
        MouseAction mouse = (MouseAction)keyStroke;
        if(mouse.isMouseDrag()) {
            TerminalPosition mp = mouse.getPosition();
            TerminalPosition wp = originWindowPosition;
            int dx = mp.getColumn() - dragStart.getColumn();
            int dy = mp.getRow() - dragStart.getRow();
            changeWindowHintsForDragged(titleBarDragWindow);
            titleBarDragWindow.setPosition(new TerminalPosition(wp.getColumn() + dx, wp.getRow() + dy));
            // TODO ? any additional children popups (shown menus, etc) should also be moved (or just closed)
        }
        
    }
    /**
     * In order for window to be draggable, it would no longer be CENTERED.    
     * Removes Hint.CENTERED, adds Hint.FIXED_POSITION to the window hints.
     */
    protected void changeWindowHintsForDragged(Window window) {
        Set<Hint> hints = new HashSet<>(titleBarDragWindow.getHints());
        hints.remove(Hint.CENTERED);
        hints.add(Hint.FIXED_POSITION);
        titleBarDragWindow.setHints(hints);
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
        windowManager.onAdded(this, window, windowList.getWindowsInStableOrder());
        
        windowList.addWindow(window);
        
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
        boolean contained = windowList.removeWindow(window);
        if(!contained) {
            //Didn't contain this window
            return this;
        }
        window.setTextGUI(null);
        windowManager.onRemoved(this, window, windowList.getWindowsInStableOrder());
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
        return windowList.getWindowsInZOrder();
    }

    @Override
    public synchronized MultiWindowTextGUI setActiveWindow(Window activeWindow) {
        windowList.setActiveWindow(activeWindow);
        return this;
    }

    @Override
    public synchronized Window getActiveWindow() {
        return windowList.getActiveWindow();
    }

    @Override
    public BasePane getBackgroundPane() {
        return backgroundPane;
    }

    @Override
    public WindowPostRenderer getWindowPostRenderer() {
        return postRenderer;
    }

    @Override
    public synchronized WindowBasedTextGUI moveToTop(Window window) {
        windowList.moveToTop(window);
        invalidate();
        return this;
    }
    
    public synchronized WindowBasedTextGUI moveToBottom(Window window) {
        windowList.moveToBottom(window);
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
        windowList.cycleActiveWindow(reverse);
        return this;
    }
}
