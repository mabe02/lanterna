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

import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.ThemeDefinition;

/**
 * Static non-interactive component that is typically rendered as a single line. Normally this component is used to
 * separate component from each other in situations where a bordered panel isn't ideal. By default the separator will
 * ask for a size of 1x1 so you'll need to make it bigger, either through the layout manager or by overriding the
 * preferred size.
 * @author Martin
 */
public class Separator extends AbstractComponent<Separator> {

    private final Direction direction;

    /**
     * Creates a new {@code Separator} for a specific direction, which will decide whether to draw a horizontal line or
     * a vertical line
     *
     * @param direction Direction of the line to draw within the separator
     */
    public Separator(Direction direction) {
        if(direction == null) {
            throw new IllegalArgumentException("Cannot create a separator with a null direction");
        }
        this.direction = direction;
    }

    /**
     * Returns the direction of the line drawn for this separator
     * @return Direction of the line drawn for this separator
     */
    public Direction getDirection() {
        return direction;
    }

    @Override
    protected DefaultSeparatorRenderer createDefaultRenderer() {
        return new DefaultSeparatorRenderer();
    }

    /**
     * Helper interface that doesn't add any new methods but makes coding new button renderers a little bit more clear
     */
    public static abstract class SeparatorRenderer implements ComponentRenderer<Separator> {
    }

    /**
     * This is the default separator renderer that is used if you don't override anything. With this renderer, the
     * separator has a preferred size of one but will take up the whole area it is given and fill that space with either
     * horizontal or vertical lines, depending on the direction of the {@code Separator}
     */
    public static class DefaultSeparatorRenderer extends SeparatorRenderer {
        @Override
        public TerminalSize getPreferredSize(Separator component) {
            return TerminalSize.ONE;
        }

        @Override
        public void drawComponent(TextGUIGraphics graphics, Separator component) {
            ThemeDefinition themeDefinition = component.getThemeDefinition();
            graphics.applyThemeStyle(themeDefinition.getNormal());
            char character = themeDefinition.getCharacter(component.getDirection().name().toUpperCase(),
                    component.getDirection() == Direction.HORIZONTAL ? Symbols.SINGLE_LINE_HORIZONTAL : Symbols.SINGLE_LINE_VERTICAL);
            graphics.fill(character);
        }
    }
}
