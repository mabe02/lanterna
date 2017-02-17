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
 * Copyright (C) 2010-2017 Martin Berglund
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalTextUtils;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.ThemeDefinition;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The checkbox component looks like a regular checkbox that you can find in modern graphics user interfaces, a label
 * and a space that the user can toggle on and off by using enter or space keys.
 *
 * @author Martin
 */
public class CheckBox extends AbstractInteractableComponent<CheckBox> {

    /**
     * Listener interface that can be used to catch user events on the check box
     */
    public interface Listener {
        /**
         * This is fired when the user has altered the checked state of this {@code CheckBox}
         * @param checked If the {@code CheckBox} is now toggled on, this is set to {@code true}, otherwise
         * {@code false}
         */
        void onStatusChanged(boolean checked);
    }

    private final List<Listener> listeners;
    private String label;
    private boolean checked;

    /**
     * Creates a new checkbox with no label, initially set to un-checked
     */
    public CheckBox() {
        this("");
    }

    /**
     * Creates a new checkbox with a specific label, initially set to un-checked
     * @param label Label to assign to the check box
     */
    public CheckBox(String label) {
        if(label == null) {
            throw new IllegalArgumentException("Cannot create a CheckBox with null label");
        }
        else if(label.contains("\n") || label.contains("\r")) {
            throw new IllegalArgumentException("Multiline checkbox labels are not supported");
        }
        this.listeners = new CopyOnWriteArrayList<Listener>();
        this.label = label;
        this.checked = false;
    }

    /**
     * Programmatically updated the check box to a particular checked state
     * @param checked If {@code true}, the check box will be set to toggled on, otherwise {@code false}
     * @return Itself
     */
    public synchronized CheckBox setChecked(final boolean checked) {
        this.checked = checked;
        runOnGUIThreadIfExistsOtherwiseRunDirect(new Runnable() {
            @Override
            public void run() {
                for(Listener listener : listeners) {
                    listener.onStatusChanged(checked);
                }
            }
        });
        invalidate();
        return this;
    }

    /**
     * Returns the checked state of this check box
     * @return {@code true} if the check box is toggled on, otherwise {@code false}
     */
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

    /**
     * Updates the label of the checkbox
     * @param label New label to assign to the check box
     * @return Itself
     */
    public synchronized CheckBox setLabel(String label) {
        if(label == null) {
            throw new IllegalArgumentException("Cannot set CheckBox label to null");
        }
        this.label = label;
        invalidate();
        return this;
    }

    /**
     * Returns the label of check box
     * @return Label currently assigned to the check box
     */
    public String getLabel() {
        return label;
    }

    /**
     * Adds a listener to this check box so that it will be notificed on certain user actions
     * @param listener Listener to fire events on
     * @return Itself
     */
    public CheckBox addListener(Listener listener) {
        if(listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
        return this;
    }

    /**
     * Removes a listener from this check box so that, if it was previously added, it will no long receive any events
     * @param listener Listener to remove from the check box
     * @return Itself
     */
    public CheckBox removeListener(Listener listener) {
        listeners.remove(listener);
        return this;
    }

    @Override
    protected CheckBoxRenderer createDefaultRenderer() {
        return new DefaultCheckBoxRenderer();
    }

    /**
     * Helper interface that doesn't add any new methods but makes coding new check box renderers a little bit more clear
     */
    public static abstract class CheckBoxRenderer implements InteractableRenderer<CheckBox> {
    }

    /**
     * The default renderer that is used unless overridden. This renderer will draw the checkbox label on the right side
     * of a "[ ]" block which will contain a "X" inside it if the check box has toggle status on
     */
    public static class DefaultCheckBoxRenderer extends CheckBoxRenderer {
        private static final TerminalPosition CURSOR_LOCATION = new TerminalPosition(1, 0);
        @Override
        public TerminalPosition getCursorLocation(CheckBox component) {
            if(component.getThemeDefinition().isCursorVisible()) {
                return CURSOR_LOCATION;
            }
            else {
                return null;
            }
        }

        @Override
        public TerminalSize getPreferredSize(CheckBox component) {
            int width = 3;
            if(!component.label.isEmpty()) {
                width += 1 + TerminalTextUtils.getColumnWidth(component.label);
            }
            return new TerminalSize(width, 1);
        }

        @Override
        public void drawComponent(TextGUIGraphics graphics, CheckBox component) {
            ThemeDefinition themeDefinition = component.getThemeDefinition();
            if(component.isFocused()) {
                graphics.applyThemeStyle(themeDefinition.getActive());
            }
            else {
                graphics.applyThemeStyle(themeDefinition.getNormal());
            }

            graphics.fill(' ');
            graphics.putString(4, 0, component.label);

            if(component.isFocused()) {
                graphics.applyThemeStyle(themeDefinition.getPreLight());
            }
            else {
                graphics.applyThemeStyle(themeDefinition.getInsensitive());
            }
            graphics.setCharacter(0, 0, themeDefinition.getCharacter("LEFT_BRACKET", '['));
            graphics.setCharacter(2, 0, themeDefinition.getCharacter("RIGHT_BRACKET", ']'));
            graphics.setCharacter(3, 0, ' ');

            if(component.isFocused()) {
                graphics.applyThemeStyle(themeDefinition.getSelected());
            }
            else {
                graphics.applyThemeStyle(themeDefinition.getNormal());
            }
            graphics.setCharacter(1, 0, (component.isChecked() ? themeDefinition.getCharacter("MARKER", 'x') : ' '));
        }
    }
}
