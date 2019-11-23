package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.gui2.menu.MenuItem;

import java.util.Arrays;

/**
 * This class is a {@link Window} implementation that automatically sets some common settings that you'd want on
 * specifically popup windows with menu items. It ensures that the window is modal and has a fixed position (rather than
 * letting the window manager choose).
 */
public class MenuPopupWindow extends AbstractWindow {
    private final Panel menuItemPanel;

    /**
     * Creates a new popup window with a relative position to the component supplied.
     * @param parent Component that this popup menu is triggered from
     */
    public MenuPopupWindow(Component parent) {
        setHints(Arrays.asList(Hint.MODAL, Hint.MENU_POPUP, Hint.FIXED_POSITION));
        if (parent != null) {
            TerminalPosition menuPositionGlobal = parent.toGlobal(TerminalPosition.TOP_LEFT_CORNER);
            setPosition(menuPositionGlobal.withRelative(0, 1));
        }
        menuItemPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        setComponent(menuItemPanel);
    }

    /**
     * Adds a new menu item to this popup window. The item will automatically be selected if it's the first one added.
     * @param menuItem Menu item to add to the popup window.
     */
    public void addMenuItem(MenuItem menuItem) {
        menuItemPanel.addComponent(menuItem);
        menuItem.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));
        if (menuItemPanel.getChildCount() == 1) {
            setFocusedInteractable(menuItem);
        }
        invalidate();
    }
}
