package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;

import static java.lang.Math.max;
import static java.lang.Math.min;

public final class ScrollableContentAdapter implements ScrollController {
    private Component adaptedComponent;
    private ScrollController scrollController;
    private ScrollData scrollData;
    private boolean canContentControlScrolling;

    public void setAdaptedComponent(final Component component) {
        adaptedComponent = component;
        connectScrollableComponent();
    }

    private void invalidate() {
        final Component component = adaptedComponent;
        final Component container = component != null ? component.getParent() : null;

        if (container != null) {
            container.invalidate();
        }
    }

    public boolean getCanContentControlScrolling() {
        return canContentControlScrolling;
    }

    public void setCanContentControlScrolling(final boolean value) {
        if (value == canContentControlScrolling) {
            return;
        }

        canContentControlScrolling = value;

        if (scrollController == null) {
            return;
        }

        connectScrollableComponent();
    }

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
        return isScrollClient() ? ensureScrollData().computedHorizontalOffset : 0;
    }

    @Override
    public void setHorizontalOffset(final int offset) {
        if (isScrollClient()) {
            final int newOffset = validateInputOffset(offset);
            if (newOffset != ensureScrollData().horizontalOffset) {
                scrollData.horizontalOffset = newOffset;
                invalidate();
            }
        }
    }

    @Override
    public int getVerticalOffset() {
        return isScrollClient() ? ensureScrollData().computedVerticalOffset : 0;
    }

    @Override
    public void setVerticalOffset(final int offset) {
        if (isScrollClient()) {
            final int newOffset = validateInputOffset(offset);
            if (newOffset != ensureScrollData().verticalOffset) {
                scrollData.verticalOffset = newOffset;
                invalidate();
            }
        }
    }

    @Override
    public boolean isVerticalScrollingEnabled() {
        return isScrollClient() && ensureScrollData().canVerticallyScroll;
    }

    @Override
    public void setVerticalScrollingEnabled(final boolean value) {
        if (isScrollClient()) {
            if (value != ensureScrollData().canVerticallyScroll) {
                scrollData.canVerticallyScroll = true;
                invalidate();
            }
        }
    }

    @Override
    public boolean isHorizontalScrollingEnabled() {
        return isScrollClient() && ensureScrollData().canHorizontallyScroll;
    }

    @Override
    public void setHorizontalScrollingEnabled(final boolean value) {
        if (isScrollClient()) {
            if (value != ensureScrollData().canHorizontallyScroll) {
                scrollData.canHorizontallyScroll = true;
                invalidate();
            }
        }
    }

    @Override
    public int getViewportWidth() {
        return isScrollClient() ? ensureScrollData().viewportWidth : 0;
    }

    @Override
    public int getViewportHeight() {
        return isScrollClient() ? ensureScrollData().viewportHeight : 0;
    }

    @Override
    public int getExtentWidth() {
        return isScrollClient() ? ensureScrollData().extentWidth : 0;
    }

    @Override
    public int getExtentHeight() {
        return isScrollClient() ? ensureScrollData().extentHeight : 0;
    }

    @Override
    public Rectangle makeVisible(final Component component, final Rectangle targetRectangle) {
        return makeVisible(component, targetRectangle, true);
    }

    @Override
    public ScrollViewer getScrollOwner() {
        return isScrollClient() ? ensureScrollData().scrollOwner : null;
    }

    @Override
    public void setScrollOwner(final ScrollViewer owner) {
        if (isScrollClient()) {
            ensureScrollData().scrollOwner = owner;
        }
    }

    final Rectangle makeVisible(
        final Component component,
        final Rectangle targetRectangle,
        final boolean throwOnError) {

        return makeVisible(component, this, targetRectangle, throwOnError);
    }

    static Rectangle makeVisible(
        final Component component,
        final ScrollController controller,
        final Rectangle targetRectangle,
        final boolean throwOnError) {

        // An empty rectangle has no size or position.  We can't meaningfully use it.
        if (targetRectangle.isEmpty() || component == null || controller == null) {
            return Rectangle.EMPTY;
        }

        final ScrollViewer sv = controller.getScrollOwner();

        if (sv == null) {
            return Rectangle.EMPTY;
        }

        final TerminalPosition relativeOffset = component.toAncestor(sv, TerminalPosition.of(0, 0));

        Rectangle rectangle = targetRectangle;

        rectangle = rectangle.offset(relativeOffset);

        if (!isScrollClient(controller) || (!throwOnError && rectangle.isEmpty())) {
            return rectangle;
        }

        //
        // Initialize the viewport:
        //

        Rectangle viewport = Rectangle.of(
            controller.getHorizontalOffset(),
            controller.getVerticalOffset(),
            controller.getViewportWidth(),
            controller.getViewportHeight()
        );

        rectangle = rectangle.offset(viewport.getPosition());

        //
        // Compute the offsets required to minimally scroll the child maximally into view:
        //

        final int minX = computeScrollOffsetWithMinimalScroll(
            viewport.getLeft(),
            viewport.getRight(),
            rectangle.getLeft(),
            rectangle.getRight()
        );

        final int minY = computeScrollOffsetWithMinimalScroll(
            viewport.getTop(),
            viewport.getBottom(),
            rectangle.getTop(),
            rectangle.getBottom()
        );

        //
        // We have computed the scrolling offsets; scroll to them:
        //

        controller.setHorizontalOffset(minX);
        controller.setVerticalOffset(minY);

        //
        // Compute the visible rectangle of the child relative to the viewport, then return it:
        //

        viewport = viewport.offset(minX, minY);
        rectangle = rectangle.intersection(viewport);

        if (throwOnError || !rectangle.isEmpty()) {
            rectangle = rectangle.offset(-viewport.getLeft(), -viewport.getTop());
        }

        return rectangle;
    }

    static int computeScrollOffsetWithMinimalScroll(
        final int topView,
        final int bottomView,
        final int topChild,
        final int bottomChild) {

        //
        // # CHILD POSITION       CHILD SIZE      SCROLL      REMEDY
        // 1 Above viewport       <= viewport     Down        Align top edge of child & viewport.
        // 2 Above viewport       > viewport      Down        Align bottom edge of child & viewport.
        // 3 Below viewport       <= viewport     Up          Align bottom edge of child & viewport.
        // 4 Below viewport       > viewport      Up          Align top edge of child & viewport.
        // 5 Entirely within viewport             N/A         No scroll.
        // 6 Spanning viewport                    N/A         No scroll.
        //
        // Note: "Above viewport" = childTop above viewportTop, childBottom above viewportBottom
        //       "Below viewport" = childTop below viewportTop, childBottom below viewportBottom
        //
        // The child thus may overlap with the viewport, but will scroll the same direction.
        //

        final boolean isAbove = topChild < topView && bottomChild < bottomView;
        final boolean isBelow = bottomChild > bottomView && topChild > topView;
        final boolean isLarger = bottomChild - topChild > bottomView - topView;

        if (isAbove && !isLarger || isBelow && isLarger) {
            // Handle cases 1 & 4 described above.
            return topChild;
        }
        else if (isAbove || isBelow) {
            // Handle cases 2 & 3 described above.
            return bottomChild - (bottomView - topView);
        }
        else {
            // Handle cases: 5 & 6 above.
            return topView;
        }
    }

    private ScrollData ensureScrollData() {
        final ScrollData scrollData = this.scrollData;
        return scrollData != null ? scrollData : (this.scrollData = new ScrollData(this));
    }

    private boolean isScrollClient() {
        return scrollController == this;
    }

    private static int coerceOffset(final int offset, final int extent, final int viewport) {
        return max(0, min(offset, extent - viewport));
    }

    private static boolean coerceOffsets(final ScrollData d) {
        final int computedHorizontalOffset = coerceOffset(
            d.horizontalOffset,
            d.extentWidth,
            d.viewportWidth
        );

        final int computedVerticalOffset = coerceOffset(
            d.verticalOffset,
            d.extentHeight,
            d.viewportHeight
        );

        final boolean valid = d.computedHorizontalOffset == computedHorizontalOffset &&
                              d.computedVerticalOffset == computedVerticalOffset;

        d.computedHorizontalOffset = computedHorizontalOffset;
        d.computedVerticalOffset = computedVerticalOffset;

        if (d.offset == null ||
            d.offset.getColumn() != d.computedHorizontalOffset ||
            d.offset.getRow() != d.computedVerticalOffset) {

            d.offset = new TerminalPosition(computedHorizontalOffset, computedVerticalOffset);
        }

        return valid;
    }

    static int validateInputOffset(final int offset) {
        return max(0, offset);
    }

    final void draw(final TextGUIGraphics g, final TerminalSize extent) {
        final Component component = this.adaptedComponent;
        final ScrollData d = ensureScrollData();

        verifyScrollData(d, g.getSize(), extent);

        component.setPosition(d.graphicsOffset);
        component.draw(g.newTextGraphics(d.graphicsOffset, d.graphicsSize));
    }

    static void verifyScrollData(final ScrollData d, final TerminalSize viewport, final TerminalSize extent) {
        boolean valid;

        int coercedViewportWidth = viewport.getColumns();
        int coercedViewportHeight = viewport.getRows();

        if (coercedViewportWidth == INFINITY) {
            coercedViewportWidth = extent.getColumns();
        }

        if (coercedViewportHeight == INFINITY) {
            coercedViewportHeight = extent.getRows();
        }

        valid = coercedViewportWidth == d.viewportWidth &&
                coercedViewportHeight == d.viewportHeight;

        d.viewportWidth = coercedViewportWidth;
        d.viewportHeight = coercedViewportHeight;

        valid &= coerceOffsets(d);

        if (d.viewport == null ||
            d.viewport.getColumns() != coercedViewportWidth ||
            d.viewport.getRows() != coercedViewportHeight) {

            d.viewport = new TerminalSize(coercedViewportWidth, coercedViewportHeight);
        }

        d.extentWidth = extent.getColumns();
        d.extentHeight = extent.getRows();

        if (isScrollClient(d.owner)) {
            d.graphicsOffset = new TerminalPosition(-d.computedHorizontalOffset, -d.computedVerticalOffset);
            d.graphicsSize = new TerminalSize(d.extentWidth, d.extentHeight);
        }
        else {
            d.graphicsOffset = TerminalPosition.of(0, 0);
            d.graphicsSize = d.viewport;
        }

        if (!valid && d.scrollOwner != null) {
            d.scrollOwner.invalidateScrollInfo();
        }
    }

    static boolean isScrollClient(final ScrollController controller) {
        return controller instanceof ScrollableContentAdapter &&
               ((ScrollableContentAdapter) controller).isScrollClient();
    }

    private void connectScrollableComponent() {
        final Component component = adaptedComponent;
        final Component container = component != null ? component.getParent() : null;

        if (container instanceof ScrollViewer) {
            final ScrollViewer scrollContainer = (ScrollViewer) container;

            ScrollController s = null;

            // If permitted, allow any scrollable content to control scrolling.
            if (canContentControlScrolling && component instanceof ScrollController) {
                s = (ScrollController) component;
            }

            // If content isn't controlling scrolling, we must control it ourselves.
            if (s == null) {
                s = this;
                ensureScrollData();
            }

            // Detach any previous Scrollable from the ScrollViewer.
            if (s != scrollController && scrollController != null) {
                if (isScrollClient()) {
                    scrollData = null;
                }
                else {
                    scrollData.scrollOwner = null;
                }
            }

            // Connect the ScrollViewer and the scroll controller to each other.
            scrollController = s;
            s.setScrollOwner(scrollContainer);
            scrollContainer.setScrollController(scrollController);
        }
        else if (scrollController != null) {
            // We're not in a valid scrolling scenario, so disconnect any existing
            // links and get ourselves into a totally unlinked state.

            final ScrollViewer oldScrollOwner = scrollController.getScrollOwner();

            if (oldScrollOwner != null) {
                oldScrollOwner.setScrollController(null);
            }

            scrollController.setScrollOwner(null);
            scrollController = null;
            scrollData = null;
        }
    }

    final static class ScrollData {
        final ScrollController owner;

        ScrollViewer scrollOwner;

        boolean canHorizontallyScroll;
        boolean canVerticallyScroll;

        int viewportWidth;
        int viewportHeight;
        int extentWidth;
        int extentHeight;
        int horizontalOffset;
        int verticalOffset;

        int computedVerticalOffset;
        int computedHorizontalOffset;

        TerminalSize viewport;
        TerminalPosition offset;

        TerminalPosition graphicsOffset;
        TerminalSize graphicsSize;

        ScrollData(final ScrollController owner) {
            if (owner == null) {
                throw new IllegalArgumentException("Owner cannot be null.");
            }
            this.owner = owner;
        }
    }
}
