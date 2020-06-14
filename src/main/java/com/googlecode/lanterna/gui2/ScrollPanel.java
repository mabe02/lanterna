/*
 * This file is part of lanterna (https://github.com/mabe02/lanterna).
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
 * Copyright (C) 2010-2020 Martin Berglund
 */
package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.gui2.BorderLayout.Location;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This scroll panel can be used instead of allowing the TextBox, ActionListBox, CheckBoxList, RadioBoxList components to draw their own ScrollBar.
 *
 * This is backwards compatible via this approach:
 *   1) the scrollable components are added to an instance of one of these ScrollPanels
 *   2) that causes a flag to be set on them, which their renderers use to not paint the ScrollBar
 *   3) the ScrollPanel is added to the Container instead of the scrollable components directly
 * Note: there is a gap in compatability in that custom Renderers created by users of the library may be doing stuff with their ScrollBars (very unlikely)
 * 
 * The contained Component is not replaceable, user is advised to discard the instance of ScrollPanel and recreate for that functionality.
 * 
 * Note: only extending Panel due to ease of development, since don't know the framework enough to just implement the Component interface
 *       user should not expect to invoke any of Panel container style method
 
 * TODO: implement Component, don't extends Panel
 *
 * @author ginkoblongata
 */
public class ScrollPanel extends Panel {
    private final ScrollableBox scrollableBox;
    private final ScrollBar verticalScrollBar;
    private final ScrollBar horizontalScrollBar;
    
    private final ScrollPanelLayoutManager scrollPanelLayoutManager;

    /**
     * Default constructor, creates a new panel with no child components and by default set to a vertical
     * {@code LinearLayout} layout manager.
     */
    public ScrollPanel(ScrollableBox scrollableBox) {
        this.scrollableBox = scrollableBox;
        verticalScrollBar = new ScrollBar(Direction.VERTICAL);
        horizontalScrollBar = new ScrollBar(Direction.HORIZONTAL);
        scrollPanelLayoutManager = new ScrollPanelLayoutManager();
        setLayoutManager(scrollPanelLayoutManager);
        
        scrollableBox.setIsWithinScrollPanel(true);
        addComponent(scrollableBox, Location.CENTER);
    }
    
    @Override
    public void invalidate() {
        // ?
        super.invalidate();
        // ?
        scrollableBox.invalidate();
    }
    
    
    public class ScrollPanelLayoutManager extends BorderLayout {
    
        boolean once = false;
        boolean priorLayoutHasVerticalScrollVisible;
        boolean priorLayoutHasHorizontalScrollVisible;
        
        public ScrollPanelLayoutManager() {
            priorLayoutHasVerticalScrollVisible = scrollableBox.isVerticalScrollVisible();
            priorLayoutHasHorizontalScrollVisible = scrollableBox.isHorizontalScrollVisible();
        }
        
        @Override
        public TerminalSize getPreferredSize(List<Component> components) {
            TerminalSize size = scrollableBox.getPreferredSize();
            
            int columns = size.getColumns() + (scrollableBox.isVerticalScrollCapable() && scrollableBox.isVerticalScrollVisible() ? 1 : 0);
            int rows = size.getRows() + (scrollableBox.isHorizontalScrollCapable() && scrollableBox.isHorizontalScrollVisible() ? 1 : 0);
            
            return new TerminalSize(columns, rows);
        }
        
        @Override
        public void doLayout(TerminalSize area, List<Component> components) {
           super.doLayout(area, components);
            //int width = area.getColumns();
            //int height = area.getRows();
            //
            //if (priorLayoutHasVerticalScrollVisible) {
            //    width--;
            //}
            //if (priorLayoutHasHorizontalScrollVisible) {
            //    height--;
            //}
            //
            //scrollableBox.setSize(
            
        }
        
        @Override
        public boolean hasChanged() {
            boolean isVerticalScrollVisible = scrollableBox.isVerticalScrollVisible();
            boolean isHorizontalScrollVisible = scrollableBox.isHorizontalScrollVisible();
            
            boolean isChanged = !once || priorLayoutHasVerticalScrollVisible != isVerticalScrollVisible || priorLayoutHasHorizontalScrollVisible != isHorizontalScrollVisible;
            
            priorLayoutHasVerticalScrollVisible = isVerticalScrollVisible;
            priorLayoutHasHorizontalScrollVisible = isHorizontalScrollVisible;
            
            if (isChanged) {
                removeComponent(verticalScrollBar);
                removeComponent(horizontalScrollBar);
                if (isVerticalScrollVisible) {
                    addComponent(verticalScrollBar, Location.RIGHT);
                }
                if (isHorizontalScrollVisible) {
                    addComponent(horizontalScrollBar, Location.BOTTOM);
                }
            }
            
            once = true;
            return isChanged;
        }
    }
    
}