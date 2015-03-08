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
package jexer.bits;

/**
 * A text cell color.
 */
public class Color {

    /**
     * The color value.  Default is WHITE.
     */
    public int value = 7;

    /**
     * Public constructor
     */
    public Color(int value) {
	this.value = value;
    }

    // The color integer values.  NOT EXPOSED.
    static private final int black   = 0;
    static private final int red     = 1;
    static private final int green   = 2;
    static private final int yellow  = 3;
    static private final int blue    = 4;
    static private final int magenta = 5;
    static private final int cyan    = 6;
    static private final int white   = 7;

    /**
     * Black.  Bold + black = dark grey
     */
    static public final Color BLACK = new Color(black);

    /**
     * Red
     */
    static public final Color RED = new Color(red);

    /**
     * Green
     */
    static public final Color GREEN  = new Color(green);

    /**
     * Yellow.  Sometimes not-bold yellow is brown.
     */
    static public final Color YELLOW = new Color(yellow);

    /**
     * Blue
     */
    static public final Color BLUE = new Color(blue);

    /**
     * Magenta (purple)
     */
    static public final Color MAGENTA = new Color(magenta);

    /**
     * Cyan (blue-green)
     */
    static public final Color CYAN = new Color(cyan);

    /**
     * White
     */
    static public final Color WHITE = new Color(white);

    /**
     * Invert a color in the same way as (CGA/VGA color XOR 0x7).
     *
     * @param color color to change
     * @return the inverted color
     */
    static public Color invert(Color color) {
	switch (color.value) {
	case black:
	    return Color.WHITE;
	case white:
	    return Color.BLACK;
	case red:
	    return Color.CYAN;
	case cyan:
	    return Color.RED;
	case green:
	    return Color.MAGENTA;
	case magenta:
	    return Color.GREEN;
	case blue:
	    return Color.YELLOW;
	case yellow:
	    return Color.BLUE;
	}
	throw new IllegalArgumentException("Invalid Color value: " +
	    color.value);
    }
}
