package com.googlecode.lanterna.gui2.dialogs;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;

/**
 * Created by martin on 05/06/15.
 */
public class TextInputDialog extends DialogWindow {

    private final TextBox textBox;
    private String result;

    private TextInputDialog(
                String title,
                String description,
                TerminalSize textBoxPreferredSize,
                String initialContent,
                boolean password) {

        super(title);
        this.result = null;
        this.textBox = new TextBox(textBoxPreferredSize, initialContent);

        if(password) {
            textBox.setMask('*');
        }

        Panel mainPanel = new Panel();
        mainPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        Label descriptionLabel = new Label(description);

        Panel buttonPanel = new Panel();
        buttonPanel.setLayoutManager(new GridLayout(2));
        buttonPanel.addComponent(new Button("OK", new Runnable() {
            @Override
            public void run() {
                TextInputDialog.this.result = TextInputDialog.this.textBox.getText();
                TextInputDialog.this.close();
            }
        }).setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.END, GridLayout.Alignment.CENTER, true, false)));
        buttonPanel.addComponent(new Button("Cancel", new Runnable() {
            @Override
            public void run() {
                TextInputDialog.this.close();
            }
        }));

        mainPanel.addComponent(descriptionLabel);
        mainPanel.addComponent(new EmptySpace(TerminalSize.ONE));
        mainPanel.addComponent(textBox);
        mainPanel.addComponent(new EmptySpace(TerminalSize.ONE));
        mainPanel.addComponent(buttonPanel);
        setComponent(mainPanel);
    }

    public static String showDialog(WindowBasedTextGUI textGUI, String title, String description, String initialContent) {
        TextInputDialog textInputDialog = new TextInputDialog(title, description, null, initialContent, false);
        return showDialog(textGUI, textInputDialog);
    }

    public static String showPasswordDialog(WindowBasedTextGUI textGUI, String title, String description, String initialContent) {
        TextInputDialog textInputDialog = new TextInputDialog(title, description, null, initialContent, true);
        return showDialog(textGUI, textInputDialog);
    }

    private static String showDialog(WindowBasedTextGUI textGUI, TextInputDialog textInputDialog) {
        textGUI.addWindow(textInputDialog);

        //Wait for the window to close, in case the window manager doesn't honor the MODAL hint
        textInputDialog.waitUntilClosed();

        return textInputDialog.result;
    }
}
