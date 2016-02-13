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
package com.googlecode.lanterna.terminal.swing;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.IOSafeTerminal;
import com.googlecode.lanterna.terminal.ResizeListener;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * This is the class that does the heavy lifting for both {@link AWTTerminal} and {@link SwingTerminal}. It maintains
 * most of the external terminal state and also the main back buffer that is copied to the components area on draw
 * operations.
 *
 * @author martin
 */
@SuppressWarnings("serial")
abstract class GraphicalTerminalImplementation implements IOSafeTerminal {
    private final TerminalEmulatorDeviceConfiguration deviceConfiguration;
    private final TerminalEmulatorColorConfiguration colorConfiguration;
    private final VirtualTerminal virtualTerminal;
    private final BlockingQueue<KeyStroke> keyQueue;
    private final List<ResizeListener> resizeListeners;

    private final String enquiryString;
    private final EnumSet<SGR> activeSGRs;
    private TextColor foregroundColor;
    private TextColor backgroundColor;

    private volatile boolean cursorIsVisible;
    private volatile Timer blinkTimer;
    private volatile boolean hasBlinkingText;
    private volatile boolean blinkOn;
    private volatile boolean flushed;

    // We use two different data structures to optimize drawing
    //  * A map (as a two-dimensional array) of all characters currently visible inside this component
    //  * A backbuffer with the graphics content
    //
    // The buffer is the most important one as it allows us to re-use what was drawn earlier. It is not reset on every
    // drawing operation but updates just in those places where the map tells us the character has changed. Note that
    // when the component is resized, we always update the whole buffer.
    //
    // DON'T RELY ON THESE FOR SIZE! We make it a big bigger than necessary to make resizing smoother. Use the AWT/Swing
    // methods to get the correct dimensions or use {@code getTerminalSize()} to get the size in terminal space.
    private CharacterState[][] visualState;
    private BufferedImage backbuffer;

    /**
     * Creates a new GraphicalTerminalImplementation component using custom settings and a custom scroll controller. The
     * scrolling controller will be notified when the terminal's history size grows and will be called when this class
     * needs to figure out the current scrolling position.
     * @param initialTerminalSize Initial size of the terminal, which will be used when calculating the preferred size
     *                            of the component. If null, it will default to 80x25. If the AWT layout manager forces
     *                            the component to a different size, the value of this parameter won't have any meaning
     * @param deviceConfiguration Device configuration to use for this SwingTerminal
     * @param colorConfiguration Color configuration to use for this SwingTerminal
     * @param scrollController Controller to use for scrolling, the object passed in will be notified whenever the
     *                         scrollable area has changed
     */
    public GraphicalTerminalImplementation(
            TerminalSize initialTerminalSize,
            TerminalEmulatorDeviceConfiguration deviceConfiguration,
            TerminalEmulatorColorConfiguration colorConfiguration,
            TerminalScrollController scrollController) {

        //This is kind of meaningless since we don't know how large the
        //component is at this point, but we should set it to something
        if(initialTerminalSize == null) {
            initialTerminalSize = new TerminalSize(80, 24);
        }
        this.virtualTerminal = new VirtualTerminal(
                deviceConfiguration.getLineBufferScrollbackSize(),
                initialTerminalSize,
                scrollController);
        this.keyQueue = new LinkedBlockingQueue<KeyStroke>();
        this.resizeListeners = new CopyOnWriteArrayList<ResizeListener>();
        this.deviceConfiguration = deviceConfiguration;
        this.colorConfiguration = colorConfiguration;

        this.activeSGRs = EnumSet.noneOf(SGR.class);
        this.foregroundColor = TextColor.ANSI.DEFAULT;
        this.backgroundColor = TextColor.ANSI.DEFAULT;
        this.cursorIsVisible = true;        //Always start with an activate and visible cursor
        this.enquiryString = "TerminalEmulator";
        this.visualState = new CharacterState[48][160];
        this.backbuffer = null;  // We don't know the dimensions yet
        this.blinkTimer = null;
        this.hasBlinkingText = false;   // Assume initial content doesn't have any blinking text
        this.blinkOn = true;
        this.flushed = false;

        //Set the initial scrollable size
        //scrollObserver.newScrollableLength(fontConfiguration.getFontHeight() * terminalSize.getRows());
    }

