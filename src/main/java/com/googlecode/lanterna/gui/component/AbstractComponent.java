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
import com.googlecode.lanterna.gui.TextGraphics;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.listener.ComponentListener;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;
import java.util.ArrayList;
import java.util.Collections;
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
    private TerminalSize preferredSizeOverride;
    private boolean visible;
    private Alignment alignment;

    public AbstractComponent()
    {
        componentListeners = new LinkedList<ComponentListener>();
        parent = null;
        visible = true;
        preferredSizeOverride = null;
        alignment = Alignment.CENTER;
    }

    @Override
    public Container getParent()
    {
        return parent;
    }

    protected void setParent(Container parent)
    {
        this.parent = parent;
    }

    @Override
    public void addComponentListener(ComponentListener cl)
    {
        if(cl != null)
            componentListeners.add(cl);
    }

    @Override
    public void removeComponentListener(ComponentListener cl)
    {
        componentListeners.remove(cl);
    }

    @Override
    public boolean isVisible()
    {
        return visible;
    }

    @Override
    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    @Override
    public boolean isScrollable() {
        return false;
    }    

    @Override
    public void setPreferredSize(TerminalSize preferredSizeOverride) {
        this.preferredSizeOverride = preferredSizeOverride;
        invalidate();
    }

    @Override
    public TerminalSize getPreferredSize() {
        if(preferredSizeOverride != null)
            return preferredSizeOverride;
        else
            return calculatePreferredSize();            
    }
    
    protected abstract TerminalSize calculatePreferredSize();

    @Override
    public TerminalSize getMinimumSize() {
        return new TerminalSize(1, 1);
    }

    @Override
    public Alignment getAlignment() {
        return alignment;
    }

    @Override
    public void setAlignment(Alignment alignment) {
        if(alignment == null) {
            throw new IllegalArgumentException("Alignment argument to "
                    + "AbstractComponent.setAlignment(...) cannot be null");
        }        
        this.alignment = alignment;
    }
    
    protected void invalidate()
    {
        for(ComponentListener cl: componentListeners)
            cl.onComponentInvalidated(this);
        
        if(parent != null && parent instanceof AbstractContainer) {
            ((AbstractContainer)parent).invalidate();
        }
    }
    
    protected List<ComponentListener> getComponentListeners() {
        //This isn't thread safe either, but at least the list can't be modified
        return Collections.unmodifiableList(componentListeners);
    }

    @Override
    public Window getWindow()
    {
        Container container = getParent();
        if(container != null)
            return container.getWindow();
        return null;
    }

    protected GUIScreen getGUIScreen()
    {
        Window window = getWindow();
        if(window == null)
            return null;
        return window.getOwner();
    }

    /**
     * Will create a sub-graphic area according to the alignment, if the assigned size is larger 
     * than the preferred size.
     */
    protected TextGraphics transformAccordingToAlignment(TextGraphics graphics, TerminalSize preferredSize) {
        if(graphics.getWidth() <= preferredSize.getColumns() &&
                graphics.getHeight() <= preferredSize.getRows()) {
            
            //Don't do anything, return the original TextGraphics
            return graphics;
        }
        if(alignment == Alignment.FILL) {
            //For FILL, we also want to return it like it is
            return graphics;
        }
        
        int leftPosition = 0;
        if(alignment == Alignment.TOP_CENTER || alignment == Alignment.CENTER || alignment == Alignment.BOTTON_CENTER) {
            leftPosition = (graphics.getWidth() - preferredSize.getColumns()) / 2;
        }
        else if(alignment == Alignment.TOP_RIGHT || alignment == Alignment.RIGHT_CENTER || alignment == Alignment.BOTTOM_RIGHT) {
            leftPosition = graphics.getWidth() - preferredSize.getColumns();
        }
        
        int topPosition = 0;
        if(alignment == Alignment.LEFT_CENTER || alignment == Alignment.CENTER || alignment == Alignment.RIGHT_CENTER) {
            topPosition = (graphics.getHeight() - preferredSize.getRows()) / 2;
        }
        else if(alignment == Alignment.BOTTOM_LEFT || alignment == Alignment.BOTTON_CENTER || alignment == Alignment.BOTTOM_RIGHT) {
            topPosition = graphics.getHeight() - preferredSize.getRows();
        }
        return graphics.subAreaGraphics(new TerminalPosition(leftPosition, topPosition), preferredSize);
    }
}
