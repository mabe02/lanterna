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
 * Copyright (C) 2010-2014 Martin
 */
package com.googlecode.lanterna.gui;

import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.dialog.ListSelectDialog;
import com.googlecode.lanterna.gui.layout.LinearLayout;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TestTerminalFactory;
import java.io.IOException;

public class AlignmentTest {
    public static void main(String[] args) throws IOException {
        final GUIScreen guiScreen = new TestTerminalFactory(args).createGUIScreen();
        guiScreen.getScreen().startScreen();
        guiScreen.setBackgroundRenderer(new DefaultBackgroundRenderer("GUI Test"));

        final Window mainWindow = new Window("Alignment Test");
        final Label label = new Label("Aligned Label");
        label.setPreferredSize(new TerminalSize(40, 10));
        mainWindow.addComponent(label);
        Panel buttonPanel = new Panel(Panel.Orientation.HORIZONTAL);
        Button changeAlignmentButton = new Button("Change alignment", new Action() {
            @Override
            public void doAction() throws IOException {
                Component.Alignment a = ListSelectDialog.<Component.Alignment>showDialog(
                        guiScreen,
                        "Choose alignment",
                        "Please choose an alignment for the label above",
                        Component.Alignment.values());
                if(a != null)
                    label.setAlignment(a);
            }
        });
        changeAlignmentButton.setAlignment(Component.Alignment.RIGHT_CENTER);
        Button exitButton = new Button("Exit", new Action() {
            @Override
            public void doAction() {
                mainWindow.close();
            }
        });
        buttonPanel.addComponent(changeAlignmentButton, LinearLayout.GROWS_HORIZONTALLY);
        buttonPanel.addComponent(exitButton);
        mainWindow.addComponent(buttonPanel, LinearLayout.GROWS_HORIZONTALLY);

        guiScreen.showWindow(mainWindow, GUIScreen.Position.CENTER);
        guiScreen.getScreen().stopScreen();
    }
}
