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
import jexer.bits.GraphicsChars;
import jexer.event.TMouseEvent;

/**
 * THScroller implements a simple horizontal scroll bar.
 */
public final class THScroller extends TWidget {

    /**
     * Value that corresponds to being on the left edge of the scroll bar.
     */
    private int leftValue = 0;

    /**
     * Get the value that corresponds to being on the left edge of the scroll
     * bar.
     *
     * @return the scroll value
     */
    public int getLeftValue() {
        return leftValue;
    }

    /**
     * Set the value that corresponds to being on the left edge of the
     * scroll bar.
     *
     * @param leftValue the new scroll value
     */
    public void setLeftValue(final int leftValue) {
        this.leftValue = leftValue;
    }

    /**
     * Value that corresponds to being on the right edge of the scroll bar.
     */
    private int rightValue = 100;

    /**
     * Get the value that corresponds to being on the right edge of the
     * scroll bar.
     *
     * @return the scroll value
     */
    public int getRightValue() {
        return rightValue;
    }

    /**
     * Set the value that corresponds to being on the right edge of the
     * scroll bar.
     *
     * @param rightValue the new scroll value
     */
    public void setRightValue(final int rightValue) {
        this.rightValue = rightValue;
    }

    /**
     * Current value of the scroll.
     */
    private int value = 0;

    /**
     * Get current value of the scroll.
     *
     * @return the scroll value
     */
    public int getValue() {
        return value;
    }

    /**
     * Set current value of the scroll.
     *
     * @param value the new scroll value
     */
    public void setValue(final int value) {
        this.value = value;
    }

    /**
     * The increment for clicking on an arrow.
     */
    private int smallChange = 1;

    /**
     * Set the increment for clicking on an arrow.
     *
     * @param smallChange the new increment value
     */
    public void setSmallChange(final int smallChange) {
        this.smallChange = smallChange;
    }

    /**
     * The increment for clicking in the bar between the box and an arrow.
     */
    private int bigChange = 20;

    /**
     * Set the increment for clicking in the bar between the box and an
     * arrow.
     *
     * @param bigChange the new increment value
     */
    public void setBigChange(final int bigChange) {
        this.bigChange = bigChange;
    }

    /**
     * When true, the user is dragging the scroll box.
     */
    private boolean inScroll = false;

    /**
     * Public constructor.
     *
     * @param parent parent widget
     * @param x column relative to parent
     * @param y row relative to parent
     * @param width height of scroll bar
     */
    public THScroller(final TWidget parent, final int x, final int y,
        final int width) {

        // Set parent and window
        super(parent, x, y, width, 1);
    }

    /**
     * Compute the position of the scroll box (a.k.a. grip, thumb).
     *
     * @return Y position of the box, between 1 and width - 2
     */
    private int boxPosition() {
        return (getWidth() - 3) * (value - leftValue) / (rightValue - leftValue) + 1;
    }

    /**
     * Draw a horizontal scroll bar.
     */
    @Override
    public void draw() {
        CellAttributes arrowColor = getTheme().getColor("tscroller.arrows");
        CellAttributes barColor = getTheme().getColor("tscroller.bar");
        getScreen().putCharXY(0, 0, GraphicsChars.CP437[0x11], arrowColor);
        getScreen().putCharXY(getWidth() - 1, 0, GraphicsChars.CP437[0x10],
            arrowColor);

        // Place the box
        if (rightValue > leftValue) {
            getScreen().hLineXY(1, 0, getWidth() - 2, GraphicsChars.CP437[0xB1],
                barColor);
            getScreen().putCharXY(boxPosition(), 0, GraphicsChars.BOX,
                arrowColor);
        } else {
            getScreen().hLineXY(1, 0, getWidth() - 2, GraphicsChars.HATCH,
                barColor);
        }

    }

    /**
     * Perform a small step change left.
     */
    public void decrement() {
        if (leftValue == rightValue) {
            return;
        }
        value -= smallChange;
        if (value < leftValue) {
            value = leftValue;
        }
    }

    /**
     * Perform a small step change right.
     */
    public void increment() {
        if (leftValue == rightValue) {
            return;
        }
        value += smallChange;
        if (value > rightValue) {
            value = rightValue;
        }
    }

    /**
     * Handle mouse button releases.
     *
     * @param mouse mouse button release event
     */
    @Override
    public void onMouseUp(final TMouseEvent mouse) {

        if (inScroll) {
            inScroll = false;
            return;
        }

        if (rightValue == leftValue) {
            return;
        }

        if ((mouse.getX() == 0)
            && (mouse.getY() == 0)
        ) {
            // Clicked on the left arrow
            decrement();
            return;
        }

        if ((mouse.getY() == 0)
            && (mouse.getX() == getWidth() - 1)
        ) {
            // Clicked on the right arrow
            increment();
            return;
        }

        if ((mouse.getY() == 0)
            && (mouse.getX() > 0)
            && (mouse.getX() < boxPosition())
        ) {
            // Clicked between the left arrow and the box
            value -= bigChange;
            if (value < leftValue) {
                value = leftValue;
            }
            return;
        }

        if ((mouse.getY() == 0)
            && (mouse.getX() > boxPosition())
            && (mouse.getX() < getWidth() - 1)
        ) {
            // Clicked between the box and the right arrow
            value += bigChange;
            if (value > rightValue) {
                value = rightValue;
            }
            return;
        }
    }

    /**
     * Handle mouse movement events.
     *
     * @param mouse mouse motion event
     */
    @Override
    public void onMouseMotion(final TMouseEvent mouse) {

        if (rightValue == leftValue) {
            inScroll = false;
            return;
        }

        if ((mouse.isMouse1())
            && (inScroll)
            && (mouse.getX() > 0)
            && (mouse.getX() < getWidth() - 1)
        ) {
            // Recompute value based on new box position
            value = (rightValue - leftValue) * (mouse.getX()) / (getWidth() - 3) + leftValue;
            return;
        }
        inScroll = false;
    }

    /**
     * Handle mouse button press events.
     *
     * @param mouse mouse button press event
     */
    @Override
    public void onMouseDown(final TMouseEvent mouse) {
        if (rightValue == leftValue) {
            inScroll = false;
            return;
        }

        if ((mouse.getY() == 0)
            && (mouse.getX() == boxPosition())
        ) {
            inScroll = true;
            return;
        }

    }

}
