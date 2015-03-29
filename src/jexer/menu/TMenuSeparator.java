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
package jexer.menu;

import jexer.bits.CellAttributes;
import jexer.bits.GraphicsChars;

/**
 * TMenuSeparator is a special case menu item.
 */
public final class TMenuSeparator extends TMenuItem {

    /**
     * Package private constructor.
     *
     * @param parent parent widget
     * @param x column relative to parent
     * @param y row relative to parent
     */
    TMenuSeparator(final TMenu parent, final int x, final int y) {
        super(parent, TMenu.MID_UNUSED, x, y, "");
        setEnabled(false);
        setActive(false);
        setWidth(parent.getWidth() - 2);
    }

    /**
     * Draw a menu separator.
     */
    @Override
    public void draw() {
        CellAttributes background = getTheme().getColor("tmenu");

        getScreen().putCharXY(0, 0, GraphicsChars.CP437[0xC3], background);
        getScreen().putCharXY(getWidth() - 1, 0, GraphicsChars.CP437[0xB4],
            background);
        getScreen().hLineXY(1, 0, getWidth() - 2, GraphicsChars.SINGLE_BAR,
            background);
    }

}
