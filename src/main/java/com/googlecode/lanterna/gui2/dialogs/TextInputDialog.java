package com.googlecode.lanterna.gui2.dialogs;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import sun.plugin2.message.Message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by martin on 05/06/15.
 */
public class TextInputDialog extends DialogWindow {

    private final TextBox textBox;
    private final Pattern validationPattern;
    private final String patternMismatchErrorMessage;
    private String result;

    TextInputDialog(
                String title,
                String description,
                TerminalSize textBoxPreferredSize,
                String initialContent,
                Pattern validationPattern,
                String patternMismatchErrorMessage,
                boolean password) {

        super(title);
        this.result = null;
        this.textBox = new TextBox(textBoxPreferredSize, initialContent);
        this.validationPattern = validationPattern;
        this.patternMismatchErrorMessage = patternMismatchErrorMessage;

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
            mainPanel.addComponent(new EmptySpace(TerminalSize.ONE));
        }
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
        if(validationPattern != null) {
            Matcher matcher = validationPattern.matcher(text);
            if(!matcher.matches()) {
                String errorMessage = patternMismatchErrorMessage;
                if(errorMessage == null) {
                    errorMessage = "Invalid input";
                }
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

    public String showDialog(WindowBasedTextGUI textGUI) {
        result = null;
        textGUI.addWindow(this);

        //Wait for the window to close, in case the window manager doesn't honor the MODAL hint
        waitUntilClosed();

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
