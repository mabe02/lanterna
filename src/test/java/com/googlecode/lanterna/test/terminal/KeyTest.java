package com.googlecode.lanterna.test.terminal;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class KeyTest {
	
	@Test
	public void testFromVim() {
		{
			KeyStroke k = KeyStroke.fromString("a");
			assertEquals(KeyType.Character, k.getKey());
			assertEquals(new Character('a'), k.getCharacter());
			assertEquals(false, k.isCtrlDown());
			assertEquals(false, k.isAltDown());
		}
		{
			KeyStroke k = KeyStroke.fromString("<c-a>");
			assertEquals(KeyType.Character, k.getKey());
			assertEquals(new Character('a'), k.getCharacter());
			assertEquals(true, k.isCtrlDown());
			assertEquals(false, k.isAltDown());
		}
		{
			KeyStroke k = KeyStroke.fromString("<a-a>");
			assertEquals(KeyType.Character, k.getKey());
			assertEquals(new Character('a'), k.getCharacter());
			assertEquals(false, k.isCtrlDown());
			assertEquals(true, k.isAltDown());
		}
		{
			KeyStroke k = KeyStroke.fromString("<c-a-a>");
			assertEquals(k.getKey(), KeyType.Character);
			assertEquals(new Character('a'), k.getCharacter());
			assertEquals(true, k.isCtrlDown());
			assertEquals(true, k.isAltDown());
		}
		assertEquals(KeyType.ReverseTab, KeyStroke.fromString("<s-tab>").getKey());
		assertEquals(KeyType.ReverseTab, KeyStroke.fromString("<S-tab>").getKey());
		assertEquals(KeyType.ReverseTab, KeyStroke.fromString("<S-Tab>").getKey());
		assertEquals(KeyType.Enter, KeyStroke.fromString("<cr>").getKey());
		assertEquals(KeyType.PageUp, KeyStroke.fromString("<PageUp>").getKey());
	}

}
