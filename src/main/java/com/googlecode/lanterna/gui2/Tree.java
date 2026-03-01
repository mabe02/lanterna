package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TerminalTextUtils;
import com.googlecode.lanterna.graphics.ThemeDefinition;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.input.MouseAction;
import com.googlecode.lanterna.input.MouseActionType;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Interactive tree component for Lanterna GUI.
 * <p>
 * A {@code Tree} displays a hierarchical set of {@link TreeNode} instances and lets the user
 * navigate with keyboard and mouse, expand/collapse branches, and select a node. The component
 * renders using a simple text representation with optional brackets and a marker indicating
 * collapsed, expanded, or leaf state. Scrolling is supported when the visible area is smaller
 * than the number of visible nodes.
 * </p>
 *
 * @param <V> Type of the value stored in each {@link TreeNode}
 */
public class Tree<V> extends AbstractInteractableComponent<Tree<V>> {

    /**
     * Listener interface that can be attached to the {@code Tree} in order to be notified on user actions
     */
    public interface Listener<V> {
        /**
         * Called by the {@code Tree} when the user changes the toggle state of one item
         * @param treeNode that has been changed
         */
        void onToggleChanged(TreeNode<V> treeNode);
    }

    private final TreeNode<V> root;
    private final int scrollWindowHeight;
    private final int columns;
    private TreeNode<V> selectedNode;
    private TreeNode<V> scrollingNode;

    private int selectedNodeLevel;
    private int selectedNodeDepth;

    private boolean overflowCircle = false;

    private Consumer<TreeNode<V>> nodeSelectedConsumer;
    private final List<Listener<V>> listeners = new CopyOnWriteArrayList<>();

    /**
     * Creates a new {@code Tree} bound to a root node.
     *
     * @param root               Root node of the tree; must not be {@code null}
     * @param columns            Width, in terminal columns, available for drawing the tree
     * @param scrollWindowHeight Height, in rows, of the scroll window (number of rows to display)
     * @throws IllegalArgumentException if {@code root} is {@code null}
     */
    public Tree(TreeNode<V> root, int columns, int scrollWindowHeight) {
        if (root == null) throw new IllegalArgumentException("Root must not be null");
        this.root = root;
        this.columns = columns;
        this.scrollWindowHeight = scrollWindowHeight;
        this.selectedNode = root;
        this.scrollingNode = root;
        this.root.setFocused(true);
        recomputeCursorPosition();
    }

    /**
     * Returns the root node of this tree.
     *
     * @return Root {@link TreeNode}
     */
    public TreeNode<V> getRoot() {
        return root;
    }

    /**
     * Computes the number of visible rows required to render the tree from the top-most
     * visible node to the last visible descendant, taking expansion state into account.
     *
     * @return Total visible depth of the tree (at least 1)
     */
    public int computeTreeDepth() {
        if (!root.isExpanded() || root.getChildren().isEmpty()) {
            return 1;
        }
        return root.getLastDirectChildren().getLastExpandedChildren().computeDepth();
    }

    private void recomputeCursorPosition() {
        this.selectedNodeLevel = selectedNode.computeLevel();
        this.selectedNodeDepth = selectedNode.computeDepth();
    }

    private TreeNode<V> getScrollingNode() {
        return scrollingNode;
    }

    private void updateScrollingNode() {
        if (scrollingNode.getPreviousNode() == selectedNode) {
           scrollingNode = selectedNode;
        }else {
            int delta = scrollingNode.getDepthTo(selectedNode);
            while (delta-- >= scrollWindowHeight) {
                scrollingNode = scrollingNode.getNextNode();
            }
        }
    }

    @Override
    public TerminalSize getSize() {
        return new TerminalSize(150, scrollWindowHeight);
    }

    @Override
    protected InteractableRenderer<Tree<V>> createDefaultRenderer() {
        int spacing = getThemeDefinition().getIntegerProperty(DefaultTreeRenderer.TREE_LEVEL_INDENT, DefaultTreeRenderer.DEFAULT_TREE_LEVEL_INDENT);
        boolean displayBrackets = getThemeDefinition().getBooleanProperty(DefaultTreeRenderer.DISPLAY_BRACKETS, true);
        boolean displayBblock = getThemeDefinition().getBooleanProperty(DefaultTreeRenderer.DISPLAY_BLOCK, false);
        return new DefaultTreeRenderer<>(spacing, displayBrackets, displayBblock);
    }

