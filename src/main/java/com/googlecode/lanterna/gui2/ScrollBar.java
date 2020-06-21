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

import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalRectangle;
import com.googlecode.lanterna.TerminalRect;
import com.googlecode.lanterna.graphics.ThemeDefinition;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.input.MouseAction;
import com.googlecode.lanterna.input.MouseActionType;

/**
 * Classic scrollbar that can be used to display where inside a larger component a view is showing. This implementation
 * is not interactable and needs to be driven externally, meaning you can't focus on the scrollbar itself, you have to
 * update its state as part of another component being modified. {@code ScrollBar}s are either horizontal or vertical,
 * which affects the way they appear and how they are drawn.
 * <p>
 * This class works on two concepts, the min-position-max values and the view size. The minimum value is always 0 and
 * cannot be changed. The maximum value is 100 and can be adjusted programmatically. Position value is whever along the
 * axis of 0 to max the scrollbar's tracker currently is placed. The view size is an important concept, it determines
 * how big the tracker should be and limits the position so that it can only reach {@code maximum value - view size}.
 * <p>
 * The regular way to use the {@code ScrollBar} class is to tie it to the model-view of another component and set the
 * scrollbar's maximum to the total height (or width, if the scrollbar is horizontal) of the model-view. View size
 * should then be assigned based on the current size of the view, meaning as the terminal and/or the GUI changes and the
 * components visible space changes, the scrollbar's view size is updated along with it. Finally the position of the
 * scrollbar should be equal to the scroll offset in the component.
 *
 * @author Martin
 */
public class ScrollBar extends AbstractInteractableComponent<ScrollBar> {

    private final Direction direction;
    private int maximum;
    private int position;
    private int viewSize;
    private final ScrollPanel scrollPanel;

    /**
     * Creates a new {@code ScrollBar} with a specified direction
     * @param direction Direction of the scrollbar
     */
    public ScrollBar(Direction direction) {
        this(direction, null);
    }
    public ScrollBar(Direction direction, ScrollPanel scrollPanel) {
        this.direction = direction;
        this.maximum = 100;
        this.position = 0;
        this.viewSize = 0;
        this.scrollPanel = scrollPanel;
    }

    /**
     * Returns the direction of this {@code ScrollBar}
     * @return Direction of this {@code ScrollBar}
     */
    public Direction getDirection() {
        return direction;
    }
    
    public boolean isVertical() {
        return direction == Direction.VERTICAL;
    }
    
    public boolean isHorizontal() {
        return direction == Direction.HORIZONTAL;
    }

    /**
     * Sets the maximum value the scrollbar's position (minus the view size) can have
     * @param maximum Maximum value
     * @return Itself
     */
    public ScrollBar setScrollMaximum(int maximum) {
        if(maximum < 0) {
            throw new IllegalArgumentException("Cannot set ScrollBar maximum to " + maximum);
        }
        if (this.maximum != maximum) {
            this.maximum = maximum;
            invalidate();
        }
        return this;
    }

    /**
     * Returns the maximum scroll value
     * @return Maximum scroll value
     */
    public int getScrollMaximum() {
        return maximum;
    }


    /**
     * Sets the scrollbar's position, should be a value between 0 and {@code maximum - view size}
     * @param position Scrollbar's tracker's position
     * @return Itself
     */
    public ScrollBar setScrollPosition(int position) {
        int newPosition = Math.min(position, this.maximum);
        if (this.position != newPosition) {
            this.position = newPosition;
            invalidate();
        }
        return this;
    }

    /**
     * Returns the position of the {@code ScrollBar}'s tracker
     * @return Position of the {@code ScrollBar}'s tracker
     */
    public int getScrollPosition() {
        return position;
    }

    /**
     * Sets the view size of the scrollbar, determining how big the scrollbar's tracker should be and also affecting the
     * maximum value of tracker's position
     * @param viewSize View size of the scrollbar
     * @return Itself
     */
    public ScrollBar setViewSize(int viewSize) {
        this.viewSize = viewSize;
        return this;
    }

    /**
     * Returns the view size of the scrollbar
     * @return View size of the scrollbar
     */
    public int getViewSize() {
        if(viewSize > 0) {
            return viewSize;
        }
        if(direction == Direction.HORIZONTAL) {
            return getSize().getColumns();
        }
        else {
            return getSize().getRows();
        }
    }
    
