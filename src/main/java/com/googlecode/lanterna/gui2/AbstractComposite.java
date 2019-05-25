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
 * Copyright (C) 2010-2019 Martin Berglund
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.input.KeyStroke;

import java.util.Collection;
import java.util.Collections;

/**
 * This abstract implementation contains common code for the different {@code Composite} implementations. A
 * {@code Composite} component is one that encapsulates a single component, like borders. Because of this, a
 * {@code Composite} can be seen as a special case of a {@code Container} and indeed this abstract class does in fact
 * implement the {@code Container} interface as well, to make the composites easier to work with internally.
 * @author martin
 * @param <T> Should always be itself, see {@code AbstractComponent}
 */
public abstract class AbstractComposite<T extends Container> extends AbstractComponent<T> implements Composite, Container {
    
    private Component component;

    /**
     * Default constructor
     */
    public AbstractComposite() {
        component = null;
    }
    
    @Override
    public void setComponent(Component component) {
        Component oldComponent = this.component;
        if(oldComponent == component) {
            return;
        }
        if(oldComponent != null) {
            removeComponent(oldComponent);
        }
        if(component != null) {
            this.component = component;
            component.onAdded(this);
            component.setPosition(TerminalPosition.TOP_LEFT_CORNER);
            invalidate();
        }
    }

    @Override
    public Component getComponent() {
        return component;
    }

    @Override
    public int getChildCount() {
        return component != null ? 1 : 0;
    }

    @Override
    public Collection<Component> getChildren() {
        if(component != null) {
            return Collections.singletonList(component);
        }
        else {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean containsComponent(Component component) {
        return component != null && component.hasParent(this);
    }

    @Override
    public boolean removeComponent(Component component) {
        if(this.component == component) {
            this.component = null;
            component.onRemoved(this);
            invalidate();
            return true;
        }
        return false;
    }

    @Override
    public boolean isInvalid() {
        return component != null && component.isInvalid();
    }

    @Override
    public void invalidate() {
        super.invalidate();

        //Propagate
        if(component != null) {
            component.invalidate();
        }
    }

    @Override
    public Interactable nextFocus(Interactable fromThis) {
        if(fromThis == null && getComponent() instanceof Interactable) {
            Interactable interactable = (Interactable) getComponent();
            if(interactable.isEnabled()) {
                return interactable;
            }
        }
        else if(getComponent() instanceof Container) {
            return ((Container)getComponent()).nextFocus(fromThis);
        }
        return null;
    }

    @Override
    public Interactable previousFocus(Interactable fromThis) {
        if(fromThis == null && getComponent() instanceof Interactable) {
            Interactable interactable = (Interactable) getComponent();
            if(interactable.isEnabled()) {
                return interactable;
            }
        }
        else if(getComponent() instanceof Container) {
            return ((Container)getComponent()).previousFocus(fromThis);
        }
        return null;
    }

    @Override
    public boolean handleInput(KeyStroke key) {
        return false;
    }

    @Override
    public void updateLookupMap(InteractableLookupMap interactableLookupMap) {
        if(getComponent() instanceof Container) {
            ((Container)getComponent()).updateLookupMap(interactableLookupMap);
        }
        else if(getComponent() instanceof Interactable) {
            interactableLookupMap.add((Interactable)getComponent());
        }
    }
}
