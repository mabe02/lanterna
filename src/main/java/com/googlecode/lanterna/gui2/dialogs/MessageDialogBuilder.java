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

import com.googlecode.lanterna.gui2.Window;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Dialog builder for the {@code MessageDialog} class, use this to create instances of that class and to customize
 * them
 * @author Martin
 */
public class MessageDialogBuilder {
    private String title;
    private String text;
    private final List<MessageDialogButton> buttons;
    private final Set<Window.Hint> extraWindowHints;

    /**
     * Default constructor
     */
    public MessageDialogBuilder() {
        this.title = "MessageDialog";
        this.text = "Text";
        this.buttons = new ArrayList<MessageDialogButton>();
        this.extraWindowHints = Collections.singleton(Window.Hint.CENTERED);
    }

    /**
     * Builds a new {@code MessageDialog} from the properties in the builder
     * @return Newly build {@code MessageDialog}
     */
    public MessageDialog build() {
        MessageDialog messageDialog = new MessageDialog(
                title,
                text,
                buttons.toArray(new MessageDialogButton[buttons.size()]));
        messageDialog.setHints(extraWindowHints);
        return messageDialog;
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
     * Assigns a set of extra window hints that you want the built dialog to have
     * @param extraWindowHints Window hints to assign to the window in addition to the ones the builder will put
     * @return Itself
     */
    public MessageDialogBuilder setExtraWindowHints(Set<Window.Hint> extraWindowHints) {
        this.extraWindowHints.clear();
        this.extraWindowHints.addAll(extraWindowHints);
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
