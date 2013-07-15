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
import com.googlecode.lanterna.gui.Border.Invisible;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.ActionListBox;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.terminal.TerminalSize;

/**
 *
 * @author Martin
 */
public class ActionListDialog extends Window
{
    private final ActionListBox actionListBox;
    private final boolean closeBeforeAction;

    private ActionListDialog(String title, String description, int actionListBoxWidth, boolean closeBeforeAction) {
        super(title);
        
        if (description != null)
        	addComponent(new Label(description));
        
        this.closeBeforeAction = closeBeforeAction;
        this.actionListBox = new ActionListBox(new TerminalSize(actionListBoxWidth, 0));
        
        addComponent(actionListBox);
        Panel cancelPanel = new Panel(new Invisible(), Panel.Orientation.HORISONTAL);
        cancelPanel.addComponent(new Label("                "));
        cancelPanel.addComponent(new Button("Close", new Action() {
            public void doAction()
            {
                close();
            }
        }));
        addComponent(cancelPanel);
    }

    private void addAction(final String title, final Action action) {
        actionListBox.addAction(title, new Action() {
            @Override
            public void doAction() {
                if(closeBeforeAction) {
                    close();
                }
                action.doAction();
                if(!closeBeforeAction) {
                    close();
                }
            }
        });
        actionListBox.setPreferredSize(new TerminalSize(actionListBox.getPreferredSize().getColumns(), 
                                                        actionListBox.getPreferredSize().getRows() + 1));
    }

    /**
     * Will display a dialog prompting the user to select an action from a list.
     * The label of each action will be the result of calling toString() on each
     * Action object.
     */
    public static void showActionListDialog(GUIScreen owner, String title, String description, Action... actions)
    {
        int maxLength = 0;
        for(Action action: actions)
            if(action.toString().length() > maxLength)
                maxLength = action.toString().length();
        
        showActionListDialog(owner, title, description, maxLength, actions);
    }

    /**
     * Will display a dialog prompting the user to select an action from a list.
     * The label of each action will be the result of calling toString() on each
     * Action object.
     */
    public static void showActionListDialog(GUIScreen owner, String title, String description, int itemWidth, Action... actions)
    {
        showActionListDialog(owner, title, description, itemWidth, false, actions);
    }
    
    /**
     * Will display a dialog prompting the user to select an action from a list.
     * The label of each action will be the result of calling toString() on each
     * Action object.
     * @param owner Screen to display the dialog on
     * @param title Title of the dialog
     * @param description Description label inside the dialog
     * @param itemWidth Width of the labels in the list, this will effectively set how wide the dialog is
     * @param closeBeforeAction When an action in chosen, should the dialog be closed before the action is executed?
     * @param actions List of actions inside the dialog
     */
    public static void showActionListDialog(GUIScreen owner, String title, String description, int itemWidth, boolean closeBeforeAction, Action... actions)
    {
        //Autodetect width?
        if(itemWidth == 0) {
            showActionListDialog(owner, title, description, actions);
            return;
        }
        
        ActionListDialog actionListDialog = new ActionListDialog(title, description, itemWidth, closeBeforeAction);
        for(Action action: actions)
            actionListDialog.addAction(action.toString(), action);
        owner.showWindow(actionListDialog, GUIScreen.Position.CENTER);
    }
}
