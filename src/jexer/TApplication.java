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
import jexer.io.Screen;
import static jexer.TCommand.*;
import static jexer.TKeypress.*;

/**
 * TApplication sets up a full Text User Interface application.
 */
public class TApplication {

    /**
     * Access to the physical screen, keyboard, and mouse.
     */
    private Backend backend;

    /**
     * Get the Screen.
     *
     * @return the Screen
     */
    public final Screen getScreen() {
        return backend.getScreen();
    }

    /**
     * Actual mouse coordinate X.
     */
    private int mouseX;

    /**
     * Actual mouse coordinate Y.
     */
    private int mouseY;

    /**
     * Event queue that will be drained by either primary or secondary Fiber.
     */
    private List<TInputEvent> eventQueue;

    /**
     * Windows and widgets pull colors from this ColorTheme.
     */
    private ColorTheme theme;

    /**
     * Get the color theme.
     *
     * @return the theme
     */
    public final ColorTheme getTheme() {
        return theme;
    }

    /**
     * When true, exit the application.
     */
    private boolean quit = false;

    /**
     * When true, repaint the entire screen.
     */
    private boolean repaint = true;

    /**
     * Request full repaint on next screen refresh.
     */
    public void setRepaint() {
        repaint = true;
    }

    /**
     * When true, just flush updates from the screen.
     */
    private boolean flush = false;

    /**
     * Y coordinate of the top edge of the desktop.  For now this is a
     * constant.  Someday it would be nice to have a multi-line menu or
     * toolbars.
     */
    private static final int desktopTop = 1;

    /**
     * Get Y coordinate of the top edge of the desktop.
     *
     * @return Y coordinate of the top edge of the desktop
     */
    public final int getDesktopTop() {
        return desktopTop;
    }

    /**
     * Y coordinate of the bottom edge of the desktop.
     */
    private int desktopBottom;

    /**
     * Get Y coordinate of the bottom edge of the desktop.
     *
     * @return Y coordinate of the bottom edge of the desktop
     */
    public final int getDesktopBottom() {
        return desktopBottom;
    }

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
     * @throws UnsupportedEncodingException if an exception is thrown when
     * creating the InputStreamReader
     */
    public TApplication(final InputStream input,
        final OutputStream output) throws UnsupportedEncodingException {

        backend       = new ECMA48Backend(input, output);
        theme         = new ColorTheme();
        desktopBottom = backend.getScreen().getHeight() - 1;
        eventQueue    = new LinkedList<TInputEvent>();
    }

