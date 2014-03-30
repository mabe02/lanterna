/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.lanterna.screen;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.terminal.TextColor;
import java.io.IOException;
import java.util.Set;

/**
 * A layer to put on top of a Terminal object, giving you a kind of screen buffer to use, which is a lot easier to work
 * with. Drawing text or graphics to the terminal is kind of like writing to a bitmap.
 *
 * @author Martin
 */
public interface Screen {

    /**
     * Erases all the characters on the screen, effectively giving you a blank area. The default background color will
     * be used. This is effectively the same as calling 
     * "fill(TerminalPosition.TOP_LEFT_CORNER, getSize(), TextColor.ANSI.Default)".
     * <p/>
     * Please note that calling this method will only affect the back buffer, you need to call refresh to make the 
     * change visible.
     */
    void clear();
    
    /**
     * Takes a rectangle on the screen and fills it with a particular color. Please note that calling this method will 
     * only affect the back buffer, you need to call refresh to make the change visible.
     * @param topLeft The top-left (inclusive) coordinate of the top left corner of the rectangle
     * @param size Size (in columns and rows) of the area to draw
     * @param color Color to draw the rectangle with
     */
    void fill(TerminalPosition topLeft, TerminalSize size, TextColor color);

    /**
     * @return Position where the cursor will be located after the screen has been refreshed or {@code null} if the
     * cursor is not visible
     */
    TerminalPosition getCursorPosition();

    /**
     * Gets the behaviour for what to do about tab characters.
     *
     * @see TabBehaviour
     */
    TabBehaviour getTabBehaviour();

    /**
     * @return Size of the screen
     */
    TerminalSize getTerminalSize();

    /**
     * Draws a string on the screen at a particular position
     *
     * @param x 0-indexed column number of where to put the first character in the string
     * @param y 0-indexed row number of where to put the first character in the string
     * @param string Text to put on the screen
     * @param foregroundColor What color to use for the text
     * @param backgroundColor What color to use for the background
     * @param styles Additional styles to apply to the text
     */
    void putString(int x, int y, String string, TextColor foregroundColor, TextColor backgroundColor, ScreenCharacterStyle... styles);

    /**
     * Draws a string on the screen at a particular position
     *
     * @param x 0-indexed column number of where to put the first character in the string
     * @param y 0-indexed row number of where to put the first character in the string
     * @param string Text to put on the screen
     * @param foregroundColor What color to use for the text
     * @param backgroundColor What color to use for the background
     * @param styles Additional styles to apply to the text
     */
    void putString(int x, int y, String string, TextColor foregroundColor, TextColor backgroundColor, Set<ScreenCharacterStyle> styles);

    /**
     * Reads the next {@code Key} from the input queue, or returns null if there is nothing on the queue.
     */
    KeyStroke readInput() throws IOException;

    /**
     * Call this method to make changes done to the backbuffer visible on the screen. The screen will calculate the 
     * changes that are required and send the necessary characters and control sequences to make it so. If the terminal 
     * has been resized since the last refresh, and no call to {@code doResize()} has been made, this method will resize the internal buffer and fill
     * the extra space with a padding character.
     * <p/>
     * Note, calling this is the same as calling refresh(false);
     */
    void refresh();
    
    /**
     * Call this method to make changes done to the backbuffer visible on the screen. There are two ways of doing this,
     * either by clearing the whole terminal and re-drawing the backbuffer to the terminal. That is called a complete 
     * refresh and will be used if you pass in true to the completeRefresh parameter. The other way is to calculate the
     * diff between what's currently visible and what's in the backbuffer, then generate what operations that needs to
     * be done to make the terminal display the backbuffer.
     * <p/>
     * In general, if you have changed the screen a lot since the last refresh, it makes sense do a complete refresh,
     * but if only a couple of locations are different then you should probably use the diff method.
     * @param completeRefresh If true, will do a complete redraw of the terminal, otherwise it will calculate a diff 
     * between the screen and the back-buffer and only send the changes required to make the terminal like the buffer
     */
    void refresh(boolean completeRefresh);

    /**
     * This method will check if there are any resize commands pending. If true, you need to call refresh() to perform
     * the screen resize
     *
     * @return true if the size is the same as before, false if the screen should be resized
     */
    boolean resizePending();

    /**
     * Moves the current cursor position or hides it. If the cursor is hidden and given a new position, it will be
     * visible after this method call.
     *
     * @param position 0-indexed column and row numbers of the new position, or if {@code null}, hides the cursor
     */
    void setCursorPosition(TerminalPosition position);

    /**
     * Moves the current cursor position, and if the cursor was hidden it will be visible after this call
     *
     * @param column 0-indexed column number of the new position
     * @param row 0-indexed row number of the new position
     */
    void setCursorPosition(int column, int row);

    void setPaddingCharacter(char character, TextColor foregroundColor, TextColor backgroundColor, ScreenCharacterStyle... style);

    /**
     * Sets the behaviour for what to do about tab characters.
     *
     * @see TabBehaviour
     */
    void setTabBehaviour(TabBehaviour tabBehaviour);

    /**
     * Calling this method will put the underlying terminal in private mode, clear the screen, move the cursor and
     * refresh.
     *
     * @throws LanternaException
     */
    void startScreen() throws IOException;

    /**
     * Calling this method will make the underlying terminal leave private mode, effectively going back to whatever
     * state the terminal was in before calling {@code startScreen()}
     *
     * @throws LanternaException
     */
    void stopScreen() throws IOException;
    
}
