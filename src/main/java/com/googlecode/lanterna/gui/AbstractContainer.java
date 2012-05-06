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
 * Copyright (C) 2010-2012 mabe02
 */

package com.googlecode.lanterna.gui;

import com.googlecode.lanterna.gui.listener.ContainerListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author mabe02
 */
public abstract class AbstractContainer extends AbstractComponent implements InteractableContainer, Container
{
    private final List<ContainerListener> containerListeners;
    private final List<Component> components;

    protected AbstractContainer()
    {
        components = new ArrayList<Component>();
        containerListeners = new LinkedList<ContainerListener>();
    }

    public void addComponent(Component component)
    {
        if(component == null)
            return;
        
        synchronized(components) {
            components.add(component);
        }

        if(component instanceof AbstractComponent)
            ((AbstractComponent)component).setParent(this);
    }

    public Component getComponentAt(int index)
    {
        synchronized(components) {
            return components.get(index);
        }
    }

    public int getComponentCount()
    {
        synchronized(components) {
           return components.size();
        }
    }

    public void removeComponent(Component component)
    {
        if(component == null)
            return;
        
        synchronized(components) {
            components.remove(component);
        }
    }

    public void removeAllComponents()
    {
        synchronized(components) {
            while(getComponentCount() > 0)
                removeComponent(getComponentAt(0));
        }
    }

    protected Iterable<Component> components()
    {
        return components;
    }

    public void addContainerListener(ContainerListener cl)
    {
        if(cl != null)
            containerListeners.add(cl);
    }

    public void removeContainerListener(ContainerListener cl)
    {
        if(cl != null)
            containerListeners.remove(cl);
    }

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

    public Interactable nextFocus(Interactable previous)
    {
        boolean chooseNext = (previous == null);

        for(Component component: components())
        {
            if(chooseNext) {
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
                chooseNext = true;
                continue;
            }

            if(component instanceof InteractableContainer) {
                InteractableContainer ic = (InteractableContainer)component;
                if(ic.hasInteractable(previous)) {
                    Interactable next = ic.nextFocus(previous);
                    if(next == null) {
                        chooseNext = true;
                        continue;
                    }
                    else
                        return next;
                }
            }
        }
        return null;
    }

    public Interactable previousFocus(Interactable fromThis)
    {
        boolean chooseNext = (fromThis == null);

        List<Component> revComponents = new ArrayList<Component>(components);
        Collections.reverse(revComponents);

        for(Component component: revComponents)
        {
            if(chooseNext) {
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
                chooseNext = true;
                continue;
            }

            if(component instanceof InteractableContainer) {
                InteractableContainer ic = (InteractableContainer)component;
                if(ic.hasInteractable(fromThis)) {
                    Interactable next = ic.previousFocus(fromThis);
                    if(next == null) {
                        chooseNext = true;
                        continue;
                    }
                    else
                        return next;
                }
            }
        }
        return null;
    }
}
