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
package jexer;

/**
 * This class represents keystrokes.
 */
public final class TKeypress {

    // Various special keystrokes

    /**
     * "No key".
     */
    public static final int NONE        = 255;

    /**
     * Function key F1.
     */
    public static final int F1          = 1;

    /**
     * Function key F2.
     */
    public static final int F2          = 2;

    /**
     * Function key F3.
     */
    public static final int F3          = 3;

    /**
     * Function key F4.
     */
    public static final int F4          = 4;

    /**
     * Function key F5.
     */
    public static final int F5          = 5;

    /**
     * Function key F6.
     */
    public static final int F6          = 6;

    /**
     * Function key F7.
     */
    public static final int F7          = 7;

    /**
     * Function key F8.
     */
    public static final int F8          = 8;

    /**
     * Function key F9.
     */
    public static final int F9          = 9;

    /**
     * Function key F10.
     */
    public static final int F10         = 10;

    /**
     * Function key F11.
     */
    public static final int F11         = 11;

    /**
     * Function key F12.
     */
    public static final int F12         = 12;

    /**
     * Home.
     */
    public static final int HOME        = 20;

    /**
     * End.
     */
    public static final int END         = 21;

    /**
     * Page up.
     */
    public static final int PGUP        = 22;

    /**
     * Page down.
     */
    public static final int PGDN        = 23;

    /**
     * Insert.
     */
    public static final int INS         = 24;

    /**
     * Delete.
     */
    public static final int DEL         = 25;

    /**
     * Right arrow.
     */
    public static final int RIGHT       = 30;

    /**
     * Left arrow.
     */
    public static final int LEFT        = 31;

    /**
     * Up arrow.
     */
    public static final int UP          = 32;

    /**
     * Down arrow.
     */
    public static final int DOWN        = 33;

    /**
     * Tab.
     */
    public static final int TAB         = 40;

    /**
     * Back-tab (shift-tab).
     */
    public static final int BTAB        = 41;

    /**
     * Enter.
     */
    public static final int ENTER       = 42;

    /**
     * Escape.
     */
    public static final int ESC         = 43;

    /**
     * If true, ch is meaningless, use keyCode instead.
     */
    private boolean isFunctionKey;

    /**
     * Getter for isFunctionKey.
     *
     * @return if true, ch is meaningless, use keyCode instead
     */
    public boolean isFnKey() {
        return isFunctionKey;
    }

    /**
     * Will be set to F1, F2, HOME, END, etc. if isKey is true.
     */
    private int keyCode;

    /**
     * Getter for function key code.
     *
     * @return function key code int value (only valid is isKey is true)
     */
    public int getKeyCode() {
        return keyCode;
    }

    /**
     * Keystroke modifier ALT.
     */
    private boolean alt;

    /**
     * Getter for ALT.
     *
     * @return alt value
     */
    public boolean isAlt() {
        return alt;
    }

    /**
     * Keystroke modifier CTRL.
     */
    private boolean ctrl;

    /**
     * Getter for CTRL.
     *
     * @return ctrl value
     */
    public boolean isCtrl() {
        return ctrl;
    }

    /**
     * Keystroke modifier SHIFT.
     */
    private boolean shift;

    /**
     * Getter for SHIFT.
     *
     * @return shift value
     */
    public boolean isShift() {
        return shift;
    }

    /**
     * The character received.
     */
    private char ch;

    /**
     * Getter for character.
     *
     * @return the character (only valid if isKey is false)
     */
    public char getChar() {
        return ch;
    }

    /**
     * Public constructor makes an immutable instance.
     *
     * @param isKey is true, this is a function key
     * @param fnKey the function key code (only valid if isKey is true)
     * @param ch the character (only valid if fnKey is false)
     * @param alt if true, ALT was pressed with this keystroke
     * @param ctrl if true, CTRL was pressed with this keystroke
     * @param shift if true, SHIFT was pressed with this keystroke
     */
    public TKeypress(final boolean isKey, final int fnKey, final char ch,
            final boolean alt, final boolean ctrl, final boolean shift) {

        this.isFunctionKey = isKey;
        this.keyCode       = fnKey;
        this.ch            = ch;
        this.alt           = alt;
        this.ctrl          = ctrl;
        this.shift         = shift;
    }

