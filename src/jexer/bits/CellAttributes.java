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
 * The attributes used by a Cell: color, bold, blink, etc.
 */
public class CellAttributes {

    /**
     * Bold attribute.
     */
    private boolean bold;

    /**
     * Getter for bold.
     *
     * @return bold value
     */
    public final boolean isBold() {
        return bold;
    }

    /**
     * Setter for bold.
     *
     * @param bold new bold value
     */
    public final void setBold(final boolean bold) {
        this.bold = bold;
    }

    /**
     * Blink attribute.
     */
    private boolean blink;

    /**
     * Getter for blink.
     *
     * @return blink value
     */
    public final boolean isBlink() {
        return blink;
    }

    /**
     * Setter for blink.
     *
     * @param blink new blink value
     */
    public final void setBlink(final boolean blink) {
        this.blink = blink;
    }

    /**
     * Reverse attribute.
     */
    private boolean reverse;

    /**
     * Getter for reverse.
     *
     * @return reverse value
     */
    public final boolean isReverse() {
        return reverse;
    }

    /**
     * Setter for reverse.
     *
     * @param reverse new reverse value
     */
    public final void setReverse(final boolean reverse) {
        this.reverse = reverse;
    }

    /**
     * Underline attribute.
     */
    private boolean underline;

    /**
     * Getter for underline.
     *
     * @return underline value
     */
    public final boolean isUnderline() {
        return underline;
    }

    /**
     * Setter for underline.
     *
     * @param underline new underline value
     */
    public final void setUnderline(final boolean underline) {
        this.underline = underline;
    }

    /**
     * Protected attribute.
     */
    private boolean protect;

    /**
     * Getter for protect.
     *
     * @return protect value
     */
    public final boolean isProtect() {
        return protect;
    }

    /**
     * Setter for protect.
     *
     * @param protect new protect value
     */
    public final void setProtect(final boolean protect) {
        this.protect = protect;
    }

    /**
     * Foreground color.  Color.WHITE, Color.RED, etc.
     */
    private Color foreColor;

    /**
     * Getter for foreColor.
     *
     * @return foreColor value
     */
    public final Color getForeColor() {
        return foreColor;
    }

    /**
     * Setter for foreColor.
     *
     * @param foreColor new foreColor value
     */
    public final void setForeColor(final Color foreColor) {
        this.foreColor = foreColor;
    }

    /**
     * Background color.  Color.WHITE, Color.RED, etc.
     */
    private Color backColor;

    /**
     * Getter for backColor.
     *
     * @return backColor value
     */
    public final Color getBackColor() {
        return backColor;
    }

    /**
     * Setter for backColor.
     *
     * @param backColor new backColor value
     */
    public final void setBackColor(final Color backColor) {
        this.backColor = backColor;
    }

    /**
     * Set to default: white foreground on black background, no
     * bold/underline/blink/rever/protect.
     */
    public void reset() {
        bold      = false;
        blink     = false;
        reverse   = false;
        underline = false;
        protect   = false;
        foreColor = Color.WHITE;
        backColor = Color.BLACK;
    }

    /**
     * Public constructor sets default values of the cell to white-on-black,
     * no bold/blink/reverse/underline/protect.
     *
     * @see #reset()
     */
    public CellAttributes() {
        reset();
    }

    /**
     * Comparison check.  All fields must match to return true.
     *
     * @param rhs another CellAttributes instance
     * @return true if all fields are equal
     */
    @Override
    public boolean equals(final Object rhs) {
        if (!(rhs instanceof CellAttributes)) {
            return false;
        }

        CellAttributes that = (CellAttributes) rhs;
        return ((bold == that.bold)
            && (blink == that.blink)
            && (reverse == that.reverse)
            && (underline == that.underline)
            && (protect == that.protect)
            && (foreColor == that.foreColor)
            && (backColor == that.backColor));
    }

    /**
     * Hashcode uses all fields in equals().
     *
     * @return the hash
     */
    @Override
    public int hashCode() {
        int A = 13;
        int B = 23;
        int hash = A;
        hash = (B * hash) + (bold ? 1 : 0);
        hash = (B * hash) + (blink ? 1 : 0);
        hash = (B * hash) + (underline ? 1 : 0);
        hash = (B * hash) + (reverse ? 1 : 0);
        hash = (B * hash) + (protect ? 1 : 0);
        hash = (B * hash) + foreColor.hashCode();
        hash = (B * hash) + backColor.hashCode();
        return hash;
    }

    /**
     * Set my field values to that's field.
     *
     * @param rhs another CellAttributes instance
     */
    public void setTo(final Object rhs) {
        CellAttributes that = (CellAttributes) rhs;

        this.bold      = that.bold;
        this.blink     = that.blink;
        this.reverse   = that.reverse;
        this.underline = that.underline;
        this.protect   = that.protect;
        this.foreColor = that.foreColor;
        this.backColor = that.backColor;
    }

    /**
     * Make human-readable description of this CellAttributes.
     *
     * @return displayable String
     */
    @Override
    public String toString() {
        if (bold) {
            return String.format("bold %s on %s",
                foreColor, backColor);
        } else {
            return String.format("%s on %s", foreColor, backColor);
        }
    }

}
