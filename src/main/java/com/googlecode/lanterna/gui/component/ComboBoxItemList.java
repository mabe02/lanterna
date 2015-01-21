/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.lanterna.gui.component;

import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.Theme;
import com.googlecode.lanterna.input.Key;

/**
 *
 * @author maslbl4
 */
public class ComboBoxItemList extends AbstractListBox {

    Action selectedAction;
     /**
     * Adds an action to the list, using toString() of the action as a label
     * @param action Action to be performed when the user presses enter key
     */
    public void setSelectedAction(final Action action) {
        selectedAction = action;
    }

    
    @Override
    protected String createItemString(int i) {
        return getItemAt(i).toString();
    }

    @Override
    public void addItem(Object item) {
        super.addItem(item);
    }

    @Override
    protected Result unhandledKeyboardEvent(Key key) {
        if (key.getKind() == Key.Kind.Enter) {
            selectedAction.doAction();
            return Result.EVENT_HANDLED;
        }
      
        return Result.EVENT_NOT_HANDLED;
    }

    @Override
    protected Theme.Definition getListItemThemeDefinition(Theme theme) {
        return theme.getDefinition(Theme.Category.DIALOG_AREA);
    }

    @Override
    protected Theme.Definition getSelectedListItemThemeDefinition(Theme theme) {
        return theme.getDefinition(Theme.Category.TEXTBOX_FOCUSED);
    }

    
}
