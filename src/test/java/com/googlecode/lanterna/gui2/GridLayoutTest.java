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
 * Copyright (C) 2010-2016 Martin
 */
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
