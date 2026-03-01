package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalTextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Model node used by {@link Tree} to represent hierarchical data.
 * <p>
 * A {@code TreeNode} contains a value and a label, a reference to its parent, and a list of
 * children. Nodes can be marked as expanded/collapsed and visible/hidden. Utility methods are
 * provided to navigate between visible nodes in depth-first order which is how {@link Tree}
 * renders and navigates.
 * </p>
 *
 * @param <V> Type of the value associated with this node
 */
public class TreeNode<V> {

    private final V value;
    private boolean expanded;
    private boolean focused = false;
    private boolean visible = true;
    private String label;

    private TreeNode<V> parent;
    private final List<TreeNode<V>> children = new ArrayList<>();

    /**
     * Creates an expanded node with the label derived from {@code value.toString()}.
     *
     * @param value Value held by this node; may be {@code null}
     */
    public TreeNode(V value) {
        this(value, true);
    }

    /**
     * Creates a node with the label derived from {@code value.toString()} and the specified
     * expanded state.
     *
     * @param value    Value held by this node; may be {@code null}
     * @param expanded {@code true} if the node should be initially expanded
     * @throws IllegalArgumentException if the generated label contains line breaks
     */
    public TreeNode(V value, boolean expanded) {
        this.value = value;
        this.label = value == null ? "" : value.toString();
        if (label.contains("\n") || label.contains("\r")) {
            throw new IllegalArgumentException("Multiline tree nodes are not supported");
        }
        this.expanded = expanded;
    }

    /**
     * Creates a node with an explicit label and expanded state.
     *
     * @param value    Value held by this node; may be {@code null}
     * @param label    Text displayed for this node; line breaks are not allowed
     * @param expanded {@code true} if the node should be initially expanded
     * @throws IllegalArgumentException if {@code label} contains line breaks
     */
    public TreeNode(V value, String label, boolean expanded) {
        this.value = value;
        this.label = label == null ? "" : label;
        if (this.label.contains("\n") || this.label.contains("\r")) {
            throw new IllegalArgumentException("Multiline tree nodes are not supported");
        }
        this.expanded = expanded;
    }

    /**
     * Adds an existing node as a child of this node.
     *
     * @param node Child node to add; must not be {@code null}
     * @return The same {@code node} instance for chaining
     * @throws IllegalArgumentException if {@code node} is {@code null}
     */
    public TreeNode<V> addChild(TreeNode<V> node) {
        if (node == null) {
            throw new IllegalArgumentException("Node can't be null");
        }
        node.setParent(this);
        children.add(node);
        return node;
    }

    /**
     * Convenience method to create and add an expanded child with the label derived from
     * {@code value.toString()}.
     *
     * @param value Value for the new child node; may be {@code null}
     * @return The created child node
     */
    public TreeNode<V> addChild(V value) {
        return addChild(value, true);
    }

    /**
     * Creates and adds a child with the label derived from {@code value.toString()} and the
     * specified expanded state.
     *
     * @param value    Value for the new child node; may be {@code null}
     * @param expanded {@code true} if the child should be initially expanded
     * @return The created child node
     */
    public TreeNode<V> addChild(V value, boolean expanded) {
        TreeNode<V> node = new TreeNode<>(value, expanded);
        return addChild(node);
    }

    /**
     * Removes a child node from this node. If the child is not present this is a no-op.
     *
     * @param node Child node to remove; if {@code null} nothing happens
     */
    public void removeChild(TreeNode<V> node) {
        if (node == null) {
            return;
        }
        node.setParent(null);
        children.remove(node);
    }

    /**
     * Returns the label displayed for this node.
     *
     * @return Node label (never {@code null})
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the label displayed for this node. Line breaks are not allowed.
     *
     * @param label New label; {@code null} is treated as an empty string
     */
    public void setLabel(String label) {
        if (label.contains("\n") || label.contains("\r")) {
            throw new IllegalArgumentException("Multiline tree nodes are not supported");
        }
        this.label = label;
    }

    /**
     * Returns the mutable list of children for this node.
     *
     * @return List of children (never {@code null})
     */
    public List<TreeNode<V>> getChildren() {
        return children;
    }

