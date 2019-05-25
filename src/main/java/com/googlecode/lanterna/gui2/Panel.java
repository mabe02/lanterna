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

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This class is the basic building block for creating user interfaces, being the standard implementation of
 * {@code Container} that supports multiple children. A {@code Panel} is a component that can contain one or more
 * other components, including nested panels. The panel itself doesn't have any particular appearance and isn't
 * interactable by itself, although you can set a border for the panel and interactable components inside the panel will
 * receive input focus as expected.
 *
 * @author Martin
 */
public class Panel extends AbstractComponent<Panel> implements Container {
    private final List<Component> components;
    private LayoutManager layoutManager;
    private TerminalSize cachedPreferredSize;
    private TextColor fillColorOverride;

    /**
     * Default constructor, creates a new panel with no child components and by default set to a vertical
     * {@code LinearLayout} layout manager.
     */
    public Panel() {
        this(new LinearLayout());
    }

    public Panel(LayoutManager layoutManager) {
        if(layoutManager == null) {
            layoutManager = new AbsoluteLayout();
        }
        this.components = new ArrayList<Component>();
        this.layoutManager = layoutManager;
        this.cachedPreferredSize = null;
    }

    /**
     * Adds a new child component to the panel. Where within the panel the child will be displayed is up to the layout
     * manager assigned to this panel. If the component has already been added to another panel, it will first be
     * removed from that panel before added to this one.
     * @param component Child component to add to this panel
     * @return Itself
     */
    public Panel addComponent(Component component) {
        return addComponent(Integer.MAX_VALUE, component);
    }

    /**
     * Adds a new child component to the panel. Where within the panel the child will be displayed is up to the layout
     * manager assigned to this panel. If the component has already been added to another panel, it will first be
     * removed from that panel before added to this one.
     * @param component Child component to add to this panel
     * @param index At what index to add the component among the existing components
     * @return Itself
     */
    public Panel addComponent(int index, Component component) {
        if(component == null) {
            throw new IllegalArgumentException("Cannot add null component");
        }
        synchronized(components) {
            if(components.contains(component)) {
                return this;
            }
            if(component.getParent() != null) {
                component.getParent().removeComponent(component);
            }
            if (index > components.size()) {
                index = components.size();
            }
            else if (index < 0) {
                index = 0;
            }
            components.add(index, component);
        }
        component.onAdded(this);
        invalidate();
        return this;
    }

    /**
     * This method is a shortcut for calling:
     * <pre>
     *     {@code
     *     component.setLayoutData(layoutData);
     *     panel.addComponent(component);
     *     }
     * </pre>
     * @param component Component to add to the panel
     * @param layoutData Layout data to assign to the component
     * @return Itself
     */
    public Panel addComponent(Component component, LayoutData layoutData) {
        if(component != null) {
            component.setLayoutData(layoutData);
            addComponent(component);
        }
        return this;
    }

    @Override
    public boolean containsComponent(Component component) {
        return component != null && component.hasParent(this);
    }

    @Override
    public boolean removeComponent(Component component) {
        if(component == null) {
            throw new IllegalArgumentException("Cannot remove null component");
        }
        synchronized(components) {
            int index = components.indexOf(component);
            if(index == -1) {
                return false;
            }
            if(getBasePane() != null && getBasePane().getFocusedInteractable() == component) {
                getBasePane().setFocusedInteractable(null);
            }
            components.remove(index);
        }
        component.onRemoved(this);
        invalidate();
        return true;
    }

    /**
     * Removes all child components from this panel
     * @return Itself
     */
    public Panel removeAllComponents() {
        synchronized(components) {
            for(Component component : new ArrayList<Component>(components)) {
                removeComponent(component);
            }
        }
        return this;
    }

    /**
     * Assigns a new layout manager to this panel, replacing the previous layout manager assigned. Please note that if
     * the panel is not empty at the time you assign a new layout manager, the existing components might not show up
     * where you expect them and their layout data property might need to be re-assigned.
     * @param layoutManager New layout manager this panel should be using
     * @return Itself
     */
    public synchronized Panel setLayoutManager(LayoutManager layoutManager) {
        if(layoutManager == null) {
            layoutManager = new AbsoluteLayout();
        }
        this.layoutManager = layoutManager;
        invalidate();
        return this;
    }

    /**
     * Returns the color used to override the default background color from the theme, if set. Otherwise {@code null} is
     * returned and whatever theme is assigned will be used to derive the fill color.
     * @return The color, if any, used to fill the panel's unused space instead of the theme's color
     */
    public TextColor getFillColorOverride() {
        return fillColorOverride;
    }

    /**
     * Sets an override color to be used instead of the theme's color for Panels when drawing unused space. If called
     * with {@code null}, it will reset back to the theme's color.
     * @param fillColor Color to draw the unused space with instead of what the theme definition says, no {@code null}
     *                  to go back to the theme definition
     */
    public void setFillColorOverride(TextColor fillColor) {
        this.fillColorOverride = fillColorOverride;
    }

