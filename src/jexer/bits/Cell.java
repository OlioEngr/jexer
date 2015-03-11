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
package jexer.bits;

/**
 * This class represents a single text cell on the screen.
 */
public class Cell extends CellAttributes {

    /**
     * The character at this cell.
     */
    private char ch;

    /**
     * Getter for cell character.
     *
     * @return cell character
     */
    public final char getChar() {
        return ch;
    }

    /**
     * Setter for cell character.
     *
     * @param ch new cell character
     */
    public final void setChar(final char ch) {
        this.ch = ch;
    }

    /**
     * Reset this cell to a blank.
     */
    @Override
    public final void reset() {
        super.reset();
        ch = ' ';
    }

    /**
     * Check to see if this cell has default attributes: white foreground,
     * black background, no bold/blink/reverse/underline/protect, and a
     * character value of ' ' (space).
     *
     * @return true if this cell has default attributes.
     */
    public final boolean isBlank() {
        if ((getForeColor().equals(Color.WHITE))
            && (getBackColor().equals(Color.BLACK))
            && !getBold()
            && !getBlink()
            && !getReverse()
            && !getUnderline()
            && !getProtect()
            && (ch == ' ')
        ) {
            return true;
        }

        return false;
    }

    /**
     * Comparison check.  All fields must match to return true.
     *
     * @param rhs another Cell instance
     * @return true if all fields are equal
     */
    @Override
    public final boolean equals(final Object rhs) {
        if (!(rhs instanceof Cell)) {
            return false;
        }

        Cell that = (Cell) rhs;
        return (super.equals(rhs)
            && (ch == that.ch));
    }

    /**
     * Set my field values to that's field.
     *
     * @param rhs an instance of either Cell or CellAttributes
     */
    @Override
    public final void setTo(final Object rhs) {
        // Let this throw a ClassCastException
        CellAttributes thatAttr = (CellAttributes) rhs;
        super.setTo(thatAttr);

        if (rhs instanceof Cell) {
            Cell that = (Cell) rhs;
            this.ch = that.ch;
        }
    }

    /**
     * Set my field attr values to that's field.
     *
     * @param that a CellAttributes instance
     */
    public final void setAttr(final CellAttributes that) {
        super.setTo(that);
    }

    /**
     * Public constructor sets default values of the cell to blank.
     *
     * @see #isBlank()
     * @see #reset()
     */
    public Cell() {
        reset();
    }

    /**
     * Public constructor sets the character.  Attributes are the same as
     * default.
     *
     * @param ch character to set to
     * @see #reset()
     */
    public Cell(final char ch) {
        reset();
        this.ch = ch;
    }

    /**
     * Make human-readable description of this Cell.
     *
     * @return displayable String
     */
    @Override
    public final String toString() {
        return String.format("fore: %d back: %d bold: %s blink: %s ch %c",
            getForeColor(), getBackColor(), getBold(), getBlink(), ch);
    }
}
