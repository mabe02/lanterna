/*
 * This file is part of lanterna (https://github.com/mabe02/lanterna).
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
 * Copyright (C) 2010-2020 Martin Berglund
 */
package com.googlecode.lanterna.issue;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.bundle.LanternaThemes;
import com.googlecode.lanterna.graphics.*;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Issue409 {
    public static void main(String[] args) {
        try {
            Screen screen = new DefaultTerminalFactory().createScreen();
            screen.startScreen();

            final Window window = new BasicWindow();

            Panel panel = new Panel();
            panel.addComponent(new CustomBackgroundTextBox(TextColor.ANSI.RED));
            panel.addComponent(new EmptySpace());
            panel.addComponent(new CustomBackgroundTextBox(TextColor.ANSI.GREEN));
            panel.addComponent(new EmptySpace());
            final CyclingThemesTextBox cyclingThemesTextBox = new CyclingThemesTextBox();
            panel.addComponent(cyclingThemesTextBox);
            panel.addComponent(new EmptySpace());
            panel.addComponent(new Button("Close", window::close));

            window.setComponent(panel);
            final MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);
            gui.addWindow(window);
            new Thread(() -> {
                int counter = 0;
                while(cyclingThemesTextBox.getTextGUI() != null) {
                    if (++counter % 200 == 0) {
                        gui.getGUIThread().invokeLater(cyclingThemesTextBox::nextTheme);
                    }
                    else {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                }
            }).start();

            window.waitUntilClosed();
            screen.stopScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class CustomBackgroundTextBox extends TextBox {
        public CustomBackgroundTextBox(final TextColor.ANSI color) {
            super("Custom " + color.name());
            setTheme(new DelegatingTheme(getTheme()) {
                @Override
                public ThemeDefinition getDefinition(Class<?> clazz) {
                    ThemeDefinition themeDefinition = super.getDefinition(clazz);
                    return new FixedBackgroundTextBoxThemeStyle(themeDefinition, color);
                }
            });
        }
    }

    private static class CyclingThemesTextBox extends TextBox {
        private final List<String> systemThemes;
        private int index;

        public CyclingThemesTextBox() {
            super("Cycling themes: default");
            setPreferredSize(new TerminalSize(40, 1));
            systemThemes = new ArrayList<>(LanternaThemes.getRegisteredThemes());
            index = 0;
        }

        void nextTheme() {
            if (++index == systemThemes.size()) {
                index = 0;
            }
            String name = systemThemes.get(index);
            Theme theme = LanternaThemes.getRegisteredTheme(name);
            setTheme(theme);
            setText("Cycling themes: " + name);
        }
    }

    private static class FixedBackgroundTextBoxThemeStyle extends DelegatingThemeDefinition {
        private final TextColor.ANSI color;

        public FixedBackgroundTextBoxThemeStyle(ThemeDefinition definition, TextColor.ANSI color) {
            super(definition);
            this.color = color;
        }

        @Override
        public ThemeStyle getNormal() {
            DefaultMutableThemeStyle mutableThemeStyle = new DefaultMutableThemeStyle(super.getNormal());
            return mutableThemeStyle.setBackground(color);
        }

        @Override
        public ThemeStyle getActive() {
            DefaultMutableThemeStyle mutableThemeStyle = new DefaultMutableThemeStyle(super.getActive());
            return mutableThemeStyle.setBackground(color);
        }
    }
}
