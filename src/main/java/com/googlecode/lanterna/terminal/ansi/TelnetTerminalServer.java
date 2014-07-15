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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import javax.net.ServerSocketFactory;

/**
 *
 * @author martin
 */
@SuppressWarnings("WeakerAccess")
public class TelnetTerminalServer {
    private final Charset charset;
    private final ServerSocket serverSocket;

    public TelnetTerminalServer(int port) throws IOException {
        this(ServerSocketFactory.getDefault(), port);
    }

    public TelnetTerminalServer(int port, Charset charset) throws IOException {
        this(ServerSocketFactory.getDefault(), port, charset);
    }
    
    public TelnetTerminalServer(ServerSocketFactory serverSocketFactory, int port) throws IOException {
        this(serverSocketFactory, port, Charset.defaultCharset());
    }
    
    public TelnetTerminalServer(ServerSocketFactory serverSocketFactory, int port, Charset charset) throws IOException {
        this.serverSocket = serverSocketFactory.createServerSocket(port);
        this.charset = charset;
    }
    
    public void setSocketTimeout(int timeout) throws SocketException {
        serverSocket.setSoTimeout(timeout);
    }
    
    public TelnetTerminal acceptConnection() throws IOException {
        Socket clientSocket = serverSocket.accept();
        clientSocket.setTcpNoDelay(true);
        return new TelnetTerminal(clientSocket, charset);
    }
}
