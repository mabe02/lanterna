package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.dialogs.*;

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

        final Table table = new Table("Column 1", "Column 2", "Column3");
        table.addRow("Row1", "Row1", "Row1");
        table.addRow("Row2", "Row2", "Row2");
        table.addRow("Row3", "Row3", "Row3");
        table.setCellComponent(0, 0, new Button("Row1").setRenderer(new Button.FlatButtonRenderer()));
        table.setCellComponent(1, 0, new Button("Row2").setRenderer(new Button.FlatButtonRenderer()));
        table.setCellComponent(2, 0, new Button("Row3").setRenderer(new Button.FlatButtonRenderer()));

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
                table.withBorder(Borders.singleLineBevel("Table")),
                buttonPanel));
        textGUI.addWindow(window);
    }

    private void onModify(WindowBasedTextGUI textGUI, Table table) {
        String[] dialogChoices = new String[] {
                "Change table content",
                "Change table style"
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
    }

    private void onModifyContent(WindowBasedTextGUI textGUI, Table table) {
        String columnIndexAsText = askForANumber(textGUI, "Enter column # to modify (0-" + (table.getColumnCount() - 1) + ")");
        if(columnIndexAsText == null) {
            return;
        }
        String rowIndexAsText = askForANumber(textGUI, "Enter row # to modify (0-" + (table.getRowCount() - 1) + ")");
        if(rowIndexAsText == null) {
            return;
        }
        String[] componentTypes = new String[] {
                "Label",
                "Button",
                "Flat Button"
        };
        String choice = chooseAString(textGUI, "Which component to create at " + columnIndexAsText + "x" + rowIndexAsText + "?", componentTypes);
        if(choice == null) {
            return;
        }
        if(choice == componentTypes[0]) {
            String newLabel = askForAString(textGUI, "Enter new label for the table cell at row " + rowIndexAsText + " column " + columnIndexAsText);
            if(newLabel != null) {
                table.setCellComponent(Integer.parseInt(rowIndexAsText), Integer.parseInt(columnIndexAsText), new Label(newLabel));
            }
        }
        else if(choice == componentTypes[1]) {
            String newLabel = askForAString(textGUI, "Enter a label for the button at table cell row " + rowIndexAsText + " column " + columnIndexAsText);
            if(newLabel != null) {
                table.setCellComponent(Integer.parseInt(rowIndexAsText), Integer.parseInt(columnIndexAsText), new Button(newLabel));
            }
        }
        else if(choice == componentTypes[2]) {
            String newLabel = askForAString(textGUI, "Enter a label for the flat button at table cell row " + rowIndexAsText + " column " + columnIndexAsText);
            if(newLabel != null) {
                Button button = new Button(newLabel);
                button.setRenderer(new Button.FlatButtonRenderer());
                table.setCellComponent(Integer.parseInt(rowIndexAsText), Integer.parseInt(columnIndexAsText), button);
            }
        }
    }

    private void onModifyStyle(WindowBasedTextGUI textGUI, Table table) {
        String[] dialogChoices = new String[] {
                "Header border style (vertical)",
                "Header border style (horizontal)",
                "Cell border style (vertical)",
                "Cell border style (horizontal)",
                "Row Highlighting"
        };
        String choice = chooseAString(textGUI, "Which style do you want to change?", dialogChoices);
        Table.DefaultTableRenderer renderer = (Table.DefaultTableRenderer) table.getRenderer();
        if(choice == null) {
            return;
        }
        else if(choice == dialogChoices[4]) {
            MessageDialogButton resetAction = MessageDialog.showMessageDialog(
                    textGUI,
                    "Reset?",
                    "Do you want to reset current row highlighting setting?",
                    MessageDialogButton.Yes,
                    MessageDialogButton.No);
            if(resetAction == null) {
                return;
            }
            else if(resetAction == MessageDialogButton.Yes) {
                renderer.setSelectionOverlay(null, null, null);
            }
            else {
                TextColor.ANSI foreground = new ListSelectDialogBuilder<TextColor.ANSI>()
                        .setTitle("Choose highlight foreground color")
                        .addListItems(TextColor.ANSI.values())
                        .build()
                        .showDialog(textGUI);
                if(foreground == null) {
                    return;
                }
                TextColor.ANSI background = new ListSelectDialogBuilder<TextColor.ANSI>()
                        .setTitle("Choose highlight background color")
                        .addListItems(TextColor.ANSI.values())
                        .build()
                        .showDialog(textGUI);
                if(background == null) {
                    return;
                }
                renderer.setSelectionOverlay(foreground, background, EnumSet.noneOf(SGR.class));
            }
        }
        else {
            Table.TableCellBorderStyle newStyle = new ListSelectDialogBuilder<Table.TableCellBorderStyle>()
                    .setTitle("Choose a new style")
                    .addListItems(Table.TableCellBorderStyle.values())
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
        return new TextInputDialogBuilder()
                .setTitle(title)
                .setValidationPattern(Pattern.compile("[0-9]+"), "Not a number")
                .build()
                .showDialog(textGUI);
    }
}
