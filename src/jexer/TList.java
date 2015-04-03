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

import java.util.ArrayList;
import java.util.List;

import jexer.bits.CellAttributes;
import jexer.event.TKeypressEvent;
import jexer.event.TMouseEvent;
import static jexer.TKeypress.*;

/**
 * TList shows a list of strings, and lets the user select one.
 */
public class TList extends TWidget {

    /**
     * The list of strings to display.
     */
    private List<String> strings;

    /**
     * Selected string.
     */
    private int selectedString = -1;

    /**
     * Get the selection index.
     *
     * @return -1 if nothing is selected, otherwise the index into the list
     */
    public final int getSelectedIndex() {
        return selectedString;
    }

    /**
     * Set the selected string index.
     *
     * @param index -1 to unselect, otherwise the index into the list
     */
    public final void setSelectedIndex(final int index) {
        selectedString = index;
    }

    /**
     * Get the selected string.
     *
     * @return the selected string, or null of nothing is selected yet
     */
    public final String getSelected() {
        if ((selectedString >= 0) && (selectedString <= strings.size() - 1)) {
            return strings.get(selectedString);
        }
        return null;
    }

    /**
     * Set the new list of strings to display.
     *
     * @param list new list of strings
     */
    public final void setList(final List<String> list) {
        strings.clear();
        strings.addAll(list);
        reflow();
    }

    /**
     * Vertical scrollbar.
     */
    private TVScroller vScroller;

    /**
     * Get the vertical scrollbar.  This is used by subclasses.
     *
     * @return the vertical scrollbar
     */
    public final TVScroller getVScroller() {
        return vScroller;
    }

    /**
     * Horizontal scrollbar.
     */
    private THScroller hScroller;

    /**
     * Get the horizontal scrollbar.  This is used by subclasses.
     *
     * @return the horizontal scrollbar
     */
    public final THScroller getHScroller() {
        return hScroller;
    }

    /**
     * Maximum width of a single line.
     */
    private int maxLineWidth;

    /**
     * The action to perform when the user selects an item (clicks or enter).
     */
    private TAction enterAction = null;

    /**
     * The action to perform when the user navigates with keyboard.
     */
    private TAction moveAction = null;

    /**
     * Perform user selection action.
     */
    public void dispatchEnter() {
        assert (selectedString >= 0);
        assert (selectedString < strings.size());
        if (enterAction != null) {
            enterAction.DO();
        }
    }

    /**
     * Perform list movement action.
     */
    public void dispatchMove() {
        assert (selectedString >= 0);
        assert (selectedString < strings.size());
        if (moveAction != null) {
            moveAction.DO();
        }
    }

    /**
     * Resize for a new width/height.
     */
    public void reflow() {

        // Reset the lines
        selectedString = -1;
        maxLineWidth = 0;

        for (int i = 0; i < strings.size(); i++) {
            String line = strings.get(i);
            if (line.length() > maxLineWidth) {
                maxLineWidth = line.length();
            }
        }

        // Start at the top
        if (vScroller == null) {
            vScroller = new TVScroller(this, getWidth() - 1, 0,
                getHeight() - 1);
        } else {
            vScroller.setX(getWidth() - 1);
            vScroller.setHeight(getHeight() - 1);
        }
        vScroller.setBottomValue(strings.size() - getHeight() + 1);
        vScroller.setTopValue(0);
        vScroller.setValue(0);
        if (vScroller.getBottomValue() < 0) {
            vScroller.setBottomValue(0);
        }
        vScroller.setBigChange(getHeight() - 1);

        // Start at the left
        if (hScroller == null) {
            hScroller = new THScroller(this, 0, getHeight() - 1,
                getWidth() - 1);
        } else {
            hScroller.setY(getHeight() - 1);
            hScroller.setWidth(getWidth() - 1);
        }
        hScroller.setRightValue(maxLineWidth - getWidth() + 1);
        hScroller.setLeftValue(0);
        hScroller.setValue(0);
        if (hScroller.getRightValue() < 0) {
            hScroller.setRightValue(0);
        }
        hScroller.setBigChange(getWidth() - 1);
    }

    /**
     * Public constructor.
     *
     * @param parent parent widget
     * @param strings list of strings to show
     * @param x column relative to parent
     * @param y row relative to parent
     * @param width width of text area
     * @param height height of text area
     */
    public TList(final TWidget parent, final List<String> strings, final int x,
        final int y, final int width, final int height) {

        this(parent, strings, x, y, width, height, null);
    }

    /**
     * Public constructor.
     *
     * @param parent parent widget
     * @param strings list of strings to show.  This is allowed to be null
     * and set later with setList() or by subclasses.
     * @param x column relative to parent
     * @param y row relative to parent
     * @param width width of text area
     * @param height height of text area
     * @param enterAction action to perform when an item is selected
     */
    public TList(final TWidget parent, final List<String> strings, final int x,
        final int y, final int width, final int height,
        final TAction enterAction) {

        super(parent, x, y, width, height);
        this.enterAction = enterAction;
        this.strings = new ArrayList<String>();
        if (strings != null) {
            this.strings.addAll(strings);
        }
        reflow();
    }

