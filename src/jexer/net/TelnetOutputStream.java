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
        // TODO
    }

    /**
     * Flushes this output stream and forces any buffered output bytes to be
     * written out.
     */
    @Override
    public void flush() throws IOException {
    }

    /**
     * Writes b.length bytes from the specified byte array to this output
     * stream.
     */
    @Override
    public void write(byte[] b) throws IOException {
        // TODO
    }

    /**
     * Writes len bytes from the specified byte array starting at offset off
     * to this output stream.
     */
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        // TODO
    }

    /**
     * Writes the specified byte to this output stream.
     */
    @Override
    public void write(int b) throws IOException {
        // TODO
    }

}
