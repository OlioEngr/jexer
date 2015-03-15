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

import jexer.bits.CellAttributes;
import jexer.bits.GraphicsChars;
import jexer.event.TKeypressEvent;
import jexer.event.TMouseEvent;
import static jexer.TKeypress.*;

/**
 *
 */
public final class TField extends TWidget {

    /**
     * Field text.
     */
    private String text = "";

    /**
     * Get field text.
     *
     * @return field text
     */
    public String getText() {
        return text;
    }

    /**
     * If true, only allow enough characters that will fit in the width.  If
     * false, allow the field to scroll to the right.
     */
    private boolean fixed = false;

    /**
     * Current editing position within text.
     */
    private int position = 0;

    /**
     * Beginning of visible portion.
     */
    private int windowStart = 0;

    /**
     * If true, new characters are inserted at position.
     */
    private boolean insertMode = true;

    /**
     * Remember mouse state.
     */
    private TMouseEvent mouse;

    /**
     * The action to perform when the user presses enter.
     */
    private TAction enterAction;

    /**
     * The action to perform when the text is updated.
     */
    private TAction updateAction;

    /**
     * Public constructor.
     *
     * @param parent parent widget
     * @param x column relative to parent
     * @param y row relative to parent
     * @param width visible text width
     * @param fixed if true, the text cannot exceed the display width
     */
    public TField(final TWidget parent, final int x, final int y,
        final int width, final boolean fixed) {

        this(parent, x, y, width, fixed, "", null, null);
    }

    /**
     * Public constructor.
     *
     * @param parent parent widget
     * @param x column relative to parent
     * @param y row relative to parent
     * @param width visible text width
     * @param fixed if true, the text cannot exceed the display width
     * @param text initial text, default is empty string
     */
    public TField(final TWidget parent, final int x, final int y,
        final int width, final boolean fixed, final String text) {

        this(parent, x, y, width, fixed, text, null, null);
    }

    /**
     * Public constructor.
     *
     * @param parent parent widget
     * @param x column relative to parent
     * @param y row relative to parent
     * @param width visible text width
     * @param fixed if true, the text cannot exceed the display width
     * @param text initial text, default is empty string
     * @param enterAction function to call when enter key is pressed
     * @param updateAction function to call when the text is updated
     */
    public TField(final TWidget parent, final int x, final int y,
        final int width, final boolean fixed, final String text,
        final TAction enterAction, final TAction updateAction) {

        // Set parent and window
        super(parent);
        setX(x);
        setY(y);
        setHeight(1);
        setWidth(width);
        setHasCursor(true);

        this.fixed = fixed;
        this.text = text;
        this.enterAction = enterAction;
        this.updateAction = updateAction;
    }

    /**
     * Returns true if the mouse is currently on the field.
     *
     * @return if true the mouse is currently on the field
     */
    private boolean mouseOnField() {
        int rightEdge = getWidth() - 1;
        if ((mouse != null)
            && (mouse.getY() == 0)
            && (mouse.getX() >= 0)
            && (mouse.getX() <= rightEdge)
        ) {
            return true;
        }
        return false;
    }

    /**
     * Dispatch to the action function.
     *
     * @param enter if true, the user pressed Enter, else this was an update
     * to the text.
     */
    private void dispatch(final boolean enter) {
        if (enter) {
            if (enterAction != null) {
                enterAction.DO();
            }
        } else {
            if (updateAction != null) {
                updateAction.DO();
            }
        }
    }

    /**
     * Draw the text field.
     */
    @Override
    public void draw() {
        CellAttributes fieldColor;

        if (getAbsoluteActive()) {
            fieldColor = getTheme().getColor("tfield.active");
        } else {
            fieldColor = getTheme().getColor("tfield.inactive");
        }

        int end = windowStart + getWidth();
        if (end > text.length()) {
            end = text.length();
        }
        getScreen().hLineXY(0, 0, getWidth(), GraphicsChars.HATCH, fieldColor);
        getScreen().putStrXY(0, 0, text.substring(windowStart, end),
            fieldColor);

        // Fix the cursor, it will be rendered by TApplication.drawAll().
        updateCursor();
    }

