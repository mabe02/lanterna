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
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.screen.VirtualScreen;

import java.io.IOException;
import java.util.Collection;

/**
 *
 * @author Martin
 */
public class MultiWindowTextGUI extends AbstractTextGUI implements WindowBasedTextGUI {
    private final VirtualScreen virtualScreen;
    private final WindowManager windowManager;
    private final BasePane backgroundPane;
    private final WindowPostRenderer postRenderer;
    private boolean eofWhenNoWindows;

    public MultiWindowTextGUI(Screen screen) {
        this(screen, TextColor.ANSI.BLUE);
    }

    public MultiWindowTextGUI(Screen screen, TextColor backgroundColor) {
        this(screen, new SimpleWindowManager(), new EmptySpace(backgroundColor));
    }

    public MultiWindowTextGUI(Screen screen, WindowManager windowManager, Component background) {
        this(screen, windowManager, new WindowShadowRenderer(), background);
    }

    public MultiWindowTextGUI(Screen screen, WindowManager windowManager, WindowPostRenderer postRenderer, Component background) {
        this(new VirtualScreen(screen), windowManager, postRenderer, background);
    }

    private MultiWindowTextGUI(VirtualScreen screen, WindowManager windowManager, WindowPostRenderer postRenderer, Component background) {
        super(screen);
        if(windowManager == null) {
            throw new IllegalArgumentException("Creating a window-based TextGUI requires a WindowManager");
        }
        if(background == null) {
            //Use a sensible default instead of throwing
            background = new EmptySpace(TextColor.ANSI.BLUE);
        }
        this.virtualScreen = screen;
        this.windowManager = windowManager;
        this.postRenderer = postRenderer;
        this.eofWhenNoWindows = false;
        this.backgroundPane = new AbstractBasePane() {
            @Override
            public TextGUI getTextGUI() {
                return MultiWindowTextGUI.this;
            }

            @Override
            public TerminalPosition toGlobal(TerminalPosition localPosition) {
                return localPosition;
            }
        };
        this.backgroundPane.setComponent(background);
        
        this.windowManager.addListener(new WindowManager.Listener() {
            @Override
            public void onWindowAdded(WindowManager manager, Window window) {
                window.setTextGUI(MultiWindowTextGUI.this);
            }

            @Override
            public void onWindowRemoved(WindowManager manager, Window window) {
                window.setTextGUI(null);
            }
        });
    }

    @Override
    public boolean isPendingUpdate() {
        return super.isPendingUpdate() || backgroundPane.isInvalid() || windowManager.isInvalid();
    }

    @Override
    public void updateScreen() throws IOException {
        TerminalSize preferredSize = TerminalSize.ONE;
        for(Window window: windowManager.getWindows()) {
            preferredSize = preferredSize.max(window.getPreferredSize());
        }
        virtualScreen.setMinimumSize(preferredSize.withRelativeColumns(10).withRelativeRows(5));
        super.updateScreen();
    }

    @Override
    protected KeyStroke readKeyStroke() throws IOException {
        KeyStroke keyStroke = super.pollInput();
        if(eofWhenNoWindows && keyStroke == null && windowManager.getWindows().isEmpty()) {
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
    protected void drawGUI(TextGUIGraphics graphics) {
        backgroundPane.draw(graphics);
        for(Window window: getWindowManager().getWindows()) {
            WindowDecorationRenderer decorationRenderer = getWindowManager().getWindowDecorationRenderer(window);
            TerminalPosition topLeft = getWindowManager().getTopLeftPosition(window, graphics.getSize());
            TerminalSize windowSize = getWindowManager().getSize(window, topLeft, graphics.getSize());
            window.setPosition(topLeft.withRelative(decorationRenderer.getOffset(window)));
            window.setDecoratedSize(windowSize);
            TextGUIGraphics windowGraphics = graphics.newTextGraphics(topLeft, windowSize);
            if(!window.getHints().contains(Window.Hint.NO_DECORATIONS)) {
                windowGraphics = decorationRenderer.draw(this, windowGraphics, window);
            }
            window.draw(windowGraphics);
            if(postRenderer != null && !window.getHints().contains(Window.Hint.NO_POST_RENDERING)) {
                postRenderer.postRender(graphics, this, window, topLeft, windowSize);
            }
        }
    }

    @Override
    public TerminalPosition getCursorPosition() {
        Window activeWindow = windowManager.getActiveWindow();
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
    public Interactable getFocusedInteractable() {
        Window activeWindow = windowManager.getActiveWindow();
        if(activeWindow != null) {
            return activeWindow.getFocusedInteractable();
        }
        else {
            return backgroundPane.getFocusedInteractable();
        }
    }

    @Override
    public boolean handleInput(KeyStroke keyStroke) {
        Window activeWindow = windowManager.getActiveWindow();
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
    public WindowBasedTextGUI addWindow(Window window) {
        windowManager.addWindow(window);
        return this;
    }

    @Override
    public WindowBasedTextGUI removeWindow(Window window) {
        windowManager.removeWindow(window);
        return this;
    }

    @Override
    public Collection<Window> getWindows() {
        return windowManager.getWindows();
    }

    @Override
    public Window getActiveWindow() {
        return windowManager.getActiveWindow();
    }

    public BasePane getBackgroundPane() {
        return backgroundPane;
    }
}
