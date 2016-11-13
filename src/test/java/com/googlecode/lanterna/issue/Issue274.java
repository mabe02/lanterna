/*
 * Author Rajatt, modified by Andreas
 */
package com.googlecode.lanterna.issue;

import java.io.IOException;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.MouseCaptureMode;
import com.googlecode.lanterna.terminal.Terminal;

public class Issue274 {

    public static void main(String[] args) throws IOException, InterruptedException {

        final Terminal ter = new DefaultTerminalFactory()
                .setForceTextTerminal(true)
                .setMouseCaptureMode(MouseCaptureMode.CLICK)
                .setTelnetPort(1024)
                .createTerminal();

        final Screen screen = new TerminalScreen(ter);
        screen.startScreen();
        final MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);

        Panel menubar = new Panel();
        menubar.setLayoutManager(new LinearLayout(Direction.HORIZONTAL).setSpacing(1));

        TextBox text = new TextBox(new TerminalSize(10,10),TextBox.Style.MULTI_LINE);
        menubar.addComponent(text);

        menubar.addComponent(new Button("Open", new Runnable(){
            @Override
            public void run() {
                final Window op = new BasicWindow("Select file");
                gui.addWindow(op);
                op.setComponent(new Button("Close", new Runnable(){
                    @Override
                    public void run() {
                        op.close();
                    }
                }));
            }
        }));

        menubar.addComponent(new Button("Save"));

        menubar.addComponent(new Button("Exit", new Runnable(){
            public void run(){
                gui.getActiveWindow().close();
            }
        }));

        Window main = new BasicWindow("Test");
        main.setComponent(menubar);
        try {
            gui.addWindowAndWait(main);
        }
        finally {
            screen.stopScreen();
        }
    }
}
