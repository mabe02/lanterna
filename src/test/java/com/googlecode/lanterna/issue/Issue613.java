package com.googlecode.lanterna.issue;

import com.googlecode.lanterna.bundle.LanternaThemes;
import com.googlecode.lanterna.gui2.AsynchronousTextGUIThread;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.SeparateTextGUIThread;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import com.googlecode.lanterna.terminal.swing.AWTTerminalFrame;

import java.io.IOException;
import java.util.Arrays;

public class Issue613 {

    private static final DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
    private static TerminalScreen screen;

    public static void main(String[] args) {
        try {
            final SwingTerminalFrame swingTerminal = terminalFactory.createSwingTerminal();
            screen = new TerminalScreen(swingTerminal);
            // exception would be here
            // calling .startScreen() needed size to already be established before hand
            // fix was to add .pack() in SwingTerminalFrame constructor
            screen.startScreen();

            //Setting up basic GUI
            MultiWindowTextGUI multiWindowTextGUI = new MultiWindowTextGUI(new SeparateTextGUIThread.Factory(), screen);
            ((AsynchronousTextGUIThread) multiWindowTextGUI.getGUIThread()).start();

            //Creating window
            BasicWindow window = new BasicWindow("Simple placeholder window");
            window.setHints(Arrays.asList(Window.Hint.CENTERED, Window.Hint.FIT_TERMINAL_WINDOW, Window.Hint.FULL_SCREEN));

            multiWindowTextGUI.setTheme(LanternaThemes.getRegisteredTheme("default"));

            swingTerminal.setVisible(true);
            multiWindowTextGUI.addWindowAndWait(window);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (screen != null) {
                try {
                    screen.stopScreen();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
