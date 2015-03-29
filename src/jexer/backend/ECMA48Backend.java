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
package jexer.backend;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import jexer.event.TInputEvent;
import jexer.io.ECMA48Screen;
import jexer.io.ECMA48Terminal;

/**
 * This class uses an xterm/ANSI X3.64/ECMA-48 type terminal to provide a
 * screen, keyboard, and mouse to TApplication.
 */
public final class ECMA48Backend extends Backend {

    /**
     * Input events are processed by this Terminal.
     */
    private ECMA48Terminal terminal;

    /**
     * Public constructor.
     *
     * @param listener the object this backend needs to wake up when new
     * input comes in
     * @param input an InputStream connected to the remote user, or null for
     * System.in.  If System.in is used, then on non-Windows systems it will
     * be put in raw mode; shutdown() will (blindly!) put System.in in cooked
     * mode.  input is always converted to a Reader with UTF-8 encoding.
     * @param output an OutputStream connected to the remote user, or null
     * for System.out.  output is always converted to a Writer with UTF-8
     * encoding.
     * @throws UnsupportedEncodingException if an exception is thrown when
     * creating the InputStreamReader
     */
    public ECMA48Backend(final Object listener, final InputStream input,
        final OutputStream output) throws UnsupportedEncodingException {

        // Create a terminal and explicitly set stdin into raw mode
        terminal = new ECMA48Terminal(listener, input, output);

        // Keep the terminal's sessionInfo so that TApplication can see it
        sessionInfo = terminal.getSessionInfo();

        // Create a screen
        screen = new ECMA48Screen(terminal);

        // Clear the screen
        terminal.getOutput().write(terminal.clearAll());
        terminal.flush();
    }

    /**
     * Sync the logical screen to the physical device.
     */
    @Override
    public void flushScreen() {
        screen.flushPhysical();
    }

    /**
     * Get keyboard, mouse, and screen resize events.
     *
     * @param queue list to append new events to
     */
    @Override
    public void getEvents(final List<TInputEvent> queue) {
        if (terminal.hasEvents()) {
            terminal.getEvents(queue);
        }
    }

    /**
     * Close the I/O, restore the console, etc.
     */
    @Override
    public void shutdown() {
        terminal.shutdown();
    }

}
