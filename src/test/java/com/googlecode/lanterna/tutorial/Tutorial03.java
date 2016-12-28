package com.googlecode.lanterna.tutorial;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalResizeListener;

import java.io.IOException;

/**
 * The third tutorial, introducing the Screen interface
 * @author Martin
 */
public class Tutorial03 {
    public static void main(String[] args) {
        /*
        In the third tutorial, we will look at using the next layer available in Lanterna, which is built on top of the
        Terminal interface you saw in tutorial 1 and 2.

        A Screen works similar to double-buffered video memory, it has two surfaces than can be directly addressed and
        modified and by calling a special method that content of the back-buffer is move to the front. Instead of pixels
        though, a Screen holds two text character surfaces (front and back) which corresponds to each "cell" in the
        terminal. You can freely modify the back "buffer" and you can read from the front "buffer", calling the
        refreshScreen() method to copy content from the back buffer to the front buffer, which will make Lanterna also
        apply the changes so that the user can see them in the terminal.
         */
        DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
        Screen screen = null;
        try {
            /*
            You can use the DefaultTerminalFactory to create a Screen, this will generally give you the TerminalScreen
            implementation that is probably what you want to do. Please see VirtualScreen for more details on a separate
            implementation that allows you to create a terminal surface that is bigger than the physical size of the
            terminal emulator the software is running in.
             */
            screen = defaultTerminalFactory.createScreen();

            /*
            Screens will only work in private mode and while you can call methods to mutate its state, before you can
            make any of these changes visible, you'll need to call startScreen() which will prepare and setup the
            terminal.
             */
            screen.startScreen();

            /*
            Let's turn off the cursor for this tutorial
             */
            screen.setCursorPosition(null);

            // TODO...
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        finally {
            if(screen != null) {
                try {
                    /*
                    The close() call here will restore the terminal by exiting from private mode which was done in
                    the call to startScreen()
                     */
                    screen.close();
                }
                catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
