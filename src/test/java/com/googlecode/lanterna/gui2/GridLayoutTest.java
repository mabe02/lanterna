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

        Panel leftGridPanel = new Panel();
        leftGridPanel.setLayoutManager(new GridLayout(4));
        leftGridPanel.addComponent(new EmptySpace(TextColor.ANSI.BLACK, new TerminalSize(4, 2)));
        leftGridPanel.addComponent(new EmptySpace(TextColor.ANSI.BLUE, new TerminalSize(4, 2)));
        leftGridPanel.addComponent(new EmptySpace(TextColor.ANSI.CYAN, new TerminalSize(4, 2)));
        leftGridPanel.addComponent(new EmptySpace(TextColor.ANSI.GREEN, new TerminalSize(4, 2)));

        leftGridPanel.addComponent(new EmptySpace(TextColor.ANSI.MAGENTA, new TerminalSize(4, 2))
                .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.BEGINNING, GridLayout.Alignment.CENTER, true, false, 4, 1)));
        leftGridPanel.addComponent(new EmptySpace(TextColor.ANSI.RED, new TerminalSize(4, 2))
                .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER, true, false, 4, 1)));
        leftGridPanel.addComponent(new EmptySpace(TextColor.ANSI.YELLOW, new TerminalSize(4, 2))
                .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.END, GridLayout.Alignment.CENTER, true, false, 4, 1)));
        leftGridPanel.addComponent(new EmptySpace(TextColor.ANSI.BLACK, new TerminalSize(4, 2))
                .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.FILL, GridLayout.Alignment.CENTER, true, false, 4, 1)));

        Panel rightGridPanel = new Panel();
        rightGridPanel.setLayoutManager(new GridLayout(5));
        rightGridPanel.addComponent(new EmptySpace(TextColor.ANSI.BLACK, new TerminalSize(4, 2)));
        rightGridPanel.addComponent(new EmptySpace(TextColor.ANSI.MAGENTA, new TerminalSize(4, 2))
                .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.BEGINNING, false, true, 1, 4)));
        rightGridPanel.addComponent(new EmptySpace(TextColor.ANSI.RED, new TerminalSize(4, 2))
                .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER, false, true, 1, 4)));
        rightGridPanel.addComponent(new EmptySpace(TextColor.ANSI.YELLOW, new TerminalSize(4, 2))
                .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.END, false, true, 1, 4)));
        rightGridPanel.addComponent(new EmptySpace(TextColor.ANSI.BLACK, new TerminalSize(4, 2))
                .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.FILL, false, true, 1, 4)));
        rightGridPanel.addComponent(new EmptySpace(TextColor.ANSI.BLUE, new TerminalSize(4, 2)));
        rightGridPanel.addComponent(new EmptySpace(TextColor.ANSI.CYAN, new TerminalSize(4, 2)));
        rightGridPanel.addComponent(new EmptySpace(TextColor.ANSI.GREEN, new TerminalSize(4, 2)));

        Panel contentPanel = new Panel();
        contentPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        contentPanel.addComponent(Panels.horizontal(leftGridPanel, new EmptySpace(TerminalSize.ONE), rightGridPanel));
        contentPanel.addComponent(new EmptySpace(TerminalSize.ONE));
        contentPanel.addComponent(new Button("Close", new Runnable() {
            @Override
            public void run() {
                window.close();
            }
        }));
        window.setComponent(contentPanel);
        textGUI.addWindow(window);
    }
}
