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

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Simple container for other components
 * @author Martin
 */
public class Panel extends AbstractComponent<Panel> implements Container {
    private final List<Component> components;
    private LayoutManager layoutManager;
    private TerminalSize cachedPreferredSize;
    private boolean needsReLayout;

    public Panel() {
        components = new ArrayList<Component>();
        layoutManager = new LinearLayout();
        cachedPreferredSize = null;
        needsReLayout = false;
    }
    
    public Panel addComponent(Component component) {
        if(component == null) {
            throw new IllegalArgumentException("Cannot add null component");
        }
        synchronized(components) {
            if(components.contains(component)) {
                return this;
            }
            components.add(component);
            component.onAdded(this);
        }
        invalidateStructure();
        return this;
    }

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
            component.onRemoved(this);
        }
        invalidateStructure();
        return true;
    }

    public Panel removeAllComponents() {
        synchronized(components) {
            for(Component component: new ArrayList<Component>(components)) {
                removeComponent(component);
            }
        }
        return this;
    }
    
    public Panel setLayoutManager(LayoutManager layoutManager) {
        if(layoutManager == null) {
            layoutManager = new AbsoluteLayout();
        }
        this.layoutManager = layoutManager;
        invalidateStructure();
        return this;
    }

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
                cachedPreferredSize = layoutManager.getPreferredSize(components);
                return cachedPreferredSize;
            }

            @Override
            public void drawComponent(TextGUIGraphics graphics, Panel component) {
                if(isInvalid()) {
                    layout(graphics.getSize());
                }
                for(Component child: components) {
                    TextGUIGraphics componentGraphics = graphics.newTextGraphics(child.getPosition(), child.getSize());
                    child.draw(componentGraphics);
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
        for(Component component: components) {
            if(component.isInvalid()) {
                return true;
            }
        }
        return super.isInvalid() || layoutManager.hasChanged() || needsReLayout;
    }    

    @Override
    public Interactable nextFocus(Interactable fromThis) {
        boolean chooseNextAvailable = (fromThis == null);

        for (Component component : components) {
            if (chooseNextAvailable) {
                if (component instanceof Interactable) {
                    return (Interactable) component;
                }
                else if (component instanceof Container) {
                    Interactable firstInteractable = ((Container)(component)).nextFocus(null);
                    if (firstInteractable != null) {
                        return firstInteractable;
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
                    Interactable next = container.nextFocus(fromThis);
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
    public Interactable previousFocus(Interactable fromThis) {
        boolean chooseNextAvailable = (fromThis == null);

        List<Component> revComponents = new ArrayList<Component>(components);
        Collections.reverse(revComponents);

        for (Component component : revComponents) {
            if (chooseNextAvailable) {
                if (component instanceof Interactable) {
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
        for(Component component: components) {
            if(component instanceof Container) {
                ((Container)component).updateLookupMap(interactableLookupMap);
            }
            else if(component instanceof Interactable) {
                interactableLookupMap.add((Interactable)component);
            }
        }
    }

    private void invalidateStructure() {
        needsReLayout = true;
        invalidate();
    }

    private void layout(TerminalSize size) {
        layoutManager.doLayout(size, components);
        needsReLayout = false;
    }
}
