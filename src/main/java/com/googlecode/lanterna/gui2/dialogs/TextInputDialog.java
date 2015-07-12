package com.googlecode.lanterna.gui2.dialogs;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;

import java.math.BigInteger;
import java.util.regex.Pattern;

/**
 * TextInputDialog is a modal text input dialog that prompts the user to enter a text string. The class supports
 * validation and password masking. The builder class to help setup TextInputDialogs is TextInputDialogBuilder.
 */
public class TextInputDialog extends DialogWindow {

    private final TextBox textBox;
    private final TextInputDialogResultValidator validator;
    private String result;

    TextInputDialog(
                String title,
                String description,
                TerminalSize textBoxPreferredSize,
                String initialContent,
                TextInputDialogResultValidator validator,
                boolean password) {

        super(title);
        this.result = null;
        this.textBox = new TextBox(textBoxPreferredSize, initialContent);
        this.validator = validator;

        if(password) {
            textBox.setMask('*');
        }

        Panel buttonPanel = new Panel();
        buttonPanel.setLayoutManager(new GridLayout(2).setHorizontalSpacing(1));
        buttonPanel.addComponent(new Button("OK", new Runnable() {
            @Override
            public void run() {
                onOK();
            }
        }).setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER, true, false)));
        buttonPanel.addComponent(new Button("Cancel", new Runnable() {
            @Override
            public void run() {
                onCancel();
            }
        }));

        Panel mainPanel = new Panel();
        mainPanel.setLayoutManager(
                new GridLayout(1)
                        .setLeftMarginSize(1)
                        .setRightMarginSize(1));
        if(description != null) {
            mainPanel.addComponent(new Label(description));
        }
        mainPanel.addComponent(new EmptySpace(TerminalSize.ONE));
        textBox.setLayoutData(
                GridLayout.createLayoutData(
                        GridLayout.Alignment.FILL,
                        GridLayout.Alignment.CENTER,
                        true,
                        false))
                .addTo(mainPanel);
        mainPanel.addComponent(new EmptySpace(TerminalSize.ONE));
        buttonPanel.setLayoutData(
                GridLayout.createLayoutData(
                        GridLayout.Alignment.END,
                        GridLayout.Alignment.CENTER,
                        false,
                        false))
                .addTo(mainPanel);
        setComponent(mainPanel);
    }

    private void onOK() {
        String text = textBox.getText();
        if(validator != null) {
            String errorMessage = validator.validate(text);
            if(errorMessage != null) {
                MessageDialog.showMessageDialog(getTextGUI(), getTitle(), errorMessage, MessageDialogButton.OK);
                return;
            }
        }
        result = text;
        close();
    }

    private void onCancel() {
        close();
    }

    @Override
    public String showDialog(WindowBasedTextGUI textGUI) {
        result = null;
        super.showDialog(textGUI);
        return result;
    }


    public static String showDialog(WindowBasedTextGUI textGUI, String title, String description, String initialContent) {
        TextInputDialog textInputDialog = new TextInputDialogBuilder()
                .setTitle(title)
                .setDescription(description)
                .setInitialContent(initialContent)
                .build();
        return textInputDialog.showDialog(textGUI);
    }

    public static BigInteger showNumberDialog(WindowBasedTextGUI textGUI, String title, String description, String initialContent) {
        TextInputDialog textInputDialog = new TextInputDialogBuilder()
                .setTitle(title)
                .setDescription(description)
                .setInitialContent(initialContent)
                .setValidationPattern(Pattern.compile("[0-9]+"), "Not a number")
                .build();
        String numberString = textInputDialog.showDialog(textGUI);
        return numberString != null ? new BigInteger(numberString) : null;
    }

    public static String showPasswordDialog(WindowBasedTextGUI textGUI, String title, String description, String initialContent) {
        TextInputDialog textInputDialog = new TextInputDialogBuilder()
                .setTitle(title)
                .setDescription(description)
                .setInitialContent(initialContent)
                .setPasswordInput(true)
                .build();
        return textInputDialog.showDialog(textGUI);
    }
}
