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
 * Copyright (C) 2010-2019 Martin Berglund
 */
package com.googlecode.lanterna.screen;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.io.IOException;

/**
 * VirtualScreen wraps a normal screen and presents it as a screen that has a configurable minimum size; if the real
 * screen is smaller than this size, the presented screen will add scrolling to get around it. To anyone using this
 * class, it will appear and behave just as a normal screen. Scrolling is done by using CTRL + arrow keys.
 * <p>
 * The use case for this class is to allow you to set a minimum size that you can count on be honored, no matter how
 * small the user makes the terminal. This should make programming GUIs easier.
 * @author Martin
 */
public class VirtualScreen extends AbstractScreen {
    private final Screen realScreen;
    private final FrameRenderer frameRenderer;
    private TerminalSize minimumSize;
    private TerminalPosition viewportTopLeft;
    private TerminalSize viewportSize;

    /**
     * Creates a new VirtualScreen that wraps a supplied Screen. The screen passed in here should be the real screen
     * that is created on top of the real {@code Terminal}, it will have the correct size and content for what's
     * actually displayed to the user, but this class will present everything as one view with a fixed minimum size,
     * no matter what size the real terminal has.
     * <p>
     * The initial minimum size will be the current size of the screen.
     * @param screen Real screen that will be used when drawing the whole or partial virtual screen
     */
    public VirtualScreen(Screen screen) {
        super(screen.getTerminalSize());
        this.frameRenderer = new DefaultFrameRenderer();
        this.realScreen = screen;
        this.minimumSize = screen.getTerminalSize();
        this.viewportTopLeft = TerminalPosition.TOP_LEFT_CORNER;
        this.viewportSize = minimumSize;
    }

    /**
     * Sets the minimum size we want the virtual screen to have. If the user resizes the real terminal to something
     * smaller than this, the virtual screen will refuse to make it smaller and add scrollbars to the view.
     * @param minimumSize Minimum size we want the screen to have
     */
    public void setMinimumSize(TerminalSize minimumSize) {
        this.minimumSize = minimumSize;
        TerminalSize virtualSize = minimumSize.max(realScreen.getTerminalSize());
        if(!minimumSize.equals(virtualSize)) {
            addResizeRequest(virtualSize);
            super.doResizeIfNecessary();
        }
        calculateViewport(realScreen.getTerminalSize());
    }

    /**
     * Returns the minimum size this virtual screen can have. If the real terminal is made smaller than this, the
     * virtual screen will draw scrollbars and implement scrolling
     * @return Minimum size configured for this virtual screen
     */
    public TerminalSize getMinimumSize() {
        return minimumSize;
    }

