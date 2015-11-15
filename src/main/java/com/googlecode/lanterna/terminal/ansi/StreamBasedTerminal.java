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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.input.InputDecoder;
import com.googlecode.lanterna.input.KeyDecodingProfile;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.ScreenInfoAction;
import com.googlecode.lanterna.input.ScreenInfoCharacterPattern;
import com.googlecode.lanterna.terminal.AbstractTerminal;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

/**
 * An abstract terminal implementing functionality for terminals using OutputStream/InputStream. You can extend from
 * this class if your terminal implementation is using standard input and standard output but not ANSI escape codes (in
 * which case you should extend ANSITerminal). This class also contains some automatic UTF-8 to VT100 character
 * conversion when the terminal is not set to read UTF-8.
 *
 * @author Martin
 */
public abstract class StreamBasedTerminal extends AbstractTerminal {

    private static final Charset UTF8_REFERENCE = Charset.forName("UTF-8");

    private final InputStream terminalInput;
    private final OutputStream terminalOutput;
    private final Charset terminalCharset;

    private final InputDecoder inputDecoder;
    private final Queue<KeyStroke> keyQueue;
    private final Object readMutex;
    
    @SuppressWarnings("WeakerAccess")
    public StreamBasedTerminal(InputStream terminalInput, OutputStream terminalOutput, Charset terminalCharset) {
        this.terminalInput = terminalInput;
        this.terminalOutput = terminalOutput;
        if(terminalCharset == null) {
            this.terminalCharset = Charset.defaultCharset();
        }
        else {
            this.terminalCharset = terminalCharset;
        }
        this.inputDecoder = new InputDecoder(new InputStreamReader(this.terminalInput, this.terminalCharset));
        this.keyQueue = new LinkedList<KeyStroke>();
        this.readMutex = new Object();
        //noinspection ConstantConditions
    }

    /**
     * Outputs a single character to the terminal output stream, translating any UTF-8 graphical symbol if necessary
     *
     * @param c Character to write to the output stream
     * @throws java.io.IOException If there was an underlying I/O error
     */
    @Override
    public void putCharacter(char c) throws IOException {
        writeToTerminal(translateCharacter(c));
    }

    /**
     * This method will write a list of bytes directly to the output stream of the terminal.
     * @param bytes Bytes to write to the terminal (synchronized)
     * @throws java.io.IOException If there was an underlying I/O error
     */
    @SuppressWarnings("WeakerAccess")
    protected void writeToTerminal(byte... bytes) throws IOException {
        synchronized(terminalOutput) {
            terminalOutput.write(bytes);
        }
    }

    @Override
    public byte[] enquireTerminal(int timeout, TimeUnit timeoutTimeUnit) throws IOException {
        synchronized(terminalOutput) {
            terminalOutput.write(5);    //ENQ
            flush();
        }
        
        //Wait for input
        long startTime = System.currentTimeMillis();
        while(terminalInput.available() == 0) {
            if(System.currentTimeMillis() - startTime > timeoutTimeUnit.toMillis(timeout)) {
                return new byte[0];
            }
            try { 
                Thread.sleep(1); 
            } 
            catch(InterruptedException e) {
                return new byte[0];
            }
        }
        
        //We have at least one character, read as far as we can and return
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        while(terminalInput.available() > 0) {
            buffer.write(terminalInput.read());
        }
        return buffer.toByteArray();
    }
    
    /**
     * Adds a KeyDecodingProfile to be used when converting raw user input characters to {@code Key} objects.
     *
     * @see KeyDecodingProfile
     * @param profile Decoding profile to add
     * @deprecated Use {@code getInputDecoder().addProfile(profile)} instead
     */
    @Deprecated
    @SuppressWarnings("WeakerAccess")
    public void addKeyDecodingProfile(KeyDecodingProfile profile) {
        inputDecoder.addProfile(profile);
    }

    public InputDecoder getInputDecoder() {
        return inputDecoder;
    }

    @SuppressWarnings("ConstantConditions")
    TerminalSize waitForTerminalSizeReport() throws IOException {
        long startTime = System.currentTimeMillis();
        synchronized(readMutex) {
            while(true) {
                KeyStroke key = inputDecoder.getNextCharacter(false);
                if(key == null) {
                    if(System.currentTimeMillis() - startTime > 1000) {  //Wait 1 second for the terminal size report to come, is this reasonable?
                        throw new IOException(
                                "Timeout while waiting for terminal size report! Your terminal may have refused to go into cbreak mode.");
                    }
                    try {
                        Thread.sleep(1);
                    }
                    catch(InterruptedException ignored) {}
                    continue;
                }

                // check both: real ScreenInfoActions and F3 keystrokes with modifiers:
                ScreenInfoAction report = ScreenInfoCharacterPattern.tryToAdopt(key);
                if (report == null) {
                    keyQueue.add(key);
                }
                else {
                    TerminalPosition reportedTerminalPosition = report.getPosition();
                    onResized(reportedTerminalPosition.getColumn(), reportedTerminalPosition.getRow());
                    return new TerminalSize(reportedTerminalPosition.getColumn(), reportedTerminalPosition.getRow());
                }
            }
        }
    }

    @Override
    public KeyStroke pollInput() throws IOException {
        return readInput(false);
    }

