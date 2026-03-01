package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.input.MouseAction;
import com.googlecode.lanterna.input.MouseActionType;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link Tree} and {@link TreeNode} behavior that do not require a live GUI.
 */
public class TreeUnitTests {
    
    @Test(expected = IllegalArgumentException.class)
    public void constructorRequiresNonNullRoot() {
        new Tree<>(null, 20, 5);
    }

    @Test
    public void defaultsAfterConstruction() {
        TreeNode<String> root = new TreeNode<>("root", true);
        Tree<String> tree = new Tree<>(root, 20, 5);

        assertSame(root, tree.getRoot());
        assertSame(root, tree.getSelectedNode());
        assertTrue(root.isFocused());
        // Size should be based on scroll window height
        TerminalSize size = tree.getSize();
        assertEquals(5, size.getRows());
    }

    @Test
    public void setDisplayRootMovesSelectionToFirstChild() {
        TreeNode<String> root = new TreeNode<>("root", true);
        TreeNode<String> c1 = root.addChild("c1", true);
        root.addChild("c2", true);
        Tree<String> tree = new Tree<>(root, 20, 5);

        assertSame(root, tree.getSelectedNode());
        tree.setDisplayRoot(false);
        assertSame(c1, tree.getSelectedNode());
        assertTrue(c1.isFocused());

        // Toggling back shouldn't break anything
        tree.setDisplayRoot(true);
        // When re-enabling root display, we don't automatically move focus back to root
        assertSame(c1, tree.getSelectedNode());
    }

    @Test
    public void computeTreeDepthSimpleAndExpanded() {
        TreeNode<String> root = new TreeNode<>("root", true);
        // Collapsed root or no children => depth = 1
        Tree<String> tree1 = new Tree<>(root, 20, 5);
        assertEquals(1, tree1.computeTreeDepth());

        TreeNode<String> c1 = root.addChild("c1", true);
        TreeNode<String> c2 = root.addChild("c2", false);
        c1.addChild("c1-1", true);
        c1.addChild("c1-2", true);

        // Last direct child of root is c2 (collapsed), so last expanded in that branch is c2 itself
        // Depth is computed from root.getLastDirectChildren().getLastExpandedChildren().computeDepth()
        // which counts visible nodes from the start
        assertTrue(root.isExpanded());
        assertEquals(c2.getLastExpandedChildren().computeDepth(), tree1.computeTreeDepth());
    }

    @Test
    public void invisibleNodesDoNotCountTowardsDepth() {
        TreeNode<String> root = new TreeNode<>("root", true);
        // Collapsed root or no children => depth = 1
        Tree<String> tree1 = new Tree<>(root, 20, 5);
        assertEquals(1, tree1.computeTreeDepth());

        TreeNode<String> c1 = root.addChild("c1", true);
        c1.addChild("c1-1", true);
        c1.setVisible(false);

        // c1 is not visible, so depth is still 1
        assertTrue(root.isExpanded());
        assertEquals(1, root.getLastExpandedChildren().computeDepth());
    }

    @Test
    public void navigationWithArrowKeys() {
        TreeNode<String> root = new TreeNode<>("root", true);
        TreeNode<String> c1 = root.addChild("c1", true);
        TreeNode<String> c1a = c1.addChild("c1a", true);
        c1.addChild("c1b", true);
        TreeNode<String> c2 = root.addChild("c2", true);

        Tree<String> tree = new Tree<>(root, 20, 10);

        // No-op consumer to avoid NPE on activation strokes
        tree.setNodeSelectedConsumer(n -> {});

        assertSame(root, tree.getSelectedNode());
        // Down -> c1
        tree.handleKeyStroke(new KeyStroke(KeyType.ARROW_DOWN));
        assertSame(c1, tree.getSelectedNode());
        // Down -> c1a (because c1 is expanded)
        tree.handleKeyStroke(new KeyStroke(KeyType.ARROW_DOWN));
        assertSame(c1a, tree.getSelectedNode());
        // Up -> c1
        tree.handleKeyStroke(new KeyStroke(KeyType.ARROW_UP));
        assertSame(c1, tree.getSelectedNode());

        // Home selects first visible node depending on displayRoot (default true)
        tree.handleKeyStroke(new KeyStroke(KeyType.HOME));
        assertSame(root, tree.getSelectedNode());

        // End selects last visible node
        tree.handleKeyStroke(new KeyStroke(KeyType.END));
        assertSame(c2, tree.getSelectedNode());
    }

