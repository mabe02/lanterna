package com.googlecode.lanterna.gui2;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Test/example class for various kinds of window manager behaviours
 * @author Martin
 */
public class StackedModelWindowManagerTest extends TestBase {
    public static void main(String[] args) throws IOException, InterruptedException {
        new StackedModelWindowManagerTest().run(args);
    }

    @Override
    public void init(final WindowBasedTextGUI textGUI) {
        final Window mainWindow = new BasicWindow("Choose test");
        mainWindow.getContentArea().addComponent(new Button("Centered window", new Runnable() {
            @Override
            public void run() {
                textGUI.getWindowManager().addWindow(new CenteredWindow());
            }
        }));
        mainWindow.getContentArea().addComponent(new Button("Undecorated window", new Runnable() {
            @Override
            public void run() {
                textGUI.getWindowManager().addWindow(new UndecoratedWindow());
            }
        }));
        mainWindow.getContentArea().addComponent(new Button("Close", new Runnable() {
            @Override
            public void run() {
                mainWindow.close();
            }
        }));
        textGUI.getWindowManager().addWindow(mainWindow
        );
    }

    private static class CenteredWindow extends TestWindow {
        CenteredWindow() {
            super("Centered window");
        }

        @Override
        public Set<WindowManager.Hint> getWindowManagerHints() {
            return new HashSet<WindowManager.Hint>(Arrays.asList(StackedModalWindowManager.LOCATION_CENTERED));
        }
    }

    private static class UndecoratedWindow extends TestWindow {
        UndecoratedWindow() {
            super("Undecorated");
        }

        @Override
        public Set<WindowManager.Hint> getWindowManagerHints() {
            return new HashSet<WindowManager.Hint>(Arrays.asList(StackedModalWindowManager.NO_WINDOW_DECORATIONS));
        }
    }

    private static class TestWindow extends BasicWindow {
        TestWindow(String title) {
            super(title);
            getContentArea().addComponent(new Button("Close", new Runnable() {
                @Override
                public void run() {
                    close();
                }
            }));
        }
    }
}
