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
 * Copyright (C) 2010-2012 Martin
 */
package com.googlecode.lanterna.gui.layout;

import com.googlecode.lanterna.gui.Component;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Layout manager inspired by java.awt.BorderLayout
 * @author Martin
 */
public class BorderLayout implements LayoutManager {
    public static final LayoutParameter CENTER = new LayoutParameter("BorderLayout.CENTER");
    public static final LayoutParameter LEFT = new LayoutParameter("BorderLayout.LEFT");
    public static final LayoutParameter RIGHT = new LayoutParameter("BorderLayout.RIGHT");
    public static final LayoutParameter TOP = new LayoutParameter("BorderLayout.TOP");
    public static final LayoutParameter BOTTOM = new LayoutParameter("BorderLayout.BOTTOM");
    private static final Set<LayoutParameter> BORDER_LAYOUT_POSITIONS = 
            Collections.unmodifiableSet(new HashSet<LayoutParameter>(Arrays.asList(
                CENTER,
                LEFT,
                RIGHT,
                TOP,
                BOTTOM)));
    
    private final Map<LayoutParameter, Component> components;

    public BorderLayout() {
        components = new IdentityHashMap<LayoutParameter, Component>(5);      
    }

    @Override
    public void addComponent(Component component, LayoutParameter... parameters) {
        if(parameters.length == 0) {
            parameters = new LayoutParameter[] { CENTER };
        }
        else if(parameters.length > 1) {
            throw new IllegalArgumentException("Calling BorderLayout.addComponent with more than one "
                    + "layout parameter");
        }
        else if(!BORDER_LAYOUT_POSITIONS.contains(parameters[0])) {
            throw new IllegalArgumentException("Calling BorderLayout.addComponent layout parameter " +
                    parameters[0] + " is not allowed");
        }
        else if(component == null) {
            throw new IllegalArgumentException("Calling BorderLayout.addComponent component parameter " +
                    "set to null is not allowed");
        }
        
        //Make sure we don't add the same component twice
        removeComponent(component);
        synchronized(components) {
            components.put(parameters[0], component);        
        }
    }

    @Override
    public void removeComponent(Component component) {
        synchronized(components) {
            for(LayoutParameter parameter: BORDER_LAYOUT_POSITIONS) {
                if(components.get(parameter) == component) {
                    components.remove(parameter);
                    return;
                }
            }
        }
    }

    @Override
    public TerminalSize getPreferredSize() {      
        synchronized(components) {
            int preferredHeight = 
                    (components.containsKey(TOP) ? components.get(TOP).getPreferredSize().getRows() : 0)
                    +
                    Math.max(
                        components.containsKey(LEFT) ? components.get(LEFT).getPreferredSize().getRows() : 0,
                        Math.max(
                            components.containsKey(CENTER) ? components.get(CENTER).getPreferredSize().getRows() : 0,
                            components.containsKey(RIGHT) ? components.get(RIGHT).getPreferredSize().getRows() : 0))
                    +
                    (components.containsKey(BOTTOM) ? components.get(BOTTOM).getPreferredSize().getRows() : 0);
            
            int preferredWidth = 
                    Math.max(
                        (components.containsKey(LEFT) ? components.get(LEFT).getPreferredSize().getColumns() : 0) +
                            (components.containsKey(CENTER) ? components.get(CENTER).getPreferredSize().getColumns() : 0) +
                            (components.containsKey(RIGHT) ? components.get(RIGHT).getPreferredSize().getColumns() : 0),
                        Math.max(
                            components.containsKey(TOP) ? components.get(TOP).getPreferredSize().getColumns() : 0,
                            components.containsKey(BOTTOM) ? components.get(BOTTOM).getPreferredSize().getColumns() : 0));
            return new TerminalSize(preferredWidth, preferredHeight);
        }
    }

    @Override
    public List<? extends LaidOutComponent> layout(TerminalSize layoutArea) {
        int availableHorizontalSpace = layoutArea.getColumns();
        int availableVerticalSpace = layoutArea.getRows();
        List<LaidOutComponent> finalLayout = new ArrayList<LaidOutComponent>();
        
        synchronized(components) {
            //We'll need this later on
            int topComponentHeight = 0;
            int leftComponentWidth = 0;
            
            //First allocate the top
            if(components.containsKey(TOP)) {
                Component topComponent = components.get(TOP);
                topComponentHeight = Math.min(topComponent.getPreferredSize().getRows(), availableVerticalSpace);
                finalLayout.add(
                        new DefaultLaidOutComponent(
                            topComponent, 
                            new TerminalSize(
                                availableHorizontalSpace, 
                                topComponentHeight),
                            new TerminalPosition(0, 0)));
                availableVerticalSpace -= topComponentHeight;
            }
            
            //Next allocate the bottom
            if(components.containsKey(BOTTOM)) {
                Component bottomComponent = components.get(BOTTOM);
                int bottomComponentHeight = Math.min(bottomComponent.getPreferredSize().getRows(), availableVerticalSpace);
                finalLayout.add(
                        new DefaultLaidOutComponent(
                            bottomComponent, 
                            new TerminalSize(
                                availableHorizontalSpace, 
                                bottomComponentHeight),
                            new TerminalPosition(0, layoutArea.getRows() - bottomComponentHeight)));
                availableVerticalSpace -= bottomComponentHeight;
            }
            
            //Now divide the remaining space between LEFT, CENTER and RIGHT
            if(components.containsKey(LEFT)) {
                Component leftComponent = components.get(LEFT);
                leftComponentWidth = Math.min(leftComponent.getPreferredSize().getColumns(), availableHorizontalSpace);
                finalLayout.add(
                        new DefaultLaidOutComponent(
                            leftComponent, 
                            new TerminalSize(
                                leftComponentWidth, 
                                availableVerticalSpace),
                            new TerminalPosition(0, topComponentHeight)));
                availableHorizontalSpace -= leftComponentWidth;
            }
            if(components.containsKey(RIGHT)) {
                Component rightComponent = components.get(RIGHT);
                int rightComponentWidth = Math.min(rightComponent.getPreferredSize().getColumns(), availableHorizontalSpace);
                finalLayout.add(
                        new DefaultLaidOutComponent(
                            rightComponent, 
                            new TerminalSize(
                                rightComponentWidth, 
                                availableVerticalSpace),
                            new TerminalPosition(layoutArea.getColumns() - rightComponentWidth, topComponentHeight)));
                availableHorizontalSpace -= rightComponentWidth;
            }
            if(components.containsKey(CENTER)) {
                Component centerComponent = components.get(CENTER);
                finalLayout.add(
                        new DefaultLaidOutComponent(
                            centerComponent, 
                            new TerminalSize(
                                availableHorizontalSpace, 
                                availableVerticalSpace),
                            new TerminalPosition(leftComponentWidth, topComponentHeight)));
            }
        }         
        return finalLayout;
    }

    @Override
    public boolean maximisesVertically() {
        return false;
    }

    @Override
    public boolean maximisesHorisontally() {
        return false;
    }    
}
