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

import java.io.IOException;

/**
 * Simple test for the different kinds of list boxes
 * @author Martin
 */
public class ListBoxTest extends TestBase {
    public static void main(String[] args) throws IOException, InterruptedException {
        new ListBoxTest().run(args);
    }

    @Override
    public void init(WindowBasedTextGUI textGUI) {
        final BasicWindow window = new BasicWindow("ListBox test");

        Panel horizontalPanel = new Panel();
        horizontalPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

        TerminalSize size = new TerminalSize(14, 10);
        CheckBoxList<String> checkBoxList = new CheckBoxList<String>(size);
        RadioBoxList<String> radioBoxList = new RadioBoxList<String>(size);
        ActionListBox actionListBox = new ActionListBox(size);
        for(int i = 0; i < 30; i++) {
            final String itemText = "Item " + (i + 1);
            checkBoxList.addItem(itemText);
            radioBoxList.addItem(itemText);
            actionListBox.addItem(itemText, new Runnable() {
                @Override
                public void run() {
                    System.out.println("Selected " + itemText);
                }
            });
        }
        horizontalPanel.addComponent(checkBoxList.withBorder(Borders.singleLine("CheckBoxList")));
        horizontalPanel.addComponent(radioBoxList.withBorder(Borders.singleLine("RadioBoxList")));
        horizontalPanel.addComponent(actionListBox.withBorder(Borders.singleLine("ActionListBox")));

        window.setComponent(
                Panels.vertical(
                        horizontalPanel,
                        new Button("OK", new Runnable() {
                            @Override
                            public void run() {
                                window.close();
                            }
                        })));
        textGUI.addWindow(window);
    }
}