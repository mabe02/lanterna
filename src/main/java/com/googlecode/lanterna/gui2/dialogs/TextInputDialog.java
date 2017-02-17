/*
 * This file is part of lanterna (http://code.google.com/p/lanterna/).
 *
 * lanterna is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2010-2017 Martin Berglund
 */
package com.googlecode.lanterna.gui2.dialogs;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;

import java.math.BigInteger;
import java.util.regex.Pattern;

/**
 * {@code TextInputDialog} is a modal text input dialog that prompts the user to enter a text string. The class supports
 * validation and password masking. The builder class to help setup {@code TextInputDialog}s is
 * {@code TextInputDialogBuilder}.
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
        buttonPanel.addComponent(new Button(LocalizedString.OK.toString(), new Runnable() {
            @Override
            public void run() {
                onOK();
            }
        }).setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER, true, false)));
        buttonPanel.addComponent(new Button(LocalizedString.Cancel.toString(), new Runnable() {
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

    /**
     * Shortcut for quickly showing a {@code TextInputDialog}
     * @param textGUI GUI to show the dialog on
     * @param title Title of the dialog
     * @param description Description of the dialog
     * @param initialContent What content to place in the text box initially
     * @return The string the user typed into the text box, or {@code null} if the dialog was cancelled
     */
    public static String showDialog(WindowBasedTextGUI textGUI, String title, String description, String initialContent) {
        TextInputDialog textInputDialog = new TextInputDialogBuilder()
                .setTitle(title)
                .setDescription(description)
                .setInitialContent(initialContent)
                .build();
        return textInputDialog.showDialog(textGUI);
    }

    /**
     * Shortcut for quickly showing a {@code TextInputDialog} that only accepts numbers
     * @param textGUI GUI to show the dialog on
     * @param title Title of the dialog
     * @param description Description of the dialog
     * @param initialContent What content to place in the text box initially
     * @return The number the user typed into the text box, or {@code null} if the dialog was cancelled
     */
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

    /**
     * Shortcut for quickly showing a {@code TextInputDialog} with password masking
     * @param textGUI GUI to show the dialog on
     * @param title Title of the dialog
     * @param description Description of the dialog
     * @param initialContent What content to place in the text box initially
     * @return The string the user typed into the text box, or {@code null} if the dialog was cancelled
     */
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
