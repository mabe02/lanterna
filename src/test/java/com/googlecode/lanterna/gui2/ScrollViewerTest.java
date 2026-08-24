package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;

import java.io.IOException;
import java.util.Arrays;

public class ScrollViewerTest extends TestBase {
    public static void main(final String[] args) throws IOException, InterruptedException {
        new ScrollViewerTest().run(args);
    }

    @Override
    public void init(final WindowBasedTextGUI textGUI) {
        final BasicWindow selectorWindow = new BasicWindow("Select a Test");

        selectorWindow.setComponent(
            new ActionListBox().addItem(
                new Runnable() {
                    @Override
                    public void run() {
                        testCheckerboard(textGUI);
                    }

                    @Override
                    public String toString() {
                        return "Scrollbar policies";
                    }
                }
            ).addItem(
                new Runnable() {
                    @Override
                    public void run() {
                        testFocusedComponentsScrolledIntoView(textGUI);
                    }

                    @Override
                    public String toString() {
                        return "Focused component scrolled into view";
                    }
                }
            ).addItem(
                new Runnable() {
                    @Override
                    public void run() {
                        selectorWindow.close();
                    }

                    @Override
                    public String toString() {
                        return "Exit";
                    }
                }
            )
        );

        textGUI.addWindow(selectorWindow);
    }

    private void testCheckerboard(final WindowBasedTextGUI textGUI) {
        final BasicWindow basicWindow = new BasicWindow("Scrollbar Policy Test");
        final Panel p = new Panel();

        basicWindow.setCloseWindowWithEscape(true);

        p.setPreferredSize(new TerminalSize(40, 20));

        final ScrollViewer sv = new ScrollViewer();

        sv.setComponent(new DumbComponent());

        p.addComponent(sv);
        basicWindow.setComponent(p);

        textGUI.addWindow(basicWindow);
        basicWindow.setPosition(TerminalPosition.TOP_LEFT_CORNER);
    }

    private void testFocusedComponentsScrolledIntoView(final WindowBasedTextGUI textGUI) {
        final BasicWindow basicWindow = new BasicWindow("Bring Into View Test");
        final Panel p1 = new Panel(new BorderLayout());
        final Panel p2 = new Panel(new GridLayout(10).setHorizontalSpacing(6).setVerticalSpacing(3));
        final ScrollViewer sv = new ScrollViewer();

        basicWindow.setCloseWindowWithEscape(true);

        for (int i = 1; i <= 10; i++) {
            for (int j = 1; j <= 10; j++) {
                p2.addComponent(new Button("(" + i + ", " + j + ")"));
            }
        }

        p1.setPreferredSize(new TerminalSize(20, 10));
        p1.addComponent(sv, BorderLayout.Location.CENTER);
        sv.setComponent(p2);
        basicWindow.setComponent(p1);

        textGUI.addWindow(basicWindow);
    }
    
    private final static class DumbComponent extends AbstractInteractableComponent<DumbComponent> {
        @Override
        protected Result handleKeyStroke(KeyStroke keyStroke) {
            return Result.UNHANDLED;
        }

        @Override
        protected InteractableRenderer<DumbComponent> createDefaultRenderer() {
            return new InteractableRenderer<DumbComponent>() {
                final TextColor BLACK = TextColor.Factory.fromString("#191919");
                final TextColor WHITE = TextColor.ANSI.WHITE;

                @Override
                public TerminalPosition getCursorLocation(final DumbComponent c) {
                    return null;
                }

                @Override
                public TerminalSize getPreferredSize(final DumbComponent c) {
                    return new TerminalSize(80, 40);
                }

                @Override
                public void drawComponent(final TextGUIGraphics g, final DumbComponent c) {
                    final int w = 80;
                    final int h = 40;

                    for (int x = 0; x < w; x += 6) {
                        for (int y = 0; y < h; y ++) {
                            final TextColor bg1 = y % 2 == 0 ? BLACK : WHITE;
                            final TextColor bg2 = y % 2 == 0 ? WHITE : BLACK;

                            g.fillRectangle(
                                new TerminalPosition(x, y),
                                new TerminalSize(3, 2),
                                new TextCharacter(' ', WHITE, bg1)
                            );
                            g.fillRectangle(
                                new TerminalPosition(x + 3, y),
                                new TerminalSize(3, 2),
                                new TextCharacter(' ', WHITE, bg2)
                            );
                        }
                    }
                }
            };
        }
    }
}
