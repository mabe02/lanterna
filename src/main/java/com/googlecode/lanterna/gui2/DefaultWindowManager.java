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
    private TerminalSize lastKnownScreenSize;

    public DefaultWindowManager() {
        this(new DefaultWindowDecorationRenderer());
    }

    public DefaultWindowManager(WindowDecorationRenderer windowDecorationRenderer) {
        this(windowDecorationRenderer, null);
    }

    public DefaultWindowManager(TerminalSize initialScreenSize) {
        this(new DefaultWindowDecorationRenderer(), initialScreenSize);
    }

    public DefaultWindowManager(WindowDecorationRenderer windowDecorationRenderer, TerminalSize initialScreenSize) {
        this.windowDecorationRenderer = windowDecorationRenderer;
        if(initialScreenSize != null) {
            this.lastKnownScreenSize = initialScreenSize;
        }
        else {
            this.lastKnownScreenSize = new TerminalSize(80, 24);
        }
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
        WindowDecorationRenderer decorationRenderer = getWindowDecorationRenderer(window);
        TerminalSize expectedDecoratedSize = decorationRenderer.getDecoratedSize(window, window.getPreferredSize());
        window.setDecoratedSize(expectedDecoratedSize);

        if(allWindows.isEmpty()) {
            window.setPosition(TerminalPosition.OFFSET_1x1);
        }
        else if(window.getHints().contains(Window.Hint.CENTERED)) {
            int left = (lastKnownScreenSize.getColumns() - expectedDecoratedSize.getColumns()) / 2;
            int top = (lastKnownScreenSize.getRows() - expectedDecoratedSize.getRows()) / 2;
            window.setPosition(new TerminalPosition(left, top));
        }
        else {
            TerminalPosition nextPosition = allWindows.get(allWindows.size() - 1).getPosition().withRelative(2, 1);
            if(nextPosition.getColumn() + expectedDecoratedSize.getColumns() > lastKnownScreenSize.getColumns() ||
                    nextPosition.getRow() + expectedDecoratedSize.getRows() > lastKnownScreenSize.getRows()) {
                nextPosition = TerminalPosition.OFFSET_1x1;
            }
            window.setPosition(nextPosition);

        }
    }

    @Override
    public void onRemoved(WindowBasedTextGUI textGUI, Window window, List<Window> allWindows) {
        //NOP
    }

    @Override
    public void prepareWindows(WindowBasedTextGUI textGUI, List<Window> allWindows, TerminalSize screenSize) {
        this.lastKnownScreenSize = screenSize;
        for(Window window: allWindows) {
            WindowDecorationRenderer decorationRenderer = getWindowDecorationRenderer(window);
            TerminalSize size = decorationRenderer.getDecoratedSize(window, window.getPreferredSize());
            TerminalPosition position = window.getPosition();

            if(window.getHints().contains(Window.Hint.FIT_TERMINAL_WINDOW) ||
                    window.getHints().contains(Window.Hint.CENTERED)) {
                //If the window is too big for the terminal, move it up towards 0x0 and if that's not enough then shrink
                //it instead
                while(position.getRow() > 0 && position.getRow() + size.getRows() > screenSize.getRows()) {
                    position = position.withRelativeRow(-1);
                }
                while(position.getColumn() > 0 && position.getColumn() + size.getColumns() > screenSize.getColumns()) {
                    position = position.withRelativeColumn(-1);
                }
                if(position.getRow() + size.getRows() > screenSize.getRows()) {
                    size = size.withRows(screenSize.getRows() - position.getRow());
                }
                if(position.getColumn() + size.getColumns() > screenSize.getColumns()) {
                    size = size.withColumns(screenSize.getColumns() - position.getColumn());
                }
            }
            if(window.getHints().contains(Window.Hint.CENTERED)) {
                int left = (lastKnownScreenSize.getColumns() - size.getColumns()) / 2;
                int top = (lastKnownScreenSize.getRows() - size.getRows()) / 2;
                position = new TerminalPosition(left, top);
            }

            window.setPosition(position);
            window.setDecoratedSize(size);
        }
    }
}
