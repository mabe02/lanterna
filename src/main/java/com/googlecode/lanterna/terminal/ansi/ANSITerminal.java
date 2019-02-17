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
 * Copyright (C) 2010-2019 Martin Berglund
 */
package com.googlecode.lanterna.terminal.ansi;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.input.*;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.terminal.ExtendedTerminal;
import com.googlecode.lanterna.terminal.MouseCaptureMode;

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

    private MouseCaptureMode requestedMouseCaptureMode;
    private MouseCaptureMode mouseCaptureMode;
    private boolean inPrivateMode;

    @SuppressWarnings("WeakerAccess")
    protected ANSITerminal(
            InputStream terminalInput,
            OutputStream terminalOutput,
            Charset terminalCharset) {

        super(terminalInput, terminalOutput, terminalCharset);
        this.inPrivateMode = false;
        this.requestedMouseCaptureMode = null;
        this.mouseCaptureMode = null;
        getInputDecoder().addProfile(getDefaultKeyDecodingProfile());
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

    // Final because we handle the onResized logic here; extending classes should override #findTerminalSize instead
    @Override
    public final synchronized TerminalSize getTerminalSize() throws IOException {
        TerminalSize size = findTerminalSize();
        onResized(size);
        return size;
    }

    protected TerminalSize findTerminalSize() throws IOException {
        saveCursorPosition();
        setCursorPosition(5000, 5000);
        resetMemorizedCursorPosition();
        reportPosition();
        restoreCursorPosition();
        TerminalPosition terminalPosition = waitForCursorPositionReport();
        if (terminalPosition == null) {
            terminalPosition = new TerminalPosition(80,24);
        }
        return new TerminalSize(terminalPosition.getColumn(), terminalPosition.getRow());
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
            case ITALIC:
                writeCSISequenceToTerminal((byte) '3', (byte) 'm');
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
            case ITALIC:
                writeCSISequenceToTerminal((byte) '2', (byte) '3', (byte) 'm');
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
        if (requestedMouseCaptureMode != null) {
            this.mouseCaptureMode = requestedMouseCaptureMode;
            updateMouseCaptureMode(this.mouseCaptureMode, 'h');
        }
        flush();
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
        if (null != mouseCaptureMode) {
            updateMouseCaptureMode(this.mouseCaptureMode, 'l');
            this.mouseCaptureMode = null;
        }
        flush();
        inPrivateMode = false;
    }

    @Override
    public void close() throws IOException {
        if(isInPrivateMode()) {
            exitPrivateMode();
        }
        super.close();
    }

    @Override
    public void setCursorPosition(int x, int y) throws IOException {
        writeCSISequenceToTerminal(((y + 1) + ";" + (x + 1) + "H").getBytes());
    }

    @Override
    public void setCursorPosition(TerminalPosition position) throws IOException {
        setCursorPosition(position.getColumn(), position.getRow());
    }

    @Override
    public synchronized TerminalPosition getCursorPosition() throws IOException {
        resetMemorizedCursorPosition();
        reportPosition();

        // ANSI terminal positions are 1-indexed so top-left corner is 1x1 instead of 0x0, that's why we need to adjust it here
        TerminalPosition terminalPosition = waitForCursorPositionReport();
        if (terminalPosition == null) {
            terminalPosition = TerminalPosition.OFFSET_1x1;
        }
        return terminalPosition.withRelative(-1, -1);
    }

    @Override
    public void setCursorVisible(boolean visible) throws IOException {
        writeCSISequenceToTerminal(("?25" + (visible ? "h" : "l")).getBytes());
    }

    @Override
    public KeyStroke readInput() throws IOException {
        KeyStroke keyStroke;
        do {
            // KeyStroke may because null by filterMouseEvents, so that's why we have the while(true) loop here
            keyStroke = filterMouseEvents(super.readInput());
        } while(keyStroke == null);
        return keyStroke;
    }

    @Override
    public KeyStroke pollInput() throws IOException {
        return filterMouseEvents(super.pollInput());
    }

    private KeyStroke filterMouseEvents(KeyStroke keyStroke) {
        //Remove bad input events from terminals that are not following the xterm protocol properly
        if(keyStroke == null || keyStroke.getKeyType() != KeyType.MouseEvent) {
            return keyStroke;
        }

        MouseAction mouseAction = (MouseAction)keyStroke;
        switch(mouseAction.getActionType()) {
            case CLICK_RELEASE:
                if(mouseCaptureMode == MouseCaptureMode.CLICK) {
                    return null;
                }
                break;
            case DRAG:
                if(mouseCaptureMode == MouseCaptureMode.CLICK ||
                        mouseCaptureMode == MouseCaptureMode.CLICK_RELEASE) {
                    return null;
                }
                break;
            case MOVE:
                if(mouseCaptureMode == MouseCaptureMode.CLICK ||
                        mouseCaptureMode == MouseCaptureMode.CLICK_RELEASE ||
                        mouseCaptureMode == MouseCaptureMode.CLICK_RELEASE_DRAG) {
                    return null;
                }
                break;
            default:
        }
        return mouseAction;
    }

    @Override
    public void pushTitle() throws IOException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void popTitle() throws IOException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void iconify() throws IOException {
        writeCSISequenceToTerminal((byte)'2', (byte)'t');
    }

    @Override
    public void deiconify() throws IOException {
        writeCSISequenceToTerminal((byte)'1', (byte)'t');
    }

    @Override
    public void maximize() throws IOException {
        writeCSISequenceToTerminal((byte)'9', (byte)';', (byte)'1', (byte)'t');
    }

    @Override
    public void unmaximize() throws IOException {
        writeCSISequenceToTerminal((byte)'9', (byte)';', (byte)'0', (byte)'t');
    }

    private void updateMouseCaptureMode(MouseCaptureMode mouseCaptureMode, char l_or_h) throws IOException {
        if (mouseCaptureMode == null) { return; }

        switch(mouseCaptureMode) {
        case CLICK:
            writeCSISequenceToTerminal((byte)'?', (byte)'9', (byte)l_or_h);
            break;
        case CLICK_RELEASE:
            writeCSISequenceToTerminal((byte)'?', (byte)'1', (byte)'0', (byte)'0', (byte)'0', (byte)l_or_h);
            break;
        case CLICK_RELEASE_DRAG:
            writeCSISequenceToTerminal((byte)'?', (byte)'1', (byte)'0', (byte)'0', (byte)'2', (byte)l_or_h);
            break;
        case CLICK_RELEASE_DRAG_MOVE:
            writeCSISequenceToTerminal((byte)'?', (byte)'1', (byte)'0', (byte)'0', (byte)'3', (byte)l_or_h);
            break;
        }
        if(getCharset().equals(Charset.forName("UTF-8"))) {
            writeCSISequenceToTerminal((byte)'?', (byte)'1', (byte)'0', (byte)'0', (byte)'5', (byte)l_or_h);
        }
    }

    @Override
    public void setMouseCaptureMode(MouseCaptureMode mouseCaptureMode) throws IOException {
        requestedMouseCaptureMode = mouseCaptureMode;
        if (inPrivateMode && requestedMouseCaptureMode != this.mouseCaptureMode) {
            updateMouseCaptureMode(this.mouseCaptureMode, 'l');
            this.mouseCaptureMode = requestedMouseCaptureMode;
            updateMouseCaptureMode(this.mouseCaptureMode, 'h');
        }
    }

    @Override
    public void scrollLines(int firstLine, int lastLine, int distance) throws IOException {
        final String CSI = "\033[";

        // some sanity checks:
        if (distance == 0) { return; }
        if (firstLine < 0) { firstLine = 0; }
        if (lastLine < firstLine) { return; }
        StringBuilder sb = new StringBuilder();

        // define range:
        sb.append(CSI).append(firstLine+1)
          .append(';').append(lastLine+1).append('r');

        // place cursor on line to scroll away from:
        int target = distance > 0 ? lastLine : firstLine;
        sb.append(CSI).append(target+1).append(";1H");

        // do scroll:
        if (distance > 0) {
            int num = Math.min( distance, lastLine - firstLine + 1);
            for (int i = 0; i < num; i++) { sb.append('\n'); }
        } else { // distance < 0
            int num = Math.min( -distance, lastLine - firstLine + 1);
            for (int i = 0; i < num; i++) { sb.append("\033M"); }
        }

        // reset range:
        sb.append(CSI).append('r');

        // off we go!
        writeToTerminal(sb.toString().getBytes());
    }

    /**
     * Method to test if the terminal (as far as the library knows) is in private mode.
     *
     * @return True if there has been a call to enterPrivateMode() but not yet exitPrivateMode()
     */
    boolean isInPrivateMode() {
        return inPrivateMode;
    }

    void reportPosition() throws IOException {
        writeCSISequenceToTerminal("6n".getBytes());
    }

    void restoreCursorPosition() throws IOException {
        writeCSISequenceToTerminal("u".getBytes());
    }

    void saveCursorPosition() throws IOException {
        writeCSISequenceToTerminal("s".getBytes());
    }
}
