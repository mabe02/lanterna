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
 * Copyright (C) 2010-2014 Martin
 */
package com.googlecode.lanterna.terminal.ansi;

import com.googlecode.lanterna.input.DefaultKeyDecodingProfile;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.terminal.TextColor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * Class containing common code for ANSI compliant text terminals and terminal emulators.
 *
 * @see <a href="http://en.wikipedia.org/wiki/ANSI_escape_code">Wikipedia</a>
 * @author Martin
 */
public abstract class ANSITerminal extends StreamBasedTerminal {

    private boolean inPrivateMode;

    public ANSITerminal(InputStream terminalInput, OutputStream terminalOutput, Charset terminalCharset) {
        super(terminalInput, terminalOutput, terminalCharset);
        this.inPrivateMode = false;
        addKeyDecodingProfile(new DefaultKeyDecodingProfile());
    }

    private void writeCSISequenctToTerminal(byte... tail) throws IOException {
        byte[] completeSequence = new byte[tail.length + 2];
        completeSequence[0] = (byte)0x1b;
        completeSequence[1] = (byte)'[';
        System.arraycopy(tail, 0, completeSequence, 2, tail.length);
        writeToTerminal(completeSequence);
    }

    @Override
    public TerminalSize getTerminalSize() throws IOException {
        saveCursorPosition();
        moveCursor(5000, 5000);
        reportPosition();
        restoreCursorPosition();
        return waitForTerminalSizeReport(1000); //Wait 1 second for the terminal size report to come, is this reasonable?
    }

    @Override
    public void applyBackgroundColor(TextColor.ANSI color) throws IOException {
        writeCSISequenctToTerminal((byte) '4', (byte) ((color.getIndex() + "").charAt(0)), (byte) 'm');
    }

    @Override
    public void applyBackgroundColor(int r, int g, int b) throws IOException {
        if(r < 0 || r > 255) {
            throw new IllegalArgumentException("applyForegroundColor: r is outside of valid range (0-255)");
        }
        if(g < 0 || g > 255) {
            throw new IllegalArgumentException("applyForegroundColor: g is outside of valid range (0-255)");
        }
        if(b < 0 || b > 255) {
            throw new IllegalArgumentException("applyForegroundColor: b is outside of valid range (0-255)");
        }
        writeCSISequenctToTerminal(("48;2;" + r + ";" + g + ";" + b + "m").getBytes());
    }

    @Override
    public void applyBackgroundColor(int index) throws IOException {
        if(index < 0 || index > 255) {
            throw new IllegalArgumentException("applyBackgroundColor: index is outside of valid range (0-255)");
        }

        writeCSISequenctToTerminal(("48;5;" + index + "m").getBytes());
    }

    @Override
    public void applyForegroundColor(TextColor.ANSI color) throws IOException {
        writeCSISequenctToTerminal((byte) '3', (byte) ((color.getIndex() + "").charAt(0)), (byte) 'm');
    }

    @Override
    public void applyForegroundColor(int r, int g, int b) throws IOException {
        if(r < 0 || r > 255) {
            throw new IllegalArgumentException("applyForegroundColor: r is outside of valid range (0-255)");
        }
        if(g < 0 || g > 255) {
            throw new IllegalArgumentException("applyForegroundColor: g is outside of valid range (0-255)");
        }
        if(b < 0 || b > 255) {
            throw new IllegalArgumentException("applyForegroundColor: b is outside of valid range (0-255)");
        }
        writeCSISequenctToTerminal(("38;2;" + r + ";" + g + ";" + b + "m").getBytes());
    }

    @Override
    public void applyForegroundColor(int index) throws IOException {
        if(index < 0 || index > 255) {
            throw new IllegalArgumentException("applyForegroundColor: index is outside of valid range (0-255)");
        }
        writeCSISequenctToTerminal(("38;5;" + index + "m").getBytes());
    }

