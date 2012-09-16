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

import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.Interactable;
import com.googlecode.lanterna.gui.TextGraphics;
import com.googlecode.lanterna.gui.Theme;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;

/**
 *
 * @author Martin
 */
public class Button extends AbstractInteractableComponent
{
    private Label buttonLabel;
    private Action onPressEvent;

    public Button(String text) {
        this(text, new Action() { public void doAction() {} });
    }

    public Button(String text, Action onPressEvent) {
        this.onPressEvent = onPressEvent;
        this.buttonLabel = new Label(text);
        this.buttonLabel.setStyle(Theme.Category.BUTTON_LABEL_INACTIVE);

        if(this.onPressEvent == null)
            this.onPressEvent = new Action() { public void doAction() {} };
    }

    @Override
    public void repaint(TextGraphics graphics)
    {
        if(hasFocus())
            graphics.applyTheme(graphics.getTheme().getDefinition(Theme.Category.BUTTON_ACTIVE));
        else
            graphics.applyTheme(graphics.getTheme().getDefinition(Theme.Category.BUTTON_INACTIVE));
        
        TerminalSize preferredSize = calculatePreferredSize();
        graphics = transformAccordingToAlignment(graphics, preferredSize);

        if(graphics.getWidth() < preferredSize.getColumns()) {
            int allowedSize = graphics.getWidth() - 4;
            graphics.drawString(0, 0, "< ");
            graphics.drawString(graphics.getWidth() - 2, 0, " >");
            TextGraphics subGraphics = graphics.subAreaGraphics(new TerminalPosition(2, 0),
                    new TerminalSize(allowedSize, buttonLabel.getPreferredSize().getRows()));
            buttonLabel.repaint(subGraphics);
        }
        else {
            int leftPosition = (graphics.getWidth() - preferredSize.getColumns()) / 2;
            graphics.drawString(leftPosition, 0, "< ");
            final TerminalSize labelPrefSize = buttonLabel.getPreferredSize();
            TextGraphics subGraphics = graphics.subAreaGraphics(
                    new TerminalPosition(leftPosition + 2, 0),
                    new TerminalSize(labelPrefSize.getColumns(), labelPrefSize.getRows()));
            buttonLabel.repaint(subGraphics);
            graphics.drawString(leftPosition + 2 + labelPrefSize.getColumns(), 0, " >");
        }    
        setHotspot(null);
    }

    public void setText(String text)
    {
        this.buttonLabel.setText(text);
    }

    public String getText()
    {
        return buttonLabel.getText();
    }

    @Override
    protected TerminalSize calculatePreferredSize() {
        TerminalSize labelSize = buttonLabel.getPreferredSize();
        return new TerminalSize(labelSize.getColumns() + 2 + 2, labelSize.getRows());
    }

    @Override
    public void afterEnteredFocus(FocusChangeDirection direction) {
        buttonLabel.setStyle(Theme.Category.BUTTON_LABEL_ACTIVE);
    }

    @Override
    public void afterLeftFocus(FocusChangeDirection direction) {
        buttonLabel.setStyle(Theme.Category.BUTTON_LABEL_INACTIVE);
    }

    @Override
    public Interactable.Result keyboardInteraction(Key key)
    {
        switch(key.getKind()) {
            case Enter:
                onPressEvent.doAction();
                return Result.EVENT_HANDLED;
                
            case ArrowDown:
                return Result.NEXT_INTERACTABLE_DOWN;
                
            case ArrowRight:
            case Tab:
                return Result.NEXT_INTERACTABLE_RIGHT;

            case ArrowUp:
                return Result.PREVIOUS_INTERACTABLE_UP;
                
            case ArrowLeft:
            case ReverseTab:
                return Result.PREVIOUS_INTERACTABLE_LEFT;
                
            default:
                return Result.EVENT_NOT_HANDLED;
        }
    }
}
