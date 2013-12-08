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
import com.googlecode.lanterna.terminal.TerminalSize;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Martin
 */
public abstract class AbstractContainer extends AbstractComponent implements Container {
    
    private final List<Component> components;
    private final List<LayoutManager.Parameter[]> layoutParameters;

    public AbstractContainer() {
        components = new ArrayList<Component>();
        layoutParameters = new ArrayList<Parameter[]>();
    }

    @Override
    public void addComponent(Component component, Parameter... layoutParameters) {
        if(component == null) {
            throw new IllegalArgumentException("Cannot add null component");
        }
        synchronized(components) {
            components.add(component);
            if(layoutParameters == null) {
                this.layoutParameters.add(new Parameter[0]);
            }
            else {
                this.layoutParameters.add(layoutParameters);
            }
        }
        invalidate();
    }

    @Override
    public void removeComponent(Component component) {
        if(component == null) {
            throw new IllegalArgumentException("Cannot remove null component");
        }
        synchronized(components) {
            int index = components.indexOf(component);
            if(index == -1) {
                return;
            }
            components.remove(index);
            layoutParameters.remove(index);
        }
        invalidate();
    }

    @Override
    public void removeAllComponents() {
        synchronized(components) {
            components.clear();
            layoutParameters.clear();
        }
        invalidate();
    }

    @Override
    public boolean containsComponent(Component component) {
        if(component == null) {
            throw new IllegalArgumentException("Cannot find null component");
        }
        synchronized(components) {
            return components.contains(component);
        }
    }

    @Override
    public int getComponentIndex(Component component) {
        if(component == null) {
            throw new IllegalArgumentException("Cannot find index of null component");
        }
        synchronized(components) {
            return components.indexOf(component);
        }
    }

    @Override
    public Component getComponentAt(int index) {
        synchronized(components) {
            if(index < 0 || index > components.size()) {
                throw new IndexOutOfBoundsException("Cannot find index " + index + " from the components list, out of array bounds!");
            }
            if(index == components.size()) {
                return null;
            }
            return components.get(index);
        }
    }

    @Override
    public int getNumberOfComponents() {
        synchronized(components) {
            return components.size();
        }
    }
    
    protected List<LayoutManager.LaidOutComponent> sendToLayoutManager(LayoutManager layoutManager, TerminalSize areaSize) {
        return layoutManager.doLayout(
                areaSize, 
                Collections.unmodifiableList(components),
                Collections.unmodifiableList(layoutParameters));
    }
}
