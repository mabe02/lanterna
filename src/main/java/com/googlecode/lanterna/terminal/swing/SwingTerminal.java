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
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
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
    
    public static interface ScrollObserver {
        void newScrollableLength(int rows);
    }
    
    private static class NullScrollObserver implements ScrollObserver {
        @Override
        public void newScrollableLength(int pixels) {
        }
    }
    
    private final ScrollObserver scrollObserver;
    private final SwingTerminalDeviceConfiguration deviceConfiguration;
    private final SwingTerminalFontConfiguration fontConfiguration;
    private final SwingTerminalColorConfiguration colorConfiguration;
    private final TextBuffer mainBuffer;
    private final TextBuffer privateModeBuffer;
    private final TerminalDeviceEmulator deviceEmulator;
    private final VirtualTerminalImplementation terminalImplementation;
    private final Timer blinkTimer;

    private TextBuffer currentBuffer;
    private String enquiryString;
    private int scrollOffset;

    private volatile boolean cursorIsVisible;
    private volatile boolean blinkOn;

    public SwingTerminal() {
        this(new NullScrollObserver());
    }
    
    public SwingTerminal(ScrollObserver scrollObserver) {
        this(SwingTerminalDeviceConfiguration.DEFAULT,
                SwingTerminalFontConfiguration.DEFAULT,
                SwingTerminalColorConfiguration.DEFAULT);
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
        
        this(deviceConfiguration, fontConfiguration, colorConfiguration, new NullScrollObserver());
    }
    
    /**
     * Creates a new SwingTerminal component.
     * @param deviceConfiguration
     * @param fontConfiguration
     * @param colorConfiguration
     * @param scrollObserver
     */
    public SwingTerminal(
            SwingTerminalDeviceConfiguration deviceConfiguration,
            SwingTerminalFontConfiguration fontConfiguration,
            SwingTerminalColorConfiguration colorConfiguration,
            ScrollObserver scrollObserver) {
        
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
        this.deviceEmulator = new TerminalDeviceEmulator();
        this.terminalImplementation = new VirtualTerminalImplementation(deviceEmulator, terminalSize);
        this.deviceConfiguration = deviceConfiguration;
        this.fontConfiguration = fontConfiguration;
        this.colorConfiguration = colorConfiguration;
        this.scrollObserver = scrollObserver;

        this.mainBuffer = new TextBuffer(deviceConfiguration.getLineBufferScrollbackSize(), terminalImplementation.getTerminalSize());
        this.privateModeBuffer = new TextBuffer(0, terminalImplementation.getTerminalSize());
        this.currentBuffer = mainBuffer;    //Always start with the active buffer
        this.cursorIsVisible = true;        //Always start with an activate and visible cursor
        this.enquiryString = "SwingTerminal";
        this.scrollOffset = 0;
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
        return new Dimension(fontConfiguration.getFontWidth() * terminalImplementation.getTerminalSize().getColumns(),
                fontConfiguration.getFontHeight() * terminalImplementation.getTerminalSize().getRows());
    }

    @Override
    protected void paintComponent(Graphics g) {
        //First, resize the buffer width/height if necessary
        int fontWidth = fontConfiguration.getFontWidth();
        int fontHeight = fontConfiguration.getFontHeight();
        int widthInNumberOfCharacters = getWidth() / fontWidth;
        int visibleRows = getHeight() / fontHeight;

        currentBuffer.readjust(widthInNumberOfCharacters, visibleRows);
        terminalImplementation.setTerminalSize(terminalImplementation.getTerminalSize().withColumns(widthInNumberOfCharacters).withRows(visibleRows));
        TerminalPosition cursorPosition = terminalImplementation.getCurrentPosition();

        //Fill with black to remove any previous content
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        //Draw line by line, character by character
        int rowIndex = 0;
        for(List<TerminalCharacter> row: currentBuffer.getVisibleLines(visibleRows, scrollOffset)) {
            for(int columnIndex = 0; columnIndex < row.size(); columnIndex++) {
                TerminalCharacter character = row.get(columnIndex);
                boolean atCursorLocation = cursorPosition.equals(columnIndex, rowIndex);
                //If next position is the cursor location and this is a CJK character (i.e. cursor is on the padding),
                //consider this location the cursor position since otherwise the cursor will be skipped
                if(!atCursorLocation && 
                        cursorPosition.getColumn() == columnIndex + 1 && 
                        cursorPosition.getRow() == rowIndex &&
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
    
    public void setScrollOffset(int scrollOffset) {
        this.scrollOffset = scrollOffset;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                repaint();
            }
        });
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
        return terminalImplementation.readInput();
    }

    @Override
    public void addKeyDecodingProfile(KeyDecodingProfile profile) {
        terminalImplementation.addKeyDecodingProfile(profile);
    }

    @Override
    public void enterPrivateMode() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                terminalImplementation.enterPrivateMode();
            }
        });
    }

    @Override
    public void exitPrivateMode() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                terminalImplementation.exitPrivateMode();
            }
        });
    }

    @Override
    public void clearScreen() {        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                terminalImplementation.clearScreen();
            }
        });
    }

    @Override
    public void moveCursor(final int x, final int y) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                terminalImplementation.moveCursor(x, y);
            }
        });
    }

    @Override
    public void setCursorVisible(final boolean visible) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                terminalImplementation.setCursorVisible(visible);
            }
        });
    }

    @Override
    public void putCharacter(final char c) {
        SwingUtilities.invokeLater(new Runnable() {
            private int lastSize = -1;
            
            @Override
            public void run() {
                terminalImplementation.putCharacter(c);
                if(currentBuffer.getNumberOfLines() != lastSize) {
                    scrollObserver.newScrollableLength(currentBuffer.getNumberOfLines());
                    lastSize = currentBuffer.getNumberOfLines();
                }
            }
        });
    }

    @Override
    public void enableSGR(final SGR sgr) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                terminalImplementation.enableSGR(sgr);
            }
        });
    }

    @Override
    public void disableSGR(final SGR sgr) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                terminalImplementation.disableSGR(sgr);
            }
        });
    }

    @Override
    public void resetAllSGR() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                terminalImplementation.resetAllSGR();
            }
        });
    }

    @Override
    public void setForegroundColor(final TextColor color) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                terminalImplementation.setForegroundColor(color);
            }
        });
    }

    @Override
    public void setBackgroundColor(final TextColor color) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                terminalImplementation.setBackgroundColor(color);
            }
        });
    }

    @Override
    public TerminalSize getTerminalSize() {
        return terminalImplementation.getTerminalSize();
    }

    @Override
    public byte[] enquireTerminal(int timeout, TimeUnit timeoutUnit) {
        return terminalImplementation.enquireTerminal(timeout, timeoutUnit);
    }

    @Override
    public void flush() {
        if(SwingUtilities.isEventDispatchThread()) {
            terminalImplementation.flush();
        }
        else {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        terminalImplementation.flush();
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
        terminalImplementation.addResizeListener(listener);
    }

    @Override
    public void removeResizeListener(ResizeListener listener) {
        terminalImplementation.removeResizeListener(listener);
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
                deviceEmulator.registerKeyStroke(new KeyStroke(character, ctrlDown, altDown));
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                deviceEmulator.registerKeyStroke(new KeyStroke(KeyType.Enter));
            }
            else if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                deviceEmulator.registerKeyStroke(new KeyStroke(KeyType.Escape));
            }
            else if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                deviceEmulator.registerKeyStroke(new KeyStroke(KeyType.Backspace));
            }
            else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
                deviceEmulator.registerKeyStroke(new KeyStroke(KeyType.ArrowLeft));
            }
            else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
                deviceEmulator.registerKeyStroke(new KeyStroke(KeyType.ArrowRight));
            }
            else if(e.getKeyCode() == KeyEvent.VK_UP) {
                deviceEmulator.registerKeyStroke(new KeyStroke(KeyType.ArrowUp));
            }
            else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
                deviceEmulator.registerKeyStroke(new KeyStroke(KeyType.ArrowDown));
            }
            else if(e.getKeyCode() == KeyEvent.VK_INSERT) {
                deviceEmulator.registerKeyStroke(new KeyStroke(KeyType.Insert));
            }
            else if(e.getKeyCode() == KeyEvent.VK_DELETE) {
                deviceEmulator.registerKeyStroke(new KeyStroke(KeyType.Delete));
            }
            else if(e.getKeyCode() == KeyEvent.VK_HOME) {
                deviceEmulator.registerKeyStroke(new KeyStroke(KeyType.Home));
            }
            else if(e.getKeyCode() == KeyEvent.VK_END) {
                deviceEmulator.registerKeyStroke(new KeyStroke(KeyType.End));
            }
            else if(e.getKeyCode() == KeyEvent.VK_PAGE_UP) {
                deviceEmulator.registerKeyStroke(new KeyStroke(KeyType.PageUp));
            }
            else if(e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
                deviceEmulator.registerKeyStroke(new KeyStroke(KeyType.PageDown));
            }
            else if(e.getKeyCode() == KeyEvent.VK_F1) {
                deviceEmulator.registerKeyStroke(new KeyStroke(KeyType.F1));
            }
            else if(e.getKeyCode() == KeyEvent.VK_F2) {
                deviceEmulator.registerKeyStroke(new KeyStroke(KeyType.F2));
            }
            else if(e.getKeyCode() == KeyEvent.VK_F3) {
                deviceEmulator.registerKeyStroke(new KeyStroke(KeyType.F3));
            }
            else if(e.getKeyCode() == KeyEvent.VK_F4) {
                deviceEmulator.registerKeyStroke(new KeyStroke(KeyType.F4));
            }
            else if(e.getKeyCode() == KeyEvent.VK_F5) {
                deviceEmulator.registerKeyStroke(new KeyStroke(KeyType.F5));
            }
            else if(e.getKeyCode() == KeyEvent.VK_F6) {
                deviceEmulator.registerKeyStroke(new KeyStroke(KeyType.F6));
            }
            else if(e.getKeyCode() == KeyEvent.VK_F7) {
                deviceEmulator.registerKeyStroke(new KeyStroke(KeyType.F7));
            }
            else if(e.getKeyCode() == KeyEvent.VK_F8) {
                deviceEmulator.registerKeyStroke(new KeyStroke(KeyType.F8));
            }
            else if(e.getKeyCode() == KeyEvent.VK_F9) {
                deviceEmulator.registerKeyStroke(new KeyStroke(KeyType.F9));
            }
            else if(e.getKeyCode() == KeyEvent.VK_F10) {
                deviceEmulator.registerKeyStroke(new KeyStroke(KeyType.F10));
            }
            else if(e.getKeyCode() == KeyEvent.VK_F11) {
                deviceEmulator.registerKeyStroke(new KeyStroke(KeyType.F11));
            }
            else if(e.getKeyCode() == KeyEvent.VK_F12) {
                deviceEmulator.registerKeyStroke(new KeyStroke(KeyType.F12));
            }
            else if(e.getKeyCode() == KeyEvent.VK_TAB) {
                if(e.isShiftDown()) {
                    deviceEmulator.registerKeyStroke(new KeyStroke(KeyType.ReverseTab));
                }
                else {
                    deviceEmulator.registerKeyStroke(new KeyStroke(KeyType.Tab));
                }
            }
            else {
                //keyTyped doesn't catch this scenario (for whatever reason...) so we have to do it here
                boolean altDown = (e.getModifiersEx() & InputEvent.ALT_DOWN_MASK) != 0;
                boolean ctrlDown = (e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0;
                if(altDown && ctrlDown && e.getKeyCode() >= 'A' && e.getKeyCode() <= 'Z') {
                    char asLowerCase = Character.toLowerCase((char) e.getKeyCode());
                    deviceEmulator.registerKeyStroke(new KeyStroke(asLowerCase, true, true));
                }
            }
        }
    }

    private class TerminalDeviceEmulator implements VirtualTerminalImplementation.DeviceEmulator {
        private final Queue<KeyStroke> keyQueue;

        public TerminalDeviceEmulator() {
            this.keyQueue = new ConcurrentLinkedQueue<KeyStroke>();
        }
        
        public void registerKeyStroke(KeyStroke keyStroke) {
            keyQueue.add(keyStroke);
        }
        
        @Override
        public KeyStroke readInput() {
            return keyQueue.poll();
        }

        @Override
        public void enterPrivateMode() {
            SwingTerminal.this.currentBuffer = SwingTerminal.this.privateModeBuffer;
        }

        @Override
        public void exitPrivateMode() {
            SwingTerminal.this.currentBuffer = SwingTerminal.this.mainBuffer;
        }

        @Override
        public TextBuffer getBuffer() {
            return currentBuffer;
        }

        @Override
        public void setCursorVisible(boolean visible) {
            SwingTerminal.this.cursorIsVisible = visible;
        }
        
        @Override
        public void flush() {
            SwingTerminal.this.repaint();
        }

        @Override
        public byte[] equireTerminal() {
            return SwingTerminal.this.enquiryString.getBytes(Charset.defaultCharset());
        }
    }
}
