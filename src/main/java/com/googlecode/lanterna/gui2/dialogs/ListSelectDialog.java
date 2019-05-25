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

import com.googlecode.lanterna.TerminalTextUtils;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;

import java.util.List;

/**
 * Dialog that allows the user to select an item from a list
 *
 * @param <T> Type of elements in the list
 * @author Martin
 */
public class ListSelectDialog<T> extends DialogWindow {
    private T result;

    ListSelectDialog(
            String title,
            String description,
            TerminalSize listBoxPreferredSize,
            boolean canCancel,
            List<T> content) {

        super(title);
        this.result = null;
        if(content.isEmpty()) {
            throw new IllegalStateException("ListSelectDialog needs at least one item");
        }

        ActionListBox listBox = new ActionListBox(listBoxPreferredSize);
        for(final T item: content) {
            listBox.addItem(item.toString(), new Runnable() {
                @Override
                public void run() {
                    onSelect(item);
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

    private void onSelect(T item) {
        result = item;
        close();
    }

    private void onCancel() {
        close();
    }

    /**
     * {@inheritDoc}
     *
     * @param textGUI Text GUI to add the dialog to
     * @return The item in the list that was selected or {@code null} if the dialog was cancelled
     */
    @Override
    public T showDialog(WindowBasedTextGUI textGUI) {
        result = null;
        super.showDialog(textGUI);
        return result;
    }

    /**
     * Shortcut for quickly creating a new dialog
     * @param textGUI Text GUI to add the dialog to
     * @param title Title of the dialog
     * @param description Description of the dialog
     * @param items Items in the dialog
     * @param <T> Type of items in the dialog
     * @return The selected item or {@code null} if cancelled
     */
    public static <T> T showDialog(WindowBasedTextGUI textGUI, String title, String description, T... items) {
        return showDialog(textGUI, title, description, null, items);
    }

    /**
     * Shortcut for quickly creating a new dialog
     * @param textGUI Text GUI to add the dialog to
     * @param title Title of the dialog
     * @param description Description of the dialog
     * @param listBoxHeight Maximum height of the list box, scrollbars will be used if there are more items
     * @param items Items in the dialog
     * @param <T> Type of items in the dialog
     * @return The selected item or {@code null} if cancelled
     */
    public static <T> T showDialog(WindowBasedTextGUI textGUI, String title, String description, int listBoxHeight, T... items) {
        int width = 0;
        for(T item: items) {
            width = Math.max(width, TerminalTextUtils.getColumnWidth(item.toString()));
        }
        width += 2;
        return showDialog(textGUI, title, description, new TerminalSize(width, listBoxHeight), items);
    }

    /**
     * Shortcut for quickly creating a new dialog
     * @param textGUI Text GUI to add the dialog to
     * @param title Title of the dialog
     * @param description Description of the dialog
     * @param listBoxSize Maximum size of the list box, scrollbars will be used if the items cannot fit
     * @param items Items in the dialog
     * @param <T> Type of items in the dialog
     * @return The selected item or {@code null} if cancelled
     */
    public static <T> T showDialog(WindowBasedTextGUI textGUI, String title, String description, TerminalSize listBoxSize, T... items) {
        ListSelectDialog<T> listSelectDialog = new ListSelectDialogBuilder<T>()
                .setTitle(title)
                .setDescription(description)
                .setListBoxSize(listBoxSize)
                .addListItems(items)
                .build();
        return listSelectDialog.showDialog(textGUI);
    }
}
