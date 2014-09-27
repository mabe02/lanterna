package com.googlecode.lanterna.screen;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.io.IOException;

/**
 * VirtualScreen wraps a normal screen and presents it as a screen that has a configurable minimum size; if the real
 * screen is smaller than this size, the presented screen will add scrolling to get around it. To anyone using this
 * class, it will appear and behave just as a normal screen.
 * @author Martin
 */
public class VirtualScreen extends AbstractScreen {
    private final Screen realScreen;
    private final FrameRenderer frameRenderer;
    private TerminalSize minimumSize;
    private TerminalPosition viewportTopLeft;
    private TerminalSize viewportSize;

    public VirtualScreen(Screen screen) {
        super(screen.getTerminalSize());
        this.frameRenderer = new DefaultFrameRenderer();
        this.realScreen = screen;
        this.minimumSize = screen.getTerminalSize();
        this.viewportTopLeft = TerminalPosition.TOP_LEFT_CORNER;
        this.viewportSize = minimumSize;
    }

    public void setMinimumSize(TerminalSize minimumSize) {
        this.minimumSize = minimumSize;
        TerminalSize virtualSize = minimumSize.max(getTerminalSize());
        if(!minimumSize.equals(virtualSize)) {
            addResizeRequest(virtualSize);
            super.doResizeIfNecessary();
        }
    }

    public TerminalSize getMinimumSize() {
        return minimumSize;
    }

    @Override
    public void startScreen() throws IOException {
        realScreen.startScreen();
    }

    @Override
    public void stopScreen() throws IOException {
        realScreen.stopScreen();
    }

    @Override
    public TextCharacter getFrontCharacter(TerminalPosition position) {
        return null;
    }

    @Override
    public synchronized TerminalSize doResizeIfNecessary() {
        TerminalSize underlyingSize = realScreen.doResizeIfNecessary();
        if(underlyingSize == null) {
            return null;
        }

        TerminalSize newVirtualSize = minimumSize.max(underlyingSize);
        if(newVirtualSize.equals(underlyingSize)) {
            viewportSize = underlyingSize;
            viewportTopLeft = TerminalPosition.TOP_LEFT_CORNER;
        }
        else {
            TerminalSize newViewportSize = frameRenderer.getViewportSize(underlyingSize, newVirtualSize);
            if(newViewportSize.getRows() > viewportSize.getRows()) {
                viewportTopLeft = viewportTopLeft.withRow(Math.max(0, viewportTopLeft.getRow() - (newViewportSize.getRows() - viewportSize.getRows())));
            }
            if(newViewportSize.getColumns() > viewportSize.getColumns()) {
                viewportTopLeft = viewportTopLeft.withColumn(Math.max(0, viewportTopLeft.getColumn() - (newViewportSize.getColumns() - viewportSize.getColumns())));
            }
            viewportSize = newViewportSize;
        }
        if(!getTerminalSize().equals(newVirtualSize)) {
            addResizeRequest(newVirtualSize);
            return super.doResizeIfNecessary();
        }
        return newVirtualSize;
    }

    @Override
    public void refresh(RefreshType refreshType) throws IOException {
        if(!viewportSize.equals(realScreen.getTerminalSize())) {
            frameRenderer.drawFrame(realScreen.newTextGraphics(), viewportTopLeft, viewportSize, getTerminalSize());
        }

        //Copy the rows
        if(realScreen instanceof AbstractScreen) {
            AbstractScreen asAbstractScreen = (AbstractScreen)realScreen;
            TerminalPosition viewportOffset = frameRenderer.getViewportOffset();
            getBackBuffer().copyTo(
                    asAbstractScreen.getBackBuffer(),
                    viewportTopLeft.getRow(),
                    viewportSize.getRows(),
                    viewportTopLeft.getColumn(),
                    viewportSize.getColumns(),
                    viewportOffset.getRow(),
                    viewportOffset.getColumn());
        }
        else {
            for(int y = 0; y < viewportSize.getRows(); y++) {
                for(int x = 0; x < viewportSize.getColumns(); x++) {
                    realScreen.setCharacter(x, y, getBackBuffer().getCharacterAt(x + viewportTopLeft.getColumn(), y + viewportTopLeft.getRow()));
                }
            }
        }
        realScreen.refresh(refreshType);
    }

