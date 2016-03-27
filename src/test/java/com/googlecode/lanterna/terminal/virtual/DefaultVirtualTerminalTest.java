/*
 * This file is part of lanterna (http://code.google.com/p/lanterna/).
 *
 * lanterna is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2010-2016 Martin
 */
package com.googlecode.lanterna.terminal.virtual;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.Terminal;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * Created by martin on 25/03/16.
 */
public class DefaultVirtualTerminalTest {
    private static final TextCharacter DEFAULT_CHARACTER = TextCharacter.DEFAULT_CHARACTER;
    private final DefaultVirtualTerminal virtualTerminal;

    public DefaultVirtualTerminalTest() {
        this.virtualTerminal = new DefaultVirtualTerminal();
    }

    @Test
    public void initialTerminalStateIsAsExpected() {
        assertEquals(TerminalPosition.TOP_LEFT_CORNER, virtualTerminal.getCursorPosition());
        TerminalSize terminalSize = virtualTerminal.getTerminalSize();
        assertEquals(new TerminalSize(80, 24), terminalSize);

        for(int row = 0; row < terminalSize.getRows(); row++) {
            for(int column = 0; column < terminalSize.getColumns(); column++) {
                assertEquals(DEFAULT_CHARACTER, virtualTerminal.getCharacter(column, row));
            }
        }
    }

    @Test
    public void simpleTestOutputTest() {
        String testString = "Hello World!";
        for(char c: testString.toCharArray()) {
            virtualTerminal.putCharacter(c);
        }
        assertLineEquals(testString, 0);
        assertLineEquals("", 1);
        assertEquals(new TerminalPosition(testString.length(), 0), virtualTerminal.getCursorPosition());
    }

    @Test
    public void multiLineTextTest() {
        String[] toPrint = new String[] {
                "Hello",
                "Hallo",
                "Hallå",
                "こんにちは"
        };
        for(String string: toPrint) {
            for(char c : string.toCharArray()) {
                virtualTerminal.putCharacter(c);
            }
            virtualTerminal.putCharacter('\n');
        }
        for(int i = 0; i < toPrint.length; i++) {
            assertLineEquals(toPrint[i], i);
        }
        assertEquals(new TerminalPosition(0, toPrint.length), virtualTerminal.getCursorPosition());
    }

    @Test
    public void singleLineWriteAndReadBackWorks() {
        assertEquals(TerminalPosition.TOP_LEFT_CORNER, virtualTerminal.getCursorPosition());
        virtualTerminal.putCharacter(new TextCharacter('H'));
        virtualTerminal.putCharacter(new TextCharacter('E'));
        virtualTerminal.putCharacter(new TextCharacter('L'));
        virtualTerminal.putCharacter(new TextCharacter('L'));
        virtualTerminal.putCharacter(new TextCharacter('O'));
        assertEquals(TerminalPosition.TOP_LEFT_CORNER.withColumn(5), virtualTerminal.getCursorPosition());
        assertEquals('H', virtualTerminal.getCharacter(new TerminalPosition(0, 0)).getCharacter());
        assertEquals('E', virtualTerminal.getCharacter(new TerminalPosition(1, 0)).getCharacter());
        assertEquals('L', virtualTerminal.getCharacter(new TerminalPosition(2, 0)).getCharacter());
        assertEquals('L', virtualTerminal.getCharacter(new TerminalPosition(3, 0)).getCharacter());
        assertEquals('O', virtualTerminal.getCharacter(new TerminalPosition(4, 0)).getCharacter());

        assertFalse(virtualTerminal.isWholeBufferDirtyThenReset());
        assertEquals(new TreeSet<TerminalPosition>(Arrays.asList(
                new TerminalPosition(0, 0),
                new TerminalPosition(1, 0),
                new TerminalPosition(2, 0),
                new TerminalPosition(3, 0),
                new TerminalPosition(4, 0))),
                virtualTerminal.getAndResetDirtyCells());

        // Make sure it's reset
        assertEquals(Collections.emptySet(), virtualTerminal.getAndResetDirtyCells());
    }

