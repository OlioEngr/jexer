/*
 * Jexer - Java Text User Interface
 *
 * License: LGPLv3 or later
 *
 * This module is licensed under the GNU Lesser General Public License
 * Version 3.  Please see the file "COPYING" in this directory for more
 * information about the GNU Lesser General Public License Version 3.
 *
 *     Copyright (C) 2015  Kevin Lamonte
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, see
 * http://www.gnu.org/licenses/, or write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA
 *
 * @author Kevin Lamonte [kevin.lamonte@gmail.com]
 * @version 1
 */
package jexer.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * This class provides a ServerSocket that return TelnetSocket's in accept().
 */
public final class TelnetServerSocket extends ServerSocket {

    // ServerSocket interface -------------------------------------------------

    /**
     * Creates an unbound server socket.
     *
     * @throws IOException if an I/O error occurs
     */
    public TelnetServerSocket() throws IOException {
        super();
    }

    /**
     * Creates a server socket, bound to the specified port.
     *
     * @param port the port number, or 0 to use a port number that is
     * automatically allocated.
     * @throws IOException if an I/O error occurs
     */
    public TelnetServerSocket(final int port) throws IOException {
        super(port);
    }

    /**
     * Creates a server socket and binds it to the specified local port
     * number, with the specified backlog.
     *
     * @param port the port number, or 0 to use a port number that is
     * automatically allocated.
     * @param backlog requested maximum length of the queue of incoming
     * connections.
     * @throws IOException if an I/O error occurs
     */
    public TelnetServerSocket(final int port,
        final int backlog) throws IOException {

        super(port, backlog);
    }

    /**
     * Create a server with the specified port, listen backlog, and local IP
     * address to bind to.
     *
     * @param port the port number, or 0 to use a port number that is
     * automatically allocated.
     * @param backlog requested maximum length of the queue of incoming
     * connections.
     * @param bindAddr the local InetAddress the server will bind to
     * @throws IOException if an I/O error occurs
     */
    public TelnetServerSocket(final int port, final int backlog,
        final InetAddress bindAddr) throws IOException {

        super(port, backlog, bindAddr);
    }

    /**
     * Listens for a connection to be made to this socket and accepts it. The
     * method blocks until a connection is made.
     *
     * @return the new Socket
     * @throws IOException if an I/O error occurs
     */
    @Override
    public Socket accept() throws IOException {
        if (isClosed()) {
            throw new SocketException("Socket is closed");
        }
        if (!isBound()) {
            throw new SocketException("Socket is not bound");
        }

        Socket socket = new TelnetSocket();
        implAccept(socket);
        return socket;
    }

}
