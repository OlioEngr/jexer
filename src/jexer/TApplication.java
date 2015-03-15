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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jexer.bits.CellAttributes;
import jexer.bits.ColorTheme;
import jexer.bits.GraphicsChars;
import jexer.event.TCommandEvent;
import jexer.event.TInputEvent;
import jexer.event.TKeypressEvent;
import jexer.event.TMenuEvent;
import jexer.event.TMouseEvent;
import jexer.event.TResizeEvent;
import jexer.backend.Backend;
import jexer.backend.ECMA48Backend;
import jexer.io.Screen;
import jexer.menu.TMenu;
import jexer.menu.TMenuItem;
import static jexer.TCommand.*;
import static jexer.TKeypress.*;

/**
 * TApplication sets up a full Text User Interface application.
 */
public class TApplication {

    /**
     * WidgetEventHandler is the main event consumer loop.  There are at most
     * two such threads in existence: the primary for normal case and a
     * secondary that is used for TMessageBox, TInputBox, and similar.
     */
    private class WidgetEventHandler implements Runnable {
        /**
         * The main application.
         */
        private TApplication application;

        /**
         * Whether or not this WidgetEventHandler is the primary or secondary
         * thread.
         */
        private boolean primary = true;

        /**
         * Public constructor.
         *
         * @param application the main application
         * @param primary if true, this is the primary event handler thread
         */
        public WidgetEventHandler(final TApplication application,
            final boolean primary) {

            this.application = application;
            this.primary = primary;
        }

        /**
         * The consumer loop.
         */
        public void run() {

            // Loop forever
            while (!application.quit) {

                // Wait until application notifies me
                while (!application.quit) {
                    try {
                        synchronized (application.drainEventQueue) {
                            if (application.drainEventQueue.size() > 0) {
                                break;
                            }
                        }
                        synchronized (application) {
                            application.wait();
                            if ((!primary)
                                && (application.secondaryEventReceiver == null)
                            ) {
                                // Secondary thread, time to exit
                                return;
                            }
                            break;
                        }
                    } catch (InterruptedException e) {
                        // SQUASH
                    }
                }

                // Pull all events off the queue
                for (;;) {
                    TInputEvent event = null;
                    synchronized (application.drainEventQueue) {
                        if (application.drainEventQueue.size() == 0) {
                            break;
                        }
                        event = application.drainEventQueue.remove(0);
                    }
                    if (primary) {
                        primaryHandleEvent(event);
                    } else {
                        secondaryHandleEvent(event);
                    }
                    if ((!primary)
                        && (application.secondaryEventReceiver == null)
                    ) {
                        // Secondary thread, time to exit
                        return;
                    }
                }
            } // while (true) (main runnable loop)
        }
    }

    /**
     * The primary event handler thread.
     */
    private WidgetEventHandler primaryEventHandler;

    /**
     * The secondary event handler thread.
     */
    private WidgetEventHandler secondaryEventHandler;

    /**
     * The widget receiving events from the secondary event handler thread.
     */
    private TWidget secondaryEventReceiver;

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
     * Event queue that is filled by run().
     */
    private List<TInputEvent> fillEventQueue;

    /**
     * Event queue that will be drained by either primary or secondary
     * Thread.
     */
    private List<TInputEvent> drainEventQueue;

    /**
     * Top-level menus in this application.
     */
    private List<TMenu> menus;

    /**
     * Stack of activated sub-menus in this application.
     */
    private List<TMenu> subMenus;

    /**
     * The currently acive menu.
     */
    private TMenu activeMenu = null;

    /**
     * Active keyboard accelerators.
     */
    private Map<TKeypress, TMenuItem> accelerators;

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
     * The top-level windows (but not menus).
     */
    private List<TWindow> windows;

    /**
     * Timers that are being ticked.
     */
    private List<TTimer> timers;

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
    public final void setRepaint() {
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

        backend         = new ECMA48Backend(input, output);
        theme           = new ColorTheme();
        desktopBottom   = getScreen().getHeight() - 1;
        fillEventQueue  = new ArrayList<TInputEvent>();
        drainEventQueue = new ArrayList<TInputEvent>();
        windows         = new LinkedList<TWindow>();
        menus           = new LinkedList<TMenu>();
        subMenus        = new LinkedList<TMenu>();
        timers          = new LinkedList<TTimer>();
        accelerators    = new HashMap<TKeypress, TMenuItem>();

        // Setup the main consumer thread
        primaryEventHandler = new WidgetEventHandler(this, true);
        (new Thread(primaryEventHandler)).start();
    }

