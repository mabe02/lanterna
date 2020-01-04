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
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.io.IOException;

@SuppressWarnings("rawtypes")
public class InputUITest extends TestBase {
    public static void main(String[] args) throws IOException, InterruptedException {
        new InputUITest().run(args);
    }

    @Override
    public void init(WindowBasedTextGUI textGUI) {
        final BasicWindow window = new BasicWindow("Input test");

        Interactable interactable = new AbstractInteractableComponent() {
            private String lastKey;

            @Override
            protected Result handleKeyStroke(KeyStroke keyStroke) {
                if (keyStroke.getKeyType() == KeyType.Tab) {
                    return super.handleKeyStroke(keyStroke);
                }
                if (keyStroke.getKeyType() == KeyType.Character) {
                    lastKey = keyStroke.getCharacter() + "";
                }
                else {
                    lastKey = keyStroke.getKeyType().toString();
                }
                if (keyStroke.isCtrlDown()) {
                    lastKey += " + CTRL";
                }
                if (keyStroke.isAltDown()) {
                    lastKey += " + ALT";
                }
                if (keyStroke.isShiftDown()) {
                    lastKey += " + SHIFT";
                }
                return Result.HANDLED;
            }

            @Override
            protected InteractableRenderer createDefaultRenderer() {
                return new InteractableRenderer() {
                    @Override
                    public TerminalPosition getCursorLocation(Component component) {
                        TerminalSize adjustedSize = component.getSize().withRelative(-1, -1);
                        return new TerminalPosition(adjustedSize.getColumns(), adjustedSize.getRows());
                    }

                    @Override
                    public TerminalSize getPreferredSize(Component component) {
                        return new TerminalSize(70, 5);
                    }

                    @Override
                    public void drawComponent(TextGUIGraphics graphics, Component component) {
                        graphics.setBackgroundColor(TextColor.ANSI.BLACK);
                        graphics.setForegroundColor(TextColor.ANSI.WHITE);
                        graphics.fill(' ');
                        if (lastKey != null) {
                            int leftPosition = 35 - (lastKey.length() / 2);
                            graphics.putString(leftPosition, 2, lastKey);
                        }
                    }
                };
            }
        };

        window.setComponent(
                Panels.vertical(
                        interactable.withBorder(Borders.doubleLineBevel("Press any key to test capturing the KeyStroke")),
                        new Label("Use the TAB key to shift focus"),
                        new Button("Close", window::close)));
        textGUI.addWindow(window);
    }
}