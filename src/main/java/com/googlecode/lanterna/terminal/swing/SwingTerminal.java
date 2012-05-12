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
 * Copyright (C) 2010-2012 Martin
 */

package com.googlecode.lanterna.terminal.swing;

import com.googlecode.lanterna.LanternaException;
import com.googlecode.lanterna.input.InputProvider;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.input.KeyMappingProfile;
import com.googlecode.lanterna.terminal.AbstractTerminal;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * A Swing-based text terminal emulator
 * @author Martin
 */
public class SwingTerminal extends AbstractTerminal implements InputProvider
{
    private final TerminalRenderer terminalRenderer;
    private final Font terminalFont;
    private final Timer blinkTimer;
    
    private JFrame terminalFrame;
    private TerminalCharacter [][]characterMap;
    private TerminalPosition textPosition;
    private Color currentForegroundColor;
    private Color currentBackgroundColor;
    private boolean currentlyBold;
    private boolean currentlyBlinking;
    private boolean blinkVisible;
    private Queue<Key> keyQueue;
    
    private final Object resizeMutex;

    public SwingTerminal()
    {
        this(160, 40); //By default, create a 160x40 terminal (normal size * 2)
    }

    public SwingTerminal(TerminalSize terminalSize) 
    {
        this(terminalSize.getColumns(), terminalSize.getRows());
    }
    
    public SwingTerminal(int widthInColumns, int heightInRows)
    {
        this.terminalFont = new Font("Courier New", Font.PLAIN, 14);
        this.terminalRenderer = new TerminalRenderer();
        this.blinkTimer = new Timer(500, new BlinkAction());
        this.textPosition = new TerminalPosition(0, 0);
        this.characterMap = new TerminalCharacter[heightInRows][widthInColumns];
        this.currentForegroundColor = Color.WHITE;
        this.currentBackgroundColor = Color.BLACK;
        this.currentlyBold = false;
        this.currentlyBlinking = false;
        this.blinkVisible = false;
        this.keyQueue = new ConcurrentLinkedQueue<Key>();
        this.resizeMutex = new Object();
        
        onResized(widthInColumns, heightInRows);
        clearScreen();        
    }
    
    private class BlinkAction implements ActionListener 
    {
        public void actionPerformed(ActionEvent e) 
        {
            blinkVisible = !blinkVisible;
            terminalRenderer.repaint();
        }
    }

    public void addInputProfile(KeyMappingProfile profile)
    {
    }

    public void applyBackgroundColor(Color color) throws LanternaException
    {
        currentBackgroundColor = color;
    }

    public void applyForegroundColor(Color color) throws LanternaException
    {
        currentForegroundColor = color;
    }

    public void applySGR(SGR... options) throws LanternaException
    {
        for(SGR sgr: options)
        {
            if(sgr == SGR.RESET_ALL) {
                currentlyBold = false;
                currentlyBlinking = false;
                currentForegroundColor = Color.DEFAULT;
                currentBackgroundColor = Color.DEFAULT;
            }
            else if(sgr == SGR.ENTER_BOLD)
                currentlyBold = true;
            else if(sgr == SGR.EXIT_BOLD)
                currentlyBold = false;
            else if(sgr == SGR.ENTER_BLINK)
                currentlyBlinking = true;
            else if(sgr == SGR.EXIT_BLINK)
                currentlyBlinking = false;
        }
    }

    public void clearScreen()
    {
        synchronized(resizeMutex) {
            for(int y = 0; y < size().getRows(); y++)
                for(int x = 0; x < size().getColumns(); x++)
                    this.characterMap[y][x] = new TerminalCharacter(' ', Color.WHITE, Color.BLACK, false, false);
            moveCursor(0,0);
        }
    }

