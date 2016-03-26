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
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.ansi.TelnetTerminal;
import com.googlecode.lanterna.terminal.ansi.TelnetTerminalServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by martin on 11/10/14.
 */
public class GUIOverTelnet {
    public static void main(String[] args) throws IOException {
        TelnetTerminalServer telnetTerminalServer = new TelnetTerminalServer(1024);
        System.out.println("Listening on port 1024, please connect to it with a separate telnet process");
        //noinspection InfiniteLoopStatement
        while(true) {
            final TelnetTerminal telnetTerminal = telnetTerminalServer.acceptConnection();
            System.out.println("Accepted connection from " + telnetTerminal.getRemoteSocketAddress());
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        runGUI(telnetTerminal);
                    }
                    catch(IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        telnetTerminal.close();
                    }
                    catch(IOException ignore) {}
                }
            };
            thread.start();
        }
    }

    private static final List<TextBox> ALL_TEXTBOXES = new ArrayList<TextBox>();

    @SuppressWarnings({"rawtypes","unchecked"})
    private static void runGUI(final TelnetTerminal telnetTerminal) throws IOException {
        Screen screen = new TerminalScreen(telnetTerminal);
        screen.startScreen();
        final MultiWindowTextGUI textGUI = new MultiWindowTextGUI(screen);
        textGUI.setBlockingIO(false);
        textGUI.setEOFWhenNoWindows(true);
        try {
            final BasicWindow window = new BasicWindow("Text GUI over Telnet");
            Panel contentArea = new Panel();
            contentArea.setLayoutManager(new LinearLayout(Direction.VERTICAL));
            contentArea.addComponent(new Button("Button", new Runnable() {
                @Override
                public void run() {
                    final BasicWindow messageBox = new BasicWindow("Response");
                    messageBox.setComponent(Panels.vertical(
                            new Label("Hello!"),
                            new Button("Close", new Runnable() {
                                @Override
                                public void run() {
                                    messageBox.close();
                                }
                            })));
                    textGUI.addWindow(messageBox);
                }
            }).withBorder(Borders.singleLine("This is a button")));


            final TextBox textBox = new TextBox(new TerminalSize(20, 4)) {
                @Override
                public Result handleKeyStroke(KeyStroke keyStroke) {
                    try {
                        return super.handleKeyStroke(keyStroke);
                    }
                    finally {
                        for(TextBox box: ALL_TEXTBOXES) {
                            if(this != box) {
                                box.setText(getText());
                            }
                        }
                    }
                }
            };
            ALL_TEXTBOXES.add(textBox);
            contentArea.addComponent(textBox.withBorder(Borders.singleLine("Text editor")));
            contentArea.addComponent(new AbstractInteractableComponent() {
                String text = "Press any key";
                @Override
                protected InteractableRenderer createDefaultRenderer() {
                    return new InteractableRenderer() {
                        @Override
                        public TerminalSize getPreferredSize(Component component) {
                            return new TerminalSize(30, 1);
                        }

                        @Override
                        public void drawComponent(TextGUIGraphics graphics, Component component) {
                            graphics.putString(0, 0, text);
                        }

                        @Override
                        public TerminalPosition getCursorLocation(Component component) {
                            return TerminalPosition.TOP_LEFT_CORNER;
                        }
                    };
                }

                @Override
                public Result handleKeyStroke(KeyStroke keyStroke) {
                    if(keyStroke.getKeyType() == KeyType.Tab ||
                            keyStroke.getKeyType() == KeyType.ReverseTab) {
                        return super.handleKeyStroke(keyStroke);
                    }
                    if(keyStroke.getKeyType() == KeyType.Character) {
                        text = "Character: " + keyStroke.getCharacter() + (keyStroke.isCtrlDown() ? " (ctrl)" : "") +
                                (keyStroke.isAltDown() ? " (alt)" : "");
                    }
                    else {
                        text = "Key: " + keyStroke.getKeyType() + (keyStroke.isCtrlDown() ? " (ctrl)" : "") +
                                (keyStroke.isAltDown() ? " (alt)" : "");
                    }
                    return Result.HANDLED;
                }
            }.withBorder(Borders.singleLine("Custom component")));

            contentArea.addComponent(new Button("Close", new Runnable() {
                @Override
                public void run() {
                    window.close();
                }
            }));
            window.setComponent(contentArea);

            textGUI.addWindowAndWait(window);
        }
        finally {
            screen.stopScreen();
        }
    }
}
