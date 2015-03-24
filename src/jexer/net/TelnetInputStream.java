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
import java.util.Map;
import java.util.TreeMap;

import jexer.session.SessionInfo;
import static jexer.net.TelnetSocket.*;

/**
 * TelnetInputStream works with TelnetSocket to perform the telnet protocol.
 */
public final class TelnetInputStream extends InputStream implements SessionInfo {

    /**
     * The root TelnetSocket that has my telnet protocol state.
     */
    private TelnetSocket master;

    /**
     * The raw socket's InputStream.
     */
    private InputStream input;

    /**
     * Package private constructor.
     *
     * @param master the master TelnetSocket
     * @param input the underlying socket's InputStream
     */
    TelnetInputStream(TelnetSocket master, InputStream input) {
        this.master = master;
        this.input  = input;
    }

    // SessionInfo interface --------------------------------------------------

    /**
     * User name.
     */
    private String username = "";

    /**
     * Language.
     */
    private String language = "en_US";

    /**
     * Text window width.
     */
    private int windowWidth = 80;

    /**
     * Text window height.
     */
    private int windowHeight = 24;

    /**
     * Username getter.
     *
     * @return the username
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Username setter.
     *
     * @param username the value
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * Language getter.
     *
     * @return the language
     */
    public String getLanguage() {
        return this.language;
    }

    /**
     * Language setter.
     *
     * @param language the value
     */
    public void setLanguage(final String language) {
        this.language = language;
    }

    /**
     * Text window width getter.
     *
     * @return the window width
     */
    public int getWindowWidth() {
        return windowWidth;
    }

    /**
     * Text window height getter.
     *
     * @return the window height
     */
    public int getWindowHeight() {
        return windowHeight;
    }

    /**
     * Re-query the text window size.
     */
    public void queryWindowSize() {
        // NOP
    }

    // InputStream interface --------------------------------------------------

    /**
     * Returns an estimate of the number of bytes that can be read (or
     * skipped over) from this input stream without blocking by the next
     * invocation of a method for this input stream.
     *
     */
    @Override
    public int available() throws IOException {
        // TODO
        return 0;
    }

    /**
     * Closes this input stream and releases any system resources associated
     * with the stream.
     */
    @Override
    public void close() throws IOException {
        // TODO
    }

    /**
     * Marks the current position in this input stream.
     */
    @Override
    public void mark(int readlimit) {
        // TODO
    }

    /**
     * Tests if this input stream supports the mark and reset methods.
     */
    @Override
    public boolean markSupported() {
        return false;
    }

    /**
     * Reads the next byte of data from the input stream.
     */
    @Override
    public int read() throws IOException {
        // TODO
        return -1;
    }

    /**
     * Reads some number of bytes from the input stream and stores them into
     * the buffer array b.
     */
    @Override
    public int read(byte[] b) throws IOException {
        // TODO
        return -1;
    }

    /**
     * Reads up to len bytes of data from the input stream into an array of
     * bytes.
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        // TODO
        return -1;
    }

    /**
     * Repositions this stream to the position at the time the mark method
     * was last called on this input stream.
     */
    @Override
    public void reset() throws IOException {
        // TODO
    }

    /**
     * Skips over and discards n bytes of data from this input stream.
     */
    @Override
    public long skip(long n) throws IOException {
        // TODO
        return -1;
    }


    // Telnet protocol --------------------------------------------------------

    /**
     * The subnegotiation buffer.
     */
    private byte [] subnegBuffer;

