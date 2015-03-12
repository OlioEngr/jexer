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

import jexer.bits.Cell;
import jexer.bits.CellAttributes;
import jexer.bits.GraphicsChars;
import jexer.event.TCommandEvent;
import jexer.event.TKeypressEvent;
import jexer.event.TMenuEvent;
import jexer.event.TMouseEvent;
import jexer.event.TResizeEvent;
import jexer.io.Screen;
import static jexer.TCommand.*;
import static jexer.TKeypress.*;

/**
 * TWindow is the top-level container and drawing surface for other widgets.
 */
public class TWindow extends TWidget {

    /**
     * Window's parent TApplication.
     */
    protected TApplication application;

    /**
     * Get this TWindow's parent TApplication.
     *
     * @return this TWindow's parent TApplication
     */
    public final TApplication getApplication() {
        return application;
    }

    /**
     * Get the Screen.
     *
     * @return the Screen
     */
    public final Screen getScreen() {
        return application.getScreen();
    }

    /**
     * Window title.
     */
    protected String title = "";

    /**
     * Window is resizable (default yes).
     */
    public static final int RESIZABLE   = 0x01;

    /**
     * Window is modal (default no).
     */
    public static final int MODAL       = 0x02;

    /**
     * Window is centered (default no).
     */
    public static final int CENTERED    = 0x04;

    /**
     * Window flags.
     */
    private int flags = RESIZABLE;

    /**
     * Z order.  Lower number means more in-front.
     */
    private int z = 0;

    /**
     * If true, then the user clicked on the title bar and is moving the
     * window.
     */
    private boolean inWindowMove = false;

    /**
     * If true, then the user clicked on the bottom right corner and is
     * resizing the window.
     */
    private boolean inWindowResize = false;

    /**
     * If true, then the user selected "Size/Move" (or hit Ctrl-F5) and is
     * resizing/moving the window via the keyboard.
     */
    private boolean inKeyboardResize = false;

    /**
     * If true, this window is maximized.
     */
    private boolean maximized = false;

    /**
     * Remember mouse state.
     */
    protected TMouseEvent mouse;

    // For moving the window.  resizing also uses moveWindowMouseX/Y
    private int moveWindowMouseX;
    private int moveWindowMouseY;
    private int oldWindowX;
    private int oldWindowY;

    // Resizing
    private int resizeWindowWidth;
    private int resizeWindowHeight;
    private int minimumWindowWidth = 10;
    private int minimumWindowHeight = 2;
    private int maximumWindowWidth = -1;
    private int maximumWindowHeight = -1;

    // For maximize/restore
    private int restoreWindowWidth;
    private int restoreWindowHeight;
    private int restoreWindowX;
    private int restoreWindowY;

    /**
     * Public constructor.  Window will be located at (0, 0).
     *
     * @param application TApplication that manages this window
     * @param title window title, will be centered along the top border
     * @param width width of window
     * @param height height of window
     */
    public TWindow(final TApplication application, final String title,
        final int width, final int height) {

        this(application, title, 0, 0, width, height, RESIZABLE);
    }

    /**
     * Public constructor.  Window will be located at (0, 0).
     *
     * @param application TApplication that manages this window
     * @param title window title, will be centered along the top border
     * @param width width of window
     * @param height height of window
     * @param flags bitmask of RESIZABLE, CENTERED, or MODAL
     */
    public TWindow(final TApplication application, final String title,
        final int width, final int height, final int flags) {

        this(application, title, 0, 0, width, height, flags);
    }

    /**
     * Public constructor.
     *
     * @param application TApplication that manages this window
     * @param title window title, will be centered along the top border
     * @param x column relative to parent
     * @param y row relative to parent
     * @param width width of window
     * @param height height of window
     */
    public TWindow(final TApplication application, final String title,
        final int x, final int y, final int width, final int height) {

        this(application, title, x, y, width, height, RESIZABLE);
    }

