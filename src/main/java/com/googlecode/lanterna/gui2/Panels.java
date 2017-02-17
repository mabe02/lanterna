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

/**
 * Utility class for quickly bunching up components in a panel, arranged in a particular pattern
 * @author Martin
 */
public class Panels {

    /**
     * Creates a new {@code Panel} with a {@code LinearLayout} layout manager in horizontal mode and adds all the
     * components passed in
     * @param components Components to be added to the new {@code Panel}, in order
     * @return The new {@code Panel}
     */
    public static Panel horizontal(Component... components) {
        Panel panel = new Panel();
        panel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        for(Component component: components) {
            panel.addComponent(component);
        }
        return panel;
    }

    /**
     * Creates a new {@code Panel} with a {@code LinearLayout} layout manager in vertical mode and adds all the
     * components passed in
     * @param components Components to be added to the new {@code Panel}, in order
     * @return The new {@code Panel}
     */
    public static Panel vertical(Component... components) {
        Panel panel = new Panel();
        panel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        for(Component component: components) {
            panel.addComponent(component);
        }
        return panel;
    }

    /**
     * Creates a new {@code Panel} with a {@code GridLayout} layout manager and adds all the components passed in
     * @param columns Number of columns in the grid
     * @param components Components to be added to the new {@code Panel}, in order
     * @return The new {@code Panel}
     */
    public static Panel grid(int columns, Component... components) {
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(columns));
        for(Component component: components) {
            panel.addComponent(component);
        }
        return panel;
    }

    //Cannot instantiate
    private Panels() {}
}
