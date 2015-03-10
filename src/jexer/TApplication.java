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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import jexer.bits.CellAttributes;
import jexer.bits.ColorTheme;
import jexer.bits.GraphicsChars;
import jexer.event.TCommandEvent;
import jexer.event.TInputEvent;
import jexer.event.TKeypressEvent;
import jexer.event.TMouseEvent;
import jexer.event.TResizeEvent;
import jexer.backend.Backend;
import jexer.backend.ECMA48Backend;
import static jexer.TCommand.*;
import static jexer.TKeypress.*;

/**
 * TApplication sets up a full Text User Interface application.
 */
public class TApplication {

    /**
     * Access to the physical screen, keyboard, and mouse.
     */
    public Backend backend;

    /**
     * Actual mouse coordinate X
     */
    private int mouseX;

    /**
     * Actual mouse coordinate Y
     */
    private int mouseY;

    /**
     * Event queue that will be drained by either primary or secondary Fiber
     */
    private List<TInputEvent> eventQueue;

    /**
     * Windows and widgets pull colors from this ColorTheme.
     */
    public ColorTheme theme;

    /**
     * When true, exit the application.
     */
    public boolean quit = false;

    /**
     * When true, repaint the entire screen.
     */
    public boolean repaint = true;

    /**
     * When true, just flush updates from the screen.
     */
    public boolean flush = false;

    /**
     * Y coordinate of the top edge of the desktop.
     */
    static public final int desktopTop = 1;

    /**
     * Y coordinate of the bottom edge of the desktop.
     */
    public int desktopBottom;

    /**
     * Public constructor.
     *
     * @param input an InputStream connected to the remote user, or null for
     * System.in.  If System.in is used, then on non-Windows systems it will
     * be put in raw mode; shutdown() will (blindly!) put System.in in cooked
     * mode.  input is always converted to a Reader with UTF-8 encoding.
     * @param output an OutputStream connected to the remote user, or null
     * for System.out.  output is always converted to a Writer with UTF-8
     * encoding.
     */
    public TApplication(InputStream input, OutputStream output) throws UnsupportedEncodingException {

	backend       = new ECMA48Backend(input, output);
	theme         = new ColorTheme();
	desktopBottom = backend.screen.getHeight() - 1;
	eventQueue    = new LinkedList<TInputEvent>();
    }

    /**
     * Invert the cell at the mouse pointer position.
     */
    private void drawMouse() {
	CellAttributes attr = backend.screen.getAttrXY(mouseX, mouseY);
	attr.foreColor = attr.foreColor.invert();
	attr.backColor = attr.backColor.invert();
	backend.screen.putAttrXY(mouseX, mouseY, attr, false);
	flush = true;

	/*
	if (windows.length == 0) {
	    repaint = true;
	}
	 */
	// TODO: remove this repaint after the above if (windows.length == 0)
	// can be used again.
	repaint = true;
    }

    /**
     * Draw everything.
     */
    final public void drawAll() {
	if ((flush) && (!repaint)) {
	    backend.flushScreen();
	    flush = false;
	    return;
	}

	if (!repaint) {
	    return;
	}

	// If true, the cursor is not visible
	boolean cursor = false;

	// Start with a clean screen
	backend.screen.clear();

	// Draw the background
	CellAttributes background = theme.getColor("tapplication.background");
	backend.screen.putAll(GraphicsChars.HATCH, background);

	/*
	// Draw each window in reverse Z order
	TWindow [] sorted = windows.dup;
	sorted.sort.reverse;
	foreach (w; sorted) {
	    w.drawChildren();
	}

	// Draw the blank menubar line - reset the screen clipping first so
	// it won't trim it out.
	backend.screen.resetClipping();
	backend.screen.hLineXY(0, 0, backend.screen.getWidth(), ' ',
	    theme.getColor("tmenu"));
	// Now draw the menus.
	int x = 1;
	foreach (m; menus) {
	    CellAttributes menuColor;
	    CellAttributes menuMnemonicColor;
	    if (m.active) {
		menuColor = theme.getColor("tmenu.highlighted");
		menuMnemonicColor = theme.getColor("tmenu.mnemonic.highlighted");
	    } else {
		menuColor = theme.getColor("tmenu");
		menuMnemonicColor = theme.getColor("tmenu.mnemonic");
	    }
	    // Draw the menu title
	    backend.screen.hLineXY(x, 0, cast(int)m.title.length + 2, ' ',
		menuColor);
	    backend.screen.putStrXY(x + 1, 0, m.title, menuColor);
	    // Draw the highlight character
	    backend.screen.putCharXY(x + 1 + m.mnemonic.shortcutIdx, 0,
		m.mnemonic.shortcut, menuMnemonicColor);

	    if (m.active) {
		m.drawChildren();
		// Reset the screen clipping so we can draw the next title.
		backend.screen.resetClipping();
	    }
	    x += m.title.length + 2;
	}

	foreach (m; subMenus) {
	    // Reset the screen clipping so we can draw the next sub-menu.
	    backend.screen.resetClipping();
	    m.drawChildren();
	}
	 */

	// Draw the mouse pointer
	drawMouse();

	/*
	// Place the cursor if it is visible
	TWidget activeWidget = null;
	if (sorted.length > 0) {
	    activeWidget = sorted[$ - 1].getActiveChild();
	    if (activeWidget.hasCursor) {
		backend.screen.putCursor(true, activeWidget.getCursorAbsoluteX(),
		    activeWidget.getCursorAbsoluteY());
		cursor = true;
	    }
	}

	// Kill the cursor
	if (cursor == false) {
	    backend.screen.hideCursor();
	}
	 */

	// Flush the screen contents
	backend.flushScreen();

	repaint = false;
	flush = false;
    }

