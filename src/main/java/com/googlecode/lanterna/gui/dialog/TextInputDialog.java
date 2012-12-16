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
 * Copyright (C) 2010-2012 Martin
 */

package com.googlecode.lanterna.gui.dialog;

import com.googlecode.lanterna.gui.component.EmptySpace;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.component.PasswordBox;
import com.googlecode.lanterna.gui.component.TextBox;
import com.googlecode.lanterna.gui.*;
import com.googlecode.lanterna.gui.layout.LinearLayout;

/**
 *
 * @author Martin
 */
public class TextInputDialog extends Window
{
    private final TextBox textBox;
    private String result;

    private TextInputDialog(final TextBoxFactory textBoxFactory, final String title, 
            final String description, final String initialText) 
    {
        this(textBoxFactory, title, description, initialText, 0);
    }

    private TextInputDialog(final TextBoxFactory textBoxFactory, final String title,
            final String description, final String initialText, int textBoxWidth) {
        super(title);
        Label descriptionLabel = new Label(description);
        if(textBoxWidth == 0) {
            textBoxWidth = Math.max(descriptionLabel.getPreferredSize().getColumns(), title.length());
        }

        textBox = textBoxFactory.createTextBox(initialText, textBoxWidth);
        addComponent(descriptionLabel);
        addComponent(new EmptySpace(1, 1));
        addComponent(textBox);

        addComponent(new EmptySpace(1, 1));
        Panel okCancelPanel = new Panel(new Border.Invisible(), Panel.Orientation.HORISONTAL);
        Button okButton = new Button("OK", new Action() {
            @Override
            public void doAction() {
                result = textBox.getText();
                close();
            }
        });
        okButton.setAlignment(Component.Alignment.RIGHT_CENTER);
        okCancelPanel.addComponent(okButton, LinearLayout.GROWS_HORIZONTALLY);
        Button cancelButton = new Button("Cancel", new Action() {
            @Override
            public void doAction() {
                close();
            }
        });
        okCancelPanel.addComponent(cancelButton);
        addComponent(okCancelPanel, LinearLayout.GROWS_HORIZONTALLY);
    }

    public static String showTextInputBox(final GUIScreen owner, final String title,
            final String description, final String initialText)
    {
        return showTextInputBox(owner, title, description, initialText, 0);
    }

    public static String showTextInputBox(final GUIScreen owner, final String title,
            final String description, final String initialText, final int textBoxWidth)
    {
        final TextInputDialog textInputBox =
                new TextInputDialog(new NormalTextBoxFactory(), title, description, initialText, textBoxWidth);
        owner.showWindow(textInputBox, GUIScreen.Position.CENTER);
        return textInputBox.result;
    }

    public static String showPasswordInputBox(final GUIScreen owner, final String title,
            final String description, final String initialText)
    {
        return showPasswordInputBox(owner, title, description, initialText, 0);
    }

    public static String showPasswordInputBox(final GUIScreen owner, final String title,
            final String description, final String initialText, final int textBoxWidth)
    {
        TextInputDialog textInputBox = new TextInputDialog(new PasswordTextBoxFactory(), title, description, initialText, textBoxWidth);
        owner.showWindow(textInputBox, GUIScreen.Position.CENTER);
        return textInputBox.result;
    }

    private static interface TextBoxFactory
    {
        public TextBox createTextBox(String initialContent, int width);
    }

    private static class NormalTextBoxFactory implements TextBoxFactory {
        @Override
        public TextBox createTextBox(String initialContent, int width)
        {
            return new TextBox(initialContent, width);
        }
    }

    private static class PasswordTextBoxFactory implements TextBoxFactory {
        @Override
        public TextBox createTextBox(String initialContent, int width)
        {
            return new PasswordBox(initialContent, width);
        }
    }
}
