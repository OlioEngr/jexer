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
package jexer;

import jexer.bits.CellAttributes;
import jexer.bits.Color;
import jexer.bits.GraphicsChars;
import jexer.bits.MnemonicString;
import jexer.event.TKeypressEvent;
import jexer.event.TMouseEvent;
import static jexer.TKeypress.*;

/**
 * TButton implements a simple button.  To make the button do something, pass
 * a TAction class to its constructor.
 *
 * @see TAction#DO()
 */
public final class TButton extends TWidget {

    /**
     * The shortcut and button text.
     */
    private MnemonicString mnemonic;

    /**
     * Remember mouse state.
     */
    private TMouseEvent mouse;

    /**
     * True when the button is being pressed and held down.
     */
    private boolean inButtonPress = false;

    /**
     * The action to perform when the button is clicked.
     */
    private TAction action;

    /**
     * Private constructor.
     *
     * @param parent parent widget
     * @param text label on the button
     * @param x column relative to parent
     * @param y row relative to parent
     */
    private TButton(final TWidget parent, final String text,
        final int x, final int y) {

        // Set parent and window
        super(parent);

        mnemonic = new MnemonicString(text);

        setX(x);
        setY(y);
        setHeight(2);
        setWidth(mnemonic.getRawLabel().length() + 3);
    }

    /**
     * Public constructor.
     *
     * @param parent parent widget
     * @param text label on the button
     * @param x column relative to parent
     * @param y row relative to parent
     * @param action to call when button is pressed
     */
    public TButton(final TWidget parent, final String text,
        final int x, final int y, final TAction action) {

        this(parent, text, x, y);
        this.action = action;
    }

    /**
     * Returns true if the mouse is currently on the button.
     *
     * @return if true the mouse is currently on the button
     */
    private boolean mouseOnButton() {
        int rightEdge = getWidth() - 1;
        if (inButtonPress) {
            rightEdge++;
        }
        if ((mouse != null)
            && (mouse.getY() == 0)
            && (mouse.getX() >= 0)
            && (mouse.getX() < rightEdge)
        ) {
            return true;
        }
        return false;
    }

    /**
     * Draw a button with a shadow.
     */
    @Override
    public void draw() {
        CellAttributes buttonColor;
        CellAttributes menuMnemonicColor;
        CellAttributes shadowColor = new CellAttributes();
        shadowColor.setTo(getWindow().getBackground());
        shadowColor.setForeColor(Color.BLACK);
        shadowColor.setBold(false);

        if (!getEnabled()) {
            buttonColor = getTheme().getColor("tbutton.disabled");
            menuMnemonicColor = getTheme().getColor("tbutton.disabled");
        } else if (getAbsoluteActive()) {
            buttonColor = getTheme().getColor("tbutton.active");
            menuMnemonicColor = getTheme().getColor("tbutton.mnemonic.highlighted");
        } else {
            buttonColor = getTheme().getColor("tbutton.inactive");
            menuMnemonicColor = getTheme().getColor("tbutton.mnemonic");
        }

        if (inButtonPress) {
            getScreen().putCharXY(1, 0, ' ', buttonColor);
            getScreen().putStrXY(2, 0, mnemonic.getRawLabel(), buttonColor);
            getScreen().putCharXY(getWidth() - 1, 0, ' ', buttonColor);
        } else {
            getScreen().putCharXY(0, 0, ' ', buttonColor);
            getScreen().putStrXY(1, 0, mnemonic.getRawLabel(), buttonColor);
            getScreen().putCharXY(getWidth() - 2, 0, ' ', buttonColor);

            getScreen().putCharXY(getWidth() - 1, 0,
                GraphicsChars.CP437[0xDC], shadowColor);
            getScreen().hLineXY(1, 1, getWidth() - 1,
                GraphicsChars.CP437[0xDF], shadowColor);
        }
        if (mnemonic.getShortcutIdx() >= 0) {
            if (inButtonPress) {
                getScreen().putCharXY(2 + mnemonic.getShortcutIdx(), 0,
                    mnemonic.getShortcut(), menuMnemonicColor);
            } else {
                getScreen().putCharXY(1 + mnemonic.getShortcutIdx(), 0,
                    mnemonic.getShortcut(), menuMnemonicColor);
            }

        }
    }

    /**
     * Handle mouse button presses.
     *
     * @param mouse mouse button event
     */
    @Override
    public void onMouseDown(final TMouseEvent mouse) {
        this.mouse = mouse;

        if ((mouseOnButton()) && (mouse.getMouse1())) {
            // Begin button press
            inButtonPress = true;
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

        if (inButtonPress && mouse.getMouse1()) {
            inButtonPress = false;
            // Dispatch the event
            if (action != null) {
                action.DO();
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

        if (!mouseOnButton()) {
            inButtonPress = false;
        }
    }

    /**
     * Handle keystrokes.
     *
     * @param keypress keystroke event
     */
    @Override
    public void onKeypress(final TKeypressEvent keypress) {
        if (keypress.equals(kbEnter)
            || keypress.equals(kbSpace)
        ) {
            // Dispatch
            if (action != null) {
                action.DO();
            }
            return;
        }

        // Pass to parent for the things we don't care about.
        super.onKeypress(keypress);
    }

}
