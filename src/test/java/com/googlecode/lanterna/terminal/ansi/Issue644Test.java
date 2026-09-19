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
 * Copyright (C) 2010-2024 Martin Berglund
 */
package com.googlecode.lanterna.terminal.ansi;

import com.googlecode.lanterna.TerminalSize;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Issue644Test {

    private static final byte[] DEC_SAVE_CURSOR = { (byte)0x1b, (byte)'7' };
    private static final byte[] DEC_RESTORE_CURSOR = { (byte)0x1b, (byte)'8' };
    private static final byte[] CSI_SAVE_CURSOR = { (byte)0x1b, (byte)'[', (byte)'s' };
    private static final byte[] CSI_RESTORE_CURSOR = { (byte)0x1b, (byte)'[', (byte)'u' };

    @Test
    public void saveAndRestoreCursorUseDecSequences() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ANSITerminal terminal = newTestTerminal(new ByteArrayInputStream(new byte[0]), output);

        terminal.saveCursorPosition();
        terminal.restoreCursorPosition();

        byte[] written = output.toByteArray();
        assertContains(written, DEC_SAVE_CURSOR);
        assertContains(written, DEC_RESTORE_CURSOR);
        assertFalse(containsSubsequence(written, CSI_SAVE_CURSOR));
        assertFalse(containsSubsequence(written, CSI_RESTORE_CURSOR));
    }

    @Test
    public void findTerminalSizeSaveRestoreUsesDecSequences() throws IOException {
        // CPR response: row 24, column 80 (ESC [ 24 ; 80 R)
        byte[] cursorReport = "\u001B[24;80R".getBytes(StandardCharsets.US_ASCII);
        ByteArrayInputStream input = new ByteArrayInputStream(cursorReport);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ANSITerminal terminal = newTestTerminal(input, output);

        TerminalSize size = terminal.getTerminalSize();

        assertEquals(new TerminalSize(80, 24), size);
        byte[] written = output.toByteArray();
        assertContains(written, DEC_SAVE_CURSOR);
        assertContains(written, DEC_RESTORE_CURSOR);
        assertFalse(containsSubsequence(written, CSI_SAVE_CURSOR));
        assertFalse(containsSubsequence(written, CSI_RESTORE_CURSOR));
    }

    private static ANSITerminal newTestTerminal(InputStream input, OutputStream output) {
        return new ANSITerminal(input, output, StandardCharsets.UTF_8) { };
    }

    private static void assertContains(byte[] haystack, byte[] needle) {
        assertTrue("Expected " + bytesToHex(needle) + " in output " + bytesToHex(haystack),
                containsSubsequence(haystack, needle));
    }

    private static boolean containsSubsequence(byte[] haystack, byte[] needle) {
        if (needle.length == 0 || haystack.length < needle.length) {
            return false;
        }
        outer:
        for (int i = 0; i <= haystack.length - needle.length; i++) {
            for (int j = 0; j < needle.length; j++) {
                if (haystack[i + j] != needle[j]) {
                    continue outer;
                }
            }
            return true;
        }
        return false;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(String.format("%02x ", b));
        }
        return builder.toString().trim();
    }
}