    /**
     * Public constructor.
     *
     * @param application TApplication that manages this window
     * @param title window title, will be centered along the top border
     * @param x column relative to parent
     * @param y row relative to parent
     * @param width width of window
     * @param height height of window
     * @param flags mask of RESIZABLE, CENTERED, or MODAL
     */
    public TWindow(final TApplication application, final String title,
        final int x, final int y, final int width, final int height,
        final int flags) {

        // I am my own window and parent
        this.parent = this;
        this.window = this;

        // Save fields
        this.title       = title;
        this.application = application;
        this.x           = x;
        this.y           = y + application.getDesktopTop();
        this.width       = width;
        this.height      = height;
        this.flags       = flags;

        // Minimum width/height are 10 and 2
        assert (width >= 10);
        assert (height >= 2);

        // MODAL implies CENTERED
        if (isModal()) {
            this.flags |= CENTERED;
        }

        // Center window if specified
        center();

        // Add me to the application
        application.addWindow(this);
    }

    /**
     * Recenter the window on-screen.
     */
    public final void center() {
        if ((flags & CENTERED) != 0) {
            if (width < getScreen().getWidth()) {
                x = (getScreen().getWidth() - width) / 2;
            } else {
                x = 0;
            }
            y = (application.getDesktopBottom() - application.getDesktopTop());
            y -= height;
            y /= 2;
            if (y < 0) {
                y = 0;
            }
            y += application.getDesktopTop();
        }
    }

    /**
     * Returns true if this window is modal.
     *
     * @return true if this window is modal
     */
    public final boolean isModal() {
        if ((flags & MODAL) == 0) {
            return false;
        }
        return true;
    }

    /**
     * Comparison operator sorts on z.
     *
     * @param that another TWindow instance
     * @return difference between this.z and that.z
     */
    public final int compare(final TWindow that) {
        return (z - that.z);
    }

    /**
     * Returns true if the mouse is currently on the close button.
     *
     * @return true if mouse is currently on the close button
     */
    private boolean mouseOnClose() {
        if ((mouse != null)
            && (mouse.getAbsoluteY() == y)
            && (mouse.getAbsoluteX() == x + 3)
        ) {
            return true;
        }
        return false;
    }

    /**
     * Returns true if the mouse is currently on the maximize/restore button.
     *
     * @return true if the mouse is currently on the maximize/restore button
     */
    private boolean mouseOnMaximize() {
        if ((mouse != null)
            && !isModal()
            && (mouse.getAbsoluteY() == y)
            && (mouse.getAbsoluteX() == x + width - 4)
        ) {
            return true;
        }
        return false;
    }

    /**
     * Returns true if the mouse is currently on the resizable lower right
     * corner.
     *
     * @return true if the mouse is currently on the resizable lower right
     * corner
     */
    private boolean mouseOnResize() {
        if (((flags & RESIZABLE) != 0)
            && !isModal()
            && (mouse != null)
            && (mouse.getAbsoluteY() == y + height - 1)
            && ((mouse.getAbsoluteX() == x + width - 1)
                || (mouse.getAbsoluteX() == x + width - 2))
        ) {
            return true;
        }
        return false;
    }

    /**
     * Retrieve the background color.
     *
     * @return the background color
     */
    protected final CellAttributes getBackground() {
        if (!isModal()
            && (inWindowMove || inWindowResize || inKeyboardResize)
        ) {
            assert (active);
            return application.getTheme().getColor("twindow.background.windowmove");
        } else if (isModal() && inWindowMove) {
            assert (active);
            return application.getTheme().getColor("twindow.background.modal");
        } else if (isModal()) {
            if (active) {
                return application.getTheme().getColor("twindow.background.modal");
            }
            return application.getTheme().getColor("twindow.background.modal.inactive");
        } else if (active) {
            assert (!isModal());
            return application.getTheme().getColor("twindow.background");
        } else {
            assert (!isModal());
            return application.getTheme().getColor("twindow.background.inactive");
        }
    }

    /**
     * Retrieve the border color.
     *
     * @return the border color
     */
    protected final CellAttributes getBorder() {
        if (!isModal()
            && (inWindowMove || inWindowResize || inKeyboardResize)
        ) {
            assert (active);
            return application.getTheme().getColor("twindow.border.windowmove");
        } else if (isModal() && inWindowMove) {
            assert (active);
            return application.getTheme().getColor("twindow.border.modal.windowmove");
        } else if (isModal()) {
            if (active) {
                return application.getTheme().getColor("twindow.border.modal");
            } else {
                return application.getTheme().getColor("twindow.border.modal.inactive");
            }
        } else if (active) {
            assert (!isModal());
            return application.getTheme().getColor("twindow.border");
        } else {
            assert (!isModal());
            return application.getTheme().getColor("twindow.border.inactive");
        }
    }

