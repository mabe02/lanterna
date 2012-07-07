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

import com.googlecode.lanterna.gui.Component;
import com.googlecode.lanterna.gui.Container;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.listener.ComponentListener;
import java.util.LinkedList;
import java.util.List;


/**
 *
 * @author Martin
 */
public abstract class AbstractComponent implements Component
{
    private final List<ComponentListener> componentListeners;
    private Container parent;
    private boolean visible;

    public AbstractComponent()
    {
        componentListeners = new LinkedList<ComponentListener>();
        parent = null;
        visible = true;
    }

    public Container getParent()
    {
        return parent;
    }

    protected void setParent(Container parent)
    {
        this.parent = parent;
    }

    public void addComponentListener(ComponentListener cl)
    {
        if(cl != null)
            componentListeners.add(cl);
    }

    public void removeComponentListener(ComponentListener cl)
    {
        componentListeners.remove(cl);
    }

    public boolean isVisible()
    {
        return visible;
    }

    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    public boolean isScrollable() {
        return false;
    }    

    protected void invalidate()
    {
        for(ComponentListener cl: componentListeners)
            cl.onComponentInvalidated(this);
        
        if(parent != null && parent instanceof AbstractContainer) {
            ((AbstractContainer)parent).invalidate();
        }
    }

    protected Window getParentWindow()
    {
        Container container = getParent();
        while(container != null) {
            if(container instanceof Window)
                return ((Window)(container));
            else if(container instanceof Component)
                container = ((Component)container).getParent();
            else
                break;
        }
        return null;
    }

    protected GUIScreen getGUIScreen()
    {
        Window window = getParentWindow();
        if(window == null)
            return null;
        return window.getOwner();
    }
}
