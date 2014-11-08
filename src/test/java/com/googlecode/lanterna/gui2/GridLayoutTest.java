package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;

import java.io.IOException;

/**
 * Created by martin on 20/09/14.
 */
public class GridLayoutTest extends TestBase {
    public static void main(String[] args) throws IOException, InterruptedException {
        new GridLayoutTest().run(args);
    }

    @Override
    public void init(WindowBasedTextGUI textGUI) {
        final BasicWindow window = new BasicWindow("Grid layout test");
        Panel contentArea = new Panel();
/*        contentArea.setLayoutManager(new GridLayout(4, false));
        contentArea.addComponent(new EmptySpace(TextColor.ANSI.BLACK, new TerminalSize(4, 2)));
        contentArea.addComponent(new EmptySpace(TextColor.ANSI.BLUE, new TerminalSize(4, 2)));
        contentArea.addComponent(new EmptySpace(TextColor.ANSI.CYAN, new TerminalSize(4, 2)));
        contentArea.addComponent(new EmptySpace(TextColor.ANSI.GREEN, new TerminalSize(4, 2)));
        contentArea.addComponent(new EmptySpace(TextColor.ANSI.MAGENTA, new TerminalSize(4, 2))
                .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.BEGINNING, GridLayout.Alignment.CENTER, true, false, 4, 1)));
        contentArea.addComponent(new EmptySpace(TextColor.ANSI.RED, new TerminalSize(4, 2))
        .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER, true, false, 4, 1)));
        contentArea.addComponent(new EmptySpace(TextColor.ANSI.YELLOW, new TerminalSize(4, 2))
                .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.END, GridLayout.Alignment.CENTER, true, false, 4, 1)));
        contentArea.addComponent(new EmptySpace(TextColor.ANSI.BLACK, new TerminalSize(4, 2))
                .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.FILL, GridLayout.Alignment.CENTER, true, false, 4, 1)));*/

        contentArea.addComponent(new Button("OK", new Runnable() {
            @Override
            public void run() {
                window.close();
            }
        }));
        window.setComponent(contentArea);
        textGUI.getWindowManager().addWindow(window);
    }
}
