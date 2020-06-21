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
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.gui2.BorderLayout.Location;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
    
    private final Component scrollableComponent;
    private final ScrollableBox scrollableBox;
    private final ScrollBar verticalScrollBar;
    private final ScrollBar horizontalScrollBar;
    
    private final boolean isHorizontalScrollCapable;
    private final boolean isVerticalScrollCapable;
    private CenterViewPort centerViewPort;
    
    private final ScrollPanelLayoutManager scrollPanelLayoutManager;
    
    TerminalPosition AIM_SCROLL_LESS = new TerminalPosition(1, 1);
    TerminalPosition AIM_SCROLL_MORE = new TerminalPosition(-1, -1);
    
    TerminalPosition MASK_VERTICAL = new TerminalPosition(0, 1);
    TerminalPosition MASK_HORIZONTAL = new TerminalPosition(1, 0);
    TerminalPosition ORIGIN = new TerminalPosition(0, 0);
    
    protected TerminalPosition scrollOffset = new TerminalPosition(0, 0);
    
    protected TerminalPosition thumbMouseDownPosition = null;
    protected TerminalPosition offsetAtMouseDown = null;
    protected TerminalPosition thumbMouseDownMask = null;
    //protected int selectedAtMouseDown = 0;
    private final ScrollableBox scrollableBox;
    protected int selectedAtMouseDown = 0;

    private final ScrollBar verticalScrollBar;
    private final ScrollBar horizontalScrollBar;
    
    private final boolean isHorizontalScrollCapable;
    private final boolean isVerticalScrollCapable;
    private CenterViewPort centerViewPort;
    
    private final ScrollPanelLayoutManager scrollPanelLayoutManager;
    
    /**
     * Default constructor, creates a new panel with no child components and by default set to a vertical
     * {@code LinearLayout} layout manager.
     */
    public ScrollPanel(Component scrollableComponent, boolean isHorizontalScrollCapable, boolean isVerticalScrollCapable) {
        
        this.scrollableComponent = scrollableComponent;
        this.isHorizontalScrollCapable = isHorizontalScrollCapable;
        this.isVerticalScrollCapable = isVerticalScrollCapable;
        

        verticalScrollBar = new ScrollBar(Direction.VERTICAL, this);
        horizontalScrollBar = new ScrollBar(Direction.HORIZONTAL, this);
        scrollPanelLayoutManager = new ScrollPanelLayoutManager();
        setLayoutManager(scrollPanelLayoutManager);
        
        centerViewPort = new CenterViewPort(scrollableComponent);
        addComponent(centerViewPort, Location.CENTER);
        
        if (scrollableComponent instanceof ScrollableBox) {
            ((ScrollableBox)scrollableComponent).setIsWithinScrollPanel(true);
        }
    }
    
    boolean isVerticalScrollVisible() {
        boolean isVisible = scrollableComponent.getSize().getRows() >= getSize().getRows();
        return isVerticalScrollCapable && isVisible;
    }
    boolean isHorizontalScrollVisible() {
        boolean isVisible = scrollableComponent.getSize().getColumns() >= getSize().getColumns();
        return isHorizontalScrollCapable && isVisible;
    }
    
    void updateScrollerBars() {
        if (isVerticalScrollVisible()) {
            verticalScrollBar.setViewSize(verticalScrollBar.getSize().getRows());
            verticalScrollBar.setScrollMaximum(scrollableComponent.getSize().getRows());
            //verticalScrollBar.setScrollPosition(scrollableComponent.getVerticalScrollPosition());
            verticalScrollBar.setScrollPosition(-scrollOffset.getRow());
        }
        if (isHorizontalScrollVisible()) {
            horizontalScrollBar.setViewSize(horizontalScrollBar.getSize().getColumns());
            horizontalScrollBar.setScrollMaximum(scrollableComponent.getSize().getColumns());
            //horizontalScrollBar.setScrollPosition(scrollableComponent.getHorizontalScrollPosition());
            horizontalScrollBar.setScrollPosition(-scrollOffset.getColumn());
        }
    }
    
    class ScrollPanelLayoutManager extends BorderLayout {

    public ScrollPanel(ScrollableBox scrollableBox) {
        this.scrollableBox = scrollableBox;
        verticalScrollBar = new ScrollBar(Direction.VERTICAL, this);
        horizontalScrollBar = new ScrollBar(Direction.HORIZONTAL, this);
        scrollPanelLayoutManager = new ScrollPanelLayoutManager();
        setLayoutManager(scrollPanelLayoutManager);
        
        centerViewPort = new CenterViewPort(scrollableComponent);
        addComponent(centerViewPort, Location.CENTER);
        
        if (scrollableComponent instanceof ScrollableBox) {
            scrollableBox = (ScrollableBox)scrollableComponent;
            scrollableBox.setIsWithinScrollPanel(this);
        } else {
            scrollableBox = null;
        }
    }
    
    public ScrollPanel(ScrollableBox box) {
        this(box, box.isHorizontalScrollCapable(), box.isVerticalScrollCapable());
    }
    
    public void redoOffset() {
       scrollOffset = new TerminalPosition(0,0);
       thumbMouseDownPosition = null;
    }
    
    public TerminalPosition getScrollOffset() {
        return scrollOffset;
    }
    
    boolean isVerticalScrollVisible() {
        boolean isVisible = scrollableComponent.getSize().getRows() >= getSize().getRows();
        return isVerticalScrollCapable && isVisible;
    }
    boolean isHorizontalScrollVisible() {
        boolean isVisible = scrollableComponent.getSize().getColumns() >= getSize().getColumns();
        return isHorizontalScrollCapable && isVisible;
    }
    
    void updateScrollerBars() {
        if (isVerticalScrollCapable) {
            verticalScrollBar.setViewSize(verticalScrollBar.getSize().getRows());
            verticalScrollBar.setScrollMaximum(scrollableComponent.getSize().getRows());
            verticalScrollBar.setScrollPosition(-scrollOffset.getRow());
        }
        if (isHorizontalScrollCapable) {
            horizontalScrollBar.setViewSize(horizontalScrollBar.getSize().getColumns());
            horizontalScrollBar.setScrollMaximum(scrollableComponent.getSize().getColumns());
            horizontalScrollBar.setScrollPosition(-scrollOffset.getColumn());
        }
    }
    
    class ScrollPanelLayoutManager extends BorderLayout {
    
        boolean once = false;
        boolean priorLayoutHasVerticalScrollVisible;
        boolean priorLayoutHasHorizontalScrollVisible;
        
        public ScrollPanelLayoutManager() {
//            priorLayoutHasVerticalScrollVisible = isVerticalScrollVisible();
//            priorLayoutHasHorizontalScrollVisible = isHorizontalScrollVisible();
            priorLayoutHasVerticalScrollVisible = scrollableBox.isVerticalScrollVisible();
            priorLayoutHasHorizontalScrollVisible = scrollableBox.isHorizontalScrollVisible();
            priorLayoutHasVerticalScrollVisible = isVerticalScrollVisible();
            priorLayoutHasHorizontalScrollVisible = isHorizontalScrollVisible();
        }
        
        @Override
        public TerminalSize getPreferredSize(List<Component> components) {
//            TerminalSize size = ScrollPanel.this.getPreferredSize();
//            
//            int columns = size.getColumns() + (isVerticalScrollVisible() ? 1 : 0);
//            int rows = size.getRows() + (isHorizontalScrollVisible() ? 1 : 0);
            TerminalSize size = scrollableBox.getPreferredSize();
//            TerminalSize size = ScrollPanel.this.getPreferredSize();
            
            int columns = size.getColumns() + (isVerticalScrollVisible() ? 1 : 0);
            int rows = size.getRows() + (isHorizontalScrollVisible() ? 1 : 0);
            //TerminalSize size = ScrollPanel.this.getPreferredSize();
            TerminalSize size = super.getPreferredSize(components);
            int xpad = isVerticalScrollVisible() ? 1 : 0;
            int ypad = isHorizontalScrollVisible() ? 1 : 0;
            int columns = size.getColumns() + xpad;
            int rows = size.getRows() + ypad;
            
            return new TerminalSize(columns, rows);
        }
        
        @Override
        public void doLayout(TerminalSize area, List<Component> components) {
           super.doLayout(area, components);
           
           updateScrollerBars();
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
          //  boolean isVerticalScrollVisible = isVerticalScrollVisible();
          //  boolean isHorizontalScrollVisible = isHorizontalScrollVisible();
            boolean isVerticalScrollVisible = scrollableBox.isVerticalScrollVisible();
            boolean isHorizontalScrollVisible = scrollableBox.isHorizontalScrollVisible();
            boolean isVerticalScrollVisible = isVerticalScrollVisible();
            boolean isHorizontalScrollVisible = isHorizontalScrollVisible();
            
            boolean isChanged = !once || priorLayoutHasVerticalScrollVisible != isVerticalScrollVisible || priorLayoutHasHorizontalScrollVisible != isHorizontalScrollVisible;
            
            priorLayoutHasVerticalScrollVisible = isVerticalScrollVisible;
            priorLayoutHasHorizontalScrollVisible = isHorizontalScrollVisible;
            
            // xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
            // this 'true' here is a hack to get the layout happening
            // otherwise the scroller lags one 'tick' behind realtime
            // other option would be to subclass the Renderer it seems
            // xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
            if (true || isChanged) {
            //if (isChanged) {
                removeComponent(verticalScrollBar);
                removeComponent(horizontalScrollBar);
                if (isVerticalScrollVisible) {
                    addComponent(verticalScrollBar, Location.RIGHT);
                }
                if (isHorizontalScrollVisible) {
                    addComponent(horizontalScrollBar, Location.BOTTOM);
                }
                updateScrollerBars();
            }
            
            once = true;
            return isChanged;
        }
    }
    
    class ScrollPanelCenterLayoutManager implements LayoutManager {
    
        boolean once = false;
        boolean priorLayoutHasVerticalScrollVisible;
        boolean priorLayoutHasHorizontalScrollVisible;
        
        public ScrollPanelCenterLayoutManager() {
            priorLayoutHasVerticalScrollVisible = isVerticalScrollVisible();
            priorLayoutHasHorizontalScrollVisible = isHorizontalScrollVisible();
        }
        
        @Override
        public TerminalSize getPreferredSize(List<Component> components) {
            // ignored as this is in a BorderLayout.CENTER anyway
            if (components.size() != 1) {
                return TerminalSize.ONE;
            }
            return components.get(0).getPreferredSize();
        }
        
        @Override
        public void doLayout(TerminalSize area, List<Component> components) {
            if (components.size() != 1) {
                return;
            }
            
            Component it = components.get(0);
            TerminalSize preferredSize = it.getPreferredSize();
            int width = isHorizontalScrollVisible() ? preferredSize.getColumns() : getSize().getColumns();
            int height = isVerticalScrollVisible() ? preferredSize.getRows() : getSize().getRows();
            
            it.setSize(new TerminalSize(width, height));
            it.setPosition(scrollOffset);
        }
        
        @Override
        public boolean hasChanged() {
            boolean isVerticalScrollVisible = isVerticalScrollVisible();
            boolean isHorizontalScrollVisible = isHorizontalScrollVisible();
            
            boolean isChanged = !once || priorLayoutHasVerticalScrollVisible != isVerticalScrollVisible || priorLayoutHasHorizontalScrollVisible != isHorizontalScrollVisible;
            
            priorLayoutHasVerticalScrollVisible = isVerticalScrollVisible;
            priorLayoutHasHorizontalScrollVisible = isHorizontalScrollVisible;
            
            once = true;
            return isChanged;
        }
    }
    public class CenterViewPort extends Panel {
        public CenterViewPort(Component component) {
            setLayoutManager(new ScrollPanelCenterLayoutManager());
            addComponent(component);
        }
    }

/*
 .o88b.  .d88b.  d8b   db d888888b d8888b.  .d88b.  db      db      d88888b d8888b. 
d8P  Y8 .8P  Y8. 888o  88 `~~88~~' 88  `8D .8P  Y8. 88      88      88'     88  `8D 
8P      88    88 88V8o 88    88    88oobY' 88    88 88      88      88ooooo 88oobY' 
8b      88    88 88 V8o88    88    88`8b   88    88 88      88      88~~~~~ 88`8b   
Y8b  d8 `8b  d8' 88  V888    88    88 `88. `8b  d8' 88booo. 88booo. 88.     88 `88. 
 `Y88P'  `Y88P'  VP   V8P    YP    88   YD  `Y88P'  Y88888P Y88888P Y88888P 88   YD
*/
    
    public void doPage(boolean isVertical, boolean isLess) {
        doPageKeyboard(isVertical, isLess);
    }
    
    public void doScroll(boolean isVertical, boolean isLess) {
        TerminalPosition mask = mask(isVertical);
        TerminalPosition aim = aim(isLess);
        TerminalPosition offset = aim.multiply(mask);
        doOffsetAmount(offset);
    }
    
    public void thumbMouseDown(boolean isVertical, TerminalPosition position) {
        thumbMouseDownPosition = position;
        thumbMouseDownMask = mask(isVertical);
        offsetAtMouseDown = scrollOffset;
        if (scrollableBox != null) {
            selectedAtMouseDown = scrollableBox.getSelectedIndex();
        }
    }
    public void mouseUp() {
        thumbMouseDownPosition = null;
    }
    
    
    public void thumbMouseDrag(boolean isVertical, TerminalPosition position) {
        if (thumbMouseDownPosition == null) {
            thumbMouseDown(isVertical, position);
            return;
        }
        
        TerminalPosition mask = mask(isVertical);
        TerminalPosition delta = calculateThumbDelta(mask, position);
        delta = delta.multiply(AIM_SCROLL_MORE);
        // reseting to the beginning prior to offset to get smoother resolution
        scrollOffset = offsetAtMouseDown;
        
        
        if (!Objects.equals(ORIGIN, delta)) {
            if (scrollableBox != null) {
                scrollableBox.setSelectedIndex(selectedAtMouseDown);
            }
            doOffsetAmount(delta);
        }
    }
    
    public void doPageKeyboard(boolean isVertical, boolean isLess) {
        TerminalSize vpSize = getViewportSize();
        TerminalPosition mask = mask(isVertical);
        TerminalPosition aim = aim(isLess);
        TerminalPosition offset = new TerminalPosition(vpSize.getColumns(), vpSize.getRows());
        offset = offset.multiply(aim);
        offset = offset.multiply(mask);
        doOffsetAmount(offset);
    }
    public void doOffsetAmount(TerminalPosition desiredOffset) {
        TerminalPosition priorOffset = scrollOffset;
        adjustScrollOffset(desiredOffset);
        
        if (scrollableBox != null) {
            if (Objects.equals(priorOffset, scrollOffset)) {
                // scrolling stopped, start moving selection more
                int amount = desiredOffset.getRow() * -1;
                scrollableBox.setSelectedIndex(scrollableBox.getSelectedIndex() + amount);
            }
            scrollableBox.pullSelectionIntoView();
        }
    }
    //private void pullSelectionIntoView() {
    //    int minViewableSelection = Math.max(0, -scrollOffset.getRow());
    //    int maxViewableSelection = minViewableSelection + getSize().getRows();
    //    if (selectedIndex < minViewableSelection) {
    //        selectedIndex = minViewableSelection;
    //    } else if(selectedIndex >= maxViewableSelection) {
    //        selectedIndex = maxViewableSelection -1;
    //    }
    //}
    
    public void adjustScrollOffset(TerminalPosition offsetAmount) {
        TerminalPosition min = minOffset();
        TerminalPosition max = maxOffset();
        TerminalPosition goal = scrollOffset.plus(offsetAmount);
        goal = goal.min(max);
        goal = goal.max(min);
        
        scrollOffset = goal;
        
        updateScrollerBars();
    }
    
    TerminalPosition mask(boolean isVertical) {
        return isVertical ? MASK_VERTICAL : MASK_HORIZONTAL;
    }
    TerminalPosition aim(boolean isLess) {
        return isLess ? AIM_SCROLL_LESS : AIM_SCROLL_MORE;
    }
    
    TerminalPosition minOffset() {
        TerminalSize vpSize = getViewportSize();
        TerminalSize viewedComponentSize = getViewedComponenntSize();
        int x = Math.min(0, vpSize.getColumns() - viewedComponentSize.getColumns());
        int y = Math.min(0, vpSize.getRows() - viewedComponentSize.getRows());
        return new TerminalPosition(x, y);
    }
    TerminalPosition maxOffset() {
        return ORIGIN;
    }
    
    public TerminalSize getViewportSize() {
        return centerViewPort.getSize();
    }
    
    public TerminalSize getViewedComponenntSize() {
        return scrollableComponent.getSize();
    }
    
    TerminalPosition calculateThumbDelta(TerminalPosition mask, TerminalPosition position) {
        TerminalPosition delta = position.minus(thumbMouseDownPosition);
        delta = delta.multiply(mask);
        
        float xRatioMultiplier = ((float)getViewedComponenntSize().getColumns() / (float)getViewportSize().getColumns());
        float yRatioMultiplier = ((float)getViewedComponenntSize().getRows() / (float)getViewportSize().getRows());
        
        int dx = (int)(delta.getColumn() * xRatioMultiplier);
        int dy = (int)(delta.getRow() * yRatioMultiplier);
        
        return new TerminalPosition(dx, dy);
    }
    
}