    @Test
    public void clearAllMarksEverythingAsDirtyAndEverythingInTheTerminalIsReplacedWithDefaultCharacter() {
        virtualTerminal.setTerminalSize(new TerminalSize(10, 5));
        assertEquals(TerminalPosition.TOP_LEFT_CORNER, virtualTerminal.getCursorPosition());
        virtualTerminal.putCharacter(new TextCharacter('H'));
        virtualTerminal.putCharacter(new TextCharacter('E'));
        virtualTerminal.putCharacter(new TextCharacter('L'));
        virtualTerminal.putCharacter(new TextCharacter('L'));
        virtualTerminal.putCharacter(new TextCharacter('O'));
        virtualTerminal.clearScreen();

        assertTrue(virtualTerminal.isWholeBufferDirtyThenReset());
        assertEquals(Collections.emptySet(), virtualTerminal.getAndResetDirtyCells());

        assertEquals(TerminalPosition.TOP_LEFT_CORNER, virtualTerminal.getCursorPosition());
        assertEquals(TextCharacter.DEFAULT_CHARACTER, virtualTerminal.getCharacter(new TerminalPosition(0, 0)));
        assertEquals(TextCharacter.DEFAULT_CHARACTER, virtualTerminal.getCharacter(new TerminalPosition(1, 0)));
        assertEquals(TextCharacter.DEFAULT_CHARACTER, virtualTerminal.getCharacter(new TerminalPosition(2, 0)));
        assertEquals(TextCharacter.DEFAULT_CHARACTER, virtualTerminal.getCharacter(new TerminalPosition(3, 0)));
        assertEquals(TextCharacter.DEFAULT_CHARACTER, virtualTerminal.getCharacter(new TerminalPosition(4, 0)));
    }

    @Test
    public void replacingAllContentTriggersWholeTerminalIsDirty() {
        virtualTerminal.setTerminalSize(new TerminalSize(5, 3));
        assertEquals(TerminalPosition.TOP_LEFT_CORNER, virtualTerminal.getCursorPosition());
        virtualTerminal.putCharacter(new TextCharacter('H'));
        virtualTerminal.putCharacter(new TextCharacter('E'));
        virtualTerminal.putCharacter(new TextCharacter('L'));
        virtualTerminal.putCharacter(new TextCharacter('L'));
        virtualTerminal.putCharacter(new TextCharacter('O'));
        virtualTerminal.putCharacter(new TextCharacter('H'));
        virtualTerminal.putCharacter(new TextCharacter('E'));
        virtualTerminal.putCharacter(new TextCharacter('L'));
        virtualTerminal.putCharacter(new TextCharacter('L'));
        virtualTerminal.putCharacter(new TextCharacter('O'));
        virtualTerminal.putCharacter(new TextCharacter('B'));
        virtualTerminal.putCharacter(new TextCharacter('Y'));
        virtualTerminal.putCharacter(new TextCharacter('E'));
        virtualTerminal.putCharacter(new TextCharacter('!'));

        assertTrue(virtualTerminal.isWholeBufferDirtyThenReset());
        assertEquals(Collections.emptySet(), virtualTerminal.getAndResetDirtyCells());
    }

    @Test
    public void tooLongLinesWrap() {
        virtualTerminal.setTerminalSize(new TerminalSize(5, 5));
        assertEquals(TerminalPosition.TOP_LEFT_CORNER, virtualTerminal.getCursorPosition());
        virtualTerminal.putCharacter(new TextCharacter('H'));
        virtualTerminal.putCharacter(new TextCharacter('E'));
        virtualTerminal.putCharacter(new TextCharacter('L'));
        virtualTerminal.putCharacter(new TextCharacter('L'));
        virtualTerminal.putCharacter(new TextCharacter('O'));
        virtualTerminal.putCharacter(new TextCharacter('!'));
        assertEquals(TerminalPosition.OFFSET_1x1, virtualTerminal.getCursorPosition());

        // Expected layout:
        // |HELLO|
        // |!    |
        // where the cursor is one column after the '!'
    }

