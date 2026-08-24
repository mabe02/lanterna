package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.Window.Hint;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.virtual.DefaultVirtualTerminal;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Regression tests verifying that the terminal caret is hidden whenever the focused {@link Interactable} has been
 * scrolled out of a {@link ScrollViewer}'s viewport, instead of continuing to render at its last (now clipped)
 * on-screen position.
 */
public class ScrollViewerCursorVisibilityTest {

    private DefaultVirtualTerminal terminal;
    private MultiWindowTextGUI gui;

    @Before
    public void setUp() throws IOException {
        TerminalSize size = new TerminalSize(24, 10);
        terminal = new DefaultVirtualTerminal(size);
        TerminalScreen screen = new TerminalScreen(terminal);
        screen.startScreen();
        DefaultWindowManager windowManager = new DefaultWindowManager(new EmptyWindowDecorationRenderer(), size);
        // SameTextGUIThread is used (rather than SeparateTextGUIThread) because ScrollViewer schedules some of its
        // internal layout bookkeeping via TextGUIThread.invokeLater(), and SeparateTextGUIThread refuses to accept
        // any such work until its background thread has actually been started -- which these tests never do, since
        // they drive drawing synchronously via gui.updateScreen().
        gui = new MultiWindowTextGUI(new SameTextGUIThread.Factory(), screen, windowManager, null, new EmptySpace());
    }

    /**
     * Builds an undecorated window pinned to the top-left corner with a fixed size, so that its content (the
     * {@code ScrollViewer}) is laid out at exactly {@code size} regardless of its own preferred size -- rather than
     * being stretched to fill the whole terminal, which is what a full-screen window would otherwise do.
     */
    private BasicWindow createPinnedWindow(TerminalSize size, Component content) {
        BasicWindow window = new BasicWindow();
        window.setHints(Arrays.asList(Hint.NO_DECORATIONS, Hint.FIXED_POSITION));
        window.setPosition(TerminalPosition.TOP_LEFT_CORNER);
        window.setFixedSize(size);
        window.setComponent(content);
        return window;
    }

    @Test
    public void caretHiddenWhileFocusedComponentScrolledOutOfViewAndRestoredWhenScrolledBackIntoView() throws IOException {
        ScrollViewer sv = new ScrollViewer();

        Panel content = new Panel(new LinearLayout(Direction.VERTICAL).setSpacing(0));
        TextBox[] boxes = new TextBox[5];
        for (int i = 0; i < boxes.length; i++) {
            boxes[i] = new TextBox();
            content.addComponent(boxes[i]);
        }
        sv.setComponent(content);

        BasicWindow window = createPinnedWindow(new TerminalSize(14, 3), sv);
        gui.addWindow(window);

        // Initial layout: the first (topmost) TextBox should have taken focus automatically and be in view.
        gui.updateScreen();
        assertEquals(boxes[0], window.getFocusedInteractable());
        assertTrue("Caret should be visible while the focused component is in view", terminal.isCursorVisible());
        assertEquals(TerminalPosition.TOP_LEFT_CORNER, terminal.getCursorPosition());

        // Scroll the content down without moving focus, so the focused TextBox is pushed out of the viewport --
        // this simulates e.g. dragging the scrollbar or another component controlling the scroll position.
        ScrollController controller = sv.getScrollController();
        assertNotNull(controller);
        controller.setVerticalOffset(ScrollController.INFINITY);

        gui.updateScreen();
        assertEquals(boxes[0], window.getFocusedInteractable());
        assertFalse("Caret must not render once the focused component has scrolled out of view", terminal.isCursorVisible());

        // Scroll back to the top; the caret should reappear now that the component is visible again.
        controller.setVerticalOffset(0);
        gui.updateScreen();
        assertTrue("Caret should reappear once the focused component is scrolled back into view", terminal.isCursorVisible());
        assertEquals(TerminalPosition.TOP_LEFT_CORNER, terminal.getCursorPosition());
    }

    @Test
    public void caretVisibilityAccountsForBorderPaddingOfFocusedComponent() throws IOException {
        ScrollViewer sv = new ScrollViewer();

        Panel content = new Panel(new LinearLayout(Direction.VERTICAL).setSpacing(0));
        content.addComponent(new TextBox());
        content.addComponent(new TextBox());
        content.addComponent(new TextBox());

        TextBox borderedBox = new TextBox();
        content.addComponent(borderedBox.withBorder(Borders.singleLine()));
        sv.setComponent(content);

        BasicWindow window = createPinnedWindow(new TerminalSize(20, 4), sv);
        gui.addWindow(window);
        gui.updateScreen();

        window.setFocusedInteractable(borderedBox);
        gui.updateScreen();

        // Layout (rows, unscrolled): box0=0, box1=1, box2=2, border-top=3, borderedBox=4, border-bottom=5.
        // With a 4-row viewport starting at offset 0, only row 3 (the border's top edge) is visible -- the
        // bordered TextBox itself, one row further down due to the border's padding, is still clipped.
        assertFalse(
            "Caret must stay hidden when only the focused component's border (not the component itself) is visible",
            terminal.isCursorVisible());

        // Scrolling down by one row brings the actual TextBox (not just its border) into view.
        sv.getScrollController().setVerticalOffset(1);
        gui.updateScreen();
        assertTrue(
            "Caret should become visible once the bordered component itself scrolls into view",
            terminal.isCursorVisible());
    }
}