    @Override
    public void enableSGR(SGR sgr) throws IOException {
        switch(sgr) {
            case BLINK:
                writeCSISequenctToTerminal((byte) '5', (byte) 'm');
                break;
            case BOLD:
                writeCSISequenctToTerminal((byte) '1', (byte) 'm');
                break;
            case BORDERED:
                writeCSISequenctToTerminal((byte) '5', (byte) '1', (byte) 'm');
                break;
            case CIRCLED:
                writeCSISequenctToTerminal((byte) '5', (byte) '2', (byte) 'm');
                break;
            case CROSSEDOUT:
                writeCSISequenctToTerminal((byte) '9', (byte) 'm');
                break;
            case FRAKTUR:
                writeCSISequenctToTerminal((byte) '2', (byte) '0', (byte) 'm');
                break;
            case REVERSE:
                writeCSISequenctToTerminal((byte) '7', (byte) 'm');
                break;
            case UNDERLINE:
                writeCSISequenctToTerminal((byte) '4', (byte) 'm');
                break;
        }
    }

    @Override
    public void disableSGR(SGR sgr) throws IOException {
        switch(sgr) {
            case BLINK:
                writeToTerminal((byte) '2', (byte) '5', (byte) 'm');
                break;
            case BOLD:
                writeToTerminal((byte) '2', (byte) '2', (byte) 'm');
                break;
            case BORDERED:
                writeToTerminal((byte) '5', (byte) '4', (byte) 'm');
                break;
            case CIRCLED:
                writeToTerminal((byte) '5', (byte) '4', (byte) 'm');
                break;
            case CROSSEDOUT:
                writeToTerminal((byte) '2', (byte) '9', (byte) 'm');
                break;
            case FRAKTUR:
                writeToTerminal((byte) '2', (byte) '3', (byte) 'm');
                break;
            case REVERSE:
                writeToTerminal((byte) '2', (byte) '7', (byte) 'm');
                break;
            case UNDERLINE:
                writeToTerminal((byte) '2', (byte) '4', (byte) 'm');
                break;
        }
    }

    @Override
    public void resetAllSGR() throws IOException {
        writeCSISequenctToTerminal((byte) '0', (byte) 'm');
    }

    @Override
    public void clearScreen() throws IOException {
        writeCSISequenctToTerminal((byte) '2', (byte) 'J');
    }

    @Override
    public void enterPrivateMode() throws IOException {
        if(inPrivateMode) {
            throw new IllegalStateException("Cannot call enterPrivateMode() when already in private mode");
        }
        writeCSISequenctToTerminal((byte) '?', (byte) '1', (byte) '0', (byte) '4', (byte) '9', (byte) 'h');
        inPrivateMode = true;
    }

    @Override
    public void exitPrivateMode() throws IOException {
        if(!inPrivateMode) {
            throw new IllegalStateException("Cannot call exitPrivateMode() when not in private mode");
        }
        resetAllSGR();
        setCursorVisible(true);
        writeCSISequenctToTerminal((byte) '?', (byte) '1', (byte) '0', (byte) '4', (byte) '9', (byte) 'l');
        inPrivateMode = false;
    }

    @Override
    public void moveCursor(int x, int y) throws IOException {
        writeCSISequenctToTerminal(((y + 1) + ";" + (x + 1) + "H").getBytes());
    }

    @Override
    public void setCursorVisible(boolean visible) throws IOException {
        writeCSISequenctToTerminal(("?25" + (visible ? "h" : "l")).getBytes());
    }

    /**
     * Method to test if the terminal (as far as the library knows) is in private mode.
     *
     * @return True if there has been a call to enterPrivateMode() but not yet exitPrivateMode()
     */
    public boolean isInPrivateMode() {
        return inPrivateMode;
    }

    /**
     * Synchronize with writerMutex externally!!!
     */
    protected void reportPosition() throws IOException {
        writeCSISequenctToTerminal("6n".getBytes());
    }

    /**
     * Synchronize with writerMutex externally!!!
     */
    protected void restoreCursorPosition() throws IOException {
        writeCSISequenctToTerminal("u".getBytes());
    }

    /**
     * Synchronize with writerMutex externally!!!
     */
    protected void saveCursorPosition() throws IOException {
        writeCSISequenctToTerminal("s".getBytes());
    }
}
