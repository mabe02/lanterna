package com.googlecode.lanterna.test.issue;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.listener.WindowAdapter;
import com.googlecode.lanterna.input.Key;

/**
 * Created by martin on 21/09/14.
 */
public class Issue116 {
    public static void main(String[] args) {
        GUIScreen textGUI = TerminalFacade.createGUIScreen();
        if (textGUI == null) {
            System.err.println("Couldn't allocate a terminal!");
            return;
        }
        textGUI.getScreen().startScreen();
        Window window = new Window("Issue116");
        window.addComponent(new Label("Label 1"));
        window.addComponent(new Label("Label 2"));
        window.addComponent(new Label("Label 3"));
        window.addComponent(new Label("Label 4"));
        window.addComponent(new Label("Label 5"));
        window.addComponent(new Label("Label 6"));
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void onUnhandledKeyboardInteraction(Window window, Key key) {
                if(key.getKind() == Key.Kind.Escape) {
                    window.close();
                }
            }
        });

        textGUI.showWindow(window, GUIScreen.Position.FULL_SCREEN);
        textGUI.getScreen().stopScreen();
    }
}
