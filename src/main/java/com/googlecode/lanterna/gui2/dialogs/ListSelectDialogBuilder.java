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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Dialog builder for the {@code ListSelectDialog} class, use this to create instances of that class and to customize
 * them
 * @author Martin
 */
public class ListSelectDialogBuilder<T> extends AbstractDialogBuilder<ListSelectDialogBuilder<T>, ListSelectDialog<T>> {
    private final List<T> content;
    private TerminalSize listBoxSize;
    private boolean canCancel;

    /**
     * Default constructor
     */
    public ListSelectDialogBuilder() {
        super("ListSelectDialog");
        this.listBoxSize = null;
        this.canCancel = true;
        this.content = new ArrayList<T>();
    }

    @Override
    protected ListSelectDialogBuilder<T> self() {
        return this;
    }

    @Override
    protected ListSelectDialog<T> buildDialog() {
        return new ListSelectDialog<T>(
                title,
                description,
                listBoxSize,
                canCancel,
                content);
    }

    /**
     * Sets the size of the list box in the dialog, scrollbars will be used if there is not enough space to draw all
     * items. If set to {@code null}, the dialog will ask for enough space to be able to draw all items.
     * @param listBoxSize Size of the list box in the dialog
     * @return Itself
     */
    public ListSelectDialogBuilder<T> setListBoxSize(TerminalSize listBoxSize) {
        this.listBoxSize = listBoxSize;
        return this;
    }

    /**
     * Size of the list box in the dialog or {@code null} if the dialog will ask for enough space to draw all items
     * @return Size of the list box in the dialog or {@code null} if the dialog will ask for enough space to draw all items
     */
    public TerminalSize getListBoxSize() {
        return listBoxSize;
    }

    /**
     * Sets if the dialog can be cancelled or not (default: {@code true})
     * @param canCancel If {@code true}, the user has the option to cancel the dialog, if {@code false} there is no such
     *                  button in the dialog
     * @return Itself
     */
    public ListSelectDialogBuilder<T> setCanCancel(boolean canCancel) {
        this.canCancel = canCancel;
        return this;
    }

    /**
     * Returns {@code true} if the dialog can be cancelled once it's opened
     * @return {@code true} if the dialog can be cancelled once it's opened
     */
    public boolean isCanCancel() {
        return canCancel;
    }

    /**
     * Adds an item to the list box at the end
     * @param item Item to add to the list box
     * @return Itself
     */
    public ListSelectDialogBuilder<T> addListItem(T item) {
        this.content.add(item);
        return this;
    }

    /**
     * Adds a list of items to the list box at the end, in the order they are passed in
     * @param items Items to add to the list box
     * @return Itself
     */
    public ListSelectDialogBuilder<T> addListItems(T... items) {
        this.content.addAll(Arrays.asList(items));
        return this;
    }

    /**
     * Returns a copy of the list of items in the list box
     * @return Copy of the list of items in the list box
     */
    public List<T> getListItems() {
        return new ArrayList<T>(content);
    }
}
