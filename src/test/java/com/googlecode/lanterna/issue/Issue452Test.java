/*
 * This file is part of lanterna (https://github.com/mabe02/lanterna).
 *
 * lanterna is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2010-2020 Martin Berglund
 */
package com.googlecode.lanterna.issue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.gui2.ActionListBox;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.CheckBox;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Interactable;
import com.googlecode.lanterna.gui2.LayoutData;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.RadioBoxList;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.TextBox.Style;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.input.MouseAction;
import com.googlecode.lanterna.input.MouseActionType;
import com.googlecode.lanterna.screen.TerminalScreen;

/**
 * Automatic tests for mouse support. These test do not actually lauch
 * {@link TerminalScreen} as testing that is not the point. Point is to test how
 * different {@link Interactable}s handle mouse clicks.
 */
public class Issue452Test {

    private Panel content;
    private Window window;
    private static final int GRID_WIDTH = 100;
    private static final LayoutData LAYOUT_NEW_ROW = GridLayout.createHorizontallyFilledLayoutData(GRID_WIDTH);

    @Before
    public void before() {
        window = new BasicWindow("Issue452Test");
        content = new Panel(new GridLayout(GRID_WIDTH));
        GridLayout gridLayout = (GridLayout) content.getLayoutManager();
        gridLayout.setVerticalSpacing(1);
        window.setPosition(TerminalPosition.TOP_LEFT_CORNER);
        window.setComponent(content);
    }

    @Test
    public void testSingleLineTextBox() {
        TextBox singleLine = new TextBox("123456789");
        content.addComponent(singleLine, LAYOUT_NEW_ROW);
        // Focus component
        clickOn(singleLine);
        assertTrue(singleLine.isFocused());
        // Click at 3rd position
        singleLine.handleInput(clickAt(3, 0));
        singleLine.handleInput(new KeyStroke(KeyType.Backspace));
        // 3rd position (3) should be deleted
        assertEquals("12456789", singleLine.getText());
    }

    @Test
    public void testMultuLineTextBox() {
        TextBox multiLine = new TextBox("123456789\nabcdefgh", Style.MULTI_LINE);
        content.addComponent(multiLine, LAYOUT_NEW_ROW);
        // Focus component
        clickOn(multiLine);
        assertTrue(multiLine.isFocused());
        // Click at 3rd position 1st row
        multiLine.handleInput(clickAt(3, 0));
        multiLine.handleInput(new KeyStroke(KeyType.Backspace));
        // 3rd position (3) should be deleted
        assertEquals("12456789\nabcdefgh", multiLine.getText());
        // Click at 5th position 2nd row
        multiLine.handleInput(clickAt(5, 1));
        multiLine.handleInput(new KeyStroke(KeyType.Backspace));
        // 5th position (e) should be deleted
        assertEquals("12456789\nabcdfgh", multiLine.getText());
    }

    @Test
    public void testCheckBox() {
        CheckBox checkBox = new CheckBox("Checkbox");
        content.addComponent(checkBox, LAYOUT_NEW_ROW);
        assertFalse(checkBox.isFocused());
        assertFalse(checkBox.isChecked());
        // First click should only focus the checkbox
        clickOn(checkBox);
        assertTrue(checkBox.isFocused());
        assertFalse(checkBox.isChecked());
        // Second click should change its value to TRUE
        clickOn(checkBox);
        assertTrue(checkBox.isFocused());
        assertTrue(checkBox.isChecked());
        // Third click should change its value back to FALSE
        clickOn(checkBox);
        assertTrue(checkBox.isFocused());
        assertFalse(checkBox.isChecked());
    }

    @Test
    public void testButton() {
        Button button = new Button("Button", createRunnable("Button"));
        content.addComponent(button, LAYOUT_NEW_ROW);
        assertFalse(button.isFocused());
        // First click should only focus the button
        clickOn(button);
        try {
            clickOn(button);
            fail();
        } catch (RunnableExecuted e) {
            assertEquals("Button", e.getName());
        }
    }

    @Test
    public void testActionListBox() {
        ActionListBox menu = new ActionListBox();
        menu.addItem(createRunnable("Menu1"));
        menu.addItem(createRunnable("Menu2"));
        menu.addItem(createRunnable("Menu3"));
        menu.addItem(createRunnable("Menu4"));
        content.addComponent(menu, LAYOUT_NEW_ROW);
        clickOn(menu);
        // First index is selected at the beginning so no need to focus it
        assertEquals(0, menu.getSelectedIndex());
        try {
            menu.handleInput(clickAt(0, 0));
            fail();
        } catch (RunnableExecuted e) {
            assertEquals("Menu1", e.getName());
        }
        // First click on second menu to focus
        menu.handleInput(clickAt(0, 1));
        assertEquals(1, menu.getSelectedIndex());
        try {
            menu.handleInput(clickAt(0, 1));
            fail();
        } catch (RunnableExecuted e) {
            assertEquals("Menu2", e.getName());
        }
        // First click on third menu to focus
        menu.handleInput(clickAt(0, 2));
        assertEquals(2, menu.getSelectedIndex());
        try {
            menu.handleInput(clickAt(0, 2));
            fail();
        } catch (RunnableExecuted e) {
            assertEquals("Menu3", e.getName());
        }
        // First click on forth menu to focus
        menu.handleInput(clickAt(0, 3));
        assertEquals(3, menu.getSelectedIndex());
        try {
            menu.handleInput(clickAt(0, 3));
            fail();
        } catch (RunnableExecuted e) {
            assertEquals("Menu4", e.getName());
        }
    }

