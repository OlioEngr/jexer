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
package jexer.event;

import jexer.TKeypress;

/**
 * This class encapsulates a keyboard input event.
 */
public final class TKeypressEvent extends TInputEvent {

    /**
     * Keystroke received.
     */
    private TKeypress key;

    /**
     * Get keystroke.
     *
     * @return keystroke
     */
    public TKeypress getKey() {
        return key;
    }

    /**
     * Public contructor.
     *
     * @param key the TKeypress received
     */
    public TKeypressEvent(final TKeypress key) {
        this.key = key;
    }

    /**
     * Public constructor.
     *
     * @param isKey is true, this is a function key
     * @param fnKey the function key code (only valid if isKey is true)
     * @param ch the character (only valid if fnKey is false)
     * @param alt if true, ALT was pressed with this keystroke
     * @param ctrl if true, CTRL was pressed with this keystroke
     * @param shift if true, SHIFT was pressed with this keystroke
     */
    public TKeypressEvent(final boolean isKey, final int fnKey, final char ch,
        final boolean alt, final boolean ctrl, final boolean shift) {

        this.key = new TKeypress(isKey, fnKey, ch, alt, ctrl, shift);
    }

    /**
     * Public constructor.
     *
     * @param key the TKeypress received
     * @param alt if true, ALT was pressed with this keystroke
     * @param ctrl if true, CTRL was pressed with this keystroke
     * @param shift if true, SHIFT was pressed with this keystroke
     */
    public TKeypressEvent(final TKeypress key,
        final boolean alt, final boolean ctrl, final boolean shift) {

        this.key = new TKeypress(key.isFnKey(), key.getKeyCode(), key.getChar(),
            alt, ctrl, shift);
    }

    /**
     * Comparison check.  All fields must match to return true.
     *
     * @param rhs another TKeypressEvent or TKeypress instance
     * @return true if all fields are equal
     */
    @Override
    public boolean equals(final Object rhs) {
        if (!(rhs instanceof TKeypressEvent)
            && !(rhs instanceof TKeypress)
        ) {
            return false;
        }

        if (rhs instanceof TKeypressEvent) {
            TKeypressEvent that = (TKeypressEvent) rhs;
            return (key.equals(that.key)
                && (getTime().equals(that.getTime())));
        }

        TKeypress that = (TKeypress) rhs;
        return (key.equals(that));
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
        hash = (B * hash) + getTime().hashCode();
        hash = (B * hash) + key.hashCode();
        return hash;
    }

    /**
     * Make human-readable description of this TKeypressEvent.
     *
     * @return displayable String
     */
    @Override
    public String toString() {
        return String.format("Keypress: %s", key.toString());
    }
}
