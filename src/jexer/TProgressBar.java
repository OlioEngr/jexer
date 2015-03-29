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

import jexer.bits.CellAttributes;
import jexer.bits.GraphicsChars;

/**
 * TProgressBar implements a simple progress bar.
 */
public final class TProgressBar extends TWidget {

    /**
     * Value that corresponds to 0% progress.
     */
    private int minValue = 0;

    /**
     * Get the value that corresponds to 0% progress.
     *
     * @return the value that corresponds to 0% progress
     */
    public int getMinValue() {
        return minValue;
    }

    /**
     * Set the value that corresponds to 0% progress.
     *
     * @param minValue the value that corresponds to 0% progress
     */
    public void setMinValue(final int minValue) {
        this.minValue = minValue;
    }

    /**
     * Value that corresponds to 100% progress.
     */
    private int maxValue = 100;

    /**
     * Get the value that corresponds to 100% progress.
     *
     * @return the value that corresponds to 100% progress
     */
    public int getMaxValue() {
        return maxValue;
    }

    /**
     * Set the value that corresponds to 100% progress.
     *
     * @param maxValue the value that corresponds to 100% progress
     */
    public void setMaxValue(final int maxValue) {
        this.maxValue = maxValue;
    }

    /**
     * Current value of the progress.
     */
    private int value = 0;

    /**
     * Get the current value of the progress.
     *
     * @return the current value of the progress
     */
    public int getValue() {
        return value;
    }

    /**
     * Set the current value of the progress.
     *
     * @param value the current value of the progress
     */
    public void setValue(final int value) {
        this.value = value;
    }

    /**
     * Public constructor.
     *
     * @param parent parent widget
     * @param x column relative to parent
     * @param y row relative to parent
     * @param width width of progress bar
     * @param value initial value of percent complete
     */
    public TProgressBar(final TWidget parent, final int x, final int y,
        final int width, final int value) {

        // Set parent and window
        super(parent, false, x, y, width, 1);

        this.value = value;
    }

    /**
     * Draw a static progress bar.
     */
    @Override
    public void draw() {
        CellAttributes completeColor = getTheme().getColor("tprogressbar.complete");
        CellAttributes incompleteColor = getTheme().getColor("tprogressbar.incomplete");

        float progress = ((float)value - minValue) / ((float)maxValue - minValue);
        int progressInt = (int)(progress * 100);
        int progressUnit = 100 / (getWidth() - 2);

        getScreen().putCharXY(0, 0, GraphicsChars.CP437[0xC3], incompleteColor);
        for (int i = 0; i < getWidth() - 2; i++) {
            float iProgress = (float)i / (getWidth() - 2);
            int iProgressInt = (int)(iProgress * 100);
            if (iProgressInt <= progressInt - progressUnit) {
                getScreen().putCharXY(i + 1, 0, GraphicsChars.BOX,
                    completeColor);
            } else {
                getScreen().putCharXY(i + 1, 0, GraphicsChars.SINGLE_BAR,
                    incompleteColor);
            }
        }
        if (value >= maxValue) {
            getScreen().putCharXY(getWidth() - 2, 0, GraphicsChars.BOX,
                completeColor);
        } else {
            getScreen().putCharXY(getWidth() - 2, 0, GraphicsChars.SINGLE_BAR,
                incompleteColor);
        }
        getScreen().putCharXY(getWidth() - 1, 0, GraphicsChars.CP437[0xB4],
            incompleteColor);
    }

}