    /**
     * For debugging, return a descriptive string for this telnet option.
     * These are pulled from: http://www.iana.org/assignments/telnet-options
     *
     * @return a string describing the telnet option code
     */
    private String optionString(final int option) {
        switch (option) {
        case 0: return "Binary Transmission";
        case 1: return "Echo";
        case 2: return "Reconnection";
        case 3: return "Suppress Go Ahead";
        case 4: return "Approx Message Size Negotiation";
        case 5: return "Status";
        case 6: return "Timing Mark";
        case 7: return "Remote Controlled Trans and Echo";
        case 8: return "Output Line Width";
        case 9: return "Output Page Size";
        case 10: return "Output Carriage-Return Disposition";
        case 11: return "Output Horizontal Tab Stops";
        case 12: return "Output Horizontal Tab Disposition";
        case 13: return "Output Formfeed Disposition";
        case 14: return "Output Vertical Tabstops";
        case 15: return "Output Vertical Tab Disposition";
        case 16: return "Output Linefeed Disposition";
        case 17: return "Extended ASCII";
        case 18: return "Logout";
        case 19: return "Byte Macro";
        case 20: return "Data Entry Terminal";
        case 21: return "SUPDUP";
        case 22: return "SUPDUP Output";
        case 23: return "Send Location";
        case 24: return "Terminal Type";
        case 25: return "End of Record";
        case 26: return "TACACS User Identification";
        case 27: return "Output Marking";
        case 28: return "Terminal Location Number";
        case 29: return "Telnet 3270 Regime";
        case 30: return "X.3 PAD";
        case 31: return "Negotiate About Window Size";
        case 32: return "Terminal Speed";
        case 33: return "Remote Flow Control";
        case 34: return "Linemode";
        case 35: return "X Display Location";
        case 36: return "Environment Option";
        case 37: return "Authentication Option";
        case 38: return "Encryption Option";
        case 39: return "New Environment Option";
        case 40: return "TN3270E";
        case 41: return "XAUTH";
        case 42: return "CHARSET";
        case 43: return "Telnet Remote Serial Port (RSP)";
        case 44: return "Com Port Control Option";
        case 45: return "Telnet Suppress Local Echo";
        case 46: return "Telnet Start TLS";
        case 47: return "KERMIT";
        case 48: return "SEND-URL";
        case 49: return "FORWARD_X";
        case 138: return "TELOPT PRAGMA LOGON";
        case 139: return "TELOPT SSPI LOGON";
        case 140: return "TELOPT PRAGMA HEARTBEAT";
        case 255: return "Extended-Options-List";
        default:
            if ((option >= 50) && (option <= 137)) {
                return "Unassigned";
            }
            return "UNKNOWN - OTHER";
        }
    }

    /**
     * Send a DO/DON'T/WILL/WON'T response to the remote side.
     *
     * @param response a TELNET_DO/DONT/WILL/WONT byte
     * @param option telnet option byte (binary mode, term type, etc.)
     */
    private void respond(final int response,
        final int option) throws IOException {

        byte [] buffer = new byte[3];
        buffer[0] = (byte)TELNET_IAC;
        buffer[1] = (byte)response;
        buffer[2] = (byte)option;

        master.output.write(buffer);
    }

    /**
     * Tell the remote side we WILL support an option.
     *
     * @param option telnet option byte (binary mode, term type, etc.)
     */
    private void WILL(final int option) throws IOException {
        respond(TELNET_WILL, option);
    }

    /**
     * Tell the remote side we WON'T support an option.
     *
     * @param option telnet option byte (binary mode, term type, etc.)
     */
    private void WONT(final int option) throws IOException {
        respond(TELNET_WONT, option);
    }

    /**
     * Tell the remote side we DO support an option.
     *
     * @param option telnet option byte (binary mode, term type, etc.)
     */
    private void DO(final int option) throws IOException {
        respond(TELNET_DO, option);
    }

    /**
     * Tell the remote side we DON'T support an option.
     *
     * @param option telnet option byte (binary mode, term type, etc.)
     */
    private void DONT(final int option) throws IOException {
        respond(TELNET_DONT, option);
    }

    /**
     * Tell the remote side we WON't or DON'T support an option.
     *
     * @param remoteQuery a TELNET_DO/DONT/WILL/WONT byte
     * @param option telnet option byte (binary mode, term type, etc.)
     */
    private void refuse(final int remoteQuery,
        final int option) throws IOException {

        if (remoteQuery == TELNET_DO) {
            WONT(option);
        } else {
            DONT(option);
        }
    }

    /**
     * Build sub-negotiation packet (RFC 855)
     *
     * @param option telnet option
     * @param response output buffer of response bytes
     */
    private void telnetSendSubnegResponse(final int option,
        final byte [] response) throws IOException {

        byte [] buffer = new byte[response.length + 5];
        buffer[0] = (byte)TELNET_IAC;
        buffer[1] = (byte)TELNET_SB;
        buffer[2] = (byte)option;
        System.arraycopy(response, 0, buffer, 3, response.length);
        buffer[response.length + 3] = (byte)TELNET_IAC;
        buffer[response.length + 4] = (byte)TELNET_SE;
        master.output.write(buffer);
    }

