package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialogBuilder;

import java.io.IOException;
import java.util.ArrayList;
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

        final Table table = new Table("Column 1", "Column 2", "Column3");
        table.addRow("Row1", "Row1", "Row1");
        table.addRow("Row2", "Row2", "Row2");
        table.addRow("Row3", "Row3", "Row3");

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
                                for(int i = 0; i < table.getColumnCount(); i++) {
                                    labels.add("Row" + (table.getRowCount() + 1));
                                }
                                table.addRow(labels.toArray(new String[labels.size()]));
                            }
                        })
                        .addAction("Column", new Runnable() {
                            @Override
                            public void run() {
                                List<String> labels = new ArrayList<String>();
                                for(int i = 0; i < table.getRowCount(); i++) {
                                    labels.add("Row" + (i + 1));
                                }
                                table.addColumn("NewColumn", labels.toArray(new String[labels.size()]));
                            }
                        })
                        .build()
                        .showDialog(textGUI);
            }
        }));
        buttonPanel.addComponent(new Button("Modify...", new Runnable() {
            @Override
            public void run() {
                String columnIndexAsText = askForANumber(textGUI, "Enter column # to modify (0-" + (table.getColumnCount()-1) + ")");
                if(columnIndexAsText == null) {
                    return;
                }
                String rowIndexAsText = askForANumber(textGUI, "Enter row # to modify (0-" + (table.getRowCount()-1) + ")");
                if(rowIndexAsText == null) {
                    return;
                }
                String newLabel = askForAString(textGUI, "Enter new label for the table cell at row " + rowIndexAsText + " column " + columnIndexAsText);
                if(newLabel != null) {
                    table.setCellComponent(Integer.parseInt(rowIndexAsText), Integer.parseInt(columnIndexAsText), new Label(newLabel));
                }
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
                                String numberAsText = askForANumber(textGUI, "Enter row # to remove (0-" + (table.getRowCount()-1) + ")");
                                if(numberAsText != null) {
                                    table.removeRow(Integer.parseInt(numberAsText));
                                }
                            }
                        })
                        .addAction("Column", new Runnable() {
                            @Override
                            public void run() {
                                String numberAsText = askForANumber(textGUI, "Enter column # to remove (0-" + (table.getColumnCount()-1) + ")");
                                if(numberAsText != null) {
                                    table.removeColumn(Integer.parseInt(numberAsText));
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
                table.withBorder(Borders.singleLine("Table")),
                buttonPanel));
        textGUI.addWindow(window);
    }

    private String askForAString(WindowBasedTextGUI textGUI, String title) {
        return new TextInputDialogBuilder()
                .setTitle(title)
                .build()
                .showDialog(textGUI);
    }

    private String askForANumber(WindowBasedTextGUI textGUI, String title) {
        return new TextInputDialogBuilder()
                .setTitle(title)
                .setValidationPattern(Pattern.compile("[0-9]+"), "Not a number")
                .build()
                .showDialog(textGUI);
    }
}
