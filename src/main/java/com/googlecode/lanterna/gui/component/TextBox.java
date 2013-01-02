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

import com.googlecode.lanterna.gui.TextGraphics;
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
    
    /**
     * Creates a text box component, where the user can enter text by typing on
     * the keyboard. It will be empty and 10 columns wide.
     */
    public TextBox()
    {
        this("");
    }   

    /**
     * Creates a text box component, where the user can enter text by typing on
     * the keyboard. The width of the text box will be calculated from the size
     * of the initial content, passed in as the first argument. The text box 
     * will at least be 10 columns wide, regardless of the content though.
     * @param initialContent Initial text content of the text box
     */
    public TextBox(String initialContent)
    {
        this(initialContent, 0);
    }

    /**
     * Creates a text box component, where the user can enter text by typing on
     * the keyboard. 
     * @param initialContent Initial text content of the text box
     * @param width Width, in columns, of the text box. Set to 0 if you 
     * want to autodetect the width based on initial content (will be set to 
     * 10 columns in width at minimum)
     */
    public TextBox(String initialContent, int width)
    {
        if(initialContent == null)
            initialContent = "";
        if(width <= 0) {
            if(initialContent.length() > 10)
                width = initialContent.length();
            else
                width = 10;
        }
        
        this.forceWidth = width;
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

    public int getEditPosition() {
        return editPosition;
    }

    protected String prerenderTransformation(String textboxString)
    {
        return textboxString;
    }

    @Override
    public void repaint(TextGraphics graphics)
    {
        if(hasFocus())
            graphics.applyTheme(Category.TEXTBOX_FOCUSED);
        else
            graphics.applyTheme(Category.TEXTBOX);

        graphics.fillArea(' ');
        String displayString = prerenderTransformation(backend).substring(visibleLeftPosition);
        if(displayString.length() > graphics.getWidth())
            displayString = displayString.substring(0, graphics.getWidth()-1);
        graphics.drawString(0, 0, displayString);
        setHotspot(graphics.translateToGlobalCoordinates(new TerminalPosition(editPosition - visibleLeftPosition, 0)));
        lastKnownWidth = graphics.getWidth();
    }

    @Override
    protected TerminalSize calculatePreferredSize() {
        return new TerminalSize(forceWidth, 1);
    }

    @Override
    public Result keyboardInteraction(Key key)
    {
        try {
            switch(key.getKind()) {
                case Tab:
                case Enter:
                    return Result.NEXT_INTERACTABLE_RIGHT;
                    
                case ArrowDown:
                    return Result.NEXT_INTERACTABLE_DOWN;

                case ReverseTab:
                    return Result.PREVIOUS_INTERACTABLE_LEFT;
                    
                case ArrowUp:
                    return Result.PREVIOUS_INTERACTABLE_UP;

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
                    
                default:
                    return Result.EVENT_NOT_HANDLED;
            }
            return Result.EVENT_HANDLED;
        }
        finally {
            invalidate();
        }
    }
}
