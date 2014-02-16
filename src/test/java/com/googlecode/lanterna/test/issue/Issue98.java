package com.googlecode.lanterna.test.issue;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.Border;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.component.Panel;

public class Issue98 {

    public static void main(String[] args) {

        GUIScreen textGUI = TerminalFacade.createGUIScreen();

        if (textGUI == null) {
            System.err.println("Couldn't allocate a terminal!");
            return;
        }

        textGUI.getScreen().startScreen();

        textGUI.showWindow(new MainWindow(), GUIScreen.Position.FULL_SCREEN);
    }

    public static class MainWindow extends Window {

        public MainWindow() {

            super("Main Window");

            Panel panel = new Panel(new Border.Bevel(true), Panel.Orientation.VERTICAL);

            addComponent(panel);

            panel.addComponent(new Button("Exit (inside panel)", new Action() {
                public void doAction() {
                    getOwner().getScreen().stopScreen();
                    System.exit(0);
                }
            }));

            
            /*
            //If lines below are uncommented, both buttons will be working
            addComponent(new Button("Exit (outside panel)", new Action() {
                public void doAction() {
                    getOwner().getScreen().stopScreen();
                    System.exit(0);
                }
            }));
            */
        }
    }
}
