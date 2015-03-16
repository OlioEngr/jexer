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
package jexer.io;

import java.util.List;
import java.util.LinkedList;

import jexer.TKeypress;
import jexer.bits.Color;
import jexer.event.TInputEvent;
import jexer.event.TKeypressEvent;
import jexer.event.TMouseEvent;
import jexer.event.TResizeEvent;
import jexer.session.SessionInfo;
import jexer.session.TSessionInfo;
import static jexer.TKeypress.*;

/**
 * This class reads keystrokes and mouse events from an AWT Frame.
 */
public final class AWTTerminal {

    /**
     * The session information.
     */
    private SessionInfo sessionInfo;

    /**
     * Getter for sessionInfo.
     *
     * @return the SessionInfo
     */
    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }

    /**
     * The event queue, filled up by a thread reading on input.
     */
    private List<TInputEvent> eventQueue;

    /**
     * If true, we want the reader thread to exit gracefully.
     */
    private boolean stopReaderThread;

    /**
     * The reader thread.
     */
    private Thread readerThread;

    /**
     * true if mouse1 was down.  Used to report mouse1 on the release event.
     */
    private boolean mouse1;

    /**
     * true if mouse2 was down.  Used to report mouse2 on the release event.
     */
    private boolean mouse2;

    /**
     * true if mouse3 was down.  Used to report mouse3 on the release event.
     */
    private boolean mouse3;

    /**
     * Check if there are events in the queue.
     *
     * @return if true, getEvents() has something to return to the backend
     */
    public boolean hasEvents() {
        synchronized (eventQueue) {
            return (eventQueue.size() > 0);
        }
    }

    /**
     * Constructor sets up state for getEvent().
     *
     * @param screen the top-level AWT frame 
     */
    public AWTTerminal(final AWTScreen screen) {
        mouse1           = false;
        mouse2           = false;
        mouse3           = false;
        stopReaderThread = false;
        sessionInfo      = new TSessionInfo();
        eventQueue       = new LinkedList<TInputEvent>();
    }

    /**
     * Restore terminal to normal state.
     */
    public void shutdown() {
        // System.err.println("=== shutdown() ==="); System.err.flush();
    }

    /**
     * Return any events in the IO queue.
     *
     * @param queue list to append new events to
     */
    public void getEvents(final List<TInputEvent> queue) {
        synchronized (eventQueue) {
            if (eventQueue.size() > 0) {
                synchronized (queue) {
                    queue.addAll(eventQueue);
                }
                eventQueue.clear();
            }
        }
    }

    /**
     * Return any events in the IO queue due to timeout.
     *
     * @param queue list to append new events to
     */
    public void getIdleEvents(final List<TInputEvent> queue) {

        // Insert any polling action here...

        // Return any events that showed up
        synchronized (eventQueue) {
            if (eventQueue.size() > 0) {
                synchronized (queue) {
                    queue.addAll(eventQueue);
                }
                eventQueue.clear();
            }
        }
    }

}
