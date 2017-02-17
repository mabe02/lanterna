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
 * Copyright (C) 2010-2017 Martin Berglund
 */
package com.googlecode.lanterna.terminal.ansi;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import javax.net.ServerSocketFactory;

/**
 * This class implements a Telnet server, capable of accepting multiple clients and presenting each one as their own
 * Terminal. You need to tell it at least what port to listen on and then it create a Server socket listening for
 * incoming connections. Use {@code acceptConnection()} to wait for the next incoming connection, it will be returned as
 * a {@code TelnetTerminal} object that represents the client and which will be the way for the server to send content
 * to this client. Next connecting client (through {@code acceptConnection()} will get a different
 * {@code TelnetTerminal}, i.e. their content will not be in sync automatically but considered as two different
 * terminals.
 * @author martin
 * @see TelnetTerminal
 * @see <a href="http://en.wikipedia.org/wiki/Telnet">Wikipedia</a>
 */
@SuppressWarnings("WeakerAccess")
public class TelnetTerminalServer {
    private final Charset charset;
    private final ServerSocket serverSocket;

    /**
     * Creates a new TelnetTerminalServer on a specific port
     * @param port Port to listen for incoming telnet connections
     * @throws IOException If there was an underlying I/O exception
     */
    public TelnetTerminalServer(int port) throws IOException {
        this(ServerSocketFactory.getDefault(), port);
    }

    /**
     * Creates a new TelnetTerminalServer on a specific port, using a certain character set
     * @param port Port to listen for incoming telnet connections
     * @param charset Character set to use
     * @throws IOException If there was an underlying I/O exception
     */
    public TelnetTerminalServer(int port, Charset charset) throws IOException {
        this(ServerSocketFactory.getDefault(), port, charset);
    }

    /**
     * Creates a new TelnetTerminalServer on a specific port through a ServerSocketFactory
     * @param port Port to listen for incoming telnet connections
     * @param serverSocketFactory ServerSocketFactory to use when creating the ServerSocket
     * @throws IOException If there was an underlying I/O exception
     */
    public TelnetTerminalServer(ServerSocketFactory serverSocketFactory, int port) throws IOException {
        this(serverSocketFactory, port, Charset.defaultCharset());
    }

    /**
     * Creates a new TelnetTerminalServer on a specific port through a ServerSocketFactory with a certain Charset
     * @param serverSocketFactory ServerSocketFactory to use when creating the ServerSocket
     * @param port Port to listen for incoming telnet connections
     * @param charset Character set to use
     * @throws IOException If there was an underlying I/O exception
     */
    public TelnetTerminalServer(ServerSocketFactory serverSocketFactory, int port, Charset charset) throws IOException {
        this.serverSocket = serverSocketFactory.createServerSocket(port);
        this.charset = charset;
    }

    /**
     * Returns the actual server socket used by this object. Can be used to tweak settings but be careful!
     * @return Underlying ServerSocket
     */
    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    /**
     * Waits for the next client to connect in to our server and returns a Terminal implementation, TelnetTerminal, that
     * represents the remote terminal this client is running. The terminal can be used just like any other Terminal, but
     * keep in mind that all operations are sent over the network.
     * @return TelnetTerminal for the remote client's terminal
     * @throws IOException If there was an underlying I/O exception
     */
    public TelnetTerminal acceptConnection() throws IOException {
        Socket clientSocket = serverSocket.accept();
        clientSocket.setTcpNoDelay(true);
        return new TelnetTerminal(clientSocket, charset);
    }

    /**
     * Closes the server socket, accepting no new connection. Any call to acceptConnection() after this will fail.
     * @throws IOException If there was an underlying I/O exception
     */
    public void close() throws IOException {
        serverSocket.close();
    }
}
