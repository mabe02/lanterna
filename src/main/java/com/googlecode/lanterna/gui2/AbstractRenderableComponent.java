package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalSize;

/**
 * Created by martin on 03/10/14.
 */
public abstract class AbstractRenderableComponent<T extends ComponentRenderer> extends AbstractComponent {
    private T renderer;

    protected AbstractRenderableComponent() {
        renderer = createDefaultRenderer();
        if(renderer == null) {
            throw new IllegalArgumentException(getClass() + " returns a null default renderer");
        }
    }

    protected abstract T createDefaultRenderer();

    public TerminalSize calculatePreferredSize() {
        return renderer.getPreferredSize(this);
    }

    @Override
    public void drawComponent(TextGUIGraphics graphics) {
        //This will override the default renderer with the one from the theme, if there was one
        updateRenderer(graphics.getThemeDefinition(getClass()).getRenderer());

        //Delegate drawing the component to the renderer
        renderer.drawComponent(graphics, this);
    }

    protected void updateRenderer(String className) {
        if(className == null) {
            return;
        }
        if(renderer.getClass().getName().equals(className)) {
            return;
        }
        try {
            Object newRenderer = Class.forName(className).newInstance();
            setRenderer((T)newRenderer);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    protected void setRenderer(T renderer) {
        if(renderer == null) {
            renderer = createDefaultRenderer();
            if(renderer == null) {
                throw new IllegalStateException(getClass() + " returned a null default renderer");
            }
        }
        this.renderer = renderer;
    }

    protected T getRenderer() {
        return renderer;
    }
}
