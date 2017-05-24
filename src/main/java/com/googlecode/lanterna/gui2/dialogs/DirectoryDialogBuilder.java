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
 * Copyright (C) 2010-2016 Martin
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package com.googlecode.lanterna.gui2.dialogs;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.LocalizedString;

import java.io.File;

/**
 * Dialog builder for the {@code DirectoryDialog} class, use this to create instances of that class and to customize
 * them.
 *
 * @author Martin
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DirectoryDialogBuilder extends AbstractDialogBuilder<DirectoryDialogBuilder, DirectoryDialog> {

    private String actionLabel;

    private TerminalSize suggestedSize;

    private File selectedDir;

    private boolean showHiddenDirectories;

    /**
     * Default constructor
     */
    public DirectoryDialogBuilder() {
        super("DirectoryDialog");
        actionLabel = LocalizedString.OK.toString();
        suggestedSize = new TerminalSize(45, 10);
        showHiddenDirectories = false;
        selectedDir = null;
    }

    @Override
    protected DirectoryDialog buildDialog() {
        return new DirectoryDialog(title, description, actionLabel, suggestedSize, showHiddenDirectories, selectedDir);
    }

    /**
     * Defines the label to be but on the confirmation button (default: "ok"). You probably want to set this to
     * {@code LocalizedString.Save.toString()} or {@code LocalizedString.Open.toString()}
     *
     * @param actionLabel Label to put on the confirmation button
     * @return Itself
     */
    public DirectoryDialogBuilder setActionLabel(String actionLabel) {
        this.actionLabel = actionLabel;
        return this;
    }

    /**
     * Returns the label on the confirmation button
     *
     * @return Label on the confirmation button
     */
    public String getActionLabel() {
        return actionLabel;
    }

    /**
     * Sets the suggested size for the file dialog, it won't have exactly this size but roughly. Default suggested size
     * is 45x10.
     *
     * @param suggestedSize Suggested size for the file dialog
     * @return Itself
     */
    public DirectoryDialogBuilder setSuggestedSize(TerminalSize suggestedSize) {
        this.suggestedSize = suggestedSize;
        return this;
    }

    /**
     * Returns the suggested size for the file dialog
     *
     * @return Suggested size for the file dialog
     */
    public TerminalSize getSuggestedSize() {
        return suggestedSize;
    }

    /**
     * Sets the directory that is initially selected in the dialog
     *
     * @param selectedDir Directory that is initially selected in the dialog
     * @return Itself
     */
    public DirectoryDialogBuilder setSelectedDirectory(File selectedDir) {
        this.selectedDir = selectedDir;
        return this;
    }

    /**
     * Returns the directory that is initially selected in the dialog
     *
     * @return Directory that is initially selected in the dialog
     */
    public File getSelectedDirectory() {
        return selectedDir;
    }

    /**
     * Sets if hidden directories should be visible in the dialog (default: {@code false}
     *
     * @param showHiddenDirectories If {@code true} then hidden directories will be visible
     */
    public void setShowHiddenDirectories(boolean showHiddenDirectories) {
        this.showHiddenDirectories = showHiddenDirectories;
    }

    /**
     * Checks if hidden directories will be visible in the dialog
     *
     * @return If {@code true} then hidden directories will be visible
     */
    public boolean isShowHiddenDirectories() {
        return showHiddenDirectories;
    }

    @Override
    protected DirectoryDialogBuilder self() {
        return this;
    }
}
