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
 * Copyright (C) 2010-2019 Martin Berglund
 */
package com.googlecode.lanterna.gui2.dialogs;

import com.googlecode.lanterna.TerminalSize;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Dialog builder for the {@code TextInputDialog} class, use this to create instances of that class and to customize
 * them
 * @author Martin
 */
public class TextInputDialogBuilder extends AbstractDialogBuilder<TextInputDialogBuilder, TextInputDialog> {
    private String initialContent;
    private TerminalSize textBoxSize;
    private TextInputDialogResultValidator validator;
    private boolean passwordInput;

    /**
     * Default constructor
     */
    public TextInputDialogBuilder() {
        super("TextInputDialog");
        this.initialContent = "";
        this.textBoxSize = null;
        this.validator = null;
        this.passwordInput = false;
    }

    @Override
    protected TextInputDialogBuilder self() {
        return this;
    }

    protected TextInputDialog buildDialog() {
        TerminalSize size = textBoxSize;
        if ((initialContent == null || initialContent.trim().equals("")) && size == null) {
            size = new TerminalSize(40, 1);
        }
        return new TextInputDialog(
                title,
                description,
                size,
                initialContent,
                validator,
                passwordInput);
    }

    /**
     * Sets the initial content the dialog will have
     * @param initialContent Initial content the dialog will have
     * @return Itself
     */
    public TextInputDialogBuilder setInitialContent(String initialContent) {
        this.initialContent = initialContent;
        return this;
    }

    /**
     * Returns the initial content the dialog will have
     * @return Initial content the dialog will have
     */
    public String getInitialContent() {
        return initialContent;
    }

    /**
     * Sets the size of the text box the dialog will have
     * @param textBoxSize Size of the text box the dialog will have
     * @return Itself
     */
    public TextInputDialogBuilder setTextBoxSize(TerminalSize textBoxSize) {
        this.textBoxSize = textBoxSize;
        return this;
    }

    /**
     * Returns the size of the text box the dialog will have
     * @return Size of the text box the dialog will have
     */
    public TerminalSize getTextBoxSize() {
        return textBoxSize;
    }

    /**
     * Sets the validator that will be attached to the text box in the dialog
     * @param validator Validator that will be attached to the text box in the dialog
     * @return Itself
     */
    public TextInputDialogBuilder setValidator(TextInputDialogResultValidator validator) {
        this.validator = validator;
        return this;
    }

    /**
     * Returns the validator that will be attached to the text box in the dialog
     * @return validator that will be attached to the text box in the dialog
     */
    public TextInputDialogResultValidator getValidator() {
        return validator;
    }

    /**
     * Helper method that assigned a validator to the text box the dialog will have which matches the pattern supplied
     * @param pattern Pattern to validate the text box
     * @param errorMessage Error message to show when the pattern doesn't match
     * @return Itself
     */
    public TextInputDialogBuilder setValidationPattern(final Pattern pattern, final String errorMessage) {
        return setValidator(new TextInputDialogResultValidator() {
            @Override
            public String validate(String content) {
                Matcher matcher = pattern.matcher(content);
                if(!matcher.matches()) {
                    if(errorMessage == null) {
                        return "Invalid input";
                    }
                    return errorMessage;
                }
                return null;
            }
        });
    }

    /**
     * Sets if the text box the dialog will have contains a password and should be masked (default: {@code false})
     * @param passwordInput {@code true} if the text box should be password masked, {@code false} otherwise
     * @return Itself
     */
    public TextInputDialogBuilder setPasswordInput(boolean passwordInput) {
        this.passwordInput = passwordInput;
        return this;
    }

    /**
     * Returns {@code true} if the text box the dialog will have contains a password and should be masked
     * @return {@code true} if the text box the dialog will have contains a password and should be masked
     */
    public boolean isPasswordInput() {
        return passwordInput;
    }
}
