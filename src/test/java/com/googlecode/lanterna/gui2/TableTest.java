package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.dialogs.*;
import com.googlecode.lanterna.gui2.table.DefaultTableRenderer;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.gui2.table.TableCellBorderStyle;
import com.googlecode.lanterna.gui2.table.TableModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Test for the Table component
 */
public class TableTest extends TestBase {
    public static void main(String[] args) throws IOException, InterruptedException {
        new TableTest().run(args);
    }

    @Override
    public void init(final WindowBasedTextGUI textGUI) {
        final BasicWindow window = new BasicWindow("Table container test");

        final Table<String> table = new Table("Column 1", "Column 2", "Column3");
        final TableModel<String> model = table.getTableModel();
        model.addRow("Row1", "Row1", "Row1");
        model.addRow("Row2", "Row2", "Row2");
        model.addRow("Row3", "Row3", "Row3");

        Panel buttonPanel = new Panel();
        buttonPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

        buttonPanel.addComponent(new Button("Add...", new Runnable() {
            @Override
            public void run() {
                new ActionListDialogBuilder()
                        .setTitle("Add to table")
                        .addAction("Row", new Runnable() {
                            @Override
                            public void run() {
                                List<String> labels = new ArrayList<String>();
                                for(int i = 0; i < model.getColumnCount(); i++) {
                                    labels.add("Row" + (model.getRowCount() + 1));
                                }
                                model.addRow(labels.toArray(new String[labels.size()]));
                                table.invalidate();
                            }
                        })
                        .addAction("5 Rows", new Runnable() {
                            @Override
                            public void run() {
                                for(int row = 0; row < 5; row++) {
                                    List<String> labels = new ArrayList<String>();
                                    for(int i = 0; i < model.getColumnCount(); i++) {
                                        labels.add("Row" + (model.getRowCount() + 1));
                                    }
                                    model.addRow(labels.toArray(new String[labels.size()]));
                                }
                                table.invalidate();
                            }
                        })
                        .addAction("Column", new Runnable() {
                            @Override
                            public void run() {
                                List<String> labels = new ArrayList<String>();
                                for(int i = 0; i < model.getRowCount(); i++) {
                                    labels.add("Row" + (i + 1));
                                }
                                model.addColumn("NewColumn", labels.toArray(new String[labels.size()]));
                                table.invalidate();
                            }
                        })
                        .build()
                        .showDialog(textGUI);
            }
        }));
        buttonPanel.addComponent(new Button("Modify...", new Runnable() {
            @Override
            public void run() {
                onModify(textGUI, table);
            }
        }));
        buttonPanel.addComponent(new Button("Remove...", new Runnable() {
            @Override
            public void run() {
                new ActionListDialogBuilder()
                        .setTitle("Remove from table")
                        .addAction("Row", new Runnable() {
                            @Override
                            public void run() {
                                String numberAsText = askForANumber(textGUI, "Enter row # to remove (0-" + (model.getRowCount()-1) + ")");
                                if(numberAsText != null) {
                                    model.removeRow(Integer.parseInt(numberAsText));
                                }
                            }
                        })
                        .addAction("Column", new Runnable() {
                            @Override
                            public void run() {
                                String numberAsText = askForANumber(textGUI, "Enter column # to remove (0-" + (model.getColumnCount()-1) + ")");
                                if(numberAsText != null) {
                                    model.removeColumn(Integer.parseInt(numberAsText));
                                }
                            }
                        })
                        .build()
                        .showDialog(textGUI);
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
        textGUI.addWindow(window);
    }

    private void onModify(WindowBasedTextGUI textGUI, Table table) {
        String[] dialogChoices = new String[] {
                "Change table content",
                "Change table style",
                "Change view size"
        };
        String choice = chooseAString(textGUI, "Modify what?", dialogChoices);
        if(choice == null) {
            return;
        }
        else if(choice == dialogChoices[0]) {
            onModifyContent(textGUI, table);
        }
        else if(choice == dialogChoices[1]) {
            onModifyStyle(textGUI, table);
        }
        else if(choice == dialogChoices[2]) {
            onModifyViewSize(textGUI, table);
        }
    }

    private void onModifyContent(WindowBasedTextGUI textGUI, Table<String> table) {
        TableModel<String> model = table.getTableModel();
        String columnIndexAsText = askForANumber(textGUI, "Enter column # to modify (0-" + (model.getColumnCount() - 1) + ")");
        if(columnIndexAsText == null) {
            return;
        }
        String rowIndexAsText = askForANumber(textGUI, "Enter row # to modify (0-" + (model.getRowCount() - 1) + ")");
        if(rowIndexAsText == null) {
            return;
        }
        String newLabel = askForAString(textGUI, "Enter new label for the table cell at row " + rowIndexAsText + " column " + columnIndexAsText);
        if(newLabel != null) {
            model.setCell(Integer.parseInt(rowIndexAsText), Integer.parseInt(columnIndexAsText), newLabel);
        }
    }

    private void onModifyStyle(WindowBasedTextGUI textGUI, Table table) {
        String[] dialogChoices = new String[] {
                "Header border style (vertical)",
                "Header border style (horizontal)",
                "Cell border style (vertical)",
                "Cell border style (horizontal)",
                "Toggle cell selection"
        };
        String choice = chooseAString(textGUI, "Which style do you want to change?", dialogChoices);
        DefaultTableRenderer renderer = (DefaultTableRenderer) table.getRenderer();
        if(choice == null) {
            return;
        }
        else if(choice == dialogChoices[4]) {
            table.setCellSelection(!table.isCellSelection());
        }
        else {
            TableCellBorderStyle newStyle = new ListSelectDialogBuilder<TableCellBorderStyle>()
                    .setTitle("Choose a new style")
                    .addListItems(TableCellBorderStyle.values())
                    .build()
                    .showDialog(textGUI);
            if(newStyle != null) {
                if(choice == dialogChoices[0]) {
                    renderer.setHeaderVerticalBorderStyle(newStyle);
                }
                else if(choice == dialogChoices[1]) {
                    renderer.setHeaderHorizontalBorderStyle(newStyle);
                }
                else if(choice == dialogChoices[2]) {
                    renderer.setCellVerticalBorderStyle(newStyle);
                }
                else if(choice == dialogChoices[3]) {
                    renderer.setCellHorizontalBorderStyle(newStyle);
                }
            }
        }
        table.invalidate();
    }

    private void onModifyViewSize(WindowBasedTextGUI textGUI, Table table) {
        String viewSize = askForANumber(textGUI, "Enter number of rows to display at once");
        if(viewSize == null) {
            return;
        }
        table.setVisibleRows(Integer.parseInt(viewSize));
    }

    private String chooseAString(WindowBasedTextGUI textGUI, String title, String... items) {
        return new ListSelectDialogBuilder<String>()
                .setTitle(title)
                .addListItems(items)
                .build()
                .showDialog(textGUI);
    }

    private String askForAString(WindowBasedTextGUI textGUI, String title) {
        return new TextInputDialogBuilder()
                .setTitle(title)
                .build()
                .showDialog(textGUI);
    }

    private String askForANumber(WindowBasedTextGUI textGUI, String title) {
        return askForANumber(textGUI, title, "");
    }

    private String askForANumber(WindowBasedTextGUI textGUI, String title, String initialNumber) {
        return new TextInputDialogBuilder()
                .setTitle(title)
                .setInitialContent(initialNumber)
                .setValidationPattern(Pattern.compile("[0-9]+"), "Not a number")
                .build()
                .showDialog(textGUI);
    }
}
