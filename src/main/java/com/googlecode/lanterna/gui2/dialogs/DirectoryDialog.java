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
import com.googlecode.lanterna.gui2.ActionListBox;
import com.googlecode.lanterna.gui2.BorderLayout;
import com.googlecode.lanterna.gui2.BorderLayout.Location;
import com.googlecode.lanterna.gui2.Borders;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LocalizedString;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Dialog that allows the user to iterate the file system and pick directory.
 *
 * @author Martin
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DirectoryDialog extends DialogWindow {

    private final ActionListBox dirListBox;

    private final TextBox dirBox;

    private final boolean showHiddenDirs;

    private File directory;

    private File selectedDir;

    /**
     * Default constructor for {@code DirectoryDialog}
     *
     * @param title          Title of the dialog
     * @param description    Description of the dialog, is displayed at the top of the content area
     * @param actionLabel    Label to use on the "confirm" button, for example "open" or "save"
     * @param dialogSize     Rough estimation of how big you want the dialog to be
     * @param showHiddenDirs If {@code true}, hidden directories will be visible
     * @param selectedObject Initially selected directory node
     */
    public DirectoryDialog(
            String title,
            String description,
            String actionLabel,
            TerminalSize dialogSize,
            boolean showHiddenDirs,
            File selectedObject) {
        super(title);
        this.selectedDir = null;
        this.showHiddenDirs = showHiddenDirs;

        if (selectedObject == null || !selectedObject.exists()) {
            selectedObject = new File("").getAbsoluteFile();
        }
        selectedObject = selectedObject.getAbsoluteFile();

        Panel contentPane = new Panel();
        contentPane.setLayoutManager(new BorderLayout());

        Panel dirsPane = new Panel();
        dirsPane.setLayoutManager(new BorderLayout());
        contentPane.addComponent(dirsPane, Location.CENTER);

        if (description != null)
            contentPane.addComponent(new Label(description), Location.TOP);

        int unitHeight = dialogSize.getRows();

        dirListBox = new ActionListBox(new TerminalSize(dialogSize.getColumns(), unitHeight));
        dirsPane.addComponent(dirListBox.withBorder(Borders.singleLine()), Location.CENTER);

        dirBox = new TextBox(new TerminalSize(dialogSize.getColumns(), 1));
        dirsPane.addComponent(dirBox.withBorder(Borders.singleLine()), Location.BOTTOM);

        Panel panelButtons = new Panel(new GridLayout(2));
        panelButtons.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.END, GridLayout.Alignment.CENTER, false, false, 2, 1));
        panelButtons.addComponent(new Button(actionLabel, new OkHandler()));
        panelButtons.addComponent(new Button(LocalizedString.Cancel.toString(), new CancelHandler()));
        contentPane.addComponent(panelButtons, Location.BOTTOM);

        if (selectedObject.isFile()) {
            directory = selectedObject.getParentFile();
        }
        else if (selectedObject.isDirectory()) {
            directory = selectedObject;
        }

        reloadViews(directory);
        setComponent(contentPane);
    }

    /**
     * {@inheritDoc}
     *
     * @param textGUI Text GUI to add the dialog to
     * @return The directory which was selected in the dialog or {@code null} if the dialog was cancelled
     */
    @Override
    public File showDialog(WindowBasedTextGUI textGUI) {
        selectedDir = null;
        super.showDialog(textGUI);
        return selectedDir;
    }

    private class OkHandler implements Runnable {

        @Override
        public void run() {
            File dir = new File(dirBox.getText());
            if (dir.exists() && dir.isDirectory()) {
                selectedDir = dir;
                close();
            }
            else {
                MessageDialog.showMessageDialog(getTextGUI(), "Error", "Please select a valid directory name", MessageDialogButton.OK);
            }
        }
    }

    private class CancelHandler implements Runnable {

        @Override
        public void run() {
            selectedDir = null;
            close();
        }
    }

    private class DoNothing implements Runnable {

        @Override
        public void run() {
        }
    }

    private void reloadViews(final File directory) {
        dirBox.setText(directory.getAbsolutePath());
        dirListBox.clearItems();
        File[] entries = directory.listFiles();
        if (entries == null) {
            return;
        }
        Arrays.sort(entries, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
            }
        });
        if (directory.getAbsoluteFile().getParentFile() != null) {
            dirListBox.addItem("..", new Runnable() {
                @Override
                public void run() {
                    DirectoryDialog.this.directory = directory.getAbsoluteFile().getParentFile();
                    reloadViews(directory.getAbsoluteFile().getParentFile());
                }
            });
        }
        else {
            File[] roots = File.listRoots();
            for (final File entry : roots) {
                if (entry.canRead()) {
                    dirListBox.addItem('[' + entry.getPath() + ']', new Runnable() {
                        @Override
                        public void run() {
                            DirectoryDialog.this.directory = entry;
                            reloadViews(entry);
                        }
                    });
                }
            }
        }
        for (final File entry : entries) {
            if (entry.isHidden() && !showHiddenDirs) {
                continue;
            }
            if (entry.isDirectory()) {
                dirListBox.addItem(entry.getName(), new Runnable() {
                    @Override
                    public void run() {
                        DirectoryDialog.this.directory = entry;
                        reloadViews(entry);
                    }
                });
            }
        }
        if (dirListBox.isEmpty()) {
            dirListBox.addItem("<empty>", new DoNothing());
        }
    }
}
