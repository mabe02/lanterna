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
 * Copyright (C) 2010-2026 Martin Berglund
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.*;
import com.googlecode.lanterna.bundle.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.MouseCaptureMode;

import java.io.IOException;
import java.util.*;

/**
 * Some common code for the GUI tests to get a text system up and running on a separate thread
 * @author Martin
 */
public abstract class TestBase {

    MultiWindowTextGUI textGUI;
    TestHelperUiController testHelperUiController;

    /*
        ┌──testing helper─────────────────────────────────────────────────────────────────┐
        │┌─Themes────────────────┐ ┌─Log Messages────────────────────────────────────────┐│
        ││theme: default         │ │┌───────────────────────────────────────────────────┐││
        ││theme: defrost         │ ││...................................................│││
        ││theme: bigsnake        │ ││...................................................│││
        ││theme: conqueror       │ ││...................................................│││
        ││theme: businessmachine │ ││...................................................│││
        ││theme: blaster         │ ││...................................................│││
        │└───────────────────────┘ ││...................................................│││
        │                          ││...................................................│││
        │                          ││...................................................│││
        │                          ││...................................................│││
        │                          ││...................................................│││
        │                          │└───────────────────────────────────────────────────┘││
        │                          │                                        <CLEAR LOG>  ││
        │                          └─────────────────────────────────────────────────────┘│
        │< Exit >                                                                         │
        └─────────────────────────────────────────────────────────────────────────────────┘
    */
    public static class TestHelperUiController {
        MultiWindowTextGUI textGui;
        List<String> themeNames = new ArrayList(LanternaThemes.getRegisteredThemes());
        ActionListBox themesListBox = new ActionListBox();
        TextBox logTextBox;
        public TestHelperUiController(MultiWindowTextGUI textGui) {
            this.textGui = textGui;
        }
        /*
            ┌─Themes────────────────┐
            │theme: default         │
            │theme: defrost         │
            │theme: bigsnake        │
            │theme: conqueror       │
            │theme: businessmachine │
            │theme: blaster         │
            └───────────────────────┘
        */
        private Component makeThemeChangerComponent() {
            for (String name : themeNames) {
                themesListBox.addItem( "theme: " + name, () -> assignTheme(name));
            }
            return themesListBox.withBorder(Borders.singleLine("Themes"));
        }
        private void assignTheme(String themeName) {
            if (themeName == null) {
                themeName = "businessmachine";
            }
            Theme theme = LanternaThemes.getRegisteredTheme(themeName);
            Collection<Window> windows = textGui.getWindows();
            if (theme != null && windows != null) {
                for (Window w : windows) {
                    w.setTheme(theme);
                }
                textGui.setTheme(theme);
            }
            Integer index = themeNames.indexOf(themeName);
            if (index != null && themesListBox.getSelectedIndex() != index) {
                themesListBox.setSelectedIndex(index);
            }
        }
        /*
            ┌─Log Messages────────────────────────────────────────┐
            │┌───────────────────────────────────────────────────┐│
            ││...................................................││
            ││...................................................││
            ││...................................................││
            ││...................................................││
            ││...................................................││
            ││...................................................││
            ││...................................................││
            ││...................................................││
            ││...................................................││
            ││...................................................││
            │└───────────────────────────────────────────────────┘│
            │                                        <CLEAR LOG>  │
            └─────────────────────────────────────────────────────┘
        */
        private Component makeLogMessagesComponent() {
            logTextBox = new TextBox(new TerminalSize(80, 10));
            logTextBox.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.FILL));
            Button clearLogButton = new Button("CLEAR LOG", () -> logTextBox.setText(""));
            
            Panel ui = new Panel(new LinearLayout(Direction.VERTICAL));
            //ui.setPreferredSize(new TerminalSize(160, 40));
            ui.addComponent(logTextBox.withBorder(Borders.singleLine("")));
            
