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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

/**
 * This class provides a Swing implementation of the Terminal interface that is an embeddable component you can put into
 * a Swing container. The class has static helper methods for opening a new frame with a SwingTerminal as its content,
 * similar to how the SwingTerminal used to work in earlier versions of lanterna. This version supports private mode and
 * non-private mode with a scrollback history. You can customize many of the properties by supplying device
 * configuration, font configuration and color configuration when you construct the object.
 * @author martin
 */
@SuppressWarnings("serial")
public class SwingTerminal extends JComponent implements IOSafeTerminal {
    private final SwingTerminalDeviceConfiguration deviceConfiguration;
    private final SwingTerminalFontConfiguration fontConfiguration;
    private final SwingTerminalColorConfiguration colorConfiguration;
    private final VirtualTerminal virtualTerminal;
    private final BlockingQueue<KeyStroke> keyQueue;
    private final List<ResizeListener> resizeListeners;
    private final Timer blinkTimer;

    private final EnumSet<SGR> activeSGRs;
    private TextColor foregroundColor;
    private TextColor backgroundColor;
    private final String enquiryString;

    private volatile boolean cursorIsVisible;
    private volatile boolean blinkOn;

    /**
     * Creates a new SwingTerminal with all the defaults set
     */
    public SwingTerminal() {
        this(new TerminalScrollController.Null());
    }


    /**
     * Creates a new SwingTerminal with a particular scrolling controller that will be notified when the terminals
     * history size grows and will be called when this class needs to figure out the current scrolling position.
     * @param scrollController Controller for scrolling the terminal history
     */
    @SuppressWarnings("WeakerAccess")
    public SwingTerminal(TerminalScrollController scrollController) {
        this(SwingTerminalDeviceConfiguration.getDefault(),
                SwingTerminalFontConfiguration.getDefault(),
                SwingTerminalColorConfiguration.getDefault(),
                scrollController);
    }

    /**
     * Creates a new SwingTerminal component using custom settings and no scroll controller.
     * @param deviceConfiguration Device configuration to use for this SwingTerminal
     * @param fontConfiguration Font configuration to use for this SwingTerminal
     * @param colorConfiguration Color configuration to use for this SwingTerminal
     */
    public SwingTerminal(
            SwingTerminalDeviceConfiguration deviceConfiguration,
            SwingTerminalFontConfiguration fontConfiguration,
            SwingTerminalColorConfiguration colorConfiguration) {

        this(null, deviceConfiguration, fontConfiguration, colorConfiguration);
    }

    /**
     * Creates a new SwingTerminal component using custom settings and no scroll controller.
     * @param initialTerminalSize Initial size of the terminal, which will be used when calculating the preferred size
     *                            of the component. If null, it will default to 80x25. If the AWT layout manager forces
     *                            the component to a different size, the value of this parameter won't have any meaning
     * @param deviceConfiguration Device configuration to use for this SwingTerminal
     * @param fontConfiguration Font configuration to use for this SwingTerminal
     * @param colorConfiguration Color configuration to use for this SwingTerminal
     */
    public SwingTerminal(
            TerminalSize initialTerminalSize,
            SwingTerminalDeviceConfiguration deviceConfiguration,
            SwingTerminalFontConfiguration fontConfiguration,
            SwingTerminalColorConfiguration colorConfiguration) {

        this(initialTerminalSize,
                deviceConfiguration,
                fontConfiguration,
                colorConfiguration,
                new TerminalScrollController.Null());
    }

    /**
     * Creates a new SwingTerminal component using custom settings and a custom scroll controller. The scrolling
     * controller will be notified when the terminal's history size grows and will be called when this class needs to
     * figure out the current scrolling position.
     * @param deviceConfiguration Device configuration to use for this SwingTerminal
     * @param fontConfiguration Font configuration to use for this SwingTerminal
     * @param colorConfiguration Color configuration to use for this SwingTerminal
     * @param scrollController Controller to use for scrolling, the object passed in will be notified whenever the
     *                         scrollable area has changed
     */
    public SwingTerminal(
            SwingTerminalDeviceConfiguration deviceConfiguration,
            SwingTerminalFontConfiguration fontConfiguration,
            SwingTerminalColorConfiguration colorConfiguration,
            TerminalScrollController scrollController) {

        this(null, deviceConfiguration, fontConfiguration, colorConfiguration, scrollController);
    }



