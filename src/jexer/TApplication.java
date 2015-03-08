/**
 * Jexer - Java Text User Interface - demonstration program
 *
 * Version: $Id$
 *
 * Author: Kevin Lamonte, <a href="mailto:kevin.lamonte@gmail.com">kevin.lamonte@gmail.com</a>
 *
 * License: LGPLv3 or later
 *
 * Copyright: This module is licensed under the GNU Lesser General
 * Public License Version 3.  Please see the file "COPYING" in this
 * directory for more information about the GNU Lesser General Public
 * License Version 3.
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
 */
package jexer;

/**
 * TApplication sets up a full Text User Interface application.
 */
public class TApplication {

    /**
     * Run this application until it exits, using stdin and stdout
     */
    final public void run() {

	/*
	while (quit == false) {

	    // Timeout is in milliseconds, so default timeout after 1
	    // second of inactivity.
	    uint timeout = getSleepTime(1000);
	    // std.stdio.stderr.writefln("poll() timeout: %d", timeout);

	    if (eventQueue.length > 0) {
		// Do not wait if there are definitely events waiting to be
		// processed or a screen redraw to do.
		timeout = 0;
	    }

	    // Pull any pending input events
	    TInputEvent [] events = backend.getEvents(timeout);
	    metaHandleEvents(events);

	    // Process timers and call doIdle()'s
	    doIdle();

	    // Update the screen
	    drawAll();
	}

	// Shutdown the fibers
	eventQueue.length = 0;
	if (secondaryEventFiber !is null) {
	    assert(secondaryEventReceiver !is null);
	    secondaryEventReceiver = null;
	    if (secondaryEventFiber.state == Fiber.State.HOLD) {
		// Wake up the secondary handler so that it can exit.
		secondaryEventFiber.call();
	    }
	}

	if (primaryEventFiber.state == Fiber.State.HOLD) {
	    // Wake up the primary handler so that it can exit.
	    primaryEventFiber.call();
	}

	backend.shutdown();
	 */

	System.out.println("Hello");
    }
}
