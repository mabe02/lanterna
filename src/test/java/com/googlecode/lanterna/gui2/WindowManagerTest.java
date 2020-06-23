package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;
import java.util.*;

public class WindowManagerTest extends TestBase {
    public static void main(String[] args) throws IOException, InterruptedException {
        new WindowManagerTest().run(args);
    }

    @Override
    protected MultiWindowTextGUI createTextGUI(Screen screen) {
        return new MultiWindowTextGUI(new SeparateTextGUIThread.Factory(), screen, new CustomWindowManager());
    }

    Set<String> HACK_what_are_these_orderings = new HashSet<>();

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

    private class CustomWindowManager extends DefaultWindowManager {
        // not sure how to determine padding decoration
        final int PAD = 8;
        int xOffset;
        @Override
        public void prepareWindows(WindowBasedTextGUI textGUI, List<Window> allWindows, TerminalSize screenSize) {
            trackTheOrderings(allWindows);
            xOffset = 0;
            super.prepareWindows(textGUI, allWindows, screenSize);
        }
    
        @Override
        protected void prepareWindow(TerminalSize screenSize, Window window) {
            super.prepareWindow(screenSize, window);

            window.setDecoratedSize(window.getPreferredSize().withRelative(12, 10));
            
            window.setPosition(new TerminalPosition(
                    screenSize.getColumns() - window.getDecoratedSize().getColumns() - 1 - xOffset - (2*PAD),
                    screenSize.getRows() - window.getDecoratedSize().getRows() - 1
            ));
            
            xOffset += window.getDecoratedSize().getColumns();
        }
    }
    
    
    
    void trackTheOrderings(List<Window> windows) {
        String token = "";
        for (Window w : windows) {
            final int systemHashCode = System.identityHashCode(w);
            token += systemHashCode + ", ";
        }
        token = "[" + token.substring(0, token.length() -2) + "]";
        HACK_what_are_these_orderings.add(token);
        
        if (HACK_what_are_these_orderings.size() > 1) {
            // put debugger break point here
            int hmmm_not_sure_what_is_going_on = 9876;
        }
        
    }
    
}
