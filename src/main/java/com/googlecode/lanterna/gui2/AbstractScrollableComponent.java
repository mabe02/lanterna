package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalSize;

public abstract class AbstractScrollableComponent<T extends Component>
    extends AbstractComponent<T>
    implements ScrollController {

    private ScrollableContentAdapter.ScrollData scrollData;

    @Override
    public void lineUp() {
        setVerticalOffset(getVerticalOffset() - 1);
    }

    @Override
    public void lineDown() {
        setVerticalOffset(getVerticalOffset() + 1);
    }

    @Override
    public void lineLeft() {
        setHorizontalOffset(getHorizontalOffset() - 1);
    }

    @Override
    public void lineRight() {
        setHorizontalOffset(getHorizontalOffset() + 1);
    }

    @Override
    public void pageUp() {
        setVerticalOffset(getVerticalOffset() - getViewportHeight());
    }

    @Override
    public void pageDown() {
        setVerticalOffset(getVerticalOffset() + getViewportHeight());
    }

    @Override
    public void pageLeft() {
        setHorizontalOffset(getHorizontalOffset() - getViewportWidth());
    }

    @Override
    public void pageRight() {
        setHorizontalOffset(getHorizontalOffset() + getViewportWidth());
    }

    @Override
    public int getHorizontalOffset() {
        return ensureScrollData().computedHorizontalOffset;
    }

    @Override
    public void setHorizontalOffset(final int offset) {
        final int newOffset = ScrollableContentAdapter.validateInputOffset(offset);
        if (newOffset != ensureScrollData().horizontalOffset) {
            scrollData.horizontalOffset = newOffset;
            invalidate();
        }
    }

    @Override
    public int getVerticalOffset() {
        return ensureScrollData().computedVerticalOffset;
    }

    @Override
    public void setVerticalOffset(final int offset) {
        final int newOffset = ScrollableContentAdapter.validateInputOffset(offset);
        if (newOffset != ensureScrollData().verticalOffset) {
            scrollData.verticalOffset = newOffset;
            invalidate();
        }
    }

    @Override
    public boolean isVerticalScrollingEnabled() {
        return ensureScrollData().canVerticallyScroll;
    }

    @Override
    public void setVerticalScrollingEnabled(final boolean value) {
        if (value != ensureScrollData().canVerticallyScroll) {
            scrollData.canVerticallyScroll = true;
            invalidate();
        }
    }

    @Override
    public boolean isHorizontalScrollingEnabled() {
        return ensureScrollData().canHorizontallyScroll;
    }

    @Override
    public void setHorizontalScrollingEnabled(final boolean value) {
        if (value != ensureScrollData().canHorizontallyScroll) {
            scrollData.canHorizontallyScroll = true;
            invalidate();
        }
    }

    @Override
    public int getViewportWidth() {
        return ensureScrollData().viewportWidth;
    }

    @Override
    public int getViewportHeight() {
        return ensureScrollData().viewportHeight;
    }

    @Override
    public int getExtentWidth() {
        return ensureScrollData().extentWidth;
    }

    @Override
    public int getExtentHeight() {
        return ensureScrollData().extentHeight;
    }

    @Override
    public Rectangle makeVisible(final Component component, final Rectangle targetRectangle) {
        return ScrollableContentAdapter.makeVisible(component, this, targetRectangle, true);
    }

    @Override
    public ScrollViewer getScrollOwner() {
        return ensureScrollData().scrollOwner;
    }

    @Override
    public void setScrollOwner(final ScrollViewer owner) {
        ensureScrollData().scrollOwner = owner;
    }

    private ScrollableContentAdapter.ScrollData ensureScrollData() {
        final ScrollableContentAdapter.ScrollData scrollData = this.scrollData;
        return scrollData != null ? scrollData
                                  : (this.scrollData = new ScrollableContentAdapter.ScrollData(this));
    }

    protected void updateScrollData(final TerminalSize viewport, final TerminalSize extent) {
        ScrollableContentAdapter.verifyScrollData(ensureScrollData(), viewport, extent);
    }

    @Override
    protected void onBeforeDrawing() {
        updateScrollData(getSize(), getPreferredSize());
        super.onBeforeDrawing();
    }
}