    ///////////
    // First abstract methods that are implemented in AWTTerminalImplementation and SwingTerminalImplementation
    ///////////

    /**
     * Used to find out the font height, in pixels
     * @return Terminal font height in pixels
     */
    protected abstract int getFontHeight();

    /**
     * Used to find out the font width, in pixels
     * @return Terminal font width in pixels
     */
    protected abstract int getFontWidth();

    /**
     * Used when requiring the total height of the terminal component, in pixels
     * @return Height of the terminal component, in pixels
     */
    protected abstract int getHeight();

    /**
     * Used when requiring the total width of the terminal component, in pixels
     * @return Width of the terminal component, in pixels
     */
    protected abstract int getWidth();

    /**
     * Returning the AWT font to use for the specific character. This might not always be the same, in case a we are
     * trying to draw an unusual character (probably CJK) which isn't contained in the standard terminal font.
     * @param character Character to get the font for
     * @return Font to be used for this character
     */
    protected abstract Font getFontForCharacter(TextCharacter character);

    /**
     * Returns {@code true} if anti-aliasing is enabled, {@code false} otherwise
     * @return {@code true} if anti-aliasing is enabled, {@code false} otherwise
     */
    protected abstract boolean isTextAntiAliased();

    /**
     * Called by the {@code GraphicalTerminalImplementation} when it would like the OS to schedule a repaint of the
     * window
     */
    protected abstract void repaint();

