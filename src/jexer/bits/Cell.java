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
 * A single text cell on the screen
 */
public class Cell extends CellAttributes {

    /**
     * The character at this cell
     */
    public char ch;

    /**
     * Reset this cell to a blank
     */
    @Override
    public void reset() {
	super.reset();
	ch = ' ';
    }

    /**
     * Returns true if this cell has default attributes
     */
    public boolean isBlank() {
	if ((foreColor.equals(Color.WHITE)) &&
	    (backColor.equals(Color.BLACK)) &&
	    (bold == false) &&
	    (blink == false) &&
	    (reverse == false) &&
	    (underline == false) &&
	    (protect == false) &&
	    (ch == ' ')) {
	    return true;
	}

	return false;
    }

    /**
     * Comparison.  All fields must match to return true.
     */
    @Override
    public boolean equals(Object rhs) {
	if (!(rhs instanceof Cell)) {
	    return false;
	}

	Cell that = (Cell)rhs;
	return (super.equals(rhs) &&
	    (ch == that.ch));
    }

    /**
     * Set my field values to that's field
     */
    @Override
    public void setTo(Object rhs) {
	CellAttributes thatAttr = (CellAttributes)rhs;
	super.setTo(thatAttr);

	if (rhs instanceof Cell) {
	    Cell that = (Cell)rhs;
	    this.ch = that.ch;
	}
    }

    /**
     * Set my field attr values to that's field
     */
    public void setAttr(CellAttributes that) {
	super.setTo(that);
    }

    /**
     * Public constructor
     */
    public Cell() {
	reset();
    }

    /**
     * Public constructor
     *
     * @param ch character to set to
     */
    public Cell(char ch) {
	reset();
	this.ch = ch;
    }

    /**
     * Make human-readable description of this Cell
     */
    @Override
    public String toString() {
	return String.format("fore: %d back: %d bold: %s blink: %s ch %c",
	    foreColor, backColor, bold, blink, ch);
    }
}

