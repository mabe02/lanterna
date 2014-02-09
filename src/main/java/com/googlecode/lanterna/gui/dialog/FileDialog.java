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

import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.Border;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Theme;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.ActionListBox;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.component.EmptySpace;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.component.TextBox;
import com.googlecode.lanterna.gui.layout.BorderLayout;
import com.googlecode.lanterna.gui.layout.LinearLayout;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

/**
 *
 * @author martin
 */
public class FileDialog extends Window {

    public static File showOpenFileDialog(GUIScreen owner, File directory, String title) {
        FileDialog dialog = new FileDialog(directory, title, Kind.Open);
        owner.showWindow(dialog);
        return dialog.getSelectedFile();
    }
    
    public static File showSaveFileDialog(GUIScreen owner, File directory, String title) {
        FileDialog dialog = new FileDialog(directory, title, Kind.Save);
        owner.showWindow(dialog);
        return dialog.getSelectedFile();
    }
    
    private static enum Kind {
        Open,
        Save,
        ;
    }
    
    private final Label labelCurrentDirectory;
    private final TextBox fileText;
    private final ActionListBox dirView;
    private final ActionListBox fileView;
    private File currentDirectory;
    private File selectedFile;
    
    private FileDialog(File directory, String title, Kind kind) {
        super(title);
        this.selectedFile = null;
        
        Panel panelFileDir = new Panel(Panel.Orientation.HORISONTAL);
        fileView = createFileListBox();
        dirView = createFileListBox();
        fileText = new TextBox();
        Panel panelButtons = new Panel(Panel.Orientation.HORISONTAL);
        Button okButton = new Button(kind.name(), new Action() {
            @Override
            public void doAction() {
                selectedFile = new File(currentDirectory, fileText.getText());
                close();
            }
        });
        Button cancelButton = new Button("Cancel", new Action() {
            @Override
            public void doAction() {
                close();
            }
        });
                
        labelCurrentDirectory = new Label();
        addComponent(labelCurrentDirectory, LinearLayout.GROWS_HORIZONTALLY);
        panelFileDir.setLayoutManager(new BorderLayout());
        panelFileDir.addComponent(fileView.addBorder(new Border.Bevel(true), "Files"), BorderLayout.CENTER);
        panelFileDir.addComponent(dirView.addBorder(new Border.Bevel(true), "Directories"), BorderLayout.RIGHT);
        addComponent(panelFileDir, LinearLayout.GROWS_HORIZONTALLY);
        addComponent(new EmptySpace(40,1));
        addComponent(fileText, LinearLayout.GROWS_HORIZONTALLY);
        addComponent(new EmptySpace(40,1));
        panelButtons.addComponent(new EmptySpace(), LinearLayout.GROWS_HORIZONTALLY);
        panelButtons.addComponent(okButton);
        panelButtons.addComponent(cancelButton);
        addComponent(panelButtons, LinearLayout.GROWS_HORIZONTALLY);
        reloadViews(directory);
    }
    
    private void reloadViews(File directory) {
        this.currentDirectory = directory.getAbsoluteFile();
        dirView.clearItems();
        fileView.clearItems();
        labelCurrentDirectory.setText(currentDirectory.getAbsolutePath());
        File []entries = directory.listFiles();
        Arrays.sort(entries, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
            }
        });
        dirView.addAction("..", new Action() {
            @Override
            public void doAction() {
                reloadViews(currentDirectory.getParentFile());
            }
        });
        for(final File entry: entries) {
            if(entry.isDirectory()) {
                dirView.addAction(entry.getName(), new Action() {
                    @Override
                    public void doAction() {
                        reloadViews(entry);
                    }
                });
            }
            else {
                fileView.addAction(entry.getName(), new Action() {
                    @Override
                    public void doAction() {
                        fileText.setText(entry.getName());
                        setFocus(fileText);
                    }
                });
            }
        }
    }

    private File getSelectedFile() {
        return selectedFile;
    }

    private ActionListBox createFileListBox() {
        return new ActionListBox() {
            @Override
            protected Theme.Definition getListItemThemeDefinition(Theme theme) {
                return theme.getDefinition(Theme.Category.TEXTBOX);
            }

            @Override
            protected Theme.Definition getSelectedListItemThemeDefinition(Theme theme) {
                return theme.getDefinition(Theme.Category.TEXTBOX_FOCUSED);
            }
        };
    }
}