    @Test
    public void makeSureDoubleWidthCharactersWrapProperly() {
        virtualTerminal.setTerminalSize(new TerminalSize(9, 5));
        assertEquals(TerminalPosition.TOP_LEFT_CORNER, virtualTerminal.getCursorPosition());
        virtualTerminal.putCharacter(new TextCharacter('こ'));
        virtualTerminal.putCharacter(new TextCharacter('ん'));
        virtualTerminal.putCharacter(new TextCharacter('に'));
        virtualTerminal.putCharacter(new TextCharacter('ち'));
        virtualTerminal.putCharacter(new TextCharacter('は'));
        virtualTerminal.putCharacter(new TextCharacter('!'));
        assertEquals(new TerminalPosition(3, 1), virtualTerminal.getCursorPosition());

        // Expected layout:
        // |こんにち|
        // |は!    |
        // where the cursor is one column after the '!' (2 + 1 = 3rd column)

        // Make sure there's a default padding character at 8x0
        assertEquals(TextCharacter.DEFAULT_CHARACTER, virtualTerminal.getCharacter(new TerminalPosition(8, 0)));
    }

    @Test
    public void overwritingDoubleWidthCharactersEraseTheOtherHalf() {
        virtualTerminal.setTerminalSize(new TerminalSize(5, 5));
        virtualTerminal.putCharacter(new TextCharacter('画'));
        virtualTerminal.putCharacter(new TextCharacter('面'));

        assertEquals('画', virtualTerminal.getCharacter(new TerminalPosition(0, 0)).getCharacter());
        assertEquals('画', virtualTerminal.getCharacter(new TerminalPosition(1, 0)).getCharacter());
        assertEquals('面', virtualTerminal.getCharacter(new TerminalPosition(2, 0)).getCharacter());
        assertEquals('面', virtualTerminal.getCharacter(new TerminalPosition(3, 0)).getCharacter());

        virtualTerminal.setCursorPosition(new TerminalPosition(0, 0));
        virtualTerminal.putCharacter(new TextCharacter('Y'));

        assertEquals('Y', virtualTerminal.getCharacter(new TerminalPosition(0, 0)).getCharacter());
        assertEquals(TextCharacter.DEFAULT_CHARACTER, virtualTerminal.getCharacter(new TerminalPosition(1, 0)));

        virtualTerminal.setCursorPosition(new TerminalPosition(3, 0));
        virtualTerminal.putCharacter(new TextCharacter('V'));

        assertEquals(TextCharacter.DEFAULT_CHARACTER, virtualTerminal.getCharacter(new TerminalPosition(2, 0)));
        assertEquals('V', virtualTerminal.getCharacter(new TerminalPosition(3, 0)).getCharacter());
    }

    @Test
    public void testCursorPositionUpdatesWhenTerminalSizeChanges() {
        virtualTerminal.setTerminalSize(new TerminalSize(3, 3));
        virtualTerminal.putCharacter('\n');
        virtualTerminal.putCharacter('\n');
        assertEquals(new TerminalPosition(0, 2), virtualTerminal.getCursorPosition());
        virtualTerminal.putCharacter('\n');
        assertEquals(new TerminalPosition(0, 2), virtualTerminal.getCursorPosition());
        virtualTerminal.putCharacter('\n');
        assertEquals(new TerminalPosition(0, 2), virtualTerminal.getCursorPosition());

        // Shrink viewport
        virtualTerminal.setTerminalSize(new TerminalSize(3, 2));
        assertEquals(new TerminalPosition(0, 1), virtualTerminal.getCursorPosition());

        // Restore
        virtualTerminal.setTerminalSize(new TerminalSize(3, 3));
        assertEquals(new TerminalPosition(0, 2), virtualTerminal.getCursorPosition());

        // Enlarge
        virtualTerminal.setTerminalSize(new TerminalSize(3, 4));
        assertEquals(new TerminalPosition(0, 3), virtualTerminal.getCursorPosition());
        virtualTerminal.setTerminalSize(new TerminalSize(3, 5));
        assertEquals(new TerminalPosition(0, 4), virtualTerminal.getCursorPosition());

        // We've reached the total size of the buffer, enlarging it further shouldn't affect the cursor position
        virtualTerminal.setTerminalSize(new TerminalSize(3, 6));
        assertEquals(new TerminalPosition(0, 4), virtualTerminal.getCursorPosition());
        virtualTerminal.setTerminalSize(new TerminalSize(3, 7));
        assertEquals(new TerminalPosition(0, 4), virtualTerminal.getCursorPosition());
    }

