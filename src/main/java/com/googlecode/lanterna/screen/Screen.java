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
 *
 * @author martin
 */
public interface Screen {

    /**
     * Erases all the characters on the screen, effectively giving you a blank area. The default background color will
     * be used, if you want to fill the screen with a different color you will need to do this manually.
     */
    void clear();

    /**
     * Clears the terminal and repaints with the whole content of the Screen. This is useful of something has written to
     * the terminal outside of the Screen (System.out or through direct calls to the underlying Terminal) and you want
     * to make sure that the content of Screen is completely pushed to the terminal.
     */
    void completeRefresh();

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
     * Call this method to make changes done through {@code putCharacter(...)},
     * {@code putString(...)} visible on the terminal. The screen will calculate the changes that are required and send
     * the necessary characters and control sequences to make it so. If the terminal has been resized since the last
     * refresh, and no call to {@code doResize()} has been made, this method will resize the internal buffer and fill
     * the extra space with a padding character.
     */
    void refresh();

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
