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

package com.googlecode.lanterna.gui.component;

import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.Component;
import com.googlecode.lanterna.gui.Container;
import com.googlecode.lanterna.gui.Interactable;
import com.googlecode.lanterna.gui.InteractableContainer;
import com.googlecode.lanterna.gui.layout.LayoutParameter;
import com.googlecode.lanterna.gui.listener.ContainerListener;
import com.googlecode.lanterna.gui.util.ShortcutHelper;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.input.Key.Kind;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Martin
 */
public abstract class AbstractContainer extends AbstractComponent implements InteractableContainer, Container
{
    private final List<ContainerListener> containerListeners;
    private final List<Component> components;
    private final ShortcutHelper shortcutHelper;

    protected AbstractContainer()
    {
        components = new ArrayList<Component>();
        containerListeners = new LinkedList<ContainerListener>();
        shortcutHelper = new ShortcutHelper();
    }

    @Override
    public void addComponent(Component component, LayoutParameter... layoutParameters)
    {
        if(component == null)
            return;
        
        synchronized(components) {
            components.add(component);
        }

        if(component instanceof AbstractComponent)
            ((AbstractComponent)component).setParent(this);
    }

    @Override
    public Component getComponentAt(int index)
    {
        synchronized(components) {
            return components.get(index);
        }
    }

    @Override
    public boolean containsComponent(Component component) {
        synchronized(components) {
            return components.contains(component);
        }
    }

    @Override
    public int getComponentCount()
    {
        synchronized(components) {
           return components.size();
        }
    }

    @Override
    public boolean removeComponent(Component component)
    {
        if(component == null)
            return false;
        
        synchronized(components) {
            return components.remove(component);
        }
    }

    public void removeAllComponents()
    {
        synchronized(components) {
            while(getComponentCount() > 0)
                removeComponent(getComponentAt(0));
        }
    }

    @Override
    public boolean isScrollable() {
        for(Component component: components)
            if(component.isScrollable())
                return true;
        return false;
    }

    protected Iterable<Component> components()
    {
        return components;
    }

    @Override
    public void addContainerListener(ContainerListener cl)
    {
        if(cl != null)
            containerListeners.add(cl);
    }

    @Override
    public void removeContainerListener(ContainerListener cl)
    {
        if(cl != null)
            containerListeners.remove(cl);
    }

    @Override
    public boolean hasInteractable(Interactable interactable)
    {
        for(Component component: components())
        {
            if(component instanceof InteractableContainer)
                if(((InteractableContainer)(component)).hasInteractable(interactable))
                    return true;
            if(component == interactable)
                return true;
        }
        return false;
    }

    @Override
    public Interactable nextFocus(Interactable previous)
    {
        boolean chooseNextAvailable = (previous == null);

        for(Component component: components())
        {
            if(chooseNextAvailable) {
                if(!component.isVisible())
                    continue;                    
                if(component instanceof Interactable)
                    return (Interactable)component;
                if(component instanceof InteractableContainer) {
                    Interactable firstInteractable = ((InteractableContainer)(component)).nextFocus(null);
                    if(firstInteractable != null)
                        return firstInteractable;
                }
                continue;
            }

            if(component == previous) {
                chooseNextAvailable = true;
                continue;
            }

            if(component instanceof InteractableContainer) {
                InteractableContainer ic = (InteractableContainer)component;
                if(ic.hasInteractable(previous)) {
                    Interactable next = ic.nextFocus(previous);
                    if(next == null) {
                        chooseNextAvailable = true;
                        continue;
                    }
                    else
                        return next;
                }
            }
        }
        return null;
    }

    @Override
    public Interactable previousFocus(Interactable fromThis)
    {
        boolean chooseNextAvailable = (fromThis == null);

        List<Component> revComponents = new ArrayList<Component>(components);
        Collections.reverse(revComponents);

        for(Component component: revComponents)
        {
            if(chooseNextAvailable) {
                if(!component.isVisible())
                    continue;
                if(component instanceof Interactable)
                    return (Interactable)component;
                if(component instanceof InteractableContainer) {
                    Interactable lastInteractable = ((InteractableContainer)(component)).previousFocus(null);
                    if(lastInteractable != null)
                        return lastInteractable;
                }
                continue;
            }

            if(component == fromThis) {
                chooseNextAvailable = true;
                continue;
            }

            if(component instanceof InteractableContainer) {
                InteractableContainer ic = (InteractableContainer)component;
                if(ic.hasInteractable(fromThis)) {
                    Interactable next = ic.previousFocus(fromThis);
                    if(next == null) {
                        chooseNextAvailable = true;
                        continue;
                    }
                    else
                        return next;
                }
            }
        }
        return null;
    }

    @Override
    public void addShortcut(Kind key, Action action) {
        shortcutHelper.addShortcut(key, action);
    }

    @Override
    public void addShortcut(char character, boolean withCtrl, boolean withAlt, Action action) {
        shortcutHelper.addShortcut(character, withCtrl, withAlt, action);
    }

    @Override
    public boolean triggerShortcut(Key key) {
        return shortcutHelper.triggerShortcut(key);
    }
}