    @Test
    public void textScrollingOutOfTheBacklogDisappears() {
        virtualTerminal.setTerminalSize(new TerminalSize(10, 3));
        // Backlog of 1, meaning viewport size + 1 row
        virtualTerminal.setBacklogSize(1);
        putString("Line 1\n");
        assertEquals(new TerminalPosition(0, 1), virtualTerminal.getCursorPosition());
        assertEquals(virtualTerminal.getCursorPosition(), virtualTerminal.getCursorBufferPosition());
        putString("Line 2\n");
        putString("Line 3\n");
        putString("Line 4\n"); // This should knock out "Line 1"

        // Expected content:
        //(|Line 1    | <- discarded)
        // ------------
        // |Line 2    | <- backlog
        // ------------
        // |Line 3    | <- viewport
        // |Line 4    | <- viewport
        // |          | <- viewport

        assertBufferLineEquals("Line 2", 0);
        assertBufferLineEquals("Line 3", 1);
        assertLineEquals("Line 3", 0);
        assertLineEquals("Line 4", 1);
        assertLineEquals("", 2);
        assertEquals(new TerminalPosition(0, 2), virtualTerminal.getCursorPosition());
        assertEquals(new TerminalPosition(0, 3), virtualTerminal.getCursorBufferPosition());

        // Make terminal bigger
        virtualTerminal.setTerminalSize(new TerminalSize(10, 4));

        // Now "Line 2" should be the top row
        assertLineEquals("Line 2", 0);
        assertLineEquals("Line 3", 1);
        assertLineEquals("Line 4", 2);
        assertLineEquals("", 3);
        assertEquals(new TerminalPosition(0, 3), virtualTerminal.getCursorPosition());
        assertEquals(new TerminalPosition(0, 3), virtualTerminal.getCursorBufferPosition());

        // Make it even bigger
        virtualTerminal.setTerminalSize(new TerminalSize(10, 5));

        // Should make no difference, the viewport will add an empty row at the end, because there is nothing in the
        // backlog to insert at the top
        assertLineEquals("Line 2", 0);
        assertLineEquals("Line 3", 1);
        assertLineEquals("Line 4", 2);
        assertLineEquals("", 3);
        assertLineEquals("", 4);
        assertEquals(new TerminalPosition(0, 3), virtualTerminal.getCursorPosition());
        assertEquals(new TerminalPosition(0, 3), virtualTerminal.getCursorBufferPosition());
    }

