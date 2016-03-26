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
 * Copyright (C) 2010-2016 Martin
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalTextUtils;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.ThemeDefinition;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

/**
 * Simple labeled button with an optional action attached to it, you trigger the action by pressing the Enter key on the
 * keyboard when the component is in focus.
 * @author Martin
 */
public class Button extends AbstractInteractableComponent<Button> {
    private final Runnable action;
    private String label;

    /**
     * Creates a new button with a specific label and no attached action. Why would you need this? I have no idea.
     * @param label Label to put on the button
     */
    public Button(String label) {
        this(label, new Runnable() {
            @Override
            public void run() {
            }
        });
    }

    /**
     * Creates a new button with a label and an associated action to fire when triggered by the user
     * @param label Label to put on the button
     * @param action What action to fire when the user triggers the button by pressing the enter key
     */
    public Button(String label, Runnable action) {
        this.action = action;
        setLabel(label);
    }

    @Override
    protected ButtonRenderer createDefaultRenderer() {
        return new DefaultButtonRenderer();
    }

    @Override
    public synchronized TerminalPosition getCursorLocation() {
        return getRenderer().getCursorLocation(this);
    }

    @Override
    public synchronized Result handleKeyStroke(KeyStroke keyStroke) {
        if(keyStroke.getKeyType() == KeyType.Enter) {
            action.run();
            return Result.HANDLED;
        }
        return super.handleKeyStroke(keyStroke);
    }

    /**
     * Updates the label on the button to the specified string
     * @param label New label to use on the button
     */
    public final synchronized void setLabel(String label) {
        if(label == null) {
            throw new IllegalArgumentException("null label to a button is not allowed");
        }
        if(label.isEmpty()) {
            label = " ";
        }
        this.label = label;
        invalidate();
    }

    /**
     * Returns the label current assigned to the button
     * @return Label currently used by the button
     */
    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return "Button{" + label + "}";
    }

    /**
     * Helper interface that doesn't add any new methods but makes coding new button renderers a little bit more clear
     */
    public interface ButtonRenderer extends InteractableRenderer<Button> {
    }

    /**
     * This is the default button renderer that is used if you don't override anything. With this renderer, buttons are
     * drawn on a single line, with the label inside of "&lt;" and "&gt;".
     */
    public static class DefaultButtonRenderer implements ButtonRenderer {
        @Override
        public TerminalPosition getCursorLocation(Button button) {
            return new TerminalPosition(1 + getLabelShift(button, button.getSize()), 0);
        }

        @Override
        public TerminalSize getPreferredSize(Button button) {
            return new TerminalSize(Math.max(8, TerminalTextUtils.getColumnWidth(button.getLabel()) + 2), 1);
        }

        @Override
        public void drawComponent(TextGUIGraphics graphics, Button button) {
            if(button.isFocused()) {
                graphics.applyThemeStyle(getThemeDefinition(graphics).getActive());
            }
            else {
                graphics.applyThemeStyle(getThemeDefinition(graphics).getInsensitive());
            }
            graphics.fill(' ');
            graphics.setCharacter(0, 0, getThemeDefinition(graphics).getCharacter("LEFT_BORDER", '<'));
            graphics.setCharacter(graphics.getSize().getColumns() - 1, 0, getThemeDefinition(graphics).getCharacter("RIGHT_BORDER", '>'));

            if(button.isFocused()) {
                graphics.applyThemeStyle(getThemeDefinition(graphics).getActive());
            }
            else {
                graphics.applyThemeStyle(getThemeDefinition(graphics).getPreLight());
            }
            int labelShift = getLabelShift(button, graphics.getSize());
            graphics.setCharacter(1 + labelShift, 0, button.getLabel().charAt(0));

            if(TerminalTextUtils.getColumnWidth(button.getLabel()) == 1) {
                return;
            }
            if(button.isFocused()) {
                graphics.applyThemeStyle(getThemeDefinition(graphics).getSelected());
            }
            else {
                graphics.applyThemeStyle(getThemeDefinition(graphics).getNormal());
            }
            graphics.putString(1 + labelShift + 1, 0, button.getLabel().substring(1));
        }

        private int getLabelShift(Button button, TerminalSize size) {
            int availableSpace = size.getColumns() - 2;
            if(availableSpace <= 0) {
                return 0;
            }
            int labelShift = 0;
            int widthInColumns = TerminalTextUtils.getColumnWidth(button.getLabel());
            if(availableSpace > widthInColumns) {
                labelShift = (size.getColumns() - 2 - widthInColumns) / 2;
            }
            return labelShift;
        }
    }

    /**
     * Alternative button renderer that displays buttons with just the label and minimal decoration
     */
    public static class FlatButtonRenderer implements ButtonRenderer {
        @Override
        public TerminalPosition getCursorLocation(Button component) {
            return null;
        }

        @Override
        public TerminalSize getPreferredSize(Button component) {
            return new TerminalSize(TerminalTextUtils.getColumnWidth(component.getLabel()), 1);
        }

        @Override
        public void drawComponent(TextGUIGraphics graphics, Button button) {
            if(button.isFocused()) {
                graphics.applyThemeStyle(getThemeDefinition(graphics).getActive());
            }
            else {
                graphics.applyThemeStyle(getThemeDefinition(graphics).getInsensitive());
            }
            graphics.fill(' ');
            if(button.isFocused()) {
                graphics.applyThemeStyle(getThemeDefinition(graphics).getSelected());
            }
            else {
                graphics.applyThemeStyle(getThemeDefinition(graphics).getNormal());
            }
            graphics.putString(0, 0, button.getLabel());
        }
    }

    private static ThemeDefinition getThemeDefinition(TextGUIGraphics graphics) {
        return graphics.getThemeDefinition(Button.class);
    }
}
