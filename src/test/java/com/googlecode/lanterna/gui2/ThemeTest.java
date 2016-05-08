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

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.bundle.LanternaThemes;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Martin on 2016-05-08.
 */
public class ThemeTest extends TestBase {
    public static void main(String[] args) throws IOException, InterruptedException {
        new ThemeTest().run(args);
    }

    @Override
    public void init(final WindowBasedTextGUI textGUI) {
        final BasicWindow mainSelectionWindow = new BasicWindow("Theme Tests");
        ActionListBox mainSelector = new ActionListBox();
        mainSelector.addItem("Multi-theme test", new Runnable() {
            @Override
            public void run() {
                runMultiThemeTest(textGUI);
            }
        });
        mainSelector.addItem("Exit", new Runnable() {
            @Override
            public void run() {
                mainSelectionWindow.close();
            }
        });
        mainSelectionWindow.setComponent(mainSelector);
        mainSelectionWindow.setHints(Arrays.asList(Window.Hint.CENTERED));

        textGUI.addWindow(mainSelectionWindow);
    }

    private void runMultiThemeTest(final WindowBasedTextGUI textGUI) {
        final BasicWindow window1 = new BasicWindow("Theme: Bigsnake");
        window1.setHints(Arrays.asList(Window.Hint.FIXED_POSITION, Window.Hint.FIXED_SIZE));
        window1.setTheme(LanternaThemes.getRegisteredTheme("bigsnake"));
        window1.setPosition(new TerminalPosition(2, 1));
        window1.setSize(new TerminalSize(24, 9));

        final BasicWindow window2 = new BasicWindow("Theme: Conqueror");
        window2.setHints(Arrays.asList(Window.Hint.FIXED_POSITION, Window.Hint.FIXED_SIZE));
        window2.setTheme(LanternaThemes.getRegisteredTheme("conqueror"));
        window2.setPosition(new TerminalPosition(30, 1));
        window2.setSize(new TerminalSize(24, 9));

        final Panel leftHolder = new Panel().setPreferredSize(new TerminalSize(15, 4));
        final Panel rightHolder = new Panel().setPreferredSize(new TerminalSize(15, 4));
        GridLayout layoutManager = new GridLayout(1);
        leftHolder.setLayoutManager(layoutManager);
        rightHolder.setLayoutManager(layoutManager);

        final Button exampleButton = new Button("Example");
        exampleButton.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER, true, true));
        leftHolder.addComponent(exampleButton);

        ActionListBox leftWindowActionBox = new ActionListBox()
                .addItem("Move button to right", new Runnable() {
                    @Override
                    public void run() {
                        // TODO: Figure out why we need to remove the button first; it should be automatic!
                        leftHolder.removeComponent(exampleButton);
                        rightHolder.addComponent(exampleButton);
                    }
                })
                .addItem("Switch active window", new Runnable() {
                    @Override
                    public void run() {
                        textGUI.setActiveWindow(window2);
                    }
                })
                .addItem("Exit", new Runnable() {
                    @Override
                    public void run() {
                        window1.close();
                        window2.close();
                    }
                });
        window1.setComponent(
                Panels.vertical(
                    leftHolder.withBorder(Borders.singleLine()),
                    leftWindowActionBox));

        ActionListBox rightWindowActionBox = new ActionListBox()
                .addItem("Move button to left", new Runnable() {
                    @Override
                    public void run() {
                        rightHolder.removeComponent(exampleButton);
                        leftHolder.addComponent(exampleButton);
                    }
                })
                .addItem("Switch active window", new Runnable() {
                    @Override
                    public void run() {
                        textGUI.setActiveWindow(window1);
                    }
                })
                .addItem("Exit", new Runnable() {
                    @Override
                    public void run() {
                        window1.close();
                        window2.close();
                    }
                });
        window2.setComponent(
                Panels.vertical(
                    rightHolder.withBorder(Borders.singleLine()),
                    rightWindowActionBox));

        window1.setFocusedInteractable(leftWindowActionBox);
        window2.setFocusedInteractable(rightWindowActionBox);

        textGUI.addWindow(window1);
        textGUI.addWindow(window2);
        textGUI.setActiveWindow(window1);
    }
}
