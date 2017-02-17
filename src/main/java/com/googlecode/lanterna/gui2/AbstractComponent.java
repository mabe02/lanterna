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
 * Copyright (C) 2010-2017 Martin Berglund
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.bundle.LanternaThemes;
import com.googlecode.lanterna.graphics.Theme;
import com.googlecode.lanterna.graphics.ThemeDefinition;

/**
 * AbstractComponent provides some good default behaviour for a {@code Component}, all components in Lanterna extends
 * from this class in some way. If you want to write your own component that isn't interactable or theme:able, you
 * probably want to extend from this class.
 * <p>
 * The way you want to declare your new {@code Component} is to pass in itself as the generic parameter, like this:
 * <pre>
 * {@code
 *     public class MyComponent extends AbstractComponent<MyComponent> {
 *         ...
 *     }
 * }
 * </pre>
 * This was, the component renderer will be correctly setup type-wise and you will need to do fewer typecastings when
 * you implement the drawing method your new component.
 *
 * @author Martin
 * @param <T> Should always be itself, this value will be used for the {@code ComponentRenderer} declaration
 */
public abstract class AbstractComponent<T extends Component> implements Component {
    /**
     * Manually set renderer
     */
    private ComponentRenderer<T> overrideRenderer;
    /**
     * If overrideRenderer is not set, this is used instead if not null, set by the theme
     */
    private ComponentRenderer<T> themeRenderer;

    /**
     * To keep track of the theme that created the themeRenderer, so we can reset it if the theme changes
     */
    private Theme themeRenderersTheme;

    /**
     * If the theme had nothing for this component and no override is set, this is the third fallback
     */
    private ComponentRenderer<T> defaultRenderer;

    private Container parent;
    private TerminalSize size;
    private TerminalSize explicitPreferredSize;   //This is keeping the value set by the user (if setPreferredSize() is used)
    private TerminalPosition position;
    private Theme themeOverride;
    private LayoutData layoutData;
    private boolean invalid;

    /**
     * Default constructor
     */
    public AbstractComponent() {
        size = TerminalSize.ZERO;
        position = TerminalPosition.TOP_LEFT_CORNER;
        explicitPreferredSize = null;
        layoutData = null;
        invalid = true;
        parent = null;
        overrideRenderer = null;
        themeRenderer = null;
        themeRenderersTheme = null;
        defaultRenderer = null;
    }
    
    /**
     * When you create a custom component, you need to implement this method and return a Renderer which is responsible
     * for taking care of sizing the component, rendering it and choosing where to place the cursor (if Interactable).
     * This value is intended to be overridden by custom themes.
     * @return Renderer to use when sizing and drawing this component
     */
    protected abstract ComponentRenderer<T> createDefaultRenderer();

    /**
     * Takes a {@code Runnable} and immediately executes it if this is called on the designated GUI thread, otherwise
     * schedules it for later invocation.
     * @param runnable {@code Runnable} to execute on the GUI thread
     */
    protected void runOnGUIThreadIfExistsOtherwiseRunDirect(Runnable runnable) {
        if(getTextGUI() != null && getTextGUI().getGUIThread() != null) {
            getTextGUI().getGUIThread().invokeLater(runnable);
        }
        else {
            runnable.run();
        }
    }

    /**
     * Explicitly sets the {@code ComponentRenderer} to be used when drawing this component. This will override whatever
     * the current theme is suggesting or what the default renderer is. If you call this with {@code null}, the override
     * is cleared.
     * @param renderer {@code ComponentRenderer} to be used when drawing this component
     * @return Itself
     */
    public T setRenderer(ComponentRenderer<T> renderer) {
        this.overrideRenderer = renderer;
        return self();
    }

