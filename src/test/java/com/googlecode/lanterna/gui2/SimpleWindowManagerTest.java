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

import com.googlecode.lanterna.TestUtils;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;

/**
 * Test/example class for various kinds of window manager behaviours
 * @author Martin
 */
public class SimpleWindowManagerTest extends TestBase {
    public static void main(String[] args) throws IOException, InterruptedException {
        new SimpleWindowManagerTest().run(args);
    }

    @Override
    public void init(final WindowBasedTextGUI textGUI) {
        final Window mainWindow = new BasicWindow("Choose test");
        Panel contentArea = new Panel();
        contentArea.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        contentArea.addComponent(new Button("Centered window", () -> textGUI.addWindow(new CenteredWindow())));
        contentArea.addComponent(new Button("Undecorated window", () -> textGUI.addWindow(new UndecoratedWindow())));
        contentArea.addComponent(new Button("Undecorated + Centered window", () -> textGUI.addWindow(new UndecoratedCenteredWindow())));
        contentArea.addComponent(new Button("Full-screen window", () -> textGUI.addWindow(new FullScreenWindow(true))));
        contentArea.addComponent(new Button("Undecorated + Full-screen window", () -> textGUI.addWindow(new FullScreenWindow(false))));
        contentArea.addComponent(new Button("Expanded window", () -> textGUI.addWindow(new ExpandedWindow(true))));
        contentArea.addComponent(new Button("Undecorated + Expanded window", () -> textGUI.addWindow(new ExpandedWindow(false))));
        contentArea.addComponent(new Button("Close", mainWindow::close));
        mainWindow.setComponent(contentArea);
        textGUI.addWindow(mainWindow);
    }

    private static class CenteredWindow extends TestWindow {
        CenteredWindow() {
            super("Centered window");
            setHints(Collections.singletonList(Hint.CENTERED));
        }
    }

    private static class UndecoratedWindow extends TestWindow {
        UndecoratedWindow() {
            super("Undecorated");
            setHints(Collections.singletonList(Hint.NO_DECORATIONS));
        }
    }

    private static class UndecoratedCenteredWindow extends TestWindow {

        UndecoratedCenteredWindow() {
            super("UndecoratedCentered");
            setHints(Arrays.asList(Hint.NO_DECORATIONS, Hint.CENTERED));
        }
    }

    private static class FullScreenWindow extends TestWindow {

        public FullScreenWindow(boolean decorations) {
            super("FullScreenWindow");

            Panel content = new Panel();
            content.setLayoutManager(new BorderLayout());
            TextBox textBox = new TextBox(TestUtils.downloadGPL(), TextBox.Style.MULTI_LINE);
            textBox.setLayoutData(BorderLayout.Location.CENTER);
            textBox.setReadOnly(true);
            content.addComponent(textBox);

            setComponent(content);

            setHints(decorations ? Collections.singletonList(Hint.FULL_SCREEN) : Arrays.asList(Hint.FULL_SCREEN, Hint.NO_DECORATIONS));
        }
    }

    private static class ExpandedWindow extends TestWindow {

        public ExpandedWindow(boolean decorations) {
            super("ExpandedWindow");

            Panel content = new Panel();
            content.setLayoutManager(new BorderLayout());
            TextBox textBox = new TextBox(TestUtils.downloadGPL(), TextBox.Style.MULTI_LINE);
            textBox.setLayoutData(BorderLayout.Location.CENTER);
            textBox.setReadOnly(true);
            content.addComponent(textBox);

            setComponent(content);

            setHints(decorations ? Collections.singletonList(Hint.EXPANDED) : Arrays.asList(Hint.EXPANDED, Hint.NO_DECORATIONS));
        }
    }

    private static class TestWindow extends BasicWindow {
        TestWindow(String title) {
            super(title);
            setComponent(new Button("Close", this::close));
            setCloseWindowWithEscape(true);
        }
    }
}
