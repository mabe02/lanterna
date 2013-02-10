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
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.gui2.LayoutManager.Parameter;

/**
 *
 * @author Martin
 */
public abstract class LayoutManagedContainer implements Container {
    private final LayoutManager layoutManager;

    public LayoutManagedContainer(LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public void addComponent(Component component, Parameter... layoutParameters) {
        layoutManager.addComponent(component, layoutParameters);
    }

    @Override
    public void removeComponent(Component component) {
        layoutManager.removeComponent(component);
    }

    @Override
    public boolean containsComponent(Component component) {
        return layoutManager.containsComponent(component);
    }

    @Override
    public int getComponentIndex(Component component) {
        return layoutManager.getComponentIndex(component);
    }

    @Override
    public Component getComponentAt(int index) {
        return layoutManager.getComponentAt(index);
    }

    @Override
    public int getNumberOfComponents() {
        return layoutManager.getNumberOfComponents();
    }   
}