    @Test
    public void backlogTrimmingAdjustsCursorPositionAndDirtyCells() {
        virtualTerminal.setTerminalSize(new TerminalSize(80, 3));
        virtualTerminal.setBacklogSize(0);
        virtualTerminal.putCharacter(fromChar('A'));
        virtualTerminal.setCursorPosition(new TerminalPosition(1, 1));
        virtualTerminal.putCharacter(fromChar('B'));
        virtualTerminal.setCursorPosition(new TerminalPosition(2, 2));
        virtualTerminal.putCharacter(fromChar('C'));

        assertLineEquals("A", 0);
        assertLineEquals(" B", 1);
        assertLineEquals("  C", 2);

        // Dirty positions should now be these
        assertEquals(new TreeSet<TerminalPosition>(Arrays.asList(
                new TerminalPosition(0, 0),
                new TerminalPosition(1, 1),
                new TerminalPosition(2, 2))), virtualTerminal.getDirtyCells());
        assertEquals(new TerminalPosition(3, 2), virtualTerminal.getCursorPosition());

        // Add one more row to shift out the first line
        virtualTerminal.putCharacter('\n');

        // Dirty positions should now be adjusted
        assertEquals(new TreeSet<TerminalPosition>(Arrays.asList(
                new TerminalPosition(1, 0),
                new TerminalPosition(2, 1))), virtualTerminal.getDirtyCells());
        assertEquals(new TerminalPosition(0, 2), virtualTerminal.getCursorPosition());
    }

    @Test
    public void testPrivateMode() throws Exception {
        final int ROWS = 5;
        virtualTerminal.setTerminalSize(new TerminalSize(20, ROWS));
        for(int i = 1; i <= ROWS + 2; i++) {
            putString("Line " + i + "\n");
        }
        assertEquals(new TerminalPosition(0, ROWS - 1), virtualTerminal.getCursorPosition());
        assertEquals(new TerminalPosition(0, ROWS + 2), virtualTerminal.getCursorBufferPosition());

        virtualTerminal.enterPrivateMode();
        assertEquals(new TerminalPosition(0, 0), virtualTerminal.getCursorPosition());
        assertEquals(new TerminalPosition(0, 0), virtualTerminal.getCursorBufferPosition());
        for(int i = 0; i < ROWS; i++) {
            assertLineEquals("", i);
        }

        // There should be no backlog in private mode
        for(int i = 1; i <= ROWS + 4; i++) {
            putString("Line " + i + "\n");
        }
        for(int i = 0; i < ROWS - 1; i++) {
            assertLineEquals("Line " + (i + 6), i);
        }
        assertLineEquals("", ROWS - 1);
        assertEquals(5, virtualTerminal.getBufferLineCount());

        virtualTerminal.exitPrivateMode();
        for(int i = 0; i < ROWS - 1; i++) {
            assertLineEquals("Line " + (i+4), i);
        }
        assertLineEquals("", ROWS - 1);
    }

    @Test
    public void testForEachLine() {
        final int ROWS = 40;
        virtualTerminal.setTerminalSize(new TerminalSize(10, 5));
        for(int i = 1; i <= ROWS; i++) {
            putString("Line " + i + "\n");
        }
        virtualTerminal.forEachLine(0, ROWS, new VirtualTerminal.BufferWalker() {
            @Override
            public void onLine(int rowNumber, VirtualTerminal.BufferLine bufferLine) {
                if(rowNumber == ROWS) {
                    assertLineEquals("", bufferLine);
                }
                else {
                    assertLineEquals("Line " + (rowNumber + 1), bufferLine);
                }
            }
        });
    }

    @Test
    public void testColorAndSGR() {
        virtualTerminal.putCharacter('A');
        virtualTerminal.setBackgroundColor(TextColor.ANSI.BLUE);
        virtualTerminal.setForegroundColor(TextColor.ANSI.WHITE);
        virtualTerminal.putCharacter('B');
        virtualTerminal.enableSGR(SGR.BOLD);
        virtualTerminal.enableSGR(SGR.UNDERLINE);
        virtualTerminal.putCharacter('C');
        virtualTerminal.disableSGR(SGR.BOLD);
        virtualTerminal.putCharacter('D');
        virtualTerminal.resetColorAndSGR();
        virtualTerminal.putCharacter('E');

        assertEquals(TextCharacter.DEFAULT_CHARACTER.withCharacter('A'), virtualTerminal.getCharacter(0, 0));
        assertEquals(new TextCharacter('B', TextColor.ANSI.WHITE, TextColor.ANSI.BLUE), virtualTerminal.getCharacter(1, 0));
        assertEquals(new TextCharacter('C', TextColor.ANSI.WHITE, TextColor.ANSI.BLUE, SGR.BOLD, SGR.UNDERLINE), virtualTerminal.getCharacter(2, 0));
        assertEquals(new TextCharacter('D', TextColor.ANSI.WHITE, TextColor.ANSI.BLUE, SGR.UNDERLINE), virtualTerminal.getCharacter(3, 0));
        assertEquals(TextCharacter.DEFAULT_CHARACTER.withCharacter('E'), virtualTerminal.getCharacter(4, 0));
    }

