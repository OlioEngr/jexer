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

import java.util.Date;

/**
 * TTimer implements a simple timer.
 */
public final class TTimer {

    /**
     * If true, re-schedule after every tick.  Note package private access.
     */
    boolean recurring = false;

    /**
     * Duration (in millis) between ticks if this is a recurring timer.
     */
    private long duration = 0;

    /**
     * The next time this timer needs to be ticked.
     */
    private Date nextTick;

    /**
     * Get the next time this timer needs to be ticked.  Note package private
     * access.
     *
     * @return time at which action should be called
     */
    Date getNextTick() {
        return nextTick;
    }

    /**
     * The action to perfom on a tick.
     */
    private TAction action;

    /**
     * Tick this timer.  Note package private access.
     */
    void tick() {
        if (action != null) {
            action.DO();
        }
        // Set next tick
        Date ticked = new Date();
        if (recurring) {
            nextTick = new Date(ticked.getTime() + duration);
        }
    }

    /**
     * Get the number of milliseconds between now and the next tick time.
     *
     * @return number of millis
     */
    public long getMillis() {
        return nextTick.getTime() - (new Date()).getTime();
    }

    /**
     * Package private constructor.
     *
     * @param duration number of milliseconds to wait between ticks
     * @param recurring if true, re-schedule this timer after every tick
     * @param action to perform on next tick
     */
    TTimer(final long duration, final boolean recurring, final TAction action) {

        this.recurring = recurring;
        this.duration  = duration;
        this.action    = action;

        nextTick = new Date((new Date()).getTime() + duration);
    }

}
