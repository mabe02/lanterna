package com.googlecode.lanterna.screen;

import java.io.IOException;

import com.googlecode.lanterna.TestTerminalFactory;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.graphics.TextGraphicsWriter;

public class TextGraphicsWriterTest {
    public static void main(String[] args) throws IOException {
        Screen screen = new TestTerminalFactory(args).createScreen();
        screen.startScreen();

        TextGraphics writer = new ScreenTextGraphics(screen);
        writer.setForegroundColor(TextColor.ANSI.WHITE);
        writer.setBackgroundColor(TextColor.ANSI.BLUE);
        writer.fill(' ');
        TextGraphicsWriter tw = new TextGraphicsWriter(writer);

        String loremIpsum =
                "  Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod" +
                " tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim" +
                " veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid" +
                " ex ea commodi consequat. Quis aute iure reprehenderit in voluptate" +
                " velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint" +
                " obcaecat cupiditat non proident, sunt in culpa qui officia deserunt" +
                " mollit anim id est laborum.\n";
        // change all blanks behind full-stops or commas to underlined tabs:
        loremIpsum = loremIpsum.replaceAll("([.,]) ", "$1\033[4m\t\033[24m");
        // each occurrence of "dolor" gets its own background:
        loremIpsum = loremIpsum.replaceAll("(dolor)", "\033[45m$1\033[49m");
        // each 'o' is turned yellow.
        loremIpsum = loremIpsum.replaceAll("([o])", "\033[1;33m$1\033[22;39m");

        tw.putString("\033[m");
        tw.setWrapBehaviour(WrapBehaviour.SINGLE_LINE);
        writer.setTabBehaviour(TabBehaviour.ALIGN_TO_COLUMN_4);
        tw.putString("\n"+tw.getWrapBehaviour()+":\n");
        tw.putString(loremIpsum);

        tw.setWrapBehaviour(WrapBehaviour.CLIP);
        tw.putString("\n"+tw.getWrapBehaviour()+":\n");
        tw.putString(loremIpsum);

        tw.setWrapBehaviour(WrapBehaviour.CHAR);
        tw.putString("\n"+tw.getWrapBehaviour()+":\n");
        tw.putString(loremIpsum);

        tw.setWrapBehaviour(WrapBehaviour.WORD);
        tw.putString("\n"+tw.getWrapBehaviour()+":\n");
        tw.putString(loremIpsum);

        tw.setWrapBehaviour(WrapBehaviour.CLIP);
        writer.setTabBehaviour(TabBehaviour.IGNORE);
        tw.putString("\n"+tw.getWrapBehaviour()+" + TabBehaviour.IGNORE:\n");
        tw.putString(loremIpsum);

        tw.putString("\033[m");
        tw.setStyleable(false);
        tw.putString(tw.getWrapBehaviour()+" + Styleable turned off, so esc-sequences are visible:\n");
        tw.putString(loremIpsum);

        screen.refresh();
        screen.readInput();
        screen.stopScreen();
    }
}