    @Test
    public void testTabExpansion() {
        putString("XXXXXXXXXXXXXXXXXX");
        virtualTerminal.setCursorPosition(0, 0);
        virtualTerminal.putCharacter('\t');
        assertLineEquals("    XXXXXXXXXXXXXX", 0);

        virtualTerminal.clearScreen();
        putString("XXXXXXXXXXXXXXXXXX");
        virtualTerminal.setCursorPosition(1, 0);
        virtualTerminal.putCharacter('\t');
        assertLineEquals("X   XXXXXXXXXXXXXX", 0);

        virtualTerminal.clearScreen();
        putString("XXXXXXXXXXXXXXXXXX");
        virtualTerminal.setCursorPosition(2, 0);
        virtualTerminal.putCharacter('\t');
        assertLineEquals("XX  XXXXXXXXXXXXXX", 0);

        virtualTerminal.clearScreen();
        putString("XXXXXXXXXXXXXXXXXX");
        virtualTerminal.setCursorPosition(3, 0);
        virtualTerminal.putCharacter('\t');
        assertLineEquals("XXX XXXXXXXXXXXXXX", 0);

        virtualTerminal.clearScreen();
        putString("XXXXXXXXXXXXXXXXXX");
        virtualTerminal.setCursorPosition(4, 0);
        virtualTerminal.putCharacter('\t');
        assertLineEquals("XXXX    XXXXXXXXXX", 0);

        virtualTerminal.clearScreen();
        putString("XXXXXXXXXXXXXXXXXX");
        virtualTerminal.setCursorPosition(5, 0);
        virtualTerminal.putCharacter('\t');
        assertLineEquals("XXXXX   XXXXXXXXXX", 0);
    }

    @Test
    public void testInput() {
        KeyStroke keyStroke1 = new KeyStroke('A', false, false);
        KeyStroke keyStroke2 = new KeyStroke('B', false, false);
        virtualTerminal.addInput(keyStroke1);
        virtualTerminal.addInput(keyStroke2);
        assertEquals(keyStroke1, virtualTerminal.pollInput());
        assertEquals(keyStroke2, virtualTerminal.readInput());
    }

    @Test
    public void testVirtualTerminalListener() {
        final AtomicInteger flushCounter = new AtomicInteger(0);
        final AtomicInteger bellCounter = new AtomicInteger(0);
        final AtomicInteger resizeCounter = new AtomicInteger(0);

        VirtualTerminalListener listener = new VirtualTerminalListener() {
            @Override
            public void onFlush() {
                flushCounter.incrementAndGet();
            }

            @Override
            public void onBell() {
                bellCounter.incrementAndGet();
            }

            @Override
            public void onResized(Terminal terminal, TerminalSize newSize) {
                resizeCounter.incrementAndGet();
            }
        };

        virtualTerminal.flush();
        virtualTerminal.bell();
        virtualTerminal.setTerminalSize(new TerminalSize(40, 10));
        assertEquals(0, flushCounter.get());
        assertEquals(0, bellCounter.get());
        assertEquals(0, resizeCounter.get());

        virtualTerminal.addVirtualTerminalListener(listener);
        virtualTerminal.flush();
        virtualTerminal.bell();
        virtualTerminal.setTerminalSize(new TerminalSize(80, 20));
        assertEquals(1, flushCounter.get());
        assertEquals(1, bellCounter.get());
        assertEquals(1, resizeCounter.get());

        virtualTerminal.removeVirtualTerminalListener(listener);
        virtualTerminal.flush();
        virtualTerminal.bell();
        virtualTerminal.setTerminalSize(new TerminalSize(40, 10));
        assertEquals(1, flushCounter.get());
        assertEquals(1, bellCounter.get());
        assertEquals(1, resizeCounter.get());
    }

