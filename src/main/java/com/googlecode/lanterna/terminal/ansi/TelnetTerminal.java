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

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.TerminalSize;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 *
 * @author martin
 */
public class TelnetTerminal extends ANSITerminal {
    
    private final Socket socket;

    TelnetTerminal(Socket socket, Charset terminalCharset) throws IOException {
        super(new TelnetClientIACFilterer(socket.getInputStream()), socket.getOutputStream(), terminalCharset);
        this.socket = socket;
        //setCBreak(true);
        setEcho(false);
    }

    @Override
    public void setEcho(boolean echoOn) throws IOException {
        writeToTerminal((byte)255, (byte)251, (byte)1);
        flush();
    }

//    @Override
//    public TerminalSize getTerminalSize() throws IOException {
//        return new TerminalSize(80, 20);
//    }
    
    @Override
    public void setCBreak(boolean cbreakOn) throws IOException {
        writeToTerminal(
            (byte)255, (byte)253, (byte)34,  /* IAC DO LINEMODE */
            (byte)255, (byte)250, (byte)34, (byte)1, (byte)0, (byte)255, (byte)240 /* IAC SB LINEMODE MODE 0 IAC SE */
        );
        flush();
    }

    @Override
    public KeyStroke readInput() throws IOException {
        KeyStroke keyStroke = super.readInput();
        System.out.println("Got " + keyStroke);
        return keyStroke;
    }
    
    public void close() throws IOException {
        socket.close();
    }
    
    private static class TelnetClientIACFilterer extends InputStream {
        private final InputStream inputStream;

        public TelnetClientIACFilterer(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public int read() throws IOException {
            int data = inputStream.read();
            System.out.println("Telnet client sent: " + data);
            return data;
        }

        @Override
        public void close() throws IOException {
            inputStream.close();
        }

        @Override
        public int available() throws IOException {
            return inputStream.available();
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int readBytes = inputStream.read(b, off, len);
            ByteArrayOutputStream filteredOutput = new ByteArrayOutputStream(len);
            for(int i = 0; i < readBytes; i++) {
                System.out.println("Telnet client sent: 0x" + String.format("%02X ", b[i]) + " (" + b[i] + ")");
                filteredOutput.write(b[i]);
            }
            System.arraycopy(filteredOutput.toByteArray(), 0, b, 0, filteredOutput.size());
            return filteredOutput.size();
        }
    }
}
