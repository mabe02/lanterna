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

package com.googlecode.lanterna.gui.component;

import com.googlecode.lanterna.gui.*;
import com.googlecode.lanterna.gui.layout.LinearLayout;
import com.googlecode.lanterna.gui.util.ShortcutHelper;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.input.Key.Kind;
import com.googlecode.lanterna.terminal.TerminalSize;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Martin
 */
public class Table extends AbstractComponent implements InteractableContainer
{
    private final ShortcutHelper shortcutHelper;
    private final Panel mainPanel;
    private final List<Component[]> rows;
    private Panel[] columns;

    public Table()
    {
        this(1);
    }

    public Table(String title)
    {
        this(1, title);
    }

    public Table(int nrOfColumns)
    {
        this(nrOfColumns, null);
    }

    public Table(int nrOfColumns, String title)
    {
        if(title == null)
            mainPanel = new Panel(Panel.Orientation.HORISONTAL);
        else
            mainPanel = new Panel(title, Panel.Orientation.HORISONTAL);

        shortcutHelper = new ShortcutHelper();
        rows = new ArrayList<Component[]>();

        //Initialize to something to avoid null pointer exceptions
        columns = new Panel[0];
        alterTableStructure(nrOfColumns);        
    }

    public void setColumnPaddingSize(int size)
    {
        ((LinearLayout)mainPanel.getLayoutManager()).setPadding(size);
    }

    /**
    Adds a new row to this table, using the given components as its cells. 
    */
    public void addRow(Component ...components)
    {
        Component[] newRow = new Component[columns.length];
        for(int i = 0; i < columns.length; i++) {
            if(i >= components.length)
                newRow[i] = new EmptySpace(1, 1);
            else
                newRow[i] = components[i];
        }
        rows.add(newRow);
        for(int i = 0; i < columns.length; i++)
            columns[i].addComponent(newRow[i]);
        invalidate();
    }

    public int getNrOfRows()
    {
        return rows.size();
    }

    public Component[] getRow(int index)
    {
        return Arrays.copyOf(rows.get(index), columns.length);
    }

    public void removeRow(int index)
    {
        Component[] row = getRow(index);
        rows.remove(index);
        for(int i = 0; i < columns.length; i++)
            columns[i].removeComponent(row[i]);

        invalidate();
    }

    public final void alterTableStructure(int nrOfColumns)
    {
        removeAllRows();
        mainPanel.removeAllComponents();
        columns = new Panel[nrOfColumns];
        for(int i = 0; i < nrOfColumns; i++) {
            columns[i] = new Panel(Panel.Orientation.VERTICAL);
            mainPanel.addComponent(columns[i]);
        }
    }

    /**
    Removes all rows from this table.
    */
    public void removeAllRows()
    {
        rows.clear();
        for(int i = 0; i < columns.length; i++)
            columns[i].removeAllComponents();

        invalidate();
    }

    @Override
    protected TerminalSize calculatePreferredSize() {
        return mainPanel.getPreferredSize();
    }

    @Override
    public void repaint(TextGraphics graphics)
    {
        mainPanel.repaint(graphics);
    }

    @Override
    protected void setParent(Container container)
    {
        super.setParent(container);
        
        //Link the parent past the table
        mainPanel.setParent(getParent());
    }

    @Override
    public boolean hasInteractable(Interactable interactable)
    {
        return mainPanel.hasInteractable(interactable);
    }

    @Override
    public Interactable nextFocus(Interactable fromThis)
    {
        return mainPanel.nextFocus(fromThis);
    }

    @Override
    public Interactable previousFocus(Interactable fromThis)
    {
        return mainPanel.previousFocus(fromThis);
    }

    @Override
    public void addShortcut(Kind key, Action action) {
        shortcutHelper.addShortcut(key, action);
    }

    @Override
    public void addShortcut(char character, boolean withCtrl, boolean withAlt, Action action) {
        shortcutHelper.addShortcut(character, withCtrl, withAlt, action);
    }

    @Override
    public boolean triggerShortcut(Key key) {
        return shortcutHelper.triggerShortcut(key);
    }
}
