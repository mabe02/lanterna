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
            return new ComponentRenderer() {
                @Override
                public TerminalSize getPreferredSize(Component component) {
                    return null;
                }

                @Override
                public void drawComponent(TextGUIGraphics graphics, Component component) {
                    TerminalSize size = graphics.getSize();
                    graphics.setForegroundColor(TextColor.ANSI.WHITE);
                    graphics.setBackgroundColor(TextColor.ANSI.BLUE);
                    graphics.fill(' ');

                    graphics.putString(7, 0, "Reminds you of some BIOS, doesn't it?");
                    graphics.setCharacter(0, 1, Symbols.DOUBLE_LINE_TOP_LEFT_CORNER);
                    graphics.drawLine(new TerminalPosition(1, 1), new TerminalPosition(size.getColumns() - 2, 1), Symbols.DOUBLE_LINE_HORIZONTAL);
                    graphics.setCharacter(size.getColumns() - 1, 1, Symbols.DOUBLE_LINE_TOP_RIGHT_CORNER);
                    graphics.drawLine(new TerminalPosition(size.getColumns() - 1, 2), new TerminalPosition(size.getColumns() - 1, size.getRows() - 2), Symbols.DOUBLE_LINE_VERTICAL);
                    graphics.setCharacter(size.getColumns() - 1, size.getRows() - 1, Symbols.DOUBLE_LINE_BOTTOM_RIGHT_CORNER);
                    graphics.drawLine(new TerminalPosition(1, size.getRows() - 1), new TerminalPosition(size.getColumns() - 2, size.getRows() - 1), Symbols.DOUBLE_LINE_HORIZONTAL);
                    graphics.setCharacter(0, size.getRows() - 1, Symbols.DOUBLE_LINE_BOTTOM_LEFT_CORNER);
                    graphics.drawLine(new TerminalPosition(0, size.getRows() - 2), new TerminalPosition(0, size.getRows() - 1), Symbols.DOUBLE_LINE_VERTICAL);
                }
            };
        }


    }
}
