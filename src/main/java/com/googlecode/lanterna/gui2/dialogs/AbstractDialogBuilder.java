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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract class for dialog building, containing much shared code between different kinds of dialogs
 * @param <B> The real type of the builder class
 * @param <T> Type of dialog this builder is building
 * @author Martin
 */
public abstract class AbstractDialogBuilder<B, T extends DialogWindow> {
    protected String title;
    protected String description;
    protected Set<Window.Hint> extraWindowHints;

    /**
     * Default constructor for a dialog builder
     * @param title Title to assign to the dialog
     */
    public AbstractDialogBuilder(String title) {
        this.title = title;
        this.description = null;
        this.extraWindowHints = Collections.singleton(Window.Hint.CENTERED);
    }

    /**
     * Changes the title of the dialog
     * @param title New title
     * @return Itself
     */
    public B setTitle(String title) {
        if(title == null) {
            title = "";
        }
        this.title = title;
        return self();
    }

    /**
     * Returns the title that the built dialog will have
     * @return Title that the built dialog will have
     */
    public String getTitle() {
        return title;
    }

    /**
     * Changes the description of the dialog
     * @param description New description
     * @return Itself
     */
    public B setDescription(String description) {
        this.description = description;
        return self();
    }

    /**
     * Returns the description that the built dialog will have
     * @return Description that the built dialog will have
     */
    public String getDescription() {
        return description;
    }

    /**
     * Assigns a set of extra window hints that you want the built dialog to have
     * @param extraWindowHints Window hints to assign to the window in addition to the ones the builder will put
     * @return Itself
     */
    public B setExtraWindowHints(Set<Window.Hint> extraWindowHints) {
        this.extraWindowHints = extraWindowHints;
        return self();
    }

    /**
     * Returns the list of extra window hints that will be assigned to the window when built
     * @return List of extra window hints that will be assigned to the window when built
     */
    public Set<Window.Hint> getExtraWindowHints() {
        return extraWindowHints;
    }

    /**
     * Helper method for casting this to {@code type} parameter {@code B}
     * @return {@code this} as {@code B}
     */
    protected abstract B self();

    /**
     * Builds the dialog according to the builder implementation
     * @return New dialog object
     */
    protected abstract T buildDialog();

    /**
     * Builds a new dialog following the specifications of this builder
     * @return New dialog built following the specifications of this builder
     */
    public final T build() {
        T dialog = buildDialog();
        if(!extraWindowHints.isEmpty()) {
            Set<Window.Hint> combinedHints = new HashSet<Window.Hint>(dialog.getHints());
            combinedHints.addAll(extraWindowHints);
            dialog.setHints(combinedHints);
        }
        return dialog;
    }
}
