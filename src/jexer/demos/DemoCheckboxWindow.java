/*
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
package jexer.demos;

import jexer.*;

/**
 * This window demonstates the TRadioGroup, TRadioButton, and TCheckbox
 * widgets.
 */
public class DemoCheckboxWindow extends TWindow {

    /**
     * Constructor.
     *
     * @param parent the main application
     */
    DemoCheckboxWindow(final TApplication parent) {
        this(parent, CENTERED | RESIZABLE);
    }

    /**
     * Constructor.
     *
     * @param parent the main application
     * @param flags bitmask of MODAL, CENTERED, or RESIZABLE
     */
    DemoCheckboxWindow(final TApplication parent, final int flags) {
        // Construct a demo window.  X and Y don't matter because it will be
        // centered on screen.
        super(parent, "Radiobuttons and Checkboxes", 0, 0, 60, 15, flags);

        int row = 1;

        // Add some widgets
        addLabel("Check box example 1", 1, row);
        addCheckbox(35, row++, "Checkbox 1", false);
        addLabel("Check box example 2", 1, row);
        addCheckbox(35, row++, "Checkbox 2", true);
        row += 2;

        TRadioGroup group = addRadioGroup(1, row, "Group 1");
        group.addRadioButton("Radio option 1");
        group.addRadioButton("Radio option 2");
        group.addRadioButton("Radio option 3");

        addButton("&Close Window", (getWidth() - 14) / 2, getHeight() - 4,
            new TAction() {
                public void DO() {
                    DemoCheckboxWindow.this.getApplication()
                        .closeWindow(DemoCheckboxWindow.this);
                }
            }
        );
    }

}