    /**
     * Returns the layout manager assigned to this panel
     * @return Layout manager assigned to this panel
     */
    public LayoutManager getLayoutManager() {
        return layoutManager;
    }

    @Override
    public int getChildCount() {
        synchronized(components) {
            return components.size();
        }
    }

    @Override
    public Collection<Component> getChildren() {
        synchronized(components) {
            return new ArrayList<Component>(components);
        }
    }

    @Override
    protected ComponentRenderer<Panel> createDefaultRenderer() {
        return new ComponentRenderer<Panel>() {

            @Override
            public TerminalSize getPreferredSize(Panel component) {
                synchronized(components) {
                    cachedPreferredSize = layoutManager.getPreferredSize(components);
                }
                return cachedPreferredSize;
            }

            @Override
            public void drawComponent(TextGUIGraphics graphics, Panel component) {
                if(isInvalid()) {
                    layout(graphics.getSize());
                }

                // Reset the area
                graphics.applyThemeStyle(getThemeDefinition().getNormal());
                if (fillColorOverride != null) {
                    graphics.setBackgroundColor(fillColorOverride);
                }
                graphics.fill(' ');

                synchronized(components) {
                    for(Component child: components) {
                        TextGUIGraphics componentGraphics = graphics.newTextGraphics(child.getPosition(), child.getSize());
                        child.draw(componentGraphics);
                    }
                }
            }
        };
    }

    @Override
    public TerminalSize calculatePreferredSize() {
        if(cachedPreferredSize != null && !isInvalid()) {
            return cachedPreferredSize;
        }
        return super.calculatePreferredSize();
    }

    @Override
    public boolean isInvalid() {
        synchronized(components) {
            for(Component component: components) {
                if(component.isInvalid()) {
                    return true;
                }
            }
        }
        return super.isInvalid() || layoutManager.hasChanged();
    }    

    @Override
    public Interactable nextFocus(Interactable fromThis) {
        boolean chooseNextAvailable = (fromThis == null);

        synchronized(components) {
            for(Component component : components) {
                if(chooseNextAvailable) {
                    if(component instanceof Interactable && ((Interactable) component).isEnabled() && ((Interactable) component).isFocusable()) {
                        return (Interactable) component;
                    }
                    else if(component instanceof Container) {
                        Interactable firstInteractable = ((Container) (component)).nextFocus(null);
                        if(firstInteractable != null) {
                            return firstInteractable;
                        }
                    }
                    continue;
                }

                if(component == fromThis) {
                    chooseNextAvailable = true;
                    continue;
                }

                if(component instanceof Container) {
                    Container container = (Container) component;
                    if(fromThis.isInside(container)) {
                        Interactable next = container.nextFocus(fromThis);
                        if(next == null) {
                            chooseNextAvailable = true;
                        }
                        else {
                            return next;
                        }
                    }
                }
            }
            return null;
        }
    }

    @Override
    public Interactable previousFocus(Interactable fromThis) {
        boolean chooseNextAvailable = (fromThis == null);

        List<Component> revComponents = new ArrayList<Component>();
        synchronized(components) {
            revComponents.addAll(components);
        }
        Collections.reverse(revComponents);

        for (Component component : revComponents) {
            if (chooseNextAvailable) {
                if (component instanceof Interactable && ((Interactable)component).isEnabled() && ((Interactable)component).isFocusable()) {
                    return (Interactable) component;
                }
                if (component instanceof Container) {
                    Interactable lastInteractable = ((Container)(component)).previousFocus(null);
                    if (lastInteractable != null) {
                        return lastInteractable;
                    }
                }
                continue;
            }

            if (component == fromThis) {
                chooseNextAvailable = true;
                continue;
            }

            if (component instanceof Container) {
                Container container = (Container) component;
                if (fromThis.isInside(container)) {
                    Interactable next = container.previousFocus(fromThis);
                    if (next == null) {
                        chooseNextAvailable = true;
                    } else {
                        return next;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public boolean handleInput(KeyStroke key) {
        return false;
    }
    
    @Override
    public void updateLookupMap(InteractableLookupMap interactableLookupMap) {
        synchronized(components) {
            for(Component component: components) {
                if(component instanceof Container) {
                    ((Container)component).updateLookupMap(interactableLookupMap);
                }
                else if(component instanceof Interactable && ((Interactable)component).isEnabled() && ((Interactable)component).isFocusable()) {
                    interactableLookupMap.add((Interactable)component);
                }
            }
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();

        synchronized(components) {
            //Propagate
            for(Component component: components) {
                component.invalidate();
            }
        }
    }

    private void layout(TerminalSize size) {
        synchronized(components) {
            layoutManager.doLayout(size, components);
        }
    }
}