    /**
     * Retrieve the border line type.
     *
     * @return the border line type
     */
    protected final int getBorderType() {
        if (!isModal()
            && (inWindowMove || inWindowResize || inKeyboardResize)
        ) {
            assert (active);
            return 1;
        } else if (isModal() && inWindowMove) {
            assert (active);
            return 1;
        } else if (isModal()) {
            if (active) {
                return 2;
            } else {
                return 1;
            }
        } else if (active) {
            return 2;
        } else {
            return 1;
        }
    }

    /**
     * Subclasses should override this method to cleanup resources.  This is
     * called by application.closeWindow().
     */
    public void onClose() {
        // Default: do nothing
    }

    /**
     * Called by TApplication.drawChildren() to render on screen.
     */
    @Override
    public void draw() {
        // Draw the box and background first.
        CellAttributes border = getBorder();
        CellAttributes background = getBackground();
        int borderType = getBorderType();

        getScreen().drawBox(0, 0, width, height, border,
            background, borderType, true);

        // Draw the title
        int titleLeft = (width - title.length() - 2) / 2;
        putCharXY(titleLeft, 0, ' ', border);
        putStrXY(titleLeft + 1, 0, title);
        putCharXY(titleLeft + title.length() + 1, 0, ' ', border);

        if (active) {

            // Draw the close button
            putCharXY(2, 0, '[', border);
            putCharXY(4, 0, ']', border);
            if (mouseOnClose() && mouse.getMouse1()) {
                putCharXY(3, 0, GraphicsChars.CP437[0x0F],
                    !isModal()
                    ? application.getTheme().getColor("twindow.border.windowmove")
                    : application.getTheme().getColor("twindow.border.modal.windowmove"));
            } else {
                putCharXY(3, 0, GraphicsChars.CP437[0xFE],
                    !isModal()
                    ? application.getTheme().getColor("twindow.border.windowmove")
                    : application.getTheme().getColor("twindow.border.modal.windowmove"));
            }

            // Draw the maximize button
            if (!isModal()) {

                putCharXY(width - 5, 0, '[', border);
                putCharXY(width - 3, 0, ']', border);
                if (mouseOnMaximize() && mouse.getMouse1()) {
                    putCharXY(width - 4, 0, GraphicsChars.CP437[0x0F],
                        application.getTheme().getColor("twindow.border.windowmove"));
                } else {
                    if (maximized) {
                        putCharXY(width - 4, 0, GraphicsChars.CP437[0x12],
                            application.getTheme().getColor("twindow.border.windowmove"));
                    } else {
                        putCharXY(width - 4, 0, GraphicsChars.UPARROW,
                            application.getTheme().getColor("twindow.border.windowmove"));
                    }
                }

                // Draw the resize corner
                if ((flags & RESIZABLE) != 0) {
                    putCharXY(width - 2, height - 1, GraphicsChars.SINGLE_BAR,
                        application.getTheme().getColor("twindow.border.windowmove"));
                    putCharXY(width - 1, height - 1, GraphicsChars.LRCORNER,
                        application.getTheme().getColor("twindow.border.windowmove"));
                }
            }
        }
    }

    /**
     * Handle mouse button presses.
     *
     * @param mouse mouse button event
     */
    @Override
    public void onMouseDown(final TMouseEvent mouse) {
        this.mouse = mouse;
        application.setRepaint();

        inKeyboardResize = false;

        if ((mouse.getAbsoluteY() == y)
            && mouse.getMouse1()
            && (x <= mouse.getAbsoluteX())
            && (mouse.getAbsoluteX() < x + width)
            && !mouseOnClose()
            && !mouseOnMaximize()
        ) {
            // Begin moving window
            inWindowMove = true;
            moveWindowMouseX = mouse.getAbsoluteX();
            moveWindowMouseY = mouse.getAbsoluteY();
            oldWindowX = x;
            oldWindowY = y;
            if (maximized) {
                maximized = false;
            }
            return;
        }
        if (mouseOnResize()) {
            // Begin window resize
            inWindowResize = true;
            moveWindowMouseX = mouse.getAbsoluteX();
            moveWindowMouseY = mouse.getAbsoluteY();
            resizeWindowWidth = width;
            resizeWindowHeight = height;
            if (maximized) {
                maximized = false;
            }
            return;
        }

        // I didn't take it, pass it on to my children
        super.onMouseDown(mouse);
    }

