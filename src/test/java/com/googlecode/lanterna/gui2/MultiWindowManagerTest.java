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

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by martin on 29/11/15.
 */
public class MultiWindowManagerTest extends TestBase {

    private static final AtomicInteger WINDOW_COUNTER = new AtomicInteger(0);

    public static void main(String[] args) throws IOException, InterruptedException {
        new MultiWindowManagerTest().run(args);
    }

    @Override
    public void init(final WindowBasedTextGUI textGUI) {
        textGUI.getBackgroundPane().setComponent(new BackgroundComponent());
        final Window mainWindow = new BasicWindow("Multi Window Test");
        Panel contentArea = new Panel();
        contentArea.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        contentArea.addComponent(new Button("Add new window", new Runnable() {
            @Override
            public void run() {
                onNewWindow(textGUI);
            }
        }));
        contentArea.addComponent(new EmptySpace(TerminalSize.ONE));
        contentArea.addComponent(new Button("Close", new Runnable() {
            @Override
            public void run() {
                mainWindow.close();
            }
        }));
        mainWindow.setComponent(contentArea);
        textGUI.addListener(new TextGUI.Listener() {
            @Override
            public boolean onUnhandledKeyStroke(TextGUI textGUI, KeyStroke keyStroke) {
                if(keyStroke.isCtrlDown() && keyStroke.getKeyType() == KeyType.Tab) {
                    ((WindowBasedTextGUI)textGUI).cycleActiveWindow(false);
                }
                else if(keyStroke.isCtrlDown() && keyStroke.getKeyType() == KeyType.ReverseTab) {
                    ((WindowBasedTextGUI)textGUI).cycleActiveWindow(true);
                }
                else {
                    return false;
                }
                return true;
            }
        });
        textGUI.addWindow(mainWindow);
    }

    private void onNewWindow(WindowBasedTextGUI textGUI) {
        DynamicWindow window = new DynamicWindow();
        textGUI.addWindow(window);
    }

    private static class DynamicWindow extends BasicWindow {

        private final Label labelWindowSize;
        private final Label labelWindowPosition;
        private final Label labelUnlockWindow;

        public DynamicWindow() {
            super("Window #" + WINDOW_COUNTER.incrementAndGet());

            Panel statsTableContainer = new Panel();
            statsTableContainer.setLayoutManager(new GridLayout(2));
            statsTableContainer.addComponent(new Label("Position:"));
            this.labelWindowPosition = new Label("");
            statsTableContainer.addComponent(labelWindowPosition);
            statsTableContainer.addComponent(new Label("Size:"));
            this.labelWindowSize = new Label("");
            statsTableContainer.addComponent(labelWindowSize);
            statsTableContainer.addComponent(new Label("Auto-size:"));
            this.labelUnlockWindow = new Label("true");
            statsTableContainer.addComponent(labelUnlockWindow);

            Panel contentArea = new Panel();
            contentArea.setLayoutManager(new GridLayout(1));
            contentArea.addComponent(statsTableContainer);
            contentArea.addComponent(new EmptySpace(TerminalSize.ONE));
            contentArea.addComponent(
                    new Label(
                            "Move window with ALT+Arrow\n" +
                            "Resize window with CTRL+Arrow\n" +
                            " (need to disabled managed mode to resize)"));
            contentArea.addComponent(new EmptySpace(TerminalSize.ONE).setLayoutData(
                    GridLayout.createLayoutData(GridLayout.Alignment.FILL, GridLayout.Alignment.FILL, true, true)));
            contentArea.addComponent(
                    Panels.horizontal(
                            new Button("Toggle managed", new Runnable() {
                                @Override
                                public void run() {
                                    toggleManaged();
                                }
                            }),
                            new Button("Close", new Runnable() {
                @Override
                public void run() {
                    close();
                }
            })));
            setComponent(contentArea);
        }

        private void toggleManaged() {
            boolean isManaged = labelUnlockWindow.getText().equals("true");
            isManaged = !isManaged;
            if(isManaged) {
                setHints(Collections.EMPTY_LIST);
            }
            else {
                setHints(Arrays.asList(Hint.FIXED_SIZE));
            }
            labelUnlockWindow.setText(Boolean.toString(isManaged));
        }

        @Override
        public boolean handleInput(KeyStroke key) {
            boolean handled = super.handleInput(key);
            if(!handled) {
                switch(key.getKeyType()) {
                    case ArrowDown:
                        if(key.isAltDown()) {
                            setPosition(getPosition().withRelativeRow(1));
                        }
                        else if(key.isCtrlDown()) {
                            setSize(getSize().withRelativeRows(1));
                        }
                        handled = true;
                        break;
                    case ArrowLeft:
                        if(key.isAltDown()) {
                            setPosition(getPosition().withRelativeColumn(-1));
                        }
                        else if(key.isCtrlDown() && getSize().getColumns() > 1) {
                            setSize(getSize().withRelativeColumns(-1));
                        }
                        handled = true;
                        break;
                    case ArrowRight:
                        if(key.isAltDown()) {
                            setPosition(getPosition().withRelativeColumn(1));
                        }
                        else if(key.isCtrlDown()) {
                            setSize(getSize().withRelativeColumns(1));
                        }
                        handled = true;
                        break;
                    case ArrowUp:
                        if(key.isAltDown()) {
                            setPosition(getPosition().withRelativeRow(-1));
                        }
                        else if(key.isCtrlDown() && getSize().getRows() > 1) {
                            setSize(getSize().withRelativeRows(-1));
                        }
                        handled = true;
                        break;
                }
            }
            return handled;
        }

        @Override
        public void draw(TextGUIGraphics graphics) {
            this.labelWindowPosition.setText(getPosition().toString());
            this.labelWindowSize.setText(graphics.getSize().toString());
            super.draw(graphics);
        }
    }

    private class BackgroundComponent extends AbstractComponent<BackgroundComponent> {
        @Override
        protected ComponentRenderer<BackgroundComponent> createDefaultRenderer() {
            return new ComponentRenderer<BackgroundComponent>() {
                @Override
                public TerminalSize getPreferredSize(BackgroundComponent component) {
                    return TerminalSize.ONE;
                }

                @Override
                public void drawComponent(TextGUIGraphics graphics, BackgroundComponent component) {
                    graphics.setForegroundColor(TextColor.ANSI.CYAN);
                    graphics.setBackgroundColor(TextColor.ANSI.BLUE);
                    graphics.fill(' ');
                    String text = "Press <CTRL+Tab> and <CTRL+Shift+Tab> to cycle active window";
                    graphics.putString(graphics.getSize().getColumns() - text.length() - 4, graphics.getSize().getRows() - 1, text, SGR.BOLD);
                }
            };
        }


    }
}