    @Override
    public boolean isFocusable() {
        return true;
    }

    @Override
    public Result handleKeyStroke(KeyStroke keyStroke) {
        if (keyStroke.getKeyType() == KeyType.MouseEvent) {
            MouseAction mouseAction = (MouseAction) keyStroke;
            MouseActionType actionType = mouseAction.getActionType();
        
            if (actionType == MouseActionType.CLICK_RELEASE
                    || actionType == MouseActionType.SCROLL_UP
                    || actionType == MouseActionType.SCROLL_DOWN) {
                return super.handleKeyStroke(keyStroke);
            }
            
            if (scrollPanel != null) {
                boolean isVertical = isVertical();
                ScrollBarRects rects = getScrollBarRenderer().getScrollBarRects();
                
                // the mouse events delivered are not in local coordinates, translate them
                int x = mouseAction.getPosition().getColumn() - getGlobalPosition().getColumn();
                int y = mouseAction.getPosition().getRow() - getGlobalPosition().getRow();
                
                if (isMouseDown(keyStroke)) {
                    rects.lessArrow.whenContains(x, y, () -> scrollPanel.doScroll(isVertical, true));
                    rects.moreArrow.whenContains(x, y, () -> scrollPanel.doScroll(isVertical, false));
                    rects.thumb.whenContains(x, y, () -> scrollPanel.thumbMouseDown(isVertical, mouseAction.getPosition()));
                    rects.pageLess.whenContains(x, y, () -> scrollPanel.doPage(isVertical, true));
                    rects.pageMore.whenContains(x, y, () -> scrollPanel.doPage(isVertical, false));
                }
                if (isMouseUp(keyStroke)) {
                    scrollPanel.mouseUp();
                }
                if (isMouseDrag(keyStroke)) {
                    scrollPanel.thumbMouseDrag(isVertical, mouseAction.getPosition());
                }
                if (isMouseUp(keyStroke)) {
                    scrollPanel.mouseUp();
                }
                if (isMouseDrag(keyStroke)) {
                    scrollPanel.thumbMouseDrag(isVertical, mouseAction.getPosition());
                }
            }
        }
        return Result.HANDLED;
    }
    
    public ScrollBarRenderer getScrollBarRenderer() {
        return (ScrollBarRenderer) getRenderer();
    }
    @Override
    protected InteractableRenderer<ScrollBar> createDefaultRenderer() {
        return new DefaultScrollBarRenderer();
    }

    /**
     * Helper class for making new {@code ScrollBar} renderers a little bit cleaner
     */
    public static abstract class ScrollBarRenderer implements InteractableRenderer<ScrollBar> {
        
        @Override
        public TerminalSize getPreferredSize(ScrollBar component) {
            return TerminalSize.ONE;
        }
        @Override
        public TerminalPosition getCursorLocation(ScrollBar component) {
            //todo, use real thing
            //return null;
            return new TerminalPosition(0,0);
        }
        
        public abstract ScrollBarRects getScrollBarRects();
    }
    
    public static class ScrollBarRects {
        public final TerminalRectangle lessArrow;
        public final TerminalRectangle moreArrow;
        public final TerminalRectangle thumb;
        public final TerminalRectangle thumbCenter;
        public final TerminalRectangle pageLess;
        public final TerminalRectangle pageMore;
        
        public ScrollBarRects(
                TerminalRectangle lessArrow,
                TerminalRectangle moreArrow,
                TerminalRectangle thumb,
                TerminalRectangle thumbCenter,
                TerminalRectangle pageLess,
                TerminalRectangle pageMore) {
            this.lessArrow   = lessArrow;
            this.moreArrow   = moreArrow;
            this.thumb       = thumb;
            this.thumbCenter = thumbCenter;
            this.pageLess    = pageLess;
            this.pageMore    = pageMore;
        }
        
        public abstract ScrollBarRects getScrollBarRects();
    }
    
    public static class ScrollBarRects {
        public final TerminalRectangle lessArrow;
        public final TerminalRectangle moreArrow;
        public final TerminalRectangle thumb;
        public final TerminalRectangle thumbCenter;
        public final TerminalRectangle pageLess;
        public final TerminalRectangle pageMore;
        
