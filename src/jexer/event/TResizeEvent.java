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
 * This class encapsulates a screen or window resize event.
 */
public class TResizeEvent extends TInputEvent {

    /**
     * Resize events can be generated for either a total screen resize or a
     * widget/window resize.
     */
    public enum Type {
        /**
         * The entire screen size changed.
         */
        SCREEN,

        /**
         * A widget was resized.
         */
        WIDGET
    }

    /**
     * The type of resize.
     */
    private Type type;

    /**
     * Get resize type.
     *
     * @return SCREEN or WIDGET
     */
    public final Type getType() {
        return type;
    }

    /**
     * New width.
     */
    private int width;

    /**
     * Get the new width.
     *
     * @return width
     */
    public final int getWidth() {
        return width;
    }

    /**
     * New height.
     */
    private int height;

    /**
     * Get the new height.
     *
     * @return height
     */
    public final int getHeight() {
        return width;
    }

    /**
     * Public contructor.
     *
     * @param type the Type of resize, Screen or Widget
     * @param width the new width
     * @param height the new height
     */
    public TResizeEvent(final Type type, final int width, final int height) {
        this.type   = type;
        this.width  = width;
        this.height = height;
    }

    /**
     * Make human-readable description of this TResizeEvent.
     *
     * @return displayable String
     */
    @Override
    public final String toString() {
        return String.format("Resize: %s width = %d height = %d",
            type, width, height);
    }

}
