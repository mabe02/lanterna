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
 * Copyright (C) 2010-2011 mabe02
 */

package org.lantern.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.lantern.gui.listener.ContainerListener;

/**
 *
 * @author mabe02
 */
public abstract class AbstractContainer extends AbstractComponent implements InteractableContainer, Container
{
    private final List containerListeners;
    private final List components;

    protected AbstractContainer()
    {
        components = new ArrayList();
        containerListeners = new LinkedList();
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
            return (Component)components.get(index);
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

    protected Iterator components()
    {
        return components.iterator();
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
        Iterator iter = components();
        while(iter.hasNext())
        {
            Component component = (Component)iter.next();
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

        Iterator iter = components();
        while(iter.hasNext())
        {
            Component component = (Component)iter.next();
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

        List revComponents = new ArrayList(components);
        Collections.reverse(revComponents);

        Iterator iter = revComponents.iterator();
        while(iter.hasNext())
        {
            Component component = (Component)iter.next();
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