    /**
     * Maximize window.
     */
    private void maximize() {
        restoreWindowWidth = width;
        restoreWindowHeight = height;
        restoreWindowX = x;
        restoreWindowY = y;
        width = getScreen().getWidth();
        height = application.getDesktopBottom() - 1;
        x = 0;
        y = 1;
        maximized = true;
    }

    /**
     * Restote (unmaximize) window.
     */
    private void restore() {
        width = restoreWindowWidth;
        height = restoreWindowHeight;
        x = restoreWindowX;
        y = restoreWindowY;
        maximized = false;
    }

    /**
     * Handle mouse button releases.
     *
     * @param mouse mouse button release event
     */
    @Override
    public void onMouseUp(final TMouseEvent mouse) {
        this.mouse = mouse;
        application.setRepaint();

        if ((inWindowMove) && (mouse.getMouse1())) {
            // Stop moving window
            inWindowMove = false;
            return;
        }

        if ((inWindowResize) && (mouse.getMouse1())) {
            // Stop resizing window
            inWindowResize = false;
            return;
        }

        if (mouse.getMouse1() && mouseOnClose()) {
            // Close window
            application.closeWindow(this);
            return;
        }

        if ((mouse.getAbsoluteY() == y) && mouse.getMouse1()
            && mouseOnMaximize()) {
            if (maximized) {
                // Restore
                restore();
            } else {
                // Maximize
                maximize();
            }
            // Pass a resize event to my children
            onResize(new TResizeEvent(TResizeEvent.Type.WIDGET, width, height));
            return;
        }

        // I didn't take it, pass it on to my children
        super.onMouseUp(mouse);
    }

    /**
     * Handle mouse movements.
     *
     * @param mouse mouse motion event
     */
    @Override
    public void onMouseMotion(final TMouseEvent mouse) {
        this.mouse = mouse;
        application.setRepaint();

        if (inWindowMove) {
            // Move window over
            x = oldWindowX + (mouse.getAbsoluteX() - moveWindowMouseX);
            y = oldWindowY + (mouse.getAbsoluteY() - moveWindowMouseY);
            // Don't cover up the menu bar
            if (y < application.getDesktopTop()) {
                y = application.getDesktopTop();
            }
            return;
        }

        if (inWindowResize) {
            // Move window over
            width = resizeWindowWidth + (mouse.getAbsoluteX() - moveWindowMouseX);
            height = resizeWindowHeight + (mouse.getAbsoluteY() - moveWindowMouseY);
            if (x + width > getScreen().getWidth()) {
                width = getScreen().getWidth() - x;
            }
            if (y + height > application.getDesktopBottom()) {
                y = application.getDesktopBottom() - height + 1;
            }
            // Don't cover up the menu bar
            if (y < application.getDesktopTop()) {
                y = application.getDesktopTop();
            }

            // Keep within min/max bounds
            if (width < minimumWindowWidth) {
                width = minimumWindowWidth;
                inWindowResize = false;
            }
            if (height < minimumWindowHeight) {
                height = minimumWindowHeight;
                inWindowResize = false;
            }
            if ((maximumWindowWidth > 0) && (width > maximumWindowWidth)) {
                width = maximumWindowWidth;
                inWindowResize = false;
            }
            if ((maximumWindowHeight > 0) && (height > maximumWindowHeight)) {
                height = maximumWindowHeight;
                inWindowResize = false;
            }

            // Pass a resize event to my children
            onResize(new TResizeEvent(TResizeEvent.Type.WIDGET, width, height));
            return;
        }

        // I didn't take it, pass it on to my children
        super.onMouseMotion(mouse);
    }

