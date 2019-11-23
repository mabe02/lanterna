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

public class MenuItem extends AbstractInteractableComponent<MenuItem> {
    private String label;
    private final Runnable action;

    public MenuItem(String label) {
        this(label, new Runnable() {
            @Override
            public void run() {
            }
        });
    }

    public MenuItem(String label, Runnable action) {
        this.action = action;
        if (label == null || label.trim().isEmpty()) {
            throw new IllegalArgumentException("Menu label is not allowed to be null or empty");
        }
        this.label = label.trim();
    }

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
