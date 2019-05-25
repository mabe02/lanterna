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
import com.googlecode.lanterna.gui2.*;

import java.util.List;

/**
 * Dialog containing a multiple item action list box
 * @author Martin
 */
public class ActionListDialog extends DialogWindow {

    ActionListDialog(
            String title,
            String description,
            TerminalSize actionListPreferredSize,
            boolean canCancel,
            final boolean closeAutomatically,
            List<Runnable> actions) {

        super(title);

        ActionListBox listBox = new ActionListBox(actionListPreferredSize);
        for(final Runnable action: actions) {
            listBox.addItem(action.toString(), new Runnable() {
                @Override
                public void run() {
                    action.run();
                    if(closeAutomatically) {
                        close();
                    }
                }
            });
        }

        Panel mainPanel = new Panel();
        mainPanel.setLayoutManager(
                new GridLayout(1)
                        .setLeftMarginSize(1)
                        .setRightMarginSize(1));
        if(description != null) {
            mainPanel.addComponent(new Label(description));
            mainPanel.addComponent(new EmptySpace(TerminalSize.ONE));
        }
        listBox.setLayoutData(
                GridLayout.createLayoutData(
                        GridLayout.Alignment.FILL,
                        GridLayout.Alignment.CENTER,
                        true,
                        false))
                .addTo(mainPanel);
        mainPanel.addComponent(new EmptySpace(TerminalSize.ONE));

        if(canCancel) {
            Panel buttonPanel = new Panel();
            buttonPanel.setLayoutManager(new GridLayout(2).setHorizontalSpacing(1));
            buttonPanel.addComponent(new Button(LocalizedString.Cancel.toString(), new Runnable() {
                @Override
                public void run() {
                    onCancel();
                }
            }).setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER, true, false)));
            buttonPanel.setLayoutData(
                    GridLayout.createLayoutData(
                            GridLayout.Alignment.END,
                            GridLayout.Alignment.CENTER,
                            false,
                            false))
                    .addTo(mainPanel);
        }
        setComponent(mainPanel);
    }

    private void onCancel() {
        close();
    }

    /**
     * Helper method for immediately displaying a {@code ActionListDialog}, the method will return when the dialog is
     * closed
     * @param textGUI Text GUI the dialog should be added to
     * @param title Title of the dialog
     * @param description Description of the dialog
     * @param items Items in the {@code ActionListBox}, the label will be taken from each {@code Runnable} by calling
     *              {@code toString()} on each one
     */
    public static void showDialog(WindowBasedTextGUI textGUI, String title, String description, Runnable... items) {
        ActionListDialog actionListDialog = new ActionListDialogBuilder()
                .setTitle(title)
                .setDescription(description)
                .addActions(items)
                .build();
        actionListDialog.showDialog(textGUI);
    }
}
