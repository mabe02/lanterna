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

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Dialog that allows the user to iterate the file system and pick file to open/save
 *
 * @author Martin
 */
public class FileDialog extends DialogWindow {

    private final ActionListBox fileListBox;
    private final ActionListBox directoryListBox;
    private final TextBox fileBox;
    private final Button okButton;
    private final boolean showHiddenFilesAndDirs;

    private File directory;
    private File selectedFile;

    /**
     * Default constructor for {@code FileDialog}
     * @param title Title of the dialog
     * @param description Description of the dialog, is displayed at the top of the content area
     * @param actionLabel Label to use on the "confirm" button, for example "open" or "save"
     * @param dialogSize Rough estimation of how big you want the dialog to be
     * @param showHiddenFilesAndDirs If {@code true}, hidden files and directories will be visible
     * @param selectedObject Initially selected file node
     */
    public FileDialog(
            String title,
            String description,
            String actionLabel,
            TerminalSize dialogSize,
            boolean showHiddenFilesAndDirs,
            File selectedObject) {
        super(title);
        this.selectedFile = null;
        this.showHiddenFilesAndDirs = showHiddenFilesAndDirs;

        if(selectedObject == null || !selectedObject.exists()) {
            selectedObject = new File("").getAbsoluteFile();
        }
        selectedObject = selectedObject.getAbsoluteFile();

        Panel contentPane = new Panel();
        contentPane.setLayoutManager(new GridLayout(2));

        if(description != null) {
            new Label(description)
                    .setLayoutData(
                            GridLayout.createLayoutData(
                                    GridLayout.Alignment.BEGINNING,
                                    GridLayout.Alignment.CENTER,
                                    false,
                                    false,
                                    2,
                                    1))
                    .addTo(contentPane);
        }

        int unitWidth = dialogSize.getColumns() / 3;
        int unitHeight = dialogSize.getRows();

        new FileSystemLocationLabel()
                .setLayoutData(GridLayout.createLayoutData(
                        GridLayout.Alignment.FILL,
                        GridLayout.Alignment.CENTER,
                        true,
                        false,
                        2,
                        1))
                .addTo(contentPane);

        fileListBox = new ActionListBox(new TerminalSize(unitWidth * 2, unitHeight));
        fileListBox.withBorder(Borders.singleLine())
                .setLayoutData(GridLayout.createLayoutData(
                        GridLayout.Alignment.BEGINNING,
                        GridLayout.Alignment.CENTER,
                        false,
                        false))
                .addTo(contentPane);
        directoryListBox = new ActionListBox(new TerminalSize(unitWidth, unitHeight));
        directoryListBox.withBorder(Borders.singleLine())
                .addTo(contentPane);

        fileBox = new TextBox()
                .setLayoutData(GridLayout.createLayoutData(
                        GridLayout.Alignment.FILL,
                        GridLayout.Alignment.CENTER,
                        true,
                        false,
                        2,
                        1))
                .addTo(contentPane);

        new Separator(Direction.HORIZONTAL)
                .setLayoutData(
                        GridLayout.createLayoutData(
                                GridLayout.Alignment.FILL,
                                GridLayout.Alignment.CENTER,
                                true,
                                false,
                                2,
                                1))
                .addTo(contentPane);

        okButton = new Button(actionLabel, new OkHandler());
        Panels.grid(2,
                okButton,
                new Button(LocalizedString.Cancel.toString(), new CancelHandler()))
                .setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.END, GridLayout.Alignment.CENTER, false, false, 2, 1))
                .addTo(contentPane);

        if(selectedObject.isFile()) {
            directory = selectedObject.getParentFile();
            fileBox.setText(selectedObject.getName());
        }
        else if(selectedObject.isDirectory()) {
            directory = selectedObject;
        }

        reloadViews(directory);
        setComponent(contentPane);
    }

    /**
     * {@inheritDoc}
     * @param textGUI Text GUI to add the dialog to
     * @return The file which was selected in the dialog or {@code null} if the dialog was cancelled
     */
    @Override
    public File showDialog(WindowBasedTextGUI textGUI) {
        selectedFile = null;
        super.showDialog(textGUI);
        return selectedFile;
    }

    private class OkHandler implements Runnable {
        @Override
        public void run() {
            if(!fileBox.getText().isEmpty()) {
                File file = new File(fileBox.getText());
                selectedFile = file.isAbsolute() ? file : new File(directory, fileBox.getText());
                close();
            }
            else {
                MessageDialog.showMessageDialog(getTextGUI(), "Error", "Please select a valid file name", MessageDialogButton.OK);
            }
        }
    }

    private class CancelHandler implements Runnable {
        @Override
        public void run() {
            selectedFile = null;
            close();
        }
    }

    private class DoNothing implements Runnable {
        @Override
        public void run() {
        }
    }

    private void reloadViews(final File directory) {
        directoryListBox.clearItems();
        fileListBox.clearItems();
        File []entries = directory.listFiles();
        if(entries == null) {
            return;
        }
        Arrays.sort(entries, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
            }
        });
        if (directory.getAbsoluteFile().getParentFile() !=null){
            directoryListBox.addItem("..", new Runnable() {
                @Override
                public void run() {
                    FileDialog.this.directory = directory.getAbsoluteFile().getParentFile();
                    reloadViews(directory.getAbsoluteFile().getParentFile());
                }
            });
        } else {
            File[] roots = File.listRoots();
            for (final File entry : roots) {
                if (entry.canRead()) {
                    directoryListBox.addItem('[' + entry.getPath() + ']', new Runnable() {
                        @Override
                        public void run() {
                            FileDialog.this.directory = entry;
                            reloadViews(entry);
                        }
                    });
                }
            }
        }
        for(final File entry: entries) {
            if(entry.isHidden() && !showHiddenFilesAndDirs) {
                continue;
            }
            if(entry.isDirectory()) {
                directoryListBox.addItem(entry.getName(), new Runnable() {
                    @Override
                    public void run() {
                        FileDialog.this.directory = entry;
                        reloadViews(entry);
                    }
                });
            }
            else {
                fileListBox.addItem(entry.getName(), new Runnable() {
                    @Override
                    public void run() {
                        fileBox.setText(entry.getName());
                        setFocusedInteractable(okButton);
                    }
                });
            }
        }
        if(fileListBox.isEmpty()) {
            fileListBox.addItem("<empty>", new DoNothing());
        }
    }

    private class FileSystemLocationLabel extends Label {
        public FileSystemLocationLabel() {
            super("");
            setPreferredSize(TerminalSize.ONE);
        }

        @Override
        public void onBeforeDrawing() {
            TerminalSize area = getSize();
            String absolutePath = directory.getAbsolutePath();
            int absolutePathLengthInColumns = TerminalTextUtils.getColumnWidth(absolutePath);
            if(area.getColumns() < absolutePathLengthInColumns) {
                absolutePath = absolutePath.substring(absolutePathLengthInColumns - area.getColumns());
                absolutePath = "..." + absolutePath.substring(Math.min(absolutePathLengthInColumns, 3));
            }
            setText(absolutePath);
        }
    }
}
