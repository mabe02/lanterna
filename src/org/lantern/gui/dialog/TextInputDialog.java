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
 * Copyright (C) 2010-2011 mabe02
 */

package org.lantern.gui.dialog;

import org.lantern.LanternException;
import org.lantern.gui.*;

/**
 *
 * @author mabe02
 */
public class TextInputDialog extends Window
{
    private final TextBox textBox;
    private String result;

    private TextInputDialog(final TextBoxFactory textBoxFactory, final String title, 
            final String description, final String initialText, int textBoxWidth)
    {
        super(title);
        Label descriptionLabel = new Label(description);
        if(textBoxWidth == -1)
            textBoxWidth = descriptionLabel.getPreferredSize().getColumns();

        textBox = textBoxFactory.createTextBox(textBoxWidth, initialText);
        addComponent(descriptionLabel);
        addComponent(new EmptySpace(1, 1));
        addComponent(textBox);

        int internalWidth = textBoxWidth > descriptionLabel.getPreferredSize().getColumns() ?
            textBoxWidth : descriptionLabel.getPreferredSize().getColumns();
        int buttonWidth = "OK".length() + 4 + "Cancel".length() + 4 + 1;
        int space = (internalWidth - buttonWidth) / 2;

        addComponent(new EmptySpace(1, 1));
        Panel okCancelPanel = new Panel(new Border.Invisible(), Panel.Orientation.HORISONTAL);
        okCancelPanel.addComponent(new EmptySpace(space, 1));
        okCancelPanel.addComponent(new Button("OK", new Action() {
            public void doAction()
            {
                result = textBox.getText();
                close();
            }
        }));
        okCancelPanel.addComponent(new Button("Cancel", new Action() {
            public void doAction()
            {
                close();
            }
        }));
        addComponent(okCancelPanel);
    }

    public static String showTextInputBox(final GUIScreen owner, final String title,
            final String description, final String initialText) throws LanternException
    {
        return showTextInputBox(owner, title, description, initialText, -1);
    }

    public static String showTextInputBox(final GUIScreen owner, final String title,
            final String description, final String initialText, final int textBoxWidth) throws LanternException
    {
        final TextInputDialog textInputBox =
                new TextInputDialog(new NormalTextBoxFactory(), title, description, initialText, textBoxWidth);
        owner.showWindow(textInputBox, GUIScreen.Position.CENTER);
        return textInputBox.result;
    }

    public static String showPasswordInputBox(final GUIScreen owner, final String title,
            final String description, final String initialText) throws LanternException
    {
        return showPasswordInputBox(owner, title, description, initialText, -1);
    }

    public static String showPasswordInputBox(final GUIScreen owner, final String title,
            final String description, final String initialText, final int textBoxWidth) throws LanternException
    {
        TextInputDialog textInputBox = new TextInputDialog(new PasswordTextBoxFactory(), title, description, initialText, textBoxWidth);
        owner.showWindow(textInputBox, GUIScreen.Position.CENTER);
        return textInputBox.result;
    }

    private static interface TextBoxFactory
    {
        public TextBox createTextBox(int width, String initialContent);
    }

    private static class NormalTextBoxFactory implements TextBoxFactory {
        public TextBox createTextBox(int width, String initialContent)
        {
            return new TextBox(width, initialContent);
        }
    }

    private static class PasswordTextBoxFactory implements TextBoxFactory {
        public TextBox createTextBox(int width, String initialContent)
        {
            return new PasswordBox(width, initialContent);
        }
    }
}
