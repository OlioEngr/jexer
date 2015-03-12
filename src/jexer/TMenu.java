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

import jexer.bits.MnemonicString;

/**
 * TMenu is a top-level collection of TMenuItems.
 */
public class TMenu extends TWindow {

    /**
     * If true, this is a sub-menu.
     */
    private boolean isSubMenu = false;

    /**
     * The shortcut and title.
     */
    private MnemonicString mnemonic;

    // Reserved menu item IDs
    public static final int MID_UNUSED          = -1;

    // File menu
    public static final int MID_EXIT            = 1;
    public static final int MID_QUIT            = MID_EXIT;
    public static final int MID_OPEN_FILE       = 2;
    public static final int MID_SHELL           = 3;

    // Edit menu
    public static final int MID_CUT             = 10;
    public static final int MID_COPY            = 11;
    public static final int MID_PASTE           = 12;
    public static final int MID_CLEAR           = 13;

    // Window menu
    public static final int MID_TILE            = 20;
    public static final int MID_CASCADE         = 21;
    public static final int MID_CLOSE_ALL       = 22;
    public static final int MID_WINDOW_MOVE     = 23;
    public static final int MID_WINDOW_ZOOM     = 24;
    public static final int MID_WINDOW_NEXT     = 25;
    public static final int MID_WINDOW_PREVIOUS = 26;
    public static final int MID_WINDOW_CLOSE    = 27;

    /**
     * Public constructor.
     *
     * @param parent parent application
     * @param x column relative to parent
     * @param y row relative to parent
     * @param label mnemonic menu title.  Label must contain a keyboard
     * shortcut (mnemonic), denoted by prefixing a letter with "&",
     * e.g. "&File"
     */
    public TMenu(final TApplication parent, final int x, final int y,
        final String label) {

        super(parent, label, x, y, parent.getScreen().getWidth(),
            parent.getScreen().getHeight());

        // My parent constructor added me as a window, get rid of that
        parent.closeWindow(this);

        // Setup the menu shortcut
        mnemonic = new MnemonicString(title);
        this.title = mnemonic.getRawLabel();
        assert (mnemonic.getShortcutIdx() >= 0);

        // Recompute width and height to reflect an empty menu
        width = this.title.length() + 4;
        height = 2;

        this.active = false;
    }

}