    /**
     * Invert the cell at the mouse pointer position.
     */
    private void drawMouse() {
        CellAttributes attr = getScreen().getAttrXY(mouseX, mouseY);
        attr.setForeColor(attr.getForeColor().invert());
        attr.setBackColor(attr.getBackColor().invert());
        getScreen().putAttrXY(mouseX, mouseY, attr, false);
        flush = true;

        if (windows.size() == 0) {
            repaint = true;
        }
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
        getScreen().clear();

        // Draw the background
        CellAttributes background = theme.getColor("tapplication.background");
        getScreen().putAll(GraphicsChars.HATCH, background);

        // Draw each window in reverse Z order
        List<TWindow> sorted = new LinkedList<TWindow>(windows);
        Collections.sort(sorted);
        Collections.reverse(sorted);
        for (TWindow window: sorted) {
            window.drawChildren();
        }

        // Draw the blank menubar line - reset the screen clipping first so
        // it won't trim it out.
        getScreen().resetClipping();
        getScreen().hLineXY(0, 0, getScreen().getWidth(), ' ',
            theme.getColor("tmenu"));
        // Now draw the menus.
        int x = 1;
        for (TMenu menu: menus) {
            CellAttributes menuColor;
            CellAttributes menuMnemonicColor;
            if (menu.getActive()) {
                menuColor = theme.getColor("tmenu.highlighted");
                menuMnemonicColor = theme.getColor("tmenu.mnemonic.highlighted");
            } else {
                menuColor = theme.getColor("tmenu");
                menuMnemonicColor = theme.getColor("tmenu.mnemonic");
            }
            // Draw the menu title
            getScreen().hLineXY(x, 0, menu.getTitle().length() + 2, ' ',
                menuColor);
            getScreen().putStrXY(x + 1, 0, menu.getTitle(), menuColor);
            // Draw the highlight character
            getScreen().putCharXY(x + 1 + menu.getMnemonic().getShortcutIdx(),
                0, menu.getMnemonic().getShortcut(), menuMnemonicColor);

            if (menu.getActive()) {
                menu.drawChildren();
                // Reset the screen clipping so we can draw the next title.
                getScreen().resetClipping();
            }
            x += menu.getTitle().length() + 2;
        }

        for (TMenu menu: subMenus) {
            // Reset the screen clipping so we can draw the next sub-menu.
            getScreen().resetClipping();
            menu.drawChildren();
        }

        // Draw the mouse pointer
        drawMouse();

        // Place the cursor if it is visible
        TWidget activeWidget = null;
        if (sorted.size() > 0) {
            activeWidget = sorted.get(sorted.size() - 1).getActiveChild();
            if (activeWidget.visibleCursor()) {
                getScreen().putCursor(true, activeWidget.getCursorAbsoluteX(),
                    activeWidget.getCursorAbsoluteY());
                cursor = true;
            }
        }

        // Kill the cursor
        if (!cursor) {
            getScreen().hideCursor();
        }

        // Flush the screen contents
        backend.flushScreen();

        repaint = false;
        flush = false;
    }

    /**
     * Run this application until it exits.
     */
    public final void run() {
        while (!quit) {
            // Timeout is in milliseconds, so default timeout after 1 second
            // of inactivity.
            int timeout = getSleepTime(1000);

            // See if there are any definitely events waiting to be processed
            // or a screen redraw to do.  If so, do not wait if there is no
            // I/O coming in.
            synchronized (drainEventQueue) {
                if (drainEventQueue.size() > 0) {
                    timeout = 0;
                }
            }
            synchronized (fillEventQueue) {
                if (fillEventQueue.size() > 0) {
                    timeout = 0;
                }
            }

            // Pull any pending I/O events
            backend.getEvents(fillEventQueue, timeout);

            // Dispatch each event to the appropriate handler, one at a time.
            for (;;) {
                TInputEvent event = null;
                synchronized (fillEventQueue) {
                    if (fillEventQueue.size() == 0) {
                        break;
                    }
                    event = fillEventQueue.remove(0);
                }
                metaHandleEvent(event);
            }

            // Process timers and call doIdle()'s
            doIdle();

            // Update the screen
            drawAll();
        }

        // Shutdown the consumer threads
        synchronized (this) {
            this.notifyAll();
        }

        backend.shutdown();
    }

