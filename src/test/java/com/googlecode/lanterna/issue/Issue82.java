/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.lanterna.issue;

import com.googlecode.lanterna.TestTerminalFactory;
import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.dialog.WaitingDialog;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author martin
 */
public class Issue82 {

    public static void main(String[] args) throws InterruptedException, IOException {
        // Construct the screen
        final GUIScreen guiScreen = new TestTerminalFactory(args).createGUIScreen();
        guiScreen.getScreen().startScreen();

        final Window main = new Window("Main");
        final WaitingDialog waitingDialog = new WaitingDialog("Loading...", "Standby");

        // Spawn the main window in a own thread. Otherwise our Controller blocks.
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    guiScreen.showWindow(main, GUIScreen.Position.FULL_SCREEN);
                }
                catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // Show the waiting popup. For this we can use the event thread (i thought)
        guiScreen.runInEventThread(new Action() {
            @Override
            public void doAction() {
                try {
                    guiScreen.showWindow(waitingDialog);
                }
                catch(IOException ex) {
                    ex.printStackTrace();
                }
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
