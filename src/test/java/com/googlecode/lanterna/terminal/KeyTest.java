package com.googlecode.lanterna.terminal;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class KeyTest {

    @Test
    public void testFromVim() {
        {
            KeyStroke k = KeyStroke.fromString("a");
            assertEquals(KeyType.Character, k.getKeyType());
            assertEquals(new Character('a'), k.getCharacter());
            assertEquals(false, k.isCtrlDown());
            assertEquals(false, k.isAltDown());
        }
        {
            KeyStroke k = KeyStroke.fromString("<c-a>");
            assertEquals(KeyType.Character, k.getKeyType());
            assertEquals(new Character('a'), k.getCharacter());
            assertEquals(true, k.isCtrlDown());
            assertEquals(false, k.isAltDown());
        }
        {
            KeyStroke k = KeyStroke.fromString("<a-a>");
            assertEquals(KeyType.Character, k.getKeyType());
            assertEquals(new Character('a'), k.getCharacter());
            assertEquals(false, k.isCtrlDown());
            assertEquals(true, k.isAltDown());
        }
        {
            KeyStroke k = KeyStroke.fromString("<c-a-a>");
            assertEquals(k.getKeyType(), KeyType.Character);
            assertEquals(new Character('a'), k.getCharacter());
            assertEquals(true, k.isCtrlDown());
            assertEquals(true, k.isAltDown());
        }
        assertEquals(KeyType.ReverseTab, KeyStroke.fromString("<s-tab>").getKeyType());
        assertEquals(KeyType.ReverseTab, KeyStroke.fromString("<S-tab>").getKeyType());
        assertEquals(KeyType.ReverseTab, KeyStroke.fromString("<S-Tab>").getKeyType());
        assertEquals(KeyType.Enter, KeyStroke.fromString("<cr>").getKeyType());
        assertEquals(KeyType.PageUp, KeyStroke.fromString("<PageUp>").getKeyType());
    }

}
