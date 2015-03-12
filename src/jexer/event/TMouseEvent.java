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

/**
 * This class encapsulates several kinds of mouse input events.  Note that
 * the relative (x,y) ARE MUTABLE: TWidget's onMouse() handlers perform that
 * update during event dispatching.
 */
public final class TMouseEvent extends TInputEvent {

    /**
     * The type of event generated.
     */
    public enum Type {
        /**
         * Mouse motion.  X and Y will have screen coordinates.
         */
        MOUSE_MOTION,

        /**
         * Mouse button down.  X and Y will have screen coordinates.
         */
        MOUSE_DOWN,

        /**
         * Mouse button up.  X and Y will have screen coordinates.
         */
        MOUSE_UP
    }

    /**
     * Type of event, one of MOUSE_MOTION, MOUSE_UP, or MOUSE_DOWN, or
     * KEYPRESS.
     */
    private Type type;

    /**
     * Get type.
     *
     * @return type
     */
    public Type getType() {
        return type;
    }

    /**
     * Mouse X - relative coordinates.
     */
    private int x;

    /**
     * Get x.
     *
     * @return x
     */
    public int getX() {
        return x;
    }

    /**
     * Set x.
     *
     * @param x new relative X value
     * @see jexer.TWidget#onMouseDown(TMouseEvent mouse)
     * @see jexer.TWidget#onMouseDown(TMouseEvent mouse)
     * @see jexer.TWidget#onMouseMotion(TMouseEvent mouse)
     */
    public void setX(final int x) {
        this.x = x;
    }

    /**
     * Mouse Y - relative coordinates.
     */
    private int y;

    /**
     * Get y.
     *
     * @return y
     */
    public int getY() {
        return y;
    }

    /**
     * Set y.
     *
     * @param y new relative Y value
     * @see jexer.TWidget#onMouseDown(TMouseEvent mouse)
     * @see jexer.TWidget#onMouseDown(TMouseEvent mouse)
     * @see jexer.TWidget#onMouseMotion(TMouseEvent mouse)
     */
    public void setY(final int y) {
        this.y = y;
    }

    /**
     * Mouse X - absolute screen coordinates.
     */
    private int absoluteX;

    /**
     * Get absoluteX.
     *
     * @return absoluteX
     */
    public int getAbsoluteX() {
        return absoluteX;
    }

    /**
     * Mouse Y - absolute screen coordinate.
     */
    private int absoluteY;

    /**
     * Get absoluteY.
     *
     * @return absoluteY
     */
    public int getAbsoluteY() {
        return absoluteY;
    }

    /**
     * Mouse button 1 (left button).
     */
    private boolean mouse1;

    /**
     * Get mouse1.
     *
     * @return mouse1
     */
    public boolean getMouse1() {
        return mouse1;
    }

    /**
     * Mouse button 2 (right button).
     */
    private boolean mouse2;

    /**
     * Get mouse2.
     *
     * @return mouse2
     */
    public boolean getMouse2() {
        return mouse2;
    }

    /**
     * Mouse button 3 (middle button).
     */
    private boolean mouse3;

    /**
     * Get mouse3.
     *
     * @return mouse3
     */
    public boolean getMouse3() {
        return mouse3;
    }

    /**
     * Mouse wheel UP (button 4).
     */
    private boolean mouseWheelUp;

    /**
     * Get mouseWheelUp.
     *
     * @return mouseWheelUp
     */
    public boolean getMouseWheelUp() {
        return mouseWheelUp;
    }

    /**
     * Mouse wheel DOWN (button 5).
     */
    private boolean mouseWheelDown;

    /**
     * Get mouseWheelDown.
     *
     * @return mouseWheelDown
     */
    public boolean getMouseWheelDown() {
        return mouseWheelDown;
    }

    /**
     * Public contructor.
     *
     * @param type the type of event, MOUSE_MOTION, MOUSE_DOWN, or MOUSE_UP
     * @param x relative column
     * @param y relative row
     * @param absoluteX absolute column
     * @param absoluteY absolute row
     * @param mouse1 if true, left button is down
     * @param mouse2 if true, right button is down
     * @param mouse3 if true, middle button is down
     * @param mouseWheelUp if true, mouse wheel (button 4) is down
     * @param mouseWheelDown if true, mouse wheel (button 5) is down
     */
    public TMouseEvent(final Type type, final int x, final int y,
        final int absoluteX, final int absoluteY,
        final boolean mouse1, final boolean mouse2, final boolean mouse3,
        final boolean mouseWheelUp, final boolean mouseWheelDown) {

        this.type               = type;
        this.x                  = x;
        this.y                  = y;
        this.absoluteX          = absoluteX;
        this.absoluteY          = absoluteY;
        this.mouse1             = mouse1;
        this.mouse2             = mouse2;
        this.mouse3             = mouse3;
        this.mouseWheelUp       = mouseWheelUp;
        this.mouseWheelDown     = mouseWheelDown;
    }

    /**
     * Make human-readable description of this TMouseEvent.
     *
     * @return displayable String
     */
    @Override
    public String toString() {
        return String.format("Mouse: %s x %d y %d absoluteX %d absoluteY %d 1 %s 2 %s 3 %s DOWN %s UP %s",
            type,
            x, y,
            absoluteX, absoluteY,
            mouse1,
            mouse2,
            mouse3,
            mouseWheelUp,
            mouseWheelDown);
    }

}
