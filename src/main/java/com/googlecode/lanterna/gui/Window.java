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

package com.googlecode.lanterna.gui;

import com.googlecode.lanterna.LanternaException;
import com.googlecode.lanterna.gui.layout.SizePolicy;
import com.googlecode.lanterna.gui.listener.ComponentAdapter;
import com.googlecode.lanterna.gui.listener.ContainerListener;
import com.googlecode.lanterna.gui.listener.WindowListener;
import com.googlecode.lanterna.gui.theme.Theme.Category;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Martin
 */
public class Window implements Container
{
    private final List<WindowListener> windowListeners;
    private final List<ComponentInvalidatorAlert> invalidatorAlerts;
    private GUIScreen owner;
    private final Panel contentPane;
    private Interactable currentlyInFocus;
    private boolean soloWindow;

    public Window(String title)
    {
        this.windowListeners = new ArrayList<WindowListener>();
        this.invalidatorAlerts = new ArrayList<ComponentInvalidatorAlert>();
        this.owner = null;
        this.contentPane = new Panel(title);
        this.contentPane.setParent(this);
        this.currentlyInFocus = null;
        this.soloWindow = false;
    }

    public void addWindowListener(WindowListener listener)
    {
        windowListeners.add(listener);
    }

    public GUIScreen getOwner()
    {
        return owner;
    }

    void setOwner(GUIScreen owner)
    {
        this.owner = owner;
    }

    public Border getBorder()
    {
        return contentPane.getBorder();
    }

    public void setBorder(Border border)
    {
        if(border != null)
            contentPane.setBorder(border);
    }

    public TerminalSize getPreferredSize()
    {
        return contentPane.getPreferredSize();
    }

    public void repaint(TextGraphics graphics)
    {
        graphics.applyThemeItem(graphics.getTheme().getItem(Category.DialogArea));
        graphics.fillRectangle(' ', new TerminalPosition(0, 0), new TerminalSize(graphics.getWidth(), graphics.getHeight()));
        contentPane.repaint(graphics);
    }

    public void invalidate()
    {
        for(WindowListener listener: windowListeners)
            listener.onWindowInvalidated(this);
    }

    public void addEmptyLine()
    {
        addComponent(new EmptySpace(1, 1));
    }

    public void addComponent(Component component)
    {
        addComponent(component, SizePolicy.CONSTANT);
    }
    
    public void addComponent(Component component, SizePolicy sizePolicy)
    {
        if(component == null)
            return;

        contentPane.addComponent(component, sizePolicy);
        ComponentInvalidatorAlert invalidatorAlert = new ComponentInvalidatorAlert(component);
        invalidatorAlerts.add(invalidatorAlert);
        component.addComponentListener(invalidatorAlert);

        if(currentlyInFocus == null)
            setFocus(contentPane.nextFocus(null));

        invalidate();
    }

    public void addContainerListener(ContainerListener cl)
    {
        contentPane.addContainerListener(cl);
    }

    public void removeContainerListener(ContainerListener cl)
    {
        contentPane.removeContainerListener(cl);
    }

    public Component getComponentAt(int index)
    {
        return contentPane.getComponentAt(index);
    }

    public int getComponentCount()
    {
        return contentPane.getComponentCount();
    }

    public void removeComponent(Component component)
    {
        if(component instanceof InteractableContainer) {
            InteractableContainer container = (InteractableContainer)component;
            if(container.hasInteractable(currentlyInFocus)) {
                Interactable original = currentlyInFocus;
                Interactable current = contentPane.nextFocus(original);
                while(container.hasInteractable(current) && original != current)
                    current = contentPane.nextFocus(current);
                if(container.hasInteractable(current))
                    setFocus(null);
                else
                    setFocus(current);
            }
        }
        else if(component == currentlyInFocus)
            setFocus(contentPane.nextFocus(currentlyInFocus));
        
        contentPane.removeComponent(component);

        for(ComponentInvalidatorAlert invalidatorAlert: invalidatorAlerts) {
            if(component == invalidatorAlert.component) {
                component.removeComponentListener(invalidatorAlert);
                invalidatorAlerts.remove(invalidatorAlert);
                break;
            }
        }
    }

    protected void removeAllComponents()
    {
        while(getComponentCount() > 0)
            removeComponent(getComponentAt(0));
    }

    public void setBetweenComponentsPadding(int paddingSize)
    {
        contentPane.setBetweenComponentsPadding(paddingSize);
    }

    public TerminalPosition getWindowHotspotPosition()
    {
        if(currentlyInFocus == null)
            return null;
        else
            return currentlyInFocus.getHotspot();
    }

    public void onKeyPressed(Key key) throws LanternaException
    {
        InteractableResult resultContainer = new InteractableResult();

        if(currentlyInFocus != null) {
            currentlyInFocus.keyboardInteraction(key, resultContainer);
            if(resultContainer.type == Interactable.Result.NEXT_INTERACTABLE) {
                Interactable nextItem = contentPane.nextFocus(currentlyInFocus);
                if(nextItem == null)
                    nextItem = contentPane.nextFocus(null);
                setFocus(nextItem, Interactable.FocusChangeDirection.DOWN_OR_RIGHT);
            }
            if(resultContainer.type == Interactable.Result.PREVIOUS_INTERACTABLE) {
                Interactable prevItem = contentPane.previousFocus(currentlyInFocus);
                if(prevItem == null)
                    prevItem = contentPane.previousFocus(null);
                setFocus(prevItem, Interactable.FocusChangeDirection.UP_OR_LEFT);
            }
        }
    }

    public boolean isSoloWindow()
    {
        return soloWindow;
    }

    public void setSoloWindow(boolean soloWindow)
    {
        this.soloWindow = soloWindow;
    }

    boolean maximisesVertically()
    {
        return contentPane.maximisesVertically();
    }

    boolean maximisesHorisontally()
    {
        return contentPane.maximisesHorisontally();
    }

    protected void setFocus(Interactable newFocus)
    {
        setFocus(newFocus, null);
    }

    protected void setFocus(Interactable newFocus, Interactable.FocusChangeDirection direction)
    {
        if(currentlyInFocus != null)
            currentlyInFocus.onLeaveFocus(direction);
        currentlyInFocus = newFocus;
        if(currentlyInFocus != null)
            currentlyInFocus.onEnterFocus(direction);
        invalidate();
    }

    protected void close()
    {
        if(owner != null)
            owner.closeWindow(this);
    }

    protected void onVisible()
    {
        for(WindowListener listener: windowListeners)
            listener.onWindowShown(this);
    }

    protected void onClosed()
    {
        for(WindowListener listener: windowListeners)
            listener.onWindowClosed(this);
    }

    private class ComponentInvalidatorAlert extends ComponentAdapter
    {
        private Component component;

        public ComponentInvalidatorAlert(Component component)
        {
            this.component = component;
        }

        public Component getComponent()
        {
            return component;
        }

        @Override
        public void onComponentInvalidated(Component component)
        {
            invalidate();
        }
    }
}
