package com.googlecode.lanterna.test.issue;

import com.googlecode.lanterna.screen.DefaultScreen;
import com.googlecode.lanterna.screen.ScreenWriter;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.test.TestTerminalFactory;
import java.io.IOException;

class IssueX {

    public static void main(String[] args) throws InterruptedException, IOException {
        LanternaTerminalWriter writer = new LanternaTerminalWriter(args);
        for (int i = 0; i < 1000; i++) {
            writer.write(String.valueOf(i), Terminal.SGR.BOLD);
            Thread.sleep(100);
        }
        writer.close();
    }

    public static class LanternaTerminalWriter {

        private DefaultScreen screen;
        private ScreenWriter screenWriter;

        private int current_y = 1;
        private int default_x = 3;

        @SuppressWarnings("unused")
		private boolean conversionFinished = false;

        public LanternaTerminalWriter(String[] args) throws IOException {
            screen = new TestTerminalFactory(args).createScreen();
            screen.startScreen();

            screenWriter = new ScreenWriter(screen);
        }

        public void close() throws IOException {
            screen.stopScreen();
            conversionFinished = true;
        }

        public void write(String string, Terminal.SGR... styles) throws IOException {
            screenWriter.setPosition(new TerminalPosition(default_x, current_y));
            screenWriter.enableModifiers(styles);
            screenWriter.putString(string);
            screen.readInput();
            screen.refresh();
        }

    }
}

