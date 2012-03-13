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
 * Copyright (C) 2010-2011 mabe02
 */

package org.lantern.terminal;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.lantern.LanternException;
import org.lantern.input.InputDecoder;
import org.lantern.input.Key;
import org.lantern.input.KeyMappingProfile;

/**
 * Containing a common implementation for most terminal
 * @author mabe02
 */
public abstract class AbstractTerminal implements Terminal
{
    private final Charset terminalCharset;
    private final OutputStream terminalOutput;
    private final InputDecoder inputDecoder;
    private final List<ResizeListener> resizeListeners;
    protected TerminalSize lastKnownSize;

    public AbstractTerminal(final InputStream terminalInput, final OutputStream terminalOutput,
            final Charset terminalCharset)
    {
        this.terminalOutput = terminalOutput;
        this.terminalCharset = terminalCharset;
        this.inputDecoder = new InputDecoder(new InputStreamReader(terminalInput, terminalCharset));
        this.resizeListeners = new ArrayList<ResizeListener>();
        this.lastKnownSize = null;
    }

    //Allow subclasses (that's susposted to know what they're doing) to write directly to the terminal
    protected synchronized void writeToTerminal(final byte... bytes) throws LanternException
    {
        try {
            terminalOutput.write(bytes);
        }
        catch(IOException e) {
            throw new LanternException(e);
        }
    }

    public void putCharacter(char c) throws LanternException
    {
        writeToTerminal(translateCharacter(c));
    }

    public Key readInput() throws LanternException
    {
        Key key = inputDecoder.getNextCharacter();
        if(key != null && key.getKind() == Key.Kind.CursorLocation) {
            TerminalPosition reportedTerminalPosition = inputDecoder.getLastReportedTerminalPosition();
            if(reportedTerminalPosition != null) {
                TerminalSize newSize = new TerminalSize(reportedTerminalPosition.getColumn(),
                                                            reportedTerminalPosition.getRow());

                if(lastKnownSize == null || !newSize.equals(lastKnownSize)) {
                    lastKnownSize = newSize;
                    onResized();
                }
            }
            return readInput();
        }
        else
            return key;
    }

    public void addInputProfile(KeyMappingProfile profile)
    {
        inputDecoder.addProfile(profile);
    }

    public void setCBreak(boolean cbreakOn) throws LanternException
    {
        TerminalStatus.setCBreak(cbreakOn);
    }

    public void setEcho(boolean echoOn) throws LanternException
    {
        TerminalStatus.setKeyEcho(echoOn);
    }

    public void addResizeListener(ResizeListener listener)
    {
        if(listener != null)
            resizeListeners.add(listener);
    }

    public void removeResizeListener(ResizeListener listener)
    {
        if(listener != null)
            resizeListeners.remove(listener);
    }
    
    public void hackSendFakeResize() throws LanternException
    {
        onResized();
    }

    public void flush() throws LanternException {
        try {
            terminalOutput.flush();
        }
        catch(IOException e) {
            throw new LanternException(e);
        }
    }

    protected synchronized void onResized() throws LanternException
    {
        TerminalSize size = queryTerminalSize();
        for(ResizeListener resizeListener: resizeListeners)
            resizeListener.onResized(size);
    }

    private byte[] translateCharacter(char input)
    {
        if("UTF-8".equals(terminalCharset.toString()))
            return convertToCharset(input);

        //Convert ACS to ordinary terminal codes
        switch(input)
        {
            case ACS.ARROW_DOWN:
                return convertToVT100('v');

            case ACS.ARROW_LEFT:
                return convertToVT100('<');

            case ACS.ARROW_RIGHT:
                return convertToVT100('>');

            case ACS.ARROW_UP:
                return convertToVT100('^');

            case ACS.BLOCK_DENSE:
            case ACS.BLOCK_MIDDLE:
            case ACS.BLOCK_SOLID:
            case ACS.BLOCK_SPARSE:
                return convertToVT100((char)97);

            case ACS.HEART:
            case ACS.CLUB:
            case ACS.SPADES:
                return convertToVT100('?');

            case ACS.FACE_BLACK:
            case ACS.FACE_WHITE:
            case ACS.DIAMOND:
                return convertToVT100((char)96);

            case ACS.DOT:
                return convertToVT100((char)102);

            case ACS.DOUBLE_LINE_CROSS:
            case ACS.SINGLE_LINE_CROSS:
                return convertToVT100((char)110);

            case ACS.DOUBLE_LINE_HORIZONTAL:
            case ACS.SINGLE_LINE_HORIZONTAL:
                return convertToVT100((char)113);

            case ACS.DOUBLE_LINE_LOW_LEFT_CORNER:
            case ACS.SINGLE_LINE_LOW_LEFT_CORNER:
                return convertToVT100((char)109);

            case ACS.DOUBLE_LINE_LOW_RIGHT_CORNER:
            case ACS.SINGLE_LINE_LOW_RIGHT_CORNER:
                return convertToVT100((char)106);

            case ACS.DOUBLE_LINE_T_DOWN:
            case ACS.SINGLE_LINE_T_DOWN:
            case ACS.DOUBLE_LINE_T_SINGLE_DOWN:
            case ACS.SINGLE_LINE_T_DOUBLE_DOWN:
                return convertToVT100((char)119);

            case ACS.DOUBLE_LINE_T_LEFT:
            case ACS.SINGLE_LINE_T_LEFT:
            case ACS.DOUBLE_LINE_T_SINGLE_LEFT:
            case ACS.SINGLE_LINE_T_DOUBLE_LEFT:
                return convertToVT100((char)117);

            case ACS.DOUBLE_LINE_T_RIGHT:
            case ACS.SINGLE_LINE_T_RIGHT:
            case ACS.DOUBLE_LINE_T_SINGLE_RIGHT:
            case ACS.SINGLE_LINE_T_DOUBLE_RIGHT:
                return convertToVT100((char)116);

            case ACS.DOUBLE_LINE_T_UP:
            case ACS.SINGLE_LINE_T_UP:
            case ACS.DOUBLE_LINE_T_SINGLE_UP:
            case ACS.SINGLE_LINE_T_DOUBLE_UP:
                return convertToVT100((char)118);

            case ACS.DOUBLE_LINE_UP_LEFT_CORNER:
            case ACS.SINGLE_LINE_UP_LEFT_CORNER:
                return convertToVT100((char)108);

            case ACS.DOUBLE_LINE_UP_RIGHT_CORNER:
            case ACS.SINGLE_LINE_UP_RIGHT_CORNER:
                return convertToVT100((char)107);

            case ACS.DOUBLE_LINE_VERTICAL:
            case ACS.SINGLE_LINE_VERTICAL:
                return convertToVT100((char)120);

            default:
                return convertToCharset(input);
        }
    }

    private byte[] convertToVT100(char code)
    {
        //Warning! This might be terminal type specific!!!!
        //So far it's worked everywhere I've tried it (xterm, gnome-terminal, putty)
        return new byte[] {
            0x1B, 0x28, 0x30,   //Enter alternative character set
            (byte)code,
            0x1B, 0x28, 0x42    //Exit alternative character set
        };
    }

    private byte[] convertToCharset(char input)
    {
        //TODO: This is a silly way to do it, improve?
        final char[] buffer = new char[1];
        buffer[0] = input;
        return terminalCharset.encode(CharBuffer.wrap(buffer)).array();
    }
}
