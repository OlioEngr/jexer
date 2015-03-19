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
 * TCheckbox implements an on/off checkbox.
 */
public final class TCheckbox extends TWidget {

    /**
     * Checkbox state, true means checked.
     */
    private boolean checked = false;

    /**
     * Label for this checkbox.
     */
    private String label;

    /**
     * Public constructor.
     *
     * @param parent parent widget
     * @param x column relative to parent
     * @param y row relative to parent
     * @param label label to display next to (right of) the checkbox
     * @param checked initial check state
     */
    public TCheckbox(final TWidget parent, final int x, final int y,
        final String label, final boolean checked) {

        // Set parent and window
        super(parent, x, y, label.length() + 4, 1);

        this.label = label;
        this.checked = checked;

        setHasCursor(true);
        setCursorX(1);
    }

    /**
     * Returns true if the mouse is currently on the checkbox.
     *
     * @param mouse mouse event
     * @return true if the mouse is currently on the checkbox
     */
    private boolean mouseOnCheckbox(final TMouseEvent mouse) {
        if ((mouse.getY() == 0)
            && (mouse.getX() >= 0)
            && (mouse.getX() <= 2)
        ) {
            return true;
        }
        return false;
    }

    /**
     * Draw a checkbox with label.
     */
    @Override
    public void draw() {
        CellAttributes checkboxColor;

        if (getAbsoluteActive()) {
            checkboxColor = getTheme().getColor("tcheckbox.active");
        } else {
            checkboxColor = getTheme().getColor("tcheckbox.inactive");
        }

        getScreen().putCharXY(0, 0, '[', checkboxColor);
        if (checked) {
            getScreen().putCharXY(1, 0, GraphicsChars.CHECK, checkboxColor);
        } else {
            getScreen().putCharXY(1, 0, ' ', checkboxColor);
        }
        getScreen().putCharXY(2, 0, ']', checkboxColor);
        getScreen().putStrXY(4, 0, label, checkboxColor);
    }

    /**
     * Handle mouse checkbox presses.
     *
     * @param mouse mouse button down event
     */
    @Override
    public void onMouseDown(final TMouseEvent mouse) {
        if ((mouseOnCheckbox(mouse)) && (mouse.getMouse1())) {
            // Switch state
            checked = !checked;
        }
    }

    /**
     * Handle keystrokes.
     *
     * @param keypress keystroke event
     */
    @Override
    public void onKeypress(final TKeypressEvent keypress) {
        if (keypress.equals(kbSpace)) {
            checked = !checked;
            return;
        }

        // Pass to parent for the things we don't care about.
        super.onKeypress(keypress);
    }

}
