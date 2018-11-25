package com.googlecode.lanterna.issue;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import com.googlecode.lanterna.gui2.table.DefaultTableRenderer;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.gui2.table.TableModel;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

public class Issue384 {
    private static final Set<Integer> EXPANDABLE_COLUMNS = new TreeSet<Integer>(Arrays.asList(1));

    public static void main(String[] args) throws IOException, InterruptedException {
        final Screen screen = new DefaultTerminalFactory().createScreen();
        screen.startScreen();
        final MultiWindowTextGUI textGUI = new MultiWindowTextGUI(screen);
        final Window window = new BasicWindow("Table container test");
        window.setHints(Arrays.asList(Window.Hint.FIXED_SIZE));
        window.setSize(new TerminalSize(60, 14));

        final Table<String> table = new Table<String>("Column", "Expanded Column", "Column");
        table.setCellSelection(true);
        table.setVisibleRows(10);
        final DefaultTableRenderer<String> tableRenderer = new DefaultTableRenderer<String>();
        tableRenderer.setExpandableColumns(Arrays.asList(1));
        table.setRenderer(tableRenderer);

        final TableModel<String> model = table.getTableModel();
        for(int i = 1; i <= 20; i++) {
            String cellLabel = "Row" + i;
            model.addRow(cellLabel, cellLabel, cellLabel);
        }

        Panel buttonPanel = new Panel();
        buttonPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        buttonPanel.addComponent(new Button("Change Expandable Columns", new Runnable() {
            @Override
            public void run() {
                showExpandableColumnsEditor(textGUI, tableRenderer);
            }
        }));
        buttonPanel.addComponent(new Button("Close", new Runnable() {
            @Override
            public void run() {
                window.close();
            }
        }));

        window.setComponent(Panels.vertical(
                table.withBorder(Borders.singleLineBevel("Table")),
                buttonPanel));
        table.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));
        textGUI.addWindow(window);
        textGUI.waitForWindowToClose(window);
        screen.stopScreen();
    }

    private static void showExpandableColumnsEditor(MultiWindowTextGUI textGUI, final DefaultTableRenderer<String> tableRenderer) {
        final DialogWindow dialogWindow = new DialogWindow("Select expandable columns") { };
        Panel contentPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        final CheckBoxList<String> checkBoxList = new CheckBoxList<String>();
        checkBoxList.addItem("Column1", EXPANDABLE_COLUMNS.contains(0));
        checkBoxList.addItem("Column2", EXPANDABLE_COLUMNS.contains(1));
        checkBoxList.addItem("Column3", EXPANDABLE_COLUMNS.contains(2));
        contentPanel.addComponent(checkBoxList);
        contentPanel.addComponent(new Button("OK", new Runnable() {
            @Override
            public void run() {
                EXPANDABLE_COLUMNS.clear();
                for(int i = 0; i < 3; i++) {
                    if (checkBoxList.isChecked(i)) {
                        EXPANDABLE_COLUMNS.add(i);
                    }
                }
                tableRenderer.setExpandableColumns(EXPANDABLE_COLUMNS);
                dialogWindow.close();
            }
        }), LinearLayout.createLayoutData(LinearLayout.Alignment.End));
        dialogWindow.setComponent(contentPanel);
        dialogWindow.showDialog(textGUI);
    }
}