    /**
     * Telnet option: Terminal Speed (RFC 1079).  Client side.
     */
    private void telnetSendTerminalSpeed() throws IOException {
        byte [] response = {0, '3', '8', '4', '0', '0', ',',
                            '3', '8', '4', '0', '0'};
        telnetSendSubnegResponse(32, response);
    }

    /**
     * Telnet option: Terminal Type (RFC 1091).  Client side.
     */
    private void telnetSendTerminalType() throws IOException {
        byte [] response = {0, 'v', 't', '1', '0', '0' };
        telnetSendSubnegResponse(24, response);
    }

    /**
     * Telnet option: Terminal Type (RFC 1091).  Server side.
     */
    private void requestTerminalType() throws IOException {
        byte [] response = new byte[1];
        response[0] = 1;
        telnetSendSubnegResponse(24, response);
    }

    /**
     * Telnet option: Terminal Speed (RFC 1079).  Server side.
     */
    private void requestTerminalSpeed() throws IOException {
        byte [] response = new byte[1];
        response[0] = 1;
        telnetSendSubnegResponse(32, response);
    }

    /**
     * Telnet option: New Environment (RFC 1572).  Server side.
     */
    private void requestEnvironment() throws IOException {
        byte [] response = new byte[1];
        response[0] = 1;
        telnetSendSubnegResponse(39, response);
    }

    /**
     * Send the options we want to negotiate on:
     *     Binary Transmission           RFC 856
     *     Suppress Go Ahead             RFC 858
     *     Negotiate About Window Size   RFC 1073
     *     Terminal Type                 RFC 1091
     *     Terminal Speed                RFC 1079
     *     New Environment               RFC 1572
     *
     * When run as a server:
     *     Echo
     */
    private void telnetSendOptions() throws IOException {
        if (master.nvt.binaryMode == false) {
            // Binary Transmission: must ask both do and will
            DO(0);
            WILL(0);
        }

        if (master.nvt.goAhead == true) {
            // Suppress Go Ahead
            DO(3);
            WILL(3);
        }

        // Server only options
        if (master.nvt.isServer == true) {
            // Enable Echo - I echo to them, they do not echo back to me.
            DONT(1);
            WILL(1);

            if (master.nvt.doTermType == true) {
                // Terminal type - request it
                DO(24);
            }

            if (master.nvt.doTermSpeed == true) {
                // Terminal speed - request it
                DO(32);
            }

            if (master.nvt.doNAWS == true) {
                // NAWS - request it
                DO(31);
            }

            if (master.nvt.doEnvironment == true) {
                // Environment - request it
                DO(39);
            }

        } else {

            if (master.nvt.doTermType == true) {
                // Terminal type - request it
                WILL(24);
            }

            if (master.nvt.doTermSpeed == true) {
                // Terminal speed - request it
                WILL(32);
            }

            if (master.nvt.doNAWS == true) {
                // NAWS - request it
                WILL(31);
            }

            if (master.nvt.doEnvironment == true) {
                // Environment - request it
                WILL(39);
            }

        }
    }

    /**
     * New Environment parsing state.
     */
    private enum EnvState {
        INIT,
        TYPE,
        NAME,
        VALUE
    }

