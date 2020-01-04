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
import com.googlecode.lanterna.bundle.LanternaThemes;
import com.googlecode.lanterna.graphics.SimpleTheme;
import com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.gui2.table.TableModel;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThemeTest extends TestBase {
    public static void main(String[] args) throws IOException, InterruptedException {
        new ThemeTest().run(args);
    }

    @Override
    public void init(final WindowBasedTextGUI textGUI) {
        final BasicWindow mainSelectionWindow = new BasicWindow("Theme Tests");
        ActionListBox mainSelector = new ActionListBox();
        mainSelector.addItem("Component test", () -> runComponentTest(textGUI));
        mainSelector.addItem("Multi-theme test", () -> runMultiThemeTest(textGUI));
        mainSelector.addItem("Make custom theme", () -> runCustomTheme(textGUI));
        mainSelector.addItem("Exit", mainSelectionWindow::close);
        mainSelectionWindow.setComponent(mainSelector);
        mainSelectionWindow.setHints(Collections.singletonList(Window.Hint.CENTERED));

        textGUI.addWindow(mainSelectionWindow);
    }

    private void runComponentTest(WindowBasedTextGUI textGUI) {
        final BasicWindow componentTestChooser = new BasicWindow("Component test");
        componentTestChooser.setHints(Collections.singletonList(Window.Hint.CENTERED));

        Panel mainPanel = new Panel();
        mainPanel.addComponent(new Label("Choose component:                     "));
        mainPanel.addComponent(new EmptySpace());
        ThemedComponentTestDialog[] componentTestDialogs = new ThemedComponentTestDialog[]{
                new ThemedComponentTestDialog(textGUI, "ActionListBox",
                        new ActionListBox(new TerminalSize(15, 5))
                                .addItem(new NullRunnable("Item #1"))
                                .addItem(new NullRunnable("Item #2"))
                                .addItem(new NullRunnable("Item #3"))
                                .addItem(new NullRunnable("Item #4"))
                                .addItem(new NullRunnable("Item #5"))
                                .addItem(new NullRunnable("Item #6"))
                                .addItem(new NullRunnable("Item #7"))
                                .addItem(new NullRunnable("Item #8"))),
                new ThemedComponentTestDialog(textGUI, "AnimatedLabel",
                        new AnimatedLabel("First Frame")
                                .addFrame("Second Frame")
                                .addFrame("Third Frame")
                                .addFrame("Last Frame")),
                new ThemedComponentTestDialog(textGUI, "Borders",
                        new Panel()
                                .setLayoutManager(new GridLayout(4))
                                .addComponent(new EmptySpace(new TerminalSize(4, 2)).withBorder(Borders.singleLine()))
                                .addComponent(new EmptySpace(new TerminalSize(4, 2)).withBorder(Borders.singleLineBevel()))
                                .addComponent(new EmptySpace(new TerminalSize(4, 2)).withBorder(Borders.doubleLine()))
                                .addComponent(new EmptySpace(new TerminalSize(4, 2)).withBorder(Borders.doubleLineBevel()))),
                new ThemedComponentTestDialog(textGUI, "Button",
                        new Button("This is a button")),
                new ThemedComponentTestDialog(textGUI, "CheckBox",
                        new CheckBox("This is a checkbox")),
                new ThemedComponentTestDialog(textGUI, "CheckBoxList",
                        new CheckBoxList<String>(new TerminalSize(15, 5))
                                .addItem("Item #1")
                                .addItem("Item #2")
                                .addItem("Item #3")
                                .addItem("Item #4")
                                .addItem("Item #5")
                                .addItem("Item #6")
                                .addItem("Item #7")
                                .addItem("Item #8")),
                new ThemedComponentTestDialog(textGUI, "ComboBox",
                        new Panel()
                                .addComponent(new ComboBox<>("Editable", "Item #2", "Item #3", "Item #4", "Item #5", "Item #6", "Item #7")
                                        .setReadOnly(false)
                                        .setPreferredSize(new TerminalSize(12, 1)))
                                .addComponent(new EmptySpace())
                                .addComponent(new ComboBox<>("Read-only", "Item #2", "Item #3", "Item #4", "Item #5", "Item #6", "Item #7")
                                        .setReadOnly(true)
                                        .setPreferredSize(new TerminalSize(12, 1)))),
                new ThemedComponentTestDialog(textGUI, "Label",
                        new Label("This is a label")),
                new ThemedComponentTestDialog(textGUI, "RadioBoxList",
                        new RadioBoxList<String>(new TerminalSize(15, 5))
                                .addItem("Item #1")
                                .addItem("Item #2")
                                .addItem("Item #3")
                                .addItem("Item #4")
                                .addItem("Item #5")
                                .addItem("Item #6")
                                .addItem("Item #7")
                                .addItem("Item #8")),
                new ThemedComponentTestDialog(textGUI, "ProgressBar",
                        new ProgressBar(0, 100, 24)
                                .setLabelFormat("%2.0f%%")
                                .setValue(26)),
                new ThemedComponentTestDialog(textGUI, "ScrollBar",
                        new Panel()
                                .setLayoutManager(new GridLayout(2))
                                .addComponent(new ScrollBar(Direction.HORIZONTAL).setPreferredSize(new TerminalSize(6, 1)))
                                .addComponent(new ScrollBar(Direction.VERTICAL).setPreferredSize(new TerminalSize(1, 6)))),
                new ThemedComponentTestDialog(textGUI, "Separator",
                        new Panel()
                                .setLayoutManager(new GridLayout(2))
                                .addComponent(new Separator(Direction.HORIZONTAL).setPreferredSize(new TerminalSize(6, 1)))
                                .addComponent(new Separator(Direction.VERTICAL).setPreferredSize(new TerminalSize(1, 6)))),
                new ThemedComponentTestDialog(textGUI, "Table",
                        new Table<String>("Column #1", "Column #2", "Column #3")
                                .setTableModel(
                                        new TableModel<String>("Column #1", "Column #2", "Column #3")
                                                .addRow("Row #1", "Row #1", "Row #1")
                                                .addRow("Row #2", "Row #2", "Row #2")
                                                .addRow("Row #3", "Row #3", "Row #3")
                                                .addRow("Row #4", "Row #4", "Row #4"))),
                new ThemedComponentTestDialog(textGUI, "TextBox",
                        new Panel()
                                .addComponent(
                                        Panels.horizontal(
                                                new TextBox("Single-line text box")
                                                        .setPreferredSize(new TerminalSize(15, 1)),
                                                new TextBox("Single-line read-only")
                                                        .setPreferredSize(new TerminalSize(15, 1))
                                                        .setReadOnly(true)))
                                .addComponent(new EmptySpace())
                                .addComponent(
                                        Panels.horizontal(
                                            new TextBox(new TerminalSize(15, 5), "Multi\nline\ntext\nbox\nHere is a very long line that doesn't fit")
                                                    .setVerticalFocusSwitching(false),
                                            new TextBox(new TerminalSize(15, 5), "Multi\nline\nread-only\ntext\nbox\n" +
                                                    "Here is a very long line that doesn't fit")
                                                    .setReadOnly(true))))
        };
        ActionListBox listBox = new ActionListBox(new TerminalSize(15, 7)).setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        for(ThemedComponentTestDialog themedComponentTestDialog: componentTestDialogs) {
            listBox.addItem(themedComponentTestDialog);
        }
        mainPanel.addComponent(listBox);
        mainPanel.addComponent(new EmptySpace());
        mainPanel.addComponent(new Button(LocalizedString.Close.toString(), componentTestChooser::close).setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.End)));

        componentTestChooser.setComponent(mainPanel);
        textGUI.addWindowAndWait(componentTestChooser);
    }

    private static class ThemedComponentTestDialog implements Runnable {
        private final WindowBasedTextGUI textGUI;
        private final String label;
        private final Component borderedComponent;
        private final Component embeddedComponent;

        public ThemedComponentTestDialog(WindowBasedTextGUI textGUI, String label, Component component) {
            this.textGUI = textGUI;
            this.label = label;


            Panel componentPanel = new Panel();
            componentPanel.setLayoutManager(new GridLayout(1)
                    .setBottomMarginSize(1)
                    .setTopMarginSize(1)
                    .setLeftMarginSize(2)
                    .setRightMarginSize(2));
            componentPanel.addComponent(component.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER)));

            this.embeddedComponent = component;
            this.borderedComponent = componentPanel.withBorder(Borders.singleLine(label));

            if(embeddedComponent instanceof AnimatedLabel) {
                ((AnimatedLabel)embeddedComponent).startAnimation(917);
            }
            else if(embeddedComponent instanceof ProgressBar) {
                Thread progressBarAdvanceTimer = new Thread(() -> {
                    ProgressBar progressBar = (ProgressBar)embeddedComponent;
                    while(true) {
                        try {
                            Thread.sleep(100);
                            if(progressBar.getValue() == progressBar.getMax()) {
                                Thread.sleep(1000);
                                progressBar.setValue(0);
                            }
                            if(progressBar.getValue() == 0) {
                                Thread.sleep(1000);
                            }
                            progressBar.setValue(progressBar.getValue() + 1);
                        }
                        catch(InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, "ProgressBar #" + System.identityHashCode(embeddedComponent));
                progressBarAdvanceTimer.setDaemon(true);
                progressBarAdvanceTimer.start();
            }
        }

        @Override
        public void run() {
            final BasicWindow componentWindow = new BasicWindow();
            componentWindow.setHints(Collections.singletonList(Window.Hint.CENTERED));
            componentWindow.setTitle("Themed Component");

            Panel mainPanel = new Panel();
            mainPanel.setLayoutManager(new GridLayout(2));
            mainPanel.addComponent(borderedComponent.setLayoutData(GridLayout.createHorizontallyFilledLayoutData(2)));

            ActionListBox actionListBox = new ActionListBox();
            for(final String themeName: LanternaThemes.getRegisteredThemes()) {
                actionListBox.addItem(themeName, () -> borderedComponent.setTheme(LanternaThemes.getRegisteredTheme(themeName)));
            }
            mainPanel.addComponent(actionListBox
                    .withBorder(Borders.doubleLine("Change theme:"))
                    .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER)));

            Button closeButton = new Button(LocalizedString.Close.toString(), componentWindow::close).setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.END, GridLayout.Alignment.END));
            mainPanel.addComponent(closeButton);

            componentWindow.setComponent(mainPanel);
            closeButton.takeFocus();
            textGUI.addWindowAndWait(componentWindow);
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private static class NullRunnable implements Runnable {
        private final String label;

        public NullRunnable(String label) {
            this.label = label;
        }

        @Override
        public void run() {
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private void runMultiThemeTest(final WindowBasedTextGUI textGUI) {
        final List<String> themes = new ArrayList<>(LanternaThemes.getRegisteredThemes());
        final int[] windowThemeIndex = new int[] { themes.indexOf("bigsnake"), themes.indexOf("conqueror") };
        final BasicWindow window1 = new BasicWindow("Theme: bigsnake");
        window1.setHints(Collections.singletonList(Window.Hint.FIXED_POSITION));
        window1.setTheme(LanternaThemes.getRegisteredTheme(themes.get(windowThemeIndex[0])));
        window1.setPosition(new TerminalPosition(2, 1));

        final BasicWindow window2 = new BasicWindow("Theme: conqueror");
        window2.setHints(Collections.singletonList(Window.Hint.FIXED_POSITION));
        window2.setTheme(LanternaThemes.getRegisteredTheme(themes.get(windowThemeIndex[1])));
        window2.setPosition(new TerminalPosition(30, 1));

        final Panel leftHolder = new Panel().setPreferredSize(new TerminalSize(15, 4));
        final Panel rightHolder = new Panel().setPreferredSize(new TerminalSize(15, 4));
        GridLayout layoutManager = new GridLayout(1);
        leftHolder.setLayoutManager(layoutManager);
        rightHolder.setLayoutManager(layoutManager);

        final Button exampleButton = new Button("Example");
        exampleButton.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER, true, true));
        exampleButton.setEnabled(false);
        leftHolder.addComponent(exampleButton);

        ActionListBox leftWindowActionBox = new ActionListBox()
                .addItem("Move button to right", () -> rightHolder.addComponent(exampleButton))
                .addItem("Override button theme", () -> {
                    ActionListDialogBuilder actionListDialogBuilder = new ActionListDialogBuilder();
                    actionListDialogBuilder.setTitle("Choose theme for the button");
                    for(final String theme: themes) {
                        actionListDialogBuilder.addAction(theme, () -> exampleButton.setTheme(LanternaThemes.getRegisteredTheme(theme)));
                    }
                    actionListDialogBuilder.addAction("Clear override", () -> exampleButton.setTheme(null));
                    actionListDialogBuilder.build().showDialog(textGUI);
                })
                .addItem("Cycle window theme", () -> {
                    windowThemeIndex[0]++;
                    if(windowThemeIndex[0] >= themes.size()) {
                        windowThemeIndex[0] = 0;
                    }
                    String themeName = themes.get(windowThemeIndex[0]);
                    window1.setTheme(LanternaThemes.getRegisteredTheme(themeName));
                    window1.setTitle("Theme: " + themeName);
                })
                .addItem("Switch active window", () -> textGUI.setActiveWindow(window2))
                .addItem("Exit", () -> {
                    window1.close();
                    window2.close();
                });
        window1.setComponent(
                Panels.vertical(
                    leftHolder.withBorder(Borders.singleLine()),
                    leftWindowActionBox));
        window1.addWindowListener(new WindowListenerAdapter() {
            @Override
            public void onInput(Window basePane, KeyStroke keyStroke, AtomicBoolean deliverEvent) {
                if(keyStroke.getKeyType() == KeyType.Tab ||
                        keyStroke.getKeyType() == KeyType.ReverseTab) {
                    textGUI.setActiveWindow(window2);
                    deliverEvent.set(false);
                }
            }
        });

        ActionListBox rightWindowActionBox = new ActionListBox()
                .addItem("Move button to left", () -> leftHolder.addComponent(exampleButton))
                .addItem("Override button theme", () -> {
                    ActionListDialogBuilder actionListDialogBuilder = new ActionListDialogBuilder();
                    actionListDialogBuilder.setTitle("Choose theme for the button");
                    for(final String theme: themes) {
                        actionListDialogBuilder.addAction(theme, () -> exampleButton.setTheme(LanternaThemes.getRegisteredTheme(theme)));
                    }
                    actionListDialogBuilder.addAction("Clear override", () -> exampleButton.setTheme(null));
                    actionListDialogBuilder.build().showDialog(textGUI);
                })
                .addItem("Cycle window theme", () -> {
                    windowThemeIndex[1]++;
                    if(windowThemeIndex[1] >= themes.size()) {
                        windowThemeIndex[1] = 0;
                    }
                    String themeName = themes.get(windowThemeIndex[1]);
                    window2.setTheme(LanternaThemes.getRegisteredTheme(themeName));
                    window2.setTitle("Theme: " + themeName);
                })
                .addItem("Switch active window", () -> textGUI.setActiveWindow(window1))
                .addItem("Exit", () -> {
                    window1.close();
                    window2.close();
                });
        window2.setComponent(
                Panels.vertical(
                    rightHolder.withBorder(Borders.singleLine()),
                    rightWindowActionBox));
        window2.addWindowListener(new WindowListenerAdapter() {
            @Override
            public void onInput(Window basePane, KeyStroke keyStroke, AtomicBoolean deliverEvent) {
                if(keyStroke.getKeyType() == KeyType.Tab ||
                        keyStroke.getKeyType() == KeyType.ReverseTab) {
                    textGUI.setActiveWindow(window1);
                    deliverEvent.set(false);
                }
            }
        });

        window1.setFocusedInteractable(leftWindowActionBox);
        window2.setFocusedInteractable(rightWindowActionBox);

        textGUI.addWindow(window1);
        textGUI.addWindow(window2);
        textGUI.setActiveWindow(window1);
    }

    private void runCustomTheme(final WindowBasedTextGUI textGUI) {
        final BasicWindow customThemeCreator = new BasicWindow("Custom Theme");
        customThemeCreator.setHints(Collections.singletonList(Window.Hint.CENTERED));

        Panel mainPanel = new Panel();
        mainPanel.addComponent(new Label("Choose colors:"));

        Panel colorTable = new Panel(new GridLayout(2));
        colorTable.addComponent(new Label("Base foreground:"));
        final ComboBox<TextColor.ANSI> baseForeground = new ComboBox<>(TextColor.ANSI.values());
        colorTable.addComponent(baseForeground);
        colorTable.addComponent(new Label("Base background:"));
        final ComboBox<TextColor.ANSI> baseBackground = new ComboBox<>(TextColor.ANSI.values());
        baseBackground.setSelectedIndex(7);
        colorTable.addComponent(baseBackground);
        colorTable.addComponent(new Label("Editable foreground:"));
        final ComboBox<TextColor.ANSI> editableForeground = new ComboBox<>(TextColor.ANSI.values());
        editableForeground.setSelectedIndex(7);
        colorTable.addComponent(editableForeground);
        colorTable.addComponent(new Label("Editable background:"));
        final ComboBox<TextColor.ANSI> editableBackground = new ComboBox<>(TextColor.ANSI.values());
        editableBackground.setSelectedIndex(4);
        colorTable.addComponent(editableBackground);
        colorTable.addComponent(new Label("Selected foreground:"));
        final ComboBox<TextColor.ANSI> selectedForeground = new ComboBox<>(TextColor.ANSI.values());
        selectedForeground.setSelectedIndex(7);
        colorTable.addComponent(selectedForeground);
        colorTable.addComponent(new Label("Selected background:"));
        final ComboBox<TextColor.ANSI> selectedBackground = new ComboBox<>(TextColor.ANSI.values());
        selectedBackground.setSelectedIndex(4);
        colorTable.addComponent(selectedBackground);
        colorTable.addComponent(new Label("GUI background:"));
        final ComboBox<TextColor.ANSI> guiBackground = new ComboBox<>(TextColor.ANSI.values());
        guiBackground.setSelectedIndex(4);
        colorTable.addComponent(guiBackground);
        final CheckBox activeIsBoxCheck = new CheckBox("Active content is bold").setChecked(true);

        mainPanel.addComponent(new EmptySpace());
        mainPanel.addComponent(colorTable);
        mainPanel.addComponent(activeIsBoxCheck);
        mainPanel.addComponent(new EmptySpace());

        Button okButton = new Button(LocalizedString.OK.toString(), () -> {
            SimpleTheme theme = SimpleTheme.makeTheme(
                    activeIsBoxCheck.isChecked(),
                    baseForeground.getSelectedItem(),
                    baseBackground.getSelectedItem(),
                    editableForeground.getSelectedItem(),
                    editableBackground.getSelectedItem(),
                    selectedForeground.getSelectedItem(),
                    selectedBackground.getSelectedItem(),
                    guiBackground.getSelectedItem());
            textGUI.setTheme(theme);
            customThemeCreator.close();
        });
        Button cancelButton = new Button(LocalizedString.Cancel.toString(), customThemeCreator::close);
        mainPanel.addComponent(Panels.horizontal(
                okButton,
                cancelButton
        ).setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.End)));

        customThemeCreator.setComponent(mainPanel);
        okButton.takeFocus();
        textGUI.addWindowAndWait(customThemeCreator);
    }
}
