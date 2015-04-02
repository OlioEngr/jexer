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
package jexer.menu;

import jexer.TKeypress;
import jexer.TWidget;
import jexer.bits.CellAttributes;
import jexer.bits.GraphicsChars;
import jexer.bits.MnemonicString;
import jexer.event.TKeypressEvent;
import jexer.event.TMouseEvent;
import jexer.event.TMenuEvent;
import static jexer.TKeypress.*;

/**
 * TMenuItem implements a menu item.
 */
public class TMenuItem extends TWidget {

    /**
     * Label for this menu item.
     */
    private String label;

    /**
     * Menu ID.  IDs less than 1024 are reserved for common system
     * functions.  Existing ones are defined in TMenu, i.e. TMenu.MID_EXIT.
     */
    private int id = TMenu.MID_UNUSED;

    /**
     * Get the menu item ID.
     *
     * @return the id
     */
    public final int getId() {
        return id;
    }

    /**
     * When true, this item can be checked or unchecked.
     */
    private boolean checkable = false;

    /**
     * Set checkable flag.
     *
     * @param checkable if true, this menu item can be checked/unchecked
     */
    public final void setCheckable(final boolean checkable) {
        this.checkable = checkable;
    }

    /**
     * When true, this item is checked.
     */
    private boolean checked = false;

    /**
     * Global shortcut key.
     */
    private TKeypress key;

    /**
     * The title string.  Use '&' to specify a mnemonic, i.e. "&File" will
     * highlight the 'F' and allow 'f' or 'F' to select it.
     */
    private MnemonicString mnemonic;

    /**
     * Get the mnemonic string for this menu item.
     *
     * @return mnemonic string
     */
    public final MnemonicString getMnemonic() {
        return mnemonic;
    }

    /**
     * Get a global accelerator key for this menu item.
     *
     * @return global keyboard accelerator, or null if no key is associated
     * with this item
     */
    public final TKeypress getKey() {
        return key;
    }

    /**
     * Set a global accelerator key for this menu item.
     *
     * @param key global keyboard accelerator
     */
    public final void setKey(final TKeypress key) {
        this.key = key;

        if (key != null) {
            int newWidth = (label.length() + 4 + key.toString().length() + 2);
            if (newWidth > getWidth()) {
                setWidth(newWidth);
            }
        }
    }

    /**
     * Package private constructor.
     *
     * @param parent parent widget
     * @param id menu id
     * @param x column relative to parent
     * @param y row relative to parent
     * @param label menu item title
     */
    TMenuItem(final TMenu parent, final int id, final int x, final int y,
        final String label) {

        // Set parent and window
        super(parent);

        mnemonic = new MnemonicString(label);

        setX(x);
        setY(y);
        setHeight(1);
        this.label = mnemonic.getRawLabel();
        setWidth(label.length() + 4);
        this.id = id;

        // Default state for some known menu items
        switch (id) {

        case TMenu.MID_CUT:
            setEnabled(false);
            break;
        case TMenu.MID_COPY:
            setEnabled(false);
            break;
        case TMenu.MID_PASTE:
            setEnabled(false);
            break;
        case TMenu.MID_CLEAR:
            setEnabled(false);
            break;

        case TMenu.MID_TILE:
            break;
        case TMenu.MID_CASCADE:
            break;
        case TMenu.MID_CLOSE_ALL:
            break;
        case TMenu.MID_WINDOW_MOVE:
            break;
        case TMenu.MID_WINDOW_ZOOM:
            break;
        case TMenu.MID_WINDOW_NEXT:
            break;
        case TMenu.MID_WINDOW_PREVIOUS:
            break;
        case TMenu.MID_WINDOW_CLOSE:
            break;
        default:
            break;
        }

    }

    /**
     * Returns true if the mouse is currently on the menu item.
     *
     * @param mouse mouse event
     * @return if true then the mouse is currently on this item
     */
    private boolean mouseOnMenuItem(final TMouseEvent mouse) {
        if ((mouse.getY() == 0)
            && (mouse.getX() >= 0)
            && (mouse.getX() < getWidth())
        ) {
            return true;
        }
        return false;
    }

    /**
     * Draw a menu item with label.
     */
    @Override
    public void draw() {
        CellAttributes background = getTheme().getColor("tmenu");
        CellAttributes menuColor;
        CellAttributes menuMnemonicColor;
        if (isAbsoluteActive()) {
            menuColor = getTheme().getColor("tmenu.highlighted");
            menuMnemonicColor = getTheme().getColor("tmenu.mnemonic.highlighted");
        } else {
            if (isEnabled()) {
                menuColor = getTheme().getColor("tmenu");
                menuMnemonicColor = getTheme().getColor("tmenu.mnemonic");
            } else {
                menuColor = getTheme().getColor("tmenu.disabled");
                menuMnemonicColor = getTheme().getColor("tmenu.disabled");
            }
        }

        char cVSide = GraphicsChars.WINDOW_SIDE;
        getScreen().vLineXY(0, 0, 1, cVSide, background);
        getScreen().vLineXY(getWidth() - 1, 0, 1, cVSide, background);

        getScreen().hLineXY(1, 0, getWidth() - 2, ' ', menuColor);
        getScreen().putStringXY(2, 0, mnemonic.getRawLabel(), menuColor);
        if (key != null) {
            String keyLabel = key.toString();
            getScreen().putStringXY((getWidth() - keyLabel.length() - 2), 0,
                keyLabel, menuColor);
        }
        if (mnemonic.getShortcutIdx() >= 0) {
            getScreen().putCharXY(2 + mnemonic.getShortcutIdx(), 0,
                mnemonic.getShortcut(), menuMnemonicColor);
        }
        if (checked) {
            assert (checkable);
            getScreen().putCharXY(1, 0, GraphicsChars.CHECK, menuColor);
        }

    }

    /**
     * Dispatch event(s) due to selection or click.
     */
    public void dispatch() {
        assert (isEnabled());

        getApplication().addMenuEvent(new TMenuEvent(id));
        if (checkable) {
            checked = !checked;
        }
    }

    /**
     * Handle mouse button releases.
     *
     * @param mouse mouse button release event
     */
    @Override
    public void onMouseUp(final TMouseEvent mouse) {
        if ((mouseOnMenuItem(mouse)) && (mouse.isMouse1())) {
            dispatch();
            return;
        }
    }

    /**
     * Handle keystrokes.
     *
     * @param keypress keystroke event
     */
    @Override
    public void onKeypress(final TKeypressEvent keypress) {
        if (keypress.equals(kbEnter)) {
            dispatch();
            return;
        }

        // Pass to parent for the things we don't care about.
        super.onKeypress(keypress);
    }
}
