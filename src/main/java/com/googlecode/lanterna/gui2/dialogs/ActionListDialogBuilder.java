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
 * Dialog builder for the {@code ActionListDialog} class, use this to create instances of that class and to customize
 * them
 * @author Martin
 */
public class ActionListDialogBuilder extends AbstractDialogBuilder<ActionListDialogBuilder, ActionListDialog> {

    private final List<Runnable> actions;
    private TerminalSize listBoxSize;
    private boolean canCancel;
    private boolean closeAutomatically;

    /**
     * Default constructor
     */
    public ActionListDialogBuilder() {
        super("ActionListDialogBuilder");
        this.listBoxSize = null;
        this.canCancel = true;
        this.closeAutomatically = true;
        this.actions = new ArrayList<Runnable>();
    }

    @Override
    protected ActionListDialogBuilder self() {
        return this;
    }

    @Override
    protected ActionListDialog buildDialog() {
        return new ActionListDialog(
                title,
                description,
                listBoxSize,
                canCancel,
                closeAutomatically,
                actions);
    }

    /**
     * Sets the size of the internal {@code ActionListBox} in columns and rows, forcing scrollbars to appear if the
     * space isn't big enough to contain all the items
     * @param listBoxSize Size of the {@code ActionListBox}
     * @return Itself
     */
    public ActionListDialogBuilder setListBoxSize(TerminalSize listBoxSize) {
        this.listBoxSize = listBoxSize;
        return this;
    }

    /**
     * Returns the specified size of the internal {@code ActionListBox} or {@code null} if there is no size and the list
     * box will attempt to take up enough size to draw all items
     * @return Specified size of the internal {@code ActionListBox} or {@code null} if there is no size
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
    public ActionListDialogBuilder setCanCancel(boolean canCancel) {
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
     * Adds an additional action to the {@code ActionListBox} that is to be displayed when the dialog is opened
     * @param label Label of the new action
     * @param action Action to perform if the user selects this item
     * @return Itself
     */
    public ActionListDialogBuilder addAction(final String label, final Runnable action) {
        return addAction(new Runnable() {
            @Override
            public String toString() {
                return label;
            }

            @Override
            public void run() {
                action.run();
            }
        });
    }

    /**
     * Adds an additional action to the {@code ActionListBox} that is to be displayed when the dialog is opened. The
     * label of this item will be derived by calling {@code toString()} on the runnable
     * @param action Action to perform if the user selects this item
     * @return Itself
     */
    public ActionListDialogBuilder addAction(Runnable action) {
        this.actions.add(action);
        return this;
    }

    /**
     * Adds additional actions to the {@code ActionListBox} that is to be displayed when the dialog is opened. The
     * label of the items will be derived by calling {@code toString()} on each runnable
     * @param actions Items to add to the {@code ActionListBox}
     * @return Itself
     */
    public ActionListDialogBuilder addActions(Runnable... actions) {
        this.actions.addAll(Arrays.asList(actions));
        return this;
    }

    /**
     * Returns a copy of the internal list of actions currently inside this builder that will be assigned to the
     * {@code ActionListBox} in the dialog when built
     * @return Copy of the internal list of actions currently inside this builder
     */
    public List<Runnable> getActions() {
        return new ArrayList<Runnable>(actions);
    }

    /**
     * Sets if clicking on an action automatically closes the dialog after the action is finished (default: {@code true})
     * @param closeAutomatically if {@code true} dialog will be automatically closed after choosing and finish any of the action
     * @return Itself
     */
    public ActionListDialogBuilder setCloseAutomaticallyOnAction(boolean closeAutomatically) {
        this.closeAutomatically = closeAutomatically;
        return this;
    }
}