    @Override
    protected Result handleKeyStroke(KeyStroke keyStroke) {
        if (isKeyboardActivationStroke(keyStroke)) {
            selectedNode.toggleExpanded();
            if (nodeSelectedConsumer != null) {
                nodeSelectedConsumer.accept(selectedNode);
            }
            runOnGUIThreadIfExistsOtherwiseRunDirect(() -> {
                for(Listener<V> listener: listeners) {
                    listener.onToggleChanged(selectedNode);
                }
            });
            return Result.HANDLED;
        } else if (keyStroke.getKeyType() == KeyType.MOUSE_EVENT) {
            MouseAction mouseAction = (MouseAction) keyStroke;
            MouseActionType actionType = mouseAction.getActionType();

            if(actionType == MouseActionType.SCROLL_UP) {
                focusPrevNode();
                return Result.HANDLED;
            }
            if(actionType == MouseActionType.SCROLL_DOWN) {
                focusNextNode();
                return Result.HANDLED;
            }

            Result result = super.handleKeyStroke(keyStroke);
            int selectedDepth = getSelectedDepthByMouseAction(mouseAction);
            if (actionType == MouseActionType.CLICK_DOWN) {
                TreeNode<V> nodeAtDepth = scrollingNode.getNodeAtDepth(selectedDepth);
                if (nodeAtDepth != null) {
                    nodeAtDepth.toggleExpanded();
                    selectedNode.setFocused(false);
                    nodeAtDepth.setFocused(true);
                    selectedNode = nodeAtDepth;
                    recomputeCursorPosition();
                }
                return Result.HANDLED;
            }
            return result;
        } else if (!keyStroke.isAltDown() && !keyStroke.isCtrlDown() && !keyStroke.isShiftDown()) {
            switch (keyStroke.getKeyType()) {
                case ARROW_DOWN:
                    focusNextNode();
                    return Result.HANDLED;
                case ARROW_UP:
                    focusPrevNode();
                    return Result.HANDLED;
                case TAB:
                    return Result.MOVE_FOCUS_NEXT;
                case REVERSE_TAB:
                    return Result.MOVE_FOCUS_PREVIOUS;
                case ARROW_RIGHT:
                    return Result.MOVE_FOCUS_RIGHT;
                case ARROW_LEFT:
                    return Result.MOVE_FOCUS_LEFT;
                case HOME:
                    selectFirstNode();
                    return Result.HANDLED;
                case END:
                    selectLastNode();
                    return Result.HANDLED;

                case PAGE_UP:
                    for (int i = 0; i < scrollWindowHeight; i++) {
                        if(!focusPrevNode()) {
                            break;
                        }
                    }
                    scrollingNode = selectedNode;
                    return Result.HANDLED;

                case PAGE_DOWN:
                    for (int i = 0; i < scrollWindowHeight; i++) {
                        if(!focusNextNode()) {
                            break;
                        }
                    }
                    scrollingNode = selectedNode;
                    return Result.HANDLED;
                default:
                    return Result.UNHANDLED;
            }
        }
        return Result.UNHANDLED;
    }

    /**
     * Moves focus/selection to the first visible node.
     */
    public void selectFirstNode() {
        selectedNode.setFocused(false);
        TreeNode<V> firstNode = isDisplayRoot() ? root : root.getChildren().get(0);
        firstNode.setFocused(true);
        this.selectedNode = firstNode;
        this.scrollingNode = firstNode;
        recomputeCursorPosition();
    }

    /**
     * Moves focus/selection to the last visible node.
     */
    public void selectLastNode() {
        selectedNode.setFocused(false);
        TreeNode<V> lastNode = getRoot().isExpanded() ? root.getLastDirectChildren().getLastExpandedChildren() : root;
        lastNode.setFocused(true);
        this.selectedNode = lastNode;
        updateScrollingNode();
        recomputeCursorPosition();
    }

    /**
     * By converting {@link TerminalPosition}s to
     * {@link #toGlobal(TerminalPosition)} gets index clicked on by mouse action.
     *
     * @return index of an item that was clicked on with {@link MouseAction}
     */
    protected int getSelectedDepthByMouseAction(MouseAction click) {
        return click.getPosition().getRow() - getGlobalPosition().getRow();
    }

    private boolean focusNextNode() {
        TreeNode<V> nextNode = selectedNode.getNextNode();
        if (nextNode == null && overflowCircle) {
            selectFirstNode();
            return false;
        } else if (nextNode != null) {
            selectedNode.setFocused(false);
            nextNode.setFocused(true);
            this.selectedNode = nextNode;
            updateScrollingNode();
            recomputeCursorPosition();
            return true;
        }
        return false;
    }

    private boolean focusPrevNode() {
        TreeNode<V> previousNode = selectedNode.getPreviousNode();
        if (previousNode == null && overflowCircle) {
            selectLastNode();
            return false;
        } else if (previousNode != null) {
            selectedNode.setFocused(false);
            previousNode.setFocused(true);
            this.selectedNode = previousNode;
            updateScrollingNode();
            recomputeCursorPosition();
            return true;
        }
        return false;
    }

