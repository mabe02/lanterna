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

import com.googlecode.lanterna.input.InputProvider;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.input.KeyMappingProfile;
import com.googlecode.lanterna.terminal.AbstractTerminal;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.terminal.XTerm8bitIndexedColorUtils;
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
    private final Timer blinkTimer;
    
    private JFrame terminalFrame;
    private TerminalAppearance appearance;
    private TerminalCharacter [][]characterMap;
    private TerminalPosition textPosition;
    private TerminalCharacterColor currentForegroundColor;
    private TerminalCharacterColor currentBackgroundColor;
    private boolean currentlyBold;
    private boolean currentlyBlinking;
    private boolean currentlyUnderlined;
    private boolean blinkVisible;
    private boolean cursorVisible;
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
        this(TerminalAppearance.DEFAULT_APPEARANCE, widthInColumns, heightInRows);
    }
    
    public SwingTerminal(TerminalAppearance appearance)
    {
        this(appearance, 160, 40);
    }
    
    public SwingTerminal(TerminalAppearance appearance, int widthInColumns, int heightInRows)
    {
        this.appearance = appearance;
        this.terminalRenderer = new TerminalRenderer();
        this.blinkTimer = new Timer(500, new BlinkAction());
        this.textPosition = new TerminalPosition(0, 0);
        this.characterMap = new TerminalCharacter[heightInRows][widthInColumns];
        this.currentForegroundColor = new CharacterANSIColor(Color.WHITE);
        this.currentBackgroundColor = new CharacterANSIColor(Color.BLACK);
        this.currentlyBold = false;
        this.currentlyBlinking = false;
        this.currentlyUnderlined = false;
        this.blinkVisible = false;
        this.cursorVisible = true;
        this.keyQueue = new ConcurrentLinkedQueue<Key>();
        this.resizeMutex = new Object();
        
        onResized(widthInColumns, heightInRows);
        clearScreen();        
    }
    
    /**
     * This method will give you the underlying JFrame for this terminal.
     * Please be careful when calling methods on the JFrame, especially if 
     * you modify the rendering or content pane, since that may cause the
     * terminal rendering to malfunction.
     * 
     * <p>Good uses of this method is to set the window title, window icon list
     * and so on. If you add more logic to this JFrame, you should probably
     * ask yourself why you are using Lanterna to begin with.
     * 
     * @return JFrame object used by this SwingTerminal
     */
    public JFrame getJFrame() {
        return terminalFrame;
    }
    
    private class BlinkAction implements ActionListener 
    {
        public void actionPerformed(ActionEvent e) 
        {
            blinkVisible = !blinkVisible;
            terminalRenderer.repaint();
        }
    }

    @Override
    public void addInputProfile(KeyMappingProfile profile)
    {
    }

    @Override
    public void applyBackgroundColor(Color color) {
        currentBackgroundColor = new CharacterANSIColor(color);
    }

    @Override
    public void applyBackgroundColor(int r, int g, int b) {
        currentBackgroundColor = new Character24bitColor(new java.awt.Color(r, g, b));
    }

    @Override
    public void applyBackgroundColor(int index) {
        currentBackgroundColor = new CharacterIndexedColor(index);
    }

    @Override
    public void applyForegroundColor(Color color) {
        currentForegroundColor = new CharacterANSIColor(color);
    }

    @Override
    public void applyForegroundColor(int r, int g, int b) {
        currentForegroundColor = new Character24bitColor(new java.awt.Color(r, g, b));
    }

    @Override
    public void applyForegroundColor(int index) {
        currentForegroundColor = new CharacterIndexedColor(index);
    }

    @Override
    public void applySGR(SGR... options)
    {
        for(SGR sgr: options)
        {
            if(sgr == SGR.RESET_ALL) {
                currentlyBold = false;
                currentlyBlinking = false;
                currentlyUnderlined = false;
                currentForegroundColor = new CharacterANSIColor(Color.DEFAULT);
                currentBackgroundColor = new CharacterANSIColor(Color.BLACK);
            }
            else if(sgr == SGR.ENTER_BOLD)
                currentlyBold = true;
            else if(sgr == SGR.EXIT_BOLD)
                currentlyBold = false;
            else if(sgr == SGR.ENTER_BLINK)
                currentlyBlinking = true;
            else if(sgr == SGR.EXIT_BLINK)
                currentlyBlinking = false;
            else if(sgr == SGR.ENTER_UNDERLINE)
                currentlyUnderlined = true;
            else if(sgr == SGR.EXIT_UNDERLINE)
                currentlyUnderlined = false;
        }
    }

    @Override
    public void clearScreen()
    {
        synchronized(resizeMutex) {
            for(int y = 0; y < size().getRows(); y++)
                for(int x = 0; x < size().getColumns(); x++)
                    this.characterMap[y][x] = new TerminalCharacter(
                            ' ', 
                            new CharacterANSIColor(Color.DEFAULT),
                            new CharacterANSIColor(Color.BLACK),
                            false, 
                            false, 
                            false);
            moveCursor(0,0);
        }
    }

    @Override
    public void enterPrivateMode()
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

    @Override
    public void exitPrivateMode()
    {
        if(terminalFrame == null)
            return;
        
        blinkTimer.stop();
        terminalFrame.setVisible(false);
        terminalFrame.dispose();
    }

    @Override
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

    @Override
    public void setCursorVisible(boolean visible) {
        this.cursorVisible = visible;
        refreshScreen();
    }

    @Override
    public synchronized void putCharacter(char c)
    {
        characterMap[textPosition.getRow()][textPosition.getColumn()] =
                new TerminalCharacter(c, currentForegroundColor, currentBackgroundColor, currentlyBold, currentlyBlinking, currentlyUnderlined);
        if(textPosition.getColumn() == size().getColumns() - 1 &&
                textPosition.getRow() == size().getRows() - 1)
            moveCursor(0, textPosition.getRow());
        if(textPosition.getColumn() == size().getColumns() - 1)
            moveCursor(0, textPosition.getRow() + 1);
        else
            moveCursor(textPosition.getColumn() + 1, textPosition.getRow());
    }

    @Override
    public TerminalSize queryTerminalSize()
    {
        //Just bypass to getTerminalSize()
        return getTerminalSize();
    }

    @Override
    public TerminalSize getTerminalSize() {
        return size();
    }

    private synchronized void resize(int newSizeColumns, int newSizeRows)
    {
        TerminalCharacter [][]newCharacterMap = new TerminalCharacter[newSizeRows][newSizeColumns];
        for(int y = 0; y < newSizeRows; y++)
            for(int x = 0; x < newSizeColumns; x++)
                newCharacterMap[y][x] = new TerminalCharacter(
                        ' ', 
                        new CharacterANSIColor(Color.WHITE), 
                        new CharacterANSIColor(Color.BLACK), 
                        false, 
                        false, 
                        false);

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
    public Key readInput()
    {
        return keyQueue.poll();
    }

    @Override
    public void flush() {
        //Not needed
    }
    
    /**
     * Changes the current color palett to a new one supplied
     * @param palette Palett to use
     */
    public void setTerminalPalette(TerminalPalette palette) {
        appearance = appearance.withPalette(palette);
        refreshScreen();
    }

    private void refreshScreen()
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
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

    private class KeyCapturer extends KeyAdapter
    {
        private Set<Character> typedIgnore = new HashSet<Character>(
                Arrays.asList('\n', '\t', '\r', '\b'));

        @Override
        public void keyTyped(KeyEvent e)
        {
            char character = e.getKeyChar();
            boolean altDown = (e.getModifiersEx() & InputEvent.ALT_DOWN_MASK) != 0;
            boolean ctrlDown = (e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0;
            
            if(!typedIgnore.contains(character)) {
                if(ctrlDown) {
                    //We need to re-adjust the character if ctrl is pressed, just like for the AnsiTerminal
                    character = (char)('a' - 1 + character);
                }
                keyQueue.add(new Key(character, ctrlDown, altDown));
            }
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
            else {
                //keyTyped doesn't catch this scenario (for whatever reason...) so we have to do it here
                boolean altDown = (e.getModifiersEx() & InputEvent.ALT_DOWN_MASK) != 0;
                boolean ctrlDown = (e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0;
                if(altDown && ctrlDown && e.getKeyCode() >= 'A' && e.getKeyCode() <= 'Z') {
                    char asLowerCase = Character.toLowerCase((char)e.getKeyCode());
                    keyQueue.add(new Key(asLowerCase, true, true));
                }
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

            FontMetrics fontMetrics = frame.getGraphics().getFontMetrics(appearance.getNormalTextFont());
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
            FontMetrics fontMetrics = getGraphics().getFontMetrics(appearance.getNormalTextFont());
            final int screenWidth = SwingTerminal.this.size().getColumns() * fontMetrics.charWidth(' ');
            final int screenHeight = SwingTerminal.this.size().getRows() * fontMetrics.getHeight();
            return new Dimension(screenWidth, screenHeight);
        }

        @Override
        protected void paintComponent(Graphics g)
        {
            final Graphics2D graphics2D = (Graphics2D)g.create();
            graphics2D.setFont(appearance.getNormalTextFont());
            graphics2D.setColor(java.awt.Color.BLACK);
            graphics2D.fillRect(0, 0, getWidth(), getHeight());
            final FontMetrics fontMetrics = getGraphics().getFontMetrics(appearance.getNormalTextFont());
            final int charWidth = fontMetrics.charWidth(' ');
            final int charHeight = fontMetrics.getHeight();
            
            for(int row = 0; row < SwingTerminal.this.size().getRows(); row++) {
                for(int col = 0; col < SwingTerminal.this.size().getColumns(); col++) {
                    TerminalCharacter character = characterMap[row][col];
                    if(cursorVisible && row == textPosition.getRow() && col == textPosition.getColumn())
                        graphics2D.setColor(character.getForegroundAsAWTColor(appearance.useBrightColorsOnBold()));
                    else
                        graphics2D.setColor(character.getBackgroundAsAWTColor());
                    graphics2D.fillRect(col * charWidth, row * charHeight, charWidth, charHeight);
                    if((cursorVisible && row == textPosition.getRow() && col == textPosition.getColumn()) ||
                            (character.isBlinking() && !blinkVisible))
                        graphics2D.setColor(character.getBackgroundAsAWTColor());
                    else
                        graphics2D.setColor(character.getForegroundAsAWTColor(appearance.useBrightColorsOnBold()));
                        
                    if(character.isBold())
                        graphics2D.setFont(appearance.getBoldTextFont());
                    
                    if(character.isUnderlined())
                        graphics2D.drawLine(
                                col * charWidth, ((row + 1) * charHeight) - 1, 
                                (col+1) * charWidth, ((row + 1) * charHeight) - 1);
                        
                    graphics2D.drawString(character.toString(), col * charWidth, ((row + 1) * charHeight) - fontMetrics.getDescent());
                    
                    if(character.isBold())
                        graphics2D.setFont(appearance.getNormalTextFont());   //Restore the original font
                }
            }
            graphics2D.dispose();
        }
    }

    private static class TerminalCharacter {
        
        private final char character;
        private final TerminalCharacterColor foreground;
        private final TerminalCharacterColor background;
        private final boolean bold;
        private final boolean blinking;
        private final boolean underlined;

        TerminalCharacter(
                char character, 
                TerminalCharacterColor foreground, 
                TerminalCharacterColor background, 
                boolean bold, 
                boolean blinking, 
                boolean underlined) {
            this.character = character;
            this.foreground = foreground;
            this.background = background;
            this.bold = bold;
            this.blinking = blinking;
            this.underlined = underlined;
        }

        public boolean isBold() {
            return bold;
        }

        public boolean isBlinking() {
            return blinking;
        }

        public boolean isUnderlined() {
            return underlined;
        }
        
        private java.awt.Color getForegroundAsAWTColor(boolean useBrightOnBold) {
            return foreground.getColor(isBold() && useBrightOnBold, true);
        }

        private java.awt.Color getBackgroundAsAWTColor() {
            return background.getColor(false, false);
        }

        @Override
        public String toString() {
            return Character.toString(character);
        }
    }
    
    private static abstract class TerminalCharacterColor {
        public abstract java.awt.Color getColor(boolean brightHint, boolean foregroundHint);
    }
    
    private class CharacterANSIColor extends TerminalCharacterColor {

        private final Color color;

        CharacterANSIColor(Color color) {
            this.color = color;
        }
        
        @Override
        public java.awt.Color getColor(boolean brightHint, boolean foregroundHint) {
            switch(color) {
                case BLACK:
                    if(brightHint)
                        return appearance.getColorPalette().getBrightBlack();
                    else
                        return appearance.getColorPalette().getNormalBlack();

                case BLUE:
                    if(brightHint)
                        return appearance.getColorPalette().getBrightBlue();
                    else
                        return appearance.getColorPalette().getNormalBlue();

                case CYAN:
                    if(brightHint)
                        return appearance.getColorPalette().getBrightCyan();
                    else
                        return appearance.getColorPalette().getNormalCyan();

                case DEFAULT:
                    if(brightHint)
                        return appearance.getColorPalette().getDefaultBrightColor();
                    else
                        return appearance.getColorPalette().getDefaultColor();

                case GREEN:
                    if(brightHint)
                        return appearance.getColorPalette().getBrightGreen();
                    else
                        return appearance.getColorPalette().getNormalGreen();

                case MAGENTA:
                    if(brightHint)
                        return appearance.getColorPalette().getBrightMagenta();
                    else
                        return appearance.getColorPalette().getNormalMagenta();

                case RED:
                    if(brightHint)
                        return appearance.getColorPalette().getBrightRed();
                    else
                        return appearance.getColorPalette().getNormalRed();

                case WHITE:
                    if(brightHint)
                        return appearance.getColorPalette().getBrightWhite();
                    else
                        return appearance.getColorPalette().getNormalWhite();

                case YELLOW:
                    if(brightHint)
                        return appearance.getColorPalette().getBrightYellow();
                    else
                        return appearance.getColorPalette().getNormalYellow();
            }
            return java.awt.Color.PINK;
        }        
    }
    
    private class CharacterIndexedColor extends TerminalCharacterColor {

        private final int index;

        CharacterIndexedColor(int index) {
            this.index = index;
        }
        
        @Override
        public java.awt.Color getColor(boolean brightHint, boolean foregroundHint) {
            return XTerm8bitIndexedColorUtils.getAWTColor(index, appearance.getColorPalette());
        }        
    }
    
    private static class Character24bitColor extends TerminalCharacterColor {
        
        private final java.awt.Color c;

        Character24bitColor(java.awt.Color c) {
            this.c = c;
        }
        
        @Override
        public java.awt.Color getColor(boolean brightHint, boolean foregroundHint) {
            return c;
        }
    }
}
