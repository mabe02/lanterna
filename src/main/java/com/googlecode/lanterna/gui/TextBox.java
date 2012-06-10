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

package com.googlecode.lanterna.gui;

import com.googlecode.lanterna.gui.Theme.Category;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;

/**
 *
 * @author Martin
 */
public class TextBox extends AbstractInteractableComponent
{
    private final int forceWidth;
    private String backend;
    private int editPosition;
    private int visibleLeftPosition;
    private int lastKnownWidth;
    
    public TextBox()
    {
        this("");
    }
    
    public TextBox(String initialContent)
    {
        this(-1, initialContent);
    }

    public TextBox(int forceWidth, String initialContent)
    {
        if(initialContent == null)
            initialContent = "";
        
        this.forceWidth = forceWidth;
        this.backend = initialContent;
        this.editPosition = initialContent.length();
        this.visibleLeftPosition = 0;
        this.lastKnownWidth = 0;
    }

    public String getText()
    {
        return backend;
    }

    public void setText(String text)
    {
        backend = text;
        editPosition = backend.length();
        invalidate();
    }

    public void setEditPosition(int editPosition)
    {
        if(editPosition < 0)
            editPosition = 0;
        if(editPosition > backend.length())
            editPosition = backend.length();
        
        this.editPosition = editPosition;
        invalidate();
    }

    protected String prerenderTransformation(String textboxString)
    {
        return textboxString;
    }

    public void repaint(TextGraphics graphics)
    {
        if(hasFocus())
            graphics.applyThemeItem(Category.TextBoxFocused);
        else
            graphics.applyThemeItem(Category.TextBox);

        graphics.fillArea(' ');
        String displayString = prerenderTransformation(backend).substring(visibleLeftPosition);
        if(displayString.length() > graphics.getWidth())
            displayString = displayString.substring(0, graphics.getWidth()-1);
        graphics.drawString(0, 0, displayString);
        setHotspot(graphics.translateToGlobalCoordinates(new TerminalPosition(editPosition - visibleLeftPosition, 0)));
        lastKnownWidth = graphics.getWidth();
    }

    public TerminalSize getPreferredSize()
    {
        return new TerminalSize(forceWidth, 1);
    }

    public void keyboardInteraction(Key key, InteractableResult result)
    {
        switch(key.getKind()) {
            case Tab:
            case ArrowDown:
            case Enter:
                result.type = Result.NEXT_INTERACTABLE;
                break;

            case ReverseTab:
            case ArrowUp:
                result.type = Result.PREVIOUS_INTERACTABLE;
                break;

            case ArrowRight:
                if(editPosition == backend.length())
                    break;
                editPosition++;
                if(editPosition - visibleLeftPosition >= lastKnownWidth)
                    visibleLeftPosition++;
                break;

            case ArrowLeft:
                if(editPosition == 0)
                    break;
                editPosition--;
                if(editPosition - visibleLeftPosition < 0)
                    visibleLeftPosition--;
                break;

            case End:
                editPosition = backend.length();
                if(editPosition - visibleLeftPosition >= lastKnownWidth)
                    visibleLeftPosition = editPosition - lastKnownWidth + 1;
                break;

            case Home:
                editPosition = 0;
                visibleLeftPosition = 0;
                break;

            case Delete:
                if(editPosition == backend.length())
                    break;
                backend = backend.substring(0, editPosition) + backend.substring(editPosition + 1);
                break;

            case Backspace:
                if(editPosition == 0)
                    break;
                editPosition--;
                if(editPosition - visibleLeftPosition < 0)
                    visibleLeftPosition--;
                backend = backend.substring(0, editPosition) + backend.substring(editPosition + 1);
                break;

            case NormalKey:
                //Add character
                if(Character.isISOControl(key.getCharacter()) || key.getCharacter() > 127)
                    break;

                backend = backend.substring(0, editPosition) + (char)key.getCharacter() + backend.substring(editPosition);
                editPosition++;
                if(editPosition - visibleLeftPosition >= lastKnownWidth)
                    visibleLeftPosition++;
                break;
        }
        invalidate();
    }
}