    @Override
    public synchronized ComponentRenderer<T> getRenderer() {
        // First try the override
        if(overrideRenderer != null) {
            return overrideRenderer;
        }

        // Then try to create and return a renderer from the theme
        Theme currentTheme = getTheme();
        if((themeRenderer == null && getBasePane() != null) ||
                // Check if the theme has changed
                themeRenderer != null && currentTheme != themeRenderersTheme) {

            themeRenderer = currentTheme.getDefinition(getClass()).getRenderer(selfClass());
            if(themeRenderer != null) {
                themeRenderersTheme = currentTheme;
            }
        }
        if(themeRenderer != null) {
            return themeRenderer;
        }

        // Finally, fallback to the default renderer
        if(defaultRenderer == null) {
            defaultRenderer = createDefaultRenderer();
            if(defaultRenderer == null) {
                throw new IllegalStateException(getClass() + " returned a null default renderer");
            }
        }
        return defaultRenderer;
    }

    @Override
    public void invalidate() {
        invalid = true;
    }

    @Override
    public synchronized T setSize(TerminalSize size) {
        this.size = size;
        return self();
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
    public final synchronized T setPreferredSize(TerminalSize explicitPreferredSize) {
        this.explicitPreferredSize = explicitPreferredSize;
        return self();
    }

    /**
     * Invokes the component renderer's size calculation logic and returns the result. This value represents the
     * preferred size and isn't necessarily what it will eventually be assigned later on.
     * @return Size that the component renderer believes the component should be
     */
    protected synchronized TerminalSize calculatePreferredSize() {
        return getRenderer().getPreferredSize(self());
    }

    @Override
    public synchronized T setPosition(TerminalPosition position) {
        this.position = position;
        return self();
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
    public final synchronized void draw(final TextGUIGraphics graphics) {
        //Delegate drawing the component to the renderer
        setSize(graphics.getSize());
        onBeforeDrawing();
        getRenderer().drawComponent(graphics, self());
        onAfterDrawing(graphics);
        invalid = false;
    }

    /**
     * This method is called just before the component's renderer is invoked for the drawing operation. You can use this
     * hook to do some last-minute adjustments to the component, as an alternative to coding it into the renderer
     * itself. The component should have the correct size and position at this point, if you call {@code getSize()} and
     * {@code getPosition()}.
     */
    protected void onBeforeDrawing() {
        //No operation by default
    }

    /**
     * This method is called immediately after the component's renderer has finished the drawing operation. You can use
     * this hook to do some post-processing if you need, as an alternative to coding it into the renderer. The
     * {@code TextGUIGraphics} supplied is the same that was fed into the renderer.
     * @param graphics Graphics object you can use to manipulate the appearance of the component
     */
    @SuppressWarnings("EmptyMethod")
    protected void onAfterDrawing(TextGUIGraphics graphics) {
        //No operation by default
    }

    @Override
    public synchronized T setLayoutData(LayoutData data) {
        if(layoutData != data) {
            layoutData = data;
            invalidate();
        }
        return self();
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
    public synchronized Theme getTheme() {
        if(themeOverride != null) {
            return themeOverride;
        }
        else if(parent != null) {
            return parent.getTheme();
        }
        else if(getBasePane() != null) {
            return getBasePane().getTheme();
        }
        else {
            return LanternaThemes.getDefaultTheme();
        }
    }

    @Override
    public ThemeDefinition getThemeDefinition() {
        return getTheme().getDefinition(getClass());
    }

    @Override
    public synchronized Component setTheme(Theme theme) {
        themeOverride = theme;
        invalidate();
        return this;
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
    public synchronized Border withBorder(Border border) {
        border.setComponent(this);
        return border;
    }

    @Override
    public synchronized T addTo(Panel panel) {
        panel.addComponent(this);
        return self();
    }

    @Override
    public synchronized void onAdded(Container container) {
        parent = container;
    }

    @Override
    public synchronized void onRemoved(Container container) {
        parent = null;
        themeRenderer = null;
    }

    /**
     * This is a little hack to avoid doing typecasts all over the place when having to return {@code T}. Credit to
     * avl42 for this one!
     * @return Itself, but as type T
     */
    @SuppressWarnings("unchecked")
    protected T self() {
        return (T)this;
    }

    @SuppressWarnings("unchecked")
    private Class<T> selfClass() {
        return (Class<T>)getClass();
    }
}