    /**
     * Peek at certain application-level events, add to eventQueue, and wake
     * up the consuming Thread.
     *
     * @param event the input event to consume
     */
    private void metaHandleEvent(final TInputEvent event) {

        /*
        System.err.printf(String.format("metaHandleEvents event: %s\n",
                event)); System.err.flush();
         */

        if (quit) {
            // Do no more processing if the application is already trying
            // to exit.
            return;
        }

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
            getScreen().setDimensions(resize.getWidth(),
                resize.getHeight());
            desktopBottom = getScreen().getHeight() - 1;
            repaint = true;
            mouseX = 0;
            mouseY = 0;
            return;
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

         // Put into the main queue
        synchronized (drainEventQueue) {
            drainEventQueue.add(event);
        }

        // Wake all threads: primary thread will either be consuming events
        // again or waiting in yield(), and secondary thread will either not
        // exist or consuming events.
        synchronized (this) {
            this.notifyAll();
        }
    }

    /**
     * Dispatch one event to the appropriate widget or application-level
     * event handler.  This is the primary event handler, it has the normal
     * application-wide event handling.
     *
     * @param event the input event to consume
     * @see #secondaryHandleEvent(TInputEvent event)
     */
    private void primaryHandleEvent(final TInputEvent event) {

        // System.err.printf("Handle event: %s\n", event);

        // Special application-wide events -----------------------------------

        // Peek at the mouse position
        if (event instanceof TMouseEvent) {
            // See if we need to switch focus to another window or the menu
            checkSwitchFocus((TMouseEvent) event);
        }

        // Handle menu events
        if ((activeMenu != null) && !(event instanceof TCommandEvent)) {
            TMenu menu = activeMenu;

            if (event instanceof TMouseEvent) {
                TMouseEvent mouse = (TMouseEvent) event;

                while (subMenus.size() > 0) {
                    TMenu subMenu = subMenus.get(subMenus.size() - 1);
                    if (subMenu.mouseWouldHit(mouse)) {
                        break;
                    }
                    if ((mouse.getType() == TMouseEvent.Type.MOUSE_MOTION)
                        && (!mouse.getMouse1())
                        && (!mouse.getMouse2())
                        && (!mouse.getMouse3())
                        && (!mouse.getMouseWheelUp())
                        && (!mouse.getMouseWheelDown())
                    ) {
                        break;
                    }
                    // We navigated away from a sub-menu, so close it
                    closeSubMenu();
                }

                // Convert the mouse relative x/y to menu coordinates
                assert (mouse.getX() == mouse.getAbsoluteX());
                assert (mouse.getY() == mouse.getAbsoluteY());
                if (subMenus.size() > 0) {
                    menu = subMenus.get(subMenus.size() - 1);
                }
                mouse.setX(mouse.getX() - menu.getX());
                mouse.setY(mouse.getY() - menu.getY());
            }
            menu.handleEvent(event);
            return;
        }

        if (event instanceof TKeypressEvent) {
            TKeypressEvent keypress = (TKeypressEvent) event;

            // See if this key matches an accelerator, and if so dispatch the
            // menu event.
            TKeypress keypressLowercase = keypress.getKey().toLowerCase();
            TMenuItem item = null;
            synchronized (accelerators) {
                item = accelerators.get(keypressLowercase);
            }
            if (item != null) {
                // Let the menu item dispatch
                item.dispatch();
                return;
            } else {
                // Handle the keypress
                if (onKeypress(keypress)) {
                    return;
                }
            }
        }

        if (event instanceof TCommandEvent) {
            if (onCommand((TCommandEvent) event)) {
                return;
            }
        }

        if (event instanceof TMenuEvent) {
            if (onMenu((TMenuEvent) event)) {
                return;
            }
        }

        // Dispatch events to the active window -------------------------------
        for (TWindow window: windows) {
            if (window.getActive()) {
                if (event instanceof TMouseEvent) {
                    TMouseEvent mouse = (TMouseEvent) event;
                    // Convert the mouse relative x/y to window coordinates
                    assert (mouse.getX() == mouse.getAbsoluteX());
                    assert (mouse.getY() == mouse.getAbsoluteY());
                    mouse.setX(mouse.getX() - window.getX());
                    mouse.setY(mouse.getY() - window.getY());
                }
                // System.err("TApplication dispatch event: %s\n", event);
                window.handleEvent(event);
                break;
            }
        }
    }
    /**
     * Dispatch one event to the appropriate widget or application-level
     * event handler.  This is the secondary event handler used by certain
     * special dialogs (currently TMessageBox and TFileOpenBox).
     *
     * @param event the input event to consume
     * @see #primaryHandleEvent(TInputEvent event)
     */
    private void secondaryHandleEvent(final TInputEvent event) {
        secondaryEventReceiver.handleEvent(event);
    }

