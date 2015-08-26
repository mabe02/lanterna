package com.googlecode.lanterna.gui2.dialogs;

import com.googlecode.lanterna.TerminalSize;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by martin on 21/06/15.
 */
public class TextInputDialogBuilder extends AbstractDialogBuilder<TextInputDialogBuilder, TextInputDialog> {
    private String initialContent;
    private TerminalSize textBoxSize;
    private TextInputDialogResultValidator validator;
    private boolean passwordInput;

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

    public TextInputDialogBuilder setInitialContent(String initialContent) {
        this.initialContent = initialContent;
        return this;
    }

    public String getInitialContent() {
        return initialContent;
    }

    public TextInputDialogBuilder setTextBoxSize(TerminalSize textBoxSize) {
        this.textBoxSize = textBoxSize;
        return this;
    }

    public TerminalSize getTextBoxSize() {
        return textBoxSize;
    }

    public TextInputDialogBuilder setValidator(TextInputDialogResultValidator validator) {
        this.validator = validator;
        return this;
    }

    public TextInputDialogResultValidator getValidator() {
        return validator;
    }

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

    public TextInputDialogBuilder setPasswordInput(boolean passwordInput) {
        this.passwordInput = passwordInput;
        return this;
    }

    public boolean isPasswordInput() {
        return passwordInput;
    }
}
