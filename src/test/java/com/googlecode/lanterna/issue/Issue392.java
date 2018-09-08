package com.googlecode.lanterna.issue;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.TextGUIThread;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import java.io.IOException;

public class Issue392 {
    private static DefaultTerminalFactory terminalFactory;
    private static Terminal terminal;
    private static TerminalScreen screen;
    private static MultiWindowTextGUI textGUI;
    private static BasicWindow window;

    public static void main(String[] args) throws IOException {
        terminalFactory = new DefaultTerminalFactory();
        terminal = terminalFactory.createTerminal();
        screen = new TerminalScreen(terminal);
        screen.startScreen();
        textGUI = new MultiWindowTextGUI(screen);
        setExceptionHandler();
        window = new BasicWindow();

        Button button = new Button("test");
        button.addListener(new Button.Listener() {
            @Override
            public void onTriggered(Button b) {
                setExceptionHandler();
                throw new RuntimeException("This should be caught in the uncaght exception handler!");
            }
        });
        window.setComponent(button);

        textGUI.addWindowAndWait(window);
        screen.stopScreen();
    }

    private static void setExceptionHandler() {
        textGUI.getGUIThread().setExceptionHandler(new TextGUIThread.ExceptionHandler() {

            private boolean handleException(Exception e) {
                System.err.println("### Caught!");
                e.printStackTrace();
                return false;
            }

            @Override
            public boolean onIOException(IOException e) {
                return handleException(e);
            }

            @Override
            public boolean onRuntimeException(RuntimeException e) {
                return handleException(e);
            }
        });
    }
}
