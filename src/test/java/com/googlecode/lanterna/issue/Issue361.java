/*
 * Author Andrey Zelyaev(zella), slightly modified by Andreas(avl42)
 */
package com.googlecode.lanterna.issue;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.*;
import com.googlecode.lanterna.terminal.*;

import java.util.Collections;

public class Issue361 {

    public static void main(String[] args) throws Exception {

        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();

        BasicWindow window1 = new BasicWindow();
        window1.setHints(Collections.singletonList(Window.Hint.CENTERED));

        BasicWindow window2 = new BasicWindow();
        window2.setHints(Collections.<Window.Hint>emptyList());

        // Create gui and start gui
        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);
        gui.addWindow(window2);
        gui.addWindowAndWait(window1);
    }
}
