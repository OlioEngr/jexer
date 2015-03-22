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
 * TRadioButton implements a selectable radio button.
 */
public final class TRadioButton extends TWidget {

    /**
     * RadioButton state, true means selected.
     */
    private boolean selected = false;

    /**
     * Get RadioButton state, true means selected.
     *
     * @return if true then this is the one button in the group that is
     * selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Set RadioButton state, true means selected.  Note package private
     * access.
     *
     * @param selected if true then this is the one button in the group that
     * is selected
     */
    void setSelected(final boolean selected) {
        this.selected = selected;
    }

    /**
     * Label for this radio button.
     */
    private String label;

    /**
     * ID for this radio button.  Buttons start counting at 1 in the
     * RadioGroup.
     */
    private int id;

    /**
     * Get ID for this radio button.  Buttons start counting at 1 in the
     * RadioGroup.
     *
     * @return the ID
     */
    public int getId() {
        return id;
    }

    /**
     * Public constructor.
     *
     * @param parent parent widget
     * @param x column relative to parent
     * @param y row relative to parent
     * @param label label to display next to (right of) the radiobutton
     * @param id ID for this radio button
     */
    public TRadioButton(final TRadioGroup parent, final int x, final int y,
        final String label, final int id) {

        // Set parent and window
        super(parent, x, y, label.length() + 4, 1);

        this.label = label;
        this.id = id;

        setCursorVisible(true);
        setCursorX(1);
    }

    /**
     * Returns true if the mouse is currently on the radio button.
     *
     * @param mouse mouse event
     * @return if true the mouse is currently on the radio button
     */
    private boolean mouseOnRadioButton(final TMouseEvent mouse) {
        if ((mouse.getY() == 0)
            && (mouse.getX() >= 0)
            && (mouse.getX() <= 2)
        ) {
            return true;
        }
        return false;
    }

    /**
     * Draw a radio button with label.
     */
    @Override
    public void draw() {
        CellAttributes radioButtonColor;

        if (isAbsoluteActive()) {
            radioButtonColor = getTheme().getColor("tradiobutton.active");
        } else {
            radioButtonColor = getTheme().getColor("tradiobutton.inactive");
        }

        getScreen().putCharXY(0, 0, '(', radioButtonColor);
        if (selected) {
            getScreen().putCharXY(1, 0, GraphicsChars.CP437[0x07],
                radioButtonColor);
        } else {
            getScreen().putCharXY(1, 0, ' ', radioButtonColor);
        }
        getScreen().putCharXY(2, 0, ')', radioButtonColor);
        getScreen().putStrXY(4, 0, label, radioButtonColor);
    }

    /**
     * Handle mouse button presses.
     *
     * @param mouse mouse button press event
     */
    @Override
    public void onMouseDown(final TMouseEvent mouse) {
        if ((mouseOnRadioButton(mouse)) && (mouse.isMouse1())) {
            // Switch state
            selected = !selected;
            if (selected) {
                ((TRadioGroup) getParent()).setSelected(this);
            }
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
            selected = !selected;
            if (selected) {
                ((TRadioGroup) getParent()).setSelected(this);
            }
            return;
        }

        // Pass to parent for the things we don't care about.
        super.onKeypress(keypress);
    }

}
