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
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jexer.bits.Cell;
import jexer.bits.CellAttributes;
import jexer.event.TKeypressEvent;
import jexer.event.TMouseEvent;
import jexer.event.TResizeEvent;
import jexer.tterminal.DisplayLine;
import jexer.tterminal.ECMA48;
import static jexer.TKeypress.*;

/**
 * TTerminalWindow exposes a ECMA-48 / ANSI X3.64 style terminal in a window.
 */
public class TTerminalWindow extends TWindow {

    /**
     * The emulator.
     */
    private ECMA48 emulator;

    /**
     * The Process created by the shell spawning constructor.
     */
    private Process shell;

    /**
     * Vertical scrollbar.
     */
    private TVScroller vScroller;

    /**
     * Public constructor spawns a shell.
     *
     * @param application TApplication that manages this window
     * @param x column relative to parent
     * @param y row relative to parent
     * @param flags mask of CENTERED, MODAL, or RESIZABLE
     */
    public TTerminalWindow(final TApplication application, final int x,
        final int y, final int flags) {

        super(application, "Terminal", x, y, 80 + 2, 24 + 2, flags);

        // Assume XTERM
        ECMA48.DeviceType deviceType = ECMA48.DeviceType.XTERM;

        try {
            String [] cmdShellWindows = {
                "cmd.exe"
            };

            // You cannot run a login shell in a bare Process interactively,
            // due to libc's behavior of buffering when stdin/stdout aren't a
            // tty.  Use 'script' instead to run a shell in a pty.
            String [] cmdShell = {
                "script", "-fqe", "/dev/null"
            };
            // Spawn a shell and pass its I/O to the other constructor.

            ProcessBuilder pb;
            if (System.getProperty("os.name").startsWith("Windows")) {
                pb = new ProcessBuilder(cmdShellWindows);
            } else {
                pb = new ProcessBuilder(cmdShell);
            }
            Map<String, String> env = pb.environment();
            env.put("TERM", ECMA48.deviceTypeTerm(deviceType));
            env.put("LANG", ECMA48.deviceTypeLang(deviceType, "en"));
            env.put("COLUMNS", "80");
            env.put("LINES", "24");
            pb.redirectErrorStream(true);
            shell = pb.start();
            emulator = new ECMA48(deviceType, shell.getInputStream(),
                shell.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Setup the scroll bars
        onResize(new TResizeEvent(TResizeEvent.Type.WIDGET, getWidth(),
                getHeight()));
    }

    /**
     * Public constructor.
     *
     * @param application TApplication that manages this window
     * @param x column relative to parent
     * @param y row relative to parent
     * @param flags mask of CENTERED, MODAL, or RESIZABLE
     * @param input an InputStream connected to the remote side.  For type ==
     * XTERM, input is converted to a Reader with UTF-8 encoding.
     * @param output an OutputStream connected to the remote user.  For type
     * == XTERM, output is converted to a Writer with UTF-8 encoding.
     * @throws UnsupportedEncodingException if an exception is thrown when
     * creating the InputStreamReader
     */
    public TTerminalWindow(final TApplication application, final int x,
        final int y, final int flags, final InputStream input,
        final OutputStream output) throws UnsupportedEncodingException {

        super(application, "Terminal", x, y, 80 + 2, 24 + 2, flags);

        emulator = new ECMA48(ECMA48.DeviceType.XTERM, input, output);

        // Setup the scroll bars
        onResize(new TResizeEvent(TResizeEvent.Type.WIDGET, getWidth(),
                getHeight()));

    }

    /**
     * Draw the display buffer.
     */
    @Override
    public void draw() {
        // Synchronize against the emulator so we don't stomp on its reader
        // thread.
        synchronized (emulator) {

            // Update the scroll bars
            reflow();

            // Draw the box using my superclass
            super.draw();

            List<DisplayLine> scrollback = emulator.getScrollbackBuffer();
            List<DisplayLine> display = emulator.getDisplayBuffer();

            // Put together the visible rows
            int visibleHeight = getHeight() - 2;
            int visibleBottom = scrollback.size() + display.size()
                + vScroller.getValue();
            assert (visibleBottom >= 0);

            List<DisplayLine> preceedingBlankLines = new LinkedList<DisplayLine>();
            int visibleTop = visibleBottom - visibleHeight;
            if (visibleTop < 0) {
                for (int i = visibleTop; i < 0; i++) {
                    preceedingBlankLines.add(emulator.getBlankDisplayLine());
                }
                visibleTop = 0;
            }
            assert (visibleTop >= 0);

            List<DisplayLine> displayLines = new LinkedList<DisplayLine>();
            displayLines.addAll(scrollback);
            displayLines.addAll(display);

            List<DisplayLine> visibleLines = new LinkedList<DisplayLine>();
            visibleLines.addAll(preceedingBlankLines);
            visibleLines.addAll(displayLines.subList(visibleTop,
                    visibleBottom));

            visibleHeight -= visibleLines.size();
            assert (visibleHeight >= 0);

            // Now draw the emulator screen
            int row = 1;
            for (DisplayLine line: visibleLines) {
                int widthMax = emulator.getWidth();
                if (line.isDoubleWidth()) {
                    widthMax /= 2;
                }
                if (widthMax > getWidth() - 2) {
                    widthMax = getWidth() - 2;
                }
                for (int i = 0; i < widthMax; i++) {
                    Cell ch = line.charAt(i);
                    Cell newCell = new Cell();
                    newCell.setTo(ch);
                    boolean reverse = line.isReverseColor() ^ ch.isReverse();
                    newCell.setReverse(false);
                    if (reverse) {
                        newCell.setBackColor(ch.getForeColor());
                        newCell.setForeColor(ch.getBackColor());
                    }
                    if (line.isDoubleWidth()) {
                        getScreen().putCharXY((i * 2) + 1, row, newCell);
                        getScreen().putCharXY((i * 2) + 2, row, ' ', newCell);
                    } else {
                        getScreen().putCharXY(i + 1, row, newCell);
                    }
                }
                row++;
                if (row == getHeight() - 1) {
                    // Don't overwrite the box edge
                    break;
                }
            }
            CellAttributes background = new CellAttributes();
            // Fill in the blank lines on bottom
            for (int i = 0; i < visibleHeight; i++) {
                getScreen().hLineXY(1, i + row, getWidth() - 2, ' ',
                    background);
            }

        } // synchronized (emulator)

    }

    /**
     * Handle window close.
     */
    @Override public void onClose() {
        if (shell != null) {
            shell.destroy();
            shell = null;
        } else {
            emulator.close();
        }
    }

    /**
     * Copy out variables from the emulator that TTerminal has to expose on
     * screen.
     */
    private void readEmulatorState() {
        // Synchronize against the emulator so we don't stomp on its reader
        // thread.
        synchronized (emulator) {

            setCursorX(emulator.getCursorX() + 1);
            setCursorY(emulator.getCursorY() + 1
                + (getHeight() - 2 - emulator.getHeight()));
            if (vScroller != null) {
                setCursorY(getCursorY() - vScroller.getValue());
            }
            setCursorVisible(emulator.isCursorVisible());
            if (getCursorX() > getWidth() - 2) {
                setCursorVisible(false);
            }
            if ((getCursorY() > getHeight() - 2) || (getCursorY() < 0)) {
                setCursorVisible(false);
            }
            if (emulator.getScreenTitle().length() > 0) {
                // Only update the title if the shell is still alive
                if (shell != null) {
                    setTitle(emulator.getScreenTitle());
                }
            }

            // Check to see if the shell has died.
            if (!emulator.isReading() && (shell != null)) {
                try {
                    int rc = shell.exitValue();
                    // The emulator exited on its own, all is fine
                    setTitle(String.format("%s [Completed - %d]",
                            getTitle(), rc));
                    shell = null;
                    emulator.close();
                } catch (IllegalThreadStateException e) {
                    // The emulator thread has exited, but the shell Process
                    // hasn't figured that out yet.  Do nothing, we will see
                    // this in a future tick.
                }
            } else if (emulator.isReading() && (shell != null)) {
                // The shell might be dead, let's check
                try {
                    int rc = shell.exitValue();
                    // If we got here, the shell died.
                    setTitle(String.format("%s [Completed - %d]",
                            getTitle(), rc));
                    shell = null;
                    emulator.close();
                } catch (IllegalThreadStateException e) {
                    // The shell is still running, do nothing.
                }
            }

        } // synchronized (emulator)
    }

    /**
     * Handle window/screen resize events.
     *
     * @param resize resize event
     */
    @Override
    public void onResize(final TResizeEvent resize) {

        // Synchronize against the emulator so we don't stomp on its reader
        // thread.
        synchronized (emulator) {

            if (resize.getType() == TResizeEvent.Type.WIDGET) {
                // Resize the scroll bars
                reflow();

                // Get out of scrollback
                vScroller.setValue(0);
            }
            return;

        } // synchronized (emulator)
    }

    /**
     * Resize scrollbars for a new width/height.
     */
    private void reflow() {

        // Synchronize against the emulator so we don't stomp on its reader
        // thread.
        synchronized (emulator) {

            // Pull cursor information
            readEmulatorState();

            // Vertical scrollbar
            if (vScroller == null) {
                vScroller = new TVScroller(this, getWidth() - 2, 0,
                    getHeight() - 2);
                vScroller.setBottomValue(0);
                vScroller.setValue(0);
            } else {
                vScroller.setX(getWidth() - 2);
                vScroller.setHeight(getHeight() - 2);
            }
            vScroller.setTopValue(getHeight() - 2
                - (emulator.getScrollbackBuffer().size()
                    + emulator.getDisplayBuffer().size()));
            vScroller.setBigChange(getHeight() - 2);

        } // synchronized (emulator)
    }

    /**
     * Check if a mouse press/release/motion event coordinate is over the
     * emulator.
     *
     * @param mouse a mouse-based event
     * @return whether or not the mouse is on the emulator
     */
    private final boolean mouseOnEmulator(final TMouseEvent mouse) {

        synchronized (emulator) {
            if (!emulator.isReading()) {
                return false;
            }
        }

        if ((mouse.getAbsoluteX() >= getAbsoluteX() + 1)
            && (mouse.getAbsoluteX() <  getAbsoluteX() + getWidth() - 1)
            && (mouse.getAbsoluteY() >= getAbsoluteY() + 1)
            && (mouse.getAbsoluteY() <  getAbsoluteY() + getHeight() - 1)
        ) {
            return true;
        }
        return false;
    }

    /**
     * Handle keystrokes.
     *
     * @param keypress keystroke event
     */
    @Override
    public void onKeypress(final TKeypressEvent keypress) {

        // Scrollback up/down
        if (keypress.equals(kbShiftPgUp)
            || keypress.equals(kbCtrlPgUp)
            || keypress.equals(kbAltPgUp)
        ) {
            vScroller.bigDecrement();
            return;
        }
        if (keypress.equals(kbShiftPgDn)
            || keypress.equals(kbCtrlPgDn)
            || keypress.equals(kbAltPgDn)
        ) {
            vScroller.bigIncrement();
            return;
        }

        // Synchronize against the emulator so we don't stomp on its reader
        // thread.
        synchronized (emulator) {
            if (emulator.isReading()) {
                // Get out of scrollback
                vScroller.setValue(0);
                emulator.keypress(keypress.getKey());

                // UGLY HACK TIME!  cmd.exe needs CRLF, not just CR, so if
                // this is kBEnter then also send kbCtrlJ.
                if (System.getProperty("os.name").startsWith("Windows")) {
                    if (keypress.equals(kbEnter)) {
                        emulator.keypress(kbCtrlJ);
                    }
                }

                readEmulatorState();
                return;
            }
        }

        // Process is closed, honor "normal" TUI keystrokes
        super.onKeypress(keypress);
    }

    /**
     * Handle mouse press events.
     *
     * @param mouse mouse button press event
     */
    @Override
    public void onMouseDown(final TMouseEvent mouse) {
        if (inWindowMove || inWindowResize) {
            // TWindow needs to deal with this.
            super.onMouseDown(mouse);
            return;
        }

        if (mouse.isMouseWheelUp()) {
            vScroller.decrement();
            return;
        }
        if (mouse.isMouseWheelDown()) {
            vScroller.increment();
            return;
        }
        if (mouseOnEmulator(mouse)) {
            synchronized (emulator) {
                mouse.setX(mouse.getX() - 1);
                mouse.setY(mouse.getY() - 1);
                emulator.mouse(mouse);
                readEmulatorState();
                return;
            }
        }

        // Emulator didn't consume it, pass it on
        super.onMouseDown(mouse);
    }

    /**
     * Handle mouse release events.
     *
     * @param mouse mouse button release event
     */
    @Override
    public void onMouseUp(final TMouseEvent mouse) {
        if (inWindowMove || inWindowResize) {
            // TWindow needs to deal with this.
            super.onMouseUp(mouse);
            return;
        }

        if (mouseOnEmulator(mouse)) {
            synchronized (emulator) {
                mouse.setX(mouse.getX() - 1);
                mouse.setY(mouse.getY() - 1);
                emulator.mouse(mouse);
                readEmulatorState();
                return;
            }
        }

        // Emulator didn't consume it, pass it on
        super.onMouseUp(mouse);
    }

    /**
     * Handle mouse motion events.
     *
     * @param mouse mouse motion event
     */
    @Override
    public void onMouseMotion(final TMouseEvent mouse) {
        if (inWindowMove || inWindowResize) {
            // TWindow needs to deal with this.
            super.onMouseMotion(mouse);
            return;
        }

        if (mouseOnEmulator(mouse)) {
            synchronized (emulator) {
                mouse.setX(mouse.getX() - 1);
                mouse.setY(mouse.getY() - 1);
                emulator.mouse(mouse);
                readEmulatorState();
                return;
            }
        }

        // Emulator didn't consume it, pass it on
        super.onMouseMotion(mouse);
    }

}
