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
 * The attributes used by a Cell: color, bold, blink, etc.
 */
public class CellAttributes {

    /**
     * Bold
     */
    public boolean bold;

    /**
     * Blink
     */
    public boolean blink;

    /**
     * Reverse
     */
    public boolean reverse;

    /**
     * Underline
     */
    public boolean underline;

    /**
     * Protected
     */
    public boolean protect;

    /**
     * Foreground color.  Color.WHITE, Color.RED, etc.
     */
    public Color foreColor;

    /**
     * Background color.  Color.WHITE, Color.RED, etc.
     */
    public Color backColor;

    /**
     * Set to default not-bold, white foreground on black background.
     */
    public void reset() {
	bold     = false;
	blink    = false;
	reverse  = false;
	protect  = false;
	underline = false;
	foreColor = Color.WHITE;
	backColor = Color.BLACK;
    }

    /**
     * Public constructor
     */
    public CellAttributes() {
	reset();
    }

    /**
     * Comparison.  All fields must match to return true.
     */
    @Override
    public boolean equals(Object rhs) {
	if (!(rhs instanceof CellAttributes)) {
	    return false;
	}

	CellAttributes that = (CellAttributes)rhs;
	return ((bold == that.bold) &&
	    (blink == that.blink) &&
	    (reverse == that.reverse) &&
	    (underline == that.underline) &&
	    (protect == that.protect) &&
	    (foreColor == that.foreColor) &&
	    (backColor == that.backColor));
    }

    /**
     * Set my field values to that's field
     */
    public void setTo(Object rhs) {
	CellAttributes that = (CellAttributes)rhs;

	this.bold      = that.bold;
	this.blink     = that.blink;
	this.reverse   = that.reverse;
	this.underline = that.underline;
	this.protect   = that.protect;
	this.foreColor = that.foreColor;
	this.backColor = that.backColor;
    }

    /**
     * Convert enum to string
     *
     * @param color Color.RED, Color.BLUE, etc.
     * @return "red", "blue", etc.
     */
    static public String stringFromColor(Color color) {
	if (color.equals(Color.BLACK)) {
	    return "black";
	} else if (color.equals(Color.WHITE)) {
	    return "white";
	} else if (color.equals(Color.RED)) {
	    return "red";
	} else if (color.equals(Color.CYAN)) {
	    return "cyan";
	} else if (color.equals(Color.GREEN)) {
	    return "green";
	} else if (color.equals(Color.MAGENTA)) {
	    return "magenta";
	} else if (color.equals(Color.BLUE)) {
	    return "blue";
	} else if (color.equals(Color.YELLOW)) {
	    return "yellow";
	}
	throw new IllegalArgumentException("Invalid Color value: " +
	    color.value);
    }

    /**
     * Convert string to enum
     *
     * @param color "red", "blue", etc.
     * @return Color.RED, Color.BLUE, etc.
     */
    static public Color colorFromString(String color) {
	switch (color.toLowerCase()) {
	case "black":
	    return Color.BLACK;
	case "white":
	    return Color.WHITE;
	case "red":
	    return Color.RED;
	case "cyan":
	    return Color.CYAN;
	case "green":
	    return Color.GREEN;
	case "magenta":
	    return Color.MAGENTA;
	case "blue":
	    return Color.BLUE;
	case "yellow":
	    return Color.YELLOW;
	case "brown":
	    return Color.YELLOW;
	default:
	    // Let unknown strings become white
	    return Color.WHITE;
	}
    }

    /**
     * Make human-readable description of this CellAttributes
     */
    @Override
    public String toString() {
	return String.format("%s%s on %s",
	    bold ? "bold " : "",
	    stringFromColor(foreColor),
	    stringFromColor(backColor));
    }

}

