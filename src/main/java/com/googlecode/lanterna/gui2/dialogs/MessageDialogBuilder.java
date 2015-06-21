package com.googlecode.lanterna.gui2.dialogs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by martin on 21/06/15.
 */
public class MessageDialogBuilder {
    private String title;
    private String text;
    private List<MessageDialogButton> buttons;

    public MessageDialogBuilder() {
        this.title = "MessageDialog";
        this.text = "Text";
        this.buttons = new ArrayList<MessageDialogButton>();
    }

    public MessageDialog build() {
        return new MessageDialog(
                title,
                text,
                buttons.toArray(new MessageDialogButton[buttons.size()]));
    }

    public MessageDialogBuilder setTitle(String title) {
        if(title == null) {
            title = "";
        }
        this.title = title;
        return this;
    }

    public MessageDialogBuilder setText(String text) {
        if(text == null) {
            text = "";
        }
        this.text = text;
        return this;
    }

    public MessageDialogBuilder addButton(MessageDialogButton button) {
        if(button != null) {
            buttons.add(button);
        }
        return this;
    }
}
