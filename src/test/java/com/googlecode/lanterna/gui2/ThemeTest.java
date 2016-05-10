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
import com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
        final List<String> themes = new ArrayList<String>(LanternaThemes.getRegisteredThemes());
        final int[] windowThemeIndex = new int[] { themes.indexOf("bigsnake"), themes.indexOf("conqueror") };
        final BasicWindow window1 = new BasicWindow("Theme: bigsnake");
        window1.setHints(Arrays.asList(Window.Hint.FIXED_POSITION));
        window1.setTheme(LanternaThemes.getRegisteredTheme(themes.get(windowThemeIndex[0])));
        window1.setPosition(new TerminalPosition(2, 1));

        final BasicWindow window2 = new BasicWindow("Theme: conqueror");
        window2.setHints(Arrays.asList(Window.Hint.FIXED_POSITION));
        window2.setTheme(LanternaThemes.getRegisteredTheme(themes.get(windowThemeIndex[1])));
        window2.setPosition(new TerminalPosition(30, 1));

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
                        rightHolder.addComponent(exampleButton);
                    }
                })
                .addItem("Override button theme", new Runnable() {
                    @Override
                    public void run() {
                        ActionListDialogBuilder actionListDialogBuilder = new ActionListDialogBuilder();
                        actionListDialogBuilder.setTitle("Choose theme for the button");
                        for(final String theme: themes) {
                            actionListDialogBuilder.addAction(theme, new Runnable() {
                                @Override
                                public void run() {
                                    exampleButton.setTheme(LanternaThemes.getRegisteredTheme(theme));
                                }
                            });
                        }
                        actionListDialogBuilder.addAction("Clear override", new Runnable() {
                            @Override
                            public void run() {
                                exampleButton.setTheme(null);
                            }
                        });
                        actionListDialogBuilder.build().showDialog(textGUI);
                    }
                })
                .addItem("Cycle window theme", new Runnable() {
                    @Override
                    public void run() {
                        windowThemeIndex[0]++;
                        if(windowThemeIndex[0] >= themes.size()) {
                            windowThemeIndex[0] = 0;
                        }
                        String themeName = themes.get(windowThemeIndex[0]);
                        window1.setTheme(LanternaThemes.getRegisteredTheme(themeName));
                        window1.setTitle("Theme: " + themeName);
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
                        leftHolder.addComponent(exampleButton);
                    }
                })
                .addItem("Override button theme", new Runnable() {
                    @Override
                    public void run() {
                        ActionListDialogBuilder actionListDialogBuilder = new ActionListDialogBuilder();
                        actionListDialogBuilder.setTitle("Choose theme for the button");
                        for(final String theme: themes) {
                            actionListDialogBuilder.addAction(theme, new Runnable() {
                                @Override
                                public void run() {
                                    exampleButton.setTheme(LanternaThemes.getRegisteredTheme(theme));
                                }
                            });
                        }
                        actionListDialogBuilder.addAction("Clear override", new Runnable() {
                            @Override
                            public void run() {
                                exampleButton.setTheme(null);
                            }
                        });
                        actionListDialogBuilder.build().showDialog(textGUI);
                    }
                })
                .addItem("Cycle window theme", new Runnable() {
                    @Override
                    public void run() {
                        windowThemeIndex[1]++;
                        if(windowThemeIndex[1] >= themes.size()) {
                            windowThemeIndex[1] = 0;
                        }
                        String themeName = themes.get(windowThemeIndex[1]);
                        window2.setTheme(LanternaThemes.getRegisteredTheme(themeName));
                        window2.setTitle("Theme: " + themeName);
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
