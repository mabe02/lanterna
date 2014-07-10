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
package com.googlecode.lanterna.terminal.swing;

import com.googlecode.lanterna.CJKUtils;
import com.googlecode.lanterna.input.KeyDecodingProfile;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.IOSafeTerminal;
import com.googlecode.lanterna.terminal.ResizeListener;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.terminal.TextColor;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

/**
 * This class provides a Swing implementation of the Terminal interface that is an embeddable component you can put into
 * a Swing container. The class has static helper methods for opening a new frame with a SwingTerminal as its content,
 * similar to how the SwingTerminal used to work in earlier versions of lanterna.
 * @author martin
 */
public class SwingTerminal extends JComponent implements IOSafeTerminal {
    private final SwingTerminalDeviceConfiguration deviceConfiguration;
    private final SwingTerminalFontConfiguration fontConfiguration;
    private final SwingTerminalColorConfiguration colorConfiguration;
    private final VirtualTerminal virtualTerminal;
    private final Queue<KeyStroke> keyQueue;
    private final List<ResizeListener> resizeListeners;
    private final Timer blinkTimer;

    private final EnumSet<SGR> activeSGRs;
    private TextColor foregroundColor;
    private TextColor backgroundColor;
    private String enquiryString;

    private volatile boolean cursorIsVisible;
    private volatile boolean blinkOn;

    public SwingTerminal() {
        this(new TerminalScrollController.Null());
    }

    public SwingTerminal(TerminalScrollController scrollController) {
        this(SwingTerminalDeviceConfiguration.DEFAULT,
                SwingTerminalFontConfiguration.DEFAULT,
                SwingTerminalColorConfiguration.DEFAULT,
                scrollController);
    }

    /**
     * Creates a new SwingTerminal component.
     * @param deviceConfiguration
     * @param fontConfiguration
     * @param colorConfiguration
     */
    public SwingTerminal(
            SwingTerminalDeviceConfiguration deviceConfiguration,
            SwingTerminalFontConfiguration fontConfiguration,
            SwingTerminalColorConfiguration colorConfiguration) {

        this(deviceConfiguration, fontConfiguration, colorConfiguration, new TerminalScrollController.Null());
    }

