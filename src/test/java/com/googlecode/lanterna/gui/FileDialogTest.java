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
 * Copyright (C) 2010-2015 Martin
 */

package com.googlecode.lanterna.gui;

import com.googlecode.lanterna.gui.dialog.FileDialog;
import com.googlecode.lanterna.TestTerminalFactory;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author martin
 */
public class FileDialogTest {
    public static void main(String[] args) throws IOException {
        final GUIScreen guiScreen = new TestTerminalFactory(args).createGUIScreen();
        guiScreen.getScreen().startScreen();
        FileDialog.showOpenFileDialog(guiScreen, new File("."), "Open dialog sample");
        FileDialog.showSaveFileDialog(guiScreen, new File("."), "Save dialog sample");
        guiScreen.getScreen().stopScreen();
    }
}
