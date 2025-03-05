/*
 * This file is part of lanterna (https://github.com/mabe02/lanterna).
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
 * Copyright (C) 2025 Svatopluk Dedic
 */
package com.googlecode.lanterna.screen;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.DoublePrintingTextGraphics;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.graphics.TextGraphicsWriter;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author sdedic
 */
public class ScreenTextGraphicsTest {
    Terminal terminal;
    TerminalScreen  screen;
    TextGraphics textGraphics;
    TextGraphics subGraphics;
    TerminalPosition subPosition = new TerminalPosition(10, 10);
    TerminalSize subSize = new TerminalSize(15, 10);
    
    public ScreenTextGraphicsTest() {
    }

    @Before
    public void setUp() throws IOException {
        // pass empty InputStream, so any read completes immediately.
        terminal = new DefaultTerminalFactory(System.out, new ByteArrayInputStream(new byte[0]), 
                Charset.defaultCharset()).
                setInitialTerminalSize(new TerminalSize(120, 50)).
                createHeadlessTerminal();
        screen = new TerminalScreen(terminal);
        screen.startScreen();

        textGraphics = screen.newTextGraphics();
        subGraphics = textGraphics.newTextGraphics(subPosition, subSize);
    }
    
    @After 
    public void tearDown() throws Exception {
        screen.stopScreen(false);
    }
    
    /**
     * Checks that the root TextGraphics does not translate positions.
     * @throws Exception 
     */
    @Test    public void rootToScreenPosition() throws Exception {
        TerminalPosition pos = new TerminalPosition(3, 3);
        
        textGraphics.putString(pos, "Hello");
        
        TerminalPosition screenPos = textGraphics.toScreenPosition(pos);
        assertEquals("H", screen.getBackCharacter(screenPos).getCharacterString());
        assertEquals("l", screen.getBackCharacter(screenPos.withRelativeColumn(3)).getCharacterString());
    }    

    @Test
    public void rootSubGraphicsOffset() throws Exception {
        TerminalPosition pos = new TerminalPosition(3, 3);
        
        subGraphics.putString(pos, "Hello");
        
        assertNotEquals(pos, subGraphics.toScreenPosition(pos));
        assertNotEquals(textGraphics.toScreenPosition(pos), subGraphics.toScreenPosition(pos));
        
        TerminalPosition screenPos = subGraphics.toScreenPosition(pos);
        assertEquals("H", screen.getBackCharacter(screenPos).getCharacterString());
        assertEquals("l", screen.getBackCharacter(screenPos.withRelativeColumn(3)).getCharacterString());
    }    
    
    @Test
    public void testPositionPastSubGraphicsSize() throws Exception {
        TerminalPosition outOfRange = new TerminalPosition(20, 10);
        
        TerminalPosition toScreen = subGraphics.toScreenPosition(outOfRange);
        assertNull(toScreen);
    }

    @Test
    public void testPositionPastRootGraphicsSize() throws Exception {
        TerminalPosition outOfRange = new TerminalPosition(200, 10);
        
        TerminalPosition toScreen = textGraphics.toScreenPosition(outOfRange);
        assertNull(toScreen);
    }
    
    @Test
    public void testDoublePrintingGraphics() throws Exception {
        TerminalPosition pos = new TerminalPosition(1, 2);
        TextGraphics doubleText = new DoublePrintingTextGraphics(subGraphics);
        doubleText.putString(pos, "Ahoj");
        TerminalPosition screenPos = doubleText.toScreenPosition(pos);
        TerminalPosition nextScreenPos = doubleText.toScreenPosition(pos.withRelativeColumn("Ahoj".length()));
        
        TerminalPosition diff = nextScreenPos.minus(screenPos);
        assertEquals("Ahoj".length() * 2, diff.getColumn());
        assertEquals('A', screen.getBackCharacter(screenPos).getCharacter());
        assertEquals('j', screen.getBackCharacter(nextScreenPos.withRelativeColumn(-1)).getCharacter());
    }
    
    @Test
    public void testTextWriterPositions() throws Exception {
        TerminalPosition pos = new TerminalPosition(3, 2);
        TextGraphicsWriter writer = new TextGraphicsWriter(subGraphics);
        
        writer.setCursorPosition(pos);
        TerminalPosition startPos = writer.toScreenPosition(null);
        writer.putString("Ahoj");
        
        TerminalPosition nextPos = writer.toScreenPosition(null);
        
        TerminalPosition diff = nextPos.minus(startPos);
        assertEquals("Ahoj".length(), diff.getColumn());
        
        assertEquals('A', screen.getBackCharacter(startPos).getCharacter());
        assertEquals('j', screen.getBackCharacter(nextPos.withRelativeColumn(-1)).getCharacter());
    }
}
