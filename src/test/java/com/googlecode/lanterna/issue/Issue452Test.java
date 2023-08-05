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

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.*;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.TextBox.Style;
import com.googlecode.lanterna.gui2.table.*;
import com.googlecode.lanterna.input.*;
import com.googlecode.lanterna.screen.*;
import com.googlecode.lanterna.terminal.*;
import com.googlecode.lanterna.terminal.virtual.DefaultVirtualTerminal;


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

    /**
        some tests need to have the component rendered in to get sizing
    */
    void displayForRenderering(Component component) throws Exception {
        // xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
        // making use of the drawing routines in the renderer to know the size this thing is
        Terminal terminal = new DefaultVirtualTerminal(new TerminalSize(100, 100));
        TerminalScreen screen = new TerminalScreen(terminal);
        screen.startScreen();
        WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);
        Window window = new BasicWindow("needing to get the table drawn");
        window.setComponent(component);
        textGUI.addWindow(window);
        textGUI.updateScreen();
        // xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    }

    @Test
    public void testSingleLineTextBox() throws Exception {
        TextBox singleLine = new TextBox("123456789");
        content.addComponent(singleLine, LAYOUT_NEW_ROW);
        displayForRenderering(singleLine);

        // Focus component
        clickOn(singleLine);
        assertTrue(singleLine.isFocused());
        // Click at 3rd position
        clickOnWithRelative(singleLine, 3, 0);
        singleLine.handleInput(new KeyStroke(KeyType.BACKSPACE));
        // 3rd position (3) should be deleted
        assertEquals("12456789", singleLine.getText());
    }

    @Test
    public void testMultuLineTextBox() throws Exception {
        TextBox multiLine = new TextBox("123456789\nabcdefgh", Style.MULTI_LINE);
        content.addComponent(multiLine, LAYOUT_NEW_ROW);
        displayForRenderering(multiLine);

        // Focus component
        clickOn(multiLine);
        assertTrue(multiLine.isFocused());
        // Click at 3rd position 1st row
        clickOnWithRelative(multiLine, 3, 0);
        multiLine.handleInput(new KeyStroke(KeyType.BACKSPACE));
        // 3rd position (3) should be deleted
        assertEquals("12456789\nabcdefgh", multiLine.getText());
        // Click at 5th position 2nd row
        clickOnWithRelative(multiLine, 5, 1);
        multiLine.handleInput(new KeyStroke(KeyType.BACKSPACE));
        // 5th position (e) should be deleted
        assertEquals("12456789\nabcdfgh", multiLine.getText());
    }

    @Test
    public void testCheckBox() {
        CheckBox checkBox = new CheckBox("Checkbox");
        content.addComponent(checkBox, LAYOUT_NEW_ROW);
        assertFalse(checkBox.isFocused());
        assertFalse(checkBox.isChecked());
        // First click should focus the checkbox and item should be selected
        clickOn(checkBox);
        assertTrue(checkBox.isFocused());
        assertTrue(checkBox.isChecked());
        // Second click, focus should remain and item should be unselected
        clickOn(checkBox);
        assertTrue(checkBox.isFocused());
        assertFalse(checkBox.isChecked());
        // Third click should change its value back to TRUE
        clickOn(checkBox);
        assertTrue(checkBox.isFocused());
        assertTrue(checkBox.isChecked());
    }

    @Test
    public void testButton() {
        Button button = new Button("Button", createRunnable("Button"));
        content.addComponent(button, LAYOUT_NEW_ROW);
        assertFalse(button.isFocused());
        try {
            clickOn(button);
            fail();
        } catch (RunnableExecuted e) {
            assertEquals("Button", e.getName());
        }
    }

    @Test
    public void testActionListBox() {
        ActionListBox listBox = new ActionListBox();
        listBox.addItem(createRunnable("item_1"));
        listBox.addItem(createRunnable("item_2"));
        listBox.addItem(createRunnable("item_3"));
        listBox.addItem(createRunnable("item_4"));
        content.addComponent(listBox, LAYOUT_NEW_ROW);
        
        // First index is selected at the beginning so no need to focus it
        assertEquals(0, listBox.getSelectedIndex());
        
        try {
            listBox.handleInput(clickAt(0, 1));
            fail();
        } catch (RunnableExecuted e) {
            assertEquals("item_2", e.getName());
            assertEquals(1, listBox.getSelectedIndex());
        }
        
        try {
            listBox.handleInput(clickAt(0, 2));
            fail();
        } catch (RunnableExecuted e) {
            assertEquals("item_3", e.getName());
            assertEquals(2, listBox.getSelectedIndex());
        }
        
        try {
            listBox.handleInput(clickAt(0, 3));
            fail();
        } catch (RunnableExecuted e) {
            assertEquals("item_4", e.getName());
            assertEquals(3, listBox.getSelectedIndex());
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
        // second radio should be selected and item should be checked
        assertEquals("RadioGogo", list.getSelectedItem());
        assertEquals("RadioGogo", list.getCheckedItem());

        list.handleInput(clickAt(0, 2));
        // third radio should be selected and item should be checked
        assertEquals("RadioBlaBla", list.getSelectedItem());
        assertEquals("RadioBlaBla", list.getCheckedItem());
    }

    @Test
    public void testTable() throws Exception {
        Table<String> table = new Table<>("Column0000000", "Column111", "Column22222");
        table.setTheme(new SimpleTheme(TextColor.ANSI.WHITE, TextColor.ANSI.BLACK));
        table.getTableModel().addRow("0", "0", "0");
        table.getTableModel().addRow("1", "1", "1");
        table.getTableModel().addRow("2", "2", "2");
        table.setSelectAction(createRunnable("Table"));
        table.setCellSelection(true);
        content.addComponent(table, LAYOUT_NEW_ROW);
        displayForRenderering(table);

        assertTrue(table.isFocused());
        assertEquals(0, table.getSelectedColumn());
        assertEquals(0, table.getSelectedRow());

        // 0, 0 would get activated by first click so just to be able to run same method
        // for all indices sets position to 1, 1
        table.setSelectedColumn(1);
        table.setSelectedRow(1);
        
        // ease to set debugger breakpoints
        assertTablePositionSelectedAndExecuted(table, 0, 0);
        assertTablePositionSelectedAndExecuted(table, 1, 0);
        assertTablePositionSelectedAndExecuted(table, 2, 0);
        
        assertTablePositionSelectedAndExecuted(table, 0, 1);
        assertTablePositionSelectedAndExecuted(table, 1, 1);
        assertTablePositionSelectedAndExecuted(table, 2, 1);
        
        assertTablePositionSelectedAndExecuted(table, 0, 2);
        assertTablePositionSelectedAndExecuted(table, 1, 2);
        assertTablePositionSelectedAndExecuted(table, 2, 2);
    }

    /**
     * Every click will have +1 on row because first row is column headers, columns
     * will have + length of previous column header lenghts because they span
     * multiple columns
     */
    private void assertTablePositionSelectedAndExecuted(Table<String> table, int positionColumn, int positionRow) {
        int headerPadding = 1; // should get this from renderer somehow...?
        
        int previousColumnsWidth = 0;
        for (int i = 0; i < positionColumn; i++) {
            previousColumnsWidth += table.getTableModel().getColumnLabel(i).length();
            previousColumnsWidth += headerPadding;
        }
        
        try {
            clickOnWithRelative(table, previousColumnsWidth, positionRow + 1);
            System.out.println("--- Should not be here, should have thrown and caught exception from prior method call to clickOnWithRelative( table, positionColumn: " + positionColumn + ", x: " + previousColumnsWidth + ", y: " + (positionRow +1) +")");
            fail();
        } catch (RunnableExecuted e) {
            assertEquals("Table", e.getName());
            assertEquals("column:"+positionColumn, "column:"+table.getSelectedColumn());
            assertEquals("row:"+positionRow, "row:"+table.getSelectedRow());
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
        MouseAction mouseAction = clickAt(component.getGlobalPosition().getColumn() + column, component.getGlobalPosition().getRow() + row);
        component.handleInput(mouseAction);
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