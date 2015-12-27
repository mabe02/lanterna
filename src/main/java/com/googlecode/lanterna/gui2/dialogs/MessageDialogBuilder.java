package com.googlecode.lanterna.gui2.dialogs;

import java.util.ArrayList;
import java.util.List;

/**
 * Dialog builder for the {@code MessageDialog} class, use this to create instances of that class and to customize
 * them
 * @author Martin
 */
public class MessageDialogBuilder {
    private String title;
    private String text;
    private List<MessageDialogButton> buttons;

    /**
     * Default constructor
     */
    public MessageDialogBuilder() {
        this.title = "MessageDialog";
        this.text = "Text";
        this.buttons = new ArrayList<MessageDialogButton>();
    }

    /**
     * Builds a new {@code MessageDialog} from the properties in the builder
     * @return Newly build {@code MessageDialog}
     */
    public MessageDialog build() {
        return new MessageDialog(
                title,
                text,
                buttons.toArray(new MessageDialogButton[buttons.size()]));
    }

    /**
     * Sets the title of the {@code MessageDialog}
     * @param title New title of the message dialog
     * @return Itself
     */
    public MessageDialogBuilder setTitle(String title) {
        if(title == null) {
            title = "";
        }
        this.title = title;
        return this;
    }

    /**
     * Sets the main text of the {@code MessageDialog}
     * @param text Main text of the {@code MessageDialog}
     * @return Itself
     */
    public MessageDialogBuilder setText(String text) {
        if(text == null) {
            text = "";
        }
        this.text = text;
        return this;
    }

    /**
     * Adds a button to the dialog
     * @param button Button to add to the dialog
     * @return Itself
     */
    public MessageDialogBuilder addButton(MessageDialogButton button) {
        if(button != null) {
            buttons.add(button);
        }
        return this;
    }
}