    /**
     * Invert the cell at the mouse pointer position.
     */
    private void drawMouse() {
        CellAttributes attr = backend.getScreen().getAttrXY(mouseX, mouseY);
        attr.setForeColor(attr.getForeColor().invert());
        attr.setBackColor(attr.getBackColor().invert());
        backend.getScreen().putAttrXY(mouseX, mouseY, attr, false);
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
    public final void drawAll() {
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
        backend.getScreen().clear();

        // Draw the background
        CellAttributes background = theme.getColor("tapplication.background");
        backend.getScreen().putAll(GraphicsChars.HATCH, background);

        /*
        // Draw each window in reverse Z order
        TWindow [] sorted = windows.dup;
        sorted.sort.reverse;
        foreach (w; sorted) {
            w.drawChildren();
        }

        // Draw the blank menubar line - reset the screen clipping first so
        // it won't trim it out.
        backend.getScreen().resetClipping();
        backend.getScreen().hLineXY(0, 0, backend.getScreen().getWidth(), ' ',
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
            backend.getScreen().hLineXY(x, 0, cast(int)m.title.length + 2, ' ',
                menuColor);
            backend.getScreen().putStrXY(x + 1, 0, m.title, menuColor);
            // Draw the highlight character
            backend.getScreen().putCharXY(x + 1 + m.mnemonic.shortcutIdx, 0,
                m.mnemonic.shortcut, menuMnemonicColor);

            if (m.active) {
                m.drawChildren();
                // Reset the screen clipping so we can draw the next title.
                backend.getScreen().resetClipping();
            }
            x += m.title.length + 2;
        }

        foreach (m; subMenus) {
            // Reset the screen clipping so we can draw the next sub-menu.
            backend.getScreen().resetClipping();
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
                backend.getScreen().putCursor(true, activeWidget.getCursorAbsoluteX(),
                    activeWidget.getCursorAbsoluteY());
                cursor = true;
            }
        }

        // Kill the cursor
        if (cursor == false) {
            backend.getScreen().hideCursor();
        }
         */

        // Flush the screen contents
        backend.flushScreen();

        repaint = false;
        flush = false;
    }

    /**
     * Run this application until it exits.
     */
    public final void run() {
        List<TInputEvent> events = new LinkedList<TInputEvent>();

        while (!quit) {
            // Timeout is in milliseconds, so default timeout after 1 second
            // of inactivity.
            int timeout = getSleepTime(1000);

            if (eventQueue.size() > 0) {
                // Do not wait if there are definitely events waiting to be
                // processed or a screen redraw to do.
                timeout = 0;
            }

            // Pull any pending input events
            backend.getEvents(events, timeout);
            metaHandleEvents(events);
            events.clear();

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
    private void metaHandleEvents(final List<TInputEvent> events) {

        for (TInputEvent event: events) {

            /*
            System.err.printf(String.format("metaHandleEvents event: %s\n",
                    event)); System.err.flush();
             */

            if (quit) {
                // Do no more processing if the application is already trying
                // to exit.
                return;
            }

            // DEBUG
            if (event instanceof TKeypressEvent) {
                TKeypressEvent keypress = (TKeypressEvent) event;
                if (keypress.equals(kbAltX)) {
                    quit = true;
                    return;
                }
            }
            // DEBUG

            // Special application-wide events -------------------------------

            // Abort everything
            if (event instanceof TCommandEvent) {
                TCommandEvent command = (TCommandEvent) event;
                if (command.getCmd().equals(cmAbort)) {
                    quit = true;
                    return;
                }
            }

            // Screen resize
            if (event instanceof TResizeEvent) {
                TResizeEvent resize = (TResizeEvent) event;
                backend.getScreen().setDimensions(resize.getWidth(),
                    resize.getHeight());
                desktopBottom = backend.getScreen().getHeight() - 1;
                repaint = true;
                mouseX = 0;
                mouseY = 0;
                continue;
            }

            // Peek at the mouse position
            if (event instanceof TMouseEvent) {
                TMouseEvent mouse = (TMouseEvent) event;
                if ((mouseX != mouse.getX()) || (mouseY != mouse.getY())) {
                    mouseX = mouse.getX();
                    mouseY = mouse.getY();
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
    protected int getSleepTime(final int timeout) {
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
        // TODO: fix timers.  Until then, come back after 250 millis.
        return 250;
    }

    /**
     * Close window.  Note that the window's destructor is NOT called by this
     * method, instead the GC is assumed to do the cleanup.
     *
     * @param window the window to remove
     */
    public final void closeWindow(final TWindow window) {
        /*
         TODO

        uint z = window.z;
        window.z = -1;
        windows.sort;
        windows = windows[1 .. $];
        TWindow activeWindow = null;
        foreach (w; windows) {
            if (w.z > z) {
                w.z--;
                if (w.z == 0) {
                    w.active = true;
                    assert(activeWindow is null);
                    activeWindow = w;
                } else {
                    w.active = false;
                }
            }
        }

        // Perform window cleanup
        window.onClose();

        // Refresh screen
        repaint = true;

        // Check if we are closing a TMessageBox or similar
        if (secondaryEventReceiver !is null) {
            assert(secondaryEventFiber !is null);

            // Do not send events to the secondaryEventReceiver anymore, the
            // window is closed.
            secondaryEventReceiver = null;

            // Special case: if this is called while executing on a
            // secondaryEventFiber, call it so that widgetEventHandler() can
            // terminate.
            if (secondaryEventFiber.state == Fiber.State.HOLD) {
                secondaryEventFiber.call();
            }
            secondaryEventFiber = null;

            // Unfreeze the logic in handleEvent()
            if (primaryEventFiber.state == Fiber.State.HOLD) {
                primaryEventFiber.call();
            }
        }
         */
    }

    /**
     * Switch to the next window.
     *
     * @param forward if true, then switch to the next window in the list,
     * otherwise switch to the previous window in the list
     */
    public final void switchWindow(final boolean forward) {
        /*
         TODO

        // Only switch if there are multiple windows
        if (windows.length < 2) {
            return;
        }

        // Swap z/active between active window and the next in the
        // list
        ptrdiff_t activeWindowI = -1;
        for (auto i = 0; i < windows.length; i++) {
            if (windows[i].active) {
                activeWindowI = i;
                break;
            }
        }
        assert(activeWindowI >= 0);

        // Do not switch if a window is modal
        if (windows[activeWindowI].isModal()) {
            return;
        }

        size_t nextWindowI;
        if (forward) {
            nextWindowI = (activeWindowI + 1) % windows.length;
        } else {
            if (activeWindowI == 0) {
                nextWindowI = windows.length - 1;
            } else {
                nextWindowI = activeWindowI - 1;
            }
        }
        windows[activeWindowI].active = false;
        windows[activeWindowI].z = windows[nextWindowI].z;
        windows[nextWindowI].z = 0;
        windows[nextWindowI].active = true;

        // Refresh
        repaint = true;
        */
    }

    /**
     * Add a window to my window list and make it active.
     *
     * @param window new window to add
     */
    public final void addWindow(final TWindow window) {
        /*
         TODO
        // Do not allow a modal window to spawn a non-modal window
        if ((windows.length > 0) && (windows[0].isModal())) {
            assert(window.isModal());
        }
        foreach (w; windows) {
            w.active = false;
            w.z++;
        }
        windows ~= window;
        window.active = true;
        window.z = 0;
         */
    }


}
