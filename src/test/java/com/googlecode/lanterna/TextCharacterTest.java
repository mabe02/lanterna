package com.googlecode.lanterna;

import org.junit.Test;

import static org.junit.Assert.*;

public class TextCharacterTest {

    @Test
    public void fromString() {
        assertEquals(6, TextCharacter.fromString("Hello!").length);
        assertEquals(5, TextCharacter.fromString("あいうえお").length);
        assertEquals(1, TextCharacter.fromString("\uD83C\uDF55").length);
        assertEquals(1, TextCharacter.fromString("\uD83E\uDD7A").length);
        // This is a weird compound character that should be printed as one, but is actually two char:s
        assertEquals(1, TextCharacter.fromString("บุ").length);

        // This should be one but Java is lagging behind a bit on Unicode emoji and interprets it as 2
        //assertEquals(1, TextCharacter.fromString("\uD83D\uDC4D\uD83C\uDFFF").length);
    }

    @Test
    public void emojisAreDoubleWidth() {
        assertTrue(TextCharacter.fromString("\uD83D\uDC69\uD83C\uDFFD")[0].isDoubleWidth());
        assertTrue(TextCharacter.fromString("\uD83C\uDFE9")[0].isDoubleWidth());
        assertTrue(TextCharacter.fromString("\uD83D\uDC96")[0].isDoubleWidth());
        assertTrue(TextCharacter.fromString("❤\uFE0F")[0].isDoubleWidth());
        assertTrue(TextCharacter.fromString("\uD83D\uDE0A")[0].isDoubleWidth());
        assertTrue(TextCharacter.fromString("\uD83D\uDC40")[0].isDoubleWidth());
        assertFalse(TextCharacter.fromString("M")[0].isDoubleWidth());  // Not emoji
    }
}