    /**
     * Enable a widget to override the primary event thread.
     *
     * @param widget widget that will receive events
     */
    public final void enableSecondaryEventReceiver(final TWidget widget) {
        assert (secondaryEventReceiver == null);
        assert (secondaryEventHandler == null);
        assert (widget instanceof TMessageBox);
        secondaryEventReceiver = widget;
        secondaryEventHandler = new WidgetEventHandler(this, false);
        (new Thread(secondaryEventHandler)).start();

        // Refresh
        repaint = true;
    }

    /**
     * Yield to the secondary thread.
     */
    public final void yield() {
        assert (secondaryEventReceiver != null);
        while (secondaryEventReceiver != null) {
            synchronized (this) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    // SQUASH
                }
            }
        }
    }

    /**
     * Do stuff when there is no user input.
     */
    private void doIdle() {
        // Now run any timers that have timed out
        Date now = new Date();
        List<TTimer> keepTimers = new LinkedList<TTimer>();
        for (TTimer timer: timers) {
            if (timer.getNextTick().getTime() < now.getTime()) {
                timer.tick();
                if (timer.recurring) {
                    keepTimers.add(timer);
                }
            } else {
                keepTimers.add(timer);
            }
        }
        timers = keepTimers;

        // Call onIdle's
        for (TWindow window: windows) {
            window.onIdle();
        }
    }

    /**
     * Get the amount of time I can sleep before missing a Timer tick.
     *
     * @param timeout = initial (maximum) timeout
     * @return number of milliseconds between now and the next timer event
     */
    protected int getSleepTime(final int timeout) {
        Date now = new Date();
        long sleepTime = timeout;
        for (TTimer timer: timers) {
            if (timer.getNextTick().getTime() < now.getTime()) {
                return 0;
            }
            if ((timer.getNextTick().getTime() > now.getTime())
                && ((timer.getNextTick().getTime() - now.getTime()) < sleepTime)
            ) {
                sleepTime = timer.getNextTick().getTime() - now.getTime();
            }
        }
        assert (sleepTime >= 0);
        return (int)sleepTime;
    }

    /**
     * Close window.  Note that the window's destructor is NOT called by this
     * method, instead the GC is assumed to do the cleanup.
     *
     * @param window the window to remove
     */
    public final void closeWindow(final TWindow window) {
        int z = window.getZ();
        window.setZ(-1);
        Collections.sort(windows);
        windows.remove(0);
        TWindow activeWindow = null;
        for (TWindow w: windows) {
            if (w.getZ() > z) {
                w.setZ(w.getZ() - 1);
                if (w.getZ() == 0) {
                    w.setActive(true);
                    assert (activeWindow == null);
                    activeWindow = w;
                } else {
                    w.setActive(false);
                }
            }
        }

        // Perform window cleanup
        window.onClose();

        // Refresh screen
        repaint = true;

        // Check if we are closing a TMessageBox or similar
        if (secondaryEventReceiver != null) {
            assert (secondaryEventHandler != null);

            // Do not send events to the secondaryEventReceiver anymore, the
            // window is closed.
            secondaryEventReceiver = null;

            // Wake all threads: primary thread will be consuming events
            // again, and secondary thread will exit.
            synchronized (this) {
                this.notifyAll();
            }
        }
    }

    /**
     * Switch to the next window.
     *
     * @param forward if true, then switch to the next window in the list,
     * otherwise switch to the previous window in the list
     */
    public final void switchWindow(final boolean forward) {
        // Only switch if there are multiple windows
        if (windows.size() < 2) {
            return;
        }

        // Swap z/active between active window and the next in the list
        int activeWindowI = -1;
        for (int i = 0; i < windows.size(); i++) {
            if (windows.get(i).getActive()) {
                activeWindowI = i;
                break;
            }
        }
        assert (activeWindowI >= 0);

        // Do not switch if a window is modal
        if (windows.get(activeWindowI).isModal()) {
            return;
        }

        int nextWindowI;
        if (forward) {
            nextWindowI = (activeWindowI + 1) % windows.size();
        } else {
            if (activeWindowI == 0) {
                nextWindowI = windows.size() - 1;
            } else {
                nextWindowI = activeWindowI - 1;
            }
        }
        windows.get(activeWindowI).setActive(false);
        windows.get(activeWindowI).setZ(windows.get(nextWindowI).getZ());
        windows.get(nextWindowI).setZ(0);
        windows.get(nextWindowI).setActive(true);

        // Refresh
        repaint = true;
    }

    /**
     * Add a window to my window list and make it active.
     *
     * @param window new window to add
     */
    public final void addWindow(final TWindow window) {
        // Do not allow a modal window to spawn a non-modal window
        if ((windows.size() > 0) && (windows.get(0).isModal())) {
            assert (window.isModal());
        }
        for (TWindow w: windows) {
            w.setActive(false);
            w.setZ(w.getZ() + 1);
        }
        windows.add(window);
        window.setActive(true);
        window.setZ(0);
    }

    /**
     * Check if there is a system-modal window on top.
     *
     * @return true if the active window is modal
     */
    private boolean modalWindowActive() {
        if (windows.size() == 0) {
            return false;
        }
        return windows.get(windows.size() - 1).isModal();
    }

    /**
     * Check if a mouse event would hit either the active menu or any open
     * sub-menus.
     *
     * @param mouse mouse event
     * @return true if the mouse would hit the active menu or an open
     * sub-menu
     */
    private boolean mouseOnMenu(final TMouseEvent mouse) {
        assert (activeMenu != null);
        List<TMenu> menus = new LinkedList<TMenu>(subMenus);
        Collections.reverse(menus);
        for (TMenu menu: menus) {
            if (menu.mouseWouldHit(mouse)) {
                return true;
            }
        }
        return activeMenu.mouseWouldHit(mouse);
    }

    /**
     * See if we need to switch window or activate the menu based on
     * a mouse click.
     *
     * @param mouse mouse event
     */
    private void checkSwitchFocus(final TMouseEvent mouse) {

        if ((mouse.getType() == TMouseEvent.Type.MOUSE_DOWN)
            && (activeMenu != null)
            && (mouse.getAbsoluteY() != 0)
            && (!mouseOnMenu(mouse))
        ) {
            // They clicked outside the active menu, turn it off
            activeMenu.setActive(false);
            activeMenu = null;
            for (TMenu menu: subMenus) {
                menu.setActive(false);
            }
            subMenus.clear();
            // Continue checks
        }

        // See if they hit the menu bar
        if ((mouse.getType() == TMouseEvent.Type.MOUSE_DOWN)
            && (mouse.getMouse1())
            && (!modalWindowActive())
            && (mouse.getAbsoluteY() == 0)
        ) {

            for (TMenu menu: subMenus) {
                menu.setActive(false);
            }
            subMenus.clear();

            // They selected the menu, go activate it
            for (TMenu menu: menus) {
                if ((mouse.getAbsoluteX() >= menu.getX())
                    && (mouse.getAbsoluteX() < menu.getX()
                        + menu.getTitle().length() + 2)
                ) {
                    menu.setActive(true);
                    activeMenu = menu;
                } else {
                    menu.setActive(false);
                }
            }
            repaint = true;
            return;
        }

        // See if they hit the menu bar
        if ((mouse.getType() == TMouseEvent.Type.MOUSE_MOTION)
            && (mouse.getMouse1())
            && (activeMenu != null)
            && (mouse.getAbsoluteY() == 0)
        ) {

            TMenu oldMenu = activeMenu;
            for (TMenu menu: subMenus) {
                menu.setActive(false);
            }
            subMenus.clear();

            // See if we should switch menus
            for (TMenu menu: menus) {
                if ((mouse.getAbsoluteX() >= menu.getX())
                    && (mouse.getAbsoluteX() < menu.getX()
                        + menu.getTitle().length() + 2)
                ) {
                    menu.setActive(true);
                    activeMenu = menu;
                }
            }
            if (oldMenu != activeMenu) {
                // They switched menus
                oldMenu.setActive(false);
            }
            repaint = true;
            return;
        }

        // Only switch if there are multiple windows
        if (windows.size() < 2) {
            return;
        }

        // Switch on the upclick
        if (mouse.getType() != TMouseEvent.Type.MOUSE_UP) {
            return;
        }

        Collections.sort(windows);
        if (windows.get(0).isModal()) {
            // Modal windows don't switch
            return;
        }

        for (TWindow window: windows) {
            assert (!window.isModal());
            if (window.mouseWouldHit(mouse)) {
                if (window == windows.get(0)) {
                    // Clicked on the same window, nothing to do
                    return;
                }

                // We will be switching to another window
                assert (windows.get(0).getActive());
                assert (!window.getActive());
                windows.get(0).setActive(false);
                windows.get(0).setZ(window.getZ());
                window.setZ(0);
                window.setActive(true);
                repaint = true;
                return;
            }
        }

        // Clicked on the background, nothing to do
        return;
    }

    /**
     * Turn off the menu.
     */
    public final void closeMenu() {
        if (activeMenu != null) {
            activeMenu.setActive(false);
            activeMenu = null;
            for (TMenu menu: subMenus) {
                menu.setActive(false);
            }
            subMenus.clear();
        }
        repaint = true;
    }

    /**
     * Turn off a sub-menu.
     */
    public final void closeSubMenu() {
        assert (activeMenu != null);
        TMenu item = subMenus.get(subMenus.size() - 1);
        assert (item != null);
        item.setActive(false);
        subMenus.remove(subMenus.size() - 1);
        repaint = true;
    }

    /**
     * Switch to the next menu.
     *
     * @param forward if true, then switch to the next menu in the list,
     * otherwise switch to the previous menu in the list
     */
    public final void switchMenu(final boolean forward) {
        assert (activeMenu != null);

        for (TMenu menu: subMenus) {
            menu.setActive(false);
        }
        subMenus.clear();

        for (int i = 0; i < menus.size(); i++) {
            if (activeMenu == menus.get(i)) {
                if (forward) {
                    if (i < menus.size() - 1) {
                        i++;
                    }
                } else {
                    if (i > 0) {
                        i--;
                    }
                }
                activeMenu.setActive(false);
                activeMenu = menus.get(i);
                activeMenu.setActive(true);
                repaint = true;
                return;
            }
        }
    }

    /**
     * Method that TApplication subclasses can override to handle menu or
     * posted command events.
     *
     * @param command command event
     * @return if true, this event was consumed
     */
    protected boolean onCommand(final TCommandEvent command) {
        // Default: handle cmExit
        if (command.equals(cmExit)) {
            if (messageBox("Confirmation", "Exit application?",
                    TMessageBox.Type.YESNO).getResult() == TMessageBox.Result.YES) {
                quit = true;
            }
            repaint = true;
            return true;
        }

        /*
         TODO
        if (command.equals(cmShell)) {
            openTerminal(0, 0, TWindow.Flag.RESIZABLE);
            repaint = true;
            return true;
        }
         */

        if (command.equals(cmTile)) {
            tileWindows();
            repaint = true;
            return true;
        }
        if (command.equals(cmCascade)) {
            cascadeWindows();
            repaint = true;
            return true;
        }
        if (command.equals(cmCloseAll)) {
            closeAllWindows();
            repaint = true;
            return true;
        }

        return false;
    }

    /**
     * Method that TApplication subclasses can override to handle menu
     * events.
     *
     * @param menu menu event
     * @return if true, this event was consumed
     */
    protected boolean onMenu(final TMenuEvent menu) {

        // Default: handle MID_EXIT
        if (menu.getId() == TMenu.MID_EXIT) {
            if (messageBox("Confirmation", "Exit application?",
                    TMessageBox.Type.YESNO).getResult() == TMessageBox.Result.YES) {
                quit = true;
            }
            // System.err.printf("onMenu MID_EXIT result: quit = %s\n", quit);
            repaint = true;
            return true;
        }

        /*
         TODO
        if (menu.id == TMenu.MID_SHELL) {
            openTerminal(0, 0, TWindow.Flag.RESIZABLE);
            repaint = true;
            return true;
        }
         */

        if (menu.getId() == TMenu.MID_TILE) {
            tileWindows();
            repaint = true;
            return true;
        }
        if (menu.getId() == TMenu.MID_CASCADE) {
            cascadeWindows();
            repaint = true;
            return true;
        }
        if (menu.getId() == TMenu.MID_CLOSE_ALL) {
            closeAllWindows();
            repaint = true;
            return true;
        }
        return false;
    }

    /**
     * Method that TApplication subclasses can override to handle keystrokes.
     *
     * @param keypress keystroke event
     * @return if true, this event was consumed
     */
    protected boolean onKeypress(final TKeypressEvent keypress) {
        // Default: only menu shortcuts

        // Process Alt-F, Alt-E, etc. menu shortcut keys
        if (!keypress.getKey().getIsKey()
            && keypress.getKey().getAlt()
            && !keypress.getKey().getCtrl()
            && (activeMenu == null)
        ) {

            assert (subMenus.size() == 0);

            for (TMenu menu: menus) {
                if (Character.toLowerCase(menu.getMnemonic().getShortcut())
                    == Character.toLowerCase(keypress.getKey().getCh())
                ) {
                    activeMenu = menu;
                    menu.setActive(true);
                    repaint = true;
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Add a keyboard accelerator to the global hash.
     *
     * @param item menu item this accelerator relates to
     * @param keypress keypress that will dispatch a TMenuEvent
     */
    public final void addAccelerator(final TMenuItem item,
        final TKeypress keypress) {

        // System.err.printf("addAccelerator: key %s item %s\n", keypress, item);

        synchronized (accelerators) {
            assert (accelerators.get(keypress) == null);
            accelerators.put(keypress, item);
        }
    }

    /**
     * Recompute menu x positions based on their title length.
     */
    public final void recomputeMenuX() {
        int x = 0;
        for (TMenu menu: menus) {
            menu.setX(x);
            x += menu.getTitle().length() + 2;
        }
    }

    /**
     * Post an event to process and turn off the menu.
     *
     * @param event new event to add to the queue
     */
    public final void addMenuEvent(final TInputEvent event) {
        synchronized (fillEventQueue) {
            fillEventQueue.add(event);
        }
        closeMenu();
    }

    /**
     * Add a sub-menu to the list of open sub-menus.
     *
     * @param menu sub-menu
     */
    public final void addSubMenu(final TMenu menu) {
        subMenus.add(menu);
    }

    /**
     * Convenience function to add a top-level menu.
     *
     * @param title menu title
     * @return the new menu
     */
    public final TMenu addMenu(String title) {
        int x = 0;
        int y = 0;
        TMenu menu = new TMenu(this, x, y, title);
        menus.add(menu);
        recomputeMenuX();
        return menu;
    }

    /**
     * Convenience function to add a default "File" menu.
     *
     * @return the new menu
     */
    public final TMenu addFileMenu() {
        TMenu fileMenu = addMenu("&File");
        fileMenu.addDefaultItem(TMenu.MID_OPEN_FILE);
        fileMenu.addSeparator();
        fileMenu.addDefaultItem(TMenu.MID_SHELL);
        fileMenu.addDefaultItem(TMenu.MID_EXIT);
        return fileMenu;
    }

    /**
     * Convenience function to add a default "Edit" menu.
     *
     * @return the new menu
     */
    public final TMenu addEditMenu() {
        TMenu editMenu = addMenu("&Edit");
        editMenu.addDefaultItem(TMenu.MID_CUT);
        editMenu.addDefaultItem(TMenu.MID_COPY);
        editMenu.addDefaultItem(TMenu.MID_PASTE);
        editMenu.addDefaultItem(TMenu.MID_CLEAR);
        return editMenu;
    }

    /**
     * Convenience function to add a default "Window" menu.
     *
     * @return the new menu
     */
    public final TMenu addWindowMenu() {
        TMenu windowMenu = addMenu("&Window");
        windowMenu.addDefaultItem(TMenu.MID_TILE);
        windowMenu.addDefaultItem(TMenu.MID_CASCADE);
        windowMenu.addDefaultItem(TMenu.MID_CLOSE_ALL);
        windowMenu.addSeparator();
        windowMenu.addDefaultItem(TMenu.MID_WINDOW_MOVE);
        windowMenu.addDefaultItem(TMenu.MID_WINDOW_ZOOM);
        windowMenu.addDefaultItem(TMenu.MID_WINDOW_NEXT);
        windowMenu.addDefaultItem(TMenu.MID_WINDOW_PREVIOUS);
        windowMenu.addDefaultItem(TMenu.MID_WINDOW_CLOSE);
        return windowMenu;
    }

    /**
     * Close all open windows.
     */
    private void closeAllWindows() {
        // Don't do anything if we are in the menu
        if (activeMenu != null) {
            return;
        }
        for (TWindow window: windows) {
            closeWindow(window);
        }
    }

    /**
     * Re-layout the open windows as non-overlapping tiles.  This produces
     * almost the same results as Turbo Pascal 7.0's IDE.
     */
    private void tileWindows() {
        // Don't do anything if we are in the menu
        if (activeMenu != null) {
            return;
        }
        int z = windows.size();
        if (z == 0) {
            return;
        }
        int a = 0;
        int b = 0;
        a = (int)(Math.sqrt(z));
        int c = 0;
        while (c < a) {
            b = (z - c) / a;
            if (((a * b) + c) == z) {
                break;
            }
            c++;
        }
        assert (a > 0);
        assert (b > 0);
        assert (c < a);
        int newWidth = (getScreen().getWidth() / a);
        int newHeight1 = ((getScreen().getHeight() - 1) / b);
        int newHeight2 = ((getScreen().getHeight() - 1) / (b + c));
        // System.err.printf("Z %s a %s b %s c %s newWidth %s newHeight1 %s newHeight2 %s",
        //     z, a, b, c, newWidth, newHeight1, newHeight2);

        List<TWindow> sorted = new LinkedList<TWindow>(windows);
        Collections.sort(sorted);
        Collections.reverse(sorted);
        for (int i = 0; i < sorted.size(); i++) {
            int logicalX = i / b;
            int logicalY = i % b;
            if (i >= ((a - 1) * b)) {
                logicalX = a - 1;
                logicalY = i - ((a - 1) * b);
            }

            TWindow w = sorted.get(i);
            w.setX(logicalX * newWidth);
            w.setWidth(newWidth);
            if (i >= ((a - 1) * b)) {
                w.setY((logicalY * newHeight2) + 1);
                w.setHeight(newHeight2);
            } else {
                w.setY((logicalY * newHeight1) + 1);
                w.setHeight(newHeight1);
            }
        }
    }

    /**
     * Re-layout the open windows as overlapping cascaded windows.
     */
    private void cascadeWindows() {
        // Don't do anything if we are in the menu
        if (activeMenu != null) {
            return;
        }
        int x = 0;
        int y = 1;
        List<TWindow> sorted = new LinkedList<TWindow>(windows);
        Collections.sort(sorted);
        Collections.reverse(sorted);
        for (TWindow window: sorted) {
            window.setX(x);
            window.setY(y);
            x++;
            y++;
            if (x > getScreen().getWidth()) {
                x = 0;
            }
            if (y >= getScreen().getHeight()) {
                y = 1;
            }
        }
    }

    /**
     * Convenience function to add a timer.
     *
     * @param duration number of milliseconds to wait between ticks
     * @param recurring if true, re-schedule this timer after every tick
     * @param action function to call when button is pressed
     * @return the timer
     */
    public final TTimer addTimer(final long duration, final boolean recurring,
        final TAction action) {

        TTimer timer = new TTimer(duration, recurring, action);
        synchronized (timers) {
            timers.add(timer);
        }
        return timer;
    }

    /**
     * Convenience function to remove a timer.
     *
     * @param timer timer to remove
     */
    public final void removeTimer(final TTimer timer) {
        synchronized (timers) {
            timers.remove(timer);
        }
    }

    /**
     * Convenience function to spawn a message box.
     *
     * @param title window title, will be centered along the top border
     * @param caption message to display.  Use embedded newlines to get a
     * multi-line box.
     * @return the new message box
     */
    public final TMessageBox messageBox(final String title,
        final String caption) {

        return new TMessageBox(this, title, caption, TMessageBox.Type.OK);
    }

    /**
     * Convenience function to spawn a message box.
     *
     * @param title window title, will be centered along the top border
     * @param caption message to display.  Use embedded newlines to get a
     * multi-line box.
     * @param type one of the TMessageBox.Type constants.  Default is
     * Type.OK.
     * @return the new message box
     */
    public final TMessageBox messageBox(final String title,
        final String caption, final TMessageBox.Type type) {

        return new TMessageBox(this, title, caption, type);
    }

    /**
     * Convenience function to spawn an input box.
     *
     * @param title window title, will be centered along the top border
     * @param caption message to display.  Use embedded newlines to get a
     * multi-line box.
     * @return the new input box
     */
    public final TInputBox inputBox(final String title, final String caption) {

        return new TInputBox(this, title, caption);
    }

    /**
     * Convenience function to spawn an input box.
     *
     * @param title window title, will be centered along the top border
     * @param caption message to display.  Use embedded newlines to get a
     * multi-line box.
     * @param text initial text to seed the field with
     * @return the new input box
     */
    public final TInputBox inputBox(final String title, final String caption,
        final String text) {

        return new TInputBox(this, title, caption, text);
    }
    
}
