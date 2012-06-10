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

import com.googlecode.lanterna.gui.Theme.Category;
import com.googlecode.lanterna.gui.component.EmptySpace;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.layout.SizePolicy;
import com.googlecode.lanterna.gui.listener.ComponentAdapter;
import com.googlecode.lanterna.gui.listener.ContainerListener;
import com.googlecode.lanterna.gui.listener.WindowListener;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;
import java.util.ArrayList;
import java.util.List;

/**
 * The Window class is the basis for Lanternas GUI system. The workflow is to
 * create and show modal windows and promt the user for input. A window, once
 * displayed, will take over control by entering an event loop and won't come 
 * back until the window is closed.
 * @author Martin
 */
public class Window implements Container
{
    private final List<WindowListener> windowListeners;
    private final List<ComponentInvalidatorAlert> invalidatorAlerts;
    private GUIScreen owner;
    private final WindowContentPane contentPane;
    private Interactable currentlyInFocus;
    private boolean soloWindow;

    /**
     * Creates a new window
     * @param title Title for the new window
     */
    public Window(String title)
    {
        this.windowListeners = new ArrayList<WindowListener>();
        this.invalidatorAlerts = new ArrayList<ComponentInvalidatorAlert>();
        this.owner = null;
        this.contentPane = new WindowContentPane(title);
        this.currentlyInFocus = null;
        this.soloWindow = false;
    }

    public void addWindowListener(WindowListener listener)
    {
        windowListeners.add(listener);
    }

    /**
     * @return The GUIScreen which this window is displayed on
     */
    public GUIScreen getOwner()
    {
        return owner;
    }

    void setOwner(GUIScreen owner)
    {
        this.owner = owner;
    }

    /**
     * @return The border of this window
     */
    public Border getBorder()
    {
        return contentPane.getBorder();
    }

    public void setBorder(Border border)
    {
        if(border != null)
            contentPane.setBorder(border);
    }

    /**
     * @return How big this window would like to be
     */
    TerminalSize getPreferredSize()
    {
        return contentPane.getPreferredSize();
    }

    void repaint(TextGraphics graphics)
    {
        graphics.applyThemeItem(graphics.getTheme().getItem(Category.DialogArea));
        graphics.fillRectangle(' ', new TerminalPosition(0, 0), new TerminalSize(graphics.getWidth(), graphics.getHeight()));
        contentPane.repaint(graphics);
    }

    private void invalidate()
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

    /**
     * @return How many top-level components this window has
     */
    public int getComponentCount()
    {
        return contentPane.getComponentCount();
    }

    /**
     * Removes a top-level component from the window
     * @param component Top-level component to remove
     */
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

    /**
     * Removes all components from the window
     */
    public void removeAllComponents()
    {
        while(getComponentCount() > 0)
            removeComponent(getComponentAt(0));
    }

    /**
     * Sets the amount of padding to put between components
     * @param paddingSize Padding size, both horizontally and vertically
     */
    public void setBetweenComponentsPadding(int paddingSize)
    {
        contentPane.setBetweenComponentsPadding(paddingSize);
    }

    TerminalPosition getWindowHotspotPosition()
    {
        if(currentlyInFocus == null)
            return null;
        else
            return currentlyInFocus.getHotspot();
    }

    void onKeyPressed(Key key)
    {
        if(currentlyInFocus != null) {
            Interactable.Result result =  currentlyInFocus.keyboardInteraction(key);
            if(result == Interactable.Result.NEXT_INTERACTABLE) {
                Interactable nextItem = contentPane.nextFocus(currentlyInFocus);
                if(nextItem == null)
                    nextItem = contentPane.nextFocus(null);
                setFocus(nextItem, Interactable.FocusChangeDirection.DOWN_OR_RIGHT);
            }
            if(result == Interactable.Result.PREVIOUS_INTERACTABLE) {
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

    /**
     * If set to true, when this window is shown, all previous windows are 
     * hidden. Set to false to show them again.
     */
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
            owner.closeWindow();
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
    
    /**
     * Private class to get through some method restrictions
     */
    private class WindowContentPane extends Panel {
        public WindowContentPane(String title) {
            super(title);
            setParent(Window.this);
        }

        @Override
        protected boolean maximisesHorisontally() {
            return super.maximisesHorisontally();
        }

        @Override
        protected boolean maximisesVertically() {
            return super.maximisesVertically();
        }
    }
}