    /**
     * Handle keystrokes.
     *
     * @param keypress keystroke event
     */
    @Override
    public void onKeypress(final TKeypressEvent keypress) {

        if (inKeyboardResize) {

            // ESC - Exit size/move
            if (keypress.equals(kbEsc)) {
                inKeyboardResize = false;
            }

            if (keypress.equals(kbLeft)) {
                if (x > 0) {
                    x--;
                }
            }
            if (keypress.equals(kbRight)) {
                if (x < getScreen().getWidth() - 1) {
                    x++;
                }
            }
            if (keypress.equals(kbDown)) {
                if (y < application.getDesktopBottom() - 1) {
                    y++;
                }
            }
            if (keypress.equals(kbUp)) {
                if (y > 1) {
                    y--;
                }
            }
            if (keypress.equals(kbShiftLeft)) {
                if (width > minimumWindowWidth) {
                    width--;
                }
            }
            if (keypress.equals(kbShiftRight)) {
                if (width < maximumWindowWidth) {
                    width++;
                }
            }
            if (keypress.equals(kbShiftUp)) {
                if (height > minimumWindowHeight) {
                    height--;
                }
            }
            if (keypress.equals(kbShiftDown)) {
                if (height < maximumWindowHeight) {
                    height++;
                }
            }

            return;
        }

        // These keystrokes will typically not be seen unless a subclass
        // overrides onMenu() due to how TApplication dispatches
        // accelerators.

        // Ctrl-W - close window
        if (keypress.equals(kbCtrlW)) {
            application.closeWindow(this);
            return;
        }

        // F6 - behave like Alt-TAB
        if (keypress.equals(kbF6)) {
            application.switchWindow(true);
            return;
        }

        // Shift-F6 - behave like Shift-Alt-TAB
        if (keypress.equals(kbShiftF6)) {
            application.switchWindow(false);
            return;
        }

        // F5 - zoom
        if (keypress.equals(kbF5)) {
            if (maximized) {
                restore();
            } else {
                maximize();
            }
        }

        // Ctrl-F5 - size/move
        if (keypress.equals(kbCtrlF5)) {
            inKeyboardResize = !inKeyboardResize;
        }

        // I didn't take it, pass it on to my children
        super.onKeypress(keypress);
    }

    /**
     * Handle posted command events.
     *
     * @param command command event
     */
    @Override
    public void onCommand(final TCommandEvent command) {

        // These commands will typically not be seen unless a subclass
        // overrides onMenu() due to how TApplication dispatches
        // accelerators.

        if (command.equals(cmWindowClose)) {
            application.closeWindow(this);
            return;
        }

        if (command.equals(cmWindowNext)) {
            application.switchWindow(true);
            return;
        }

        if (command.equals(cmWindowPrevious)) {
            application.switchWindow(false);
            return;
        }

        if (command.equals(cmWindowMove)) {
            inKeyboardResize = true;
            return;
        }

        if (command.equals(cmWindowZoom)) {
            if (maximized) {
                restore();
            } else {
                maximize();
            }
        }

        // I didn't take it, pass it on to my children
        super.onCommand(command);
    }

    /**
     * Handle posted menu events.
     *
     * @param menu menu event
     */
    @Override
    public void onMenu(final TMenuEvent menu) {
        if (menu.getId() == TMenu.MID_WINDOW_CLOSE) {
            application.closeWindow(this);
            return;
        }

        if (menu.getId() == TMenu.MID_WINDOW_NEXT) {
            application.switchWindow(true);
            return;
        }

        if (menu.getId() == TMenu.MID_WINDOW_PREVIOUS) {
            application.switchWindow(false);
            return;
        }

        if (menu.getId() == TMenu.MID_WINDOW_MOVE) {
            inKeyboardResize = true;
            return;
        }

        if (menu.getId() == TMenu.MID_WINDOW_ZOOM) {
            if (maximized) {
                restore();
            } else {
                maximize();
            }
            return;
        }

        // I didn't take it, pass it on to my children
        super.onMenu(menu);
    }

