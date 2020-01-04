/*
 * This file is part of lanterna (https://github.com/mabe02/lanterna).
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
 * Copyright (C) 2010-2020 Martin Berglund
 */
package com.googlecode.lanterna.gui2.menu;

import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TerminalTextUtils;
import com.googlecode.lanterna.graphics.ThemeDefinition;
import com.googlecode.lanterna.gui2.AbstractInteractableComponent;
import com.googlecode.lanterna.gui2.BasePane;
import com.googlecode.lanterna.gui2.InteractableRenderer;
import com.googlecode.lanterna.gui2.TextGUIGraphics;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

/**
 * This class is a single item that appears in a {@link Menu} with an optional action attached to it
 */
public class MenuItem extends AbstractInteractableComponent<MenuItem> {
    private String label;
    private final Runnable action;

    /**
     * Creates a {@link MenuItem} with a label that does nothing when activated
     * @param label Label of the new {@link MenuItem}
     */
    public MenuItem(String label) {
        this(label, () -> {
        });
    }

    /**
     * Creates a new {@link MenuItem} with a label and an action that will run on the GUI thread when activated. When
     * the action has finished, the {@link Menu} containing this item will close.
     * @param label Label of the new {@link MenuItem}
     * @param action Action to invoke on the GUI thread when the menu item is activated
     */
    public MenuItem(String label, Runnable action) {
        this.action = action;
        if (label == null || label.trim().isEmpty()) {
            throw new IllegalArgumentException("Menu label is not allowed to be null or empty");
        }
        this.label = label.trim();
    }

    /**
     * Returns the label of this menu item
     * @return Label of this menu item
     */
    public String getLabel() {
        return label;
    }

    @Override
    protected InteractableRenderer<MenuItem> createDefaultRenderer() {
        return new DefaultMenuItemRenderer();
    }

    /**
     * Method to invoke when a menu item is "activated" by pressing the Enter key.
     * @return Returns {@code true} if the action was performed successfully, otherwise {@code false}, which will not
     * automatically close the popup window itself.
     */
    protected boolean onActivated() {
        action.run();
        return true;
    }

    @Override
    protected Result handleKeyStroke(KeyStroke keyStroke) {
        Result result;
        if (keyStroke.getKeyType() == KeyType.Enter) {
            if (onActivated()) {
                BasePane basePane = getBasePane();
                if (basePane instanceof Window && ((Window) basePane).getHints().contains(Window.Hint.MENU_POPUP)) {
                    ((Window) basePane).close();
                }
            }
            result = Result.HANDLED;
        }
        else {
            result = super.handleKeyStroke(keyStroke);
        }
        return result;
    }

    /**
     * Helper interface that doesn't add any new methods but makes coding new menu renderers a little bit more clear
     */
    public static abstract class MenuItemRenderer implements InteractableRenderer<MenuItem> {
    }

    /**
     * Default renderer for menu items (both sub-menus and regular items)
     */
    public static class DefaultMenuItemRenderer extends MenuItemRenderer {
        @Override
        public TerminalPosition getCursorLocation(MenuItem component) {
            return null;
        }

        @Override
        public TerminalSize getPreferredSize(MenuItem component) {
            int preferredWidth = TerminalTextUtils.getColumnWidth(component.getLabel()) + 2;
            if (component instanceof Menu && !(component.getParent() instanceof MenuBar)) {
                preferredWidth += 2;
            }
            return TerminalSize.ONE.withColumns(preferredWidth);
        }

        @Override
        public void drawComponent(TextGUIGraphics graphics, MenuItem menuItem) {
            ThemeDefinition themeDefinition = menuItem.getThemeDefinition();
            if (menuItem.isFocused()) {
                graphics.applyThemeStyle(themeDefinition.getSelected());
            }
            else {
                graphics.applyThemeStyle(themeDefinition.getNormal());
            }

            final String label = menuItem.getLabel();
            final String leadingCharacter = label.substring(0, 1);

            graphics.fill(' ');
            graphics.putString(1, 0, label);
            if (menuItem instanceof Menu && !(menuItem.getParent() instanceof MenuBar)) {
                graphics.putString(graphics.getSize().getColumns() - 2, 0, String.valueOf(Symbols.TRIANGLE_RIGHT_POINTING_BLACK));
            }
            if (!label.isEmpty()) {
                if (menuItem.isFocused()) {
                    graphics.applyThemeStyle(themeDefinition.getActive());
                }
                else {
                    graphics.applyThemeStyle(themeDefinition.getPreLight());
                }
                graphics.putString(1, 0, leadingCharacter);
            }
        }
    }
}