        public ScrollBarRects(
                TerminalRectangle lessArrow,
                TerminalRectangle moreArrow,
                TerminalRectangle thumb,
                TerminalRectangle thumbCenter,
                TerminalRectangle pageLess,
                TerminalRectangle pageMore) {
            this.lessArrow   = lessArrow;
            this.moreArrow   = moreArrow;
            this.thumb       = thumb;
            this.thumbCenter = thumbCenter;
            this.pageLess    = pageLess;
            this.pageMore    = pageMore;
        }
    }

    /**
     * Default renderer for {@code ScrollBar} which will be used unless overridden. This will draw a scrollbar using
     * arrows at each extreme end, a background color for spaces between those arrows and the tracker and then the
     * tracker itself in three different styles depending on the size of the tracker. All characters and colors are
     * customizable through whatever theme is currently in use.
     */
    public static class DefaultScrollBarRenderer extends ScrollBarRenderer {

        private boolean growScrollTracker = true;
        private ScrollBarRects scrollBarRects;

        /**
         * Default constructor
         */
        public DefaultScrollBarRenderer() {
            this.growScrollTracker = true;
        }

        /**
         * Should tracker automatically grow in size along with the {@code ScrollBar} (default: {@code true})
         * @param growScrollTracker Automatically grow tracker
         */
        public void setGrowScrollTracker(boolean growScrollTracker) {
            this.growScrollTracker = growScrollTracker;
        }

        @Override
        public void drawComponent(TextGUIGraphics graphics, ScrollBar component) {
            TerminalSize size = graphics.getSize();
            Direction direction = component.getDirection();
            int position = component.getScrollPosition();
            int maximum = component.getScrollMaximum();
            int viewSize = component.getViewSize();

            if(size.getRows() == 0 || size.getColumns() == 0) {
                return;
            }

            //Adjust position if necessary
            if(position + viewSize >= maximum) {
                position = Math.max(0, maximum - viewSize);
                component.setScrollPosition(position);
            }
            
            TerminalRectangle lessArrow = getScrollLessArrowRect(component);
            TerminalRectangle moreArrow = getScrollMoreArrowRect(component);
            TerminalRectangle thumb = getThumbRect(component, position, maximum);
            TerminalRectangle thumbCenter = getThumbCenterRect(component, thumb);
            TerminalRectangle pageLess = getPageLessRect(component, lessArrow, thumb);
            TerminalRectangle pageMore = getPageMoreRect(component, moreArrow, thumb);
            
            this.scrollBarRects = new ScrollBarRects(lessArrow, moreArrow, thumb, thumbCenter, pageLess, pageMore);
            
            ThemeDefinition themeDefinition = component.getThemeDefinition();
            graphics.applyThemeStyle(themeDefinition.getNormal());
            
            graphics.fillRectangle(TerminalPosition.TOP_LEFT_CORNER, component.getSize(), getBackgroundChar(component));
            graphics.fillRectangle(lessArrow.position, lessArrow.size, getLessChar(component));
            graphics.fillRectangle(moreArrow.position, moreArrow.size, getMoreChar(component));
            graphics.fillRectangle(thumb.position, thumb.size, getThumbChar(component));
            graphics.fillRectangle(thumbCenter.position, thumbCenter.size, getThumbCenterChar(component));
            //graphics.fillRectangle(pageLess.position, pageLess.size, 'a');
            //graphics.fillRectangle(pageMore.position, pageMore.size, 'b');
        }
        
        @Override
        public ScrollBarRects getScrollBarRects() {
            return scrollBarRects;
        }
        
        @Override
        public ScrollBarRects getScrollBarRects() {
            return scrollBarRects;
        }

