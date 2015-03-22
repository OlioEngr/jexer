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
 * TVScroller implements a simple vertical scroll bar.
 */
public final class TVScroller extends TWidget {

    /**
     * Value that corresponds to being on the top edge of the scroll bar.
     */
    private int topValue = 0;

    /**
     * Get the value that corresponds to being on the top edge of the scroll
     * bar.
     *
     * @return the scroll value
     */
    public int getTopValue() {
        return topValue;
    }

    /**
     * Set the value that corresponds to being on the top edge of the scroll
     * bar.
     *
     * @param topValue the new scroll value
     */
    public void setTopValue(final int topValue) {
        this.topValue = topValue;
    }

    /**
     * Value that corresponds to being on the bottom edge of the scroll bar.
     */
    private int bottomValue = 100;

    /**
     * Get the value that corresponds to being on the bottom edge of the
     * scroll bar.
     *
     * @return the scroll value
     */
    public int getBottomValue() {
        return bottomValue;
    }

    /**
     * Set the value that corresponds to being on the bottom edge of the
     * scroll bar.
     *
     * @param bottomValue the new scroll value
     */
    public void setBottomValue(final int bottomValue) {
        this.bottomValue = bottomValue;
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
     * @param height height of scroll bar
     */
    public TVScroller(final TWidget parent, final int x, final int y,
        final int height) {

        // Set parent and window
        super(parent, x, y, 1, height);
    }

    /**
     * Compute the position of the scroll box (a.k.a. grip, thumb).
     *
     * @return Y position of the box, between 1 and height - 2
     */
    private int boxPosition() {
        return (getHeight() - 3) * (value - topValue) / (bottomValue - topValue) + 1;
    }

    /**
     * Draw a vertical scroll bar.
     */
    @Override
    public void draw() {
        CellAttributes arrowColor = getTheme().getColor("tscroller.arrows");
        CellAttributes barColor = getTheme().getColor("tscroller.bar");
        getScreen().putCharXY(0, 0, GraphicsChars.CP437[0x1E], arrowColor);
        getScreen().putCharXY(0, getHeight() - 1, GraphicsChars.CP437[0x1F],
            arrowColor);

        // Place the box
        if (bottomValue > topValue) {
            getScreen().vLineXY(0, 1, getHeight() - 2,
                GraphicsChars.CP437[0xB1], barColor);
            getScreen().putCharXY(0, boxPosition(), GraphicsChars.BOX,
                arrowColor);
        } else {
            getScreen().vLineXY(0, 1, getHeight() - 2, GraphicsChars.HATCH,
                barColor);
        }

    }

    /**
     * Perform a small step change up.
     */
    public void decrement() {
        if (bottomValue == topValue) {
            return;
        }
        value -= smallChange;
        if (value < topValue) {
            value = topValue;
        }
    }

    /**
     * Perform a small step change down.
     */
    public void increment() {
        if (bottomValue == topValue) {
            return;
        }
        value += smallChange;
        if (value > bottomValue) {
            value = bottomValue;
        }
    }

    /**
     * Perform a big step change up.
     */
    public void bigDecrement() {
        if (bottomValue == topValue) {
            return;
        }
        value -= bigChange;
        if (value < topValue) {
            value = topValue;
        }
    }

    /**
     * Perform a big step change down.
     */
    public void bigIncrement() {
        if (bottomValue == topValue) {
            return;
        }
        value += bigChange;
        if (value > bottomValue) {
            value = bottomValue;
        }
    }

    /**
     * Go to the top edge of the scroller.
     */
    public void toTop() {
        value = topValue;
    }

    /**
     * Go to the bottom edge of the scroller.
     */
    public void toBottom() {
        value = bottomValue;
    }

    /**
     * Handle mouse button releases.
     *
     * @param mouse mouse button release event
     */
    @Override
    public void onMouseUp(final TMouseEvent mouse) {
        if (bottomValue == topValue) {
            return;
        }

        if (inScroll) {
            inScroll = false;
            return;
        }

        if ((mouse.getX() == 0)
            && (mouse.getY() == 0)
        ) {
            // Clicked on the top arrow
            decrement();
            return;
        }

        if ((mouse.getX() == 0)
            && (mouse.getY() == getHeight() - 1)
        ) {
            // Clicked on the bottom arrow
            increment();
            return;
        }

        if ((mouse.getX() == 0)
            && (mouse.getY() > 0)
            && (mouse.getY() < boxPosition())
        ) {
            // Clicked between the top arrow and the box
            value -= bigChange;
            if (value < topValue) {
                value = topValue;
            }
            return;
        }

        if ((mouse.getX() == 0)
            && (mouse.getY() > boxPosition())
            && (mouse.getY() < getHeight() - 1)
        ) {
            // Clicked between the box and the bottom arrow
            value += bigChange;
            if (value > bottomValue) {
                value = bottomValue;
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
        if (bottomValue == topValue) {
            return;
        }

        if ((mouse.isMouse1()) &&
            (inScroll) &&
            (mouse.getY() > 0) &&
            (mouse.getY() < getHeight() - 1)
        ) {
            // Recompute value based on new box position
            value = (bottomValue - topValue) * (mouse.getY()) / (getHeight() - 3) + topValue;
            return;
        }

        inScroll = false;
    }

    /**
     * Handle mouse press events.
     *
     * @param mouse mouse button press event
     */
    @Override
    public void onMouseDown(final TMouseEvent mouse) {
        if (bottomValue == topValue) {
            return;
        }

        if ((mouse.getX() == 0)
            && (mouse.getY() == boxPosition())
        ) {
            inScroll = true;
            return;
        }
    }

}