    // ------------------------------------------------------------------------
    // Passthru for Screen functions ------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Get the attributes at one location.
     *
     * @param x column coordinate.  0 is the left-most column.
     * @param y row coordinate.  0 is the top-most row.
     * @return attributes at (x, y)
     */
    public final CellAttributes getAttrXY(final int x, final int y) {
        return getScreen().getAttrXY(x, y);
    }

    /**
     * Set the attributes at one location.
     *
     * @param x column coordinate.  0 is the left-most column.
     * @param y row coordinate.  0 is the top-most row.
     * @param attr attributes to use (bold, foreColor, backColor)
     */
    public final void putAttrXY(final int x, final int y,
        final CellAttributes attr) {

        getScreen().putAttrXY(x, y, attr);
    }

    /**
     * Set the attributes at one location.
     *
     * @param x column coordinate.  0 is the left-most column.
     * @param y row coordinate.  0 is the top-most row.
     * @param attr attributes to use (bold, foreColor, backColor)
     * @param clip if true, honor clipping/offset
     */
    public final void putAttrXY(final int x, final int y,
        final CellAttributes attr, final boolean clip) {

        getScreen().putAttrXY(x, y, attr, clip);
    }

    /**
     * Fill the entire screen with one character with attributes.
     *
     * @param ch character to draw
     * @param attr attributes to use (bold, foreColor, backColor)
     */
    public final void putAll(final char ch, final CellAttributes attr) {
        getScreen().putAll(ch, attr);
    }

    /**
     * Render one character with attributes.
     *
     * @param x column coordinate.  0 is the left-most column.
     * @param y row coordinate.  0 is the top-most row.
     * @param ch character + attributes to draw
     */
    public final void putCharXY(final int x, final int y, final Cell ch) {
        getScreen().putCharXY(x, y, ch);
    }

    /**
     * Render one character with attributes.
     *
     * @param x column coordinate.  0 is the left-most column.
     * @param y row coordinate.  0 is the top-most row.
     * @param ch character to draw
     * @param attr attributes to use (bold, foreColor, backColor)
     */
    public final void putCharXY(final int x, final int y, final char ch,
        final CellAttributes attr) {

        getScreen().putCharXY(x, y, ch, attr);
    }

    /**
     * Render one character without changing the underlying attributes.
     *
     * @param x column coordinate.  0 is the left-most column.
     * @param y row coordinate.  0 is the top-most row.
     * @param ch character to draw
     */
    public final void putCharXY(final int x, final int y, final char ch) {
        getScreen().putCharXY(x, y, ch);
    }

    /**
     * Render a string.  Does not wrap if the string exceeds the line.
     *
     * @param x column coordinate.  0 is the left-most column.
     * @param y row coordinate.  0 is the top-most row.
     * @param str string to draw
     * @param attr attributes to use (bold, foreColor, backColor)
     */
    public final void putStrXY(final int x, final int y, final String str,
        final CellAttributes attr) {

        getScreen().putStrXY(x, y, str, attr);
    }

    /**
     * Render a string without changing the underlying attribute.  Does not
     * wrap if the string exceeds the line.
     *
     * @param x column coordinate.  0 is the left-most column.
     * @param y row coordinate.  0 is the top-most row.
     * @param str string to draw
     */
    public final void putStrXY(final int x, final int y, final String str) {
        getScreen().putStrXY(x, y, str);
    }

    /**
     * Draw a vertical line from (x, y) to (x, y + n).
     *
     * @param x column coordinate.  0 is the left-most column.
     * @param y row coordinate.  0 is the top-most row.
     * @param n number of characters to draw
     * @param ch character to draw
     * @param attr attributes to use (bold, foreColor, backColor)
     */
    public final void vLineXY(final int x, final int y, final int n,
        final char ch, final CellAttributes attr) {

        getScreen().vLineXY(x, y, n, ch, attr);
    }

    /**
     * Draw a horizontal line from (x, y) to (x + n, y).
     *
     * @param x column coordinate.  0 is the left-most column.
     * @param y row coordinate.  0 is the top-most row.
     * @param n number of characters to draw
     * @param ch character to draw
     * @param attr attributes to use (bold, foreColor, backColor)
     */
    public final void hLineXY(final int x, final int y, final int n,
        final char ch, final CellAttributes attr) {

        getScreen().hLineXY(x, y, n, ch, attr);
    }


}
