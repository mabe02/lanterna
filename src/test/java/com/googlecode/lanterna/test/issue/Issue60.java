package com.googlecode.lanterna.test.issue;

import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.TextGraphics;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.AbstractInteractableComponent;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.test.TestTerminalFactory;

public class Issue60 extends Window {

    public Issue60(String title) {
        super(title);
        addComponent(new AbstractInteractableComponent() {
            @Override
            public void repaint(TextGraphics graphics) {
                // TODO Auto-generated method stub
            }

            @Override
            public Result keyboardInteraction(Key key) {
                System.out.println("Char value:" + (key.getCharacter() + 0));
                System.out.println("Key kind:" + key.getKind());
                
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
    public static void main(String[] args) {
        final GUIScreen guiScreen = new TestTerminalFactory(args).createGUIScreen();
        guiScreen.getScreen().startScreen();
        guiScreen.showWindow(new Issue60("Issue 60 - Press q to quit"));
        guiScreen.getScreen().stopScreen();
    }
}
