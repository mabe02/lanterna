/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.lanterna.test.issue;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.dialog.WaitingDialog;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author martin
 */
public class Issue82 {

    public static void main(String[] args) throws InterruptedException {
        // Construct the screen
        final GUIScreen guiScreen = TerminalFacade.createGUIScreen();
        guiScreen.getScreen().startScreen();

        final Window main = new Window("Main");
        final WaitingDialog waitingDialog = new WaitingDialog("Loading...", "Standby");

        // Spawn the main window in a own thread. Otherwise our Controller blocks.
        new Thread(new Runnable() {
            @Override
            public void run() {
                guiScreen.showWindow(main, GUIScreen.Position.FULL_SCREEN);
            }
        }).start();

        // Show the waiting popup. For this we can use the event thread (i thought)
        guiScreen.runInEventThread(new Action() {
            @Override
            public void doAction() {
                guiScreen.showWindow(waitingDialog);
            }
        });

        // heavy calculation :)
        TimeUnit.SECONDS.sleep(2);

        // loading is done. close the popup now.
        System.out.println("Close loading popup");

        waitingDialog.close();
        guiScreen.invalidate();

        // this code will never reached.
    }
}
