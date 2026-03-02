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

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;

import java.io.IOException;

/**
 * Simple test for the different kinds of list boxes
 * @author Martin
 */
public class ListBoxTest extends TestBase {
    public static void main(String[] args) throws IOException, InterruptedException {
        new ListBoxTest().run(args);
    }

    /*
     ┌──ListBox test─────────────────────────────────────┐
     │┌─CheckBoxList─┐ ┌─RadioBoxList─┐ ┌─ActionListBox─┐│
     ││[ ] Item 1   ▲│ │< > Item 11  ▲│ │Item 20       ▲││
     ││[x] Item 2   █│ │< > Item 12  ▒│ │Item 21       ▒││
     ││[x] Item 3   █│ │< > Item 13  ▒│ │Item 22       ▒││
     ││[ ] Item 4   ▒│ │< > Item 14  ▒│ │Item 23       ▒││
     ││[x] Item 5   ▒│ │<o> Item 15  █│ │Item 24       ▒││
     ││[x] Item 6   ▒│ │< > Item 16  █│ │Item 25       ▒││
     ││[ ] Item 7   ▒│ │< > Item 17  ▒│ │Item 26       █││
     ││[ ] Item 8   ▒│ │< > Item 18  ▒│ │Item 27       █││
     ││[ ] Item 9   ▒│ │< > Item 19  ▒│ │Item 28       ▒││
     ││[ ] Item 10  ▼│ │< > Item 20  ▼│ │Item 29       ▼││
     │└──────────────┘ └──────────────┘ └───────────────┘│
     │<  OK  >                                           │
     └───────────────────────────────────────────────────┘
    */
    @Override
    public void init(WindowBasedTextGUI textGUI) {
        final BasicWindow window = new BasicWindow("ListBox test");

        Panel horizontalPanel = new Panel();
        horizontalPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

        TerminalSize size = new TerminalSize(14, 10);
        CheckBoxList<String> checkBoxList = new CheckBoxList<>(size);
        RadioBoxList<String> radioBoxList = new RadioBoxList<>(size);
        ActionListBox actionListBox = new ActionListBox(size);
        for(int i = 0; i < 30; i++) {
            final String itemText = "Item " + (i + 1);
            checkBoxList.addItem(itemText);
            radioBoxList.addItem(itemText);
            actionListBox.addItem(itemText, () -> log("Selected " + itemText));
        }
        horizontalPanel.addComponent(checkBoxList.withBorder(Borders.singleLine("CheckBoxList")));
        horizontalPanel.addComponent(radioBoxList.withBorder(Borders.singleLine("RadioBoxList")));
        horizontalPanel.addComponent(actionListBox.withBorder(Borders.singleLine("ActionListBox")));

        window.setComponent(
                Panels.vertical(
                        horizontalPanel,
                        new Button("OK", window::close).setAccelerator(new KeyStroke('o', false, true))));
        textGUI.addWindow(window);
    }
}
