package com.googlecode.lanterna.gui2;

public interface ScrollController {
    int INFINITY = Integer.MAX_VALUE;

    void lineUp();
    void lineDown();
    void lineLeft();
    void lineRight();

    void pageUp();
    void pageDown();
    void pageLeft();
    void pageRight();

    int getHorizontalOffset();
    void setHorizontalOffset(int offset);

    int getVerticalOffset();
    void setVerticalOffset(int offset);

    boolean isVerticalScrollingEnabled();
    void setVerticalScrollingEnabled(boolean value);

    boolean isHorizontalScrollingEnabled();
    void setHorizontalScrollingEnabled(boolean value);

    int getViewportWidth();
    int getViewportHeight();
    int getExtentWidth();
    int getExtentHeight();

    Rectangle makeVisible(Component component, Rectangle targetRectangle);

    ScrollViewer getScrollOwner();
    void setScrollOwner(ScrollViewer owner);
}
