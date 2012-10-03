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
import com.googlecode.lanterna.gui.layout.LayoutParameter;
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
public class Window
{
    private final List<WindowListener> windowListeners;
    private final List<ComponentInvalidatorAlert> invalidatorAlerts;
    private GUIScreen owner;
    private final WindowContentPane contentPane;
    private Interactable currentlyInFocus;
    private TerminalSize windowSizeOverride;
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
        this.windowSizeOverride = null;
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
     * Returns the current window size override or {@code null} if none is set
     * @return Override for the window size or {@code null}
     */
    public TerminalSize getWindowSizeOverride() {
        return windowSizeOverride;
    }

    /**
     * Sets the size of the window to a fixed value, rather than using automatic size calculation. 
     * If you call this method with {@code windowSizeOverride} parameter set to {@code null}, the
     * automatic size calculation will be activated again.
     * @param windowSizeOverride Size the window should have, or {@code null} if you want to use
     * automatic size calculation
     */
    public void setWindowSizeOverride(TerminalSize windowSizeOverride) {
        this.windowSizeOverride = windowSizeOverride;
    }

    /**
     * @return How big this window would like to be
     */
    TerminalSize getPreferredSize()
    {
        if(windowSizeOverride != null)
            return windowSizeOverride;
        else
            return contentPane.getPreferredSize();  //Automatically calculate the size
    }

    void repaint(TextGraphics graphics)
    {
        graphics.applyTheme(graphics.getTheme().getDefinition(Category.DIALOG_AREA));
        graphics.fillRectangle(' ', new TerminalPosition(0, 0), new TerminalSize(graphics.getWidth(), graphics.getHeight()));
        contentPane.repaint(graphics);
    }

    private void invalidate()
    {
        for(WindowListener listener: windowListeners)
            listener.onWindowInvalidated(this);
    }

    /**
     * Don't use the method, call {@code addComponent(new EmptySpace(1, 1))} instead
     * @deprecated
     */
    @Deprecated
    public void addEmptyLine()
    {
        addComponent(new EmptySpace(1, 1));
    }
    
    public void addComponent(Component component, LayoutParameter... layoutParameters)
    {
        if(component == null)
            return;

        contentPane.addComponent(component, layoutParameters);
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
            if(result.isNextInteractable()) {
                Interactable nextItem = contentPane.nextFocus(currentlyInFocus);
                if(nextItem == null)
                    nextItem = contentPane.nextFocus(null);
                setFocus(nextItem, result.asFocusChangeDirection());
            }
            else if(result.isPreviousInteractable()) {
                Interactable prevItem = contentPane.previousFocus(currentlyInFocus);
                if(prevItem == null)
                    prevItem = contentPane.previousFocus(null);
                setFocus(prevItem, result.asFocusChangeDirection());
            }
            else if(result == Interactable.Result.EVENT_NOT_HANDLED) {
                //Try to find a shortcut
                if(currentlyInFocus instanceof Component) {
                    Container parentContainer = ((Component)currentlyInFocus).getParent();
                    while(parentContainer != null) {
                        if(parentContainer instanceof InteractableContainer) {
                            //If we could fire off a shortcut, stop here
                            if(((InteractableContainer)parentContainer).triggerShortcut(key))
                                return;
                        }
                        //Otherwise try one level higher
                        parentContainer = parentContainer.getParent();
                    }
                }
                
                //There was no shortcut
                onUnhandledKeyPress(key);
            }
        }
        else {
            onUnhandledKeyPress(key);
        }
    }
    
    private void onUnhandledKeyPress(Key key) {
        for(WindowListener listener: windowListeners)
            listener.onUnhandledKeyboardInteraction(this, key);
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
        
        //Fire the focus changed event
        for(WindowListener listener: windowListeners) {
            listener.onFocusChanged(this, currentlyInFocus, newFocus);
        }
        
        currentlyInFocus = newFocus;
        if(currentlyInFocus != null)
            currentlyInFocus.onEnterFocus(direction);
        invalidate();
    }

    public void close()
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

    @Override
    public String toString() {
        return contentPane.getTitle();
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
        }

        @Override
        public Window getWindow() {
            return Window.this;
        }
    }
}
