package com.googlecode.lanterna.issue;

import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.TextGraphics;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.AbstractInteractableComponent;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TestTerminalFactory;
import java.io.IOException;

public class Issue60 extends Window {

    public Issue60(String title) {
        super(title);
        addComponent(new AbstractInteractableComponent() {
            @Override
            public void repaint(TextGraphics graphics) {
                // TODO Auto-generated method stub
            }

            @Override
            public Result keyboardInteraction(KeyStroke key) {
                System.out.println("Char value:" + (key.getCharacter() + 0));
                System.out.println("Key kind:" + key.getKeyType());

                if(key.getCharacter() == 'q') {
                    close();
                }

                return Result.EVENT_HANDLED;
            }

            @Override
            protected TerminalSize calculatePreferredSize() {
                // TODO Auto-generated method stub
                return new TerminalSize(2, 2);
            }
        });
        addComponent(new com.googlecode.lanterna.gui.component.Button("toto"));
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws IOException {
        final GUIScreen guiScreen = new TestTerminalFactory(args).createGUIScreen();
        guiScreen.getScreen().startScreen();
        guiScreen.showWindow(new Issue60("Issue 60 - Press q to quit"));
        guiScreen.getScreen().stopScreen();
    }
}
