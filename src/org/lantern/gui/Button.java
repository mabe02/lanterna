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
 * Copyright (C) 2010-2011 mabe02
 */

package org.lantern.gui;

import org.lantern.LanternException;
import org.lantern.gui.theme.Theme;
import org.lantern.input.Key;
import org.lantern.terminal.Terminal;
import org.lantern.terminal.TerminalPosition;
import org.lantern.terminal.TerminalSize;

/**
 *
 * @author mabe02
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
            graphics.drawString(0, 0, "< ", new Terminal.Style[]{});
            graphics.drawString(graphics.getWidth() - 2, 0, " >", new Terminal.Style[]{});
            TextGraphics subGraphics = graphics.subAreaGraphics(new TerminalPosition(2, 0),
                    new TerminalSize(allowedSize, buttonLabel.getPreferredSize().getRows()));
            buttonLabel.repaint(subGraphics);
            setHotspot(graphics.translateToGlobalCoordinates(new TerminalPosition(2, 0)));
        }
        else {
            int leftPosition = (graphics.getWidth() - preferredSize.getColumns()) / 2;
            graphics.drawString(leftPosition, 0, "< ", new Terminal.Style[]{});
            final TerminalSize labelPrefSize = buttonLabel.getPreferredSize();
            TextGraphics subGraphics = graphics.subAreaGraphics(
                    new TerminalPosition(leftPosition + 2, 0),
                    new TerminalSize(labelPrefSize.getColumns(), labelPrefSize.getRows()));
            buttonLabel.repaint(subGraphics);
            graphics.drawString(leftPosition + 2 + labelPrefSize.getColumns(), 0, " >", new Terminal.Style[]{});

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

    public void afterEnteredFocus(FocusChangeDirection direction) {
        buttonLabel.setStyle(Theme.Category.ButtonLabelActive);
    }

    public void afterLeftFocus(FocusChangeDirection direction) {
        buttonLabel.setStyle(Theme.Category.ButtonLabelInactive);
    }

    public void keyboardInteraction(Key key, InteractableResult result) throws LanternException
    {
        switch(key.getKind().getIndex()) {
            case Key.Kind.Enter_ID:
                onPressEvent.doAction();
                break;

            case Key.Kind.ArrowRight_ID:
            case Key.Kind.ArrowDown_ID:
            case Key.Kind.Tab_ID:
                result.type = Result.NEXT_INTERACTABLE;
                break;

            case Key.Kind.ArrowLeft_ID:
            case Key.Kind.ArrowUp_ID:
            case Key.Kind.ReverseTab_ID:
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
