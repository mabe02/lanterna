package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.MouseAction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.googlecode.lanterna.gui2.ScrollableContentAdapter.validateInputOffset;
import static java.lang.Math.max;

public class ScrollViewer
    extends AbstractComponent<ScrollViewer>
    implements Composite, Container {

    private final ScrollBar horizontalScrollBar;
    private final ScrollBar verticalScrollBar;
    private final ScrollableContentAdapter contentAdapter;

    private Component component;
    private ComponentRenderer<ScrollViewer> renderer;
    private ScrollController scrollController;
    private ScrollBarPolicy horizontalScrollBarPolicy = ScrollBarPolicy.AUTO;
    private ScrollBarPolicy verticalScrollBarPolicy = ScrollBarPolicy.AUTO;

    private boolean computedHorizontalScrollingEnabled;
    private boolean computedVerticalScrollingEnabled;
    private int cachedHorizontalOffset;
    private int cachedVerticalOffset;
    private int cachedExtentHeight;
    private int cachedExtentWidth;
    private int cachedViewportWidth;
    private int cachedViewportHeight;

    public ScrollViewer() {
        horizontalScrollBar = new ScrollBar(Direction.HORIZONTAL);
        verticalScrollBar = new ScrollBar(Direction.VERTICAL);
        contentAdapter = new ScrollableContentAdapter();
    }

    private int visibleChildren() {
        int result = 0;

        if (component != null) {
            result |= VISIBLE_CHILD;
        }

        if (computedVerticalScrollingEnabled) {
            result |= VISIBLE_V_BAR;
        }

        if (computedHorizontalScrollingEnabled) {
            result |= VISIBLE_H_BAR;
        }

        return result;
    }

    @Override
    public final int getChildCount() {
        return Integer.bitCount(visibleChildren());
    }

    @Override
    public final Collection<Component> getChildren() {
        return getChildrenList();
    }

    @Override
    public List<Component> getChildrenList() {
        final int visibleChildren = visibleChildren();

        if (visibleChildren == 0) {
            return Collections.emptyList();
        }

        if (visibleChildren == VISIBLE_CHILD) {
            return Collections.singletonList(component);
        }

        final List<Component> children = new ArrayList<Component>(3);

        if ((visibleChildren & VISIBLE_CHILD) != 0) {
            children.add(component);
        }

        if ((visibleChildren & VISIBLE_V_BAR) != 0) {
            children.add(verticalScrollBar);
        }

        if ((visibleChildren & VISIBLE_H_BAR) != 0) {
            children.add(horizontalScrollBar);
        }

        return children;
    }

    @Override
    public final Component getComponent() {
        return component;
    }

    @Override
    public final void setComponent(final Component component) {
        final Component oldComponent = this.component;

        if (oldComponent == component) {
            return;
        }

        if (oldComponent != null) {
            removeComponent(oldComponent);
        }

        if (component != null) {
            this.component = component;
            component.onAdded(this);
            component.setPosition(TerminalPosition.of(0, 0));
            contentAdapter.setAdaptedComponent(component);
            invalidate();
        }
    }

    @Override
    public boolean containsComponent(final Component component) {
        return component != null && component.hasParent(this);
    }

    @Override
    public boolean removeComponent(final Component component) {
        if (component == this.component) {
            contentAdapter.setAdaptedComponent(null);
            this.component = null;
            clearCachedLayout();
            component.onRemoved(this);
            invalidate();
            return true;
        }
        return false;
    }

    // <editor-fold defaultstate="collapsed" desc="Input Handling">

    @Override
    public boolean handleInput(final KeyStroke key) {
        if (key.isAltDown()) {
            return scrollInDirection(key);
        }

        final Component c = component;

        if (c instanceof Interactable) {
            return ((Interactable) c).handleInput(key) != Interactable.Result.UNHANDLED ||
                   scrollInDirection(key);
        }

        return c instanceof Container &&
               (((Container) c).handleInput(key) || scrollInDirection(key));
    }

    @Override
    public void updateLookupMap(final InteractableLookupMap map) {
        final Component component = getComponent();

        if (component instanceof Container) {
            ((Container) getComponent()).updateLookupMap(map);
        }
        else if (component instanceof Interactable) {
            map.add((Interactable) component);
        }
    }

    @Override
    public Interactable nextFocus(final Interactable fromThis) {
        final Component component = getComponent();

        if (fromThis == null && component instanceof Interactable) {
            return (Interactable) component;
        }
        else if (component instanceof Container) {
            return ((Container) component).nextFocus(fromThis);
        }

        return null;
    }

    @Override
    public Interactable previousFocus(final Interactable fromThis) {
        final Component component = getComponent();

        if (fromThis == null && component instanceof Interactable) {
            return (Interactable) component;
        }
        else if (component instanceof Container) {
            return ((Container) component).previousFocus(fromThis);
        }

        return null;
    }

    private boolean scrollInDirection(final KeyStroke key) {
        final boolean isControlDown = key.isCtrlDown();
//        final boolean isAltDown = key.isAltDown();
//
//        if (isAltDown) {
//            return false;
//        }

        if (key instanceof MouseAction) {
            final MouseAction mouse = (MouseAction) key;

            switch (mouse.getActionType()) {
                case SCROLL_UP:
                    lineUp();
                    return true;
                case SCROLL_DOWN:
                    lineDown();
                    return true;
            }
        }

        // @formatter:off
        switch (key.getKeyType()) {
            case ARROW_LEFT:     lineLeft();     break;
            case ARROW_RIGHT:    lineRight();    break;
            case ARROW_UP:       lineUp();       break;
            case ARROW_DOWN:     lineDown();     break;
            case PAGE_UP:        pageUp();       break;
            case PAGE_DOWN:      pageDown();     break;

            case HOME:
                if (isControlDown) {
                    scrollToTop();
                }
                else {
                    scrollToLeftEnd();
                }
                break;

            case END:
                if (isControlDown) {
                    scrollToBottom();
                }
                else {
                    scrollToRightEnd();
                }
                break;

            default:             return false;
        }
        // @formatter:on

        return true;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Scroll Operations">

    public void lineUp() {
        enqueueCommand(CommandCode.LINE_UP);
    }

    public void lineDown() {
        enqueueCommand(CommandCode.LINE_DOWN);
    }

    public void lineLeft() {
        enqueueCommand(CommandCode.LINE_LEFT);
    }

    public void lineRight() {
        enqueueCommand(CommandCode.LINE_RIGHT);
    }

    public void pageUp() {
        enqueueCommand(CommandCode.PAGE_UP);
    }

    public void pageDown() {
        enqueueCommand(CommandCode.PAGE_DOWN);
    }

    public void pageLeft() {
        enqueueCommand(CommandCode.PAGE_LEFT);
    }

    public void pageRight() {
        enqueueCommand(CommandCode.PAGE_RIGHT);
    }

    public void scrollToLeftEnd() {
        enqueueCommand(CommandCode.SET_OFFSET_H, -ScrollController.INFINITY);
    }

    public void scrollToRightEnd() {
        enqueueCommand(CommandCode.SET_OFFSET_H, ScrollController.INFINITY);
    }

    public void scrollToHome() {
        enqueueCommand(CommandCode.SET_OFFSET_H, -ScrollController.INFINITY);
        enqueueCommand(CommandCode.SET_OFFSET_V, -ScrollController.INFINITY);
    }

    public void scrollToEnd() {
        enqueueCommand(CommandCode.SET_OFFSET_H, ScrollController.INFINITY);
        enqueueCommand(CommandCode.SET_OFFSET_V, ScrollController.INFINITY);
    }

    public void scrollToTop() {
        enqueueCommand(CommandCode.SET_OFFSET_V, -ScrollController.INFINITY);
    }

    public void scrollToBottom() {
        enqueueCommand(CommandCode.SET_OFFSET_V, ScrollController.INFINITY);
    }

    public void scrollToHorizontalOffset(final int offset) {
        enqueueCommand(CommandCode.SET_OFFSET_H, validateInputOffset(offset));
    }

    public void scrollToVerticalOffset(final int offset) {
        enqueueCommand(CommandCode.SET_OFFSET_V, validateInputOffset(offset));
    }

    public void makeVisible(final Component component, final Rectangle targetRectangle) {
        enqueueCommand(new MakeVisibleParameters(component, targetRectangle));
    }

    private void makeVisible0(final Component component, final Rectangle targetRectangle) {
        final Container container = component != null ? component.getParent() : null;

        if (component == null ||
            container == null ||
            targetRectangle == null ||
            container != component && !component.hasParent(container) ||
            container != this && !container.hasParent(this)) {

            return;
        }

        Rectangle rectangle = targetRectangle;

        if (rectangle.isEmpty()) {
            TerminalSize size = component.getSize();

            if (size == null) {
                size = component.getPreferredSize();
            }

            if (size != null) {
                rectangle = Rectangle.of(size);
            }
            else {
                rectangle = Rectangle.EMPTY;
            }
        }

        final ScrollController s = scrollController;

        Rectangle newRectangle;

        if (s instanceof ScrollableContentAdapter) {
            newRectangle = ((ScrollableContentAdapter) s).makeVisible(component, rectangle, false);
        }
        else {
            newRectangle = s.makeVisible(component, rectangle);
        }

        if (!newRectangle.isEmpty()) {
            final TerminalPosition offset = container.toAncestor(this, newRectangle.getPosition());

            if (offset != null) {
                newRectangle = newRectangle.offset(offset);
            }
        }

        bringIntoView(newRectangle);
    }

    static ScrollViewer findScrollViewer(final Component component) {
        Component current = component != null ? component.getParent() : null;

        while (current != null) {
            if (current instanceof ScrollViewer) {
                return (ScrollViewer) current;
            }
            current = current.getParent();
        }

        return null;
    }

    final void bringIntoView(final Rectangle rectangle) {
        final ScrollViewer svAncestor = findScrollViewer(this);

        if (svAncestor != null) {
            svAncestor.makeVisible(this, rectangle);
        }
    }

    /**
     * Determines whether {@code positionInComponent} -- a position expressed in {@code component}'s own local
     * coordinate space, such as the value returned by {@link Interactable#getCursorLocation()} -- is actually
     * visible on screen. A position can be clipped from view when {@code component} is nested (directly or
     * indirectly) inside one or more {@link ScrollViewer ScrollViewers} and has been scrolled outside of their
     * viewport, even though it's still within the bounds of {@code component} itself.
     * <p>
     * This queries the live {@link ScrollController} for each ancestor {@code ScrollViewer} rather than the
     * viewer's own (deferred, one-frame-delayed) cached viewport size, since this needs to reflect what was
     * actually just drawn.
     */
    static boolean isPositionVisible(final Component component, final TerminalPosition positionInComponent) {
        Container parent = component.getParent();

        while (parent != null) {
            if (parent instanceof ScrollViewer) {
                final ScrollViewer sv = (ScrollViewer) parent;
                final ScrollController controller = sv.getScrollController();
                final TerminalPosition positionInViewer = component.toAncestor(sv, positionInComponent);

                if (controller == null ||
                    positionInViewer == null ||
                    positionInViewer.getColumn() < 0 ||
                    positionInViewer.getRow() < 0 ||
                    positionInViewer.getColumn() >= controller.getViewportWidth() ||
                    positionInViewer.getRow() >= controller.getViewportHeight()) {

                    return false;
                }
            }

            parent = parent.getParent();
        }

        return true;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Properties">

    public boolean getCanContentControlScrolling() {
        return contentAdapter.getCanContentControlScrolling();
    }

    public void setCanContentControlScrolling(final boolean value) {
        contentAdapter.setCanContentControlScrolling(value);
    }

    public ScrollBarPolicy getHorizontalScrollBarPolicy() {
        return horizontalScrollBarPolicy;
    }

    public void setHorizontalScrollBarPolicy(final ScrollBarPolicy policy) {
        horizontalScrollBarPolicy = policy;
        invalidate();
    }

    public ScrollBarPolicy getVerticalScrollBarPolicy() {
        return verticalScrollBarPolicy;
    }

    public void setVerticalScrollBarPolicy(final ScrollBarPolicy policy) {
        verticalScrollBarPolicy = policy;
        invalidate();
    }

    public boolean isHorizontalScrollingEnabled() {
        return computedHorizontalScrollingEnabled;
    }

    public boolean isVerticalScrollingEnabled() {
        return computedVerticalScrollingEnabled;
    }

    public int getVerticalOffset() {
        return cachedVerticalOffset;
    }

    public void setVerticalOffset(final int offset) {
        enqueueCommand(CommandCode.SET_OFFSET_V, offset);
    }

    public int getHorizontalOffset() {
        return cachedHorizontalOffset;
    }

    public void setHorizontalOffset(final int offset) {
        enqueueCommand(CommandCode.SET_OFFSET_H, offset);
    }

    public int getViewportWidth() {
        return cachedViewportWidth;
    }

    public int getViewportHeight() {
        return cachedViewportHeight;
    }

    public int getExtentWidth() {
        return cachedExtentWidth;
    }

    public int getExtentHeight() {
        return cachedExtentHeight;
    }

    public int getScrollableWidth() {
        return max(0, getExtentWidth() - getViewportWidth());
    }

    public int getScrollableHeight() {
        return max(0, getExtentHeight() - getViewportHeight());
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Component Overrides">

    @Override
    public synchronized void onAdded(final Container container) {
        super.onAdded(container);

        if (isLayoutUpdaterRequired) {
            ensureProcessing();
        }
    }

    @Override
    public synchronized void onRemoved(final Container container) {
        cancelLayoutUpdater();
        renderer = null;
        super.onRemoved(container);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="ScrollOwner Implementation">

    public void invalidateScrollInfo() {
        final ScrollController s = scrollController;

        if (s == null) {
            return;
        }

        if (!isLayoutInProgress && (!isRenderInProgress || !isLayoutInvalidatedFromRender)) {
            //
            // Check if we should remove/add scrollbars.
            //

            int extent = s.getExtentWidth();
            int viewport = s.getViewportWidth();

            if (horizontalScrollBarPolicy == ScrollBarPolicy.AUTO &&
                (extent > viewport && !computedHorizontalScrollingEnabled ||
                 extent <= viewport && computedHorizontalScrollingEnabled)) {

                invalidate();
            }
            else {
                extent = s.getExtentHeight();
                viewport = s.getViewportHeight();

                if (verticalScrollBarPolicy == ScrollBarPolicy.AUTO &&
                    (extent > viewport && !computedVerticalScrollingEnabled ||
                     computedVerticalScrollingEnabled && viewport >= extent)) {

                    invalidate();
                }
            }
        }

        //
        // If any scrolling properties have actually changed, fire public events post-render.
        //

        if (getHorizontalOffset() != s.getHorizontalOffset() ||
            getVerticalOffset() != s.getVerticalOffset() ||
            getViewportWidth() != s.getViewportWidth() ||
            getViewportHeight() != s.getViewportHeight() ||
            getExtentWidth() != s.getExtentWidth() ||
            getExtentHeight() != s.getExtentHeight()) {

            ensureLayoutUpdater();
        }
    }

    public void setScrollController(final ScrollController controller) {
        scrollController = controller;

        if (controller != null) {
            controller.setHorizontalScrollingEnabled(horizontalScrollBarPolicy != ScrollBarPolicy.DISABLED);
            controller.setVerticalScrollingEnabled(verticalScrollBarPolicy != ScrollBarPolicy.DISABLED);
            ensureLayoutUpdater();
        }
    }

    public ScrollController getScrollController() {
        return scrollController;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Layout">

    private boolean isLayoutInProgress;
    private boolean isLayoutInvalidatedFromRender;
    private boolean isRenderInProgress;

    private final static int VISIBLE_CHILD = 0x01;
    private final static int VISIBLE_V_BAR = 0x02;
    private final static int VISIBLE_H_BAR = 0x04;

    @Override
    public void invalidate() {
        if (isRenderInProgress) {
            if (!isLayoutInvalidatedFromRender) {
                ensureLayoutUpdater();
            }
            isLayoutInvalidatedFromRender = true;
        }

//        setPreferredSize(null);

        super.invalidate();
    }

    private void clearCachedLayout() {
        computedHorizontalScrollingEnabled = false;
        computedVerticalScrollingEnabled = false;
        cachedHorizontalOffset = 0;
        cachedVerticalOffset = 0;
        cachedExtentHeight = 0;
        cachedExtentWidth = 0;
        cachedViewportWidth = 0;
        cachedViewportHeight = 0;
    }

    private void updateLayout() {
        if (executeNextCommand()) {
            invalidate();
            return;
        }

        final int oldActualHorizontalOffset = getHorizontalOffset();
        final int oldActualVerticalOffset = getVerticalOffset();

        final int oldViewportWidth = getViewportWidth();
        final int oldViewportHeight = getViewportHeight();

        final int oldExtentWidth = getExtentWidth();
        final int oldExtentHeight = getExtentHeight();

        final int oldScrollableWidth = getScrollableWidth();
        final int oldScrollableHeight = getScrollableHeight();

        boolean changed = false;

        final ScrollController s = getScrollController();

        // Go through scrolling properties and update any inconsistent values.

        if (s != null && oldActualHorizontalOffset != s.getHorizontalOffset()) {
            cachedHorizontalOffset = s.getHorizontalOffset();
            changed = true;
        }

        if (s != null && oldActualVerticalOffset != s.getVerticalOffset()) {
            cachedVerticalOffset = s.getVerticalOffset();
            changed = true;
        }

        if (s != null && oldViewportWidth != s.getViewportWidth()) {
            cachedViewportWidth = s.getViewportWidth();
            changed = true;
        }

        if (s != null && oldViewportHeight != s.getViewportHeight()) {
            cachedViewportHeight = s.getViewportHeight();
            changed = true;
        }

        if (s != null && oldExtentWidth != s.getExtentWidth()) {
            cachedExtentWidth = s.getExtentWidth();
            changed = true;
        }

        if (s != null && oldExtentHeight != s.getExtentHeight()) {
            cachedExtentHeight = s.getExtentHeight();
            changed = true;
        }

        changed |= getScrollableWidth() != oldScrollableWidth ||
                   getScrollableHeight() != oldScrollableHeight;

        if (changed) {
            try {
                onScrollChanged();
                invalidate();
                return;
            }
            finally {
                cancelLayoutUpdater();
            }
        }

        cancelLayoutUpdater();
    }

    private final static class DeferredLayoutUpdater implements Runnable {
        ScrollViewer owner;

        @Override
        public void run() {
            final ScrollViewer sv = owner;

            if (sv == null) {
                return;
            }

            sv.isLayoutUpdaterScheduled = false;

            try {
                sv.updateLayout();
            }
            finally {
                if (sv.isLayoutUpdaterRequired) {
                    sv.ensureLayoutUpdater();
                }
            }
        }
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="CommandCode & Command Queue">

    private TextGUIThread updaterThread;
    private CommandQueue queue;
    private DeferredLayoutUpdater layoutUpdater;
    private boolean isLayoutUpdaterRequired;
    private boolean isLayoutUpdaterScheduled;

    private void enqueueCommand(final CommandCode code) {
        ensureQueue().enqueue(code, 0, null);
        ensureLayoutUpdater();
    }

    private void enqueueCommand(final MakeVisibleParameters parameters) {
        ensureQueue().enqueue(CommandCode.MAKE_VISIBLE, 0, parameters);
        ensureLayoutUpdater();
    }

    private void enqueueCommand(final CommandCode code, final int parameter) {
        ensureQueue().enqueue(code, parameter, null);
        ensureLayoutUpdater();
    }

    private CommandQueue ensureQueue() {
        final CommandQueue queue = this.queue;
        return queue != null ? queue : (this.queue = new CommandQueue());
    }

    private void ensureProcessing() {
        final CommandQueue queue = this.queue;

        if (queue != null && !queue.isEmpty()) {
            ensureLayoutUpdater();
        }
    }

    private TextGUIThread ensureUpdaterThread() {
        final TextGUIThread updaterThread = this.updaterThread;

        if (updaterThread != null) {
            return updaterThread;
        }

        final TextGUI textGUI = getTextGUI();

        if (textGUI == null) {
            return null;
        }

        return this.updaterThread = textGUI.getGUIThread();
    }

    private void cancelLayoutUpdater() {
        final DeferredLayoutUpdater u = layoutUpdater;

        if (u != null) {
            u.owner = null;
        }

        updaterThread = null;
        layoutUpdater = null;
        isLayoutUpdaterRequired = false;
        isLayoutUpdaterScheduled = false;
    }

    private void ensureLayoutUpdater() {
        isLayoutUpdaterRequired = true;

        if (isLayoutUpdaterScheduled) {
            return;
        }

        final TextGUIThread guiThread = ensureUpdaterThread();

        if (guiThread == null) {
            return;
        }

        DeferredLayoutUpdater queueProcessor = this.layoutUpdater;

        if (queueProcessor == null) {
            queueProcessor = new DeferredLayoutUpdater();
            this.layoutUpdater = queueProcessor;
        }

        queueProcessor.owner = this;
        guiThread.invokeLater(queueProcessor);
        isLayoutUpdaterScheduled = true;
    }

    private enum CommandCode {
        INVALID,
        LINE_UP,
        LINE_DOWN,
        LINE_LEFT,
        LINE_RIGHT,
        PAGE_UP,
        PAGE_DOWN,
        PAGE_LEFT,
        PAGE_RIGHT,
        SET_OFFSET_H,
        SET_OFFSET_V,
        MAKE_VISIBLE
    }

    private final static class Command {
        final static Command INVALID = new Command(CommandCode.INVALID, 0);

        CommandCode code;
        int parameter;
        MakeVisibleParameters makeVisibleParameter;

        Command(final CommandCode code, final int parameter) {
            this.code = code;
            this.parameter = parameter;
        }
    }

    private final static class MakeVisibleParameters {
        Component child;
        Rectangle targetRectangle;

        MakeVisibleParameters(final Component child, final Rectangle targetRectangle) {
            this.child = child;
            this.targetRectangle = targetRectangle;
        }
    }

    private final static class CommandQueue {
        private static final int CAPACITY = 32;

        private final Command[] array;

        private int lastWritePosition;
        private int lastReadPosition;

        CommandQueue() {
            array = new Command[CAPACITY];

            for (int i = 0; i < array.length; i++) {
                array[i] = new Command(CommandCode.INVALID, 0);
            }
        }

        final void enqueue(final CommandCode code, final int parameter, final MakeVisibleParameters mvp) {
            if (!optimizeCommand(code, parameter)) {
                final int newWritePosition = (lastWritePosition + 1) % CAPACITY;

                if (newWritePosition == lastReadPosition) {
                    lastReadPosition = (lastReadPosition + 1) % CAPACITY;
                }

                final Command command = array[newWritePosition];

                command.code = code;
                command.parameter = parameter;
                command.makeVisibleParameter = mvp;

                lastWritePosition = newWritePosition;
            }
        }

        final boolean optimizeCommand(final CommandCode code, final int parameter) {
            if (lastWritePosition != lastReadPosition) {
                if ((code == CommandCode.SET_OFFSET_H &&
                     array[lastWritePosition].code == CommandCode.SET_OFFSET_H) ||
                    (code == CommandCode.SET_OFFSET_V &&
                     array[lastWritePosition].code == CommandCode.SET_OFFSET_V)) {

                    // If the last command was "set offset" or "make visible", simply replace it.
                    array[lastWritePosition].parameter = parameter;
                    return true;
                }
            }
            return false;
        }

        final Command fetch() {
            if (lastWritePosition == lastReadPosition) {
                return Command.INVALID;
            }

            lastReadPosition = (lastReadPosition + 1) % CAPACITY;

            return array[lastReadPosition];
        }

        final boolean isEmpty() {
            return lastWritePosition == lastReadPosition;
        }
    }

    final boolean executeNextCommand() {
        final ScrollController s = this.scrollController;

        if (s == null) {
            return false;
        }

        final CommandQueue q = queue;
        final Command c = q != null ? q.fetch() : Command.INVALID;
        final MakeVisibleParameters mvp = c.makeVisibleParameter;

        c.makeVisibleParameter = null; // Release object for GC

        if (c.code == CommandCode.INVALID) {
            return false;
        }

        // @formatter:off
        switch (c.code) {
            case LINE_UP:       s.lineUp();                                     break;
            case LINE_DOWN:     s.lineDown();                                   break;
            case LINE_LEFT:     s.lineLeft();                                   break;
            case LINE_RIGHT:    s.lineRight();                                  break;
            case PAGE_UP:       s.pageUp();                                     break;
            case PAGE_DOWN:     s.pageDown();                                   break;
            case PAGE_LEFT:     s.pageLeft();                                   break;
            case PAGE_RIGHT:    s.pageRight();                                  break;

            case SET_OFFSET_H:  s.setHorizontalOffset(c.parameter);             break;
            case SET_OFFSET_V:  s.setVerticalOffset(c.parameter);               break;

            case MAKE_VISIBLE:  makeVisible0(mvp.child, mvp.targetRectangle);   break;
        }
        // @formatter:on

        return true;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Renderer">

    //
    // NOTE: ScrollViewer has no look of its own; it's just a viewport with a pair of scrollbars.
    //       Since the default renderer is responsible for complex layout, we expressly prohibit
    //       specifying a different renderer.
    //

    @Override
    public final synchronized ComponentRenderer<ScrollViewer> getRenderer() {
        ComponentRenderer<ScrollViewer> renderer = this.renderer;

        if (renderer == null) {
            this.renderer = renderer = createDefaultRenderer();
        }

        return renderer;
    }

    @Override
    public final ScrollViewer setRenderer(final ComponentRenderer<ScrollViewer> renderer) {
        throw new UnsupportedOperationException("ScrollViewer does not support custom renderers.");
    }

    @Override
    protected final ComponentRenderer<ScrollViewer> createDefaultRenderer() {
        return new ScrollViewerRenderer();
    }

    private final static class ScrollViewerRenderer implements ComponentRenderer<ScrollViewer> {
        private boolean neverMeasured = true;
        private TerminalSize desiredChildSize;
        private TerminalSize hBarSize;
        private TerminalSize vBarSize;

        @Override
        public TerminalSize getPreferredSize(final ScrollViewer sv) {
            final boolean wasLayoutInProgress = sv.isLayoutInProgress;

            TerminalSize desiredSize = TerminalSize.ZERO;

            sv.isLayoutInProgress = true;

            try {
                final Component child = sv.component;

                if (child != null) {
                    child.setPreferredSize(null);
                    desiredChildSize = desiredSize = child.getPreferredSize();
                    child.setPreferredSize(desiredSize);
                }

                if (sv.computedVerticalScrollingEnabled) {
                    vBarSize = sv.verticalScrollBar.getPreferredSize();
                    desiredSize = desiredSize.withColumns(desiredSize.getColumns() + vBarSize.getColumns());
                }
                else {
                    vBarSize = TerminalSize.ZERO;
                }

                if (sv.computedHorizontalScrollingEnabled) {
                    hBarSize = sv.horizontalScrollBar.getPreferredSize();
                    desiredSize = desiredSize.withRows(desiredSize.getRows() + hBarSize.getColumns());
                }
                else {
                    hBarSize = TerminalSize.ZERO;
                }
            }
            finally {
                sv.isLayoutInProgress = wasLayoutInProgress;
            }

            neverMeasured = false;
            return desiredSize;
        }

        @Override
        public void drawComponent(final TextGUIGraphics g, final ScrollViewer sv) {
            if (neverMeasured || sv.isInvalid()) {
                getPreferredSize(sv);
            }

            final boolean wasRenderInProgress = sv.isRenderInProgress;

            sv.isRenderInProgress = true;

            try {
                final TerminalSize size = g.getSize();
                final Component child = sv.component;

                sv.setSize(size);

                final boolean hv = computeHorizontalScrolling(sv);
                final boolean vv = computeVerticalScrolling(sv);

                if (hv != sv.computedHorizontalScrollingEnabled ||
                    vv != sv.computedVerticalScrollingEnabled) {

                    sv.computedHorizontalScrollingEnabled = hv;
                    sv.computedVerticalScrollingEnabled = vv;

                    if (!sv.isLayoutInvalidatedFromRender) {
                        sv.invalidate();
                    }
                }

                if (child != null) {
                    final int dX = vv ? -vBarSize.getColumns() - 1 : 0;
                    final int dY = hv ? -hBarSize.getRows() : 0;

                    sv.contentAdapter.draw(
                        g.newTextGraphics(
                            TerminalPosition.of(0, 0),
                            size.withRelative(dX, dY)
                        ),
                        desiredChildSize
                    );
                }

//                sv.updateLayout();

                sv.horizontalScrollBar.setViewSize(sv.getViewportWidth());
                sv.horizontalScrollBar.setScrollMaximum(sv.getExtentWidth());
                sv.horizontalScrollBar.setScrollPosition(sv.getHorizontalOffset());

                sv.verticalScrollBar.setViewSize(sv.getViewportHeight());
                sv.verticalScrollBar.setScrollMaximum(sv.getExtentHeight());
                sv.verticalScrollBar.setScrollPosition(sv.getVerticalOffset());

                if (hv) {
                    if (sv.horizontalScrollBar.getParent() != sv) {
                        sv.horizontalScrollBar.onAdded(sv);
                    }

                    final TerminalPosition hPosition = new TerminalPosition(
                        1,
                        size.getRows() - hBarSize.getRows()
                    );

                    sv.horizontalScrollBar.setPosition(hPosition);

                    sv.horizontalScrollBar.draw(
                        g.newTextGraphics(
                            hPosition.withRelativeColumn(-1),
                            new TerminalSize(size.getColumns() - 1, hBarSize.getRows())
                        )
                    );
                }
                else {
                    if (sv.horizontalScrollBar.getParent() == sv) {
                        sv.horizontalScrollBar.onRemoved(sv);
                    }
                }

                if (vv) {
                    if (sv.verticalScrollBar.getParent() != sv) {
                        sv.verticalScrollBar.onAdded(sv);
                    }

                    final TerminalPosition vPosition = new TerminalPosition(
                        size.getColumns() - vBarSize.getColumns() ,
                        1
                    );

                    sv.verticalScrollBar.setPosition(vPosition);

                    sv.verticalScrollBar.draw(
                        g.newTextGraphics(
                            vPosition.withRelativeRow(-1),
                            new TerminalSize(vBarSize.getColumns(), size.getRows() - 1)
                        )
                    );
                }
                else {
                    if (sv.verticalScrollBar.getParent() == sv) {
                        sv.verticalScrollBar.onRemoved(sv);
                    }
                }
            }
            finally {
                sv.isRenderInProgress = wasRenderInProgress;
            }
        }

        private boolean computeHorizontalScrolling(final ScrollViewer sv) {
            //noinspection SimplifiableIfStatement
            if (sv.horizontalScrollBarPolicy == ScrollBarPolicy.VISIBLE) {
                return true;
            }

            return sv.horizontalScrollBarPolicy == ScrollBarPolicy.AUTO &&
                   desiredChildSize.getColumns() > sv.getSize().getColumns() - vBarSize.getColumns();
        }

        private boolean computeVerticalScrolling(final ScrollViewer sv) {
            //noinspection SimplifiableIfStatement
            if (sv.verticalScrollBarPolicy == ScrollBarPolicy.VISIBLE) {
                return true;
            }

            return sv.verticalScrollBarPolicy == ScrollBarPolicy.AUTO &&
                   desiredChildSize.getRows() > sv.getSize().getRows() - hBarSize.getRows();
        }
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Listener">

    private final List<Listener> listeners = new CopyOnWriteArrayList<Listener>();

    protected synchronized void onScrollChanged() {
        for (final Listener listener : listeners) {
            listener.onScrollChanged(this);
        }
    }

    public interface Listener {
        void onScrollChanged(ScrollViewer sv);
    }

    public void addListener(final Listener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null.");
        }
        listeners.add(listener);
    }

    public boolean removeListener(final Listener listener) {
        return listeners.remove(listener);
    }

    // </editor-fold>
}
