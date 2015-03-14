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

import jexer.TApplication;
import jexer.TKeypress;
import jexer.TWidget;
import jexer.TWindow;
import jexer.bits.CellAttributes;
import jexer.bits.GraphicsChars;
import jexer.bits.MnemonicString;
import jexer.event.TKeypressEvent;
import jexer.event.TMouseEvent;
import static jexer.TKeypress.*;

/**
 * TMenu is a top-level collection of TMenuItems.
 */
public final class TMenu extends TWindow {

    /**
     * If true, this is a sub-menu.  Note package private access.
     */
    boolean isSubMenu = false;

    /**
     * The shortcut and title.
     */
    private MnemonicString mnemonic;

    /**
     * Get the mnemonic string.
     *
     * @return the full mnemonic string
     */
    public MnemonicString getMnemonic() {
        return mnemonic;
    }

    // Reserved menu item IDs
    public static final int MID_UNUSED          = -1;

    // File menu
    public static final int MID_EXIT            = 1;
    public static final int MID_QUIT            = MID_EXIT;
    public static final int MID_OPEN_FILE       = 2;
    public static final int MID_SHELL           = 3;

    // Edit menu
    public static final int MID_CUT             = 10;
    public static final int MID_COPY            = 11;
    public static final int MID_PASTE           = 12;
    public static final int MID_CLEAR           = 13;

    // Window menu
    public static final int MID_TILE            = 20;
    public static final int MID_CASCADE         = 21;
    public static final int MID_CLOSE_ALL       = 22;
    public static final int MID_WINDOW_MOVE     = 23;
    public static final int MID_WINDOW_ZOOM     = 24;
    public static final int MID_WINDOW_NEXT     = 25;
    public static final int MID_WINDOW_PREVIOUS = 26;
    public static final int MID_WINDOW_CLOSE    = 27;

    /**
     * Public constructor.
     *
     * @param parent parent application
     * @param x column relative to parent
     * @param y row relative to parent
     * @param label mnemonic menu title.  Label must contain a keyboard
     * shortcut (mnemonic), denoted by prefixing a letter with "&",
     * e.g. "&File"
     */
    public TMenu(final TApplication parent, final int x, final int y,
        final String label) {

        super(parent, label, x, y, parent.getScreen().getWidth(),
            parent.getScreen().getHeight());

        // My parent constructor added me as a window, get rid of that
        parent.closeWindow(this);

        // Setup the menu shortcut
        mnemonic = new MnemonicString(label);
        setTitle(mnemonic.getRawLabel());
        assert (mnemonic.getShortcutIdx() >= 0);

        // Recompute width and height to reflect an empty menu
        setWidth(getTitle().length() + 4);
        setHeight(2);

        setActive(false);
    }

    /**
     * Draw a top-level menu with title and menu items.
     */
    @Override
    public void draw() {
        CellAttributes menuColor;
        CellAttributes background = getTheme().getColor("tmenu");

        if (getAbsoluteActive()) {
            menuColor = getTheme().getColor("tmenu.highlighted");
        } else {
            menuColor = getTheme().getColor("tmenu");
        }

        assert (getAbsoluteActive());

        // Fill in the interior background
        for (int i = 0; i < getHeight(); i++) {
            hLineXY(0, i, getWidth(), ' ', background);
        }

        // Draw the box
        char cTopLeft;
        char cTopRight;
        char cBottomLeft;
        char cBottomRight;
        char cHSide;

        cTopLeft = GraphicsChars.ULCORNER;
        cTopRight = GraphicsChars.URCORNER;
        cBottomLeft = GraphicsChars.LLCORNER;
        cBottomRight = GraphicsChars.LRCORNER;
        cHSide = GraphicsChars.SINGLE_BAR;

        // Place the corner characters
        putCharXY(1, 0, cTopLeft, background);
        putCharXY(getWidth() - 2, 0, cTopRight, background);
        putCharXY(1, getHeight() - 1, cBottomLeft, background);
        putCharXY(getWidth() - 2, getHeight() - 1, cBottomRight, background);

        // Draw the box lines
        hLineXY(1 + 1, 0, getWidth() - 4, cHSide, background);
        hLineXY(1 + 1, getHeight() - 1, getWidth() - 4, cHSide, background);

        // Draw a shadow
        getScreen().drawBoxShadow(0, 0, getWidth(), getHeight());
    }

