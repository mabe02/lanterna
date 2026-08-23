package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.SimpleTheme;

import java.io.IOException;

public class TreeTest extends TestBase {

    public static void main(String[] args) throws IOException, InterruptedException {

        new TreeTest().run(new String[]{"--mouse-click"});
    }

    @Override
    public void init(final WindowBasedTextGUI textGUI) {
        final BasicWindow window = new BasicWindow("TreeTest");

        BorderLayout layout = new BorderLayout();
        Panel basePanel = new Panel();
        basePanel.setLayoutManager(layout);

        Tree<String> bracketTree = createTestTree();
        bracketTree.setDisplayRoot(false);
        bracketTree.setOverflowCircle(true);
        bracketTree.setNodeSelectedConsumer(node -> System.out.println("Selected leaf node: " + node.getLabel()));

        Panel bracketTreePanel = new Panel();
        bracketTreePanel.addComponent(bracketTree);
        bracketTreePanel.setPreferredSize(new TerminalSize(35, 15));
        basePanel.addComponent(bracketTreePanel, BorderLayout.Location.LEFT);

        Tree<String> noBracketTree = createTestTree();
        SimpleTheme theme = new SimpleTheme(TextColor.ANSI.BLUE, TextColor.ANSI.WHITE);
        theme.getDefaultDefinition().setActive(TextColor.ANSI.RED, TextColor.ANSI.CYAN);
        theme.getDefaultDefinition().setCharacter(Tree.DefaultTreeRenderer.LEAF_MARKER, '*');
        theme.getDefaultDefinition().setCharacter(Tree.DefaultTreeRenderer.DISPLAY_BLOCK_FILLER, '.');
        theme.getDefaultDefinition().setIntegerProperty(Tree.DefaultTreeRenderer.TREE_LEVEL_INDENT, 2);
        theme.getDefaultDefinition().setBooleanProperty(Tree.DefaultTreeRenderer.DISPLAY_BRACKETS, false);
        theme.getDefaultDefinition().setBooleanProperty(Tree.DefaultTreeRenderer.DISPLAY_BLOCK, true);
        noBracketTree.setTheme(theme);
        noBracketTree.setNodeSelectedConsumer(node -> System.out.println("Selected leaf node: " + node.getLabel()));

        Panel noBracketTreePanel = new Panel();
        noBracketTreePanel.addComponent(noBracketTree);
        noBracketTreePanel.setPreferredSize(new TerminalSize(35, 15));

        basePanel.addComponent(bracketTreePanel.withBorder(Borders.singleLine("Bracket Tree")), BorderLayout.Location.LEFT);
        basePanel.addComponent(noBracketTreePanel.withBorder(Borders.singleLine("No bracket in block Tree")), BorderLayout.Location.RIGHT);

        window.setComponent(basePanel);
        textGUI.addWindow(window);
    }

    private static Tree<String> createTestTree() {

        TreeNode<String> root = new TreeNode<>("root", true);
        TreeNode<String> child1 = root.addChild("child_1", false);
        TreeNode<String> child2 = root.addChild("child_2");

        root.addChild("child_3", true);
        root.addChild("child_44", true);

        TreeNode<String> child4 = child1.addChild("child_4", true);
        child4.addChild("child_5", true);
        child4.addChild("child_6", true);
        child4.addChild("child_7", true);

        TreeNode<String> transitiveParent = child4;
        for (int i = 9; i < 20; i++) {
            transitiveParent = transitiveParent.addChild("child_" + i, true);
            if (transitiveParent.getLabel().equals("child_9")) {
                transitiveParent.setVisible(false);
                transitiveParent.setLabel("child_9 (hidden)");
            }
        }

        transitiveParent = child2;
        for (int i = 20; i < 30; i++) {
            transitiveParent = transitiveParent.addChild("child_" + i, true);
        }

        return new Tree<>(root, 35, 15);
    }


}
