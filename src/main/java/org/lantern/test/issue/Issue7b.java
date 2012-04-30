/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lantern.test.issue;

import org.lantern.LanternException;
import org.lantern.LanternTerminal;
import org.lantern.gui.*;

/**
 * http://code.google.com/p/lanterna/issues/detail?id=7
 * 
 * Verifying that a panel with standard border renders all items correctly
 * 
 * @author Martin
 */
public class Issue7b {
    public static void main(String[] args) throws LanternException {
        final LanternTerminal terminal = new LanternTerminal();
        terminal.start();
        final GUIScreen textGUI = terminal.getGUIScreen();

        textGUI.setTitle("GUI Test");
        final Window mainWindow = new Window("Testing issue 7");
        mainWindow.addComponent(new EmptySpace(16, 1));
        Panel panel = new Panel("Panel");
	panel.setBorder(new Border.Standard());
	panel.addComponent(new Label("Label 1"));
	panel.addComponent(new Label("Label 2"));
	panel.addComponent(new Label("Label 3"));
        mainWindow.addComponent(panel);
        mainWindow.addComponent(new EmptySpace(16, 1));
        mainWindow.addComponent(new Button("Close", new Action() {
            public void doAction()
            {
                textGUI.closeWindow(mainWindow);
            }
        }));

        textGUI.showWindow(mainWindow, GUIScreen.Position.CENTER);
        terminal.stopAndRestoreTerminal();
    }
}
