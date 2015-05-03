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

import java.util.List;

/**
 * Simple layout manager the puts all components on a single line
 */
public class LinearLayout implements LayoutManager {

    private final Direction direction;

    public LinearLayout() {
        this(Direction.VERTICAL);
    }

    public LinearLayout(Direction direction) {
        this.direction = direction;
    }

    @Override
    public TerminalSize getPreferredSize(List<Component> components) {
        if(direction == Direction.VERTICAL) {
            return getPreferredSizeVertically(components);
        }
        else {
            return getPreferredSizeHorizontally(components);
        }
    }

    private TerminalSize getPreferredSizeVertically(List<Component> components) {
        int maxWidth = 0;
        int height = 0;
        for(Component component: components) {
            TerminalSize preferredSize = component.getPreferredSize();
            if(maxWidth < preferredSize.getColumns()) {
                maxWidth = preferredSize.getColumns();
            }
            height += preferredSize.getRows();
        }
        return new TerminalSize(maxWidth, height);
    }

    private TerminalSize getPreferredSizeHorizontally(List<Component> components) {
        int maxHeight = 0;
        int width = 0;
        for(Component component: components) {
            TerminalSize preferredSize = component.getPreferredSize();
            if(maxHeight < preferredSize.getRows()) {
                maxHeight = preferredSize.getRows();
            }
            width += preferredSize.getColumns();
        }
        return new TerminalSize(width, maxHeight);
    }

    @Override
    public void doLayout(TerminalSize area, List<Component> components) {
        if(direction == Direction.VERTICAL) {
            doVerticalLayout(area, components);
        }
        else {
            doHorizontalLayout(area, components);
        }
    }

    private void doVerticalLayout(TerminalSize area, List<Component> components) {
        int remainingVerticalSpace = area.getRows();
        int availableHorizontalSpace = area.getColumns();
        for(Component component: components) {
            if(remainingVerticalSpace <= 0) {
                component.setPosition(TerminalPosition.TOP_LEFT_CORNER);
                component.setSize(TerminalSize.ZERO);
            }
            else {
                TerminalSize preferredSize = component.getPreferredSize();
                TerminalSize decidedSize = new TerminalSize(
                        Math.min(availableHorizontalSpace, preferredSize.getColumns()),
                        Math.min(remainingVerticalSpace, preferredSize.getRows()));

                component.setPosition(component.getPosition().withColumn(0).withRow(area.getRows() - remainingVerticalSpace));
                component.setSize(component.getSize().with(decidedSize));
                remainingVerticalSpace -= decidedSize.getRows();
            }
        }
    }

    private void doHorizontalLayout(TerminalSize area, List<Component> components) {
        int remainingHorizontalSpace = area.getColumns();
        int availableVerticalSpace = area.getRows();
        for(Component component: components) {
            if(remainingHorizontalSpace <= 0) {
                component.setPosition(TerminalPosition.TOP_LEFT_CORNER);
                component.setSize(TerminalSize.ZERO);
            }
            else {
                TerminalSize preferredSize = component.getPreferredSize();
                TerminalSize decidedSize = new TerminalSize(
                        Math.min(remainingHorizontalSpace, preferredSize.getColumns()),
                        Math.min(availableVerticalSpace, preferredSize.getRows()));

                component.setPosition(component.getPosition().withRow(0).withColumn(area.getColumns() - remainingHorizontalSpace));
                component.setSize(component.getSize().with(decidedSize));
                remainingHorizontalSpace -= decidedSize.getColumns();
            }
        }
    }
}
