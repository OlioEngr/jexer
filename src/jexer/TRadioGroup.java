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

/**
 * TRadioGroup is a collection of TRadioButtons with a box and label.
 */
public final class TRadioGroup extends TWidget {

    /**
     * Label for this radio button group.
     */
    private String label;

    /**
     * Only one of my children can be selected.
     */
    private TRadioButton selectedButton = null;

    /**
     * Get the radio button ID that was selected.
     *
     * @return ID of the selected button, or 0 if no button is selected
     */
    public int getSelected() {
        if (selectedButton == null) {
            return 0;
        }
        return selectedButton.getId();
    }

    /**
     * Set the new selected radio button.  Note package private access.
     *
     * @param button new button that became selected
     */
    void setSelected(final TRadioButton button) {
        assert (button.isSelected());
        if (selectedButton != null) {
            selectedButton.setSelected(false);
        }
        selectedButton = button;
    }

    /**
     * Public constructor.
     *
     * @param parent parent widget
     * @param x column relative to parent
     * @param y row relative to parent
     * @param label label to display on the group box
     */
    public TRadioGroup(final TWidget parent, final int x, final int y,
        final String label) {

        // Set parent and window
        super(parent, x, y, label.length() + 4, 2);

        this.label = label;
    }

    /**
     * Draw a radio button with label.
     */
    @Override
    public void draw() {
        CellAttributes radioGroupColor;

        if (isAbsoluteActive()) {
            radioGroupColor = getTheme().getColor("tradiogroup.active");
        } else {
            radioGroupColor = getTheme().getColor("tradiogroup.inactive");
        }

        getScreen().drawBox(0, 0, getWidth(), getHeight(),
            radioGroupColor, radioGroupColor, 3, false);

        getScreen().hLineXY(1, 0, label.length() + 2, ' ', radioGroupColor);
        getScreen().putStrXY(2, 0, label, radioGroupColor);
    }

    /**
     * Convenience function to add a radio button to this group.
     *
     * @param label label to display next to (right of) the radiobutton
     * @return the new radio button
     */
    public TRadioButton addRadioButton(final String label) {
        int buttonX = 1;
        int buttonY = getChildren().size() + 1;
        if (label.length() + 4 > getWidth()) {
            setWidth(label.length() + 7);
        }
        setHeight(getChildren().size() + 3);
        return new TRadioButton(this, buttonX, buttonY, label,
            getChildren().size() + 1);
    }

}
