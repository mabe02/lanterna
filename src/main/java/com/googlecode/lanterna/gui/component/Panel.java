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

package com.googlecode.lanterna.gui.component;

import com.googlecode.lanterna.gui.Border;
import com.googlecode.lanterna.gui.Component;
import com.googlecode.lanterna.gui.TextGraphics;
import com.googlecode.lanterna.gui.layout.HorisontalLayout;
import com.googlecode.lanterna.gui.layout.LayoutManager;
import com.googlecode.lanterna.gui.layout.LayoutParameter;
import com.googlecode.lanterna.gui.layout.LinearLayout;
import com.googlecode.lanterna.gui.layout.VerticalLayout;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;
import java.util.List;

/**
 *
 * @author Martin
 */
public class Panel extends AbstractContainer
{
    private Border border;
    private LayoutManager layoutManager;
    private String title;

    public Panel()
    {
        this(Orientation.VERTICAL);
    }

    public Panel(String title)
    {
        this(title, Orientation.VERTICAL);
    }

    public Panel(Orientation panelOrientation)
    {
        this(new Border.Invisible(), panelOrientation);
    }

    public Panel(String title, Orientation panelOrientation)
    {
        this(title, new Border.Bevel(true), panelOrientation);
    }

    public Panel(Border border, Orientation panelOrientation)
    {
        this("", border, panelOrientation);
    }

    public Panel(String title, Border border, Orientation panelOrientation)
    {
        this.border = border;
        if(panelOrientation == Orientation.HORISONTAL)
            layoutManager = new HorisontalLayout();
        else
            layoutManager = new VerticalLayout();
        
        this.title = (title != null ? title : "");
    }

    public Border getBorder()
    {
        return border;
    }

    public void setBorder(Border border)
    {
        if(border != null)
            this.border = border;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = (title != null ? title : "");
    }

    public void setLayoutManager(LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    public LayoutManager getLayoutManager() {
        return layoutManager;
    }
    
    public boolean maximisesVertically()
    {
        return layoutManager.maximisesVertically();
    }

    public boolean maximisesHorisontally()
    {
        return layoutManager.maximisesHorisontally();
    }

    /**
     * @deprecated Use the layout manager to set the padding between components
     */
    @Deprecated
    public void setBetweenComponentsPadding(int paddingSize)
    {
        if(paddingSize < 0)
            paddingSize = 0;
        
        if(layoutManager instanceof LinearLayout) {
            ((LinearLayout)layoutManager).setPadding(paddingSize);
        }
    }

    @Override
    public void repaint(TextGraphics graphics)
    {
        border.drawBorder(graphics, new TerminalSize(graphics.getWidth(), graphics.getHeight()), title);
        TerminalPosition contentPaneTopLeft = border.getInnerAreaLocation(graphics.getWidth(), graphics.getHeight());
        TerminalSize contentPaneSize = border.getInnerAreaSize(graphics.getWidth(), graphics.getHeight());
        TextGraphics subGraphics = graphics.subAreaGraphics(contentPaneTopLeft, contentPaneSize);

        List<? extends LayoutManager.LaidOutComponent> laidOutComponents = layoutManager.layout(contentPaneSize);
        for(LayoutManager.LaidOutComponent laidOutComponent: laidOutComponents) {
            TextGraphics subSubGraphics = subGraphics.subAreaGraphics(
                    laidOutComponent.getTopLeftPosition(), laidOutComponent.getSize());
            
            if(laidOutComponent.getComponent().isVisible())
                laidOutComponent.getComponent().repaint(subSubGraphics);
        }
    }

    @Override
    protected TerminalSize calculatePreferredSize() {
        TerminalSize preferredSize = border.surroundAreaSize(layoutManager.getPreferredSize());
        if(title.length() + 4 > preferredSize.getColumns())
            preferredSize.setColumns(title.length() + 4);
        return preferredSize;
    }
    
    @Override
    public void addComponent(Component component, LayoutParameter... layoutParameters)
    {
        super.addComponent(component);
        layoutManager.addComponent(component, layoutParameters);
    }

    @Override
    public boolean removeComponent(Component component)
    {
        if(super.removeComponent(component)) {
            layoutManager.removeComponent(component);
            return true;
        }
        return false;
    }
    
    public enum Orientation
    {
        HORISONTAL,
        VERTICAL
    }

    @Override
    public String toString()
    {
        return "Panel with " + getComponentCount() + " components";
    }
}
