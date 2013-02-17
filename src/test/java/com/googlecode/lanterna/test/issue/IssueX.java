package com.googlecode.lanterna.test.issue;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.ScreenCharacterStyle;
import com.googlecode.lanterna.screen.ScreenWriter;
import com.googlecode.lanterna.test.TestTerminalFactory;

class IssueX {

    public static void main(String[] args) throws InterruptedException {
        LanternaTerminalWriter writer = new LanternaTerminalWriter(args);
        for (int i = 0; i < 1000; i++) {
            writer.write(String.valueOf(i), ScreenCharacterStyle.Bold);
            Thread.sleep(100);
        }
        writer.close();
    }

    public static class LanternaTerminalWriter {

        private Screen screen;
        private ScreenWriter screenWriter;

        private int current_y = 1;
        private int default_x = 3;

        private boolean conversionFinished = false;

        public LanternaTerminalWriter(String[] args) {
            screen = new TestTerminalFactory(args).createScreen();
            screen.startScreen();

            screenWriter = new ScreenWriter(screen);
        }

        public void close() {
            screen.stopScreen();
            conversionFinished = true;
        }

        public void write(String string, ScreenCharacterStyle... styles) {
            screenWriter.drawString(default_x, current_y, string, styles);
            screen.readInput();
            screen.refresh();
        }

    }
}

