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
package jexer.event;

/**
 * This class encapsulates a menu selection event.
 * TApplication.getMenuItem(id) can be used to obtain the TMenuItem itself,
 * say for setting enabled/disabled/checked/etc.
 */
public final class TMenuEvent extends TInputEvent {

    /**
     * MenuItem ID.
     */
    private int id;

    /**
     * Get the MenuItem ID.
     *
     * @return the ID
     */
    public int getId() {
        return id;
    }

    /**
     * Public contructor.
     *
     * @param id the MenuItem ID
     */
    public TMenuEvent(final int id) {
        this.id = id;
    }

    /**
     * Make human-readable description of this TMenuEvent.
     *
     * @return displayable String
     */
    @Override
    public String toString() {
        return String.format("MenuEvent: %d", id);
    }
}