    /**
     * Returns whether the root node is displayed (visible) as part of the tree.
     *
     * @return {@code true} if the root is visible, {@code false} otherwise
     */
    public boolean isDisplayRoot() {
        return getRoot().isVisible();
    }

    /**
     * Returns whether navigation wraps around when moving past the last/first node.
     *
     * @return {@code true} if navigation overflows in a circle, {@code false} otherwise
     */
    public boolean isOverflowCircle() {
        return overflowCircle;
    }

    /**
     * Enables or disables circular navigation when moving beyond the first or last node.
     *
     * @param overflowCircle {@code true} to wrap around, {@code false} to stop at ends
     */
    public void setOverflowCircle(boolean overflowCircle) {
        this.overflowCircle = overflowCircle;
    }

    /**
     * Shows or hides the root node. When hidden, focus moves to the first visible child
     * if one exists.
     *
     * @param displayRoot {@code true} to show the root node, {@code false} to hide it
     */
    public void setDisplayRoot(boolean displayRoot) {
        getRoot().setVisible(displayRoot);
        if (!getRoot().getChildren().isEmpty()) {
            TreeNode<V> treeNode = getRoot().getChildren().get(0);
            treeNode.setFocused(true);
            this.selectedNode = treeNode;
            this.scrollingNode = treeNode;
            recomputeCursorPosition();
        }
    }

    /**
     * Returns the currently selected (focused) node.
     *
     * @return Selected {@link TreeNode}
     */
    public TreeNode<V> getSelectedNode() {
        return selectedNode;
    }

    /**
     * Sets a consumer that will be invoked when a node is activated (for example by the
     * keyboard activation key or mouse click). The consumer receives the currently selected
     * node. This does not affect the internal expand/collapse behavior.
     *
     * @param nodeSelectedConsumer Callback to invoke on node activation; may be {@code null}
     */
    public void setNodeSelectedConsumer(Consumer<TreeNode<V>> nodeSelectedConsumer) {
        this.nodeSelectedConsumer = nodeSelectedConsumer;
    }

