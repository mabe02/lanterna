package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TestUtils;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Test/example class for various kinds of window manager behaviours
 * @author Martin
 */
public class SimpleWindowManagerTest extends TestBase {
    public static void main(String[] args) throws IOException, InterruptedException {
        new SimpleWindowManagerTest().run(args);
    }

    @Override
    public void init(final WindowBasedTextGUI textGUI) {
        final Window mainWindow = new BasicWindow("Choose test");
        Panel contentArea = new Panel();
        contentArea.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        contentArea.addComponent(new Button("Centered window", new Runnable() {
            @Override
            public void run() {
                textGUI.addWindow(new CenteredWindow());
            }
        }));
        contentArea.addComponent(new Button("Undecorated window", new Runnable() {
            @Override
            public void run() {
                textGUI.addWindow(new UndecoratedWindow());
            }
        }));
        contentArea.addComponent(new Button("Undecorated + Centered window", new Runnable() {
            @Override
            public void run() {
                textGUI.addWindow(new UndecoratedCenteredWindow());
            }
        }));
        contentArea.addComponent(new Button("Full-screen window", new Runnable() {
            @Override
            public void run() {
                textGUI.addWindow(new FullScreenWindow(true));
            }
        }));
        contentArea.addComponent(new Button("Undecorated + Full-screen window", new Runnable() {
            @Override
            public void run() {
                textGUI.addWindow(new FullScreenWindow(false));
            }
        }));
        contentArea.addComponent(new Button("Close", new Runnable() {
            @Override
            public void run() {
                mainWindow.close();
            }
        }));
        mainWindow.setComponent(contentArea);
        textGUI.addWindow(mainWindow);
    }

    private static class CenteredWindow extends TestWindow {
        CenteredWindow() {
            super("Centered window");
        }

        @Override
        public Set<Hint> getHints() {
            return new HashSet<Hint>(Collections.singletonList(Hint.CENTERED));
        }
    }

    private static class UndecoratedWindow extends TestWindow {
        UndecoratedWindow() {
            super("Undecorated");
        }

        @Override
        public Set<Hint> getHints() {
            return new HashSet<Hint>(Collections.singletonList(Hint.NO_DECORATIONS));
        }
    }

    private class UndecoratedCenteredWindow extends TestWindow {

        UndecoratedCenteredWindow() {
            super("UndecoratedCentered");
        }

        @Override
        public Set<Hint> getHints() {
            return new HashSet<Hint>(Arrays.asList(Hint.NO_DECORATIONS, Hint.CENTERED));
        }
    }

    private class FullScreenWindow extends TestWindow {
        private final boolean decorations;

        public FullScreenWindow(boolean decorations) {
            super("FullScreenWindow");
            this.decorations = decorations;

            Panel content = new Panel();
            content.setLayoutManager(new BorderLayout());
            TextBox textBox = new TextBox(TestUtils.downloadGPL(), TextBox.Style.MULTI_LINE);
            textBox.setLayoutData(BorderLayout.Location.CENTER);
            textBox.setReadOnly(true);
            content.addComponent(textBox);

            setComponent(content);
        }

        @Override
        public Set<Hint> getHints() {
            return new HashSet<Hint>(decorations ? Arrays.asList(Hint.FULL_SCREEN) : Arrays.asList(Hint.FULL_SCREEN, Hint.NO_DECORATIONS));
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
