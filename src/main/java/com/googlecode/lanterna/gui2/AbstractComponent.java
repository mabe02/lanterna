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
 * Copyright (C) 2010-2014 Martin
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;

/**
 * AbstractComponent provides some good default behaviour for a Component, all components in Lanterna extends from this
 * class in some way. If you want to write your own component that isn't interactable or theme:able, you probably want
 * to extend from this class.
 * @author Martin
 * @param <T> Type of Renderer this component will use
 */
public abstract class AbstractComponent<T extends AbstractComponent> implements Component {
    private ComponentRenderer<T> renderer;
    private Container parent;
    private TerminalSize size;
    private TerminalSize explicitPreferredSize;   //This is keeping the value set by the user (if setPreferredSize() is used)
    private TerminalPosition position;
    private LayoutData layoutData;
    private boolean invalid;

    public AbstractComponent() {
        size = TerminalSize.ZERO;
        position = TerminalPosition.TOP_LEFT_CORNER;
        explicitPreferredSize = null;
        layoutData = null;
        invalid = true;
        parent = null;
        renderer = createDefaultRenderer();
        
        if(renderer == null) {
            throw new IllegalArgumentException(getClass() + " returns a null default renderer");
        }
    }
    
    /**
     * When you create a custom component, you need to implement this method and return a Renderer which is responsible
     * for taking care of sizing the component, rendering it and choosing where to place the cursor (if Interactable).
     * This value is intended to be overridden by custom themes.
     * @return Renderer to use when sizing and drawing this component
     */
    protected abstract ComponentRenderer<T> createDefaultRenderer();

    protected void updateRenderer(String className) {
        if(className == null) {
            return;
        }
        if(renderer.getClass().getName().equals(className)) {
            return;
        }
        try {
            Object newRenderer = Class.forName(className).newInstance();
            setRenderer((ComponentRenderer<T>)newRenderer);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    protected void setRenderer(ComponentRenderer<T> renderer) {
        if(renderer == null) {
            renderer = createDefaultRenderer();
            if(renderer == null) {
                throw new IllegalStateException(getClass() + " returned a null default renderer");
            }
        }
        this.renderer = renderer;
    }

    protected ComponentRenderer<T> getRenderer() {
        return renderer;
    }

    protected void invalidate() {
        invalid = true;
    }

    @Override
    public AbstractComponent setSize(TerminalSize size) {
        this.size = size;
        return this;
    }

    @Override
    public TerminalSize getSize() {
        return size;
    }

    @Override
    public final TerminalSize getPreferredSize() {
        if(explicitPreferredSize != null) {
            return explicitPreferredSize;
        }
        else {
            return calculatePreferredSize();
        }
    }

    @Override
    public final AbstractComponent setPreferredSize(TerminalSize explicitPreferredSize) {
        this.explicitPreferredSize = explicitPreferredSize;
        return this;
    }

    public TerminalSize calculatePreferredSize() {
        return renderer.getPreferredSize((T)this);
    }

    @Override
    public AbstractComponent setPosition(TerminalPosition position) {
        this.position = position;
        return this;
    }

    @Override
    public TerminalPosition getPosition() {
        return position;
    }
    
    @Override
    public boolean isInvalid() {
        return invalid;
    }

    @Override
    public final void draw(TextGUIGraphics graphics) {
        drawComponent(graphics);
        invalid = false;
    }

    /**
     * Implement this method to define the logic to draw the component. The reason for this abstract method, instead of
     * overriding {@code Component.draw(..)} is because {@code AbstractComponent.draw(..)} calls this method and then
     * resets the invalid flag. If you could override {@code draw}, you might forget to call the super method and
     * probably won't notice that your code keeps refreshing the GUI even though nothing has changed.
     * @param graphics TextGraphics to be used to draw the component
     */
    public void drawComponent(TextGUIGraphics graphics) {
        //This will override the default renderer with the one from the theme, if there was one
        updateRenderer(graphics.getThemeDefinition(getClass()).getRenderer());

        //Delegate drawing the component to the renderer
        setSize(graphics.getSize());
        renderer.drawComponent(graphics, (T)this);
    }

    @Override
    public AbstractComponent setLayoutData(LayoutData data) {
        if(layoutData != data) {
            layoutData = data;
            invalidate();
        }
        return this;
    }

    @Override
    public LayoutData getLayoutData() {
        return layoutData;
    }

    @Override
    public Container getParent() {
        return parent;
    }

    @Override
    public TextGUI getTextGUI() {
        if(parent == null) {
            return null;
        }
        return parent.getTextGUI();
    }
    
    @Override
    public boolean isInside(Container container) {
        Component test = this;
        while(test.getParent() != null) {
            if(test.getParent() == container) {
                return true;
            }
            test = test.getParent();
        }
        return false;
    }

    @Override
    public BasePane getBasePane() {
        if(parent == null) {
            return null;
        }
        return parent.getBasePane();
    }

    @Override
    public TerminalPosition toBasePane(TerminalPosition position) {
        return getParent().toBasePane(getPosition().withRelative(position));
    }

    @Override
    public Border withBorder(Border border) {
        border.setComponent(this);
        return border;
    }

    @Override
    public void onAdded(Container container) {
        parent = container;
    }

    @Override
    public void onRemoved(Container container) {
        parent = null;
    }
}