    /**
     * Returns the current size of the viewport. This will generally match the dimensions of the underlying terminal.
     * @return Viewport size for this {@link VirtualScreen}
     */
    public TerminalSize getViewportSize() {
        return viewportSize;
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
    public void setCursorPosition(TerminalPosition position) {
        super.setCursorPosition(position);
        if(position == null) {
            realScreen.setCursorPosition(null);
            return;
        }
        position = position.withRelativeColumn(-viewportTopLeft.getColumn()).withRelativeRow(-viewportTopLeft.getRow());
        if(position.getColumn() >= 0 && position.getColumn() < viewportSize.getColumns() &&
                position.getRow() >= 0 && position.getRow() < viewportSize.getRows()) {
            realScreen.setCursorPosition(position);
        }
        else {
            realScreen.setCursorPosition(null);
        }
    }

    @Override
    public synchronized TerminalSize doResizeIfNecessary() {
        TerminalSize underlyingSize = realScreen.doResizeIfNecessary();
        if(underlyingSize == null) {
            return null;
        }

        TerminalSize newVirtualSize = calculateViewport(underlyingSize);
        if(!getTerminalSize().equals(newVirtualSize)) {
            addResizeRequest(newVirtualSize);
            return super.doResizeIfNecessary();
        }
        return newVirtualSize;
    }

    private TerminalSize calculateViewport(TerminalSize realTerminalSize) {
        TerminalSize newVirtualSize = minimumSize.max(realTerminalSize);
        if(newVirtualSize.equals(realTerminalSize)) {
            viewportSize = realTerminalSize;
            viewportTopLeft = TerminalPosition.TOP_LEFT_CORNER;
        }
        else {
            TerminalSize newViewportSize = frameRenderer.getViewportSize(realTerminalSize, newVirtualSize);
            if(newViewportSize.getRows() > viewportSize.getRows()) {
                viewportTopLeft = viewportTopLeft.withRow(Math.max(0, viewportTopLeft.getRow() - (newViewportSize.getRows() - viewportSize.getRows())));
            }
            if(newViewportSize.getColumns() > viewportSize.getColumns()) {
                viewportTopLeft = viewportTopLeft.withColumn(Math.max(0, viewportTopLeft.getColumn() - (newViewportSize.getColumns() - viewportSize.getColumns())));
            }
            viewportSize = newViewportSize;
        }
        return newVirtualSize;
    }

    @Override
    public void refresh(RefreshType refreshType) throws IOException {
        setCursorPosition(getCursorPosition()); //Make sure the cursor is at the correct position
        if(!viewportSize.equals(realScreen.getTerminalSize())) {
            frameRenderer.drawFrame(
                    realScreen.newTextGraphics(),
                    realScreen.getTerminalSize(),
                    getTerminalSize(),
                    viewportTopLeft);
        }

        //Copy the rows
        TerminalPosition viewportOffset = frameRenderer.getViewportOffset();
        if(realScreen instanceof AbstractScreen) {
            AbstractScreen asAbstractScreen = (AbstractScreen)realScreen;
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
                    realScreen.setCharacter(
                            x + viewportOffset.getColumn(),
                            y + viewportOffset.getRow(),
                            getBackBuffer().getCharacterAt(
                                    x + viewportTopLeft.getColumn(),
                                    y + viewportTopLeft.getRow()));
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
        else if(keyStroke.isAltDown() && keyStroke.getKeyType() == KeyType.ArrowLeft) {
            if(viewportTopLeft.getColumn() > 0) {
                viewportTopLeft = viewportTopLeft.withRelativeColumn(-1);
                refresh();
                return null;
            }
        }
        else if(keyStroke.isAltDown() && keyStroke.getKeyType() == KeyType.ArrowRight) {
            if(viewportTopLeft.getColumn() + viewportSize.getColumns() < getTerminalSize().getColumns()) {
                viewportTopLeft = viewportTopLeft.withRelativeColumn(1);
                refresh();
                return null;
            }
        }
        else if(keyStroke.isAltDown() && keyStroke.getKeyType() == KeyType.ArrowUp) {
            if(viewportTopLeft.getRow() > 0) {
                viewportTopLeft = viewportTopLeft.withRelativeRow(-1);
                realScreen.scrollLines(0,viewportSize.getRows()-1,-1);
                refresh();
                return null;
            }
        }
        else if(keyStroke.isAltDown() && keyStroke.getKeyType() == KeyType.ArrowDown) {
            if(viewportTopLeft.getRow() + viewportSize.getRows() < getTerminalSize().getRows()) {
                viewportTopLeft = viewportTopLeft.withRelativeRow(1);
                realScreen.scrollLines(0,viewportSize.getRows()-1,1);
                refresh();
                return null;
            }
        }
        return keyStroke;
    }

    @Override
    public void scrollLines(int firstLine, int lastLine, int distance) {
        // do base class stuff (scroll own back buffer)
        super.scrollLines(firstLine, lastLine, distance);
        // vertical range visible in realScreen:
        int vpFirst = viewportTopLeft.getRow(),
            vpRows = viewportSize.getRows();
        // adapt to realScreen range:
        firstLine = Math.max(0, firstLine - vpFirst);
        lastLine = Math.min(vpRows - 1, lastLine - vpFirst);
        // if resulting range non-empty: scroll that range in realScreen:
        if (firstLine <= lastLine) {
            realScreen.scrollLines(firstLine, lastLine, distance);
        }
    }

    /**
     * Interface for rendering the virtual screen's frame when the real terminal is too small for the virtual screen
     */
    public interface FrameRenderer {
        /**
         * Given the size of the real terminal and the current size of the virtual screen, how large should the viewport
         * where the screen content is drawn be?
         * @param realSize Size of the real terminal
         * @param virtualSize Size of the virtual screen
         * @return Size of the viewport, according to this FrameRenderer
         */
        TerminalSize getViewportSize(TerminalSize realSize, TerminalSize virtualSize);

        /**
         * Where in the virtual screen should the top-left position of the viewport be? To draw the viewport from the
         * top-left position of the screen, return 0x0 (or TerminalPosition.TOP_LEFT_CORNER) here.
         * @return Position of the top-left corner of the viewport inside the screen
         */
        TerminalPosition getViewportOffset();

        /**
         * Drawn the 'frame', meaning anything that is outside the viewport (title, scrollbar, etc)
         * @param graphics Graphics to use to text drawing operations
         * @param realSize Size of the real terminal
         * @param virtualSize Size of the virtual screen
         * @param virtualScrollPosition If the virtual screen is larger than the real terminal, this is the current
         *                              scroll offset the VirtualScreen is using
         */
        void drawFrame(
                TextGraphics graphics,
                TerminalSize realSize,
                TerminalSize virtualSize,
                TerminalPosition virtualScrollPosition);
    }

    private static class DefaultFrameRenderer implements FrameRenderer {
        @Override
        public TerminalSize getViewportSize(TerminalSize realSize, TerminalSize virtualSize) {
            if(realSize.getColumns() > 1 && realSize.getRows() > 2) {
                return realSize.withRelativeColumns(-1).withRelativeRows(-2);
            }
            else {
                return realSize;
            }
        }

        @Override
        public TerminalPosition getViewportOffset() {
            return TerminalPosition.TOP_LEFT_CORNER;
        }

        @Override
        public void drawFrame(
                TextGraphics graphics,
                TerminalSize realSize,
                TerminalSize virtualSize,
                TerminalPosition virtualScrollPosition) {

            if(realSize.getColumns() == 1 || realSize.getRows() <= 2) {
                return;
            }
            TerminalSize viewportSize = getViewportSize(realSize, virtualSize);

            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            graphics.setBackgroundColor(TextColor.ANSI.BLACK);
            graphics.fill(' ');
            graphics.putString(0, graphics.getSize().getRows() - 1, "Terminal too small, use ALT+arrows to scroll");

            int horizontalSize = (int)(((double)(viewportSize.getColumns()) / (double)virtualSize.getColumns()) * (viewportSize.getColumns()));
            int scrollable = viewportSize.getColumns() - horizontalSize - 1;
            int horizontalPosition = (int)((double)scrollable * ((double)virtualScrollPosition.getColumn() / (double)(virtualSize.getColumns() - viewportSize.getColumns())));
            graphics.drawLine(
                    new TerminalPosition(horizontalPosition, graphics.getSize().getRows() - 2),
                    new TerminalPosition(horizontalPosition + horizontalSize, graphics.getSize().getRows() - 2),
                    Symbols.BLOCK_MIDDLE);

            int verticalSize = (int)(((double)(viewportSize.getRows()) / (double)virtualSize.getRows()) * (viewportSize.getRows()));
            scrollable = viewportSize.getRows() - verticalSize - 1;
            int verticalPosition = (int)((double)scrollable * ((double)virtualScrollPosition.getRow() / (double)(virtualSize.getRows() - viewportSize.getRows())));
            graphics.drawLine(
                    new TerminalPosition(graphics.getSize().getColumns() - 1, verticalPosition),
                    new TerminalPosition(graphics.getSize().getColumns() - 1, verticalPosition + verticalSize),
                    Symbols.BLOCK_MIDDLE);
        }
    }
}
