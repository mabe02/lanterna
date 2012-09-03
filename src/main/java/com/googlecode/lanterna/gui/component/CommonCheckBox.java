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

import com.googlecode.lanterna.gui.Interactable;
import com.googlecode.lanterna.gui.TextGraphics;
import com.googlecode.lanterna.gui.Theme.Category;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;

/**
 *
 * @author Martin
 */
public abstract class CommonCheckBox extends AbstractInteractableComponent
{
    private final Label label;

    public CommonCheckBox(final String label)
    {
        this.label = new Label(label);
    }

    @Override
    protected TerminalSize calculatePreferredSize() {
        TerminalSize labelSize = label.getPreferredSize();
        labelSize.setColumns(labelSize.getColumns() + 4);
        return labelSize;
    }

    @Override
    public void repaint(TextGraphics graphics)
    {
        graphics.applyTheme(Category.CHECKBOX);

        if(hasFocus())
            graphics.applyTheme(Category.CHECKBOX_SELECTED);
        else
            graphics.applyTheme(Category.CHECKBOX);


        char check = ' ';
        if(isSelected())
            check = getSelectionCharacter();

        graphics.drawString(0, 0, surroundCharacter(check));
        graphics.applyTheme(Category.CHECKBOX);
        graphics.drawString(3, 0, " ");
        TextGraphics subArea = graphics.subAreaGraphics(new TerminalPosition(4, 0));
        label.repaint(subArea);

        setHotspot(graphics.translateToGlobalCoordinates(new TerminalPosition(1, 0)));
    }

    @Override
    public Interactable.Result keyboardInteraction(Key key)
    {
        try {
            switch(key.getKind())
            {
                case ArrowDown:
                    return Result.NEXT_INTERACTABLE_DOWN;
                    
                case Tab:
                case ArrowRight:
                    return Result.NEXT_INTERACTABLE_RIGHT;

                case ArrowUp:
                    return Result.PREVIOUS_INTERACTABLE_UP;
                    
                case ReverseTab:
                case ArrowLeft:
                    return Result.PREVIOUS_INTERACTABLE_LEFT;

                case Enter:
                    onActivated();
                    return Result.EVENT_HANDLED;

                default:
                    if(key.getCharacter() == ' ' || key.getCharacter() == 'x') {
                        onActivated();
                        return Result.EVENT_HANDLED;
                    }
                    return Result.EVENT_NOT_HANDLED;
            }
        }
        finally {
            invalidate();
        }
    }

    public abstract boolean isSelected();
    protected abstract char getSelectionCharacter();
    protected abstract String surroundCharacter(char character);
    protected abstract void onActivated();

    public void select()
    {
        onActivated();
    }
}
