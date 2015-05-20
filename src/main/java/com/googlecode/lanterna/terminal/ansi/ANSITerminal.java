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
 * Copyright (C) 2010-2015 Martin
 */
package com.googlecode.lanterna.terminal.ansi;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.input.DefaultKeyDecodingProfile;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyDecodingProfile;
import com.googlecode.lanterna.terminal.ExtendedTerminal;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * Class containing graphics code for ANSI compliant text terminals and terminal emulators. All the methods inside of
 * this class uses ANSI escape codes written to the underlying output stream.
 *
 * @see <a href="http://en.wikipedia.org/wiki/ANSI_escape_code">Wikipedia</a>
 * @author Martin
 */
public abstract class ANSITerminal extends StreamBasedTerminal implements ExtendedTerminal {

    private boolean inPrivateMode;

    @SuppressWarnings("WeakerAccess")
    protected ANSITerminal(InputStream terminalInput, OutputStream terminalOutput, Charset terminalCharset) {
        super(terminalInput, terminalOutput, terminalCharset);
        this.inPrivateMode = false;
        addKeyDecodingProfile(getDefaultKeyDecodingProfile());
    }

    /**
     * This method can be overridden in a custom terminal implementation to change the default key decoders.
     * @return The KeyDecodingProfile used by the terminal when translating character sequences to keystrokes
     */
    protected KeyDecodingProfile getDefaultKeyDecodingProfile() {
        return new DefaultKeyDecodingProfile();
    }

    private void writeCSISequenceToTerminal(byte... tail) throws IOException {
        byte[] completeSequence = new byte[tail.length + 2];
        completeSequence[0] = (byte)0x1b;
        completeSequence[1] = (byte)'[';
        System.arraycopy(tail, 0, completeSequence, 2, tail.length);
        writeToTerminal(completeSequence);
    }

    private void writeSGRSequenceToTerminal(byte... sgrParameters) throws IOException {
        byte[] completeSequence = new byte[sgrParameters.length + 3];
        completeSequence[0] = (byte)0x1b;
        completeSequence[1] = (byte)'[';
        completeSequence[completeSequence.length - 1] = (byte)'m';
        System.arraycopy(sgrParameters, 0, completeSequence, 2, sgrParameters.length);
        writeToTerminal(completeSequence);
    }

    private void writeOSCSequenceToTerminal(byte... tail) throws IOException {
        byte[] completeSequence = new byte[tail.length + 2];
        completeSequence[0] = (byte)0x1b;
        completeSequence[1] = (byte)']';
        System.arraycopy(tail, 0, completeSequence, 2, tail.length);
        writeToTerminal(completeSequence);
    }

    @Override
    public TerminalSize getTerminalSize() throws IOException {
        saveCursorPosition();
        setCursorPosition(5000, 5000);
        reportPosition();
        restoreCursorPosition();
        return waitForTerminalSizeReport();
    }

    @Override
    public void setTerminalSize(int columns, int rows) throws IOException {
        writeCSISequenceToTerminal(("8;" + rows + ";" + columns + "t").getBytes());

        //We can't trust that the previous call was honoured by the terminal so force a re-query here, which will
        //trigger a resize event if one actually took place
        getTerminalSize();
    }

    @Override
    public void setTitle(String title) throws IOException {
        //The bell character is our 'null terminator', make sure there's none in the title
        title = title.replace("\007", "");
        writeOSCSequenceToTerminal(("2;" + title + "\007").getBytes());
    }

    @Override
    public void setForegroundColor(TextColor color) throws IOException {
        writeSGRSequenceToTerminal(color.getForegroundSGRSequence());
    }

    @Override
    public void setBackgroundColor(TextColor color) throws IOException {
        writeSGRSequenceToTerminal(color.getBackgroundSGRSequence());
    }

    @Override
    public void enableSGR(SGR sgr) throws IOException {
        switch(sgr) {
            case BLINK:
                writeCSISequenceToTerminal((byte) '5', (byte) 'm');
                break;
            case BOLD:
                writeCSISequenceToTerminal((byte) '1', (byte) 'm');
                break;
            case BORDERED:
                writeCSISequenceToTerminal((byte) '5', (byte) '1', (byte) 'm');
                break;
            case CIRCLED:
                writeCSISequenceToTerminal((byte) '5', (byte) '2', (byte) 'm');
                break;
            case CROSSED_OUT:
                writeCSISequenceToTerminal((byte) '9', (byte) 'm');
                break;
            case FRAKTUR:
                writeCSISequenceToTerminal((byte) '2', (byte) '0', (byte) 'm');
                break;
            case REVERSE:
                writeCSISequenceToTerminal((byte) '7', (byte) 'm');
                break;
            case UNDERLINE:
                writeCSISequenceToTerminal((byte) '4', (byte) 'm');
                break;
        }
    }

