package com.googlecode.lanterna.gui2;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Test/example class for various kinds of window manager behaviours
 * @author Martin
 */
public class StackedModalWindowManagerTest extends TestBase {
    public static void main(String[] args) throws IOException, InterruptedException {
        new StackedModalWindowManagerTest().run(args);
    }

    @Override
    public void init(final WindowBasedTextGUI textGUI) {
        final Window mainWindow = new BasicWindow("Choose test");
        Panel contentArea = new Panel();
        contentArea.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        contentArea.addComponent(new Button("Centered window", new Runnable() {
            @Override
            public void run() {
                textGUI.getWindowManager().addWindow(new CenteredWindow());
            }
        }));
        contentArea.addComponent(new Button("Undecorated window", new Runnable() {
            @Override
            public void run() {
                textGUI.getWindowManager().addWindow(new UndecoratedWindow());
            }
        }));
        contentArea.addComponent(new Button("Close", new Runnable() {
            @Override
            public void run() {
                mainWindow.close();
            }
        }));
        mainWindow.setComponent(contentArea);
        textGUI.getWindowManager().addWindow(mainWindow);
    }

    private static class CenteredWindow extends TestWindow {
        CenteredWindow() {
            super("Centered window");
        }

        @Override
        public Set<Hint> getHints() {
            return new HashSet<Hint>(Arrays.asList(Hint.CENTERED));
        }
    }

    private static class UndecoratedWindow extends TestWindow {
        UndecoratedWindow() {
            super("Undecorated");
        }

        @Override
        public Set<Hint> getHints() {
            return new HashSet<Hint>(Arrays.asList(Hint.NO_DECORATIONS));
        }

        @Override
        public void draw(TextGUIGraphics graphics) {
            super.draw(graphics);
            System.out.println("Undecorated window size is " + getSize() + " and getDecoratedSize() returns " + getDecoratedSize());
        }
    }

    private static class TestWindow extends BasicWindow {
        TestWindow(String title) {
            super(title);
            setComponent(new Button("Close", new Runnable() {
                @Override
                public void run() {
                    close();
                }
            }));
        }
    }
}
