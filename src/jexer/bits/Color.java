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
 * A text cell color.
 */
public final class Color {

    /**
     * The color value.  Default is SGRWHITE.
     */
    private int value = SGRWHITE;

    /**
     * Get color value.  Note that these deliberately match the color values
     * of the ECMA-48 / ANSI X3.64 / VT100-ish SGR function ("ANSI colors").
     *
     * @return the value
     */
    public int getValue() {
        return value;
    }

    /**
     * Private constructor used to make the static Color instances.
     *
     * @param value the integer Color value
     */
    private Color(final int value) {
        this.value = value;
    }

    /**
     * Public constructor returns one of the static Color instances.
     *
     * @param colorName "red", "blue", etc.
     * @return Color.RED, Color.BLUE, etc.
     */
    static Color getColor(final String colorName) {
        switch (colorName.toLowerCase()) {
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
     * SGR black value = 0.
     */
    private static final int SGRBLACK   = 0;

    /**
     * SGR red value = 1.
     */
    private static final int SGRRED     = 1;

    /**
     * SGR green value = 2.
     */
    private static final int SGRGREEN   = 2;

    /**
     * SGR yellow value = 3.
     */
    private static final int SGRYELLOW  = 3;

    /**
     * SGR blue value = 4.
     */
    private static final int SGRBLUE    = 4;

    /**
     * SGR magenta value = 5.
     */
    private static final int SGRMAGENTA = 5;

    /**
     * SGR cyan value = 6.
     */
    private static final int SGRCYAN    = 6;

    /**
     * SGR white value = 7.
     */
    private static final int SGRWHITE   = 7;

    /**
     * Black.  Bold + black = dark grey
     */
    public static final Color BLACK = new Color(SGRBLACK);

    /**
     * Red.
     */
    public static final Color RED = new Color(SGRRED);

    /**
     * Green.
     */
    public static final Color GREEN  = new Color(SGRGREEN);

    /**
     * Yellow.  Sometimes not-bold yellow is brown.
     */
    public static final Color YELLOW = new Color(SGRYELLOW);

    /**
     * Blue.
     */
    public static final Color BLUE = new Color(SGRBLUE);

    /**
     * Magenta (purple).
     */
    public static final Color MAGENTA = new Color(SGRMAGENTA);

    /**
     * Cyan (blue-green).
     */
    public static final Color CYAN = new Color(SGRCYAN);

    /**
     * White.
     */
    public static final Color WHITE = new Color(SGRWHITE);

    /**
     * Invert a color in the same way as (CGA/VGA color XOR 0x7).
     *
     * @return the inverted color
     */
    public Color invert() {
        switch (value) {
        case SGRBLACK:
            return Color.WHITE;
        case SGRWHITE:
            return Color.BLACK;
        case SGRRED:
            return Color.CYAN;
        case SGRCYAN:
            return Color.RED;
        case SGRGREEN:
            return Color.MAGENTA;
        case SGRMAGENTA:
            return Color.GREEN;
        case SGRBLUE:
            return Color.YELLOW;
        case SGRYELLOW:
            return Color.BLUE;
        default:
            throw new IllegalArgumentException("Invalid Color value: " + value);
        }
    }

    /**
     * Comparison check.  All fields must match to return true.
     *
     * @param rhs another Color instance
     * @return true if all fields are equal
     */
    @Override
    public boolean equals(final Object rhs) {
        if (!(rhs instanceof Color)) {
            return false;
        }

        Color that = (Color) rhs;
        return (value == that.value);
    }

    /**
     * Make human-readable description of this Color.
     *
     * @return displayable String "red", "blue", etc.
     */
    @Override
    public String toString() {
        switch (value) {
        case SGRBLACK:
            return "black";
        case SGRWHITE:
            return "white";
        case SGRRED:
            return "red";
        case SGRCYAN:
            return "cyan";
        case SGRGREEN:
            return "green";
        case SGRMAGENTA:
            return "magenta";
        case SGRBLUE:
            return "blue";
        case SGRYELLOW:
            return "yellow";
        }
        throw new IllegalArgumentException("Invalid Color value: " + value);
    }

}