    @Override
    public KeyStroke readInput() throws IOException {
        return readInput(true);
    }

    private KeyStroke readInput(boolean blocking) throws IOException {
        synchronized(readMutex) {
            if(!keyQueue.isEmpty()) {
                return keyQueue.poll();
            }
            KeyStroke key = inputDecoder.getNextCharacter(blocking);
            if (key instanceof ScreenInfoAction) {
                TerminalPosition reportedTerminalPosition = ((ScreenInfoAction)key).getPosition();
                onResized(reportedTerminalPosition.getColumn(), reportedTerminalPosition.getRow());
                return pollInput();
            } else {
                return key;
            }
        }
    }

    @Override
    public void flush() throws IOException {
        synchronized(terminalOutput) {
            terminalOutput.flush();
        }
    }

    protected Charset getCharset() {
        return terminalCharset;
    }

    @SuppressWarnings("WeakerAccess")
    protected byte[] translateCharacter(char input) {
        if(UTF8_REFERENCE != null && UTF8_REFERENCE == terminalCharset) {
            return convertToCharset(input);
        }
        //Convert ACS to ordinary terminal codes
        switch(input) {
            case Symbols.ARROW_DOWN:
                return convertToVT100('v');
            case Symbols.ARROW_LEFT:
                return convertToVT100('<');
            case Symbols.ARROW_RIGHT:
                return convertToVT100('>');
            case Symbols.ARROW_UP:
                return convertToVT100('^');
            case Symbols.BLOCK_DENSE:
            case Symbols.BLOCK_MIDDLE:
            case Symbols.BLOCK_SOLID:
            case Symbols.BLOCK_SPARSE:
                return convertToVT100((char) 97);
            case Symbols.HEART:
            case Symbols.CLUB:
            case Symbols.SPADES:
                return convertToVT100('?');
            case Symbols.FACE_BLACK:
            case Symbols.FACE_WHITE:
            case Symbols.DIAMOND:
                return convertToVT100((char) 96);
            case Symbols.BULLET:
                return convertToVT100((char) 102);
            case Symbols.DOUBLE_LINE_CROSS:
            case Symbols.SINGLE_LINE_CROSS:
                return convertToVT100((char) 110);
            case Symbols.DOUBLE_LINE_HORIZONTAL:
            case Symbols.SINGLE_LINE_HORIZONTAL:
                return convertToVT100((char) 113);
            case Symbols.DOUBLE_LINE_BOTTOM_LEFT_CORNER:
            case Symbols.SINGLE_LINE_BOTTOM_LEFT_CORNER:
                return convertToVT100((char) 109);
            case Symbols.DOUBLE_LINE_BOTTOM_RIGHT_CORNER:
            case Symbols.SINGLE_LINE_BOTTOM_RIGHT_CORNER:
                return convertToVT100((char) 106);
            case Symbols.DOUBLE_LINE_T_DOWN:
            case Symbols.SINGLE_LINE_T_DOWN:
            case Symbols.DOUBLE_LINE_T_SINGLE_DOWN:
            case Symbols.SINGLE_LINE_T_DOUBLE_DOWN:
                return convertToVT100((char) 119);
            case Symbols.DOUBLE_LINE_T_LEFT:
            case Symbols.SINGLE_LINE_T_LEFT:
            case Symbols.DOUBLE_LINE_T_SINGLE_LEFT:
            case Symbols.SINGLE_LINE_T_DOUBLE_LEFT:
                return convertToVT100((char) 117);
            case Symbols.DOUBLE_LINE_T_RIGHT:
            case Symbols.SINGLE_LINE_T_RIGHT:
            case Symbols.DOUBLE_LINE_T_SINGLE_RIGHT:
            case Symbols.SINGLE_LINE_T_DOUBLE_RIGHT:
                return convertToVT100((char) 116);
            case Symbols.DOUBLE_LINE_T_UP:
            case Symbols.SINGLE_LINE_T_UP:
            case Symbols.DOUBLE_LINE_T_SINGLE_UP:
            case Symbols.SINGLE_LINE_T_DOUBLE_UP:
                return convertToVT100((char) 118);
            case Symbols.DOUBLE_LINE_TOP_LEFT_CORNER:
            case Symbols.SINGLE_LINE_TOP_LEFT_CORNER:
                return convertToVT100((char) 108);
            case Symbols.DOUBLE_LINE_TOP_RIGHT_CORNER:
            case Symbols.SINGLE_LINE_TOP_RIGHT_CORNER:
                return convertToVT100((char) 107);
            case Symbols.DOUBLE_LINE_VERTICAL:
            case Symbols.SINGLE_LINE_VERTICAL:
                return convertToVT100((char) 120);
            default:
                return convertToCharset(input);
        }
    }

    private byte[] convertToVT100(char code) {
        //Warning! This might be terminal type specific!!!!
        //So far it's worked everywhere I've tried it (xterm, gnome-terminal, putty)
        return new byte[]{27, 40, 48, (byte) code, 27, 40, 66};
    }

    private byte[] convertToCharset(char input) {
        //TODO: This is a silly way to do it, improve?
        final char[] buffer = new char[1];
        buffer[0] = input;
        return terminalCharset.encode(CharBuffer.wrap(buffer)).array();
    }
}