    @Test
    public void testRadioBoxList() {
        RadioBoxList<String> list = new RadioBoxList<>();
        list.addItem("RadioGaga");
        list.addItem("RadioGogo");
        list.addItem("RadioBlaBla");
        content.addComponent(list, LAYOUT_NEW_ROW);
        // At start, first radio is selected but nothing should be checked
        assertEquals("RadioGaga", list.getSelectedItem());
        assertEquals(null, list.getCheckedItem());

        list.handleInput(clickAt(0, 1));
        // second radio should be selected but still nothing should be checked
        assertEquals("RadioGogo", list.getSelectedItem());
        assertEquals(null, list.getCheckedItem());

        list.handleInput(clickAt(0, 2));
        // third radio should be selected but still nothing should be checked
        assertEquals("RadioBlaBla", list.getSelectedItem());
        assertEquals(null, list.getCheckedItem());

        list.handleInput(clickAt(0, 2));
        // second click on third radio so it should now be selected as well as checked
        assertEquals("RadioBlaBla", list.getSelectedItem());
        assertEquals("RadioBlaBla", list.getCheckedItem());

        list.handleInput(clickAt(0, 0));
        // first click on first radio so it should now be selected , but third radio
        // still should be checked
        assertEquals("RadioGaga", list.getSelectedItem());
        assertEquals("RadioBlaBla", list.getCheckedItem());

        list.handleInput(clickAt(0, 0));
        // second click on first radio so it should now be selected as well as checked
        assertEquals("RadioGaga", list.getSelectedItem());
        assertEquals("RadioGaga", list.getCheckedItem());
    }

    @Test
    public void testTable() {
        Table<String> table = new Table<>("Column0000000", "Column111", "Column22222");
        table.getTableModel().addRow("0", "0", "0");
        table.getTableModel().addRow("1", "1", "1");
        table.getTableModel().addRow("2", "2", "2");
        table.setSelectAction(createRunnable("Table"));
        table.setCellSelection(true);
        content.addComponent(table, LAYOUT_NEW_ROW);

        assertFalse(table.isFocused());
        clickOn(table);
        // after first click table should be focused and first column a first row should
        // be selected
        assertTrue(table.isFocused());
        assertEquals(0, table.getSelectedColumn());
        assertEquals(0, table.getSelectedRow());

        // 0, 0 would get activated by first click so just to be able to run same method
        // for all indices sets position to 1, 1
        table.setSelectedColumn(1);
        table.setSelectedRow(1);
        for (int positionRow = 0; positionRow < table.getTableModel().getRowCount(); positionRow++) {
            for (int positionColumn = 0; positionColumn < table.getTableModel().getColumnCount(); positionColumn++) {
                assertTablePositionSelectedAndExecuted(table, positionRow, positionColumn);
            }
        }

    }

    /**
     * Every click will have +1 on row because first row is column headers, columns
     * will have + length of previous column header lenghts because they span
     * multiple columns
     */
    private void assertTablePositionSelectedAndExecuted(Table<String> table, int positionRow, int positionColumn) {
        int previousCollumnsWidth = 0;
        for (int i = 0; i < positionColumn; i++) {
            previousCollumnsWidth += table.getTableModel().getColumnLabel(i).length();
        }
        clickOnWithRelative(table, positionColumn + previousCollumnsWidth, positionRow + 1);
        assertEquals(positionColumn, table.getSelectedColumn());
        assertEquals(positionRow, table.getSelectedRow());
        try {
            clickOnWithRelative(table, positionColumn + previousCollumnsWidth, positionRow + 1);
            fail();
        } catch (RunnableExecuted e) {
            assertEquals("Table", e.getName());
            assertEquals(positionColumn, table.getSelectedColumn());
            assertEquals(positionRow, table.getSelectedRow());
        }
    }

    /**
     * Clicks at position
     */
    private MouseAction clickAt(int column, int row) {
        return new MouseAction(MouseActionType.CLICK_DOWN, 1, new TerminalPosition(column, row));
    }

    /**
     * Clicks at position of the {@link Interactable}
     */
    private void clickOn(Interactable component) {
        component.handleInput(clickAt(component.getPosition().getColumn(), component.getPosition().getRow()));
    }

    /**
     * Clicks at position of the {@link Interactable} with offset
     */
    private void clickOnWithRelative(Interactable component, int column, int row) {
        component.handleInput(
                clickAt(component.getPosition().getColumn() + column, component.getPosition().getRow() + row));
    }

    private Runnable createRunnable(String name) {
        return new Runnable() {
            @Override
            public void run() {
                // propagate that this runnable was executed
                throw new RunnableExecuted(name);
            }
        };
    }

    private class RunnableExecuted extends RuntimeException {

        private static final long serialVersionUID = 1L;
        private String name;

        public RunnableExecuted(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

}