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
package jexer.tterminal;

import jexer.bits.Cell;
import jexer.bits.CellAttributes;

/**
 * This represents a single line of the display buffer.
 */
public final class DisplayLine {
    /**
     * Maximum line length.
     */
    private static final int MAX_LINE_LENGTH = 256;

    /**
     * The characters/attributes of the line.
     */
    private Cell [] chars;

    /**
     * Get the Cell at a specific column.
     *
     * @param idx the character index
     * @return the Cell
     */
    public Cell charAt(final int idx) {
        return chars[idx];
    }

    /**
     * Get the length of this line.
     *
     * @return line length
     */
    public int length() {
        return chars.length;
    }

    /**
     * Double-width line flag.
     */
    private boolean doubleWidth = false;

    /**
     * Get double width flag.
     *
     * @return double width
     */
    public boolean isDoubleWidth() {
        return doubleWidth;
    }

    /**
     * Set double width flag.
     *
     * @param doubleWidth new value for double width flag
     */
    public void setDoubleWidth(final boolean doubleWidth) {
        this.doubleWidth = doubleWidth;
    }

    /**
     * Double height line flag.  Valid values are:
     *
     * <p><pre>
     *   0 = single height
     *   1 = top half double height
     *   2 = bottom half double height
     * </pre>
     */
    private int doubleHeight = 0;

    /**
     * Get double height flag.
     *
     * @return double height
     */
    public int getDoubleHeight() {
        return doubleHeight;
    }

    /**
     * Set double height flag.
     *
     * @param doubleHeight new value for double height flag
     */
    public void setDoubleHeight(final int doubleHeight) {
        this.doubleHeight = doubleHeight;
    }

    /**
     * DECSCNM - reverse video.  We copy the flag to the line so that
     * reverse-mode scrollback lines still show inverted colors correctly.
     */
    private boolean reverseColor = false;

    /**
     * Get reverse video flag.
     *
     * @return reverse video
     */
    public boolean isReverseColor() {
        return reverseColor;
    }

    /**
     * Set double-height flag.
     *
     * @param reverseColor new value for reverse video flag
     */
    public void setReverseColor(final boolean reverseColor) {
        this.reverseColor = reverseColor;
    }

    /**
     * Public constructor sets everything to drawing attributes.
     *
     * @param attr current drawing attributes
     */
    public DisplayLine(final CellAttributes attr) {
        chars = new Cell[MAX_LINE_LENGTH];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = new Cell();
            chars[i].setTo(attr);
        }
    }

    /**
     * Insert a character at the specified position.
     *
     * @param idx the character index
     * @param newCell the new Cell
     */
    public void insert(final int idx, final Cell newCell) {
        System.arraycopy(chars, idx, chars, idx + 1, chars.length - idx - 1);
        chars[idx] = newCell;
    }

    /**
     * Replace character at the specified position.
     *
     * @param idx the character index
     * @param newCell the new Cell
     */
    public void replace(final int idx, final Cell newCell) {
        chars[idx].setTo(newCell);
    }

    /**
     * Set the Cell at the specified position to the blank (reset).
     *
     * @param idx the character index
     */
    public void setBlank(final int idx) {
        chars[idx].reset();
    }

    /**
     * Set the character (just the char, not the attributes) at the specified
     * position to ch.
     *
     * @param idx the character index
     * @param ch the new char
     */
    public void setChar(final int idx, final char ch) {
        chars[idx].setChar(ch);
    }

    /**
     * Set the attributes (just the attributes, not the char) at the
     * specified position to attr.
     *
     * @param idx the character index
     * @param attr the new attributes
     */
    public void setAttr(final int idx, final CellAttributes attr) {
        chars[idx].setAttr(attr);
    }

    /**
     * Delete character at the specified position, filling in the new
     * character on the right with newCell.
     *
     * @param idx the character index
     * @param newCell the new Cell
     */
    public void delete(final int idx, final Cell newCell) {
        System.arraycopy(chars, idx + 1, chars, idx, chars.length - idx - 1);
        chars[chars.length - 1] = newCell;
    }

}
