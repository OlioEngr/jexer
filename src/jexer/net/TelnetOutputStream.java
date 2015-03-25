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

import java.io.OutputStream;
import java.io.IOException;

/**
 * TelnetOutputStream works with TelnetSocket to perform the telnet protocol.
 */
public final class TelnetOutputStream extends OutputStream {

    /**
     * The root TelnetSocket that has my telnet protocol state.
     */
    private TelnetSocket master;

    /**
     * The raw socket's OutputStream.
     */
    private OutputStream output;

    /**
     * Package private constructor.
     *
     * @param master the master TelnetSocket
     * @param output the underlying socket's OutputStream
     */
    TelnetOutputStream(TelnetSocket master, OutputStream output) {
        this.master = master;
        this.output = output;
    }

    // OutputStream interface -------------------------------------------------

    /**
     * Closes this output stream and releases any system resources associated
     * with this stream.
     */
    @Override
    public void close() throws IOException {
        if (output != null) {
            output.close();
            output = null;
        }
    }

    /**
     * Flushes this output stream and forces any buffered output bytes to be
     * written out.
     */
    @Override
    public void flush() throws IOException {
        if ((master.nvt.binaryMode == false) && (master.nvt.writeCR == true)) {
            // The last byte sent to this.write() was a CR, which was never
            // actually sent.  So send the CR in ascii mode, then flush.
            // CR <anything> -> CR NULL
            output.write(master.C_CR);
            output.write(master.C_NUL);
            master.nvt.writeCR = false;
        }
        output.flush();
    }

    /**
     * Writes b.length bytes from the specified byte array to this output
     * stream.
     */
    @Override
    public void write(byte[] b) throws IOException {
        writeImpl(b, 0, b.length);
    }

    /**
     * Writes len bytes from the specified byte array starting at offset off
     * to this output stream.
     */
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        writeImpl(b, off, len);
    }

    /**
     * Writes the specified byte to this output stream.
     */
    @Override
    public void write(int b) throws IOException {
        byte [] bytes = new byte[1];
        bytes[0] = (byte)b;
        writeImpl(bytes, 0, 1);
    }

    /**
     * Writes b.length bytes from the specified byte array to this output
     * stream.  Note package private access.
     */
    void rawWrite(byte[] b) throws IOException {
        output.write(b, 0, b.length);
    }

    /**
     * Writes len bytes from the specified byte array starting at offset off
     * to this output stream.
     */
    private void writeImpl(final byte[] b, final int off,
        final int len) throws IOException {

        byte [] writeBuffer = new byte[Math.max(len, 4)];
        int writeBufferI = 0;

        for (int i = 0; i < len; i++) {
            if (writeBufferI >= writeBuffer.length - 4) {
                // Flush what we have generated so far and reset the buffer,
                // because the next byte could generate up to 4 output bytes
                // (CR <something> <IAC> <IAC>).
                super.write(writeBuffer, 0, writeBufferI);
                writeBufferI = 0;
            }

            // Pull the next byte
            byte ch = b[i + off];

            if (master.nvt.binaryMode == true) {

                if (ch == master.TELNET_IAC) {
                    // IAC -> IAC IAC
                    writeBuffer[writeBufferI++] = (byte)master.TELNET_IAC;
                    writeBuffer[writeBufferI++] = (byte)master.TELNET_IAC;
                } else {
                    // Anything else -> just send
                    writeBuffer[writeBufferI++] = ch;
                }
                continue;
            }

            // Non-binary mode: more complicated.  We use writeCR to handle
            // the case that the last byte of b was a CR.

            // Bare carriage return -> CR NUL
            if (ch == master.C_CR) {
                if (master.nvt.writeCR == true) {
                    // Flush the previous CR to the stream.
                    // CR <anything> -> CR NULL
                    writeBuffer[writeBufferI++] = (byte)master.C_CR;
                    writeBuffer[writeBufferI++] = (byte)master.C_NUL;
                }
                master.nvt.writeCR = true;
            } else if (ch == master.C_LF) {
                if (master.nvt.writeCR == true) {
                    // CR LF -> CR LF
                    writeBuffer[writeBufferI++] = (byte)master.C_CR;
                    writeBuffer[writeBufferI++] = (byte)master.C_LF;
                    master.nvt.writeCR = false;
                } else {
                    // Bare LF -> LF
                    writeBuffer[writeBufferI++] = ch;
                }
            } else if (ch == master.TELNET_IAC) {
                if (master.nvt.writeCR == true) {
                    // CR <anything> -> CR NULL
                    writeBuffer[writeBufferI++] = (byte)master.C_CR;
                    writeBuffer[writeBufferI++] = (byte)master.C_NUL;
                    master.nvt.writeCR = false;
                }
                // IAC -> IAC IAC
                writeBuffer[writeBufferI++] = (byte)master.TELNET_IAC;
                writeBuffer[writeBufferI++] = (byte)master.TELNET_IAC;
            } else {
                if (master.nvt.writeCR == true) {
                    // CR <anything> -> CR NULL
                    writeBuffer[writeBufferI++] = (byte)master.C_CR;
                    writeBuffer[writeBufferI++] = (byte)master.C_NUL;
                    master.nvt.writeCR = false;
                } else {
                    // Normal character */
                    writeBuffer[writeBufferI++] = ch;
                }
            }

        } // while (i < userbuf.length)

        if (writeBufferI > 0) {
            // Flush what we have generated so far and reset the buffer.
            super.write(writeBuffer, 0, writeBufferI);
        }
    }


}
