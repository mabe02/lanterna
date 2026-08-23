package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.Window.Hint;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.gui2.table.TableModel;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.virtual.DefaultVirtualTerminal;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class TableUnitTests {

    private DefaultVirtualTerminal terminal;
    private MultiWindowTextGUI gui;
    private BasicWindow window;
    private Table<String> table;
    private TableModel<String> model;

    @Before
    public void setUp() throws IOException {
        TerminalSize size = new TerminalSize(30, 24);
        terminal = new DefaultVirtualTerminal(size);
        TerminalScreen screen = new TerminalScreen(terminal);
        screen.startScreen();
        DefaultWindowManager windowManager = new DefaultWindowManager(new EmptyWindowDecorationRenderer(), size);
        gui = new MultiWindowTextGUI(new SeparateTextGUIThread.Factory(), screen, windowManager, null, new EmptySpace());
        window = new BasicWindow();
        window.setHints(Arrays.asList(Hint.NO_DECORATIONS, Hint.FIT_TERMINAL_WINDOW, Hint.FULL_SCREEN));
        table = new Table<>("a", "b");
        window.setComponent(new Panel(new LinearLayout().setSpacing(0)).addComponent(table, LinearLayout.createLayoutData(LinearLayout.Alignment.FILL)));
        gui.addWindow(window);
        model = table.getTableModel();
    }

    @Test
    public void testSimpleTable() throws Exception {
        model.addRow("A1", "B1");
        assertScreenEquals("" +
                "a  b\n" +
                "A1 B1");
    }

    @Test
    public void testRendersVisibleRowsAndColumns() throws Exception {
        addRowsWithLongSecondColumn(4);
        assertScreenEquals("" +
                "a  b\n" +
                "A1                           ▲\n" +
                "A2                           █\n" +
                "A3                           ▼\n" +
                "◄█████████████▒▒▒▒▒▒▒▒▒▒▒▒▒▒►");
    }

    @Test
    public void testRendersVisibleRowsAndColumnsPartially() throws Exception {
        table.getRenderer().setAllowPartialColumn(true);
        addRowsWithLongSecondColumn(4);
        assertScreenEquals("" +
                "a  b\n" +
                "A1 BBBBBBBBBBBBBBBBBBBBBBBBBB▲\n" +
                "A2 BBBBBBBBBBBBBBBBBBBBBBBBBB█\n" +
                "A3 BBBBBBBBBBBBBBBBBBBBBBBBBB▼\n" +
                "◄█████████████▒▒▒▒▒▒▒▒▒▒▒▒▒▒►");
    }

    @Test
    public void testRendersVisibleRowsAndColumnsPartiallyWhenHorizontallyScrolled() throws Exception {
        model = new TableModel<>("x", "a", "b");
        table.setTableModel(this.model);
        table.getRenderer().setAllowPartialColumn(true);
        table.getRenderer().setViewLeftColumn(1);
        addRowsWithLongThirdColumn(4);
        assertScreenEquals("" +
                "a  b\n" +
                "A1 BBBBBBBBBBBBBBBBBBBBBBBBBB▲\n" +
                "A2 BBBBBBBBBBBBBBBBBBBBBBBBBB█\n" +
                "A3 BBBBBBBBBBBBBBBBBBBBBBBBBB▼\n" +
                "◄▒▒▒▒▒▒▒▒▒█████████▒▒▒▒▒▒▒▒▒►");
    }

    @Test
    public void testRendersVisibleRows() throws Exception {
        table.setVisibleRows(2);
        addFourRows();
        assertScreenEquals("" +
                "a  b\n" +
                "A1 B1                        ▲\n" +
                "A2 B2                        ▼");
    }

    @Test
    public void testRendersVisibleRowsAndColumnsWithRestrictedVerticalSpace() throws Exception {
        table.setVisibleRows(3);
        addRowsWithLongSecondColumn(4);
        assertScreenEquals("" +
                "a  b\n" +
                "A1                           ▲\n" +
                "A2                           ▼\n" +
                "◄█████████████▒▒▒▒▒▒▒▒▒▒▒▒▒▒►");
    }

    @Test
    public void testRendersVisibleRowsWithoutVerticalScrollBar() throws Exception {
        table.setVisibleRows(2);
        table.getRenderer().setScrollBarsHidden(true);
        addFourRows();
        assertScreenEquals("" +
                "a  b\n" +
                "A1 B1\n" +
                "A2 B2");
    }

    @Test
    public void testRendersVisibleColumnsWithoutHorizontalScrollBar() throws Exception {
        table.setVisibleRows(2);
        table.getRenderer().setScrollBarsHidden(true);
        addRowsWithLongSecondColumn(2);
        assertScreenEquals("" +
                "a  b\n" +
                "A1\n" +
                "A2");
    }

    @Test
    public void testRendersVisibleRowsAndColumnsWithoutHorizontalScrollBar() throws Exception {
        table.setVisibleRows(2);
        table.getRenderer().setScrollBarsHidden(true);
        addRowsWithLongSecondColumn(4);
        assertScreenEquals("" +
                "a  b\n" +
                "A1\n" +
                "A2");
    }

    @Test
    public void testRendersVisibleRowsWithSelection() throws Exception {
        table.setVisibleRows(2);
        addFourRows();
        table.setSelectedRow(1);
        assertScreenEquals("" +
                "a  b\n" +
                "A1 B1                        ▲\n" +
                "A2 B2                        ▼");
        table.setSelectedRow(2);
        assertScreenEquals("" +
                "a  b\n" +
                "A2 B2                        ▲\n" +
                "A3 B3                        ▼");
        table.setSelectedRow(3);
        assertScreenEquals("" +
                "a  b\n" +
                "A3 B3                        ▲\n" +
                "A4 B4                        ▼");
    }

    @Test
    public void testRendersVisibleRowsWithSelectionOffScreen() throws Exception {
        table.setVisibleRows(2);
        addFourRows();
        table.setSelectedRow(3);
        assertScreenEquals("" +
                "a  b\n" +
                "A3 B3                        ▲\n" +
                "A4 B4                        ▼");
    }

    @Test
    public void testRendersVisibleRowsWithSelectionBeyondRowCount() throws Exception {
        table.setVisibleRows(2);
        addFourRows();
        table.setSelectedRow(300);
        assertScreenEquals("" +
                "a  b\n" +
                "A3 B3                        ▲\n" +
                "A4 B4                        ▼");
    }

    @Test
    public void testRendersVisibleRowsAfterRemovingSelectedRow() throws Exception {
        table.setVisibleRows(2);
        addFourRows();
        table.setSelectedRow(3);
        model.removeRow(3);
        assertScreenEquals("" +
                "a  b\n" +
                "A2 B2                        ▲\n" +
                "A3 B3                        ▼");
    }

    @Test
    public void testRendersVisibleRowsAfterInsertingBeforeSelectedRow() throws Exception {
        table.setVisibleRows(2);
        addFourRows();
        table.setSelectedRow(2);
        assertScreenEquals("" +
                "a  b\n" +
                "A2 B2                        ▲\n" +
                "A3 B3                        ▼");
        model.insertRow(0, Arrays.asList("AX", "AX"));
        assertScreenEquals("" +
                "a  b\n" +
                "A2 B2                        ▲\n" +
                "A3 B3                        ▼");
    }

    @Test
    public void testRendersVisibleRowsAfterRemovingRowBeforeSelectedRow() throws Exception {
        table.setVisibleRows(2);
        addFourRows();
        table.setSelectedRow(3);
        model.removeRow(0);
        assertScreenEquals("" +
                "a  b\n" +
                "A3 B3                        ▲\n" +
                "A4 B4                        ▼");
    }

    // ---------------- END OF TESTS ----------------

    private void addFourRows() {
        model.addRow("A1", "B1");
        model.addRow("A2", "B2");
        model.addRow("A3", "B3");
        model.addRow("A4", "B4");
    }

    private void addRowsWithLongSecondColumn(int rows) {
        for (int i = 1; i <= rows; i++) {
            model.addRow("A" + i, "BBBBBBBBBBBBBBBBBBBBBBBBBBBBB"+i);
        }
    }

    private void addRowsWithLongThirdColumn(int rows) {
        for (int i = 1; i <= rows; i++) {
            model.addRow("X", "A" + i, "BBBBBBBBBBBBBBBBBBBBBBBBBBBBB"+i);
        }
    }

    private void assertScreenEquals(String expected) throws IOException {
        gui.updateScreen();
        assertEquals(expected, stripTrailingNewlines(terminal.toString()));
    }

    private String stripTrailingNewlines(String s) {
        return s.replaceAll("(?s)[\\s\n]+$", "");
    }
}
