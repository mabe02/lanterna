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
import com.googlecode.lanterna.gui.*;

/**
 *
 * @author Martin
 */
public class MessageBox extends Window
{
    private DialogResult dialogResult;
    private MessageBox(String title, String message, DialogButtons buttons)
    {
        super(title);
        dialogResult = DialogResult.CANCEL;

        Label messageBoxLabel = new Label(message);
        addComponent(messageBoxLabel);
        addEmptyLine();

        Button okButton = new Button("OK", new Action() {
            public void doAction()
            {
                dialogResult = DialogResult.OK;
                close();
            }
        });
        Button cancelButton = new Button("Cancel", new Action() {
            public void doAction()
            {
                dialogResult = DialogResult.CANCEL;
                close();
            }
        });
        Button yesButton = new Button("Yes", new Action() {
            public void doAction()
            {
                dialogResult = DialogResult.YES;
                close();
            }
        });
        Button noButton = new Button("No", new Action() {
            public void doAction()
            {
                dialogResult = DialogResult.NO;
                close();
            }
        });

        int labelWidth = messageBoxLabel.getPreferredSize().getColumns();
        if(buttons == DialogButtons.OK) {
            Panel buttonPanel = new Panel(new Border.Invisible(), Panel.Orientation.HORISONTAL);
            int leftPadding = 0;
            int buttonsWidth = okButton.getPreferredSize().getColumns();
            if(buttonsWidth < labelWidth)
                leftPadding = (labelWidth - buttonsWidth) / 2;
            if(leftPadding > 0)
                buttonPanel.addComponent(new EmptySpace(leftPadding, 1));
            buttonPanel.addComponent(okButton);
            addComponent(buttonPanel);
        }
        else if(buttons == DialogButtons.OK_CANCEL) {
            Panel buttonPanel = new Panel(new Border.Invisible(), Panel.Orientation.HORISONTAL);
            int leftPadding = 0;
            int buttonsWidth = okButton.getPreferredSize().getColumns() +
                    cancelButton.getPreferredSize().getColumns() + 1;
            if(buttonsWidth < labelWidth)
                leftPadding = (labelWidth - buttonsWidth) / 2;
            if(leftPadding > 0)
                buttonPanel.addComponent(new EmptySpace(leftPadding, 1));
            buttonPanel.addComponent(okButton);
            buttonPanel.addComponent(cancelButton);
            addComponent(buttonPanel);
            setFocus(cancelButton);
        }
        else if(buttons == DialogButtons.YES_NO) {
            Panel buttonPanel = new Panel(new Border.Invisible(), Panel.Orientation.HORISONTAL);
            int leftPadding = 0;
            int buttonsWidth = yesButton.getPreferredSize().getColumns() +
                    noButton.getPreferredSize().getColumns() + 1;
            if(buttonsWidth < labelWidth)
                leftPadding = (labelWidth - buttonsWidth) / 2;
            if(leftPadding > 0)
                buttonPanel.addComponent(new EmptySpace(leftPadding, 1));
            buttonPanel.addComponent(yesButton);
            buttonPanel.addComponent(noButton);
            addComponent(buttonPanel);
            setFocus(noButton);
        }
        else if(buttons == DialogButtons.YES_NO_CANCEL) {
            Panel buttonPanel = new Panel(new Border.Invisible(), Panel.Orientation.HORISONTAL);
            int leftPadding = 0;
            int buttonsWidth = yesButton.getPreferredSize().getColumns() +
                    noButton.getPreferredSize().getColumns() + 1 +
                    cancelButton.getPreferredSize().getColumns();
            if(buttonsWidth < labelWidth)
                leftPadding = (labelWidth - buttonsWidth) / 2;
            if(leftPadding > 0)
                buttonPanel.addComponent(new EmptySpace(leftPadding, 1));
            buttonPanel.addComponent(yesButton);
            buttonPanel.addComponent(noButton);
            buttonPanel.addComponent(cancelButton);
            addComponent(buttonPanel);
            setFocus(cancelButton);
        }
    }

    public static DialogResult showMessageBox(final GUIScreen owner, final String title, final String message)
    {
        return showMessageBox(owner, title, message, DialogButtons.OK);
    }

    public static DialogResult showMessageBox(final GUIScreen owner, final String title,
            final String message, final DialogButtons buttons)
    {
        MessageBox messageBox = new MessageBox(title, message, buttons);
        owner.showWindow(messageBox, GUIScreen.Position.CENTER);
        return messageBox.dialogResult;
    }
}