    public void enterPrivateMode() throws LanternaException
    {
        terminalFrame = new JFrame("Terminal");
        terminalFrame.addComponentListener(new FrameResizeListener());
        terminalFrame.getContentPane().setLayout(new BorderLayout());
        terminalFrame.getContentPane().add(terminalRenderer, BorderLayout.CENTER);
        terminalFrame.addKeyListener(new KeyCapturer());
        terminalFrame.pack();
        terminalFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        terminalFrame.setLocationByPlatform(true);
        terminalFrame.setVisible(true);
        terminalFrame.setFocusTraversalKeysEnabled(false);
        //terminalEmulator.setSize(terminalEmulator.getPreferredSize());
        terminalFrame.pack();
        blinkTimer.start();
    }

    public void exitPrivateMode() throws LanternaException
    {
        if(terminalFrame == null)
            return;
        
        blinkTimer.stop();
        terminalFrame.setVisible(false);
        terminalFrame.dispose();
    }

    public void moveCursor(int x, int y)
    {
        if(x < 0)
            x = 0;
        if(x >= size().getColumns())
            x = size().getColumns() - 1;
        if(y < 0)
            y = 0;
        if(y >= size().getRows())
            y = size().getRows() - 1;

        textPosition.setColumn(x);
        textPosition.setRow(y);
        refreshScreen();
    }

    public synchronized void putCharacter(char c) throws LanternaException
    {
        characterMap[textPosition.getRow()][textPosition.getColumn()] =
                new TerminalCharacter(c, currentForegroundColor, currentBackgroundColor, currentlyBold, currentlyBlinking);
        if(textPosition.getColumn() == size().getColumns() - 1 &&
                textPosition.getRow() == size().getRows() - 1)
            moveCursor(0, textPosition.getRow());
        if(textPosition.getColumn() == size().getColumns() - 1)
            moveCursor(0, textPosition.getRow() + 1);
        else
            moveCursor(textPosition.getColumn() + 1, textPosition.getRow());
    }

    public TerminalSize queryTerminalSize()
    {
        //Just bypass to size()
        return size();
    }

    private synchronized void resize(int newSizeColumns, int newSizeRows)
    {
        TerminalCharacter [][]newCharacterMap = new TerminalCharacter[newSizeRows][newSizeColumns];
        for(int y = 0; y < newSizeRows; y++)
            for(int x = 0; x < newSizeColumns; x++)
                newCharacterMap[y][x] = new TerminalCharacter(' ', Color.WHITE, Color.BLACK, false, false);

        synchronized(resizeMutex) {
            for(int y = 0; y < size().getRows() && y < newSizeRows; y++) {
                for(int x = 0; x < size().getColumns() && x < newSizeColumns; x++) {
                    newCharacterMap[y][x] = this.characterMap[y][x];
                }
            }

            this.characterMap = newCharacterMap;
            SwingUtilities.invokeLater(new Runnable() {
                public void run()
                {
                    terminalFrame.pack();
                }
            });

            onResized(newSizeColumns, newSizeRows);
        }
    }

    @Override
    public Key readInput() throws LanternaException
    {
        return keyQueue.poll();
    }

    public void flush() throws LanternaException {
        //Not needed
    }

    private void refreshScreen()
    {
        SwingUtilities.invokeLater(new Runnable() {
            public void run()
            {
                terminalRenderer.repaint();
            }
        });
    }

    /**
     * Returns the size of the terminal, which will always be same as calling
     * getLastKnownSize(), but since that could be confusing when reading the 
     * code, I added this helper method.
     */
    private TerminalSize size()
    {
        return getLastKnownSize();
    }
    
