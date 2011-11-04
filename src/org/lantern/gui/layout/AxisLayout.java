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
 * Copyright (C) 2010-2011 mabe02
 */

package org.lantern.gui.layout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.lantern.gui.Component;
import org.lantern.terminal.TerminalPosition;
import org.lantern.terminal.TerminalSize;

/**
 *
 * @author mabe02
 */
public abstract class AxisLayout implements LanternLayout
{
    private final List componentList;
    private int padding;
    
    AxisLayout()
    {
        this.componentList = new ArrayList();
        this.padding = 0;
    }

    public void addComponent(Component component, Object modifiers)
    {
        if(modifiers instanceof SizePolicy == false)
            modifiers = SizePolicy.CONSTANT;
        componentList.add(new AxisLayoutComponent(component, (SizePolicy)modifiers));
    }

    public void removeComponent(Component component)
    {
        Iterator iterator = componentList.iterator();
        while(iterator.hasNext()) {
            if(((AxisLayoutComponent)iterator.next()).component == component) {
                iterator.remove();
                return;
            }
        }
    }

    public boolean isMaximising()
    {
        for(int i = 0; i < componentList.size(); i++) {
            AxisLayoutComponent axisLayoutComponent = (AxisLayoutComponent)componentList.get(i);
            if(axisLayoutComponent.sizePolicy == SizePolicy.MAXIMUM)
                return true;
        }

        return false;
    }

    public void setPadding(int padding)
    {
        this.padding = padding;
    }

    public TerminalSize getPreferredSize()
    {
        final TerminalSize preferredSize = new TerminalSize(0, 0);
        for(int i = 0; i < componentList.size(); i++) {
            AxisLayoutComponent axisLayoutComponent = (AxisLayoutComponent)componentList.get(i);
            final TerminalSize componentPreferredSize = axisLayoutComponent.component.getPreferredSize();
            setMajorAxis(preferredSize, getMajorAxis(preferredSize) + getMajorAxis(componentPreferredSize));
            setMinorAxis(preferredSize, Math.max(getMinorAxis(preferredSize), getMinorAxis(componentPreferredSize)));
        }
        setMajorAxis(preferredSize, getMajorAxis(preferredSize) + (padding * componentList.size()));
        return preferredSize;
    }

    public List layout(TerminalSize layoutArea)
    {
        List result = new ArrayList();
        List growingComponents = new ArrayList();

        final int availableMinorAxisSpace = getMinorAxis(layoutArea);
        int availableMajorAxisSpace = getMajorAxis(layoutArea);

        for(int i = 0; i < componentList.size(); i++) {
            AxisLayoutComponent axisLayoutComponent = (AxisLayoutComponent)componentList.get(i);
            TerminalSize componentPreferredSize = axisLayoutComponent.component.getPreferredSize();
            int componentPreferredMajorAxisSize = getMajorAxis(componentPreferredSize);
            
            if(availableMajorAxisSpace < componentPreferredMajorAxisSize)
                break;
            
            if(componentPreferredMajorAxisSize < 0)
                continue;

            //This will be re-calculated later
            TerminalPosition componentTopLeft = new TerminalPosition(0, 0);

            TerminalSize componentSize = new TerminalSize(0,0);
            setMinorAxis(componentSize, availableMinorAxisSpace);
            setMajorAxis(componentSize, getMajorAxis(componentPreferredSize));
            availableMajorAxisSpace -= (getMajorAxis(componentSize) + padding);
            
            AxisLaidOutComponent laidOutComponent = new AxisLaidOutComponent(axisLayoutComponent.component, componentSize, componentTopLeft);
            result.add(laidOutComponent);
            if(axisLayoutComponent.sizePolicy != SizePolicy.CONSTANT)
                growingComponents.add(laidOutComponent);
        }

        while(!growingComponents.isEmpty() && availableMajorAxisSpace > 0) {
            for(int i = 0; i < growingComponents.size(); i++) {
                AxisLaidOutComponent laidOutComponent = (AxisLaidOutComponent)growingComponents.get(i);
                setMajorAxis(laidOutComponent.size, getMajorAxis(laidOutComponent.size) + 1);
                if(--availableMajorAxisSpace == 0)
                    break;
            }
        }

        int nextMajorPosition = 0;
        for(int i = 0; i < result.size(); i++) {
            AxisLaidOutComponent laidOutComponent = (AxisLaidOutComponent)result.get(i);
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

    private class AxisLaidOutComponent implements LanternLayout.LaidOutComponent
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