    @Test
    public void pageUpDownMoveByWindowAndPinScrollingNode() {
        TreeNode<String> root = new TreeNode<>("root", true);
        TreeNode<String> p = root;
        // Create a deep chain of nodes to scroll through
        for (int i = 0; i < 20; i++) {
            p = p.addChild("n" + i, true);
        }
        Tree<String> tree = new Tree<>(root, 20, 5);
        tree.setNodeSelectedConsumer(n -> {});

        // Page down should advance up to 5 steps
        tree.handleKeyStroke(new KeyStroke(KeyType.PAGE_DOWN));
        // We started at root, after 5 downs we should be at the 5th visible node from start
        TreeNode<String> expected = root.getNodeAtDepth(5);
        assertSame(expected, tree.getSelectedNode());

        // Page up goes back up
        tree.handleKeyStroke(new KeyStroke(KeyType.PAGE_UP));
        assertSame(root, tree.getSelectedNode());
    }

    @Test
    public void overflowCircleWrapsAround() {
        TreeNode<String> root = new TreeNode<>("root", true);
        TreeNode<String> c1 = root.addChild("c1", true);
        TreeNode<String> c2 = c1.addChild("c2", true);
        Tree<String> tree = new Tree<>(root, 20, 5);
        tree.setNodeSelectedConsumer(n -> {});

        // Move to last
        tree.handleKeyStroke(new KeyStroke(KeyType.END));
        assertSame(c2, tree.getSelectedNode());

        // Without overflow, pressing down does not move
        tree.handleKeyStroke(new KeyStroke(KeyType.ARROW_DOWN));
        assertSame(c2, tree.getSelectedNode());

        // Enable overflow and press down -> wraps to first
        tree.setOverflowCircle(true);
        tree.handleKeyStroke(new KeyStroke(KeyType.ARROW_DOWN));
        assertSame(root, tree.getSelectedNode());

        // From first, pressing up wraps to last
        tree.handleKeyStroke(new KeyStroke(KeyType.ARROW_UP));
        assertSame(c2, tree.getSelectedNode());
    }

    @Test
    public void mouseScrollMovesSelection() {
        TreeNode<String> root = new TreeNode<>("root", true);
        TreeNode<String> c1 = root.addChild("c1", true);
        c1.addChild("c2", true);
        Tree<String> tree = new Tree<>(root, 20, 5);
        tree.setNodeSelectedConsumer(n -> {});

        assertSame(root, tree.getSelectedNode());
        // Scroll down behaves as arrow down
        MouseAction scrollDown = new MouseAction(MouseActionType.SCROLL_DOWN, 0, new TerminalPosition(0, 0));
        tree.handleKeyStroke(scrollDown);
        assertSame(c1, tree.getSelectedNode());

        // Scroll up behaves as arrow up
        MouseAction scrollUp = new MouseAction(MouseActionType.SCROLL_UP, 0, new TerminalPosition(0, 0));
        tree.handleKeyStroke(scrollUp);
        assertSame(root, tree.getSelectedNode());
    }

    @Test
    public void rendererPreferredSizeAndCursorLocation() {
        TreeNode<String> root = new TreeNode<>("root", true);
        TreeNode<String> c1 = root.addChild("c1", true);
        c1.addChild("c1a", true);
        Tree<String> tree = new Tree<>(root, 20, 5);

        Tree.DefaultTreeRenderer<String> renderer = new Tree.DefaultTreeRenderer<>();

        TerminalSize preferred = renderer.getPreferredSize(tree);
        // Height follows current implementation: if root is expanded, use root.getExpandedLength(), else 1
        int expectedHeight = root.isExpanded() ? root.getExpandedLength() : 1;
        assertEquals(expectedHeight, preferred.getRows());

        // Cursor should be at row equal to the distance from scrollingNode to selectedNode
        assertEquals(0, renderer.getCursorLocation(tree).getRow());

        // Move selection down and verify cursor row changes
        tree.setNodeSelectedConsumer(n -> {});
        tree.handleKeyStroke(new KeyStroke(KeyType.ARROW_DOWN));
        // Scrolling node remains at the root, so selected at depth 1
        assertEquals(1, renderer.getCursorLocation(tree).getRow());
        tree.handleKeyStroke(new KeyStroke(KeyType.ARROW_DOWN));
        assertEquals(2, renderer.getCursorLocation(tree).getRow());
    }
}