    private java.awt.Color convertColorToAWT(Color color, boolean bold)
    {
        //Values below are shamelessly stolen from gnome terminal!
        switch(color)
        {
            case BLACK:
                if(bold)
                    return new java.awt.Color(85, 87, 83);
                else
                    return new java.awt.Color(46, 52, 54);

            case BLUE:
                if(bold)
                    return new java.awt.Color(114, 159, 207);
                else
                    return new java.awt.Color(52, 101, 164);

            case CYAN:
                if(bold)
                    return new java.awt.Color(52, 226, 226);
                else
                    return new java.awt.Color(6, 152, 154);

            case DEFAULT:
                if(bold)
                    return new java.awt.Color(238, 238, 236);
                else
                    return new java.awt.Color(211, 215, 207);

            case GREEN:
                if(bold)
                    return new java.awt.Color(138, 226, 52);
                else
                    return new java.awt.Color(78, 154, 6);

            case MAGENTA:
                if(bold)
                    return new java.awt.Color(173, 127, 168);
                else
                    return new java.awt.Color(117, 80, 123);

            case RED:
                if(bold)
                    return new java.awt.Color(239, 41, 41);
                else
                    return new java.awt.Color(204, 0, 0);

            case WHITE:
                if(bold)
                    return new java.awt.Color(238, 238, 236);
                else
                    return new java.awt.Color(211, 215, 207);

            case YELLOW:
                if(bold)
                    return new java.awt.Color(252, 233, 79);
                else
                    return new java.awt.Color(196, 160, 0);
        }
        return java.awt.Color.PINK;
    }

    private class KeyCapturer extends KeyAdapter
    {
        private Set<Character> typedIgnore = new HashSet<Character>(
                Arrays.asList('\n', '\t', '\r', '\b'));

        @Override
        public void keyTyped(KeyEvent e)
        {
            if(typedIgnore.contains(e.getKeyChar()))
                return;
            else
                keyQueue.add(new Key(e.getKeyChar()));
        }

        @Override
        public void keyPressed(KeyEvent e)
        {
            if(e.getKeyCode() == KeyEvent.VK_ENTER)
                keyQueue.add(new Key(Key.Kind.Enter));
            else if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
                keyQueue.add(new Key(Key.Kind.Escape));
            else if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
                keyQueue.add(new Key(Key.Kind.Backspace));
            else if(e.getKeyCode() == KeyEvent.VK_LEFT)
                keyQueue.add(new Key(Key.Kind.ArrowLeft));
            else if(e.getKeyCode() == KeyEvent.VK_RIGHT)
                keyQueue.add(new Key(Key.Kind.ArrowRight));
            else if(e.getKeyCode() == KeyEvent.VK_UP)
                keyQueue.add(new Key(Key.Kind.ArrowUp));
            else if(e.getKeyCode() == KeyEvent.VK_DOWN)
                keyQueue.add(new Key(Key.Kind.ArrowDown));
            else if(e.getKeyCode() == KeyEvent.VK_INSERT)
                keyQueue.add(new Key(Key.Kind.Insert));
            else if(e.getKeyCode() == KeyEvent.VK_DELETE)
                keyQueue.add(new Key(Key.Kind.Delete));
            else if(e.getKeyCode() == KeyEvent.VK_HOME)
                keyQueue.add(new Key(Key.Kind.Home));
            else if(e.getKeyCode() == KeyEvent.VK_END)
                keyQueue.add(new Key(Key.Kind.End));
            else if(e.getKeyCode() == KeyEvent.VK_PAGE_UP)
                keyQueue.add(new Key(Key.Kind.PageUp));
            else if(e.getKeyCode() == KeyEvent.VK_PAGE_DOWN)
                keyQueue.add(new Key(Key.Kind.PageDown));
            else if(e.getKeyCode() == KeyEvent.VK_TAB) {
                if(e.isShiftDown())
                    keyQueue.add(new Key(Key.Kind.ReverseTab));
                else
                    keyQueue.add(new Key(Key.Kind.Tab));
            }
        }
    }

    private class FrameResizeListener extends ComponentAdapter
    {
        private int lastWidth = -1;
        private int lastHeight = -1;
        
