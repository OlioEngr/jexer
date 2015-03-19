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

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
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
import jexer.session.TTYSessionInfo;
import static jexer.TKeypress.*;

/**
 * This class reads keystrokes and mouse events and emits output to ANSI
 * X3.64 / ECMA-48 type terminals e.g. xterm, linux, vt100, ansi.sys, etc.
 */
public final class ECMA48Terminal implements Runnable {

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
     * Parameters being collected.  E.g. if the string is \033[1;3m, then
     * params[0] will be 1 and params[1] will be 3.
     */
    private ArrayList<String> params;

    /**
     * params[paramI] is being appended to.
     */
    private int paramI;

    /**
     * States in the input parser.
     */
    private enum ParseState {
        GROUND,
        ESCAPE,
        ESCAPE_INTERMEDIATE,
        CSI_ENTRY,
        CSI_PARAM,
        // CSI_INTERMEDIATE,
        MOUSE
    }

    /**
     * Current parsing state.
     */
    private ParseState state;

    /**
     * The time we entered ESCAPE.  If we get a bare escape without a code
     * following it, this is used to return that bare escape.
     */
    private long escapeTime;

    /**
     * The time we last checked the window size.  We try not to spawn stty
     * more than once per second.
     */
    private long windowSizeTime;

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
     * Cache the cursor visibility value so we only emit the sequence when we
     * need to.
     */
    private boolean cursorOn = true;

    /**
     * Cache the last window size to figure out if a TResizeEvent needs to be
     * generated.
     */
    private TResizeEvent windowResize = null;

    /**
     * If true, then we changed System.in and need to change it back.
     */
    private boolean setRawMode;

    /**
     * The terminal's input.  If an InputStream is not specified in the
     * constructor, then this InputStreamReader will be bound to System.in
     * with UTF-8 encoding.
     */
    private Reader input;

    /**
     * The terminal's raw InputStream.  If an InputStream is not specified in
     * the constructor, then this InputReader will be bound to System.in.
     * This is used by run() to see if bytes are available() before calling
     * (Reader)input.read().
     */
    private InputStream inputStream;

    /**
     * The terminal's output.  If an OutputStream is not specified in the
     * constructor, then this PrintWriter will be bound to System.out with
     * UTF-8 encoding.
     */
    private PrintWriter output;

    /**
     * When true, the terminal is sending non-UTF8 bytes when reporting mouse
     * events.
     *
     * TODO: Add broken mouse detection back into the reader.
     */
    private boolean brokenTerminalUTFMouse = false;

    /**
     * Get the output writer.
     *
     * @return the Writer
     */
    public PrintWriter getOutput() {
        return output;
    }

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
     * Call 'stty' to set cooked mode.
     *
     * <p>Actually executes '/bin/sh -c stty sane cooked &lt; /dev/tty'
     */
    private void sttyCooked() {
        doStty(false);
    }

    /**
     * Call 'stty' to set raw mode.
     *
     * <p>Actually executes '/bin/sh -c stty -ignbrk -brkint -parmrk -istrip
     * -inlcr -igncr -icrnl -ixon -opost -echo -echonl -icanon -isig -iexten
     * -parenb cs8 min 1 &lt; /dev/tty'
     */
    private void sttyRaw() {
        doStty(true);
    }

    /**
     * Call 'stty' to set raw or cooked mode.
     *
     * @param mode if true, set raw mode, otherwise set cooked mode
     */
    private void doStty(final boolean mode) {
        String [] cmdRaw = {
            "/bin/sh", "-c", "stty -ignbrk -brkint -parmrk -istrip -inlcr -igncr -icrnl -ixon -opost -echo -echonl -icanon -isig -iexten -parenb cs8 min 1 < /dev/tty"
        };
        String [] cmdCooked = {
            "/bin/sh", "-c", "stty sane cooked < /dev/tty"
        };
        try {
            Process process;
            if (mode) {
                process = Runtime.getRuntime().exec(cmdRaw);
            } else {
                process = Runtime.getRuntime().exec(cmdCooked);
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            String line = in.readLine();
            if ((line != null) && (line.length() > 0)) {
                System.err.println("WEIRD?! Normal output from stty: " + line);
            }
            while (true) {
                BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream(), "UTF-8"));
                line = err.readLine();
                if ((line != null) && (line.length() > 0)) {
                    System.err.println("Error output from stty: " + line);
                }
                try {
                    process.waitFor();
                    break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            int rc = process.exitValue();
            if (rc != 0) {
                System.err.println("stty returned error code: " + rc);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructor sets up state for getEvent().
     *
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
    public ECMA48Terminal(final InputStream input,
        final OutputStream output) throws UnsupportedEncodingException {

        reset();
        mouse1           = false;
        mouse2           = false;
        mouse3           = false;
        stopReaderThread = false;

        if (input == null) {
            // inputStream = System.in;
            inputStream = new FileInputStream(FileDescriptor.in);
            sttyRaw();
            setRawMode = true;
        } else {
            inputStream = input;
        }
        this.input = new InputStreamReader(inputStream, "UTF-8");

        // TODO: include TelnetSocket from NIB and have it implement
        // SessionInfo
        if (input instanceof SessionInfo) {
            sessionInfo = (SessionInfo) input;
        }
        if (sessionInfo == null) {
            if (input == null) {
                // Reading right off the tty
                sessionInfo = new TTYSessionInfo();
            } else {
                sessionInfo = new TSessionInfo();
            }
        }

        if (output == null) {
            this.output = new PrintWriter(new OutputStreamWriter(System.out,
                    "UTF-8"));
        } else {
            this.output = new PrintWriter(new OutputStreamWriter(output,
                    "UTF-8"));
        }

        // Enable mouse reporting and metaSendsEscape
        this.output.printf("%s%s", mouse(true), xtermMetaSendsEscape(true));

        // Hang onto the window size
        windowResize = new TResizeEvent(TResizeEvent.Type.SCREEN,
            sessionInfo.getWindowWidth(), sessionInfo.getWindowHeight());

        // Spin up the input reader
        eventQueue = new LinkedList<TInputEvent>();
        readerThread = new Thread(this);
        readerThread.start();
    }

    /**
     * Restore terminal to normal state.
     */
    public void shutdown() {

        // System.err.println("=== shutdown() ==="); System.err.flush();

        // Tell the reader thread to stop looking at input
        stopReaderThread = true;
        try {
            readerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Disable mouse reporting and show cursor
        output.printf("%s%s%s", mouse(false), cursor(true), normal());
        output.flush();

        if (setRawMode) {
            sttyCooked();
            setRawMode = false;
            // We don't close System.in/out
        } else {
            // Shut down the streams, this should wake up the reader thread
            // and make it exit.
            try {
                if (input != null) {
                    input.close();
                    input = null;
                }
                if (output != null) {
                    output.close();
                    output = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Flush output.
     */
    public void flush() {
        output.flush();
    }

    /**
     * Reset keyboard/mouse input parser.
     */
    private void reset() {
        state = ParseState.GROUND;
        params = new ArrayList<String>();
        paramI = 0;
        params.clear();
        params.add("");
    }

    /**
     * Produce a control character or one of the special ones (ENTER, TAB,
     * etc.).
     *
     * @param ch Unicode code point
     * @param alt if true, set alt on the TKeypress
     * @return one TKeypress event, either a control character (e.g. isKey ==
     * false, ch == 'A', ctrl == true), or a special key (e.g. isKey == true,
     * fnKey == ESC)
     */
    private TKeypressEvent controlChar(final char ch, final boolean alt) {
        // System.err.printf("controlChar: %02x\n", ch);

        switch (ch) {
        case 0x0D:
            // Carriage return --> ENTER
            return new TKeypressEvent(kbEnter, alt, false, false);
        case 0x0A:
            // Linefeed --> ENTER
            return new TKeypressEvent(kbEnter, alt, false, false);
        case 0x1B:
            // ESC
            return new TKeypressEvent(kbEsc, alt, false, false);
        case '\t':
            // TAB
            return new TKeypressEvent(kbTab, alt, false, false);
        default:
            // Make all other control characters come back as the alphabetic
            // character with the ctrl field set.  So SOH would be 'A' +
            // ctrl.
            return new TKeypressEvent(false, 0, (char)(ch + 0x40),
                alt, true, false);
        }
    }

    /**
     * Produce special key from CSI Pn ; Pm ; ... ~
     *
     * @return one KEYPRESS event representing a special key
     */
    private TInputEvent csiFnKey() {
        int key = 0;
        int modifier = 0;
        if (params.size() > 0) {
            key = Integer.parseInt(params.get(0));
        }
        if (params.size() > 1) {
            modifier = Integer.parseInt(params.get(1));
        }
        boolean alt = false;
        boolean ctrl = false;
        boolean shift = false;

        switch (modifier) {
        case 0:
            // No modifier
            break;
        case 2:
            // Shift
            shift = true;
            break;
        case 3:
            // Alt
            alt = true;
            break;
        case 5:
            // Ctrl
            ctrl = true;
            break;
        default:
            // Unknown modifier, bail out
            return null;
        }

        switch (key) {
        case 1:
            return new TKeypressEvent(kbHome, alt, ctrl, shift);
        case 2:
            return new TKeypressEvent(kbIns, alt, ctrl, shift);
        case 3:
            return new TKeypressEvent(kbDel, alt, ctrl, shift);
        case 4:
            return new TKeypressEvent(kbEnd, alt, ctrl, shift);
        case 5:
            return new TKeypressEvent(kbPgUp, alt, ctrl, shift);
        case 6:
            return new TKeypressEvent(kbPgDn, alt, ctrl, shift);
        case 15:
            return new TKeypressEvent(kbF5, alt, ctrl, shift);
        case 17:
            return new TKeypressEvent(kbF6, alt, ctrl, shift);
        case 18:
            return new TKeypressEvent(kbF7, alt, ctrl, shift);
        case 19:
            return new TKeypressEvent(kbF8, alt, ctrl, shift);
        case 20:
            return new TKeypressEvent(kbF9, alt, ctrl, shift);
        case 21:
            return new TKeypressEvent(kbF10, alt, ctrl, shift);
        case 23:
            return new TKeypressEvent(kbF11, alt, ctrl, shift);
        case 24:
            return new TKeypressEvent(kbF12, alt, ctrl, shift);
        default:
            // Unknown
            return null;
        }
    }

    /**
     * Produce mouse events based on "Any event tracking" and UTF-8
     * coordinates.  See
     * http://invisible-island.net/xterm/ctlseqs/ctlseqs.html#Mouse%20Tracking
     *
     * @return a MOUSE_MOTION, MOUSE_UP, or MOUSE_DOWN event
     */
    private TInputEvent parseMouse() {
        int buttons = params.get(0).charAt(0) - 32;
        int x = params.get(0).charAt(1) - 32 - 1;
        int y = params.get(0).charAt(2) - 32 - 1;

        // Clamp X and Y to the physical screen coordinates.
        if (x >= windowResize.getWidth()) {
            x = windowResize.getWidth() - 1;
        }
        if (y >= windowResize.getHeight()) {
            y = windowResize.getHeight() - 1;
        }

        TMouseEvent.Type eventType = TMouseEvent.Type.MOUSE_DOWN;
        boolean eventMouse1 = false;
        boolean eventMouse2 = false;
        boolean eventMouse3 = false;
        boolean eventMouseWheelUp = false;
        boolean eventMouseWheelDown = false;

        // System.err.printf("buttons: %04x\r\n", buttons);

        switch (buttons) {
        case 0:
            eventMouse1 = true;
            mouse1 = true;
            break;
        case 1:
            eventMouse2 = true;
            mouse2 = true;
            break;
        case 2:
            eventMouse3 = true;
            mouse3 = true;
            break;
        case 3:
            // Release or Move
            if (!mouse1 && !mouse2 && !mouse3) {
                eventType = TMouseEvent.Type.MOUSE_MOTION;
            } else {
                eventType = TMouseEvent.Type.MOUSE_UP;
            }
            if (mouse1) {
                mouse1 = false;
                eventMouse1 = true;
            }
            if (mouse2) {
                mouse2 = false;
                eventMouse2 = true;
            }
            if (mouse3) {
                mouse3 = false;
                eventMouse3 = true;
            }
            break;

        case 32:
            // Dragging with mouse1 down
            eventMouse1 = true;
            mouse1 = true;
            eventType = TMouseEvent.Type.MOUSE_MOTION;
            break;

        case 33:
            // Dragging with mouse2 down
            eventMouse2 = true;
            mouse2 = true;
            eventType = TMouseEvent.Type.MOUSE_MOTION;
            break;

        case 34:
            // Dragging with mouse3 down
            eventMouse3 = true;
            mouse3 = true;
            eventType = TMouseEvent.Type.MOUSE_MOTION;
            break;

        case 96:
            // Dragging with mouse2 down after wheelUp
            eventMouse2 = true;
            mouse2 = true;
            eventType = TMouseEvent.Type.MOUSE_MOTION;
            break;

        case 97:
            // Dragging with mouse2 down after wheelDown
            eventMouse2 = true;
            mouse2 = true;
            eventType = TMouseEvent.Type.MOUSE_MOTION;
            break;

        case 64:
            eventMouseWheelUp = true;
            break;

        case 65:
            eventMouseWheelDown = true;
            break;

        default:
            // Unknown, just make it motion
            eventType = TMouseEvent.Type.MOUSE_MOTION;
            break;
        }
        return new TMouseEvent(eventType, x, y, x, y,
            eventMouse1, eventMouse2, eventMouse3,
            eventMouseWheelUp, eventMouseWheelDown);
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
    private void getIdleEvents(final List<TInputEvent> queue) {
        Date now = new Date();

        // Check for new window size
        long windowSizeDelay = now.getTime() - windowSizeTime;
        if (windowSizeDelay > 1000) {
            sessionInfo.queryWindowSize();
            int newWidth = sessionInfo.getWindowWidth();
            int newHeight = sessionInfo.getWindowHeight();
            if ((newWidth != windowResize.getWidth())
                || (newHeight != windowResize.getHeight())
            ) {
                TResizeEvent event = new TResizeEvent(TResizeEvent.Type.SCREEN,
                    newWidth, newHeight);
                windowResize = new TResizeEvent(TResizeEvent.Type.SCREEN,
                    newWidth, newHeight);
                queue.add(event);
            }
            windowSizeTime = now.getTime();
        }

        // ESCDELAY type timeout
        if (state == ParseState.ESCAPE) {
            long escDelay = now.getTime() - escapeTime;
            if (escDelay > 100) {
                // After 0.1 seconds, assume a true escape character
                queue.add(controlChar((char)0x1B, false));
                reset();
            }
        }
    }

    /**
     * Parses the next character of input to see if an InputEvent is
     * fully here.
     *
     * @param events list to append new events to
     * @param ch Unicode code point
     */
    private void processChar(final List<TInputEvent> events, final char ch) {

        // ESCDELAY type timeout
        Date now = new Date();
        if (state == ParseState.ESCAPE) {
            long escDelay = now.getTime() - escapeTime;
            if (escDelay > 250) {
                // After 0.25 seconds, assume a true escape character
                events.add(controlChar((char)0x1B, false));
                reset();
            }
        }

        // TKeypress fields
        boolean ctrl = false;
        boolean alt = false;
        boolean shift = false;
        char keyCh = ch;
        TKeypress key;

        // System.err.printf("state: %s ch %c\r\n", state, ch);

        switch (state) {
        case GROUND:

            if (ch == 0x1B) {
                state = ParseState.ESCAPE;
                escapeTime = now.getTime();
                return;
            }

            if (ch <= 0x1F) {
                // Control character
                events.add(controlChar(ch, false));
                reset();
                return;
            }

            if (ch >= 0x20) {
                // Normal character
                events.add(new TKeypressEvent(false, 0, ch,
                        false, false, false));
                reset();
                return;
            }

            break;

        case ESCAPE:
            if (ch <= 0x1F) {
                // ALT-Control character
                events.add(controlChar(ch, true));
                reset();
                return;
            }

            if (ch == 'O') {
                // This will be one of the function keys
                state = ParseState.ESCAPE_INTERMEDIATE;
                return;
            }

            // '[' goes to CSI_ENTRY
            if (ch == '[') {
                state = ParseState.CSI_ENTRY;
                return;
            }

            // Everything else is assumed to be Alt-keystroke
            if ((ch >= 'A') && (ch <= 'Z')) {
                shift = true;
            }
            alt = true;
            events.add(new TKeypressEvent(false, 0, ch, alt, ctrl, shift));
            reset();
            return;

        case ESCAPE_INTERMEDIATE:
            if ((ch >= 'P') && (ch <= 'S')) {
                // Function key
                switch (ch) {
                case 'P':
                    events.add(new TKeypressEvent(kbF1));
                    break;
                case 'Q':
                    events.add(new TKeypressEvent(kbF2));
                    break;
                case 'R':
                    events.add(new TKeypressEvent(kbF3));
                    break;
                case 'S':
                    events.add(new TKeypressEvent(kbF4));
                    break;
                default:
                    break;
                }
                reset();
                return;
            }

            // Unknown keystroke, ignore
            reset();
            return;

        case CSI_ENTRY:
            // Numbers - parameter values
            if ((ch >= '0') && (ch <= '9')) {
                params.set(paramI, params.get(paramI) + ch);
                state = ParseState.CSI_PARAM;
                return;
            }
            // Parameter separator
            if (ch == ';') {
                paramI++;
                params.add("");
                return;
            }

            if ((ch >= 0x30) && (ch <= 0x7E)) {
                switch (ch) {
                case 'A':
                    // Up
                    if (params.size() > 1) {
                        if (params.get(1).equals("2")) {
                            shift = true;
                        }
                        if (params.get(1).equals("5")) {
                            ctrl = true;
                        }
                        if (params.get(1).equals("3")) {
                            alt = true;
                        }
                    }
                    events.add(new TKeypressEvent(kbUp, alt, ctrl, shift));
                    reset();
                    return;
                case 'B':
                    // Down
                    if (params.size() > 1) {
                        if (params.get(1).equals("2")) {
                            shift = true;
                        }
                        if (params.get(1).equals("5")) {
                            ctrl = true;
                        }
                        if (params.get(1).equals("3")) {
                            alt = true;
                        }
                    }
                    events.add(new TKeypressEvent(kbDown, alt, ctrl, shift));
                    reset();
                    return;
                case 'C':
                    // Right
                    if (params.size() > 1) {
                        if (params.get(1).equals("2")) {
                            shift = true;
                        }
                        if (params.get(1).equals("5")) {
                            ctrl = true;
                        }
                        if (params.get(1).equals("3")) {
                            alt = true;
                        }
                    }
                    events.add(new TKeypressEvent(kbRight, alt, ctrl, shift));
                    reset();
                    return;
                case 'D':
                    // Left
                    if (params.size() > 1) {
                        if (params.get(1).equals("2")) {
                            shift = true;
                        }
                        if (params.get(1).equals("5")) {
                            ctrl = true;
                        }
                        if (params.get(1).equals("3")) {
                            alt = true;
                        }
                    }
                    events.add(new TKeypressEvent(kbLeft, alt, ctrl, shift));
                    reset();
                    return;
                case 'H':
                    // Home
                    events.add(new TKeypressEvent(kbHome));
                    reset();
                    return;
                case 'F':
                    // End
                    events.add(new TKeypressEvent(kbEnd));
                    reset();
                    return;
                case 'Z':
                    // CBT - Cursor backward X tab stops (default 1)
                    events.add(new TKeypressEvent(kbBackTab));
                    reset();
                    return;
                case 'M':
                    // Mouse position
                    state = ParseState.MOUSE;
                    return;
                default:
                    break;
                }
            }

            // Unknown keystroke, ignore
            reset();
            return;

        case CSI_PARAM:
            // Numbers - parameter values
            if ((ch >= '0') && (ch <= '9')) {
                params.set(paramI, params.get(paramI) + ch);
                state = ParseState.CSI_PARAM;
                return;
            }
            // Parameter separator
            if (ch == ';') {
                paramI++;
                params.add(paramI, "");
                return;
            }

            if (ch == '~') {
                events.add(csiFnKey());
                reset();
                return;
            }

            if ((ch >= 0x30) && (ch <= 0x7E)) {
                switch (ch) {
                case 'A':
                    // Up
                    if (params.size() > 1) {
                        if (params.get(1).equals("2")) {
                            shift = true;
                        }
                        if (params.get(1).equals("5")) {
                            ctrl = true;
                        }
                        if (params.get(1).equals("3")) {
                            alt = true;
                        }
                    }
                    events.add(new TKeypressEvent(kbUp, alt, ctrl, shift));
                    reset();
                    return;
                case 'B':
                    // Down
                    if (params.size() > 1) {
                        if (params.get(1).equals("2")) {
                            shift = true;
                        }
                        if (params.get(1).equals("5")) {
                            ctrl = true;
                        }
                        if (params.get(1).equals("3")) {
                            alt = true;
                        }
                    }
                    events.add(new TKeypressEvent(kbDown, alt, ctrl, shift));
                    reset();
                    return;
                case 'C':
                    // Right
                    if (params.size() > 1) {
                        if (params.get(1).equals("2")) {
                            shift = true;
                        }
                        if (params.get(1).equals("5")) {
                            ctrl = true;
                        }
                        if (params.get(1).equals("3")) {
                            alt = true;
                        }
                    }
                    events.add(new TKeypressEvent(kbRight, alt, ctrl, shift));
                    reset();
                    return;
                case 'D':
                    // Left
                    if (params.size() > 1) {
                        if (params.get(1).equals("2")) {
                            shift = true;
                        }
                        if (params.get(1).equals("5")) {
                            ctrl = true;
                        }
                        if (params.get(1).equals("3")) {
                            alt = true;
                        }
                    }
                    events.add(new TKeypressEvent(kbLeft, alt, ctrl, shift));
                    reset();
                    return;
                default:
                    break;
                }
            }

            // Unknown keystroke, ignore
            reset();
            return;

        case MOUSE:
            params.set(0, params.get(paramI) + ch);
            if (params.get(0).length() == 3) {
                // We have enough to generate a mouse event
                events.add(parseMouse());
                reset();
            }
            return;

        default:
            break;
        }

        // This "should" be impossible to reach
        return;
    }

    /**
     * Tell (u)xterm that we want alt- keystrokes to send escape + character
     * rather than set the 8th bit.  Anyone who wants UTF8 should want this
     * enabled.
     *
     * @param on if true, enable metaSendsEscape
     * @return the string to emit to xterm
     */
    public String xtermMetaSendsEscape(final boolean on) {
        if (on) {
            return "\033[?1036h\033[?1034l";
        }
        return "\033[?1036l";
    }

    /**
     * Convert a list of SGR parameters into a full escape sequence.  This
     * also eliminates a trailing ';' which would otherwise reset everything
     * to white-on-black not-bold.
     *
     * @param str string of parameters, e.g. "31;1;"
     * @return the string to emit to an ANSI / ECMA-style terminal,
     * e.g. "\033[31;1m"
     */
    public String addHeaderSGR(String str) {
        if (str.length() > 0) {
            // Nix any trailing ';' because that resets all attributes
            while (str.endsWith(":")) {
                str = str.substring(0, str.length() - 1);
            }
        }
        return "\033[" + str + "m";
    }

    /**
     * Create a SGR parameter sequence for a single color change.
     *
     * @param color one of the Color.WHITE, Color.BLUE, etc. constants
     * @param foreground if true, this is a foreground color
     * @return the string to emit to an ANSI / ECMA-style terminal,
     * e.g. "\033[42m"
     */
    public String color(final Color color, final boolean foreground) {
        return color(color, foreground, true);
    }

    /**
     * Create a SGR parameter sequence for a single color change.
     *
     * @param color one of the Color.WHITE, Color.BLUE, etc. constants
     * @param foreground if true, this is a foreground color
     * @param header if true, make the full header, otherwise just emit the
     * color parameter e.g. "42;"
     * @return the string to emit to an ANSI / ECMA-style terminal,
     * e.g. "\033[42m"
     */
    public String color(final Color color, final boolean foreground,
        final boolean header) {

        int ecmaColor = color.getValue();

        // Convert Color.* values to SGR numerics
        if (foreground) {
            ecmaColor += 30;
        } else {
            ecmaColor += 40;
        }

        if (header) {
            return String.format("\033[%dm", ecmaColor);
        } else {
            return String.format("%d;", ecmaColor);
        }
    }

    /**
     * Create a SGR parameter sequence for both foreground and
     * background color change.
     *
     * @param foreColor one of the Color.WHITE, Color.BLUE, etc. constants
     * @param backColor one of the Color.WHITE, Color.BLUE, etc. constants
     * @return the string to emit to an ANSI / ECMA-style terminal,
     * e.g. "\033[31;42m"
     */
    public String color(final Color foreColor, final Color backColor) {
        return color(foreColor, backColor, true);
    }

    /**
     * Create a SGR parameter sequence for both foreground and
     * background color change.
     *
     * @param foreColor one of the Color.WHITE, Color.BLUE, etc. constants
     * @param backColor one of the Color.WHITE, Color.BLUE, etc. constants
     * @param header if true, make the full header, otherwise just emit the
     * color parameter e.g. "31;42;"
     * @return the string to emit to an ANSI / ECMA-style terminal,
     * e.g. "\033[31;42m"
     */
    public String color(final Color foreColor, final Color backColor,
        final boolean header) {

        int ecmaForeColor = foreColor.getValue();
        int ecmaBackColor = backColor.getValue();

        // Convert Color.* values to SGR numerics
        ecmaBackColor += 40;
        ecmaForeColor += 30;

        if (header) {
            return String.format("\033[%d;%dm", ecmaForeColor, ecmaBackColor);
        } else {
            return String.format("%d;%d;", ecmaForeColor, ecmaBackColor);
        }
    }

    /**
     * Create a SGR parameter sequence for foreground, background, and
     * several attributes.  This sequence first resets all attributes to
     * default, then sets attributes as per the parameters.
     *
     * @param foreColor one of the Color.WHITE, Color.BLUE, etc. constants
     * @param backColor one of the Color.WHITE, Color.BLUE, etc. constants
     * @param bold if true, set bold
     * @param reverse if true, set reverse
     * @param blink if true, set blink
     * @param underline if true, set underline
     * @return the string to emit to an ANSI / ECMA-style terminal,
     * e.g. "\033[0;1;31;42m"
     */
    public String color(final Color foreColor, final Color backColor,
        final boolean bold, final boolean reverse, final boolean blink,
        final boolean underline) {

        int ecmaForeColor = foreColor.getValue();
        int ecmaBackColor = backColor.getValue();

        // Convert Color.* values to SGR numerics
        ecmaBackColor += 40;
        ecmaForeColor += 30;

        StringBuilder sb = new StringBuilder();
        if        (  bold &&  reverse &&  blink && !underline ) {
            sb.append("\033[0;1;7;5;");
        } else if (  bold &&  reverse && !blink && !underline ) {
            sb.append("\033[0;1;7;");
        } else if ( !bold &&  reverse &&  blink && !underline ) {
            sb.append("\033[0;7;5;");
        } else if (  bold && !reverse &&  blink && !underline ) {
            sb.append("\033[0;1;5;");
        } else if (  bold && !reverse && !blink && !underline ) {
            sb.append("\033[0;1;");
        } else if ( !bold &&  reverse && !blink && !underline ) {
            sb.append("\033[0;7;");
        } else if ( !bold && !reverse &&  blink && !underline) {
            sb.append("\033[0;5;");
        } else if (  bold &&  reverse &&  blink &&  underline ) {
            sb.append("\033[0;1;7;5;4;");
        } else if (  bold &&  reverse && !blink &&  underline ) {
            sb.append("\033[0;1;7;4;");
        } else if ( !bold &&  reverse &&  blink &&  underline ) {
            sb.append("\033[0;7;5;4;");
        } else if (  bold && !reverse &&  blink &&  underline ) {
            sb.append("\033[0;1;5;4;");
        } else if (  bold && !reverse && !blink &&  underline ) {
            sb.append("\033[0;1;4;");
        } else if ( !bold &&  reverse && !blink &&  underline ) {
            sb.append("\033[0;7;4;");
        } else if ( !bold && !reverse &&  blink &&  underline) {
            sb.append("\033[0;5;4;");
        } else if ( !bold && !reverse && !blink &&  underline) {
            sb.append("\033[0;4;");
        } else {
            assert (!bold && !reverse && !blink && !underline);
            sb.append("\033[0;");
        }
        sb.append(String.format("%d;%dm", ecmaForeColor, ecmaBackColor));
        return sb.toString();
    }

    /**
     * Create a SGR parameter sequence for enabling reverse color.
     *
     * @param on if true, turn on reverse
     * @return the string to emit to an ANSI / ECMA-style terminal,
     * e.g. "\033[7m"
     */
    public String reverse(final boolean on) {
        if (on) {
            return "\033[7m";
        }
        return "\033[27m";
    }

    /**
     * Create a SGR parameter sequence to reset to defaults.
     *
     * @return the string to emit to an ANSI / ECMA-style terminal,
     * e.g. "\033[0m"
     */
    public String normal() {
        return normal(true);
    }

    /**
     * Create a SGR parameter sequence to reset to defaults.
     *
     * @param header if true, make the full header, otherwise just emit the
     * bare parameter e.g. "0;"
     * @return the string to emit to an ANSI / ECMA-style terminal,
     * e.g. "\033[0m"
     */
    public String normal(final boolean header) {
        if (header) {
            return "\033[0;37;40m";
        }
        return "0;37;40";
    }

    /**
     * Create a SGR parameter sequence for enabling boldface.
     *
     * @param on if true, turn on bold
     * @return the string to emit to an ANSI / ECMA-style terminal,
     * e.g. "\033[1m"
     */
    public String bold(final boolean on) {
        return bold(on, true);
    }

    /**
     * Create a SGR parameter sequence for enabling boldface.
     *
     * @param on if true, turn on bold
     * @param header if true, make the full header, otherwise just emit the
     * bare parameter e.g. "1;"
     * @return the string to emit to an ANSI / ECMA-style terminal,
     * e.g. "\033[1m"
     */
    public String bold(final boolean on, final boolean header) {
        if (header) {
            if (on) {
                return "\033[1m";
            }
            return "\033[22m";
        }
        if (on) {
            return "1;";
        }
        return "22;";
    }

    /**
     * Create a SGR parameter sequence for enabling blinking text.
     *
     * @param on if true, turn on blink
     * @return the string to emit to an ANSI / ECMA-style terminal,
     * e.g. "\033[5m"
     */
    public String blink(final boolean on) {
        return blink(on, true);
    }

    /**
     * Create a SGR parameter sequence for enabling blinking text.
     *
     * @param on if true, turn on blink
     * @param header if true, make the full header, otherwise just emit the
     * bare parameter e.g. "5;"
     * @return the string to emit to an ANSI / ECMA-style terminal,
     * e.g. "\033[5m"
     */
    public String blink(final boolean on, final boolean header) {
        if (header) {
            if (on) {
                return "\033[5m";
            }
            return "\033[25m";
        }
        if (on) {
            return "5;";
        }
        return "25;";
    }

    /**
     * Create a SGR parameter sequence for enabling underline / underscored
     * text.
     *
     * @param on if true, turn on underline
     * @return the string to emit to an ANSI / ECMA-style terminal,
     * e.g. "\033[4m"
     */
    public String underline(final boolean on) {
        if (on) {
            return "\033[4m";
        }
        return "\033[24m";
    }

    /**
     * Create a SGR parameter sequence for enabling the visible cursor.
     *
     * @param on if true, turn on cursor
     * @return the string to emit to an ANSI / ECMA-style terminal
     */
    public String cursor(final boolean on) {
        if (on && !cursorOn) {
            cursorOn = true;
            return "\033[?25h";
        }
        if (!on && cursorOn) {
            cursorOn = false;
            return "\033[?25l";
        }
        return "";
    }

    /**
     * Clear the entire screen.  Because some terminals use back-color-erase,
     * set the color to white-on-black beforehand.
     *
     * @return the string to emit to an ANSI / ECMA-style terminal
     */
    public String clearAll() {
        return "\033[0;37;40m\033[2J";
    }

    /**
     * Clear the line from the cursor (inclusive) to the end of the screen.
     * Because some terminals use back-color-erase, set the color to
     * white-on-black beforehand.
     *
     * @return the string to emit to an ANSI / ECMA-style terminal
     */
    public String clearRemainingLine() {
        return "\033[0;37;40m\033[K";
    }

    /**
     * Clear the line up the cursor (inclusive).  Because some terminals use
     * back-color-erase, set the color to white-on-black beforehand.
     *
     * @return the string to emit to an ANSI / ECMA-style terminal
     */
    public String clearPreceedingLine() {
        return "\033[0;37;40m\033[1K";
    }

    /**
     * Clear the line.  Because some terminals use back-color-erase, set the
     * color to white-on-black beforehand.
     *
     * @return the string to emit to an ANSI / ECMA-style terminal
     */
    public String clearLine() {
        return "\033[0;37;40m\033[2K";
    }

    /**
     * Move the cursor to the top-left corner.
     *
     * @return the string to emit to an ANSI / ECMA-style terminal
     */
    public String home() {
        return "\033[H";
    }

    /**
     * Move the cursor to (x, y).
     *
     * @param x column coordinate.  0 is the left-most column.
     * @param y row coordinate.  0 is the top-most row.
     * @return the string to emit to an ANSI / ECMA-style terminal
     */
    public String gotoXY(final int x, final int y) {
        return String.format("\033[%d;%dH", y + 1, x + 1);
    }

    /**
     * Tell (u)xterm that we want to receive mouse events based on "Any event
     * tracking" and UTF-8 coordinates.  See
     * http://invisible-island.net/xterm/ctlseqs/ctlseqs.html#Mouse%20Tracking
     *
     * Note that this also sets the alternate/primary screen buffer.
     *
     * @param on If true, enable mouse report and use the alternate screen
     * buffer.  If false disable mouse reporting and use the primary screen
     * buffer.
     * @return the string to emit to xterm
     */
    public String mouse(final boolean on) {
        if (on) {
            return "\033[?1003;1005h\033[?1049h";
        }
        return "\033[?1003;1005l\033[?1049l";
    }

    /**
     * Read function runs on a separate thread.
     */
    public void run() {
        boolean done = false;
        // available() will often return > 1, so we need to read in chunks to
        // stay caught up.
        char [] readBuffer = new char[128];
        List<TInputEvent> events = new LinkedList<TInputEvent>();

        while (!done && !stopReaderThread) {
            try {
                // We assume that if inputStream has bytes available, then
                // input won't block on read().
                int n = inputStream.available();
                if (n > 0) {
                    if (readBuffer.length < n) {
                        // The buffer wasn't big enough, make it huger
                        readBuffer = new char[readBuffer.length * 2];
                    }

                    int rc = input.read(readBuffer, 0, n);
                    // System.err.printf("read() %d", rc); System.err.flush();
                    if (rc == -1) {
                        // This is EOF
                        done = true;
                    } else {
                        for (int i = 0; i < rc; i++) {
                            int ch = readBuffer[i];
                            processChar(events, (char)ch);
                            if (events.size() > 0) {
                                // Add to the queue for the backend thread to
                                // be able to obtain.
                                synchronized (eventQueue) {
                                    eventQueue.addAll(events);
                                }
                                // Now wake up the backend
                                synchronized (this) {
                                    this.notifyAll();
                                }
                                events.clear();
                            }
                        }
                    }
                } else {
                    getIdleEvents(events);
                    if (events.size() > 0) {
                        synchronized (eventQueue) {
                            eventQueue.addAll(events);
                        }
                        events.clear();
                    }

                    // Wait 10 millis for more data
                    Thread.sleep(10);
                }
                // System.err.println("end while loop"); System.err.flush();
            } catch (InterruptedException e) {
                // SQUASH
            } catch (IOException e) {
                e.printStackTrace();
                done = true;
            }
        } // while ((done == false) && (stopReaderThread == false))
        // System.err.println("*** run() exiting..."); System.err.flush();
    }

}
