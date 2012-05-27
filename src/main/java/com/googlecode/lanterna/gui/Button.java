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

import com.googlecode.lanterna.gui.theme.Theme;
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
        this.buttonLabel.setStyle(Theme.Category.ButtonLabelInactive);

        if(this.onPressEvent == null)
            this.onPressEvent = new Action() { public void doAction() {} };
    }

    public void repaint(TextGraphics graphics)
    {
        if(hasFocus())
            graphics.applyThemeItem(graphics.getTheme().getItem(Theme.Category.ButtonActive));
        else
            graphics.applyThemeItem(graphics.getTheme().getItem(Theme.Category.ButtonInactive));

        TerminalSize preferredSize = getPreferredSize();

        if(graphics.getWidth() < preferredSize.getColumns()) {
            int allowedSize = graphics.getWidth() - 4;
            graphics.drawString(0, 0, "< ");
            graphics.drawString(graphics.getWidth() - 2, 0, " >");
            TextGraphics subGraphics = graphics.subAreaGraphics(new TerminalPosition(2, 0),
                    new TerminalSize(allowedSize, buttonLabel.getPreferredSize().getRows()));
            buttonLabel.repaint(subGraphics);
            setHotspot(graphics.translateToGlobalCoordinates(new TerminalPosition(2, 0)));
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

            setHotspot(graphics.translateToGlobalCoordinates(new TerminalPosition(leftPosition + 2, 0)));
        }
    }

    public void setText(String text)
    {
        this.buttonLabel.setText(text);
    }

    public String getText()
    {
        return buttonLabel.getText();
    }

    public TerminalSize getPreferredSize()
    {
        TerminalSize labelSize = buttonLabel.getPreferredSize();
        return new TerminalSize(labelSize.getColumns() + 2 + 2, labelSize.getRows());
    }

    @Override
    public void afterEnteredFocus(FocusChangeDirection direction) {
        buttonLabel.setStyle(Theme.Category.ButtonLabelActive);
    }

    @Override
    public void afterLeftFocus(FocusChangeDirection direction) {
        buttonLabel.setStyle(Theme.Category.ButtonLabelInactive);
    }

    public void keyboardInteraction(Key key, InteractableResult result)
    {
        switch(key.getKind()) {
            case Enter:
                onPressEvent.doAction();
                break;

            case ArrowRight:
            case ArrowDown:
            case Tab:
                result.type = Result.NEXT_INTERACTABLE;
                break;

            case ArrowLeft:
            case ArrowUp:
            case ReverseTab:
                result.type = Result.PREVIOUS_INTERACTABLE;
                break;
        }
    }

    /*
    public void keyPressed(int character)
    {
        if(character == '\r')
            onPressEvent.doAction();
        else if(character == '\t')
            getEventReciever().onEvent(InteractionEvent.NEXT_INTERACTABLE);
        else if(character == CKey.BTAB())
            getEventReciever().onEvent(InteractionEvent.PREVIOUS_INTERACTABLE);
        else if(character == CKey.RIGHT() || character == CKey.DOWN())
            getEventReciever().onEvent(InteractionEvent.NEXT_INTERACTABLE);
        else if(character == CKey.LEFT() || character == CKey.UP())
            getEventReciever().onEvent(InteractionEvent.PREVIOUS_INTERACTABLE);
    }
     */
}
