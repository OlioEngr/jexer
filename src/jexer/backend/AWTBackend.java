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
package jexer.backend;

import java.util.List;

import jexer.event.TInputEvent;
import jexer.io.AWTScreen;
import jexer.io.AWTTerminal;

/**
 * This class uses standard AWT calls to handle screen, keyboard, and mouse
 * I/O.
 */
public final class AWTBackend extends Backend {

    /**
     * Input events are processed by this Terminal.
     */
    private AWTTerminal terminal;

    /**
     * Public constructor.
     *
     * @param listener the object this backend needs to wake up when new
     * input comes in
     */
    public AWTBackend(final Object listener) {
        // Create a screen
        AWTScreen screen = new AWTScreen();
        this.screen = screen;

        // Create the AWT event listeners
        terminal = new AWTTerminal(listener, screen);

        // Hang onto the session info
        this.sessionInfo = terminal.getSessionInfo();
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
