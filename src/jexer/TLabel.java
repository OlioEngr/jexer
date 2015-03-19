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

/**
 * TLabel implements a simple label.
 */
public final class TLabel extends TWidget {

    /**
     * Label text.
     */
    private String text = "";

    /**
     * Get label text.
     *
     * @return label text
     */
    public String getText() {
        return text;
    }

    /**
     * Set label text.
     *
     * @param text new label text
     */
    public void setText(final String text) {
        this.text = text;
    }

    /**
     * Label color.
     */
    private String colorKey;

    /**
     * Public constructor, using the default "tlabel" for colorKey.
     *
     * @param parent parent widget
     * @param text label on the screen
     * @param x column relative to parent
     * @param y row relative to parent
     */
    public TLabel(final TWidget parent, final String text, final int x,
        final int y) {

        this(parent, text, x, y, "tlabel");
    }

    /**
     * Public constructor.
     *
     * @param parent parent widget
     * @param text label on the screen
     * @param x column relative to parent
     * @param y row relative to parent
     * @param colorKey ColorTheme key color to use for foreground text
     */
    public TLabel(final TWidget parent, final String text, final int x,
        final int y, final String colorKey) {

        // Set parent and window
        super(parent, false, x, y, text.length(), 1);

        this.text = text;
        this.colorKey = colorKey;
    }

    /**
     * Draw a static label.
     */
    @Override public void draw() {
        // Setup my color
        CellAttributes color = new CellAttributes();
        color.setTo(getTheme().getColor(colorKey));
        CellAttributes background = getWindow().getBackground();
        color.setBackColor(background.getBackColor());

        getScreen().putStrXY(0, 0, text, color);
    }

}