    @Override
    public KeyStroke pollInput() throws IOException {
        return filter(realScreen.pollInput());
    }

    @Override
    public KeyStroke readInput() throws IOException {
        return filter(realScreen.readInput());
    }

    private KeyStroke filter(KeyStroke keyStroke) throws IOException {
        if(keyStroke == null) {
            return null;
        }
        else if(keyStroke.isCtrlDown() && keyStroke.getKeyType() == KeyType.ArrowLeft) {
            if(viewportTopLeft.getColumn() > 0) {
                viewportTopLeft = viewportTopLeft.withRelativeColumn(-1);
                refresh();
                return null;
            }
        }
        else if(keyStroke.isCtrlDown() && keyStroke.getKeyType() == KeyType.ArrowRight) {
            if(viewportTopLeft.getColumn() + viewportSize.getColumns() < getTerminalSize().getColumns()) {
                viewportTopLeft = viewportTopLeft.withRelativeColumn(1);
                refresh();
                return null;
            }
        }
        else if(keyStroke.isCtrlDown() && keyStroke.getKeyType() == KeyType.ArrowUp) {
            if(viewportTopLeft.getRow() > 0) {
                viewportTopLeft = viewportTopLeft.withRelativeRow(-1);
                refresh();
                return null;
            }
        }
        else if(keyStroke.isCtrlDown() && keyStroke.getKeyType() == KeyType.ArrowDown) {
            if(viewportTopLeft.getRow() + viewportSize.getRows() < getTerminalSize().getRows()) {
                viewportTopLeft = viewportTopLeft.withRelativeRow(1);
                refresh();
                return null;
            }
        }
        return keyStroke;
    }

    public static interface FrameRenderer {
        TerminalSize getViewportSize(TerminalSize realSize, TerminalSize virtualSize);
        TerminalPosition getViewportOffset();
        void drawFrame(
                TextGraphics graphics,
                TerminalPosition viewportTopLeft,
                TerminalSize viewportSize,
                TerminalSize virtualSize);
    }

    private static class DefaultFrameRenderer implements FrameRenderer {
        @Override
        public TerminalSize getViewportSize(TerminalSize realSize, TerminalSize virtualSize) {
            return realSize.withRelativeColumns(-1).withRelativeRows(-2);
        }

        @Override
        public TerminalPosition getViewportOffset() {
            return TerminalPosition.TOP_LEFT_CORNER;
        }

        @Override
        public void drawFrame(TextGraphics graphics, TerminalPosition viewportTopLeft, TerminalSize viewportSize, TerminalSize virtualSize) {
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            graphics.setBackgroundColor(TextColor.ANSI.BLACK);
            graphics.fill(' ');
            graphics.putString(0, graphics.getSize().getRows() - 1, "Terminal too small, use ctrl+arrows to scroll");

            int horizontalSize = (int)(((double)(viewportSize.getColumns()) / (double)virtualSize.getColumns()) * (viewportSize.getColumns()));
            int scrollable = viewportSize.getColumns() - horizontalSize - 1;
            int horizontalPosition = (int)((double)scrollable * ((double)viewportTopLeft.getColumn() / (double)(virtualSize.getColumns() - viewportSize.getColumns())));
            graphics.setPosition(horizontalPosition, graphics.getSize().getRows() - 2);
            graphics.drawLine(new TerminalPosition(horizontalPosition + horizontalSize, graphics.getSize().getRows() - 2), ACS.BLOCK_MIDDLE);

            int verticalSize = (int)(((double)(viewportSize.getRows()) / (double)virtualSize.getRows()) * (viewportSize.getRows()));
            scrollable = viewportSize.getRows() - verticalSize - 1;
            int verticalPosition = (int)((double)scrollable * ((double)viewportTopLeft.getRow() / (double)(virtualSize.getRows() - viewportSize.getRows())));
            graphics.setPosition(graphics.getSize().getColumns() - 1, verticalPosition);
            graphics.drawLine(new TerminalPosition(graphics.getSize().getColumns() - 1, verticalPosition + verticalSize), ACS.BLOCK_MIDDLE);
        }
    }
}
