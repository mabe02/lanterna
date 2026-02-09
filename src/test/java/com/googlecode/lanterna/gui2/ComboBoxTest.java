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
 * Copyright (C) 2010-2024 Martin Berglund
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;

import java.io.IOException;
import java.util.Arrays;
import java.util.TimeZone;
import java.util.regex.Pattern;

/*
    ┌──ComboBoxTest───────────────────────────────┐
    │┌─Read-only┬─┐ ┌─Editable─┬─┐ ┌─CJK───────┬─┐│
    ││Berlin    │▼│ │Berlin    │▼│ │维基百科人│▼││
    │└──────────┴─┘ └──────────┴─┘ └───────────┴─┘│
    │                                             │
    │┌─Modify Content──────────────────────────┐  │
    ││.................... < Add  >            │  │
    ││0................... <Set Selected Index>│  │
    ││.................... <Set Selected Item> │  │
    │└─────────────────────────────────────────┘  │
    │                                             │
    │┌─Large ComboBox──────────────────┬─┐        │
    ││Africa/Abidjan                   │▼│        │
    │└─────────────────────────────────┴─┘        │
    │                                             │
    ├─────────────────────────────────────────────┤
    │<  OK  >                                     │
    └─────────────────────────────────────────────┘
*/
public class ComboBoxTest extends TestBase {
    public static void main(String[] args) throws IOException, InterruptedException {
        new ComboBoxTest().run(args);
    }

    @Override
    public void init(final WindowBasedTextGUI textGUI) {
        final BasicWindow window = new BasicWindow("ComboBoxTest");
        Panel mainPanel = new Panel();

        final ComboBox<String> comboBoxReadOnly = new ComboBox<>();
        final ComboBox<String> comboBoxEditable = new ComboBox<String>().setReadOnly(false);
        final ComboBox<String> comboBoxCJK = new ComboBox<String>().setReadOnly(false);
        final ComboBox<String> comboBoxTimeZones = new ComboBox<String>().setReadOnly(true);

        for(String item: Arrays.asList("Berlin", "London", "Paris", "Stockholm", "Tokyo")) {
            comboBoxEditable.addItem(item);
            comboBoxReadOnly.addItem(item);
        }
        for(String id: TimeZone.getAvailableIDs()) {
            comboBoxTimeZones.addItem(id);
        }
        comboBoxCJK.addItem("维基百科人人可編輯的自由百科全書");
        comboBoxCJK.addItem("ウィキペディアは誰でも編集できるフリー百科事典です");
        comboBoxCJK.addItem("위키백과는 전 세계 여러 언어로 만들어 나가는 자유 백과사전으로, 누구나 참여하실 수 있습니다.");
        comboBoxCJK.addItem("This is a string without double-width characters");
        comboBoxCJK.setPreferredSize(new TerminalSize(13, 1));

        mainPanel.addComponent(Panels.horizontal(
                comboBoxReadOnly.withBorder(Borders.singleLine("Read-only")),
                comboBoxEditable.withBorder(Borders.singleLine("Editable")),
                comboBoxCJK.withBorder(Borders.singleLine("CJK"))));
        mainPanel.addComponent(new EmptySpace(TerminalSize.ONE));

        final TextBox textBoxNewItem = new TextBox(new TerminalSize(20, 1));
        Button buttonAddItem = new Button("Add", () -> {
            comboBoxEditable.addItem(textBoxNewItem.getText());
            comboBoxReadOnly.addItem(textBoxNewItem.getText());
            textBoxNewItem.setText("");
            window.setFocusedInteractable(textBoxNewItem);
        });
        final TextBox textBoxSetSelectedIndex = new TextBox(new TerminalSize(20, 1), "0");
        textBoxSetSelectedIndex.setValidationPattern(Pattern.compile("-?[0-9]+"));
        Button buttonSetSelectedIndex = new Button("Set Selected Index", () -> {
            try {
                comboBoxEditable.setSelectedIndex(Integer.parseInt(textBoxSetSelectedIndex.getText()));
                comboBoxReadOnly.setSelectedIndex(Integer.parseInt(textBoxSetSelectedIndex.getText()));
            }
            catch (Exception e) {
                MessageDialog.showMessageDialog(textGUI, e.getClass().getName(), e.getMessage(), MessageDialogButton.OK);
            }
        });
        final TextBox textBoxSetSelectedItem = new TextBox(new TerminalSize(20, 1));
        Button buttonSetSelectedItem = new Button("Set Selected Item", () -> {
            try {
                comboBoxEditable.setSelectedItem(textBoxSetSelectedItem.getText());
                comboBoxReadOnly.setSelectedItem(textBoxSetSelectedItem.getText());
            }
            catch (Exception e) {
                MessageDialog.showMessageDialog(textGUI, e.getClass().getName(), e.getMessage(), MessageDialogButton.OK);
            }
        });
        mainPanel.addComponent(
                Panels.vertical(
                        Panels.horizontal(textBoxNewItem, buttonAddItem),
                        Panels.horizontal(textBoxSetSelectedIndex, buttonSetSelectedIndex),
                        Panels.horizontal(textBoxSetSelectedItem, buttonSetSelectedItem))
                    .withBorder(Borders.singleLineBevel("Modify Content")));

        mainPanel.addComponent(new EmptySpace(TerminalSize.ONE));
        mainPanel.addComponent(comboBoxTimeZones.withBorder(Borders.singleLine("Large ComboBox")));
        mainPanel.addComponent(new EmptySpace(TerminalSize.ONE));
        mainPanel.addComponent(new Separator(Direction.HORIZONTAL).setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.FILL)));
        mainPanel.addComponent(new Button("OK", window::close));
        window.setComponent(mainPanel);
        textGUI.addWindow(window);
    }
}