    /**
     * Sets this node's expanded state. A node without children will be treated as collapsed
     * by {@link #isExpanded()} regardless of this flag.
     *
     * @param expanded {@code true} to mark as expanded
     */
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    /**
     * Returns whether this node is considered expanded and has at least one child.
     *
     * @return {@code true} if expanded and non-empty, {@code false} otherwise
     */
    public boolean isExpanded() {
        return !children.isEmpty() && expanded;
    }

    /**
     * Returns whether this node currently has keyboard focus in a {@link Tree}.
     *
     * @return {@code true} if focused
     */
    public boolean isFocused() {
        return focused;
    }

    /**
     * Sets whether this node currently has keyboard focus in a {@link Tree}.
     *
     * @param focused {@code true} if focused
     */
    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    /**
     * Returns this node's parent, or {@code null} if it is a root node.
     *
     * @return Parent node or {@code null}
     */
    public TreeNode<V> getParent() {
        return parent;
    }

    /**
     * Sets this node's parent reference. Normally managed by {@link #addChild(TreeNode)} and
     * {@link #removeChild(TreeNode)}.
     *
     * @param parent New parent; may be {@code null}
     */
    public void setParent(TreeNode<V> parent) {
        this.parent = parent;
    }

    /**
     * Returns whether this node is visible in the {@link Tree}. Invisible nodes are skipped
     * during rendering and navigation.
     *
     * @return {@code true} if visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Sets whether this node should be visible in the {@link Tree}.
     *
     * @param visible {@code true} to make visible
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Computes how many rows of descendants this node contributes when expanded, considering
     * only visible children. Collapsed nodes contribute 0.
     *
     * @return Number of visible rows contained under this node when expanded
     */
    public int getExpandedLength() {
        if (!expanded) {
            return 0;
        }
        int max = 0;
        for (TreeNode<V> child : children) {
            if (child.isVisible()) {
                max = Math.max(max, child.getExpandedLength());
            }
        }
        return max;
    }

    /**
     * Computes the maximum width, in columns, needed to render this node and its visible
     * descendants when expanded.
     *
     * @param level       Current indentation level (0 for root)
     * @param bracketSize Number of columns used by brackets/markers before the label
     * @return Maximum width in columns
     */
    public int getMaxExpandedWidth(int level, int bracketSize) {
        if (!expanded) {
            return 0;
        }

        int maxWith = bracketSize + level + TerminalTextUtils.getColumnWidth(label);
        int nextLevel = level + 1;
        for (TreeNode<V> child : children) {
            if (child.isVisible()) {
                maxWith = Math.max(maxWith, child.getMaxExpandedWidth(nextLevel, bracketSize));
            }
        }

        return maxWith;
    }

    /**
     * Returns whether this node has no children.
     *
     * @return {@code true} if there are no children
     */
    public boolean isLeaf() {
        return children.isEmpty();
    }

    /**
     * Toggles this node's expanded state.
     */
    public void toggleExpanded() {
        this.expanded = !this.expanded;
    }

    /**
     * Returns the value associated with this node.
     *
     * @return The value; may be {@code null}
     */
    public V getValue() {
        return value;
    }

    /**
     * Returns the last visible descendant when following expanded branches, or this node if
     * there are no visible expanded descendants.
     *
     * @return Last expanded visible descendant, or {@code this}
     */
    public TreeNode<V> getLastExpandedChildren() {
        if (children.isEmpty() || !expanded) {
            return this;
        }

        TreeNode<V> lastVisibleChild = getLastVisibleChildren();
        if (lastVisibleChild != null) {
            return lastVisibleChild.getLastExpandedChildren();
        }
        return this;
    }

    /**
     * Computes the number of visible ancestors of this node, effectively the indentation level
     * used when rendering in {@link Tree}.
     *
     * @return Level starting at 0 for the root
     */
    public int computeLevel() {
        int level = 0;
        TreeNode<V> prevLevelNode = getParent();
        while (prevLevelNode != null) {
            if (prevLevelNode.isVisible()) {
                level++;
            }
            prevLevelNode = prevLevelNode.getParent();
        }
        return level;
    }

