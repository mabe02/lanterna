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

/**
 *
 * @author martin
 */
public abstract class AbstractComposite extends AbstractComponent implements Composite {
    private Component component;

    public AbstractComposite() {
        this.component = null;
    }
    
    @Override
    public void addComponent(Component component) {
        if(this.component != null) {
            throw new IllegalStateException("Cannot add more than one component to a AbstractComposite composite");
        }
        setComponent(component);
    }

    @Override
    public void setComponent(Component component) {
        if(this.component != null) {
            removeComponent(this.component);
        }
        this.component = component;
        this.component.setParent(this);
    }

    @Override
    public boolean containsComponent(Component component) {
        return this.component == component;
    }

    @Override
    public void removeComponent(Component component) {
        if(this.component != component) {
            throw new IllegalArgumentException("Cannot remove component " + component + " from AbstractComposite " + toString() + 
                    " because AbstractComposite had component " + this.component);
        }
        this.component = null;
        component.setParent(null);
    }

    @Override
    public int getNumberOfComponents() {
        return component == null ? 0 : 1;
    }

    @Override
    public int getComponentIndex(Component component) {
        if(this.component != component) {
            return -1;
        }
        return 0;
    }

    @Override
    public Component getComponentAt(int index) {
        if(index != 0) {
            throw new IllegalArgumentException("AbstractComposite:s only have one component, cannot call getComponentAt with index " + index);
        }
        return component;
    }

    @Override
    public Component getComponent() {
        return component;
    }

    @Override
    public boolean isInvalid() {
        return component != null && component.isInvalid();
    }
    
}
