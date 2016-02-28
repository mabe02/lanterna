package com.googlecode.lanterna.terminal.swing;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.TreeSet;

import static org.junit.Assert.*;

/**
 * Created by martin on 27/02/16.
 */
public class VirtualTerminalTest {
    @Test
    public void singleLineWriteAndReadBackWorks() {
        VirtualTerminal virtualTerminal = new VirtualTerminal(new TerminalSize(80, 24));
        assertEquals(TerminalPosition.TOP_LEFT_CORNER, virtualTerminal.getTranslatedCursorPosition());
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
        VirtualTerminal virtualTerminal = new VirtualTerminal(new TerminalSize(10, 5));
        assertEquals(TerminalPosition.TOP_LEFT_CORNER, virtualTerminal.getTranslatedCursorPosition());
        virtualTerminal.putCharacter(new TextCharacter('H'));
        virtualTerminal.putCharacter(new TextCharacter('E'));
        virtualTerminal.putCharacter(new TextCharacter('L'));
        virtualTerminal.putCharacter(new TextCharacter('L'));
        virtualTerminal.putCharacter(new TextCharacter('O'));
        virtualTerminal.clear();

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
        VirtualTerminal virtualTerminal = new VirtualTerminal(new TerminalSize(5, 3));
        assertEquals(TerminalPosition.TOP_LEFT_CORNER, virtualTerminal.getTranslatedCursorPosition());
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
        VirtualTerminal virtualTerminal = new VirtualTerminal(new TerminalSize(5, 5));
        assertEquals(TerminalPosition.TOP_LEFT_CORNER, virtualTerminal.getTranslatedCursorPosition());
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
        VirtualTerminal virtualTerminal = new VirtualTerminal(new TerminalSize(9, 5));
        assertEquals(TerminalPosition.TOP_LEFT_CORNER, virtualTerminal.getTranslatedCursorPosition());
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
        VirtualTerminal virtualTerminal = new VirtualTerminal(new TerminalSize(5, 5));
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
        VirtualTerminal virtualTerminal = new VirtualTerminal(new TerminalSize(3, 3));
        virtualTerminal.putCharacter(newline());
        virtualTerminal.putCharacter(newline());
        assertEquals(new TerminalPosition(0, 2), virtualTerminal.getCursorPosition());
        virtualTerminal.putCharacter(newline());
        assertEquals(new TerminalPosition(0, 2), virtualTerminal.getCursorPosition());
        virtualTerminal.putCharacter(newline());
        assertEquals(new TerminalPosition(0, 2), virtualTerminal.getCursorPosition());

        // Shrink viewport
        virtualTerminal.setViewportSize(new TerminalSize(3, 2));
        assertEquals(new TerminalPosition(0, 1), virtualTerminal.getCursorPosition());

        // Restore
        virtualTerminal.setViewportSize(new TerminalSize(3, 3));
        assertEquals(new TerminalPosition(0, 2), virtualTerminal.getCursorPosition());

        // Enlarge
        virtualTerminal.setViewportSize(new TerminalSize(3, 4));
        assertEquals(new TerminalPosition(0, 3), virtualTerminal.getCursorPosition());
        virtualTerminal.setViewportSize(new TerminalSize(3, 5));
        assertEquals(new TerminalPosition(0, 4), virtualTerminal.getCursorPosition());

        // We've reached the total size of the buffer, enlarging it further shouldn't affect the cursor position
        virtualTerminal.setViewportSize(new TerminalSize(3, 6));
        assertEquals(new TerminalPosition(0, 4), virtualTerminal.getCursorPosition());
        virtualTerminal.setViewportSize(new TerminalSize(3, 7));
        assertEquals(new TerminalPosition(0, 4), virtualTerminal.getCursorPosition());
    }

    @Test
    public void textScrollingOutOfTheBacklogDisappears() {
        VirtualTerminal virtualTerminal = new VirtualTerminal(new TerminalSize(10, 3));
        // Backlog of 1, meaning viewport size + 1 row
        virtualTerminal.setBacklogSize(1);
        putString(virtualTerminal, "Line 1\n");
        putString(virtualTerminal, "Line 2\n");
        putString(virtualTerminal, "Line 3\n");
        putString(virtualTerminal, "Line 4\n"); // This should knock out "Line 1"

        // Expected content:
        //(|Line 1    | <- discarded)
        // ------------
        // |Line 2    | <- backlog
        // ------------
        // |Line 3    | <- viewport
        // |Line 4    | <- viewport
        // |          | <- viewport

        // Make terminal bigger
        virtualTerminal.setViewportSize(new TerminalSize(10, 4));

        // Now line 2 should be the top row
        assertEquals('2', virtualTerminal.getCharacter(new TerminalPosition(5, 0)).getCharacter());

        // Make it even bigger
        virtualTerminal.setViewportSize(new TerminalSize(10, 5));

        // Should make no difference, the viewport will add an empty row at the end, because there is nothing in the
        // backlog to insert at the top
        assertEquals('2', virtualTerminal.getCharacter(new TerminalPosition(5, 0)).getCharacter());
    }

    @Test
    public void backlogTrimmingAdjustsCursorPositionAndDirtyCells() {
        VirtualTerminal virtualTerminal = new VirtualTerminal(new TerminalSize(80, 3));
        virtualTerminal.setBacklogSize(0);
        virtualTerminal.putCharacter(fromChar('A'));
        virtualTerminal.setCursorPosition(new TerminalPosition(1, 1));
        virtualTerminal.putCharacter(fromChar('B'));
        virtualTerminal.setCursorPosition(new TerminalPosition(2, 2));
        virtualTerminal.putCharacter(fromChar('C'));

        // Dirty positions should now be these
        assertEquals(new TreeSet<TerminalPosition>(Arrays.asList(
                new TerminalPosition(0, 0),
                new TerminalPosition(1, 1),
                new TerminalPosition(2, 2))), virtualTerminal.getDirtyCells());
        assertEquals(new TerminalPosition(3, 2), virtualTerminal.getCursorPosition());

        // Add one more row to shift out the first line
        virtualTerminal.putCharacter(newline());

        // Dirty positions should now be adjusted
        assertEquals(new TreeSet<TerminalPosition>(Arrays.asList(
                new TerminalPosition(1, 0),
                new TerminalPosition(2, 1))), virtualTerminal.getDirtyCells());
        assertEquals(new TerminalPosition(0, 2), virtualTerminal.getCursorPosition());
    }

    private void putString(VirtualTerminal virtualTerminal, String string) {
        for(char c: string.toCharArray()) {
            virtualTerminal.putCharacter(new TextCharacter(c));
        }
    }

    private TextCharacter newline() {
        return new TextCharacter('\n');
    }

    private TextCharacter fromChar(char c) {
        return new TextCharacter(c);
    }
}