    /**
     * Handle mouse button presses.
     *
     * @param mouse mouse button event
     */
    @Override
    public void onMouseDown(final TMouseEvent mouse) {
        this.mouse = mouse;
        setRepaint();

        // Pass to children
        for (TWidget widget: getChildren()) {
            if (widget.mouseWouldHit(mouse)) {
                // Dispatch to this child, also activate it
                activate(widget);

                // Set x and y relative to the child's coordinates
                mouse.setX(mouse.getAbsoluteX() - widget.getAbsoluteX());
                mouse.setY(mouse.getAbsoluteY() - widget.getAbsoluteY());
                widget.handleEvent(mouse);
                return;
            }
        }
    }

    /**
     * Handle mouse button releases.
     *
     * @param mouse mouse button release event
     */
    @Override
    public void onMouseUp(final TMouseEvent mouse) {
        this.mouse = mouse;
        setRepaint();

        // Pass to children
        for (TWidget widget: getChildren()) {
            if (widget.mouseWouldHit(mouse)) {
                // Dispatch to this child, also activate it
                activate(widget);

                // Set x and y relative to the child's coordinates
                mouse.setX(mouse.getAbsoluteX() - widget.getAbsoluteX());
                mouse.setY(mouse.getAbsoluteY() - widget.getAbsoluteY());
                widget.handleEvent(mouse);
                return;
            }
        }
    }

    /**
     * Handle mouse movements.
     *
     * @param mouse mouse motion event
     */
    @Override
    public void onMouseMotion(final TMouseEvent mouse) {
        this.mouse = mouse;
        setRepaint();

        // See if we should activate a different menu item
        for (TWidget widget: getChildren()) {
            if ((mouse.getMouse1())
                && (widget.mouseWouldHit(mouse))
            ) {
                // Activate this menu item
                activate(widget);
                if (widget instanceof TSubMenu) {
                    ((TSubMenu) widget).dispatch();
                }
                return;
            }
        }
    }

    /**
     * Handle keystrokes.
     *
     * @param keypress keystroke event
     */
    @Override
    public void onKeypress(final TKeypressEvent keypress) {
        if (getActiveChild() != null) {
            if (getActiveChild() instanceof TSubMenu) {
                getActiveChild().onKeypress(keypress);
                return;
            }
        }

        if (keypress.equals(kbEsc)) {
            getApplication().closeMenu();
            return;
        }
        if (keypress.equals(kbDown)) {
            switchWidget(true);
            return;
        }
        if (keypress.equals(kbUp)) {
            switchWidget(false);
            return;
        }
        if (keypress.equals(kbRight)) {
            if (!isSubMenu) {
                getApplication().switchMenu(true);
            }
            return;
        }
        if (keypress.equals(kbLeft)) {
            if (isSubMenu) {
                getApplication().closeSubMenu();
            } else {
                getApplication().switchMenu(false);
            }
            return;
        }

        // Switch to a menuItem if it has an mnemonic
        if (!keypress.getKey().getIsKey()
            && !keypress.getKey().getAlt()
            && !keypress.getKey().getCtrl()) {
            for (TWidget widget: getChildren()) {
                TMenuItem item = (TMenuItem) widget;
                if ((item.getMnemonic() != null)
                    && (Character.toLowerCase(item.getMnemonic().getShortcut())
                        == Character.toLowerCase(keypress.getKey().getCh()))
                ) {
                    // Send an enter keystroke to it
                    activate(item);
                    item.handleEvent(new TKeypressEvent(kbEnter));
                    return;
                }
            }
        }

        // Dispatch the keypress to an active widget
        for (TWidget widget: getChildren()) {
            if (widget.getActive()) {
                setRepaint();
                widget.handleEvent(keypress);
                return;
            }
        }
    }

    /**
     * Convenience function to add a custom menu item.
     *
     * @param id menu item ID.  Must be greater than 1024.
     * @param label menu item label
     * @param key global keyboard accelerator
     * @return the new menu item
     */
    public final TMenuItem addItem(final int id, final String label,
        final TKeypress key) {

        assert (id >= 1024);
        return addItemInternal(id, label, key);
    }

    /**
     * Convenience function to add a custom menu item.
     *
     * @param id menu item ID.  Must be greater than 1024.
     * @param label menu item label
     * @param key global keyboard accelerator
     * @return the new menu item
     */
    private TMenuItem addItemInternal(final int id, final String label,
        final TKeypress key) {

        int newY = getChildren().size() + 1;
        assert (newY < getHeight());

        TMenuItem menuItem = new TMenuItem(this, id, 1, newY, label);
        menuItem.setKey(key);
        setHeight(getHeight() + 1);
        if (menuItem.getWidth() + 2 > getWidth()) {
            setWidth(menuItem.getWidth() + 2);
        }
        for (TWidget widget: getChildren()) {
            widget.setWidth(getWidth() - 2);
        }
        getApplication().addAccelerator(menuItem, key.toLowerCase());
        getApplication().recomputeMenuX();
        activate(0);
        return menuItem;
    }

