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
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * A good resource on telnet communication is http://www.tcpipguide.com/free/t_TelnetProtocol.htm
 * @author martin
 */
public class TelnetTerminal extends ANSITerminal {
    
    private final Socket socket;

    TelnetTerminal(Socket socket, Charset terminalCharset) throws IOException {
        super(new TelnetClientIACFilterer(socket.getInputStream()), socket.getOutputStream(), terminalCharset);
        this.socket = socket;
        setLineMode0();
        setEchoOff();
    }

    private void setEchoOff() throws IOException {
        writeToTerminal((byte)255, (byte)251, (byte)1);
        flush();
    }
    
    private void setLineMode0() throws IOException {
        writeToTerminal(
            (byte)255, (byte)253, (byte)34,  /* IAC DO LINEMODE */
            (byte)255, (byte)250, (byte)34, (byte)1, (byte)0, (byte)255, (byte)240 /* IAC SB LINEMODE MODE 0 IAC SE */
        );
        flush();
    }

    @Override
    public KeyStroke readInput() throws IOException {
        KeyStroke keyStroke = super.readInput();
        return keyStroke;
    }
    
    public void close() throws IOException {
        socket.close();
    }
    
    private static class TelnetClientIACFilterer extends InputStream {
        private final InputStream inputStream;
        private final byte[] buffer;
        private final byte[] workingBuffer;
        private int bytesInBuffer;

        public TelnetClientIACFilterer(InputStream inputStream) {
            this.inputStream = inputStream;
            this.buffer = new byte[64 * 1024];
            this.workingBuffer = new byte[1024];
            this.bytesInBuffer = 0;
        }

        @Override
        public int read() throws IOException {
            throw new UnsupportedOperationException("TelnetClientIACFilterer doesn't support .read()");
        }

        @Override
        public void close() throws IOException {
            inputStream.close();
        }

        @Override
        public int available() throws IOException {
            int underlyingStreamAvailable = inputStream.available();
            if(underlyingStreamAvailable == 0 && bytesInBuffer == 0) {
                return 0;
            }
            else if(underlyingStreamAvailable == 0) {
                return bytesInBuffer;
            }
            else if(bytesInBuffer == buffer.length) {
                return bytesInBuffer;
            }
            fillBuffer();
            return bytesInBuffer;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if(inputStream.available() > 0) {
                fillBuffer();
            }
            if(bytesInBuffer == 0) {
                return -1;
            }
            int bytesToCopy = Math.min(len, bytesInBuffer);
            System.arraycopy(buffer, 0, b, off, bytesToCopy);
            System.arraycopy(buffer, bytesToCopy, buffer, 0, buffer.length - bytesToCopy);
            bytesInBuffer -= bytesToCopy;
            return bytesToCopy;
        }

        private void fillBuffer() throws IOException {
            int readBytes = inputStream.read(workingBuffer, 0, Math.min(workingBuffer.length, buffer.length - bytesInBuffer));
            if(readBytes == -1) {
                return;
            }
            for(int i = 0; i < readBytes; i++) {
                if(workingBuffer[i] == -1) {//0xFF = IAC = Interpret As Command
                    i++;
                    if(workingBuffer[i] >= (byte)0xfb && workingBuffer[i] <= (byte)0xfe) {
                        i++;
                        continue;
                    }
                    else if(workingBuffer[i] == (byte)0xfa) {   //0xFA = SB = Subnegotiation
                        i++;
                        //Wait for SE
                        while(workingBuffer[i] != (byte)0xF0) {
                            i++;
                        }
                    }
                    else {
                        System.err.println("Unknown Telnet command: " + workingBuffer[i]);
                    }
                }
                buffer[bytesInBuffer++] = workingBuffer[i];
            }
        }
    }
}
