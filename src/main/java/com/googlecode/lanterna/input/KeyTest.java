package com.googlecode.lanterna.input;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class KeyTest {
	
	@Test
	public void testFromVim() {
		{
			Key k = Key.fromString("a");
			assertEquals(Key.Kind.NormalKey, k.getKind());
			assertEquals(new Character('a'), k.getCharacter());
			assertEquals(false, k.isCtrlPressed());
			assertEquals(false, k.isAltPressed());
		}
		{
			Key k = Key.fromString("<c-a>");
			assertEquals(Key.Kind.NormalKey, k.getKind());
			assertEquals(new Character('a'), k.getCharacter());
			assertEquals(true, k.isCtrlPressed());
			assertEquals(false, k.isAltPressed());
		}
		{
			Key k = Key.fromString("<a-a>");
			assertEquals(Key.Kind.NormalKey, k.getKind());
			assertEquals(new Character('a'), k.getCharacter());
			assertEquals(false, k.isCtrlPressed());
			assertEquals(true, k.isAltPressed());
		}
		{
			Key k = Key.fromString("<c-a-a>");
			assertEquals(k.getKind(), Key.Kind.NormalKey);
			assertEquals(new Character('a'), k.getCharacter());
			assertEquals(true, k.isCtrlPressed());
			assertEquals(true, k.isAltPressed());
		}
		assertEquals(Key.Kind.ReverseTab, Key.fromString("<s-tab>").getKind());
		assertEquals(Key.Kind.Enter, Key.fromString("<cr>").getKind());
		assertEquals(Key.Kind.PageUp, Key.fromString("<PageUp>").getKind());
	}

}
