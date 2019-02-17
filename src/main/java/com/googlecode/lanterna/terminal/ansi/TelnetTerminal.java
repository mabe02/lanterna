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

import static com.googlecode.lanterna.terminal.ansi.TelnetProtocol.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used by the {@code TelnetTerminalServer} class when a client has connected in; this class will be the
 * interaction point for that client. All operations are sent to the client over the network socket and some of the
 * meta-operations (like echo mode) are communicated using Telnet negotiation language. You can't create objects of this
 * class directly; they are created for you when you are listening for incoming connections using a
 * {@code TelnetTerminalServer} and a client connects.
 * <p>
 * A good resource on telnet communication is http://www.tcpipguide.com/free/t_TelnetProtocol.htm<br>
 * Also here: http://support.microsoft.com/kb/231866
 * @see TelnetTerminalServer
 * @author martin
 */
public class TelnetTerminal extends ANSITerminal {
    
    private final Socket socket;
    private final NegotiationState negotiationState;

    TelnetTerminal(Socket socket, Charset terminalCharset) throws IOException {
        this(socket, new TelnetClientIACFilterer(socket.getInputStream()), socket.getOutputStream(), terminalCharset);
    }

    //This weird construction is just so that we can access the input filter without changing the visibility in StreamBasedTerminal
    private TelnetTerminal(Socket socket, TelnetClientIACFilterer inputStream, OutputStream outputStream, Charset terminalCharset) throws IOException {
        super(inputStream, outputStream, terminalCharset);
        this.socket = socket;
        this.negotiationState = inputStream.negotiationState;
        inputStream.setEventListener(new TelnetClientEventListener() {
            @Override
            public void onResize(int columns, int rows) {
                TelnetTerminal.this.onResized(columns, rows);
            }

            @Override
            public void requestReply(boolean will, byte option) throws IOException {
                writeToTerminal(COMMAND_IAC, will ? COMMAND_WILL : COMMAND_WONT, option);
            }
        });
        setLineMode0();
        setEchoOff();
        setResizeNotificationOn();
    }

    /**
     * Returns the socket address for the remote endpoint of the telnet connection
     * @return SocketAddress representing the remote client
     */
    public SocketAddress getRemoteSocketAddress() {
        return socket.getRemoteSocketAddress();
    }
    
    private void setEchoOff() throws IOException {
        writeToTerminal(COMMAND_IAC, COMMAND_WILL, OPTION_ECHO);
        flush();
    }
    
    private void setLineMode0() throws IOException {
        writeToTerminal(
                COMMAND_IAC, COMMAND_DO, OPTION_LINEMODE,
                COMMAND_IAC, COMMAND_SUBNEGOTIATION, OPTION_LINEMODE, (byte)1, (byte)0, COMMAND_IAC, COMMAND_SUBNEGOTIATION_END);
        flush();
    }

    private void setResizeNotificationOn() throws IOException {
        writeToTerminal(
                COMMAND_IAC, COMMAND_DO, OPTION_NAWS);
        flush();
    }

    /**
     * Retrieves the current negotiation state with the client, containing details on what options have been enabled
     * and what the client has said it supports.
     * @return The current negotiation state for this client
     */
    public NegotiationState getNegotiationState() {
        return negotiationState;
    }

    /**
     * Closes the socket to the client, effectively ending the telnet session and the terminal.
     * @throws IOException If there was an underlying I/O error
     */
    @Override
    public void close() throws IOException {
        super.close();
        socket.close();
    }

    /**
     * This class contains some of the various states that the Telnet negotiation protocol defines. Lanterna doesn't
     * support all of them but the more common ones are represented.
     */
    public static class NegotiationState {
        private boolean clientEcho;
        private boolean clientLineMode0;
        private boolean clientResizeNotification;
        private boolean suppressGoAhead;
        private boolean extendedAscii;

        NegotiationState() {
            this.clientEcho = true;
            this.clientLineMode0 = false;
            this.clientResizeNotification = false;
            this.suppressGoAhead = true;
            this.extendedAscii = true;  
        }

        /**
         * Is the telnet client echo mode turned on (client is echoing characters locally)
         * @return {@code true} if client echo is enabled
         */
        public boolean isClientEcho() {
            return clientEcho;
        }

        /**
         * Is the telnet client line mode 0 turned on (client sends character by character instead of line by line)
         * @return {@code true} if client line mode 0 is enabled
         */
        public boolean isClientLineMode0() {
            return clientLineMode0;
        }

        /**
         * Is the telnet client resize notification turned on (client notifies server when the terminal window has
         * changed size)
         * @return {@code true} if client resize notification is enabled
         */
        public boolean isClientResizeNotification() {
            return clientResizeNotification;
        }


        /**
         * Is the telnet client suppress go-ahead turned on
         * @return {@code true} if client suppress go-ahead is enabled
         */
        public boolean isSuppressGoAhead() {
            return suppressGoAhead;
        }

        /**
         * Is the telnet client extended ascii turned on
         * @return {@code true} if client extended ascii is enabled
         */
        public boolean isExtendedAscii() {
            return extendedAscii;
        }
        
        private void onUnsupportedStateCommand(boolean enabling, byte value) {
            System.err.println("Unsupported operation: Client says it " + (enabling ? "will" : "won't") + " do " + TelnetProtocol.CODE_TO_NAME.get(value));
        }

        private void onUnsupportedRequestCommand(boolean askedToDo, byte value) {
            System.err.println("Unsupported request: Client asks us, " + (askedToDo ? "do" : "don't") + " " + TelnetProtocol.CODE_TO_NAME.get(value));
        }

        private void onUnsupportedSubnegotiation(byte option, byte[] additionalData) {
            System.err.println("Unsupported subnegotiation: Client send " + TelnetProtocol.CODE_TO_NAME.get(option) + " with extra data " +
                    toList(additionalData));
        }
        
