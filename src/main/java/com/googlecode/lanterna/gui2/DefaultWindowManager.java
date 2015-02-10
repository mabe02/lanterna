package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;

import java.util.List;

/**
 * The default window manager implementation used by Lanterna
 * @author Martin
 */
public class DefaultWindowManager implements WindowManager {

    private final WindowDecorationRenderer windowDecorationRenderer;

    public DefaultWindowManager() {
        this(new DefaultWindowDecorationRenderer());
    }

    public DefaultWindowManager(WindowDecorationRenderer windowDecorationRenderer) {
        this.windowDecorationRenderer = windowDecorationRenderer;
    }

    @Override
    public boolean isInvalid() {
        return false;
    }

    @Override
    public WindowDecorationRenderer getWindowDecorationRenderer(Window window) {
        if(window.getHints().contains(Window.Hint.NO_DECORATIONS)) {
            return new EmptyWindowDecorationRenderer();
        }
        return windowDecorationRenderer;
    }

    @Override
    public void onAdded(WindowBasedTextGUI textGUI, Window window, List<Window> allWindows) {
        if(allWindows.isEmpty()) {
            window.setPosition(TerminalPosition.OFFSET_1x1);
        }
        else {
            window.setPosition(allWindows.get(allWindows.size() - 1).getPosition().withRelative(2, 1));
        }
        WindowDecorationRenderer decorationRenderer = getWindowDecorationRenderer(window);
        TerminalSize expectedDecoratedSize = decorationRenderer.getDecoratedSize(window, window.getPreferredSize());
        window.setDecoratedSize(expectedDecoratedSize);
    }

    @Override
    public void onRemoved(WindowBasedTextGUI textGUI, Window window, List<Window> allWindows) {
        //NOP
    }

    @Override
    public void prepareWindows(WindowBasedTextGUI textGUI, List<Window> allWindows, TerminalSize screenSize) {
        for(Window window: allWindows) {
            //Recalculate size + decorations
            //Adjust if hinted
            //Center if hinted
            //Update window properties
        }
    }
}
