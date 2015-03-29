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
package jexer.bits;

/**
 * MnemonicString is used to render a string like "&File" into a highlighted
 * 'F' and the rest of 'ile'.  To insert a literal '&', use two '&&'
 * characters, e.g. "&File && Stuff" would be "File & Stuff" with the first
 * 'F' highlighted.
 */
public final class MnemonicString {

    /**
     * Keyboard shortcut to activate this item.
     */
    private char shortcut;

    /**
     * Get the keyboard shortcut character.
     *
     * @return the highlighted character
     */
    public char getShortcut() {
        return shortcut;
    }

    /**
     * Location of the highlighted character.
     */
    private int shortcutIdx = -1;

    /**
     * Get location of the highlighted character.
     *
     * @return location of the highlighted character
     */
    public int getShortcutIdx() {
        return shortcutIdx;
    }

    /**
     * The raw (uncolored) string.
     */
    private String rawLabel;

    /**
     * Get the raw (uncolored) string.
     *
     * @return the raw (uncolored) string
     */
    public String getRawLabel() {
        return rawLabel;
    }
    
    /**
     * Public constructor.
     *
     * @param label widget label or title.  Label must contain a keyboard
     * shortcut, denoted by prefixing a letter with "&", e.g. "&File"
     */
    public MnemonicString(final String label) {

        // Setup the menu shortcut
        String newLabel = "";
        boolean foundAmp = false;
        boolean foundShortcut = false;
        int scanShortcutIdx = 0;
        for (int i = 0; i < label.length(); i++) {
            char c = label.charAt(i);
            if (c == '&') {
                if (foundAmp) {
                    newLabel += '&';
                    scanShortcutIdx++;
                } else {
                    foundAmp = true;
                }
            } else {
                newLabel += c;
                if (foundAmp) {
                    if (!foundShortcut) {
                        shortcut = c;
                        foundAmp = false;
                        foundShortcut = true;
                        shortcutIdx = scanShortcutIdx;
                    }
                } else {
                    scanShortcutIdx++;
                }
            }
        }
        this.rawLabel = newLabel;
    }
}