    /**
     * Handle the New Environment option.  Note that this implementation
     * fails to handle ESC as defined in RFC 1572.
     */
    private void handleNewEnvironment() {
        Map<StringBuilder, StringBuilder> newEnv = new TreeMap<StringBuilder, StringBuilder>();
        EnvState state = EnvState.INIT;
        StringBuilder name = new StringBuilder();
        StringBuilder value = new StringBuilder();

        for (int i = 0; i < subnegBuffer.length; i++) {
            byte b = subnegBuffer[i];

            switch (state) {

            case INIT:
                // Looking for "IS"
                if (b == 0) {
                    state = EnvState.TYPE;
                } else {
                    // The other side isn't following the rules, see ya.
                    return;
                }
                break;

            case TYPE:
                // Looking for "VAR" or "USERVAR"
                if (b == 0) {
                    // VAR
                    state = EnvState.NAME;
                    name = new StringBuilder();
                } else if (b == 3) {
                    // USERVAR
                    state = EnvState.NAME;
                    name = new StringBuilder();
                } else {
                    // The other side isn't following the rules, see ya
                    return;
                }
                break;

            case NAME:
                // Looking for "VALUE" or a name byte
                if (b == 1) {
                    // VALUE
                    state = EnvState.VALUE;
                    value = new StringBuilder();
                } else {
                    // Take it as an environment variable name/key byte
                    name.append((char)b);
                }

                break;

            case VALUE:
                // Looking for "VAR", "USERVAR", or a name byte, or the end
                if (b == 0) {
                    // VAR
                    state = EnvState.NAME;
                    if (value.length() > 0) {
                        newEnv.put(name, value);
                    }
                    name = new StringBuilder();
                } else if (b == 3) {
                    // USERVAR
                    state = EnvState.NAME;
                    if (value.length() > 0) {
                        newEnv.put(name, value);
                    }
                    name = new StringBuilder();
                } else {
                    // Take it as an environment variable value byte
                    value.append((char)b);
                }
                break;
            }
        }

        if ((name.length() > 0) && (value.length() > 0)) {
            newEnv.put(name, value);
        }

        for (StringBuilder key: newEnv.keySet()) {
            if (key.equals("LANG")) {
                language = newEnv.get(key).toString();
            }
            if (key.equals("LOGNAME")) {
                username = newEnv.get(key).toString();
            }
            if (key.equals("USER")) {
                username = newEnv.get(key).toString();
            }
        }
    }

    /**
     * Handle an option sub-negotiation.
     */
    private void handleSubneg() throws IOException {
        byte option;

        // Sanity check: there must be at least 1 byte in subnegBuffer
        if (subnegBuffer.length < 1) {
            // Buffer too small: the other side is a broken telnetd, it did
            // not send the right sub-negotiation data.  Bail out now.
            return;
        }
        option = subnegBuffer[0];

        switch (option) {

        case 24:
            // Terminal Type
            if ((subnegBuffer.length > 1) && (subnegBuffer[1] == 1)) {
                // Server sent "SEND", we say "IS"
                telnetSendTerminalType();
            }
            if ((subnegBuffer.length > 1) && (subnegBuffer[1] == 0)) {
                // Client sent "IS", record it
                StringBuilder terminalString = new StringBuilder();
                for (int i = 2; i < subnegBuffer.length; i++) {
                    terminalString.append((char)subnegBuffer[i]);
                }
                master.nvt.terminal = terminalString.toString();
            }
            break;

        case 32:
            // Terminal Speed
            if ((subnegBuffer.length > 1) && (subnegBuffer[1] == 1)) {
                // Server sent "SEND", we say "IS"
                telnetSendTerminalSpeed();
            }
            if ((subnegBuffer.length > 1) && (subnegBuffer[1] == 0)) {
                // Client sent "IS", record it
                StringBuilder speedString = new StringBuilder();
                for (int i = 2; i < subnegBuffer.length; i++) {
                    speedString.append((char)subnegBuffer[i]);
                }
                String termSpeed = speedString.toString();
            }
            break;

        case 31:
            // NAWS
            if (subnegBuffer.length >= 5) {
                int i = 0;

                i++;
                if (subnegBuffer[i] == TELNET_IAC) {
                    i++;
                }
                windowWidth = subnegBuffer[i] * 256;

                i++;
                if (subnegBuffer[i] == TELNET_IAC) {
                    i++;
                }
                windowWidth += subnegBuffer[i];

                i++;
                if (subnegBuffer[i] == TELNET_IAC) {
                    i++;
                }
                windowHeight = subnegBuffer[i] * 256;

                i++;
                if (subnegBuffer[i] == TELNET_IAC) {
                    i++;
                }
                windowHeight += subnegBuffer[i];
            }
            break;

        case 39:
            // Environment
            handleNewEnvironment();
            break;

        default:
            // Ignore this one
            break;
        }
    }


}
