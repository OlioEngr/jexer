/**
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
     * The wrapped socket.
     */
    private Socket socket;

    /**
     * The telnet-aware socket InputStream.
     */
    private TelnetInputStream input;

    /**
     * The telnet-aware socket OutputStream. Note package private access:
     * input sends stuff to output.
     */
    TelnetOutputStream output;

    // Telnet protocol special characters.  Note package private access.
    public static final int TELNET_SE         = 240;
    public static final int TELNET_NOP        = 241;
    public static final int TELNET_DM         = 242;
    public static final int TELNET_BRK        = 243;
    public static final int TELNET_IP         = 244;
    public static final int TELNET_AO         = 245;
    public static final int TELNET_AYT        = 246;
    public static final int TELNET_EC         = 247;
    public static final int TELNET_EL         = 248;
    public static final int TELNET_GA         = 249;
    public static final int TELNET_SB         = 250;
    public static final int TELNET_WILL       = 251;
    public static final int TELNET_WONT       = 252;
    public static final int TELNET_DO         = 253;
    public static final int TELNET_DONT       = 254;
    public static final int TELNET_IAC        = 255;
    public static final int C_NUL             = 0x00;
    public static final int C_LF              = 0x0A;
    public static final int C_CR              = 0x0D;

    /**
     * Telnet protocol speaks to a Network Virtual Terminal (NVT).
     */
    class TelnetState {

        // General status flags outside the NVT spec
        boolean doInit;
        boolean isServer;

        // NVT flags
        boolean echoMode;
        boolean binaryMode;
        boolean goAhead;
        boolean doTermType;
        boolean doTermSpeed;
        boolean doNAWS;
        boolean doEnvironment;
        String terminal = "";

        // Flags used by the TelnetInputStream
        boolean iac;
        boolean dowill;
        int dowillType;
        boolean subnegEnd;
        boolean isEof;
        boolean eofMsg;
        boolean readCR;

        // Flags used by the TelnetOutputStream
        int writeRC;
        int writeLastErrno;
        boolean writeLastError;
        boolean writeCR;

        /**
         * Constuctor calls reset().
         */
        public TelnetState() {
            reset();
        }

        /**
         * Reset NVT to default state as per RFC 854.
         */
        public void reset() {
            echoMode            = false;
            binaryMode          = false;
            goAhead             = true;
            doTermType          = true;
            doTermSpeed         = true;
            doNAWS              = true;
            doEnvironment       = true;
            doInit              = true;
            isServer            = true;

            iac                 = false;
            dowill              = false;
            subnegEnd           = false;
            isEof               = false;
            eofMsg              = false;
            readCR              = false;

            writeRC             = 0;
            writeLastErrno      = 0;
            writeLastError      = false;
            writeCR             = false;

        }
    }

    /**
     * State of the protocol.  Note package private access.
     */
    TelnetState nvt;

    /**
     * See if telnet server/client is in ASCII mode.
     *
     * @return if true, this connection is in ASCII mode
     */
    public boolean isAscii() {
        return (!nvt.binaryMode);
    }

    /**
     * Creates a Socket that knows the telnet protocol.
     *
     * @param socket the underlying Socket
     */
    TelnetSocket(Socket socket) throws IOException {
        super();
        nvt = new TelnetState();
        this.socket = socket;
    }

    // Socket interface -------------------------------------------------------

    /**
     * Returns an input stream for this socket.
     *
     * @return the input stream
     */
    @Override
    public InputStream getInputStream() throws IOException {
        if (input == null) {
            input = new TelnetInputStream(this, super.getInputStream());
        }
        return input;
    }

    /**
     * Returns an output stream for this socket.
     *
     * @return the output stream
     */
    @Override
    public OutputStream getOutputStream() throws IOException {
        if (output == null) {
            output = new TelnetOutputStream(this, super.getOutputStream());
        }
        return output;
    }

}