    /**
     * Start the timer that triggers blinking
     */
    protected synchronized void startBlinkTimer() {
        if(blinkTimer != null) {
            // Already on!
            return;
        }
        blinkTimer = new Timer("LanternaTerminalBlinkTimer", true);
        blinkTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                blinkOn = !blinkOn;
                if(hasBlinkingText) {
                    repaint();
                }
            }
        }, deviceConfiguration.getBlinkLengthInMilliSeconds(), deviceConfiguration.getBlinkLengthInMilliSeconds());
    }

    /**
     * Stops the timer the triggers blinking
     */
    protected synchronized void stopBlinkTimer() {
        if(blinkTimer == null) {
            // Already off!
            return;
        }
        blinkTimer.cancel();
        blinkTimer = null;
    }

    ///////////
    // First implement all the Swing-related methods
    ///////////
    /**
     * Calculates the preferred size of this terminal
     * @return Preferred size of this terminal
     */
    synchronized Dimension getPreferredSize() {
        return new Dimension(getFontWidth() * virtualTerminal.getSize().getColumns(),
                getFontHeight() * virtualTerminal.getSize().getRows());
    }

    /**
     * Updates the back buffer (if necessary) and draws it to the component's surface
     * @param componentGraphics Object to use when drawing to the component's surface
     */
    protected synchronized void paintComponent(Graphics componentGraphics) {
        //First, resize the buffer width/height if necessary
        int fontWidth = getFontWidth();
        int fontHeight = getFontHeight();
        //boolean antiAliasing = fontConfiguration.isAntiAliased();
        int widthInNumberOfCharacters = getWidth() / fontWidth;
        int visibleRows = getHeight() / fontHeight;
        boolean terminalResized = false;

        //Don't let size be less than 1
        widthInNumberOfCharacters = Math.max(1, widthInNumberOfCharacters);
        visibleRows = Math.max(1, visibleRows);

        //scrollObserver.updateModel(currentBuffer.getNumberOfLines(), visibleRows);
        TerminalSize terminalSize = virtualTerminal.getSize().withColumns(widthInNumberOfCharacters).withRows(visibleRows);
        if(!terminalSize.equals(virtualTerminal.getSize())) {
            virtualTerminal.resize(terminalSize);
            for(ResizeListener listener: resizeListeners) {
                listener.onResized(this, terminalSize);
            }
            terminalResized = true;
            ensureVisualStateHasRightSize(terminalSize);
        }
        ensureBackbufferHasRightSize();

        // At this point, if the user hasn't asked for an explicit flush, just paint the backbuffer. It's prone to
        // problems if the user isn't flushing properly but it reduces flickering when resizing the window and the code
        // is asynchronously responding to the resize
        //if(flushed) {
            updateBackBuffer(fontWidth, fontHeight, terminalResized, terminalSize);
            flushed = false;
        //}

        componentGraphics.drawImage(backbuffer, 0, 0, getWidth(), getHeight(), 0, 0, getWidth(), getHeight(), null);

        // Dispose the graphic objects
        componentGraphics.dispose();

        // Tell anyone waiting on us that drawing is complete
        notifyAll();
    }

    private void updateBackBuffer(int fontWidth, int fontHeight, boolean terminalResized, TerminalSize terminalSize) {
        //Retrieve the position of the cursor, relative to the scrolling state
        TerminalPosition translatedCursorPosition = virtualTerminal.getTranslatedCursorPosition();

        //Setup the graphics object
        Graphics2D backbufferGraphics = backbuffer.createGraphics();
        backbufferGraphics.setColor(colorConfiguration.toAWTColor(TextColor.ANSI.DEFAULT, false, false));
        backbufferGraphics.fillRect(0, 0, getWidth(), getHeight());

        if(isTextAntiAliased()) {
            backbufferGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            backbufferGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        }

        // Draw line by line, character by character
        // Initiate the blink state to whatever the cursor is using, since if the cursor is blinking then we always want
        // to do the blink repaint
        boolean foundBlinkingCharacters = deviceConfiguration.isCursorBlinking();
        int rowIndex = 0;
        for(List<TextCharacter> row: virtualTerminal.getLines()) {
            for(int columnIndex = 0; columnIndex < row.size(); columnIndex++) {
                //Any extra characters from the virtual terminal that doesn't fit can be discarded
                if(columnIndex >= terminalSize.getColumns()) {
                    continue;
                }

                TextCharacter character = row.get(columnIndex);
                boolean atCursorLocation = translatedCursorPosition.equals(columnIndex, rowIndex);
                //If next position is the cursor location and this is a CJK character (i.e. cursor is on the padding),
                //consider this location the cursor position since otherwise the cursor will be skipped
                if(!atCursorLocation &&
                        translatedCursorPosition.getColumn() == columnIndex + 1 &&
                        translatedCursorPosition.getRow() == rowIndex &&
                        TerminalTextUtils.isCharCJK(character.getCharacter())) {
                    atCursorLocation = true;
                }
                int characterWidth = fontWidth * (TerminalTextUtils.isCharCJK(character.getCharacter()) ? 2 : 1);

                Color foregroundColor = deriveTrueForegroundColor(character, atCursorLocation);
                Color backgroundColor = deriveTrueBackgroundColor(character, atCursorLocation);

                boolean drawCursor = atCursorLocation &&
                        (!deviceConfiguration.isCursorBlinking() ||     //Always draw if the cursor isn't blinking
                                (deviceConfiguration.isCursorBlinking() && blinkOn));    //If the cursor is blinking, only draw when blinkOn is true

                CharacterState characterState = new CharacterState(character, foregroundColor, backgroundColor, drawCursor);
                //if(!characterState.equals(visualState[rowIndex][columnIndex]) || terminalResized) {
                    drawCharacter(backbufferGraphics,
                            character,
                            columnIndex,
                            rowIndex,
                            foregroundColor,
                            backgroundColor,
                            fontWidth,
                            fontHeight,
                            characterWidth,
                            drawCursor);
                    visualState[rowIndex][columnIndex] = characterState;
                    if(TerminalTextUtils.isCharCJK(character.getCharacter())) {
                        visualState[rowIndex][columnIndex+1] = characterState;
                    }
                //}

                if(character.getModifiers().contains(SGR.BLINK)) {
                    foundBlinkingCharacters = true;
                }
                if(TerminalTextUtils.isCharCJK(character.getCharacter())) {
                    columnIndex++; //Skip the trailing space after a CJK character
                }
            }
            rowIndex++;
        }

        // Take care of the left-over area at the bottom and right of the component where no character can fit
        int leftoverHeight = getHeight() % fontHeight;
        int leftoverWidth = getWidth() % fontWidth;
        backbufferGraphics.setColor(Color.BLACK);
        if(leftoverWidth > 0) {
            backbufferGraphics.fillRect(getWidth() - leftoverWidth, 0, leftoverWidth, getHeight());
        }
        if(leftoverHeight > 0) {
            backbufferGraphics.fillRect(0, getHeight() - leftoverHeight, getWidth(), leftoverHeight);
        }
        backbufferGraphics.dispose();

        // Update the blink status according to if there were any blinking characters or not
        this.hasBlinkingText = foundBlinkingCharacters;
    }

    private void ensureBackbufferHasRightSize() {
        if(backbuffer == null) {
            backbuffer = new BufferedImage(getWidth() * 2, getHeight() * 2, BufferedImage.TYPE_INT_RGB);
        }
        if(backbuffer.getWidth() < getWidth() || backbuffer.getWidth() > getWidth() * 4 ||
                backbuffer.getHeight() < getHeight() || backbuffer.getHeight() > getHeight() * 4) {
            BufferedImage newBackbuffer = new BufferedImage(Math.max(getWidth(), 1) * 2, Math.max(getHeight(), 1) * 2, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = newBackbuffer.createGraphics();
            graphics.drawImage(backbuffer, 0, 0, null);
            graphics.dispose();
            backbuffer = newBackbuffer;
        }
    }

    private void ensureVisualStateHasRightSize(TerminalSize terminalSize) {
        if(visualState == null) {
            visualState = new CharacterState[terminalSize.getRows() * 2][terminalSize.getColumns() * 2];
        }
        if(visualState.length < terminalSize.getRows() || visualState.length > Math.max(terminalSize.getRows(), 1) * 4) {
            visualState = Arrays.copyOf(visualState, terminalSize.getRows() * 2);
        }
        for(int rowIndex = 0; rowIndex < visualState.length; rowIndex++) {
            CharacterState[] row = visualState[rowIndex];
            if(row == null) {
                row = new CharacterState[terminalSize.getColumns() * 2];
                visualState[rowIndex] = row;
            }
            if(row.length < terminalSize.getColumns() || row.length > Math.max(terminalSize.getColumns(), 1) * 4) {
                row = Arrays.copyOf(row, terminalSize.getColumns() * 2);
                visualState[rowIndex] = row;
            }

            // Make sure all items outside the 'real' terminal size are null
            if(rowIndex < terminalSize.getRows()) {
                Arrays.fill(row, terminalSize.getColumns(), row.length, null);
            }
            else {
                Arrays.fill(row, null);
            }
        }
    }

    private void drawCharacter(
            Graphics g,
            TextCharacter character,
            int columnIndex,
            int rowIndex,
            Color foregroundColor,
            Color backgroundColor,
            int fontWidth,
            int fontHeight,
            int characterWidth,
            boolean drawCursor) {

        int x = columnIndex * fontWidth;
        int y = rowIndex * fontHeight;
        g.setColor(backgroundColor);
        g.setClip(x, y, characterWidth, fontHeight);
        g.fillRect(x, y, characterWidth, fontHeight);

        g.setColor(foregroundColor);
        Font font = getFontForCharacter(character);
        g.setFont(font);
        FontMetrics fontMetrics = g.getFontMetrics();
        g.drawString(Character.toString(character.getCharacter()), x, ((rowIndex + 1) * fontHeight) - fontMetrics.getDescent());

        if(character.isCrossedOut()) {
            int lineStartX = x;
            int lineStartY = y + (fontHeight / 2);
            int lineEndX = lineStartX + characterWidth;
            g.drawLine(lineStartX, lineStartY, lineEndX, lineStartY);
        }
        if(character.isUnderlined()) {
            int lineStartX = x;
            int lineStartY = ((rowIndex + 1) * fontHeight) - fontMetrics.getDescent() + 1;
            int lineEndX = lineStartX + characterWidth;
            g.drawLine(lineStartX, lineStartY, lineEndX, lineStartY);
        }

        if(drawCursor) {
            if(deviceConfiguration.getCursorColor() == null) {
                g.setColor(foregroundColor);
            }
            else {
                g.setColor(colorConfiguration.toAWTColor(deviceConfiguration.getCursorColor(), false, false));
            }
            if(deviceConfiguration.getCursorStyle() == TerminalEmulatorDeviceConfiguration.CursorStyle.UNDER_BAR) {
                g.fillRect(x, y + fontHeight - 3, characterWidth, 2);
            }
            else if(deviceConfiguration.getCursorStyle() == TerminalEmulatorDeviceConfiguration.CursorStyle.VERTICAL_BAR) {
                g.fillRect(x, y + 1, 2, fontHeight - 2);
            }
        }
    }


    private Color deriveTrueForegroundColor(TextCharacter character, boolean atCursorLocation) {
        TextColor foregroundColor = character.getForegroundColor();
        TextColor backgroundColor = character.getBackgroundColor();
        boolean reverse = character.isReversed();
        boolean blink = character.isBlinking();

        if(cursorIsVisible && atCursorLocation) {
            if(deviceConfiguration.getCursorStyle() == TerminalEmulatorDeviceConfiguration.CursorStyle.REVERSED &&
                    (!deviceConfiguration.isCursorBlinking() || !blinkOn)) {
                reverse = true;
            }
        }

        if(reverse && (!blink || !blinkOn)) {
            return colorConfiguration.toAWTColor(backgroundColor, backgroundColor != TextColor.ANSI.DEFAULT, character.isBold());
        }
        else if(!reverse && blink && blinkOn) {
            return colorConfiguration.toAWTColor(backgroundColor, false, character.isBold());
        }
        else {
            return colorConfiguration.toAWTColor(foregroundColor, true, character.isBold());
        }
    }

    private Color deriveTrueBackgroundColor(TextCharacter character, boolean atCursorLocation) {
        TextColor foregroundColor = character.getForegroundColor();
        TextColor backgroundColor = character.getBackgroundColor();
        boolean reverse = character.isReversed();

        if(cursorIsVisible && atCursorLocation) {
            if(deviceConfiguration.getCursorStyle() == TerminalEmulatorDeviceConfiguration.CursorStyle.REVERSED &&
                    (!deviceConfiguration.isCursorBlinking() || !blinkOn)) {
                reverse = true;
            }
            else if(deviceConfiguration.getCursorStyle() == TerminalEmulatorDeviceConfiguration.CursorStyle.FIXED_BACKGROUND) {
                backgroundColor = deviceConfiguration.getCursorColor();
            }
        }

        if(reverse) {
            return colorConfiguration.toAWTColor(foregroundColor, backgroundColor == TextColor.ANSI.DEFAULT, character.isBold());
        }
        else {
            return colorConfiguration.toAWTColor(backgroundColor, false, false);
        }
    }

    ///////////
    // Then delegate all Terminal interface methods to the virtual terminal implementation
    //
    // Some of these methods we need to pass to the AWT-thread, which makes the call asynchronous. Hopefully this isn't
    // causing too much problem...
    ///////////
    @Override
    public KeyStroke pollInput() {
        return keyQueue.poll();
    }

    @Override
    public KeyStroke readInput() throws IOException {
        try {
            return keyQueue.take();
        }
        catch(InterruptedException ignore) {
            throw new IOException("Blocking input was interrupted");
        }
    }

    @Override
    public synchronized void enterPrivateMode() {
        virtualTerminal.switchToPrivateMode();
        clearBackBufferAndVisualState();
        flush();
    }

    @Override
    public synchronized void exitPrivateMode() {
        virtualTerminal.switchToNormalMode();
        clearBackBufferAndVisualState();
        flush();
    }

    @Override
    public synchronized void clearScreen() {
        virtualTerminal.clear();
        clearBackBufferAndVisualState();
        flush();
    }

    /**
     * Clears out the back buffer and the resets the visual state so next paint operation will do a full repaint of
     * everything
     */
    protected void clearBackBufferAndVisualState() {
        // Manually clear the backbuffer and visual state
        if(backbuffer != null) {
            Graphics2D graphics = backbuffer.createGraphics();
            Color foregroundColor = colorConfiguration.toAWTColor(TextColor.ANSI.DEFAULT, true, false);
            Color backgroundColor = colorConfiguration.toAWTColor(TextColor.ANSI.DEFAULT, false, false);
            graphics.setColor(backgroundColor);
            graphics.fillRect(0, 0, getWidth(), getHeight());
            graphics.dispose();

            for(CharacterState[] line : visualState) {
                Arrays.fill(line, new CharacterState(new TextCharacter(' '), foregroundColor, backgroundColor, false));
            }
        }
    }

    @Override
    public synchronized void setCursorPosition(final int x, final int y) {
        virtualTerminal.setCursorPosition(new TerminalPosition(x, y));
    }

    @Override
    public void setCursorVisible(final boolean visible) {
        cursorIsVisible = visible;
    }

    @Override
    public synchronized void putCharacter(final char c) {
        virtualTerminal.putCharacter(new TextCharacter(c, foregroundColor, backgroundColor, activeSGRs));
    }

    @Override
    public TextGraphics newTextGraphics() throws IOException {
        return new VirtualTerminalTextGraphics(virtualTerminal);
    }

    @Override
    public void enableSGR(final SGR sgr) {
        activeSGRs.add(sgr);
    }

    @Override
    public void disableSGR(final SGR sgr) {
        activeSGRs.remove(sgr);
    }

    @Override
    public void resetColorAndSGR() {
        foregroundColor = TextColor.ANSI.DEFAULT;
        backgroundColor = TextColor.ANSI.DEFAULT;
        activeSGRs.clear();
    }

    @Override
    public void setForegroundColor(final TextColor color) {
        foregroundColor = color;
    }

    @Override
    public void setBackgroundColor(final TextColor color) {
        backgroundColor = color;
    }

    @Override
    public synchronized TerminalSize getTerminalSize() {
        return virtualTerminal.getSize();
    }

    @Override
    public byte[] enquireTerminal(int timeout, TimeUnit timeoutUnit) {
        return enquiryString.getBytes();
    }

    @Override
    public void flush() {
        flushed = true;
        repaint();
    }

    @Override
    public void addResizeListener(ResizeListener listener) {
        resizeListeners.add(listener);
    }

    @Override
    public void removeResizeListener(ResizeListener listener) {
        resizeListeners.remove(listener);
    }

    ///////////
    // Remaining are private internal classes used by SwingTerminal
    ///////////
    private static final Set<Character> TYPED_KEYS_TO_IGNORE = new HashSet<Character>(Arrays.asList('\n', '\t', '\r', '\b', '\33', (char)127));

    /**
     * Class that translates AWT key events into Lanterna {@link KeyStroke}
     */
    protected class TerminalInputListener extends KeyAdapter {
        @Override
        public void keyTyped(KeyEvent e) {
            char character = e.getKeyChar();
            boolean altDown = (e.getModifiersEx() & InputEvent.ALT_DOWN_MASK) != 0;
            boolean ctrlDown = (e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0;

            if(!TYPED_KEYS_TO_IGNORE.contains(character)) {
                if(ctrlDown) {
                    //We need to re-adjust the character if ctrl is pressed, just like for the AnsiTerminal
                    character = (char) ('a' - 1 + character);
                }
                keyQueue.add(new KeyStroke(character, ctrlDown, altDown));
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
            boolean altDown = (e.getModifiersEx() & InputEvent.ALT_DOWN_MASK) != 0;
            boolean ctrlDown = (e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0;
            if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                keyQueue.add(new KeyStroke(KeyType.Enter, ctrlDown, altDown));
            }
            else if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                keyQueue.add(new KeyStroke(KeyType.Escape, ctrlDown, altDown));
            }
            else if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                keyQueue.add(new KeyStroke(KeyType.Backspace, ctrlDown, altDown));
            }
            else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
                keyQueue.add(new KeyStroke(KeyType.ArrowLeft, ctrlDown, altDown));
            }
            else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
                keyQueue.add(new KeyStroke(KeyType.ArrowRight, ctrlDown, altDown));
            }
            else if(e.getKeyCode() == KeyEvent.VK_UP) {
                keyQueue.add(new KeyStroke(KeyType.ArrowUp, ctrlDown, altDown));
            }
            else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
                keyQueue.add(new KeyStroke(KeyType.ArrowDown, ctrlDown, altDown));
            }
            else if(e.getKeyCode() == KeyEvent.VK_INSERT) {
                keyQueue.add(new KeyStroke(KeyType.Insert, ctrlDown, altDown));
            }
            else if(e.getKeyCode() == KeyEvent.VK_DELETE) {
                keyQueue.add(new KeyStroke(KeyType.Delete, ctrlDown, altDown));
            }
            else if(e.getKeyCode() == KeyEvent.VK_HOME) {
                keyQueue.add(new KeyStroke(KeyType.Home, ctrlDown, altDown));
            }
            else if(e.getKeyCode() == KeyEvent.VK_END) {
                keyQueue.add(new KeyStroke(KeyType.End, ctrlDown, altDown));
            }
            else if(e.getKeyCode() == KeyEvent.VK_PAGE_UP) {
                keyQueue.add(new KeyStroke(KeyType.PageUp, ctrlDown, altDown));
            }
            else if(e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
                keyQueue.add(new KeyStroke(KeyType.PageDown, ctrlDown, altDown));
            }
            else if(e.getKeyCode() == KeyEvent.VK_F1) {
                keyQueue.add(new KeyStroke(KeyType.F1, ctrlDown, altDown));
            }
            else if(e.getKeyCode() == KeyEvent.VK_F2) {
                keyQueue.add(new KeyStroke(KeyType.F2, ctrlDown, altDown));
            }
            else if(e.getKeyCode() == KeyEvent.VK_F3) {
                keyQueue.add(new KeyStroke(KeyType.F3, ctrlDown, altDown));
            }
            else if(e.getKeyCode() == KeyEvent.VK_F4) {
                keyQueue.add(new KeyStroke(KeyType.F4, ctrlDown, altDown));
            }
            else if(e.getKeyCode() == KeyEvent.VK_F5) {
                keyQueue.add(new KeyStroke(KeyType.F5, ctrlDown, altDown));
            }
            else if(e.getKeyCode() == KeyEvent.VK_F6) {
                keyQueue.add(new KeyStroke(KeyType.F6, ctrlDown, altDown));
            }
            else if(e.getKeyCode() == KeyEvent.VK_F7) {
                keyQueue.add(new KeyStroke(KeyType.F7, ctrlDown, altDown));
            }
            else if(e.getKeyCode() == KeyEvent.VK_F8) {
                keyQueue.add(new KeyStroke(KeyType.F8, ctrlDown, altDown));
            }
            else if(e.getKeyCode() == KeyEvent.VK_F9) {
                keyQueue.add(new KeyStroke(KeyType.F9, ctrlDown, altDown));
            }
            else if(e.getKeyCode() == KeyEvent.VK_F10) {
                keyQueue.add(new KeyStroke(KeyType.F10, ctrlDown, altDown));
            }
            else if(e.getKeyCode() == KeyEvent.VK_F11) {
                keyQueue.add(new KeyStroke(KeyType.F11, ctrlDown, altDown));
            }
            else if(e.getKeyCode() == KeyEvent.VK_F12) {
                keyQueue.add(new KeyStroke(KeyType.F12, ctrlDown, altDown));
            }
            else if(e.getKeyCode() == KeyEvent.VK_TAB) {
                if(e.isShiftDown()) {
                    keyQueue.add(new KeyStroke(KeyType.ReverseTab, ctrlDown, altDown));
                }
                else {
                    keyQueue.add(new KeyStroke(KeyType.Tab, ctrlDown, altDown));
                }
            }
            else {
                //keyTyped doesn't catch this scenario (for whatever reason...) so we have to do it here
                if(altDown && ctrlDown && e.getKeyCode() >= 'A' && e.getKeyCode() <= 'Z') {
                    char asLowerCase = Character.toLowerCase((char) e.getKeyCode());
                    keyQueue.add(new KeyStroke(asLowerCase, true, true));
                }
            }
        }
    }

    private static class CharacterState {
        private final TextCharacter textCharacter;
        private final Color foregroundColor;
        private final Color backgroundColor;
        private final boolean drawCursor;

        CharacterState(TextCharacter textCharacter, Color foregroundColor, Color backgroundColor, boolean drawCursor) {
            this.textCharacter = textCharacter;
            this.foregroundColor = foregroundColor;
            this.backgroundColor = backgroundColor;
            this.drawCursor = drawCursor;
        }

        @Override
        public boolean equals(Object o) {
            if(this == o) {
                return true;
            }
            if(o == null || getClass() != o.getClass()) {
                return false;
            }
            CharacterState that = (CharacterState) o;
            if(drawCursor != that.drawCursor) {
                return false;
            }
            if(!textCharacter.equals(that.textCharacter)) {
                return false;
            }
            if(!foregroundColor.equals(that.foregroundColor)) {
                return false;
            }
            return backgroundColor.equals(that.backgroundColor);
        }

        @Override
        public int hashCode() {
            int result = textCharacter.hashCode();
            result = 31 * result + foregroundColor.hashCode();
            result = 31 * result + backgroundColor.hashCode();
            result = 31 * result + (drawCursor ? 1 : 0);
            return result;
        }

        @Override
        public String toString() {
            return "CharacterState{" +
                    "textCharacter=" + textCharacter +
                    ", foregroundColor=" + foregroundColor +
                    ", backgroundColor=" + backgroundColor +
                    ", drawCursor=" + drawCursor +
                    '}';
        }
    }
}