    /**
     * Update the cursor position.
     */
    private void updateCursor() {
        if ((position > getWidth()) && fixed) {
            setCursorX(getWidth());
        } else if ((position - windowStart == getWidth()) && !fixed) {
            setCursorX(getWidth() - 1);
        } else {
            setCursorX(position - windowStart);
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

        if ((mouseOnField()) && (mouse.getMouse1())) {
            // Move cursor
            int deltaX = mouse.getX() - getCursorX();
            position += deltaX;
            if (position > text.length()) {
                position = text.length();
            }
            updateCursor();
            return;
        }
    }

    /**
     * Handle keystrokes.
     *
     * @param keypress keystroke event
     */
    @Override
    public void onKeypress(final TKeypressEvent keypress) {

        if (keypress.equals(kbLeft)) {
            if (position > 0) {
                position--;
            }
            if (fixed == false) {
                if ((position == windowStart) && (windowStart > 0)) {
                    windowStart--;
                }
            }
            return;
        }

        if (keypress.equals(kbRight)) {
            if (position < text.length()) {
                position++;
                if (fixed == true) {
                    if (position == getWidth()) {
                        position--;
                    }
                } else {
                    if ((position - windowStart) == getWidth()) {
                        windowStart++;
                    }
                }
            }
            return;
        }

        if (keypress.equals(kbEnter)) {
            dispatch(true);
            return;
        }

        if (keypress.equals(kbIns)) {
            insertMode = !insertMode;
            return;
        }
        if (keypress.equals(kbHome)) {
            position = 0;
            windowStart = 0;
            return;
        }

        if (keypress.equals(kbEnd)) {
            position = text.length();
            if (fixed == true) {
                if (position >= getWidth()) {
                    position = text.length() - 1;
                }
            } else {
                windowStart = text.length() - getWidth() + 1;
                if (windowStart < 0) {
                    windowStart = 0;
                }
            }
            return;
        }

        if (keypress.equals(kbDel)) {
            if ((text.length() > 0) && (position < text.length())) {
                text = text.substring(0, position)
                        + text.substring(position + 1);
            }
            return;
        }

        if (keypress.equals(kbBackspace) || keypress.equals(kbBackspaceDel)) {
            if (position > 0) {
                position--;
                text = text.substring(0, position)
                        + text.substring(position + 1);
            }
            if (fixed == false) {
                if ((position == windowStart)
                    && (windowStart > 0)
                ) {
                    windowStart--;
                }
            }
            dispatch(false);
            return;
        }

        if (!keypress.getKey().getIsKey()
            && !keypress.getKey().getAlt()
            && !keypress.getKey().getCtrl()
        ) {
            // Plain old keystroke, process it
            if ((position == text.length())
                && (text.length() < getWidth())) {

                // Append case
                appendChar(keypress.getKey().getCh());
            } else if ((position < text.length())
                && (text.length() < getWidth())) {

                // Overwrite or insert a character
                if (insertMode == false) {
                    // Replace character
                    text = text.substring(0, position)
                            + keypress.getKey().getCh()
                            + text.substring(position + 1);
                    position++;
                } else {
                    // Insert character
                    insertChar(keypress.getKey().getCh());
                }
            } else if ((position < text.length())
                && (text.length() >= getWidth())) {

                // Multiple cases here
                if ((fixed == true) && (insertMode == true)) {
                    // Buffer is full, do nothing
                } else if ((fixed == true) && (insertMode == false)) {
                    // Overwrite the last character, maybe move position
                    text = text.substring(0, position)
                            + keypress.getKey().getCh()
                            + text.substring(position + 1);
                    if (position < getWidth() - 1) {
                        position++;
                    }
                } else if ((fixed == false) && (insertMode == false)) {
                    // Overwrite the last character, definitely move position
                    text = text.substring(0, position)
                            + keypress.getKey().getCh()
                            + text.substring(position + 1);
                    position++;
                } else {
                    if (position == text.length()) {
                        // Append this character
                        appendChar(keypress.getKey().getCh());
                    } else {
                        // Insert this character
                        insertChar(keypress.getKey().getCh());
                    }
                }
            } else {
                assert (!fixed);

                // Append this character
                appendChar(keypress.getKey().getCh());
            }
            dispatch(false);
            return;
        }

        // Pass to parent for the things we don't care about.
        super.onKeypress(keypress);
    }

    /**
     * Append char to the end of the field.
     *
     * @param ch = char to append
     */
    private void appendChar(final char ch) {
        // Append the LAST character
        text += ch;
        position++;

        assert (position == text.length());

        if (fixed) {
            if (position == getWidth()) {
                position--;
            }
        } else {
            if ((position - windowStart) == getWidth()) {
                windowStart++;
            }
        }
    }

    /**
     * Insert char somewhere in the middle of the field.
     *
     * @param ch char to append
     */
    private void insertChar(final char ch) {
        text = text.substring(0, position) + ch + text.substring(position);
        position++;
        if ((position - windowStart) == getWidth()) {
            assert (!fixed);
            windowStart++;
        }
    }

}
