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

import com.googlecode.lanterna.TerminalSize;

/**
 * Created by martin on 03/10/14.
 * @param <T>
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

    @Override
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
