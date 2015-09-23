package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalSize;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by martin on 21/09/15.
 */
public class ComboBoxTest extends TestBase {
    public static void main(String[] args) throws IOException, InterruptedException {
        new ComboBoxTest().run(args);
    }

    @Override
    public void init(WindowBasedTextGUI textGUI) {
        final BasicWindow window = new BasicWindow("ComboBoxTest");
        Panel mainPanel = new Panel();

        final ComboBox<String> comboBoxReadOnly = new ComboBox<String>();
        final ComboBox<String> comboBoxEditable = new ComboBox<String>().setReadOnly(false);

        for(String item: Arrays.asList("Berlin", "London", "Paris", "Stockholm", "Tokyo")) {
            comboBoxEditable.addItem(item);
            comboBoxReadOnly.addItem(item);
        }

        mainPanel.addComponent(Panels.horizontal(
                comboBoxReadOnly.withBorder(Borders.singleLine("Read-only")),
                comboBoxEditable.withBorder(Borders.singleLine("Editable"))));
        mainPanel.addComponent(new EmptySpace(TerminalSize.ONE));

        final TextBox textBoxNewItem = new TextBox();
        Button buttonAddItem = new Button("Add", new Runnable() {
            @Override
            public void run() {
                comboBoxEditable.addItem(textBoxNewItem.getText());
                comboBoxReadOnly.addItem(textBoxNewItem.getText());
                textBoxNewItem.setText("");
                window.setFocusedInteractable(textBoxNewItem);
            }
        });
        mainPanel.addComponent(Panels.horizontal(textBoxNewItem, buttonAddItem));

        mainPanel.addComponent(new EmptySpace(TerminalSize.ONE));
        mainPanel.addComponent(new Separator(Direction.HORIZONTAL).setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill)));
        mainPanel.addComponent(new Button("OK", new Runnable() {
            @Override
            public void run() {
                window.close();
            }
        }));
        window.setComponent(mainPanel);
        textGUI.addWindow(window);
    }
}
