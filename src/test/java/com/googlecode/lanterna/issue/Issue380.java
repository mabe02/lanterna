package com.googlecode.lanterna.issue;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.IOException;
import java.util.Collections;

public class Issue380 {
    public static void main(String[] args) throws IOException {
        Screen screen = new DefaultTerminalFactory().createScreen();
        screen.startScreen();
        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);
        Window window = new GridWindowWithTwoLargeComponents();
        window.setHints(Collections.singletonList(Window.Hint.EXPANDED));
        gui.addWindow(window);
        gui.waitForWindowToClose(window);
        screen.stopScreen();
    }

    private static class GridWindowWithTwoLargeComponents extends AbstractWindow {
        GridWindowWithTwoLargeComponents() {
            // two column grid
            final Panel p = new Panel(new GridLayout(2));

            // spanning component in the first row
            p.addComponent(new Label("My dummy label"), GridLayout.createLayoutData(
                    GridLayout.Alignment.FILL,
                    GridLayout.Alignment.BEGINNING,
                    true,
                    false,
                    2,
                    1)
            );

            // col 1, row 2
            p.addComponent(new TextBox(),         GridLayout.createLayoutData(
                    GridLayout.Alignment.FILL,
                    GridLayout.Alignment.FILL,
                    true,
                    true)
            );
            // col 2, row 2
            p.addComponent(this.buildButtonPanel(), GridLayout.createLayoutData(
                    GridLayout.Alignment.BEGINNING,
                    GridLayout.Alignment.BEGINNING,
                    false,
                    false));

            // spanning component in row 3
            p.addComponent(this.buildButtonBar(),   GridLayout.createLayoutData(
                    GridLayout.Alignment.CENTER,
                    GridLayout.Alignment.BEGINNING,
                    false,
                    false,
                    2,
                    1)
            );
            setComponent(p);
        }

        private Component buildButtonPanel() {
            Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));
            panel.addComponent(new Button("One"));
            panel.addComponent(new Button("Two"));
            panel.addComponent(new Button("Three"));
            return panel;
        }

        private Component buildButtonBar() {
            return new Button("Close", this::close);
        }
    }
}