    /**
     * Creates a new SwingTerminal component.
     * @param deviceConfiguration
     * @param fontConfiguration
     * @param colorConfiguration
     * @param scrollController
     */
    public SwingTerminal(
            SwingTerminalDeviceConfiguration deviceConfiguration,
            SwingTerminalFontConfiguration fontConfiguration,
            SwingTerminalColorConfiguration colorConfiguration,
            TerminalScrollController scrollController) {

        //Enforce valid values on the input parameters
        if(deviceConfiguration == null) {
            deviceConfiguration = SwingTerminalDeviceConfiguration.DEFAULT;
        }
        if(fontConfiguration == null) {
            fontConfiguration = SwingTerminalFontConfiguration.DEFAULT;
        }
        if(colorConfiguration == null) {
            colorConfiguration = SwingTerminalColorConfiguration.DEFAULT;
        }

        //This is kind of meaningless since we don't know how large the
        //component is at this point, but we should set it to something
        TerminalSize terminalSize = new TerminalSize(80, 20);
        this.virtualTerminal = new VirtualTerminal(
                deviceConfiguration.getLineBufferScrollbackSize(),
                terminalSize,
                TerminalCharacter.DEFAULT_CHARACTER,
                scrollController);
        this.keyQueue = new ConcurrentLinkedQueue<KeyStroke>();
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
        setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);
        setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);
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
    public Dimension getPreferredSize() {
        return new Dimension(fontConfiguration.getFontWidth() * virtualTerminal.getSize().getColumns(),
                fontConfiguration.getFontHeight() * virtualTerminal.getSize().getRows());
    }

    @Override
    protected void paintComponent(Graphics g) {
        //First, resize the buffer width/height if necessary
        int fontWidth = fontConfiguration.getFontWidth();
        int fontHeight = fontConfiguration.getFontHeight();
        int widthInNumberOfCharacters = getWidth() / fontWidth;
        int visibleRows = getHeight() / fontHeight;

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

        //Fill with black to remove any previous content
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        //Draw line by line, character by character
        int rowIndex = 0;
        for(List<TerminalCharacter> row: virtualTerminal.getLines()) {
            for(int columnIndex = 0; columnIndex < row.size(); columnIndex++) {
                TerminalCharacter character = row.get(columnIndex);
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
                    int lineEndY = lineStartY;
                    g.drawLine(lineStartX, lineStartY, lineEndX, lineEndY);
                }
                if(character.isUnderlined()) {
                    int lineStartX = columnIndex * fontWidth;
                    int lineStartY = ((rowIndex + 1) * fontHeight) - fontMetrics.getDescent() + 1;
                    int lineEndX = lineStartX + characterWidth;
                    int lineEndY = lineStartY;
                    g.drawLine(lineStartX, lineStartY, lineEndX, lineEndY);
                }

                if(atCursorLocation && deviceConfiguration.getCursorStyle() == SwingTerminalDeviceConfiguration.CursorStyle.DOUBLE_UNDERBAR) {
                    g.setColor(colorConfiguration.toAWTColor(deviceConfiguration.getCursorColor(), false, false));
                    g.fillRect(columnIndex * fontWidth, (rowIndex * fontHeight) + fontHeight - 2, characterWidth, 2);
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

    private Color deriveTrueForegroundColor(TerminalCharacter character, boolean atCursorLocation) {
        TextColor foregroundColor = character.getForegroundColor();
        TextColor backgroundColor = character.getBackgroundColor();
        boolean reverse = character.isReverse();
        boolean blink = character.isBlink();

        if(cursorIsVisible && atCursorLocation) {
            if(deviceConfiguration.getCursorStyle() == SwingTerminalDeviceConfiguration.CursorStyle.REVERSED &&
                    (!deviceConfiguration.isCursorBlinking() || (deviceConfiguration.isCursorBlinking() && !blinkOn))) {
                reverse = true;
            }
        }

        if(reverse && (!blink || (blink && !blinkOn))) {
            return colorConfiguration.toAWTColor(backgroundColor, backgroundColor != TextColor.ANSI.DEFAULT, character.isBold());
        }
        else if(!reverse && blink && blinkOn) {
            return colorConfiguration.toAWTColor(backgroundColor, false, character.isBold());
        }
        else {
            return colorConfiguration.toAWTColor(foregroundColor, true, character.isBold());
        }
    }

    private Color deriveTrueBackgroundColor(TerminalCharacter character, boolean atCursorLocation) {
        TextColor foregroundColor = character.getForegroundColor();
        TextColor backgroundColor = character.getBackgroundColor();
        boolean reverse = character.isReverse();

        if(cursorIsVisible && atCursorLocation) {
            if(deviceConfiguration.getCursorStyle() == SwingTerminalDeviceConfiguration.CursorStyle.REVERSED &&
                    (!deviceConfiguration.isCursorBlinking() || (deviceConfiguration.isCursorBlinking() && !blinkOn))) {
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
            return colorConfiguration.toAWTColor(backgroundColor, false, character.isBold());
        }
    }

    public SwingTerminalDeviceConfiguration getDeviceConfiguration() {
        return deviceConfiguration;
    }

    public SwingTerminalFontConfiguration getFontConfiguration() {
        return fontConfiguration;
    }

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
    public KeyStroke readInput() {
        return keyQueue.poll();
    }

    @Override
    public void enterPrivateMode() {
        virtualTerminal.switchToPrivateMode();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                repaint();
            }
        });
    }

    @Override
    public void exitPrivateMode() {
        virtualTerminal.switchToNormalMode();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                repaint();
            }
        });
    }

    @Override
    public void clearScreen() {
        virtualTerminal.clear();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                repaint();
            }
        });
    }

    @Override
    public void setCursorPosition(final int x, final int y) {
        virtualTerminal.setCursorPosition(new TerminalPosition(x, y));
    }

    @Override
    public void setCursorVisible(final boolean visible) {
        cursorIsVisible = visible;
    }

    @Override
    public void putCharacter(final char c) {
        virtualTerminal.putCharacter(new TerminalCharacter(c, foregroundColor, backgroundColor, activeSGRs));
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
    public void resetAllSGR() {
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
    public TerminalSize getTerminalSize() {
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
            catch(InterruptedException e) {
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

    private static final Set<Character> TYPED_KEYS_TO_IGNORE = new HashSet<Character>(Arrays.asList('\n', '\t', '\r', '\b', '\33'));
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
            if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                keyQueue.add(new KeyStroke(KeyType.Enter));
            }
            else if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                keyQueue.add(new KeyStroke(KeyType.Escape));
            }
            else if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                keyQueue.add(new KeyStroke(KeyType.Backspace));
            }
            else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
                keyQueue.add(new KeyStroke(KeyType.ArrowLeft));
            }
            else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
                keyQueue.add(new KeyStroke(KeyType.ArrowRight));
            }
            else if(e.getKeyCode() == KeyEvent.VK_UP) {
                keyQueue.add(new KeyStroke(KeyType.ArrowUp));
            }
            else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
                keyQueue.add(new KeyStroke(KeyType.ArrowDown));
            }
            else if(e.getKeyCode() == KeyEvent.VK_INSERT) {
                keyQueue.add(new KeyStroke(KeyType.Insert));
            }
            else if(e.getKeyCode() == KeyEvent.VK_DELETE) {
                keyQueue.add(new KeyStroke(KeyType.Delete));
            }
            else if(e.getKeyCode() == KeyEvent.VK_HOME) {
                keyQueue.add(new KeyStroke(KeyType.Home));
            }
            else if(e.getKeyCode() == KeyEvent.VK_END) {
                keyQueue.add(new KeyStroke(KeyType.End));
            }
            else if(e.getKeyCode() == KeyEvent.VK_PAGE_UP) {
                keyQueue.add(new KeyStroke(KeyType.PageUp));
            }
            else if(e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
                keyQueue.add(new KeyStroke(KeyType.PageDown));
            }
            else if(e.getKeyCode() == KeyEvent.VK_F1) {
                keyQueue.add(new KeyStroke(KeyType.F1));
            }
            else if(e.getKeyCode() == KeyEvent.VK_F2) {
                keyQueue.add(new KeyStroke(KeyType.F2));
            }
            else if(e.getKeyCode() == KeyEvent.VK_F3) {
                keyQueue.add(new KeyStroke(KeyType.F3));
            }
            else if(e.getKeyCode() == KeyEvent.VK_F4) {
                keyQueue.add(new KeyStroke(KeyType.F4));
            }
            else if(e.getKeyCode() == KeyEvent.VK_F5) {
                keyQueue.add(new KeyStroke(KeyType.F5));
            }
            else if(e.getKeyCode() == KeyEvent.VK_F6) {
                keyQueue.add(new KeyStroke(KeyType.F6));
            }
            else if(e.getKeyCode() == KeyEvent.VK_F7) {
                keyQueue.add(new KeyStroke(KeyType.F7));
            }
            else if(e.getKeyCode() == KeyEvent.VK_F8) {
                keyQueue.add(new KeyStroke(KeyType.F8));
            }
            else if(e.getKeyCode() == KeyEvent.VK_F9) {
                keyQueue.add(new KeyStroke(KeyType.F9));
            }
            else if(e.getKeyCode() == KeyEvent.VK_F10) {
                keyQueue.add(new KeyStroke(KeyType.F10));
            }
            else if(e.getKeyCode() == KeyEvent.VK_F11) {
                keyQueue.add(new KeyStroke(KeyType.F11));
            }
            else if(e.getKeyCode() == KeyEvent.VK_F12) {
                keyQueue.add(new KeyStroke(KeyType.F12));
            }
            else if(e.getKeyCode() == KeyEvent.VK_TAB) {
                if(e.isShiftDown()) {
                    keyQueue.add(new KeyStroke(KeyType.ReverseTab));
                }
                else {
                    keyQueue.add(new KeyStroke(KeyType.Tab));
                }
            }
            else {
                //keyTyped doesn't catch this scenario (for whatever reason...) so we have to do it here
                boolean altDown = (e.getModifiersEx() & InputEvent.ALT_DOWN_MASK) != 0;
                boolean ctrlDown = (e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0;
                if(altDown && ctrlDown && e.getKeyCode() >= 'A' && e.getKeyCode() <= 'Z') {
                    char asLowerCase = Character.toLowerCase((char) e.getKeyCode());
                    keyQueue.add(new KeyStroke(asLowerCase, true, true));
                }
            }
        }
    }
}