    /**
     * Comparison check.  All fields must match to return true.
     *
     * @param rhs another TKeypress instance
     * @return true if all fields are equal
     */
    @Override
    public boolean equals(final Object rhs) {
        if (!(rhs instanceof TKeypress)) {
            return false;
        }

        TKeypress that = (TKeypress) rhs;
        return ((isFunctionKey == that.isFunctionKey)
                && (keyCode == that.keyCode)
                && (ch == that.ch)
                && (alt == that.alt)
                && (ctrl == that.ctrl)
                && (shift == that.shift));
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
        hash = (B * hash) + (isFunctionKey ? 1 : 0);
        hash = (B * hash) + keyCode;
        hash = (B * hash) + ch;
        hash = (B * hash) + (alt ? 1 : 0);
        hash = (B * hash) + (ctrl ? 1 : 0);
        hash = (B * hash) + (shift ? 1 : 0);
        return hash;
    }

    /**
     * Make human-readable description of this TKeypress.
     *
     * @return displayable String
     */
    @Override
    public String toString() {
        if (isFunctionKey) {
            switch (keyCode) {
            case F1:
                return String.format("%s%s%sF1",
                        ctrl ? "Ctrl+" : "",
                                alt ? "Alt+" : "",
                                        shift ? "Shift+" : "");
            case F2:
                return String.format("%s%s%sF2",
                        ctrl ? "Ctrl+" : "",
                                alt ? "Alt+" : "",
                                        shift ? "Shift+" : "");
            case F3:
                return String.format("%s%s%sF3",
                        ctrl ? "Ctrl+" : "",
                                alt ? "Alt+" : "",
                                        shift ? "Shift+" : "");
            case F4:
                return String.format("%s%s%sF4",
                        ctrl ? "Ctrl+" : "",
                                alt ? "Alt+" : "",
                                        shift ? "Shift+" : "");
            case F5:
                return String.format("%s%s%sF5",
                        ctrl ? "Ctrl+" : "",
                                alt ? "Alt+" : "",
                                        shift ? "Shift+" : "");
            case F6:
                return String.format("%s%s%sF6",
                        ctrl ? "Ctrl+" : "",
                                alt ? "Alt+" : "",
                                        shift ? "Shift+" : "");
            case F7:
                return String.format("%s%s%sF7",
                        ctrl ? "Ctrl+" : "",
                                alt ? "Alt+" : "",
                                        shift ? "Shift+" : "");
            case F8:
                return String.format("%s%s%sF8",
                        ctrl ? "Ctrl+" : "",
                                alt ? "Alt+" : "",
                                        shift ? "Shift+" : "");
            case F9:
                return String.format("%s%s%sF9",
                        ctrl ? "Ctrl+" : "",
                                alt ? "Alt+" : "",
                                        shift ? "Shift+" : "");
            case F10:
                return String.format("%s%s%sF10",
                        ctrl ? "Ctrl+" : "",
                                alt ? "Alt+" : "",
                                        shift ? "Shift+" : "");
            case F11:
                return String.format("%s%s%sF11",
                        ctrl ? "Ctrl+" : "",
                                alt ? "Alt+" : "",
                                        shift ? "Shift+" : "");
            case F12:
                return String.format("%s%s%sF12",
                        ctrl ? "Ctrl+" : "",
                                alt ? "Alt+" : "",
                                        shift ? "Shift+" : "");
            case HOME:
                return String.format("%s%s%sHOME",
                        ctrl ? "Ctrl+" : "",
                                alt ? "Alt+" : "",
                                        shift ? "Shift+" : "");
            case END:
                return String.format("%s%s%sEND",
                        ctrl ? "Ctrl+" : "",
                                alt ? "Alt+" : "",
                                        shift ? "Shift+" : "");
            case PGUP:
                return String.format("%s%s%sPGUP",
                        ctrl ? "Ctrl+" : "",
                                alt ? "Alt+" : "",
                                        shift ? "Shift+" : "");
            case PGDN:
                return String.format("%s%s%sPGDN",
                        ctrl ? "Ctrl+" : "",
                                alt ? "Alt+" : "",
                                        shift ? "Shift+" : "");
            case INS:
                return String.format("%s%s%sINS",
                        ctrl ? "Ctrl+" : "",
                                alt ? "Alt+" : "",
                                        shift ? "Shift+" : "");
            case DEL:
                return String.format("%s%s%sDEL",
                        ctrl ? "Ctrl+" : "",
                                alt ? "Alt+" : "",
                                        shift ? "Shift+" : "");
            case RIGHT:
                return String.format("%s%s%sRIGHT",
                        ctrl ? "Ctrl+" : "",
                                alt ? "Alt+" : "",
                                        shift ? "Shift+" : "");
            case LEFT:
                return String.format("%s%s%sLEFT",
                        ctrl ? "Ctrl+" : "",
                                alt ? "Alt+" : "",
                                        shift ? "Shift+" : "");
            case UP:
                return String.format("%s%s%sUP",
                        ctrl ? "Ctrl+" : "",
                                alt ? "Alt+" : "",
                                        shift ? "Shift+" : "");
            case DOWN:
                return String.format("%s%s%sDOWN",
                        ctrl ? "Ctrl+" : "",
                                alt ? "Alt+" : "",
                                        shift ? "Shift+" : "");
            case TAB:
                return String.format("%s%s%sTAB",
                        ctrl ? "Ctrl+" : "",
                                alt ? "Alt+" : "",
                                        shift ? "Shift+" : "");
            case BTAB:
                return String.format("%s%s%sBTAB",
                        ctrl ? "Ctrl+" : "",
                                alt ? "Alt+" : "",
                                        shift ? "Shift+" : "");
            case ENTER:
                return String.format("%s%s%sENTER",
                        ctrl ? "Ctrl+" : "",
                                alt ? "Alt+" : "",
                                        shift ? "Shift+" : "");
            case ESC:
                return String.format("%s%s%sESC",
                        ctrl ? "Ctrl+" : "",
                                alt ? "Alt+" : "",
                                        shift ? "Shift+" : "");
            default:
                return String.format("--UNKNOWN--");
            }
        } else {
            if (alt && !shift && !ctrl) {
                // Alt-X
                return String.format("Alt+%c", Character.toUpperCase(ch));
            } else if (!alt && shift && !ctrl) {
                // Shift-X
                return String.format("%c", ch);
            } else if (!alt && !shift && ctrl) {
                // Ctrl-X
                return String.format("Ctrl+%c", ch);
            } else if (alt && shift && !ctrl) {
                // Alt-Shift-X
                return String.format("Alt+Shift+%c", ch);
            } else if (!alt && shift && ctrl) {
                // Ctrl-Shift-X
                return String.format("Ctrl+Shift+%c", ch);
            } else if (alt && !shift && ctrl) {
                // Ctrl-Alt-X
                return String.format("Ctrl+Alt+%c", Character.toUpperCase(ch));
            } else if (alt && shift && ctrl) {
                // Ctrl-Alt-Shift-X
                return String.format("Ctrl+Alt+Shift+%c",
                        Character.toUpperCase(ch));
            } else {
                // X
                return String.format("%c", ch);
            }
        }
    }