    /**
     * Public constructor.
     *
     * @param parent parent widget
     * @param strings list of strings to show.  This is allowed to be null
     * and set later with setList() or by subclasses.
     * @param x column relative to parent
     * @param y row relative to parent
     * @param width width of text area
     * @param height height of text area
     * @param enterAction action to perform when an item is selected
     * @param moveAction action to perform when the user navigates to a new
     * item with arrow/page keys
     */
    public TList(final TWidget parent, final List<String> strings, final int x,
        final int y, final int width, final int height,
        final TAction enterAction, final TAction moveAction) {

        super(parent, x, y, width, height);
        this.enterAction = enterAction;
        this.moveAction = moveAction;
        this.strings = new ArrayList<String>();
        if (strings != null) {
            this.strings.addAll(strings);
        }
        reflow();
    }

    /**
     * Draw the files list.
     */
    @Override
    public void draw() {
        CellAttributes color = null;
        int begin = vScroller.getValue();
        int topY = 0;
        for (int i = begin; i < strings.size(); i++) {
            String line = strings.get(i);
            if (hScroller.getValue() < line.length()) {
                line = line.substring(hScroller.getValue());
            } else {
                line = "";
            }
            if (i == selectedString) {
                color = getTheme().getColor("tlist.selected");
            } else if (isAbsoluteActive()) {
                color = getTheme().getColor("tlist");
            } else {
                color = getTheme().getColor("tlist.inactive");
            }
            String formatString = "%-" + Integer.toString(getWidth() - 1) + "s";
            getScreen().putStringXY(0, topY, String.format(formatString, line),
                    color);
            topY++;
            if (topY >= getHeight() - 1) {
                break;
            }
        }

        if (isAbsoluteActive()) {
            color = getTheme().getColor("tlist");
        } else {
            color = getTheme().getColor("tlist.inactive");
        }

        // Pad the rest with blank lines
        for (int i = topY; i < getHeight() - 1; i++) {
            getScreen().hLineXY(0, i, getWidth() - 1, ' ', color);
        }
    }

    /**
     * Handle mouse press events.
     *
     * @param mouse mouse button press event
     */
    @Override
    public void onMouseDown(final TMouseEvent mouse) {
        if (mouse.isMouseWheelUp()) {
            vScroller.decrement();
            return;
        }
        if (mouse.isMouseWheelDown()) {
            vScroller.increment();
            return;
        }

        if ((mouse.getX() < getWidth() - 1)
            && (mouse.getY() < getHeight() - 1)) {
            if (vScroller.getValue() + mouse.getY() < strings.size()) {
                selectedString = vScroller.getValue() + mouse.getY();
            }
            dispatchEnter();
            return;
        }

        // Pass to children
        super.onMouseDown(mouse);
    }

    /**
     * Handle keystrokes.
     *
     * @param keypress keystroke event
     */
    @Override
    public void onKeypress(final TKeypressEvent keypress) {
        if (keypress.equals(kbLeft)) {
            hScroller.decrement();
        } else if (keypress.equals(kbRight)) {
            hScroller.increment();
        } else if (keypress.equals(kbUp)) {
            if (strings.size() > 0) {
                if (selectedString >= 0) {
                    if (selectedString > 0) {
                        if (selectedString - vScroller.getValue() == 0) {
                            vScroller.decrement();
                        }
                        selectedString--;
                    }
                } else {
                    selectedString = strings.size() - 1;
                }
            }
            if (selectedString >= 0) {
                dispatchMove();
            }
        } else if (keypress.equals(kbDown)) {
            if (strings.size() > 0) {
                if (selectedString >= 0) {
                    if (selectedString < strings.size() - 1) {
                        selectedString++;
                        if (selectedString - vScroller.getValue() == getHeight() - 1) {
                            vScroller.increment();
                        }
                    }
                } else {
                    selectedString = 0;
                }
            }
            if (selectedString >= 0) {
                dispatchMove();
            }
        } else if (keypress.equals(kbPgUp)) {
            vScroller.bigDecrement();
            if (selectedString >= 0) {
                selectedString -= getHeight() - 1;
                if (selectedString < 0) {
                    selectedString = 0;
                }
            }
            if (selectedString >= 0) {
                dispatchMove();
            }
        } else if (keypress.equals(kbPgDn)) {
            vScroller.bigIncrement();
            if (selectedString >= 0) {
                selectedString += getHeight() - 1;
                if (selectedString > strings.size() - 1) {
                    selectedString = strings.size() - 1;
                }
            }
            if (selectedString >= 0) {
                dispatchMove();
            }
        } else if (keypress.equals(kbHome)) {
            vScroller.toTop();
            if (strings.size() > 0) {
                selectedString = 0;
            }
            if (selectedString >= 0) {
                dispatchMove();
            }
        } else if (keypress.equals(kbEnd)) {
            vScroller.toBottom();
            if (strings.size() > 0) {
                selectedString = strings.size() - 1;
            }
            if (selectedString >= 0) {
                dispatchMove();
            }
        } else if (keypress.equals(kbTab)) {
            getParent().switchWidget(true);
        } else if (keypress.equals(kbShiftTab) || keypress.equals(kbBackTab)) {
            getParent().switchWidget(false);
        } else if (keypress.equals(kbEnter)) {
            if (selectedString >= 0) {
                dispatchEnter();
            }
        } else {
            // Pass other keys (tab etc.) on
            super.onKeypress(keypress);
        }
    }

}