        @Override
        public void componentResized(ComponentEvent e)
        {
            if(e.getComponent() == null || e.getComponent() instanceof JFrame == false)
                return;
            
            JFrame frame = (JFrame)e.getComponent();
            Container contentPane = frame.getContentPane();
            int newWidth = contentPane.getWidth();
            int newHeight = contentPane.getHeight();

            FontMetrics fontMetrics = frame.getGraphics().getFontMetrics(terminalFont);
            int consoleWidth = newWidth / fontMetrics.charWidth(' ');
            int consoleHeight = newHeight / fontMetrics.getHeight();

            if(consoleWidth == lastWidth && consoleHeight == lastHeight)
                return;

            lastWidth = consoleWidth;
            lastHeight = consoleHeight;
            
            resize(consoleWidth, consoleHeight);
        }
    }

    private class TerminalRenderer extends JComponent
    {
        public TerminalRenderer()
        {
        }

        @Override
        public Dimension getPreferredSize()
        {
            FontMetrics fontMetrics = getGraphics().getFontMetrics(terminalFont);
            final int screenWidth = SwingTerminal.this.size().getColumns() * fontMetrics.charWidth(' ');
            final int screenHeight = SwingTerminal.this.size().getRows() * fontMetrics.getHeight();
            return new Dimension(screenWidth, screenHeight);
        }

        @Override
        protected void paintComponent(Graphics g)
        {
            final Graphics2D graphics2D = (Graphics2D)g.create();
            graphics2D.setFont(terminalFont);
            graphics2D.setColor(java.awt.Color.BLACK);
            graphics2D.fillRect(0, 0, getWidth(), getHeight());
            final FontMetrics fontMetrics = getGraphics().getFontMetrics(terminalFont);
            final int charWidth = fontMetrics.charWidth(' ');
            final int charHeight = fontMetrics.getHeight();
            
            for(int row = 0; row < SwingTerminal.this.size().getRows(); row++) {
                for(int col = 0; col < SwingTerminal.this.size().getColumns(); col++) {
                    TerminalCharacter character = characterMap[row][col];
                    if(row == textPosition.getRow() && col == textPosition.getColumn())
                        graphics2D.setColor(character.getForegroundAsAWT());
                    else
                        graphics2D.setColor(character.getBackgroundAsAWT());
                    graphics2D.fillRect(col * charWidth, row * charHeight, charWidth, charHeight);
                    if((row == textPosition.getRow() && col == textPosition.getColumn()) ||
                            (character.isBlinking() && !blinkVisible))
                        graphics2D.setColor(character.getBackgroundAsAWT());
                    else
                        graphics2D.setColor(character.getForegroundAsAWT());
                        
                    graphics2D.drawString(character.toString(), col * charWidth, ((row + 1) * charHeight) - fontMetrics.getDescent());
                }
            }
            graphics2D.dispose();
        }
    }

    private class TerminalCharacter
    {
        private char character;
        private Color foreground;
        private Color background;
        private boolean bold;
        private boolean blinking;

        public TerminalCharacter(char character, Color foreground, Color background, boolean bold, boolean blinking)
        {
            this.character = character;
            this.foreground = foreground;
            this.background = background;
            this.bold = bold;
            this.blinking = blinking;
        }

        public Color getBackground()
        {
            return background;
        }

        public boolean isBold()
        {
            return bold;
        }

        public boolean isBlinking() {
            return blinking;
        }
        
        public char getCharacter()
        {
            return character;
        }

        public Color getForeground()
        {
            return foreground;
        }

        public java.awt.Color getForegroundAsAWT()
        {
            return convertColorToAWT(foreground, bold);
        }

        public java.awt.Color getBackgroundAsAWT()
        {
            //TODO: Fix the lookup method to handle color 'default' for background also
            if (background == Color.DEFAULT)
                return convertColorToAWT(Color.BLACK, false);
            else
                return convertColorToAWT(background, false);
        }

        @Override
        public String toString()
        {
            return Character.toString(character);
        }
    }
}
