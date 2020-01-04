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
    private static MultiWindowTextGUI textGUI;

    public static void main(String[] args) throws IOException {
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Terminal terminal = terminalFactory.createTerminal();
        TerminalScreen screen = new TerminalScreen(terminal);
        screen.startScreen();
        textGUI = new MultiWindowTextGUI(screen);
        setExceptionHandler();
        BasicWindow window = new BasicWindow();

        Button button = new Button("test");
        button.addListener(b -> {
            setExceptionHandler();
            throw new RuntimeException("This should be caught in the uncaght exception handler!");
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