    /**
     * Convert a keypress to lowercase.  Function keys and alt/ctrl keys are
     * not converted.
     *
     * @return a new instance with the key converted
     */
    public TKeypress toLowerCase() {
        TKeypress newKey = new TKeypress(isFunctionKey, keyCode, ch, alt, ctrl,
                shift);
        if (!isFunctionKey && (ch >= 'A') && (ch <= 'Z') && !ctrl && !alt) {
            newKey.shift = false;
            newKey.ch += 32;
        }
        return newKey;
    }

    /**
     * Convert a keypress to uppercase.  Function keys and alt/ctrl keys are
     * not converted.
     *
     * @return a new instance with the key converted
     */
    public TKeypress toUpperCase() {
        TKeypress newKey = new TKeypress(isFunctionKey, keyCode, ch, alt, ctrl,
                shift);
        if (!isFunctionKey && (ch >= 'a') && (ch <= 'z') && !ctrl && !alt) {
            newKey.shift = true;
            newKey.ch -= 32;
        }
        return newKey;
    }

    // Special "no-key" keypress, used to ignore undefined keystrokes
    public static final TKeypress kbNoKey = new TKeypress(true,
            TKeypress.NONE, ' ', false, false, false);

    // Normal keys
    public static final TKeypress kbF1 = new TKeypress(true,
            TKeypress.F1, ' ', false, false, false);
    public static final TKeypress kbF2 = new TKeypress(true,
            TKeypress.F2, ' ', false, false, false);
    public static final TKeypress kbF3 = new TKeypress(true,
            TKeypress.F3, ' ', false, false, false);
    public static final TKeypress kbF4 = new TKeypress(true,
            TKeypress.F4, ' ', false, false, false);
    public static final TKeypress kbF5 = new TKeypress(true,
            TKeypress.F5, ' ', false, false, false);
    public static final TKeypress kbF6 = new TKeypress(true,
            TKeypress.F6, ' ', false, false, false);
    public static final TKeypress kbF7 = new TKeypress(true,
            TKeypress.F7, ' ', false, false, false);
    public static final TKeypress kbF8 = new TKeypress(true,
            TKeypress.F8, ' ', false, false, false);
    public static final TKeypress kbF9 = new TKeypress(true,
            TKeypress.F9, ' ', false, false, false);
    public static final TKeypress kbF10 = new TKeypress(true,
            TKeypress.F10, ' ', false, false, false);
    public static final TKeypress kbF11 = new TKeypress(true,
            TKeypress.F11, ' ', false, false, false);
    public static final TKeypress kbF12 = new TKeypress(true,
            TKeypress.F12, ' ', false, false, false);
    public static final TKeypress kbAltF1 = new TKeypress(true,
            TKeypress.F1, ' ', true, false, false);
    public static final TKeypress kbAltF2 = new TKeypress(true,
            TKeypress.F2, ' ', true, false, false);
    public static final TKeypress kbAltF3 = new TKeypress(true,
            TKeypress.F3, ' ', true, false, false);
    public static final TKeypress kbAltF4 = new TKeypress(true,
            TKeypress.F4, ' ', true, false, false);
    public static final TKeypress kbAltF5 = new TKeypress(true,
            TKeypress.F5, ' ', true, false, false);
    public static final TKeypress kbAltF6 = new TKeypress(true,
            TKeypress.F6, ' ', true, false, false);
    public static final TKeypress kbAltF7 = new TKeypress(true,
            TKeypress.F7, ' ', true, false, false);
    public static final TKeypress kbAltF8 = new TKeypress(true,
            TKeypress.F8, ' ', true, false, false);
    public static final TKeypress kbAltF9 = new TKeypress(true,
            TKeypress.F9, ' ', true, false, false);
    public static final TKeypress kbAltF10 = new TKeypress(true,
            TKeypress.F10, ' ', true, false, false);
    public static final TKeypress kbAltF11 = new TKeypress(true,
            TKeypress.F11, ' ', true, false, false);
    public static final TKeypress kbAltF12 = new TKeypress(true,
            TKeypress.F12, ' ', true, false, false);
    public static final TKeypress kbCtrlF1 = new TKeypress(true,
            TKeypress.F1, ' ', false, true, false);
    public static final TKeypress kbCtrlF2 = new TKeypress(true,
            TKeypress.F2, ' ', false, true, false);
    public static final TKeypress kbCtrlF3 = new TKeypress(true,
            TKeypress.F3, ' ', false, true, false);
    public static final TKeypress kbCtrlF4 = new TKeypress(true,
            TKeypress.F4, ' ', false, true, false);
    public static final TKeypress kbCtrlF5 = new TKeypress(true,
            TKeypress.F5, ' ', false, true, false);
    public static final TKeypress kbCtrlF6 = new TKeypress(true,
            TKeypress.F6, ' ', false, true, false);
    public static final TKeypress kbCtrlF7 = new TKeypress(true,
            TKeypress.F7, ' ', false, true, false);
    public static final TKeypress kbCtrlF8 = new TKeypress(true,
            TKeypress.F8, ' ', false, true, false);
    public static final TKeypress kbCtrlF9 = new TKeypress(true,
            TKeypress.F9, ' ', false, true, false);
    public static final TKeypress kbCtrlF10 = new TKeypress(true,
            TKeypress.F10, ' ', false, true, false);
    public static final TKeypress kbCtrlF11 = new TKeypress(true,
            TKeypress.F11, ' ', false, true, false);
    public static final TKeypress kbCtrlF12 = new TKeypress(true,
            TKeypress.F12, ' ', false, true, false);
    public static final TKeypress kbShiftF1 = new TKeypress(true,
            TKeypress.F1, ' ', false, false, true);
    public static final TKeypress kbShiftF2 = new TKeypress(true,
            TKeypress.F2, ' ', false, false, true);
    public static final TKeypress kbShiftF3 = new TKeypress(true,
            TKeypress.F3, ' ', false, false, true);
    public static final TKeypress kbShiftF4 = new TKeypress(true,
            TKeypress.F4, ' ', false, false, true);
    public static final TKeypress kbShiftF5 = new TKeypress(true,
            TKeypress.F5, ' ', false, false, true);
    public static final TKeypress kbShiftF6 = new TKeypress(true,
            TKeypress.F6, ' ', false, false, true);
    public static final TKeypress kbShiftF7 = new TKeypress(true,
            TKeypress.F7, ' ', false, false, true);
    public static final TKeypress kbShiftF8 = new TKeypress(true,
            TKeypress.F8, ' ', false, false, true);
    public static final TKeypress kbShiftF9 = new TKeypress(true,
            TKeypress.F9, ' ', false, false, true);
    public static final TKeypress kbShiftF10 = new TKeypress(true,
            TKeypress.F10, ' ', false, false, true);
    public static final TKeypress kbShiftF11 = new TKeypress(true,
            TKeypress.F11, ' ', false, false, true);
    public static final TKeypress kbShiftF12 = new TKeypress(true,
            TKeypress.F12, ' ', false, false, true);
    public static final TKeypress kbEnter = new TKeypress(true,
            TKeypress.ENTER, ' ', false, false, false);
    public static final TKeypress kbTab = new TKeypress(true,
            TKeypress.TAB, ' ', false, false, false);
    public static final TKeypress kbEsc = new TKeypress(true,
            TKeypress.ESC, ' ', false, false, false);
    public static final TKeypress kbHome = new TKeypress(true,
            TKeypress.HOME, ' ', false, false, false);
    public static final TKeypress kbEnd = new TKeypress(true,
            TKeypress.END, ' ', false, false, false);
    public static final TKeypress kbPgUp = new TKeypress(true,
            TKeypress.PGUP, ' ', false, false, false);
    public static final TKeypress kbPgDn = new TKeypress(true,
            TKeypress.PGDN, ' ', false, false, false);
    public static final TKeypress kbIns = new TKeypress(true,
            TKeypress.INS, ' ', false, false, false);
    public static final TKeypress kbDel = new TKeypress(true,
            TKeypress.DEL, ' ', false, false, false);
    public static final TKeypress kbUp = new TKeypress(true,
            TKeypress.UP, ' ', false, false, false);
    public static final TKeypress kbDown = new TKeypress(true,
            TKeypress.DOWN, ' ', false, false, false);
    public static final TKeypress kbLeft = new TKeypress(true,
            TKeypress.LEFT, ' ', false, false, false);
    public static final TKeypress kbRight = new TKeypress(true,
            TKeypress.RIGHT, ' ', false, false, false);
    public static final TKeypress kbAltEnter = new TKeypress(true,
            TKeypress.ENTER, ' ', true, false, false);
    public static final TKeypress kbAltTab = new TKeypress(true,
            TKeypress.TAB, ' ', true, false, false);
    public static final TKeypress kbAltEsc = new TKeypress(true,
            TKeypress.ESC, ' ', true, false, false);
    public static final TKeypress kbAltHome = new TKeypress(true,
            TKeypress.HOME, ' ', true, false, false);
    public static final TKeypress kbAltEnd = new TKeypress(true,
            TKeypress.END, ' ', true, false, false);
    public static final TKeypress kbAltPgUp = new TKeypress(true,
            TKeypress.PGUP, ' ', true, false, false);
    public static final TKeypress kbAltPgDn = new TKeypress(true,
            TKeypress.PGDN, ' ', true, false, false);
    public static final TKeypress kbAltIns = new TKeypress(true,
            TKeypress.INS, ' ', true, false, false);
    public static final TKeypress kbAltDel = new TKeypress(true,
            TKeypress.DEL, ' ', true, false, false);
    public static final TKeypress kbAltUp = new TKeypress(true,
            TKeypress.UP, ' ', true, false, false);
    public static final TKeypress kbAltDown = new TKeypress(true,
            TKeypress.DOWN, ' ', true, false, false);
    public static final TKeypress kbAltLeft = new TKeypress(true,
            TKeypress.LEFT, ' ', true, false, false);
    public static final TKeypress kbAltRight = new TKeypress(true,
            TKeypress.RIGHT, ' ', true, false, false);
    public static final TKeypress kbCtrlEnter = new TKeypress(true,
            TKeypress.ENTER, ' ', false, true, false);
    public static final TKeypress kbCtrlTab = new TKeypress(true,
            TKeypress.TAB, ' ', false, true, false);
    public static final TKeypress kbCtrlEsc = new TKeypress(true,
            TKeypress.ESC, ' ', false, true, false);
    public static final TKeypress kbCtrlHome = new TKeypress(true,
            TKeypress.HOME, ' ', false, true, false);
    public static final TKeypress kbCtrlEnd = new TKeypress(true,
            TKeypress.END, ' ', false, true, false);
    public static final TKeypress kbCtrlPgUp = new TKeypress(true,
            TKeypress.PGUP, ' ', false, true, false);
    public static final TKeypress kbCtrlPgDn = new TKeypress(true,
            TKeypress.PGDN, ' ', false, true, false);
    public static final TKeypress kbCtrlIns = new TKeypress(true,
            TKeypress.INS, ' ', false, true, false);
    public static final TKeypress kbCtrlDel = new TKeypress(true,
            TKeypress.DEL, ' ', false, true, false);
    public static final TKeypress kbCtrlUp = new TKeypress(true,
            TKeypress.UP, ' ', false, true, false);
    public static final TKeypress kbCtrlDown = new TKeypress(true,
            TKeypress.DOWN, ' ', false, true, false);
    public static final TKeypress kbCtrlLeft = new TKeypress(true,
            TKeypress.LEFT, ' ', false, true, false);
    public static final TKeypress kbCtrlRight = new TKeypress(true,
            TKeypress.RIGHT, ' ', false, true, false);
    public static final TKeypress kbShiftEnter = new TKeypress(true,
            TKeypress.ENTER, ' ', false, false, true);
    public static final TKeypress kbShiftTab = new TKeypress(true,
            TKeypress.TAB, ' ', false, false, true);
    public static final TKeypress kbBackTab = new TKeypress(true,
            TKeypress.BTAB, ' ', false, false, false);
    public static final TKeypress kbShiftEsc = new TKeypress(true,
            TKeypress.ESC, ' ', false, false, true);
    public static final TKeypress kbShiftHome = new TKeypress(true,
            TKeypress.HOME, ' ', false, false, true);
    public static final TKeypress kbShiftEnd = new TKeypress(true,
            TKeypress.END, ' ', false, false, true);
    public static final TKeypress kbShiftPgUp = new TKeypress(true,
            TKeypress.PGUP, ' ', false, false, true);
    public static final TKeypress kbShiftPgDn = new TKeypress(true,
            TKeypress.PGDN, ' ', false, false, true);
    public static final TKeypress kbShiftIns = new TKeypress(true,
            TKeypress.INS, ' ', false, false, true);
    public static final TKeypress kbShiftDel = new TKeypress(true,
            TKeypress.DEL, ' ', false, false, true);
    public static final TKeypress kbShiftUp = new TKeypress(true,
            TKeypress.UP, ' ', false, false, true);
    public static final TKeypress kbShiftDown = new TKeypress(true,
            TKeypress.DOWN, ' ', false, false, true);
    public static final TKeypress kbShiftLeft = new TKeypress(true,
            TKeypress.LEFT, ' ', false, false, true);
    public static final TKeypress kbShiftRight = new TKeypress(true,
            TKeypress.RIGHT, ' ', false, false, true);
    public static final TKeypress kbA = new TKeypress(false,
            0, 'a', false, false, false);
    public static final TKeypress kbB = new TKeypress(false,
            0, 'b', false, false, false);
    public static final TKeypress kbC = new TKeypress(false,
            0, 'c', false, false, false);
    public static final TKeypress kbD = new TKeypress(false,
            0, 'd', false, false, false);
    public static final TKeypress kbE = new TKeypress(false,
            0, 'e', false, false, false);
    public static final TKeypress kbF = new TKeypress(false,
            0, 'f', false, false, false);
    public static final TKeypress kbG = new TKeypress(false,
            0, 'g', false, false, false);
    public static final TKeypress kbH = new TKeypress(false,
            0, 'h', false, false, false);
    public static final TKeypress kbI = new TKeypress(false,
            0, 'i', false, false, false);
    public static final TKeypress kbJ = new TKeypress(false,
            0, 'j', false, false, false);
    public static final TKeypress kbK = new TKeypress(false,
            0, 'k', false, false, false);
    public static final TKeypress kbL = new TKeypress(false,
            0, 'l', false, false, false);
    public static final TKeypress kbM = new TKeypress(false,
            0, 'm', false, false, false);
    public static final TKeypress kbN = new TKeypress(false,
            0, 'n', false, false, false);
    public static final TKeypress kbO = new TKeypress(false,
            0, 'o', false, false, false);
    public static final TKeypress kbP = new TKeypress(false,
            0, 'p', false, false, false);
    public static final TKeypress kbQ = new TKeypress(false,
            0, 'q', false, false, false);
    public static final TKeypress kbR = new TKeypress(false,
            0, 'r', false, false, false);
    public static final TKeypress kbS = new TKeypress(false,
            0, 's', false, false, false);
    public static final TKeypress kbT = new TKeypress(false,
            0, 't', false, false, false);
    public static final TKeypress kbU = new TKeypress(false,
            0, 'u', false, false, false);
    public static final TKeypress kbV = new TKeypress(false,
            0, 'v', false, false, false);
    public static final TKeypress kbW = new TKeypress(false,
            0, 'w', false, false, false);
    public static final TKeypress kbX = new TKeypress(false,
            0, 'x', false, false, false);
    public static final TKeypress kbY = new TKeypress(false,
            0, 'y', false, false, false);
    public static final TKeypress kbZ = new TKeypress(false,
            0, 'z', false, false, false);
    public static final TKeypress kbSpace = new TKeypress(false,
            0, ' ', false, false, false);
    public static final TKeypress kbAltA = new TKeypress(false,
            0, 'a', true, false, false);
    public static final TKeypress kbAltB = new TKeypress(false,
            0, 'b', true, false, false);
    public static final TKeypress kbAltC = new TKeypress(false,
            0, 'c', true, false, false);
    public static final TKeypress kbAltD = new TKeypress(false,
            0, 'd', true, false, false);
    public static final TKeypress kbAltE = new TKeypress(false,
            0, 'e', true, false, false);
    public static final TKeypress kbAltF = new TKeypress(false,
            0, 'f', true, false, false);
    public static final TKeypress kbAltG = new TKeypress(false,
            0, 'g', true, false, false);
    public static final TKeypress kbAltH = new TKeypress(false,
            0, 'h', true, false, false);
    public static final TKeypress kbAltI = new TKeypress(false,
            0, 'i', true, false, false);
    public static final TKeypress kbAltJ = new TKeypress(false,
            0, 'j', true, false, false);
    public static final TKeypress kbAltK = new TKeypress(false,
            0, 'k', true, false, false);
    public static final TKeypress kbAltL = new TKeypress(false,
            0, 'l', true, false, false);
    public static final TKeypress kbAltM = new TKeypress(false,
            0, 'm', true, false, false);
    public static final TKeypress kbAltN = new TKeypress(false,
            0, 'n', true, false, false);
    public static final TKeypress kbAltO = new TKeypress(false,
            0, 'o', true, false, false);
    public static final TKeypress kbAltP = new TKeypress(false,
            0, 'p', true, false, false);
    public static final TKeypress kbAltQ = new TKeypress(false,
            0, 'q', true, false, false);
    public static final TKeypress kbAltR = new TKeypress(false,
            0, 'r', true, false, false);
    public static final TKeypress kbAltS = new TKeypress(false,
            0, 's', true, false, false);
    public static final TKeypress kbAltT = new TKeypress(false,
            0, 't', true, false, false);
    public static final TKeypress kbAltU = new TKeypress(false,
            0, 'u', true, false, false);
    public static final TKeypress kbAltV = new TKeypress(false,
            0, 'v', true, false, false);
    public static final TKeypress kbAltW = new TKeypress(false,
            0, 'w', true, false, false);
    public static final TKeypress kbAltX = new TKeypress(false,
            0, 'x', true, false, false);
    public static final TKeypress kbAltY = new TKeypress(false,
            0, 'y', true, false, false);
    public static final TKeypress kbAltZ = new TKeypress(false,
            0, 'z', true, false, false);
    public static final TKeypress kbCtrlA = new TKeypress(false,
            0, 'A', false, true, false);
    public static final TKeypress kbCtrlB = new TKeypress(false,
            0, 'B', false, true, false);
    public static final TKeypress kbCtrlC = new TKeypress(false,
            0, 'C', false, true, false);
    public static final TKeypress kbCtrlD = new TKeypress(false,
            0, 'D', false, true, false);
    public static final TKeypress kbCtrlE = new TKeypress(false,
            0, 'E', false, true, false);
    public static final TKeypress kbCtrlF = new TKeypress(false,
            0, 'F', false, true, false);
    public static final TKeypress kbCtrlG = new TKeypress(false,
            0, 'G', false, true, false);
    public static final TKeypress kbCtrlH = new TKeypress(false,
            0, 'H', false, true, false);
    public static final TKeypress kbCtrlI = new TKeypress(false,
            0, 'I', false, true, false);
    public static final TKeypress kbCtrlJ = new TKeypress(false,
            0, 'J', false, true, false);
    public static final TKeypress kbCtrlK = new TKeypress(false,
            0, 'K', false, true, false);
    public static final TKeypress kbCtrlL = new TKeypress(false,
            0, 'L', false, true, false);
    public static final TKeypress kbCtrlM = new TKeypress(false,
            0, 'M', false, true, false);
    public static final TKeypress kbCtrlN = new TKeypress(false,
            0, 'N', false, true, false);
    public static final TKeypress kbCtrlO = new TKeypress(false,
            0, 'O', false, true, false);
    public static final TKeypress kbCtrlP = new TKeypress(false,
            0, 'P', false, true, false);
    public static final TKeypress kbCtrlQ = new TKeypress(false,
            0, 'Q', false, true, false);
    public static final TKeypress kbCtrlR = new TKeypress(false,
            0, 'R', false, true, false);
    public static final TKeypress kbCtrlS = new TKeypress(false,
            0, 'S', false, true, false);
    public static final TKeypress kbCtrlT = new TKeypress(false,
            0, 'T', false, true, false);
    public static final TKeypress kbCtrlU = new TKeypress(false,
            0, 'U', false, true, false);
    public static final TKeypress kbCtrlV = new TKeypress(false,
            0, 'V', false, true, false);
    public static final TKeypress kbCtrlW = new TKeypress(false,
            0, 'W', false, true, false);
    public static final TKeypress kbCtrlX = new TKeypress(false,
            0, 'X', false, true, false);
    public static final TKeypress kbCtrlY = new TKeypress(false,
            0, 'Y', false, true, false);
    public static final TKeypress kbCtrlZ = new TKeypress(false,
            0, 'Z', false, true, false);
    public static final TKeypress kbAltShiftA = new TKeypress(false,
            0, 'A', true, false, true);
    public static final TKeypress kbAltShiftB = new TKeypress(false,
            0, 'B', true, false, true);
    public static final TKeypress kbAltShiftC = new TKeypress(false,
            0, 'C', true, false, true);
    public static final TKeypress kbAltShiftD = new TKeypress(false,
            0, 'D', true, false, true);
    public static final TKeypress kbAltShiftE = new TKeypress(false,
            0, 'E', true, false, true);
    public static final TKeypress kbAltShiftF = new TKeypress(false,
            0, 'F', true, false, true);
    public static final TKeypress kbAltShiftG = new TKeypress(false,
            0, 'G', true, false, true);
    public static final TKeypress kbAltShiftH = new TKeypress(false,
            0, 'H', true, false, true);
    public static final TKeypress kbAltShiftI = new TKeypress(false,
            0, 'I', true, false, true);
    public static final TKeypress kbAltShiftJ = new TKeypress(false,
            0, 'J', true, false, true);
    public static final TKeypress kbAltShiftK = new TKeypress(false,
            0, 'K', true, false, true);
    public static final TKeypress kbAltShiftL = new TKeypress(false,
            0, 'L', true, false, true);
    public static final TKeypress kbAltShiftM = new TKeypress(false,
            0, 'M', true, false, true);
    public static final TKeypress kbAltShiftN = new TKeypress(false,
            0, 'N', true, false, true);
    public static final TKeypress kbAltShiftO = new TKeypress(false,
            0, 'O', true, false, true);
    public static final TKeypress kbAltShiftP = new TKeypress(false,
            0, 'P', true, false, true);
    public static final TKeypress kbAltShiftQ = new TKeypress(false,
            0, 'Q', true, false, true);
    public static final TKeypress kbAltShiftR = new TKeypress(false,
            0, 'R', true, false, true);
    public static final TKeypress kbAltShiftS = new TKeypress(false,
            0, 'S', true, false, true);
    public static final TKeypress kbAltShiftT = new TKeypress(false,
            0, 'T', true, false, true);
    public static final TKeypress kbAltShiftU = new TKeypress(false,
            0, 'U', true, false, true);
    public static final TKeypress kbAltShiftV = new TKeypress(false,
            0, 'V', true, false, true);
    public static final TKeypress kbAltShiftW = new TKeypress(false,
            0, 'W', true, false, true);
    public static final TKeypress kbAltShiftX = new TKeypress(false,
            0, 'X', true, false, true);
    public static final TKeypress kbAltShiftY = new TKeypress(false,
            0, 'Y', true, false, true);
    public static final TKeypress kbAltShiftZ = new TKeypress(false,
            0, 'Z', true, false, true);

    /**
     * Backspace as ^H.
     */
    public static final TKeypress kbBackspace = new TKeypress(false,
            0, 'H', false, true, false);

    /**
     * Backspace as ^?.
     */
    public static final TKeypress kbBackspaceDel = new TKeypress(false,
            0, (char)0x7F, false, false, false);

}
