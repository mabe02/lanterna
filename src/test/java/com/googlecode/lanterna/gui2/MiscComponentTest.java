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

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TestUtils;

import java.io.*;

/**
 * Created by martin on 19/10/14.
 */
public class MiscComponentTest extends TestBase {
    public static void main(String[] args) throws IOException, InterruptedException {
        new MiscComponentTest().run(args);
    }

    @Override
    public void init(WindowBasedTextGUI textGUI) {
        final BasicWindow window = new BasicWindow("Grid layout test");

        final Panel leftPanel = new Panel();

        Panel checkBoxPanel = new Panel();
        for(int i = 0; i < 4; i++) {
            CheckBox checkBox = new CheckBox("Checkbox #" + (i+1));
            checkBoxPanel.addComponent(checkBox);
        }

        Panel textBoxPanel = new Panel();
        textBoxPanel.addComponent(Panels.horizontal(new Label("Normal:   "), new TextBox(new TerminalSize(12, 1), "Text")));
        textBoxPanel.addComponent(Panels.horizontal(new Label("Password: "), new TextBox(new TerminalSize(12, 1), "Text").setMask('*')));

        Panel buttonPanel = new Panel();
        buttonPanel.addComponent(new Button("Enable spacing", new Runnable() {
            @Override
            public void run() {
                LinearLayout layoutManager = (LinearLayout) leftPanel.getLayoutManager();
                layoutManager.setSpacing(layoutManager.getSpacing() == 0 ? 1 : 0);
            }
        }));

        leftPanel.addComponent(checkBoxPanel.withBorder(Borders.singleLine("CheckBoxes")));
        leftPanel.addComponent(textBoxPanel.withBorder(Borders.singleLine("TextBoxes")));
        leftPanel.addComponent(buttonPanel.withBorder(Borders.singleLine("Buttons")));

        Panel rightPanel = new Panel();
        textBoxPanel = new Panel();
        TextBox readOnlyTextArea = new TextBox(new TerminalSize(16, 8));
        readOnlyTextArea.setReadOnly(true);
        readOnlyTextArea.setText(TestUtils.downloadGPL());
        textBoxPanel.addComponent(readOnlyTextArea);
        rightPanel.addComponent(textBoxPanel.withBorder(Borders.singleLine("Read-only")));
        rightPanel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));

        Panel contentArea = new Panel();
        contentArea.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        contentArea.addComponent(Panels.horizontal(leftPanel, rightPanel));
        contentArea.addComponent(
                new Separator(Direction.HORIZONTAL).setLayoutData(
                        LinearLayout.createLayoutData(LinearLayout.Alignment.Fill)));
        contentArea.addComponent(
                new Button("OK", new Runnable() {
            @Override
            public void run() {
                window.close();
            }
        }).setLayoutData(
                        LinearLayout.createLayoutData(LinearLayout.Alignment.Center)));
        window.setComponent(contentArea);
        textGUI.addWindow(window);
    }
}