    /**
     * Run this application until it exits, using stdin and stdout
     */
    public final void run() {

	while (quit == false) {
	    // Timeout is in milliseconds, so default timeout after 1 second
	    // of inactivity.
	    int timeout = getSleepTime(1000);
	    // std.stdio.stderr.writefln("poll() timeout: %d", timeout);

	    if (eventQueue.size() > 0) {
		// Do not wait if there are definitely events waiting to be
		// processed or a screen redraw to do.
		timeout = 0;
	    }

	    // Pull any pending input events
	    List<TInputEvent> events = backend.getEvents(timeout);
	    metaHandleEvents(events);

	    // Process timers and call doIdle()'s
	    doIdle();

	    // Update the screen
	    drawAll();
	}

	/*

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
	 */

	backend.shutdown();
    }

    /**
     * Peek at certain application-level events, add to eventQueue, and wake
     * up the consuming Fiber.
     *
     * @param events the input events to consume
     */
    private void metaHandleEvents(List<TInputEvent> events) {

	for (TInputEvent event: events) {

	    /*
	    System.err.printf(String.format("metaHandleEvents event: %s\n",
		    event)); System.err.flush();
	     */

	    if (quit == true) {
		// Do no more processing if the application is already trying
		// to exit.
		return;
	    }

	    // DEBUG
	    if (event instanceof TKeypressEvent) {
		TKeypressEvent keypress = (TKeypressEvent)event;
		if (keypress.key.equals(kbAltX)) {
		    quit = true;
		    return;
		}
	    }
	    // DEBUG

	    // Special application-wide events -------------------------------

	    // Abort everything
	    if (event instanceof TCommandEvent) {
		TCommandEvent command = (TCommandEvent)event;
		if (command.cmd.equals(cmAbort)) {
		    quit = true;
		    return;
		}
	    }

	    // Screen resize
	    if (event instanceof TResizeEvent) {
		TResizeEvent resize = (TResizeEvent)event;
		backend.screen.setDimensions(resize.width, resize.height);
		desktopBottom = backend.screen.getHeight() - 1;
		repaint = true;
		mouseX = 0;
		mouseY = 0;
		continue;
	    }

	    // Peek at the mouse position
	    if (event instanceof TMouseEvent) {
		TMouseEvent mouse = (TMouseEvent)event;
		if ((mouseX != mouse.x) || (mouseY != mouse.y)) {
		    mouseX = mouse.x;
		    mouseY = mouse.y;
		    drawMouse();
		}
	    }

	    /*

	    // Put into the main queue
	    addEvent(event);

	    // Have one of the two consumer Fibers peel the events off
	    // the queue.
	    if (secondaryEventFiber !is null) {
		assert(secondaryEventFiber.state == Fiber.State.HOLD);

		// Wake up the secondary handler for these events
		secondaryEventFiber.call();
	    } else {
		assert(primaryEventFiber.state == Fiber.State.HOLD);

		// Wake up the primary handler for these events
		primaryEventFiber.call();
	    }
	     */

	} // for (TInputEvent event: events)

    }

    /**
     * Do stuff when there is no user input.
     */
    private void doIdle() {
	/*
	// Now run any timers that have timed out
	auto now = Clock.currTime;
	TTimer [] keepTimers;
	foreach (t; timers) {
	    if (t.nextTick < now) {
		t.tick();
		if (t.recurring == true) {
		    keepTimers ~= t;
		}
	    } else {
		keepTimers ~= t;
	    }
	}
	timers = keepTimers;

	// Call onIdle's
	foreach (w; windows) {
	    w.onIdle();
	}
	 */
    }

    /**
     * Get the amount of time I can sleep before missing a Timer tick.
     *
     * @param timeout = initial (maximum) timeout
     * @return number of milliseconds between now and the next timer event
     */
    protected int getSleepTime(int timeout) {
	/*
	auto now = Clock.currTime;
	auto sleepTime = dur!("msecs")(timeout);
	foreach (t; timers) {
	    if (t.nextTick < now) {
		return 0;
	    }
	    if ((t.nextTick > now) &&
		((t.nextTick - now) < sleepTime)
	    ) {
		sleepTime = t.nextTick - now;
	    }
	}
	assert(sleepTime.total!("msecs")() >= 0);
	return cast(uint)sleepTime.total!("msecs")();
	 */
	return 0;
    }

}
