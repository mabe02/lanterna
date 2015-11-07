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

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;

/**
 * AbstractComponent provides some good default behaviour for a Component, all components in Lanterna extends from this
 * class in some way. If you want to write your own component that isn't interactable or theme:able, you probably want
 * to extend from this class.
 * @author Martin
 * @param <T> Type of Renderer this component will use
 */
public abstract class AbstractComponent<T extends Component> implements Component {
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
        renderer = null; //Will be set on the first call to getRenderer()
    }
    
    /**
     * When you create a custom component, you need to implement this method and return a Renderer which is responsible
     * for taking care of sizing the component, rendering it and choosing where to place the cursor (if Interactable).
     * This value is intended to be overridden by custom themes.
     * @return Renderer to use when sizing and drawing this component
     */
    protected abstract ComponentRenderer<T> createDefaultRenderer();

    @SuppressWarnings("unchecked")
    protected ComponentRenderer<T> getRendererFromTheme(String className) {
        if(className == null) {
            return null;
        }
        try {
            return (ComponentRenderer<T>)Class.forName(className).newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    protected void runOnGUIThreadIfExistsOtherwiseRunDirect(Runnable runnable) {
        if(getTextGUI() != null && getTextGUI().getGUIThread() != null) {
            getTextGUI().getGUIThread().invokeLater(runnable);
        }
        else {
            runnable.run();
        }
    }

    public T setRenderer(ComponentRenderer<T> renderer) {
        this.renderer = renderer;
        return self();
    }

    @Override
    public synchronized ComponentRenderer<T> getRenderer() {
        if(renderer == null) {
            renderer = createDefaultRenderer();
            if(renderer == null) {
                throw new IllegalStateException(getClass() + " returns a null default renderer");
            }
        }
        return renderer;
    }

    @Override
    public void invalidate() {
        invalid = true;
    }

    @Override
    public T setSize(TerminalSize size) {
        synchronized(this) {
            this.size = size;
            return self();
        }
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
    public final T setPreferredSize(TerminalSize explicitPreferredSize) {
        synchronized(this) {
            this.explicitPreferredSize = explicitPreferredSize;
            return self();
        }
    }

    public TerminalSize calculatePreferredSize() {
        synchronized(this) {
            return getRenderer().getPreferredSize(self());
        }
    }

    @Override
    public T setPosition(TerminalPosition position) {
        synchronized(this) {
            this.position = position;
            return self();
        }
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
    public final void draw(final TextGUIGraphics graphics) {
        synchronized(this) {
            drawComponent(graphics);
            invalid = false;
        }
    }

    /**
     * Implement this method to define the logic to draw the component. The reason for this abstract method, instead of
     * overriding {@code Component.draw(..)} is because {@code AbstractComponent.draw(..)} locks the internal state,
     * calls this method and then resets the invalid flag. If you could override {@code draw}, you might forget to call
     * the super method and probably won't notice that your code keeps refreshing the GUI even though nothing has changed.
     * @param graphics TextGraphics to be used to draw the component
     */
    public void drawComponent(TextGUIGraphics graphics) {
        if(getRenderer() == null) {
            ComponentRenderer<T> renderer = getRendererFromTheme(graphics.getThemeDefinition(getClass()).getRenderer());
            if(renderer == null) {
                renderer = createDefaultRenderer();
                if(renderer == null) {
                    throw new IllegalStateException(getClass() + " returned a null default renderer");
                }
            }
            setRenderer(renderer);
        }
        //Delegate drawing the component to the renderer
        setSize(graphics.getSize());
        getRenderer().drawComponent(graphics, self());
    }

    @Override
    public T setLayoutData(LayoutData data) {
        synchronized(this) {
            if(layoutData != data) {
                layoutData = data;
                invalidate();
            }
            return self();
        }
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
    public boolean hasParent(Container parent) {
        if(this.parent == null) {
            return false;
        }
        Container recursiveParent = this.parent;
        while(recursiveParent != null) {
            if(recursiveParent == parent) {
                return true;
            }
            recursiveParent = recursiveParent.getParent();
        }
        return false;
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
        Container parent = getParent();
        if(parent == null) {
            return null;
        }
        return parent.toBasePane(getPosition().withRelative(position));
    }

    @Override
    public TerminalPosition toGlobal(TerminalPosition position) {
        Container parent = getParent();
        if(parent == null) {
            return null;
        }
        return parent.toGlobal(getPosition().withRelative(position));
    }

    @Override
    public Border withBorder(Border border) {
        synchronized(this) {
            border.setComponent(this);
            return border;
        }
    }

    @Override
    public T addTo(Panel panel) {
        synchronized(this) {
            panel.addComponent(this);
            return self();
        }
    }

    @Override
    public void onAdded(Container container) {
        synchronized(this) {
            parent = container;
        }
    }

    @Override
    public void onRemoved(Container container) {
        synchronized(this) {
            parent = null;
        }
    }

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T)this;
    }
}