        private static List<String> toList(byte[] array) {
            List<String> list = new ArrayList<String>(array.length);
            for(byte b: array) {
                list.add(String.format("%02X ", b));
            }
            return list;
        }
    }
    
    private interface TelnetClientEventListener {
        void onResize(int columns, int rows);
        void requestReply(boolean will, byte option) throws IOException;
    }
    
    private static class TelnetClientIACFilterer extends InputStream {
        private final NegotiationState negotiationState;
        private final InputStream inputStream;
        private final byte[] buffer;
        private final byte[] workingBuffer;
        private int bytesInBuffer;
        private TelnetClientEventListener eventListener;

        TelnetClientIACFilterer(InputStream inputStream) {
            this.negotiationState = new NegotiationState();
            this.inputStream = inputStream;
            this.buffer = new byte[64 * 1024];
            this.workingBuffer = new byte[1024];
            this.bytesInBuffer = 0;
            this.eventListener = null;
        }

        private void setEventListener(TelnetClientEventListener eventListener) {
            this.eventListener = eventListener;
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
        @SuppressWarnings("NullableProblems")   //I can't find the correct way to fix this!
        public int read(byte[] b, int off, int len) throws IOException {
            if(available() == 0) {
               // There was nothing in the buffer and the underlying
               // stream has nothing available, so do a blocking read
               // from the stream.
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
                if(workingBuffer[i] == COMMAND_IAC) {
                    i++;
                    if(Arrays.asList(COMMAND_DO, COMMAND_DONT, COMMAND_WILL, COMMAND_WONT).contains(workingBuffer[i])) {
                        parseCommand(workingBuffer, i, readBytes);
                        ++i;
                        continue;
                    }
                    else if(workingBuffer[i] == COMMAND_SUBNEGOTIATION) {   //0xFA = SB = Subnegotiation
                        i += parseSubNegotiation(workingBuffer, ++i, readBytes);
                        continue;
                    }
                    else if(workingBuffer[i] != COMMAND_IAC) {   //Double IAC = 255
                        System.err.println("Unknown Telnet command: " + workingBuffer[i]);
                    }
                }
                buffer[bytesInBuffer++] = workingBuffer[i];
            }
        }
        
        private void parseCommand(byte[] buffer, int position, int max) throws IOException {
            if(position + 1 >= max) {
                throw new IllegalStateException("State error, we got a command signal from the remote telnet client but "
                        + "not enough characters available in the stream");
            }
            byte command = buffer[position];
            byte value = buffer[position + 1];
            switch(command) {
                case COMMAND_DO:
                case COMMAND_DONT:
                    if(value == OPTION_SUPPRESS_GO_AHEAD) {
                        negotiationState.suppressGoAhead = (command == COMMAND_DO);
                        eventListener.requestReply(command == COMMAND_DO, value);
                    }
                    else if(value == OPTION_EXTEND_ASCII) {
                        negotiationState.extendedAscii = (command == COMMAND_DO);
                        eventListener.requestReply(command == COMMAND_DO, value);
                    }
                    else {
                        negotiationState.onUnsupportedRequestCommand(command == COMMAND_DO, value);
                    }
                    break;
                case COMMAND_WILL:
                case COMMAND_WONT:
                    if(value == OPTION_ECHO) {
                        negotiationState.clientEcho = (command == COMMAND_WILL);
                    }
                    else if(value == OPTION_LINEMODE) {
                        negotiationState.clientLineMode0 = (command == COMMAND_WILL);
                    }
                    else if(value == OPTION_NAWS) {
                       negotiationState.clientResizeNotification = (command == COMMAND_WILL);
                    }
                    else {
                        negotiationState.onUnsupportedStateCommand(command == COMMAND_WILL, value);
                    }
                    break;
                default:
                    throw new UnsupportedOperationException("No command handler implemented for " + TelnetProtocol.CODE_TO_NAME.get(command));
            }
        }
        
        private int parseSubNegotiation(byte[] buffer, int position, int max) {
            int originalPosition = position;

            //Read operation
            byte operation = buffer[position++];
            
            //Read until [IAC SE]            
            ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
            while(position < max) {
                byte read = buffer[position];
                if(read != COMMAND_IAC) {
                    outputBuffer.write(read);
                }
                else {
                    if(position + 1 == max) {
                        throw new IllegalStateException("State error, unexpected end of buffer when reading subnegotiation");
                    }
                    position++;
                    if(buffer[position] == COMMAND_IAC) {
                        outputBuffer.write(COMMAND_IAC);    //Escaped IAC
                    }
                    else if(buffer[position] == COMMAND_SUBNEGOTIATION_END) {
                        parseSubNegotiation(operation, outputBuffer.toByteArray());
                        return ++position - originalPosition;
                    }
                }
                position++;
            }
            throw new IllegalStateException("State error, unexpected end of buffer when reading subnegotiation, no IAC SE");
        }

        private void parseSubNegotiation(byte option, byte[] additionalData) {
            switch(option) {
                case OPTION_NAWS:
                    eventListener.onResize(
                            convertTwoBytesToInt2(additionalData[1], additionalData[0]), 
                            convertTwoBytesToInt2(additionalData[3], additionalData[2]));
                    break;
                case OPTION_LINEMODE:
                    //We don't parse this, as this is a very complicated command :(
                    //Let's leave it for now, fingers crossed
                    break;
                default:
                    negotiationState.onUnsupportedSubnegotiation(option, additionalData);
                    break;
            }
        }
    }
    
    private static int convertTwoBytesToInt2(byte b1, byte b2) {
        return ( (b2 & 0xFF) << 8) | (b1 & 0xFF);
    }
}
