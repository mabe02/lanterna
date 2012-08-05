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
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Martin
 */
public abstract class AxisLayout implements ContainerLayout
{
    private final List<AxisLayoutComponent> componentList;
    private int padding;
    
    AxisLayout()
    {
        this.componentList = new ArrayList<AxisLayoutComponent>();
        this.padding = 0;
    }

    @Override
    public void addComponent(Component component, Object modifiers)
    {
        if(modifiers instanceof SizePolicy == false)
            modifiers = SizePolicy.CONSTANT;
        componentList.add(new AxisLayoutComponent(component, (SizePolicy)modifiers));
    }

    @Override
    public void removeComponent(Component component)
    {
        Iterator<AxisLayoutComponent> iterator = componentList.iterator();
        while(iterator.hasNext()) {
            if(iterator.next().component == component) {
                iterator.remove();
                return;
            }
        }
    }

    public boolean isMaximising()
    {
        for(AxisLayoutComponent axisLayoutComponent: componentList)
            if(axisLayoutComponent.sizePolicy == SizePolicy.MAXIMUM)
                return true;

        return false;
    }

    public void setPadding(int padding)
    {
        this.padding = padding;
    }

    @Override
    public TerminalSize getPreferredSize()
    {
        final TerminalSize preferredSize = new TerminalSize(0, 0);
        for(AxisLayoutComponent axisLayoutComponent: componentList) {
            final TerminalSize componentPreferredSize = axisLayoutComponent.component.getPreferredSize();
            setMajorAxis(preferredSize, getMajorAxis(preferredSize) + getMajorAxis(componentPreferredSize));
            setMinorAxis(preferredSize, Math.max(getMinorAxis(preferredSize), getMinorAxis(componentPreferredSize)));
        }
        setMajorAxis(preferredSize, getMajorAxis(preferredSize) + (padding * componentList.size()));
        return preferredSize;
    }

    @Override
    public List<LaidOutComponent> layout(TerminalSize layoutArea)
    {
        List<AxisLaidOutComponent> result = new ArrayList<AxisLaidOutComponent>();
        List<AxisLaidOutComponent> growingComponents = new ArrayList<AxisLaidOutComponent>();

        final int availableMinorAxisSpace = getMinorAxis(layoutArea);
        final int availableMajorAxisSpace = getMajorAxis(layoutArea);
        int usedMajorAxisSpace = 0;

        //First, try to lay out all components and give them all the space they ask for
        for(AxisLayoutComponent axisLayoutComponent: componentList) {
            TerminalSize componentPreferredSize = axisLayoutComponent.component.getPreferredSize();
            final int componentPreferredMajorAxisSize = getMajorAxis(componentPreferredSize);
            
            //Skip these
            if(componentPreferredMajorAxisSize < 0)
                continue;

            //This will be re-calculated later
            TerminalPosition componentTopLeft = new TerminalPosition(0, 0);
            
            TerminalSize componentSize = new TerminalSize(0,0);
            setMinorAxis(componentSize, availableMinorAxisSpace);
            setMajorAxis(componentSize, componentPreferredMajorAxisSize);
            usedMajorAxisSpace += (getMajorAxis(componentSize) + padding);
            
            AxisLaidOutComponent laidOutComponent = new AxisLaidOutComponent(axisLayoutComponent.component, componentSize, componentTopLeft);
            result.add(laidOutComponent);
            if(axisLayoutComponent.sizePolicy != SizePolicy.CONSTANT)
                growingComponents.add(laidOutComponent);
        }
        
        //Now, if we have used too much space, start shrinking components until 
        //they all fit, using scrollable components first
        while(usedMajorAxisSpace > availableMajorAxisSpace) {
            boolean foundSomethingToShrink = false;
            for(AxisLaidOutComponent laidOutComponent: result) {
                if(laidOutComponent.component.isScrollable() && 
                        getMajorAxis(laidOutComponent.getSize()) > 0) {
                    foundSomethingToShrink = true;
                    setMajorAxis(laidOutComponent.getSize(), getMajorAxis(laidOutComponent.getSize()) - 1);
                    usedMajorAxisSpace--;
                    
                    //Don't shrink more than necessary
                    if(usedMajorAxisSpace <= availableMajorAxisSpace)
                        break;
                }
            }
            if(!foundSomethingToShrink)
                break;  //There is nothing more to shrink
        }
        
        //If we have still used too much space, start shrinking regular components
        while(usedMajorAxisSpace > availableMajorAxisSpace) {
            boolean foundSomethingToShrink = false;
            for(AxisLaidOutComponent laidOutComponent: result) {
                if(getMajorAxis(laidOutComponent.getSize()) > 0) {
                    foundSomethingToShrink = true;
                    setMajorAxis(laidOutComponent.getSize(), getMajorAxis(laidOutComponent.getSize()) - 1);
                    usedMajorAxisSpace--;
                    
                    //Don't shrink more than necessary
                    if(usedMajorAxisSpace <= availableMajorAxisSpace)
                        break;
                }
            }
            if(!foundSomethingToShrink)
                break;  //There is nothing more to shrink
        }

        //At this point, in case there is spare space to use, let's divide it
        //between the growing components
        while(!growingComponents.isEmpty() && availableMajorAxisSpace > usedMajorAxisSpace) {
            for(AxisLaidOutComponent laidOutComponent: growingComponents) {
                setMajorAxis(laidOutComponent.size, getMajorAxis(laidOutComponent.size) + 1);
                usedMajorAxisSpace++;
                if(availableMajorAxisSpace == usedMajorAxisSpace)
                    break;
            }
        }

        //Finally, recalculate the topLeft position of each component
        int nextMajorPosition = 0;
        for(AxisLaidOutComponent laidOutComponent: result) {
            setMajorAxis(laidOutComponent.topLeftPosition, nextMajorPosition);
            nextMajorPosition += getMajorAxis(laidOutComponent.size) + padding;
        }

        return (List)result;
    }

    protected abstract void setMajorAxis(TerminalSize terminalSize, int majorAxisValue);
    protected abstract void setMinorAxis(TerminalSize terminalSize, int minorAxisValue);
    protected abstract void setMajorAxis(TerminalPosition terminalPosition, int majorAxisValue);
    protected abstract int getMajorAxis(TerminalSize terminalSize);
    protected abstract int getMinorAxis(TerminalSize terminalSize);

    protected static class AxisLayoutComponent
    {
        public Component component;
        public SizePolicy sizePolicy;

        public AxisLayoutComponent(Component component, SizePolicy sizePolicy)
        {
            this.component = component;
            this.sizePolicy = sizePolicy;
        }
    }

    private class AxisLaidOutComponent implements ContainerLayout.LaidOutComponent
    {
        final Component component;
        final TerminalSize size;
        final TerminalPosition topLeftPosition;

        public AxisLaidOutComponent(Component component, TerminalSize size, TerminalPosition topLeftPosition)
        {
            this.component = component;
            this.size = size;
            this.topLeftPosition = topLeftPosition;
        }

        public Component getComponent()
        {
            return component;
        }

        public TerminalSize getSize()
        {
            return size;
        }

        public TerminalPosition getTopLeftPosition()
        {
            return topLeftPosition;
        }
    }
}
