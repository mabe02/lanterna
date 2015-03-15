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
 * Copyright (C) 2010-2014 Martin
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.ThemeDefinition;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

/**
 * Created by martin on 19/10/14.
 */
public class CheckBox extends AbstractInteractableComponent<CheckBox> {

    private String label;
    private boolean checked;

    public CheckBox(String label) {
        if(label == null) {
            throw new IllegalArgumentException("Cannot create a CheckBox with null label");
        }
        else if(label.contains("\n") || label.contains("\r")) {
            throw new IllegalArgumentException("Multiline checkbox labels are not supported");
        }
        this.label = label;
        this.checked = false;
    }

    public CheckBox setChecked(boolean checked) {
        this.checked = checked;
        invalidate();
        return this;
    }

    public boolean isChecked() {
        return checked;
    }

    @Override
    public Result handleKeyStroke(KeyStroke keyStroke) {
        if((keyStroke.getKeyType() == KeyType.Character && keyStroke.getCharacter() == ' ') ||
                keyStroke.getKeyType() == KeyType.Enter) {
            setChecked(!isChecked());
            return Result.HANDLED;
        }
        return super.handleKeyStroke(keyStroke);
    }

    public CheckBox setLabel(String label) {
        if(label == null) {
            throw new IllegalArgumentException("Cannot set CheckBox label to null");
        }
        this.label = label;
        invalidate();
        return this;
    }

    public String getLabel() {
        return label;
    }

    @Override
    protected CheckBoxRenderer createDefaultRenderer() {
        return new DefaultCheckBoxRenderer();
    }

    public static abstract class CheckBoxRenderer implements InteractableRenderer<CheckBox> {
    }

    public static class DefaultCheckBoxRenderer extends CheckBoxRenderer {
        private static final TerminalPosition CURSOR_LOCATION = new TerminalPosition(1, 0);
        @Override
        public TerminalPosition getCursorLocation(CheckBox component) {
            return CURSOR_LOCATION;
        }

        @Override
        public TerminalSize getPreferredSize(CheckBox component) {
            return new TerminalSize(4 + component.label.length(), 1);
        }

        @Override
        public void drawComponent(TextGUIGraphics graphics, CheckBox component) {
            ThemeDefinition themeDefinition = graphics.getThemeDefinition(CheckBox.class);
            if(component.isFocused()) {
                graphics.applyThemeStyle(themeDefinition.getActive());
            }
            else {
                graphics.applyThemeStyle(themeDefinition.getNormal());
            }

            graphics.fill(' ');
            graphics.putString(4, 0, component.label);

            String head = "[" + (component.isChecked() ? themeDefinition.getCharacter("MARKER", 'x') : " ") + "]";
            if(component.isFocused()) {
                graphics.applyThemeStyle(themeDefinition.getPreLight());
            }
            else {
                graphics.applyThemeStyle(themeDefinition.getNormal());
            }
            graphics.putString(0, 0, head);
        }
    }
}
