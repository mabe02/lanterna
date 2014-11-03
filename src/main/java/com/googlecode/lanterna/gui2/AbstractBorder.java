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

/**
 *
 * @author Martin
 */
public abstract class AbstractBorder extends AbstractRenderableComponent implements Border {

    private Component component;

    public AbstractBorder() {
        component = null;
    }
    
    @Override
    public boolean hasInteractable(Interactable interactable) {
        return interactable != null && (interactable == component ||
                (component instanceof InteractableContainer && ((InteractableContainer)component).hasInteractable(interactable)));
    }

    @Override
    public Interactable nextFocus(Interactable fromThis) {
        if(fromThis == null && component instanceof Interactable) {
            return (Interactable)component;
        }
        else if(component instanceof InteractableContainer) {
            return ((InteractableContainer)component).nextFocus(fromThis);
        }
        return null;
    }

    @Override
    public Interactable previousFocus(Interactable fromThis) {
        if(fromThis == null && component instanceof Interactable) {
            return (Interactable)component;
        }
        else if(component instanceof InteractableContainer) {
            return ((InteractableContainer)component).previousFocus(fromThis);
        }
        return null;
    }

    @Override
    public void addComponent(Component component) {
        if(this.component != null) {
            throw new IllegalStateException("Cannot add more than one component to a Border composite");
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
        this.component.setPosition(getWrappedComponentTopLeftOffset());
    }

    @Override
    public boolean containsComponent(Component component) {
        return this.component == component;
    }

    @Override
    public void removeComponent(Component component) {
        if(this.component != component) {
            throw new IllegalArgumentException("Cannot remove component " + component + " from Border " + toString() + 
                    " because Border had component " + this.component);
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
            throw new IllegalArgumentException("Borders only have one component, cannot call getComponentAt with index " + index);
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

    @Override
    public AbstractBorder setSize(TerminalSize size) {
        super.setSize(size);
        component.setSize(getWrappedComponentSize(size));
        return this;
    }

    @Override
    public void updateLookupMap(InteractableLookupMap interactableLookupMap) {
        if(component instanceof InteractableContainer) {
            ((InteractableContainer)component).updateLookupMap(interactableLookupMap);
        }
        else if(component instanceof Interactable) {
            interactableLookupMap.add((Interactable)component);
        }
    }

    @Override
    protected BorderRenderer getRenderer() {
        return (BorderRenderer)super.getRenderer();
    }
    
    protected Component getWrappedComponent() {
        return component;
    }

    private TerminalPosition getWrappedComponentTopLeftOffset() {
        return getRenderer().getWrappedComponentTopLeftOffset();
    }

    private TerminalSize getWrappedComponentSize(TerminalSize borderSize) {
        return getRenderer().getWrappedComponentSize(borderSize);
    }
}
