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
 * Copyright (C) 2010-2015 Martin
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.input.KeyStroke;
import java.util.Arrays;
import java.util.Collection;

/**
 *
 * @author martin
 * @param <T>
 */
public abstract class AbstractComposite<T extends Container> extends AbstractComponent<T> implements Composite, Container {
    
    private Component component;

    public AbstractComposite() {
        component = null;
    }
    
    @Override
    public void setComponent(Component component) {
        Component oldComponent = this.component;
        this.component = component;
        if(oldComponent != null) {
            oldComponent.onRemoved(this);
        }
        if(component != null) {
            component.onAdded(this);
            component.setPosition(TerminalPosition.TOP_LEFT_CORNER);
        }
    }

    @Override
    public Component getComponent() {
        return component;
    }

    @Override
    public Collection<Component> getChildren() {
        return Arrays.asList(component);
    }
    
    @Override
    public boolean isInvalid() {
        return component != null && component.isInvalid();
    }
    
    @Override
    public Interactable nextFocus(Interactable fromThis) {
        if(fromThis == null && getComponent() instanceof Interactable) {
            return (Interactable)getComponent();
        }
        else if(getComponent() instanceof Container) {
            return ((Container)getComponent()).nextFocus(fromThis);
        }
        return null;
    }

    @Override
    public Interactable previousFocus(Interactable fromThis) {
        if(fromThis == null && getComponent() instanceof Interactable) {
            return (Interactable)getComponent();
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
