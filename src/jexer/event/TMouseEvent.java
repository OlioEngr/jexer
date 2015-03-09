/**
 * Jexer - Java Text User Interface
 *
 * Version: $Id$
 *
 * Author: Kevin Lamonte, <a href="mailto:kevin.lamonte@gmail.com">kevin.lamonte@gmail.com</a>
 *
 * License: LGPLv3 or later
 *
 * Copyright: This module is licensed under the GNU Lesser General
 * Public License Version 3.  Please see the file "COPYING" in this
 * directory for more information about the GNU Lesser General Public
 * License Version 3.
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
 */
package jexer.event;

/**
 * This class encapsulates several kinds of mouse input events.
 */
public class TMouseEvent extends TInputEvent {

    enum Type {
	/**
	 * Mouse motion.  X and Y will have screen coordinates.
	 */
	MOUSE_MOTION,

	/**
	 * Mouse button down.  X and Y will have screen coordinates.
	 */
	MOUSE_DOWN,

	/**
	 * Mouse button up.  X and Y will have screen coordinates.
	 */
	MOUSE_UP
    }

    /**
     * Type of event, one of MOUSE_MOTION, MOUSE_UP, or MOUSE_DOWN, or
     * KEYPRESS
     */
    public Type type;

    /**
     * Mouse X - relative coordinates
     */
    public int x;

    /**
     * Mouse Y - relative coordinates
     */
    public int y;

    /**
     * Mouse X - absolute screen coordinates
     */
    public int absoluteX;

    /**
     * Mouse Y - absolute screen coordinate
     */
    public int absoluteY;

    /**
     * Mouse button 1 (left button)
     */
    public boolean mouse1;

    /**
     * Mouse button 2 (right button)
     */
    public boolean mouse2;

    /**
     * Mouse button 3 (middle button)
     */
    public boolean mouse3;

    /**
     * Mouse wheel UP (button 4)
     */
    public boolean mouseWheelUp;

    /**
     * Mouse wheel DOWN (button 5)
     */
    public boolean mouseWheelDown;

    /**
     * Public contructor
     *
     * @param type the type of event, MOUSE_MOTION, MOUSE_DOWN, or MOUSE_UP
     */
    public TMouseEvent(Type type) {
	this.type = type;
    }

    /**
     * Make human-readable description of this event
     */
    @Override
    public String toString() {
	return String.format("Mouse: %s x %d y %d absoluteX %d absoluteY %d 1 %s 2 %s 3 %s DOWN %s UP %s",
	    type,
	    x, y,
	    absoluteX, absoluteY,
	    mouse1,
	    mouse2,
	    mouse3,
	    mouseWheelUp,
	    mouseWheelDown);
    }

}