    /**
     * Adds a new listener to the {@code Tree} that will be called on certain user actions
     * @param listener Listener to attach to this {@code Tree}
     * @return Itself
     */
    public synchronized Tree<V> addListener(Listener<V> listener) {
        if(listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
        return this;
    }

    /**
     * Removes a listener from this {@code Tree} so that if it had been added earlier, it will no longer be
     * called on user actions
     * @param listener Listener to remove from this {@code Tree}
     * @return Itself
     */
    public Tree<V> removeListener(Listener<V> listener) {
        listeners.remove(listener);
        return this;
    }

    /**
     * Default renderer for {@link Tree}. It draws optional brackets, an expand/collapse/leaf
     * marker, and the node label with a configurable indent per level. It also manages an
     * optional vertical scrollbar when needed.
     *
     * @param <V> Type of the value stored in each {@link TreeNode}
     */
    public static class DefaultTreeRenderer<V> implements InteractableRenderer<Tree<V>> {

        public static final int DEFAULT_TREE_LEVEL_INDENT = 1;

        public static final String LEFT_BRACKET = "LEFT_BRACKET";
        public static final String RIGHT_BRACKET = "RIGHT_BRACKET";
        public static final String EXPANDED_MARKER = "EXPANDED_MARKER";
        public static final String COLLAPSED_MARKER = "COLLAPSED_MARKER";
        public static final String LEAF_MARKER = "LEAF_MARKER";

        public static final String TREE_LEVEL_INDENT = "TREE_LEVEL_INDENT";

        public static final String DISPLAY_BRACKETS = "DISPLAY_BRACKETS";
        public static final String DISPLAY_BLOCK = "DISPLAY_BLOCK";
        public static final String DISPLAY_BLOCK_FILLER = "DISPLAY_BLOCK_FILLER";

        private final int indent;
        private final boolean displayBrackets;
        private final boolean displayBlock;

        private final ScrollBar verticalScrollBar;

        /**
         * Creates a renderer with default settings.
         */
        public DefaultTreeRenderer() {
            this(DEFAULT_TREE_LEVEL_INDENT, true, false);
        }

        /**
         * Creates a renderer with custom settings.
         *
         * @param indent          Number of columns to indent per tree level (must be {@code >= 0})
         * @param displayBrackets Whether to draw square brackets around the marker
         * @param displayBlock    Whether to fill the line up to the label with a block/filler
         * @throws IllegalArgumentException if {@code indent} is negative
         */
        public DefaultTreeRenderer(int indent, boolean displayBrackets, boolean displayBlock) {
            if (indent < 0) {
                throw new IllegalArgumentException("Indent must be >= 0");
            }
            this.indent = indent;
            this.displayBrackets = displayBrackets;
            this.displayBlock = displayBlock;
            this.verticalScrollBar = new ScrollBar(Direction.VERTICAL);
        }

        @Override
        public TerminalPosition getCursorLocation(Tree<V> tree) {
            int offset = displayBrackets ? 0 : -1;
            int row = tree.scrollingNode.getDepthTo(tree.selectedNode);
            return new TerminalPosition(1 + indent * tree.selectedNodeLevel + offset, row);
        }

        @Override
        public TerminalSize getPreferredSize(Tree<V> tree) {
            int bracketsSize =  displayBrackets ? 4 : 2;
            int width = tree.getRoot().isExpanded() ?
                    tree.getRoot().getMaxExpandedWidth(0, bracketsSize) :
                    4 + TerminalTextUtils.getColumnWidth(tree.getRoot().getLabel());
            int height = 1;
            if (tree.getRoot().isExpanded()) {
                height = tree.getRoot().getExpandedLength();
            }

            return new TerminalSize(width, height);
        }

        @Override
        public void drawComponent(TextGUIGraphics graphics, Tree<V> tree) {
            graphics.applyThemeStyle(tree.getThemeDefinition().getNormal());
            graphics.fill(' ');
            int scrollPosition = Math.max(0, tree.selectedNodeDepth - tree.scrollWindowHeight);

            TreeNode<V> activeNode = tree.getScrollingNode();
            for (int i = 0; i < tree.scrollWindowHeight; i++) {
                int level = activeNode.computeLevel();
                drawTreeNode(graphics, tree.getThemeDefinition(), activeNode, level, i, tree.columns - 3);
                activeNode = activeNode.getNextNode();
                if(activeNode == null) {
                    break;
                }
            }

            int displayedItems = tree.computeTreeDepth();
            if (displayedItems > tree.scrollWindowHeight) {
                verticalScrollBar.onAdded(tree.getParent());
                verticalScrollBar.setViewSize(tree.scrollWindowHeight);
                verticalScrollBar.setScrollMaximum(displayedItems);
                verticalScrollBar.setScrollPosition(scrollPosition);
                verticalScrollBar.draw(graphics.newTextGraphics(
                        new TerminalPosition(tree.columns - 1, 0),
                        new TerminalSize(1, graphics.getSize().getRows())));
            }
        }

        private int drawTreeNode(TextGUIGraphics graphics, ThemeDefinition themeDefinition, TreeNode<V> treeNode, int level, int depth, int columns) {
            int offset = displayBrackets ? 0 : -1;
            if (treeNode.isFocused()) {
                graphics.applyThemeStyle(themeDefinition.getActive());
            } else {
                graphics.applyThemeStyle(themeDefinition.getNormal());
            }
            int labelOffset = 4 + indent * level + offset * 2;
            String label = displayBlock ? getBlockLabel(labelOffset, treeNode.getLabel(), columns, themeDefinition.getCharacter(DISPLAY_BLOCK_FILLER, '.')) : treeNode.getLabel();
            graphics.putString(labelOffset, depth, label);

            if (displayBrackets) {
                if (treeNode.isFocused()) {
                    graphics.applyThemeStyle(themeDefinition.getPreLight());
                } else {
                    graphics.applyThemeStyle(themeDefinition.getNormal());
                }
                graphics.setCharacter(indent * level, depth, themeDefinition.getCharacter(LEFT_BRACKET, '['));
                graphics.setCharacter(2 + indent * level, depth, themeDefinition.getCharacter(RIGHT_BRACKET, ']'));
            }
            graphics.setCharacter(3 + indent * level + 2 * offset, depth, ' ');

            if (treeNode.isFocused()) {
                graphics.applyThemeStyle(themeDefinition.getSelected());
            } else {
                graphics.applyThemeStyle(themeDefinition.getNormal());
            }
            char marker = getMarker(themeDefinition, treeNode);
            graphics.setCharacter(1 + indent * level + offset, depth, marker);

            depth = depth + 1;
            if (treeNode.isExpanded()) {
                for (TreeNode<V> child : treeNode.getChildren()) {
                    if(child.isVisible()) {
                        depth = drawTreeNode(graphics, themeDefinition, child, level + 1, depth, columns);
                    }
                }
            }

            return depth;
        }

        private String getBlockLabel(int labelOffset, String label, int columns, char filler) {
            int fillSpace = columns - labelOffset - label.length();
            if(fillSpace > 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < fillSpace; i++) {
                    sb.append(filler);
                }
                label = sb.append(label).toString();
            }
            return label;
        }

        private char getMarker(ThemeDefinition themeDefinition, TreeNode<V> treeNode) {
            char marker = themeDefinition.getCharacter(COLLAPSED_MARKER, '>');
            if (treeNode.isExpanded()) {
                marker = themeDefinition.getCharacter(EXPANDED_MARKER, '<');
            } else if (treeNode.isLeaf()) {
                marker = themeDefinition.getCharacter(LEAF_MARKER, '.');
            }
            return marker;
        }
    }
}
