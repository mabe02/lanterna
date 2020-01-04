/*
 * Author Valentin(linouxis9), modified by Andreas(avl42)
 */
package com.googlecode.lanterna.issue;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.*;
import com.googlecode.lanterna.terminal.*;

import java.io.IOException;

public class Issue359 {
    public static void main(String[] args) {
        try {
            Screen screen = new DefaultTerminalFactory().createScreen();
            screen.startScreen();

            Window window = new BasicWindow();
            Button button = new Button("Hello");

            // Replacing a Component by itself just Border-wrapped
            // caused a NullPointerException lateron from within
            //   the call to gui.addWindowAndWait(window);
            window.setComponent(button);
            window.setComponent(button.withBorder(Borders.singleLine("Border")));

            MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);
            gui.addWindowAndWait(window);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