        private float clampRatio(float value) {
            return Math.max(0.0f, Math.min(value, 1.0f));
        }
        public TerminalRectangle getScrollLessArrowRect(ScrollBar component) {
            final int size = component.getViewSize();
            final int x = 0;
            final int y = 0;
            final int w = size < 2 ? 0 : 1;
            final int h = size < 2 ? 0 : 1;
            return new TerminalRectangle(x, y, w, h);
        }
        public TerminalRectangle getScrollMoreArrowRect(ScrollBar component) {
            final int size = component.getViewSize();
            final int x = component.isVertical() ? 0 : size-1;
            final int y = component.isHorizontal() ? 0 : size-1;
            final int w = size < 2 ? 0 : 1;
            final int h = size < 2 ? 0 : 1;
            return new TerminalRectangle(x, y, w, h);
        }
        public TerminalRectangle getPageLessRect(ScrollBar component, TerminalRectangle lessArrow, TerminalRectangle thumb) {
            final int x = component.isVertical() ? 0 : lessArrow.xAndWidth;
            final int y = component.isHorizontal() ? 0 : lessArrow.yAndHeight;
            final int w = component.isVertical() ? thumb.width : thumb.x - x;
            final int h = component.isHorizontal() ? thumb.height : thumb.y - y;
            return new TerminalRectangle(x, y, w, h);
        }
        public TerminalRectangle getPageMoreRect(ScrollBar component, TerminalRectangle moreArrow, TerminalRectangle thumb) {
            final int x = component.isVertical() ? 0 : thumb.xAndWidth;
            final int y = component.isHorizontal() ? 0 : thumb.yAndHeight;
            final int w = component.isVertical() ? thumb.width : moreArrow.x - thumb.xAndWidth;
            final int h = component.isHorizontal() ? thumb.height : moreArrow.y - thumb.yAndHeight;
            return new TerminalRectangle(x, y, w, h);
        }
        public TerminalRectangle getThumbRect(ScrollBar component, int position, int maximum) {
            TerminalSize size = component.getSize();
            final int viewSize = component.getViewSize();
            final int scrollableArea = viewSize -2;
            int scrollTrackerSize = 1;
            
            if (scrollableArea > 2 && growScrollTracker) {
                float ratio = clampRatio((float) viewSize / (float) maximum);
                scrollTrackerSize = Math.max(1, (int) (ratio * (float) scrollableArea));
            }
            float ratio = clampRatio((float)position / (float)(maximum - viewSize));
            int scrollTrackerPosition = (int)(ratio * (float)(scrollableArea - scrollTrackerSize)) + 1;
            
            final int thumbX = component.isVertical() ? 0 : scrollTrackerPosition;
            final int thumbY = component.isHorizontal() ? 0 : scrollTrackerPosition;
            int thumbWidth = component.isVertical() ? 1 : scrollTrackerSize;
            int thumbHeight = component.isHorizontal() ? 1 : scrollTrackerSize;
            return new TerminalRectangle(thumbX, thumbY, thumbWidth, thumbHeight);
        }
        public TerminalRectangle getThumbCenterRect(ScrollBar component, TerminalRectangle thumb) {
            final int x = component.isVertical() ? 0 : thumb.x + thumb.width/2;
            final int y = component.isHorizontal() ? 0 : thumb.y + thumb.height/2;
            final int w = 1;
            final int h = 1;
            return new TerminalRectangle(x, y, w, h);
        }
        public char getLessChar(ScrollBar component) {
            return findChar(component, "UP_ARROW", Symbols.TRIANGLE_UP_POINTING_BLACK, "LEFT_ARROW", Symbols.TRIANGLE_LEFT_POINTING_BLACK);
        }
        public char getMoreChar(ScrollBar component) {
            return findChar(component, "DOWN_ARROW", Symbols.TRIANGLE_DOWN_POINTING_BLACK, "RIGHT_ARROW", Symbols.TRIANGLE_RIGHT_POINTING_BLACK);
        }
        public char getBackgroundChar(ScrollBar component) {
            return findChar(component, "VERTICAL_BACKGROUND", Symbols.BLOCK_MIDDLE, "HORIZONTAL_BACKGROUND", Symbols.BLOCK_MIDDLE);
        }
        public char getThumbChar(ScrollBar component) {
            return findChar(component, "VERTICAL_TRACKER_BACKGROUND", Symbols.BLOCK_SOLID, "HORIZONTAL_TRACKER_BACKGROUND", Symbols.BLOCK_SOLID);
        }
        public char getThumbCenterChar(ScrollBar component) {
            return findChar(component, "VERTICAL_SMALL_TRACKER", Symbols.BLOCK_SOLID, "HORIZONTAL_SMALL_TRACKER", Symbols.BLOCK_SOLID);
        }
        private char findChar(ScrollBar component, String verticalKey, char fallbackVertical, String horizontalKey, char fallbackHorizontal) {
            ThemeDefinition tdef = component.getThemeDefinition();
            return component.isVertical() ? tdef.getCharacter(verticalKey, fallbackVertical) : tdef.getCharacter(horizontalKey, fallbackHorizontal);
        }
    }
}
