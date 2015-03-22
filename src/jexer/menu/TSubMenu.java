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
package jexer.menu;

import jexer.TKeypress;
import jexer.TWidget;
import jexer.bits.CellAttributes;
import jexer.bits.GraphicsChars;
import jexer.event.TKeypressEvent;
import static jexer.TKeypress.*;

/**
 * TSubMenu is a special case menu item that wraps another TMenu.
 */
public final class TSubMenu extends TMenuItem {

    /**
     * The menu window.  Note package private access.
     */
    TMenu menu;

    /**
     * Package private constructor.
     *
     * @param parent parent widget
     * @param title menu title.  Title must contain a keyboard shortcut,
     * denoted by prefixing a letter with "&", e.g. "&File"
     * @param x column relative to parent
     * @param y row relative to parent
     */
    TSubMenu(final TMenu parent, final String title, final int x, final int y) {
        super(parent, TMenu.MID_UNUSED, x, y, title);

        setActive(false);
        setEnabled(true);

        this.menu = new TMenu(parent.getApplication(), x, getAbsoluteY() - 1,
            title);
        setWidth(menu.getWidth() + 2);

        this.menu.isSubMenu = true;
    }

    /**
     * Draw the menu title.
     */
    @Override
    public void draw() {
        super.draw();

        CellAttributes menuColor;
        if (isAbsoluteActive()) {
            menuColor = getTheme().getColor("tmenu.highlighted");
        } else {
            if (isEnabled()) {
                menuColor = getTheme().getColor("tmenu");
            } else {
                menuColor = getTheme().getColor("tmenu.disabled");
            }
        }

        // Add the arrow
        getScreen().putCharXY(getWidth() - 2, 0, GraphicsChars.CP437[0x10],
            menuColor);
    }

    /**
     * Handle keystrokes.
     *
     * @param keypress keystroke event
     */
    @Override
    public void onKeypress(final TKeypressEvent keypress) {

        if (menu.isActive()) {
            menu.onKeypress(keypress);
            return;
        }

        if (keypress.equals(kbEnter)) {
            dispatch();
            return;
        }

        if (keypress.equals(kbRight)) {
            dispatch();
            return;
        }

        if (keypress.equals(kbDown)) {
            getParent().switchWidget(true);
            return;
        }

        if (keypress.equals(kbUp)) {
            getParent().switchWidget(false);
            return;
        }

        if (keypress.equals(kbLeft)) {
            TMenu parentMenu = (TMenu) getParent();
            if (parentMenu.isSubMenu) {
                getApplication().closeSubMenu();
            } else {
                getApplication().switchMenu(false);
            }
            return;
        }

        if (keypress.equals(kbEsc)) {
            getApplication().closeMenu();
            return;
        }
    }

    /**
     * Override dispatch() to do nothing.
     */
    @Override
    public void dispatch() {
        assert (isEnabled());
        if (isAbsoluteActive()) {
            if (!menu.isActive()) {
                getApplication().addSubMenu(menu);
                menu.setActive(true);
            }
        }
    }

    /**
     * Returns my active widget.
     *
     * @return widget that is active, or this if no children
     */
    @Override
    public TWidget getActiveChild() {
        if (menu.isActive()) {
            return menu;
        }
        // Menu not active, return me
        return this;
    }

    /**
     * Convenience function to add a custom menu item.
     *
     * @param id menu item ID.  Must be greater than 1024.
     * @param label menu item label
     * @param key global keyboard accelerator
     * @return the new menu item
     */
    public TMenuItem addItem(final int id, final String label,
        final TKeypress key) {

        return menu.addItem(id, label, key);
    }

    /**
     * Convenience function to add a menu item.
     *
     * @param id menu item ID.  Must be greater than 1024.
     * @param label menu item label
     * @return the new menu item
     */
    public TMenuItem addItem(final int id, final String label) {
        return menu.addItem(id, label);
    }

    /**
     * Convenience function to add one of the default menu items.
     *
     * @param id menu item ID.  Must be between 0 (inclusive) and 1023
     * (inclusive).
     * @return the new menu item
     */
    public TMenuItem addDefaultItem(final int id) {
        return menu.addDefaultItem(id);
    }

    /**
     * Convenience function to add a menu separator.
     */
    public void addSeparator() {
        menu.addSeparator();
    }

    /**
     * Convenience function to add a sub-menu.
     *
     * @param title menu title.  Title must contain a keyboard shortcut,
     * denoted by prefixing a letter with "&", e.g. "&File"
     * @return the new sub-menu
     */
    public TSubMenu addSubMenu(final String title) {
        return menu.addSubMenu(title);
    }


}