    /**
     * Computes the 1-based depth index of this node in a depth-first, visible-only traversal,
     * counting from the first visible node above it.
     *
     * @return Depth index (minimum 1)
     */
    public int computeDepth() {
        int depth = 1;
        TreeNode<V> prevNode = getPreviousNode();
        while (prevNode != null && prevNode.isVisible()) {
            prevNode = prevNode.getPreviousNode();
            depth++;
        }
        return depth;
    }

    /**
     * Returns the node that is {@code depth} steps ahead of this node in a visible-only
     * depth-first traversal.
     *
     * @param depth Number of steps to move forward (0 returns this node)
     * @return Target node or {@code null} if the traversal ends before reaching the depth
     */
    public TreeNode<V> getNodeAtDepth(int depth) {
        int currentDepth = 0;
        TreeNode<V> currentNode = this;
        while (currentDepth < depth && currentNode != null) {
            currentNode = currentNode.getNextNode();
            currentDepth++;
        }
        return currentNode;
    }

    /**
     * Computes how many visible steps ahead the {@code targetNode} is from this node in a
     * depth-first traversal.
     *
     * @param targetNode Node to measure distance to
     * @return Non-negative distance if reachable by moving forward, otherwise {@code -1}
     */
    public int getDepthTo(TreeNode<V> targetNode) {
        int currentDepth = 0;
        TreeNode<V> currentNode = this;
        while (currentNode.getNextNode() != null && currentNode != targetNode) {
            currentNode = currentNode.getNextNode();
            currentDepth++;
        }
        return currentNode == targetNode ? currentDepth : -1;
    }


    /**
     * Returns the last direct child in the {@link #getChildren()} list.
     *
     * @return Last child node
     * @throws IndexOutOfBoundsException if there are no children
     */
    public TreeNode<V> getLastDirectChildren() {
        return getLastVisibleChildren();
    }

    /**
        * Returns the previous visible node in a depth-first traversal, or {@code null} if this
        * is the first visible node.
        *
        * @return Previous visible node or {@code null}
        */
    public TreeNode<V> getPreviousNode() {
        if (getParent() != null) {
            return getParent().getPreviousNode(this);
        }
        return null;
    }

    private TreeNode<V> getPreviousNode(TreeNode<V> treeNode) {
        int childIndex = children.indexOf(treeNode);
        TreeNode<V> prevChildren = getLastVisibleChildren(childIndex - 1);
        if (prevChildren != null) {
            return prevChildren.getLastExpandedChildren();
        } else if (isVisible()) {
            return this;
        } else if (parent != null) {
            return parent.getPreviousNode(treeNode);
        }
        return null;
    }

    /**
     * Returns the next visible node in a depth-first traversal, or {@code null} if this is the
     * last visible node.
     *
     * @return Next visible node or {@code null}
     */
    public TreeNode<V> getNextNode() {
        TreeNode<V> nextVisibleChildren = getFirstVisibleChildren();
        if (isExpanded() && nextVisibleChildren != null) {
            return nextVisibleChildren;
        } else if (getParent() != null) {
            return getParent().getNextNode(this);
        }
        return null;
    }

    private TreeNode<V> getNextNode(TreeNode<V> treeNode) {
        int childIndex = children.indexOf(treeNode);
        TreeNode<V> nextChildren = getFirstVisibleChildren(childIndex + 1);
        if (nextChildren != null) {
            return nextChildren;
        } else if (getParent() != null) {
            return getParent().getNextNode(this);
        }
        return null;
    }

    private TreeNode<V> getFirstVisibleChildren() {
        return getFirstVisibleChildren(0);
    }

    private TreeNode<V> getFirstVisibleChildren(int start) {
        for (int i = start; i < children.size(); i++) {
            if (children.get(i).isVisible()) {
                return children.get(i);
            }
        }
        return null;
    }

    private TreeNode<V> getLastVisibleChildren() {
        return getLastVisibleChildren(children.size() - 1);
    }

    private TreeNode<V> getLastVisibleChildren(int start) {
        start = Math.min(start, children.size() - 1);
        for (int i = start; i >= 0 ; i--) {
            if(children.get(i).isVisible()) {
                return children.get(i);
            }
        }
        return null;
    }
}