            Panel buttonPanel = new Panel();
            buttonPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
            Panel spacer = new Panel();
            spacer.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.FILL, LinearLayout.GrowPolicy.CAN_GROW));
            Panel spacer2 = new Panel();
            spacer2.setPreferredSize(new TerminalSize(1, 1));
            buttonPanel.addComponent(spacer);
            clearLogButton.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.END));
            buttonPanel.addComponent(clearLogButton);
            buttonPanel.addComponent(spacer2);
            buttonPanel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.FILL, LinearLayout.GrowPolicy.CAN_GROW));
            ui.addComponent(buttonPanel);
            
            return ui.withBorder(Borders.singleLine("Log Messages"));
        }
        public Window makeWindow(Screen screen) {
            final Window window = new BasicWindow("testing helper");
            Panel ui = Panels.horizontal(
                makeThemeChangerComponent(),
                makeLogMessagesComponent()
            );
            ui = Panels.vertical(
                ui,
                new Button("Exit", () -> exit(screen))
            );
            window.setComponent(ui);
            return window;
        }
        public void log(String message) {
            logAppendMax(10, message);
        }
        public void logAppendMax(int lineCount, String message) {
            TextBox log = logTextBox;
            try {
                while (log.getLineCount() >= lineCount) {
                    log.removeLine(0);
                }
            } finally {
                log.addLine(message);
                // unfortunately some methods expect (row, column), some (column, row)
                log.setCaretPosition(Integer.MAX_VALUE, log.getLineCount());
            }
        }
        public void exit(Screen screen) {
            try {
                screen.stopScreen();
                System.exit(0);
            } catch (IOException ex) {
                System.out.println("exception occurred when trying to stop screen: " + ex);
                System.exit(1);
            }
        }
    }

    void run(String[] args) throws IOException, InterruptedException {
        Screen screen = new TestTerminalFactory(args).setMouseCaptureMode(MouseCaptureMode.CLICK_AUTODETECT).createScreen();
        screen.startScreen();
        textGUI = createTextGUI(screen);
        textGUI.setBlockingIO(false);
        textGUI.setEOFWhenNoWindows(true);
        //noinspection ResultOfMethodCallIgnored
        textGUI.isEOFWhenNoWindows();   //No meaning, just to silence IntelliJ:s "is never used" alert

        try {
            testHelperUiController = new TestHelperUiController(textGUI);
            textGUI.addWindow(testHelperUiController.makeWindow(screen));
            init(textGUI);
            testHelperUiController.assignTheme(extractTheme(args));
            arrangeWindows();
            AsynchronousTextGUIThread guiThread = (AsynchronousTextGUIThread)textGUI.getGUIThread();
            guiThread.start();
            afterGUIThreadStarted(textGUI);
            guiThread.waitForStop();
        }
        finally {
            screen.stopScreen();
        }
    }

    private String extractTheme(String[] args) {
        for(int i = 0; i < args.length; i++) {
            if(args[i].equals("--theme") && i + 1 < args.length) {
                return args[i+1];
            }
        }
        return null;
    }

    protected MultiWindowTextGUI createTextGUI(Screen screen) {
        return new MultiWindowTextGUI(new SeparateTextGUIThread.Factory(), screen, new DefaultWindowManager());
    }

    public abstract void init(WindowBasedTextGUI textGUI);
    public void afterGUIThreadStarted(WindowBasedTextGUI textGUI) {
        // By default do nothing
    }


    public void arrangeWindows() {
        final int PAD = 4;
        int x = 1;
        int y = 1;
        for (Window w : textGUI.getWindows()) {
            TerminalSize size = w.getPreferredSize();
            w.setPosition(new TerminalPosition(x, y));
            w.setHints(Collections.singletonList(Window.Hint.FIXED_POSITION));
            x += PAD;
            y += size.getRows() + PAD;
        }
    }
    void log(String message) {
        testHelperUiController.log(message);
    }
}
