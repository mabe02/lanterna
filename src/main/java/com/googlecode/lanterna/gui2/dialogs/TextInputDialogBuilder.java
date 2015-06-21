package com.googlecode.lanterna.gui2.dialogs;

import com.googlecode.lanterna.TerminalSize;

import java.util.regex.Pattern;

/**
 * Created by martin on 21/06/15.
 */
public class TextInputDialogBuilder {
    private String title;
    private String description;
    private String initialContent;
    private TerminalSize textBoxSize;
    private Pattern validationPattern;
    private String patternMissmatchErrorMessage;
    private boolean passwordInput;

    public TextInputDialogBuilder() {
        this.title = "TextInputDialog";
        this.description = null;
        this.initialContent = "";
        this.textBoxSize = null;
        this.validationPattern = null;
        this.patternMissmatchErrorMessage = "";
        this.passwordInput = false;
    }

    public TextInputDialog build() {
        TerminalSize size = textBoxSize;
        if ((initialContent == null || initialContent.trim().equals("")) && size == null) {
            size = new TerminalSize(40, 1);
        }
        return new TextInputDialog(
                title,
                description,
                size,
                initialContent,
                validationPattern,
                patternMissmatchErrorMessage,
                passwordInput);
    }

    public TextInputDialogBuilder setTitle(String title) {
        if(title == null) {
            title = "";
        }
        this.title = title;
        return this;
    }

    public TextInputDialogBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public TextInputDialogBuilder setInitialContent(String initialContent) {
        this.initialContent = initialContent;
        return this;
    }

    public TextInputDialogBuilder setTextBoxSize(TerminalSize textBoxSize) {
        this.textBoxSize = textBoxSize;
        return this;
    }

    public TextInputDialogBuilder setValidationPattern(Pattern validationPattern) {
        this.validationPattern = validationPattern;
        return this;
    }

    public TextInputDialogBuilder setPatternMissmatchErrorMessage(String patternMissmatchErrorMessage) {
        this.patternMissmatchErrorMessage = patternMissmatchErrorMessage;
        return this;
    }

    public TextInputDialogBuilder setPasswordInput(boolean passwordInput) {
        this.passwordInput = passwordInput;
        return this;
    }
}
