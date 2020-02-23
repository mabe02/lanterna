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
 * Copyright (C) 2017 Bruno Eberhard
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */
package com.googlecode.lanterna.gui2.menu;

import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.input.KeyStroke;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A menu bar offering drop-down menus. You can attach a menu bar to a {@link Window} by using the
 * {@link Window#setMenuBar(MenuBar)} method, then use {@link MenuBar#add(Menu)} to add sub-menus to the menu bar.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @author Bruno Eberhard
 * @author Martin Berglund
 */
@SuppressWarnings("SuspiciousMethodCalls")
public class MenuBar extends AbstractComponent<MenuBar> implements Container {
    private static final int EXTRA_PADDING = 0;
    private final List<Menu> menus;

    /**
     * Creates a new menu bar
     */
    public MenuBar() {
        this.menus = new CopyOnWriteArrayList<>();
    }

    /**
     * Adds a new drop-down menu to the menu bar, at the end
     * @param menu Menu to add to the menu bar
     * @return Itself
     */
    public MenuBar add(Menu menu) {
        menus.add(menu);
        menu.onAdded(this);
        return this;
    }

    @Override
    public int getChildCount() {
        return getMenuCount();
    }

    @Override
    public List<Component> getChildrenList() {
        return new ArrayList<>(menus);
    }

    @Override
    public Collection<Component> getChildren() {
        return getChildrenList();
    }

    @Override
    public boolean containsComponent(Component component) {
        return menus.contains(component);
    }

    @Override
    public synchronized boolean removeComponent(Component component) {
        boolean hadMenu = menus.remove(component);
        if (hadMenu) {
            component.onRemoved(this);
        }
        return hadMenu;
    }

    @Override
    public synchronized Interactable nextFocus(Interactable fromThis) {
        if (menus.isEmpty()) {
            return null;
        }
        else if (fromThis == null) {
            return menus.get(0);
        }
        else if (!menus.contains(fromThis) || menus.indexOf(fromThis) == menus.size() - 1) {
            return null;
        }
        else {
            return menus.get(menus.indexOf(fromThis) + 1);
        }
    }

    @Override
    public Interactable previousFocus(Interactable fromThis) {
        if (menus.isEmpty()) {
            return null;
        }
        else if (fromThis == null) {
            return menus.get(menus.size() - 1);
        }
        else if (!menus.contains(fromThis) || menus.indexOf(fromThis) == 0) {
            return null;
        }
        else {
            return menus.get(menus.indexOf(fromThis) - 1);
        }
    }

    @Override
    public boolean handleInput(KeyStroke key) {
        return false;
    }

    /**
     * Returns the drop-down menu at the specified index. This method will throw an Array
     * @param index Index of the menu to return
     * @return The drop-down menu at the specified index
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public Menu getMenu(int index) {
        return menus.get(index);
    }

    /**
     * Returns the number of menus this menu bar currently has
     * @return The number of menus this menu bar currently has
     */
    public int getMenuCount() {
        return menus.size();
    }

    @Override
    protected ComponentRenderer<MenuBar> createDefaultRenderer() {
        return new DefaultMenuBarRenderer();
    }

    @Override
    public synchronized void updateLookupMap(InteractableLookupMap interactableLookupMap) {
        for (Menu menu: menus) {
            interactableLookupMap.add(menu);
        }
    }

    @Override
    public TerminalPosition toBasePane(TerminalPosition position) {
        // Assume the menu is always at the top of the content panel
        return position;
    }

    /**
     * The default implementation for rendering a {@link MenuBar}
     */
    public class DefaultMenuBarRenderer implements ComponentRenderer<MenuBar> {
        @Override
        public TerminalSize getPreferredSize(MenuBar menuBar) {
            int maxHeight = 1;
            int totalWidth = EXTRA_PADDING;
            for (int i = 0; i < menuBar.getMenuCount(); i++) {
                Menu menu = menuBar.getMenu(i);
                TerminalSize preferredSize = menu.getPreferredSize();
                maxHeight = Math.max(maxHeight, preferredSize.getRows());
                totalWidth += preferredSize.getColumns();
            }
            totalWidth += EXTRA_PADDING;
            return new TerminalSize(totalWidth, maxHeight);
        }

        @Override
        public void drawComponent(TextGUIGraphics graphics, MenuBar menuBar) {
            // Reset the area
            graphics.applyThemeStyle(getThemeDefinition().getNormal());
            graphics.fill(' ');

            int leftPosition = EXTRA_PADDING;
            TerminalSize size = graphics.getSize();
            int remainingSpace = size.getColumns() - EXTRA_PADDING;
            for (int i = 0; i < menuBar.getMenuCount() && remainingSpace > 0; i++) {
                Menu menu = menuBar.getMenu(i);
                TerminalSize preferredSize = menu.getPreferredSize();
                menu.setPosition(menu.getPosition()
                        .withColumn(leftPosition)
                        .withRow(0));
                int finalWidth = Math.min(preferredSize.getColumns(), remainingSpace);
                menu.setSize(menu.getSize()
                                .withColumns(finalWidth)
                                .withRows(size.getRows()));
                remainingSpace -= finalWidth + EXTRA_PADDING;
                leftPosition += finalWidth + EXTRA_PADDING;
                TextGUIGraphics componentGraphics = graphics.newTextGraphics(menu.getPosition(), menu.getSize());
                menu.draw(componentGraphics);
            }
        }
    }
}