    @Override
    public void disableSGR(SGR sgr) throws IOException {
        switch(sgr) {
            case BLINK:
                writeCSISequenceToTerminal((byte) '2', (byte) '5', (byte) 'm');
                break;
            case BOLD:
                writeCSISequenceToTerminal((byte) '2', (byte) '2', (byte) 'm');
                break;
            case BORDERED:
                writeCSISequenceToTerminal((byte) '5', (byte) '4', (byte) 'm');
                break;
            case CIRCLED:
                writeCSISequenceToTerminal((byte) '5', (byte) '4', (byte) 'm');
                break;
            case CROSSED_OUT:
                writeCSISequenceToTerminal((byte) '2', (byte) '9', (byte) 'm');
                break;
            case FRAKTUR:
                writeCSISequenceToTerminal((byte) '2', (byte) '3', (byte) 'm');
                break;
            case REVERSE:
                writeCSISequenceToTerminal((byte) '2', (byte) '7', (byte) 'm');
                break;
            case UNDERLINE:
                writeCSISequenceToTerminal((byte) '2', (byte) '4', (byte) 'm');
                break;
        }
    }

    @Override
    public void resetColorAndSGR() throws IOException {
        writeCSISequenceToTerminal((byte) '0', (byte) 'm');
    }

    @Override
    public void clearScreen() throws IOException {
        writeCSISequenceToTerminal((byte) '2', (byte) 'J');
    }

    @Override
    public void enterPrivateMode() throws IOException {
        if(inPrivateMode) {
            throw new IllegalStateException("Cannot call enterPrivateMode() when already in private mode");
        }
        writeCSISequenceToTerminal((byte) '?', (byte) '1', (byte) '0', (byte) '4', (byte) '9', (byte) 'h');
        inPrivateMode = true;
    }

    @Override
    public void exitPrivateMode() throws IOException {
        if(!inPrivateMode) {
            throw new IllegalStateException("Cannot call exitPrivateMode() when not in private mode");
        }
        resetColorAndSGR();
        setCursorVisible(true);
        writeCSISequenceToTerminal((byte) '?', (byte) '1', (byte) '0', (byte) '4', (byte) '9', (byte) 'l');
        inPrivateMode = false;
    }

    @Override
    public void setCursorPosition(int x, int y) throws IOException {
        writeCSISequenceToTerminal(((y + 1) + ";" + (x + 1) + "H").getBytes());
    }

    @Override
    public void setCursorVisible(boolean visible) throws IOException {
        writeCSISequenceToTerminal(("?25" + (visible ? "h" : "l")).getBytes());
    }

    @Override
    public void pushTitle() throws IOException {
        throw new NotImplementedException();
    }

    @Override
    public void popTitle() throws IOException {
        throw new NotImplementedException();
    }

    @Override
    public void iconify() throws IOException {
        throw new NotImplementedException();
    }

    @Override
    public void deiconify() throws IOException {
        throw new NotImplementedException();
    }

    @Override
    public void maximize() throws IOException {
        throw new NotImplementedException();
    }

    @Override
    public void unmaximize() throws IOException {
        throw new NotImplementedException();
    }

    @Override
    public void setMouseMovementCapturingEnabled(boolean enabled) throws IOException {
        throw new NotImplementedException();
    }

    @Override
    public void setMouseClicksCapturingEnabled(boolean enable) throws IOException {
        throw new NotImplementedException();
    }

    /**
     * Method to test if the terminal (as far as the library knows) is in private mode.
     *
     * @return True if there has been a call to enterPrivateMode() but not yet exitPrivateMode()
     */
    boolean isInPrivateMode() {
        return inPrivateMode;
    }

    /**
     * Synchronize with writerMutex externally!!!
     */
    void reportPosition() throws IOException {
        writeCSISequenceToTerminal("6n".getBytes());
    }

    /**
     * Synchronize with writerMutex externally!!!
     */
    void restoreCursorPosition() throws IOException {
        writeCSISequenceToTerminal("u".getBytes());
    }

    /**
     * Synchronize with writerMutex externally!!!
     */
    void saveCursorPosition() throws IOException {
        writeCSISequenceToTerminal("s".getBytes());
    }
}
