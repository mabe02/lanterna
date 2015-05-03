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

    public Separator(Direction direction) {
        if(direction == null) {
            throw new IllegalArgumentException("Cannot create a separator with a null direction");
        }
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    protected DefaultSeparatorRenderer createDefaultRenderer() {
        return new DefaultSeparatorRenderer();
    }

    public static abstract class SeparatorRenderer implements ComponentRenderer<Separator> {
    }

    public static class DefaultSeparatorRenderer extends SeparatorRenderer {
        @Override
        public TerminalSize getPreferredSize(Separator component) {
            return TerminalSize.ONE;
        }

        @Override
        public void drawComponent(TextGUIGraphics graphics, Separator component) {
            ThemeDefinition themeDefinition = graphics.getThemeDefinition(Separator.class);
            graphics.applyThemeStyle(themeDefinition.getNormal());
            char character = themeDefinition.getCharacter(component.getDirection().name().toUpperCase(),
                    component.getDirection() == Direction.HORIZONTAL ? Symbols.SINGLE_LINE_HORIZONTAL : Symbols.SINGLE_LINE_VERTICAL);
            graphics.fill(character);
        }
    }
}
