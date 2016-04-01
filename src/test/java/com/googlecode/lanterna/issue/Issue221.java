package com.googlecode.lanterna.issue;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

/**
 * Created by @boekhold on 31/03/16.
 */
public class Issue221 {
    public static void main(String[] args) throws IOException {

        // Setup terminal and screen layers
        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();

        // Create panel to hold components
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(2));

        panel.addComponent(new Label("The List"));
        RadioBoxList<String> box = new RadioBoxList<String>();
        box.addItem("Item 1");
        box.addItem("Item 2");
        box.addItem("Item 3");
        box.addListener(new RadioBoxList.Listener() {
            @Override public void onSelectionChanged(int selected, int previous) {
                System.out.println("Selected Index: " + selected + ", previous: " + previous);
            }
        });

        panel.addComponent(box);

        // Create window to hold the panel
        BasicWindow window = new BasicWindow();
        window.setComponent(panel);

        // Create gui and start gui
        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));
        gui.addWindowAndWait(window);
    }

}
