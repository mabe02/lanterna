/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.lanterna.test.gui;

import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.dialog.FileDialog;
import com.googlecode.lanterna.test.TestTerminalFactory;
import java.io.File;

/**
 *
 * @author martin
 */
public class FileDialogTest {
    public static void main(String[] args) {
        final GUIScreen guiScreen = new TestTerminalFactory(args).createGUIScreen();
        guiScreen.getScreen().startScreen();
        FileDialog.showOpenFileDialog(guiScreen, new File("."), "Open dialog sample");
        FileDialog.showSaveFileDialog(guiScreen, new File("."), "Save dialog sample");
        guiScreen.getScreen().stopScreen();
    }
}