    /**
     * Creates a new SwingTerminal component using custom settings and a custom scroll controller. The scrolling
     * controller will be notified when the terminal's history size grows and will be called when this class needs to
     * figure out the current scrolling position.
     * @param initialTerminalSize Initial size of the terminal, which will be used when calculating the preferred size
     *                            of the component. If null, it will default to 80x25. If the AWT layout manager forces
     *                            the component to a different size, the value of this parameter won't have any meaning
     * @param deviceConfiguration Device configuration to use for this SwingTerminal
     * @param fontConfiguration Font configuration to use for this SwingTerminal
     * @param colorConfiguration Color configuration to use for this SwingTerminal
     * @param scrollController Controller to use for scrolling, the object passed in will be notified whenever the
     *                         scrollable area has changed
     */
    public SwingTerminal(
            TerminalSize initialTerminalSize,
            SwingTerminalDeviceConfiguration deviceConfiguration,
            SwingTerminalFontConfiguration fontConfiguration,
            SwingTerminalColorConfiguration colorConfiguration,
            TerminalScrollController scrollController) {

        //Enforce valid values on the input parameters
        if(deviceConfiguration == null) {
            deviceConfiguration = SwingTerminalDeviceConfiguration.getDefault();
        }
        if(fontConfiguration == null) {
            fontConfiguration = SwingTerminalFontConfiguration.getDefault();
        }
        if(colorConfiguration == null) {
            colorConfiguration = SwingTerminalColorConfiguration.getDefault();
        }

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
        this.fontConfiguration = fontConfiguration;
        this.colorConfiguration = colorConfiguration;

        this.activeSGRs = EnumSet.noneOf(SGR.class);
        this.foregroundColor = TextColor.ANSI.DEFAULT;
        this.backgroundColor = TextColor.ANSI.DEFAULT;
        this.cursorIsVisible = true;        //Always start with an activate and visible cursor
        this.enquiryString = "SwingTerminal";
        this.blinkTimer = new Timer(deviceConfiguration.getBlinkLengthInMilliSeconds(), new BlinkTimerCallback());

        //Set the initial scrollable size
        //scrollObserver.newScrollableLength(fontConfiguration.getFontHeight() * terminalSize.getRows());

        //Prevent us from shrinking beyond one character
        setMinimumSize(new Dimension(fontConfiguration.getFontWidth(), fontConfiguration.getFontHeight()));

        //noinspection unchecked
        setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.<AWTKeyStroke>emptySet());
        //noinspection unchecked
        setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, Collections.<AWTKeyStroke>emptySet());

        //Make sure the component is double-buffered to prevent flickering
        setDoubleBuffered(true);

        addKeyListener(new TerminalInputListener());
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow();
            }
        });
        addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                blinkTimer.start();
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
                blinkTimer.stop();
            }

            @Override
            public void ancestorMoved(AncestorEvent event) { }
        });
    }

    ///////////
    // First implement all the Swing-related methods
    ///////////
    @Override
    public synchronized Dimension getPreferredSize() {
        return new Dimension(fontConfiguration.getFontWidth() * virtualTerminal.getSize().getColumns(),
                fontConfiguration.getFontHeight() * virtualTerminal.getSize().getRows());
    }

    @Override
    protected synchronized void paintComponent(Graphics g) {
        //First, resize the buffer width/height if necessary
        int fontWidth = fontConfiguration.getFontWidth();
        int fontHeight = fontConfiguration.getFontHeight();
        boolean antiAliasing = fontConfiguration.isAntiAliased();
        int widthInNumberOfCharacters = getWidth() / fontWidth;
        int visibleRows = getHeight() / fontHeight;

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
        }
        //Retrieve the position of the cursor, relative to the scrolling state
        TerminalPosition translatedCursorPosition = virtualTerminal.getTranslatedCursorPosition();

        //Setup the graphics object
        if(antiAliasing) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        }

        //Fill with black to remove any previous content
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        //Draw line by line, character by character
        int rowIndex = 0;
        for(List<TextCharacter> row: virtualTerminal.getLines()) {
            for(int columnIndex = 0; columnIndex < row.size(); columnIndex++) {
                TextCharacter character = row.get(columnIndex);
                boolean atCursorLocation = translatedCursorPosition.equals(columnIndex, rowIndex);
                //If next position is the cursor location and this is a CJK character (i.e. cursor is on the padding),
                //consider this location the cursor position since otherwise the cursor will be skipped
                if(!atCursorLocation &&
                        translatedCursorPosition.getColumn() == columnIndex + 1 &&
                        translatedCursorPosition.getRow() == rowIndex &&
                        CJKUtils.isCharCJK(character.getCharacter())) {
                    atCursorLocation = true;
                }
                int characterWidth = fontWidth * (CJKUtils.isCharCJK(character.getCharacter()) ? 2 : 1);

                Color foregroundColor = deriveTrueForegroundColor(character, atCursorLocation);
                Color backgroundColor = deriveTrueBackgroundColor(character, atCursorLocation);

                g.setColor(backgroundColor);
                g.fillRect(columnIndex * fontWidth, rowIndex * fontHeight, characterWidth, fontHeight);

                g.setColor(foregroundColor);
                Font font = fontConfiguration.getFontForCharacter(character);
                g.setFont(font);
                FontMetrics fontMetrics = g.getFontMetrics();
                g.drawString(Character.toString(character.getCharacter()), columnIndex * fontWidth, ((rowIndex + 1) * fontHeight) - fontMetrics.getDescent());

                if(character.isCrossedOut()) {
                    int lineStartX = columnIndex * fontWidth;
                    int lineStartY = rowIndex * fontHeight + (fontHeight / 2);
                    int lineEndX = lineStartX + characterWidth;
                    g.drawLine(lineStartX, lineStartY, lineEndX, lineStartY);
                }
                if(character.isUnderlined()) {
                    int lineStartX = columnIndex * fontWidth;
                    int lineStartY = ((rowIndex + 1) * fontHeight) - fontMetrics.getDescent() + 1;
                    int lineEndX = lineStartX + characterWidth;
                    g.drawLine(lineStartX, lineStartY, lineEndX, lineStartY);
                }

                //Here we are drawing the cursor if the style isn't solid color or reversed
                if(atCursorLocation &&
                        (!deviceConfiguration.isCursorBlinking() ||     //Always draw if the cursor isn't blinking
                                (deviceConfiguration.isCursorBlinking() && blinkOn))) { //If the cursor is blinking, only draw when blinkOn is true
                    if(deviceConfiguration.getCursorColor() == null) {
                        g.setColor(foregroundColor);
                    }
                    else {
                        g.setColor(colorConfiguration.toAWTColor(deviceConfiguration.getCursorColor(), false, false));
                    }
                    if(deviceConfiguration.getCursorStyle() == SwingTerminalDeviceConfiguration.CursorStyle.UNDER_BAR) {
                        g.fillRect(columnIndex * fontWidth, (rowIndex * fontHeight) + fontHeight - 3, characterWidth, 2);
                    }
                    else if(deviceConfiguration.getCursorStyle() == SwingTerminalDeviceConfiguration.CursorStyle.VERTICAL_BAR) {
                        g.fillRect(columnIndex * fontWidth, rowIndex * fontHeight + 1, 2, fontHeight - 2);
                    }
                }
                if(CJKUtils.isCharCJK(character.getCharacter())) {
                    columnIndex++; //Skip the trailing space after a CJK character
                }
            }
            rowIndex++;
        }

        g.dispose();
        synchronized(this) {
            notifyAll();
        }
    }

    private Color deriveTrueForegroundColor(TextCharacter character, boolean atCursorLocation) {
        TextColor foregroundColor = character.getForegroundColor();
        TextColor backgroundColor = character.getBackgroundColor();
        boolean reverse = character.isReversed();
        boolean blink = character.isBlinking();

        if(cursorIsVisible && atCursorLocation) {
            if(deviceConfiguration.getCursorStyle() == SwingTerminalDeviceConfiguration.CursorStyle.REVERSED &&
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
            if(deviceConfiguration.getCursorStyle() == SwingTerminalDeviceConfiguration.CursorStyle.REVERSED &&
                    (!deviceConfiguration.isCursorBlinking() || !blinkOn)) {
                reverse = true;
            }
            else if(deviceConfiguration.getCursorStyle() == SwingTerminalDeviceConfiguration.CursorStyle.FIXED_BACKGROUND) {
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

    /**
     * Returns the current device configuration. Note that it is immutable and cannot be changed.
     * @return This SwingTerminal's current device configuration
     */
    public SwingTerminalDeviceConfiguration getDeviceConfiguration() {
        return deviceConfiguration;
    }

    /**
     * Returns the current font configuration. Note that it is immutable and cannot be changed.
     * @return This SwingTerminal's current font configuration
     */
    public SwingTerminalFontConfiguration getFontConfiguration() {
        return fontConfiguration;
    }

    /**
     * Returns the current color configuration. Note that it is immutable and cannot be changed.
     * @return This SwingTerminal's current color configuration
     */
    public SwingTerminalColorConfiguration getColorConfiguration() {
        return colorConfiguration;
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
        if(SwingUtilities.isEventDispatchThread()) {
            throw new UnsupportedOperationException("Cannot call SwingTerminal.readInput() on the AWT thread");
        }
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
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                repaint();
            }
        });
    }

    @Override
    public synchronized void exitPrivateMode() {
        virtualTerminal.switchToNormalMode();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                repaint();
            }
        });
    }

    @Override
    public synchronized void clearScreen() {
        virtualTerminal.clear();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                repaint();
            }
        });
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
        if(SwingUtilities.isEventDispatchThread()) {
            repaint();
        }
        else {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        repaint();
                    }
                });
            }
            catch(InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            catch(InterruptedException ignored) {
            }
        }
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
    private class BlinkTimerCallback implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            blinkOn = !blinkOn;
            repaint();
        }
    }

    private static final Set<Character> TYPED_KEYS_TO_IGNORE = new HashSet<Character>(Arrays.asList('\n', '\t', '\r', '\b', '\33', (char)127));
    private class TerminalInputListener extends KeyAdapter {
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
}
