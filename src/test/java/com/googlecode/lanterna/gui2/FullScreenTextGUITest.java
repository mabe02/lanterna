package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;

/**
 * Created by martin on 27/10/14.
 */
public class FullScreenTextGUITest {
    public static void main(String[] args) throws IOException, InterruptedException {
        Screen screen = new TestTerminalFactory(args).createScreen();
        screen.startScreen();
        FullScreenTextGUI textGUI = new FullScreenTextGUI(screen);
        try {
            textGUI.setComponent(new BIOS());
            TextGUIThread guiThread = textGUI.getGUIThread();
            guiThread.start();
            guiThread.waitForStop();
        }
        finally {
            screen.stopScreen();
        }
    }

    private static class BIOS extends AbstractInteractableComponent {
        @Override
        protected ComponentRenderer createDefaultRenderer() {
            return new InteractableRenderer() {
                @Override
                public TerminalSize getPreferredSize(Component component) {
                    return new TerminalSize(80, 24);
                }

                @Override
                public void drawComponent(TextGUIGraphics graphics, Component component) {
                    TerminalSize size = graphics.getSize();
                    graphics.setForegroundColor(TextColor.ANSI.WHITE);
                    graphics.setBackgroundColor(TextColor.ANSI.BLUE);
                    graphics.fill(' ');
                    
                    graphics.enableModifiers(SGR.BOLD);

                    graphics.putString(7, 0, "Reminds you of some BIOS, doesn't it?");
                    graphics.setCharacter(0, 1, Symbols.DOUBLE_LINE_TOP_LEFT_CORNER);
                    graphics.drawLine(1, 1, 78, 1, Symbols.DOUBLE_LINE_HORIZONTAL);
                    graphics.setCharacter(79, 1, Symbols.DOUBLE_LINE_TOP_RIGHT_CORNER);
                    graphics.drawLine(79, 2, 79, 22, Symbols.DOUBLE_LINE_VERTICAL);
                    graphics.setCharacter(79, 23, Symbols.DOUBLE_LINE_BOTTOM_RIGHT_CORNER);
                    graphics.drawLine(1, 23, 78, 23, Symbols.DOUBLE_LINE_HORIZONTAL);
                    graphics.setCharacter(0, 23, Symbols.DOUBLE_LINE_BOTTOM_LEFT_CORNER);
                    graphics.drawLine(0, 2, 0, 22, Symbols.DOUBLE_LINE_VERTICAL);
                    
                    graphics.setCharacter(0, 17, Symbols.DOUBLE_LINE_T_SINGLE_RIGHT);
                    graphics.drawLine(1, 17, 78, 17, Symbols.SINGLE_LINE_HORIZONTAL);
                    graphics.setCharacter(79, 17, Symbols.DOUBLE_LINE_T_SINGLE_LEFT);
                    graphics.setCharacter(40, 17, Symbols.SINGLE_LINE_T_UP);
                    graphics.drawLine(40, 2, 40, 16, Symbols.SINGLE_LINE_VERTICAL);
                    graphics.setCharacter(40, 1, Symbols.DOUBLE_LINE_T_SINGLE_DOWN);
                    
                    graphics.setCharacter(0, 20, Symbols.DOUBLE_LINE_T_SINGLE_RIGHT);
                    graphics.drawLine(1, 20, 78, 20, Symbols.SINGLE_LINE_HORIZONTAL);
                    graphics.setCharacter(79, 20, Symbols.DOUBLE_LINE_T_SINGLE_LEFT);
                }

                @Override
                public TerminalPosition getCursorLocation(Component component) {
                    return null;
                }
            };
        }


    }
}
