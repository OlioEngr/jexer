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

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * This class provides a Socket that performs the telnet protocol to both
 * establish an 8-bit clean no echo channel and expose window resize events
 * to the Jexer ECMA48 backend.
 */
public final class TelnetSocket extends Socket {

    /**
     * The telnet-aware socket InputStream.
     */
    private TelnetInputStream input;

    /**
     * The telnet-aware socket OutputStream.
     */
    private TelnetOutputStream output;

    // Telnet protocol special characters.  Note package private access.
    static final int TELNET_SE         = 240;
    static final int TELNET_NOP        = 241;
    static final int TELNET_DM         = 242;
    static final int TELNET_BRK        = 243;
    static final int TELNET_IP         = 244;
    static final int TELNET_AO         = 245;
    static final int TELNET_AYT        = 246;
    static final int TELNET_EC         = 247;
    static final int TELNET_EL         = 248;
    static final int TELNET_GA         = 249;
    static final int TELNET_SB         = 250;
    static final int TELNET_WILL       = 251;
    static final int TELNET_WONT       = 252;
    static final int TELNET_DO         = 253;
    static final int TELNET_DONT       = 254;
    static final int TELNET_IAC        = 255;
    static final int C_NUL             = 0x00;
    static final int C_LF              = 0x0A;
    static final int C_CR              = 0x0D;

    /**
     * If true, this is a server socket (i.e. created by accept()).
     */
    boolean isServer = true;

    /**
     * If true, telnet ECHO mode is set such that local echo is off and
     * remote echo is on.  This is appropriate for server sockets.
     */
    boolean echoMode = false;

    /**
     * If true, telnet BINARY mode is enabled.  We always want this to
     * ensure a Unicode-safe stream.
     */
    boolean binaryMode = false;

    /**
     * If true, the SUPPRESS-GO-AHEAD option is enabled.  We always want
     * this.
     */
    boolean goAhead = true;

    /**
     * If true, request the client terminal type.
     */
    boolean doTermType = true;

    /**
     * If true, request the client terminal speed.
     */
    boolean doTermSpeed = true;

    /**
     * If true, request the Negotiate About Window Size option to
     * determine the client text width/height.
     */
    boolean doNAWS = true;

    /**
     * If true, request the New Environment option to obtain the client
     * LOGNAME, USER, and LANG variables.
     */
    boolean doEnvironment;

    /**
     * The terminal type reported by the client.
     */
    String terminalType = "";

    /**
     * The terminal speed reported by the client.
     */
    String terminalSpeed = "";

    /**
     * See if telnet server/client is in ASCII mode.
     *
     * @return if true, this connection is in ASCII mode
     */
    public boolean isAscii() {
        return (!binaryMode);
    }

    /**
     * Creates a Socket that knows the telnet protocol.  Note package private
     * access, this is only used by TelnetServerSocket.
     *
     * @throws IOException if an I/O error occurs
     */
    TelnetSocket() throws IOException {
        super();
    }

    // Socket interface -------------------------------------------------------

    /**
     * Returns an input stream for this socket.
     *
     * @return the input stream
     * @throws IOException if an I/O error occurs
     */
    @Override
    public InputStream getInputStream() throws IOException {
        if (input == null) {
            assert (output == null);
            output = new TelnetOutputStream(this, super.getOutputStream());
            input = new TelnetInputStream(this, super.getInputStream(), output);
            input.telnetSendOptions();
        }
        return input;
    }

    /**
     * Returns an output stream for this socket.
     *
     * @return the output stream
     * @throws IOException if an I/O error occurs
     */
    @Override
    public OutputStream getOutputStream() throws IOException {
        if (output == null) {
            assert (input == null);
            output = new TelnetOutputStream(this, super.getOutputStream());
            input = new TelnetInputStream(this, super.getInputStream(), output);
            input.telnetSendOptions();
        }
        return output;
    }

}
