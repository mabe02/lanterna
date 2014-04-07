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
package com.googlecode.lanterna.screen;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.ResizeListener;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.terminal.TextColor;
import java.io.IOException;

/**
 * A layer to put on top of a Terminal object, giving you a kind of screen buffer to use, which is a lot easier to work
 * with. Drawing text or graphics to the terminal is kind of like writing to a bitmap.
 *
 * @author Martin
 */
public interface Screen {
    /**
     * Before you can use a Screen, you need to start it. By starting the screen, Lanterna will make sure the terminal
     * is in private mode (Screen only supports private mode), clears it (so that is can set the front and back buffers
     * to a known value) and places the cursor in the top left corner. After calling startScreen(), you can begin using
     * the other methods on this interface. When you want to exit from the screen and return to what you had before,
     * you can call {@code stopScreen()}.
     *
     * @throws IOException if there was an underlying IO error when exiting from private mode
     */
    void startScreen() throws IOException;

    /**
     * Calling this method will make the underlying terminal leave private mode, effectively going back to whatever
     * state the terminal was in before calling {@code startScreen()}. Once a screen has been stopped, you can start it
     * again with {@code startScreen()} which will restore the screens content to the terminal.
     *
     * @throws IOException if there was an underlying IO error when exiting from private mode
     */
    void stopScreen() throws IOException;

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
     * Takes a rectangle on the screen and fills it with a particular character and color. Please note that calling this 
     * method will only affect the back buffer, you need to call refresh() to make the change visible.
     * @param topLeft The top-left (inclusive) coordinate of the top left corner of the rectangle
     * @param size Size (in columns and rows) of the area to draw
     * @param character What character to use when filling the rectangle
     * @param color Color to draw the rectangle with
     */
    void fillRectangle(TerminalPosition topLeft, TerminalSize size, Character character, TextColor color);

    /**
     * A screen implementation typically keeps a location on the screen where the cursor will be placed after drawing
     * and refreshing the buffers, this method returns that location. If it returns null, it means that the terminal 
     * will attempt to hide the cursor (if supported by the terminal).
     * 
     * @return Position where the cursor will be located after the screen has been refreshed or {@code null} if the
     * cursor is not visible
     */
    TerminalPosition getCursorPosition();
    
    /**
     * A screen implementation typically keeps a location on the screen where the cursor will be placed after drawing
     * and refreshing the buffers, this method controls that location. If you pass null, it means that the terminal 
     * will attempt to hide the cursor (if supported by the terminal).
     *
     * @param position TerminalPosition of the new position where the cursor should be placed after refresh(), or if 
     * {@code null}, hides the cursor
     */
    void setCursorPosition(TerminalPosition position);

    /**
     * Gets the behaviour for what to do about tab characters.
     *
     * @return Behaviour for how this Screen should treat tab characters
     * @see TabBehaviour
     */
    TabBehaviour getTabBehaviour();

    /**
     * Sets the behaviour for what to do about tab characters.
     *
     * @param tabBehaviour Behaviour for how this Screen should treat tab characters
     * @see TabBehaviour
     */
    void setTabBehaviour(TabBehaviour tabBehaviour);

    /**
     * @return Size of the screen, in columns and rows
     */
    TerminalSize getTerminalSize();

    /**
     * Draws a string on the screen at a particular position
     *
     * @param position Position of the first character in the string on the screen, the remaining characters will follow
     * immediately to the right
     * @param string Text to put on the screen
     * @param foregroundColor What color to use for the text
     * @param backgroundColor What color to use for the background
     * @param styles Additional styles to apply to the text
     */
    void putString(TerminalPosition position, String string, TextColor foregroundColor, TextColor backgroundColor, Terminal.SGR... styles);

    /**
     * Reads the next {@code KeyStroke} from the input queue, or returns null if there is nothing on the queue. This
     * method will <b>not</b> block until input is available, you'll need to poll it periodically.
     * @return The KeyStroke that came from the underlying system, or null if there was no input available
     * @throws java.io.IOException In case there was a problem with the underlying input system
     */
    KeyStroke readInput() throws IOException;

    /**
     * This method will take the content from the back-buffer and move it into the front-buffer, making the changes
     * visible to the terminal in the process. The common workflow with Screen would involve drawing text and text-like
     * graphics on the back buffer and then finally calling refresh(..) to make it visible to the user.
     * @see RefreshType
     */
    void refresh();

    /**
     * This method will take the content from the back-buffer and move it into the front-buffer, making the changes
     * visible to the terminal in the process. The common workflow with Screen would involve drawing text and text-like
     * graphics on the back buffer and then finally calling refresh(..) to make it visible to the user.
     * <p/>
     * Using this method call instead of {@code refresh()} gives you a little bit more control over how the screen will
     * be refreshed.
     * @param refreshType What type of refresh to do
     * @see RefreshType
     */
    void refresh(RefreshType refreshType);

    /**
     * One problem working with Screens is that whenever the terminal is resized, the front and back buffers needs to be
     * adjusted accordingly and the program should have a chance to figure out what to do with this extra space (or less
     * space). The solution is to call, at the start of your rendering code, this method, which will check if the 
     * terminal has been resized and in that case update the internals of the Screen. 
     * @return If the terminal has been resized since this method was last called, it will return the new size of the
     * terminal. If not, it will return null.
     */
    TerminalSize doResizeIfNecessary();
    
    /**
     * Helper method to allow you to subscribe to resize events, when the user resizes the terminal. You should 
     * <b>always</b> assume that the thread calling the listener is something exotic which you cannot keep for too long
     * and you should absolutely not run any operations on the screen from inside it. Rather, try to have the listener
     * trigger something that your main thread can wake up on and take appropriate actions.
     * @param listener Listener to be invoked when the user resizes the terminal
     */
    void addResizeListener(ResizeListener listener);
    
    /**
     * Helper method to allow you to unsubscribe to resize events, when the user resizes the terminal.
     * @param listener Listener that should no longer be invoked when the user resizes the terminal
     */
    void removeResizeListener(ResizeListener listener);
    
    /**
     * This enum represents the different ways a Screen can refresh the screen, moving the back-buffer data into the
     * front-buffer that is being displayed.
     */
    public static enum RefreshType {
        
        AUTOMATIC,
        /**
         * In {@code RefreshType.DELTA} mode, the Screen will calculate a diff between the back-buffer and the 
         * front-buffer, then figure out the set of terminal commands that is required to make the front-buffer exactly
         * like the back-buffer. This normally works well when you have modified only parts of the screen, but if you
         * have modified almost everything it will cause a lot of overhead and you should use 
         * {@code RefreshType.COMPLETE} instead.
         */
        DELTA,
        /**
         * In {@code RefreshType.COMPLETE} mode, the screen will send a clear command to the terminal, when redraw the 
         * whole back-buffer line by line. This is more expensive than {@code RefreshType.COMPLETE}, especially when you
         * have only touched smaller parts of the screen, but can be faster if you have modified most of the content, 
         * as well as if you suspect the screen's internal front buffer is out-of-sync with what's really showing on the
         * terminal (you didn't go and call methods on the underlying Terminal while in screen mode, did you?)
         */
        COMPLETE,
        ;
    }
}