    /**
     * Convenience function to add a menu item.
     *
     * @param id menu item ID.  Must be greater than 1024.
     * @param label menu item label
     * @return the new menu item
     */
    public final TMenuItem addItem(final int id, final String label) {
        assert (id >= 1024);
        return addItemInternal(id, label);
    }

    /**
     * Convenience function to add a menu item.
     *
     * @param id menu item ID
     * @param label menu item label
     * @return the new menu item
     */
    private TMenuItem addItemInternal(final int id, final String label) {
        int newY = getChildren().size() + 1;
        assert (newY < getHeight());

        TMenuItem menuItem = new TMenuItem(this, id, 1, newY, label);
        setHeight(getHeight() + 1);
        if (menuItem.getWidth() + 2 > getWidth()) {
            setWidth(menuItem.getWidth() + 2);
        }
        for (TWidget widget: getChildren()) {
            widget.setWidth(getWidth() - 2);
        }
        getApplication().recomputeMenuX();
        activate(0);
        return menuItem;
    }

    /**
     * Convenience function to add one of the default menu items.
     *
     * @param id menu item ID.  Must be between 0 (inclusive) and 1023
     * (inclusive).
     * @return the new menu item
     */
    public final TMenuItem addDefaultItem(final int id) {
        assert (id >= 0);
        assert (id < 1024);

        String label;
        TKeypress key = null;
        boolean hasKey = true;

        switch (id) {

        case MID_EXIT:
            label = "E&xit";
            key = kbAltX;
            break;

        case MID_SHELL:
            label = "O&S Shell";
            hasKey = false;
            break;

        case MID_OPEN_FILE:
            label = "&Open";
            key = kbAltO;
            break;

        case MID_CUT:
            label = "Cu&t";
            key = kbCtrlX;
            break;
        case MID_COPY:
            label = "&Copy";
            key = kbCtrlC;
            break;
        case MID_PASTE:
            label = "&Paste";
            key = kbCtrlV;
            break;
        case MID_CLEAR:
            label = "C&lear";
            key = kbDel;
            break;

        case MID_TILE:
            label = "&Tile";
            hasKey = false;
            break;
        case MID_CASCADE:
            label = "C&ascade";
            hasKey = false;
            break;
        case MID_CLOSE_ALL:
            label = "Cl&ose All";
            hasKey = false;
            break;
        case MID_WINDOW_MOVE:
            label = "&Size/Move";
            key = kbCtrlF5;
            break;
        case MID_WINDOW_ZOOM:
            label = "&Zoom";
            key = kbF5;
            break;
        case MID_WINDOW_NEXT:
            label = "&Next";
            key = kbF6;
            break;
        case MID_WINDOW_PREVIOUS:
            label = "&Previous";
            key = kbShiftF6;
            break;
        case MID_WINDOW_CLOSE:
            label = "&Close";
            key = kbCtrlW;
            break;

        default:
            throw new IllegalArgumentException("Invalid menu ID: " + id);
        }

        if (hasKey) {
            return addItemInternal(id, label, key);
        }
        return addItemInternal(id, label);
    }

    /**
     * Convenience function to add a menu separator.
     */
    public final void addSeparator() {
        int newY = getChildren().size() + 1;
        assert (newY < getHeight());

        TMenuItem menuItem = new TMenuSeparator(this, 1, newY);
        setHeight(getHeight() + 1);
    }

    /**
     * Convenience function to add a sub-menu.
     *
     * @param title menu title.  Title must contain a keyboard shortcut,
     * denoted by prefixing a letter with "&", e.g. "&File"
     * @return the new sub-menu
     */
    public final TSubMenu addSubMenu(final String title) {
        int newY = getChildren().size() + 1;
        assert (newY < getHeight());

        TSubMenu subMenu = new TSubMenu(this, title, 1, newY);
        setHeight(getHeight() + 1);
        if (subMenu.getWidth() + 2 > getWidth()) {
            setWidth(subMenu.getWidth() + 2);
        }
        for (TWidget widget: getChildren()) {
            widget.setWidth(getWidth() - 2);
        }
        getApplication().recomputeMenuX();
        activate(0);
        subMenu.menu.setX(getX() + getWidth() - 2);

        return subMenu;
    }

}