    @Test
    public void settingCursorOutsideOfTerminalWindowWillBeAdjusted() {
        virtualTerminal.setTerminalSize(new TerminalSize(10, 5));
        virtualTerminal.setCursorPosition(20, 10);
        assertEquals(new TerminalPosition(9, 4), virtualTerminal.getCursorPosition());

        virtualTerminal.setCursorPosition(0, 10);
        assertEquals(new TerminalPosition(0, 4), virtualTerminal.getCursorPosition());

        virtualTerminal.setCursorPosition(20, 0);
        assertEquals(new TerminalPosition(9, 0), virtualTerminal.getCursorPosition());
    }

    @Test
    public void puttingCharacterInLastColumnDoesntMoveCursorToNextLine() {
        virtualTerminal.setTerminalSize(new TerminalSize(10, 5));
        virtualTerminal.setCursorPosition(8, 2);
        assertEquals(new TerminalPosition(8, 2), virtualTerminal.getCursorPosition());
        virtualTerminal.putCharacter('A');
        assertEquals(new TerminalPosition(9, 2), virtualTerminal.getCursorPosition());
        virtualTerminal.putCharacter('B');
        assertEquals(new TerminalPosition(10, 2), virtualTerminal.getCursorPosition());
        virtualTerminal.putCharacter('C');
        assertEquals(new TerminalPosition(1, 3), virtualTerminal.getCursorPosition());
        assertEquals(DEFAULT_CHARACTER.withCharacter('C'), virtualTerminal.getCharacter(0, 3));
    }

    private void putString(String string) {
        for(char c: string.toCharArray()) {
            virtualTerminal.putCharacter(c);
        }
    }

    private TextCharacter fromChar(char c) {
        return new TextCharacter(c);
    }

    private void assertLineEquals(String expectedLineContent, int rowNumber) {
        int column = 0;
        for(char c: expectedLineContent.toCharArray()) {
            assertEquals(DEFAULT_CHARACTER.withCharacter(c), virtualTerminal.getCharacter(column++, rowNumber));
            if(TerminalTextUtils.isCharDoubleWidth(c)) {
                column++;
            }
        }
        while(column < virtualTerminal.getTerminalSize().getColumns()) {
            assertEquals(DEFAULT_CHARACTER, virtualTerminal.getCharacter(column++, rowNumber));
        }
    }

    private void assertBufferLineEquals(String expectedBufferLineContent, int rowNumber) {
        int column = 0;
        for(char c: expectedBufferLineContent.toCharArray()) {
            assertEquals(DEFAULT_CHARACTER.withCharacter(c), virtualTerminal.getBufferCharacter(new TerminalPosition(column++, rowNumber)));
            if(TerminalTextUtils.isCharDoubleWidth(c)) {
                column++;
            }
        }
        while(column < virtualTerminal.getTerminalSize().getColumns()) {
            assertEquals(DEFAULT_CHARACTER, virtualTerminal.getBufferCharacter(column++, rowNumber));
        }
    }

    private void assertLineEquals(String expectedLineContent, VirtualTerminal.BufferLine line) {
        int column = 0;
        for(char c: expectedLineContent.toCharArray()) {
            assertEquals(DEFAULT_CHARACTER.withCharacter(c), line.getCharacterAt(column++));
            if(TerminalTextUtils.isCharDoubleWidth(c)) {
                column++;
            }
        }
        while(column < virtualTerminal.getTerminalSize().getColumns()) {
            assertEquals(DEFAULT_CHARACTER, line.getCharacterAt(column++));
        }
    }
}
