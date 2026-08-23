package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;

public class WindowManagerTest extends TestBase {
    public static void main(String[] args) throws IOException, InterruptedException {
        new WindowManagerTest().run(args);
    }

    @Override
    protected MultiWindowTextGUI createTextGUI(Screen screen) {
        return new MultiWindowTextGUI(new SeparateTextGUIThread.Factory(), screen, new CustomWindowManager());
    }

    @Override
    public void init(WindowBasedTextGUI textGUI) {
        final Window mainWindow = new BasicWindow("Window Manager Test");
        Panel contentArea = new Panel();
        contentArea.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        contentArea.addComponent(new EmptySpace(TerminalSize.ONE));
        contentArea.addComponent(new Button("Close", new Runnable() {
            @Override
            public void run() {
                mainWindow.close();
            }
        }));
        mainWindow.setComponent(contentArea);
        textGUI.addWindow(mainWindow);
    }

    private static class CustomWindowManager extends DefaultWindowManager {
        @Override
        protected void prepareWindow(TerminalSize screenSize, Window window) {
            super.prepareWindow(screenSize, window);

            window.setDecoratedSize(window.getPreferredSize().withRelative(12, 10));
            window.setPosition(new TerminalPosition(
                    screenSize.getColumns() - window.getDecoratedSize().getColumns() - 1,
                    screenSize.getRows() - window.getDecoratedSize().getRows() - 1
            ));
        }
    }
}
