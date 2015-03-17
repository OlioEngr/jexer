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

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;
import java.util.LinkedList;

import jexer.TKeypress;
import jexer.event.TCommandEvent;
import jexer.event.TInputEvent;
import jexer.event.TKeypressEvent;
import jexer.event.TMouseEvent;
import jexer.event.TResizeEvent;
import jexer.session.SessionInfo;
import jexer.session.AWTSessionInfo;
import static jexer.TCommand.*;
import static jexer.TKeypress.*;

/**
 * This class reads keystrokes and mouse events from an AWT Frame.
 */
public final class AWTTerminal implements ComponentListener, KeyListener,
                               MouseListener, MouseMotionListener,
                               MouseWheelListener, WindowListener {

    /**
     * The backend Screen.
     */
    private AWTScreen screen;

    /**
     * The session information.
     */
    private AWTSessionInfo sessionInfo;

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
     * The reader thread.
     */
    private Thread readerThread;

    /**
     * true if mouse1 was down.  Used to report mouse1 on the release event.
     */
    private boolean mouse1 = false;

    /**
     * true if mouse2 was down.  Used to report mouse2 on the release event.
     */
    private boolean mouse2 = false;

    /**
     * true if mouse3 was down.  Used to report mouse3 on the release event.
     */
    private boolean mouse3 = false;

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
        this.screen      = screen;
        mouse1           = false;
        mouse2           = false;
        mouse3           = false;
        sessionInfo      = screen.getSessionInfo();
        eventQueue       = new LinkedList<TInputEvent>();

        screen.frame.addKeyListener(this);
        screen.frame.addWindowListener(this);
        screen.frame.addComponentListener(this);
        screen.frame.addMouseListener(this);
        screen.frame.addMouseMotionListener(this);
        screen.frame.addMouseWheelListener(this);
    }

    /**
     * Restore terminal to normal state.
     */
    public void shutdown() {
        // System.err.println("=== shutdown() ==="); System.err.flush();
        screen.frame.dispose();
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

    /**
     * Pass AWT keystrokes into the event queue.
     *
     * @param key keystroke received
     */
    @Override
    public void keyReleased(final KeyEvent key) {
        // Ignore release events
    }

    /**
     * Pass AWT keystrokes into the event queue.
     *
     * @param key keystroke received
     */
    @Override
    public void keyTyped(final KeyEvent key) {
        // Ignore typed events
    }

    /**
     * Pass AWT keystrokes into the event queue.
     *
     * @param key keystroke received
     */
    @Override
    public void keyPressed(final KeyEvent key) {
        boolean alt = false;
        boolean shift = false;
        boolean ctrl = false;
        char ch = ' ';
        boolean isKey = false;
        int fnKey = 0;
        if (key.isActionKey()) {
            isKey = true;
        } else {
            ch = key.getKeyChar();
        }
        alt = key.isAltDown();
        ctrl = key.isControlDown();
        shift = key.isShiftDown();

        /*
        System.err.printf("AWT Key: %s\n", key);
        System.err.printf("   isKey: %s\n", isKey);
        System.err.printf("   alt: %s\n", alt);
        System.err.printf("   ctrl: %s\n", ctrl);
        System.err.printf("   shift: %s\n", shift);
        System.err.printf("   ch: %s\n", ch);
         */

        // Special case: not return the bare modifier presses
        switch (key.getKeyCode()) {
        case KeyEvent.VK_ALT:
            return;
        case KeyEvent.VK_ALT_GRAPH:
            return;
        case KeyEvent.VK_CONTROL:
            return;
        case KeyEvent.VK_SHIFT:
            return;
        case KeyEvent.VK_META:
            return;
        default:
            break;
        }

        TKeypress keypress = null;
        if (isKey) {
            switch (key.getKeyCode()) {
            case KeyEvent.VK_F1:
                keypress = new TKeypress(true, TKeypress.F1, ' ',
                    alt, ctrl, shift);
                break;
            case KeyEvent.VK_F2:
                keypress = new TKeypress(true, TKeypress.F2, ' ',
                    alt, ctrl, shift);
                break;
            case KeyEvent.VK_F3:
                keypress = new TKeypress(true, TKeypress.F3, ' ',
                    alt, ctrl, shift);
                break;
            case KeyEvent.VK_F4:
                keypress = new TKeypress(true, TKeypress.F4, ' ',
                    alt, ctrl, shift);
                break;
            case KeyEvent.VK_F5:
                keypress = new TKeypress(true, TKeypress.F5, ' ',
                    alt, ctrl, shift);
                break;
            case KeyEvent.VK_F6:
                keypress = new TKeypress(true, TKeypress.F6, ' ',
                    alt, ctrl, shift);
                break;
            case KeyEvent.VK_F7:
                keypress = new TKeypress(true, TKeypress.F7, ' ',
                    alt, ctrl, shift);
                break;
            case KeyEvent.VK_F8:
                keypress = new TKeypress(true, TKeypress.F8, ' ',
                    alt, ctrl, shift);
                break;
            case KeyEvent.VK_F9:
                keypress = new TKeypress(true, TKeypress.F9, ' ',
                    alt, ctrl, shift);
                break;
            case KeyEvent.VK_F10:
                keypress = new TKeypress(true, TKeypress.F10, ' ',
                    alt, ctrl, shift);
                break;
            case KeyEvent.VK_F11:
                keypress = new TKeypress(true, TKeypress.F11, ' ',
                    alt, ctrl, shift);
                break;
            case KeyEvent.VK_F12:
                keypress = new TKeypress(true, TKeypress.F12, ' ',
                    alt, ctrl, shift);
                break;
            case KeyEvent.VK_HOME:
                keypress = new TKeypress(true, TKeypress.HOME, ' ',
                    alt, ctrl, shift);
                break;
            case KeyEvent.VK_END:
                keypress = new TKeypress(true, TKeypress.END, ' ',
                    alt, ctrl, shift);
                break;
            case KeyEvent.VK_PAGE_UP:
                keypress = new TKeypress(true, TKeypress.PGUP, ' ',
                    alt, ctrl, shift);
                break;
            case KeyEvent.VK_PAGE_DOWN:
                keypress = new TKeypress(true, TKeypress.PGDN, ' ',
                    alt, ctrl, shift);
                break;
            case KeyEvent.VK_INSERT:
                keypress = new TKeypress(true, TKeypress.INS, ' ',
                    alt, ctrl, shift);
                break;
            case KeyEvent.VK_DELETE:
                keypress = new TKeypress(true, TKeypress.DEL, ' ',
                    alt, ctrl, shift);
                break;
            case KeyEvent.VK_RIGHT:
                keypress = new TKeypress(true, TKeypress.RIGHT, ' ',
                    alt, ctrl, shift);
                break;
            case KeyEvent.VK_LEFT:
                keypress = new TKeypress(true, TKeypress.LEFT, ' ',
                    alt, ctrl, shift);
                break;
            case KeyEvent.VK_UP:
                keypress = new TKeypress(true, TKeypress.UP, ' ',
                    alt, ctrl, shift);
                break;
            case KeyEvent.VK_DOWN:
                keypress = new TKeypress(true, TKeypress.DOWN, ' ',
                    alt, ctrl, shift);
                break;
            case KeyEvent.VK_TAB:
                // Special case: distinguish TAB vs BTAB
                if (shift) {
                    keypress = kbShiftTab;
                } else {
                    keypress = kbTab;
                }
                break;
            case KeyEvent.VK_ENTER:
                keypress = new TKeypress(true, TKeypress.ENTER, ' ',
                    alt, ctrl, shift);
                break;
            case KeyEvent.VK_ESCAPE:
                keypress = new TKeypress(true, TKeypress.ESC, ' ',
                    alt, ctrl, shift);
                break;
            case KeyEvent.VK_BACK_SPACE:
                // Special case: return it as kbBackspace (Ctrl-H)
                keypress = new TKeypress(false, 0, 'H', false, true, false);
                break;
            default:
                // Unsupported, ignore
                return;
            }
        }

        if (keypress == null) {
            switch (ch) {
            case 0x08:
                keypress = kbBackspace;
                break;
            case 0x0A:
                keypress = kbEnter;
                break;
            case 0x1B:
                keypress = kbEsc;
                break;
            case 0x0D:
                keypress = kbEnter;
                break;
            case 0x7F:
                keypress = kbDel;
                break;
            default:
                if (!alt && ctrl && !shift) {
                    ch = key.getKeyText(key.getKeyCode()).charAt(0);
                }
                // Not a special key, put it together
                keypress = new TKeypress(false, 0, ch, alt, ctrl, shift);
            }
        }

        // Save it and we are done.
        synchronized (eventQueue) {
            eventQueue.add(new TKeypressEvent(keypress));
        }
        // Wake up the backend
        synchronized (this) {
            this.notifyAll();
        }
    }

    /**
     * Pass window events into the event queue.
     *
     * @param event window event received
     */
    @Override
    public void windowActivated(final WindowEvent event) {
        // Ignore
    }

    /**
     * Pass window events into the event queue.
     *
     * @param event window event received
     */
    @Override
    public void windowClosed(final WindowEvent event) {
        // Ignore
    }

    /**
     * Pass window events into the event queue.
     *
     * @param event window event received
     */
    @Override
    public void windowClosing(final WindowEvent event) {
        // Drop a cmAbort and walk away
        synchronized (eventQueue) {
            eventQueue.add(new TCommandEvent(cmAbort));
        }
        // Wake up the backend
        synchronized (this) {
            this.notifyAll();
        }
    }

    /**
     * Pass window events into the event queue.
     *
     * @param event window event received
     */
    @Override
    public void windowDeactivated(final WindowEvent event) {
        // Ignore
    }

    /**
     * Pass window events into the event queue.
     *
     * @param event window event received
     */
    @Override
    public void windowDeiconified(final WindowEvent event) {
        // Ignore
    }

    /**
     * Pass window events into the event queue.
     *
     * @param event window event received
     */
    @Override
    public void windowIconified(final WindowEvent event) {
        // Ignore
    }

    /**
     * Pass window events into the event queue.
     *
     * @param event window event received
     */
    @Override
    public void windowOpened(final WindowEvent event) {
        // Ignore
    }

    /**
     * Pass component events into the event queue.
     *
     * @param event component event received
     */
    @Override
    public void componentHidden(final ComponentEvent event) {
        // Ignore
    }

    /**
     * Pass component events into the event queue.
     *
     * @param event component event received
     */
    @Override
    public void componentShown(final ComponentEvent event) {
        // Ignore
    }

    /**
     * Pass component events into the event queue.
     *
     * @param event component event received
     */
    @Override
    public void componentMoved(final ComponentEvent event) {
        // Ignore
    }

    /**
     * Pass component events into the event queue.
     *
     * @param event component event received
     */
    @Override
    public void componentResized(final ComponentEvent event) {
        // Drop a new TResizeEvent into the queue
        sessionInfo.queryWindowSize();
        synchronized (eventQueue) {
            TResizeEvent windowResize = new TResizeEvent(TResizeEvent.Type.SCREEN,
                sessionInfo.getWindowWidth(), sessionInfo.getWindowHeight());
            eventQueue.add(windowResize);
        }
        // Wake up the backend
        synchronized (this) {
            this.notifyAll();
        }
    }

    /**
     * Pass mouse events into the event queue.
     *
     * @param mouse mouse event received
     */
    @Override
    public void mouseDragged(final MouseEvent mouse) {
        int modifiers = mouse.getModifiersEx();
        boolean eventMouse1 = false;
        boolean eventMouse2 = false;
        boolean eventMouse3 = false;
        if ((modifiers & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
            eventMouse1 = true;
        }
        if ((modifiers & MouseEvent.BUTTON2_DOWN_MASK) != 0) {
            eventMouse2 = true;
        }
        if ((modifiers & MouseEvent.BUTTON3_DOWN_MASK) != 0) {
            eventMouse3 = true;
        }
        mouse1 = eventMouse1;
        mouse2 = eventMouse2;
        mouse3 = eventMouse3;
        int x = screen.textColumn(mouse.getX());
        int y = screen.textRow(mouse.getY());

        TMouseEvent mouseEvent = new TMouseEvent(TMouseEvent.Type.MOUSE_MOTION,
            x, y, x, y, mouse1, mouse2, mouse3, false, false);

        synchronized (eventQueue) {
            eventQueue.add(mouseEvent);
        }
        // Wake up the backend
        synchronized (this) {
            this.notifyAll();
        }
    }

    /**
     * Pass mouse events into the event queue.
     *
     * @param mouse mouse event received
     */
    @Override
    public void mouseMoved(final MouseEvent mouse) {
        int x = screen.textColumn(mouse.getX());
        int y = screen.textRow(mouse.getY());
        TMouseEvent mouseEvent = new TMouseEvent(TMouseEvent.Type.MOUSE_MOTION,
            x, y, x, y, mouse1, mouse2, mouse3, false, false);

        synchronized (eventQueue) {
            eventQueue.add(mouseEvent);
        }
        // Wake up the backend
        synchronized (this) {
            this.notifyAll();
        }
    }

    /**
     * Pass mouse events into the event queue.
     *
     * @param mouse mouse event received
     */
    @Override
    public void mouseClicked(final MouseEvent mouse) {
        // Ignore
    }

    /**
     * Pass mouse events into the event queue.
     *
     * @param mouse mouse event received
     */
    @Override
    public void mouseEntered(final MouseEvent mouse) {
        // Ignore
    }

    /**
     * Pass mouse events into the event queue.
     *
     * @param mouse mouse event received
     */
    @Override
    public void mouseExited(final MouseEvent mouse) {
        // Ignore
    }

    /**
     * Pass mouse events into the event queue.
     *
     * @param mouse mouse event received
     */
    @Override
    public void mousePressed(final MouseEvent mouse) {
        int modifiers = mouse.getModifiersEx();
        boolean eventMouse1 = false;
        boolean eventMouse2 = false;
        boolean eventMouse3 = false;
        if ((modifiers & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
            eventMouse1 = true;
        }
        if ((modifiers & MouseEvent.BUTTON2_DOWN_MASK) != 0) {
            eventMouse2 = true;
        }
        if ((modifiers & MouseEvent.BUTTON3_DOWN_MASK) != 0) {
            eventMouse3 = true;
        }
        mouse1 = eventMouse1;
        mouse2 = eventMouse2;
        mouse3 = eventMouse3;
        int x = screen.textColumn(mouse.getX());
        int y = screen.textRow(mouse.getY());

        TMouseEvent mouseEvent = new TMouseEvent(TMouseEvent.Type.MOUSE_DOWN,
            x, y, x, y, mouse1, mouse2, mouse3, false, false);

        synchronized (eventQueue) {
            eventQueue.add(mouseEvent);
        }
        // Wake up the backend
        synchronized (this) {
            this.notifyAll();
        }
    }

    /**
     * Pass mouse events into the event queue.
     *
     * @param mouse mouse event received
     */
    @Override
    public void mouseReleased(final MouseEvent mouse) {
        int modifiers = mouse.getModifiersEx();
        boolean eventMouse1 = false;
        boolean eventMouse2 = false;
        boolean eventMouse3 = false;
        if ((modifiers & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
            eventMouse1 = true;
        }
        if ((modifiers & MouseEvent.BUTTON2_DOWN_MASK) != 0) {
            eventMouse2 = true;
        }
        if ((modifiers & MouseEvent.BUTTON3_DOWN_MASK) != 0) {
            eventMouse3 = true;
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
        int x = screen.textColumn(mouse.getX());
        int y = screen.textRow(mouse.getY());

        TMouseEvent mouseEvent = new TMouseEvent(TMouseEvent.Type.MOUSE_UP,
            x, y, x, y, eventMouse1, eventMouse2, eventMouse3, false, false);

        synchronized (eventQueue) {
            eventQueue.add(mouseEvent);
        }
        // Wake up the backend
        synchronized (this) {
            this.notifyAll();
        }
    }

    /**
     * Pass mouse events into the event queue.
     *
     * @param mouse mouse event received
     */
    @Override
    public void mouseWheelMoved(final MouseWheelEvent mouse) {
        int modifiers = mouse.getModifiersEx();
        boolean eventMouse1 = false;
        boolean eventMouse2 = false;
        boolean eventMouse3 = false;
        boolean mouseWheelUp = false;
        boolean mouseWheelDown = false;
        if ((modifiers & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
            eventMouse1 = true;
        }
        if ((modifiers & MouseEvent.BUTTON2_DOWN_MASK) != 0) {
            eventMouse2 = true;
        }
        if ((modifiers & MouseEvent.BUTTON3_DOWN_MASK) != 0) {
            eventMouse3 = true;
        }
        mouse1 = eventMouse1;
        mouse2 = eventMouse2;
        mouse3 = eventMouse3;
        int x = screen.textColumn(mouse.getX());
        int y = screen.textRow(mouse.getY());
        if (mouse.getWheelRotation() > 0) {
            mouseWheelDown = true;
        }
        if (mouse.getWheelRotation() < 0) {
            mouseWheelUp = true;
        }

        TMouseEvent mouseEvent = new TMouseEvent(TMouseEvent.Type.MOUSE_DOWN,
            x, y, x, y, mouse1, mouse2, mouse3, mouseWheelUp, mouseWheelDown);

        synchronized (eventQueue) {
            eventQueue.add(mouseEvent);
        }
        // Wake up the backend
        synchronized (this) {
            this.notifyAll();
        }
    }

}
