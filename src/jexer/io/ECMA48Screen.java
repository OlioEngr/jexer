/**
 * Jexer - Java Text User Interface
 *
 * Version: $Id$
 *
 * Author: Kevin Lamonte, <a href="mailto:kevin.lamonte@gmail.com">kevin.lamonte@gmail.com</a>
 *
 * License: LGPLv3 or later
 *
 * Copyright: This module is licensed under the GNU Lesser General
 * Public License Version 3.  Please see the file "COPYING" in this
 * directory for more information about the GNU Lesser General Public
 * License Version 3.
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
 */
package jexer.io;

import jexer.bits.Cell;
import jexer.bits.CellAttributes;

/**
 * This Screen class draws to an xterm/ANSI X3.64/ECMA-48 type terminal.
 */
public class ECMA48Screen extends Screen {

    /**
     * We call terminal.cursor() so need the instance
     */
    private ECMA48Terminal terminal;

    /**
     * Public constructor
     *
     * @param terminal ECMA48Terminal to use
     */
    public ECMA48Screen(ECMA48Terminal terminal) {
	this.terminal = terminal;

	// Query the screen size
	setDimensions(terminal.session.getWindowWidth(),
	    terminal.session.getWindowHeight());
    }

    /**
     * Perform a somewhat-optimal rendering of a line
     *
     * @param y row coordinate.  0 is the top-most row.
     * @param sb StringBuilder to write escape sequences to
     * @param lastAttr cell attributes from the last call to flushLine
     */
    private void flushLine(int y, StringBuilder sb, CellAttributes lastAttr) {

	int lastX = -1;
	int textEnd = 0;
	for (int x = 0; x < width; x++) {
	    Cell lCell = logical[x][y];
	    if (!lCell.isBlank()) {
		textEnd = x;
	    }
	}
	// Push textEnd to first column beyond the text area
	textEnd++;

	// DEBUG
	// reallyCleared = true;

	for (int x = 0; x < width; x++) {
	    Cell lCell = logical[x][y];
	    Cell pCell = physical[x][y];

	    if ((lCell != pCell) || (reallyCleared == true)) {

		if (debugToStderr) {
		    System.err.printf("\n--\n");
		    System.err.printf(" Y: %d X: %d\n", y, x);
		    System.err.printf("   lCell: %s\n", lCell);
		    System.err.printf("   pCell: %s\n", pCell);
		    System.err.printf("    ====    \n");
		}

		if (lastAttr == null) {
		    lastAttr = new CellAttributes();
		    sb.append(terminal.normal());
		}

		// Place the cell
		if ((lastX != (x - 1)) || (lastX == -1)) {
		    // Advancing at least one cell, or the first gotoXY
		    sb.append(terminal.gotoXY(x, y));
		}

		assert(lastAttr != null);

		if ((x == textEnd) && (textEnd < width - 1)) {
		    assert(lCell.isBlank());

		    for (int i = x; i < width; i++) {
			assert(logical[i][y].isBlank());
			// Physical is always updatesd
			physical[i][y].reset();
		    }

		    // Clear remaining line
		    sb.append(terminal.clearRemainingLine());
		    lastAttr.reset();
		    return;
		}

		// Now emit only the modified attributes
		if ((lCell.foreColor != lastAttr.foreColor) &&
		    (lCell.backColor != lastAttr.backColor) &&
		    (lCell.bold == lastAttr.bold) &&
		    (lCell.reverse == lastAttr.reverse) &&
		    (lCell.underline == lastAttr.underline) &&
		    (lCell.blink == lastAttr.blink)) {

		    // Both colors changed, attributes the same
		    sb.append(terminal.color(lCell.foreColor,
			    lCell.backColor));

		    if (debugToStderr) {
			System.err.printf("1 Change only fore/back colors\n");
		    }
		} else if ((lCell.foreColor != lastAttr.foreColor) &&
		    (lCell.backColor != lastAttr.backColor) &&
		    (lCell.bold != lastAttr.bold) &&
		    (lCell.reverse != lastAttr.reverse) &&
		    (lCell.underline != lastAttr.underline) &&
		    (lCell.blink != lastAttr.blink)) {

		    if (debugToStderr) {
			System.err.printf("2 Set all attributes\n");
		    }

		    // Everything is different
		    sb.append(terminal.color(lCell.foreColor,
			    lCell.backColor,
			    lCell.bold, lCell.reverse, lCell.blink,
			    lCell.underline));

		} else if ((lCell.foreColor != lastAttr.foreColor) &&
		    (lCell.backColor == lastAttr.backColor) &&
		    (lCell.bold == lastAttr.bold) &&
		    (lCell.reverse == lastAttr.reverse) &&
		    (lCell.underline == lastAttr.underline) &&
		    (lCell.blink == lastAttr.blink)) {

		    // Attributes same, foreColor different
		    sb.append(terminal.color(lCell.foreColor, true));

		    if (debugToStderr) {
			System.err.printf("3 Change foreColor\n");
		    }

		} else if ((lCell.foreColor == lastAttr.foreColor) &&
		    (lCell.backColor != lastAttr.backColor) &&
		    (lCell.bold == lastAttr.bold) &&
		    (lCell.reverse == lastAttr.reverse) &&
		    (lCell.underline == lastAttr.underline) &&
		    (lCell.blink == lastAttr.blink)) {

		    // Attributes same, backColor different
		    sb.append(terminal.color(lCell.backColor, false));

		    if (debugToStderr) {
			System.err.printf("4 Change backColor\n");
		    }

		} else if ((lCell.foreColor == lastAttr.foreColor) &&
		    (lCell.backColor == lastAttr.backColor) &&
		    (lCell.bold == lastAttr.bold) &&
		    (lCell.reverse == lastAttr.reverse) &&
		    (lCell.underline == lastAttr.underline) &&
		    (lCell.blink == lastAttr.blink)) {

		    // All attributes the same, just print the char
		    // NOP

		    if (debugToStderr) {
			System.err.printf("5 Only emit character\n");
		    }
		} else {
		    // Just reset everything again
		    sb.append(terminal.color(lCell.foreColor, lCell.backColor,
			    lCell.bold, lCell.reverse, lCell.blink,
			    lCell.underline));

		    if (debugToStderr) {
			System.err.printf("6 Change all attributes\n");
		    }
		}
		// Emit the character
		sb.append(lCell.ch);

		// Save the last rendered cell
		lastX = x;
		lastAttr.setTo(lCell);

		// Physical is always updatesd
		physical[x][y].setTo(lCell);

	    } // if ((lCell != pCell) || (reallyCleared == true))

	} // for (int x = 0; x < width; x++)
    }

    /**
     * Render the screen to a string that can be emitted to something that
     * knows how to process ECMA-48/ANSI X3.64 escape sequences.
     *
     * @return escape sequences string that provides the updates to the
     * physical screen
     */
    public String flushString() {
	if (dirty == false) {
	    assert(reallyCleared == false);
	    return "";
	}

	CellAttributes attr = null;

	StringBuilder sb = new StringBuilder();
	if (reallyCleared == true) {
	    attr = new CellAttributes();
	    sb.append(terminal.clearAll());
	}

	for (int y = 0; y < height; y++) {
	    flushLine(y, sb, attr);
	}

	dirty = false;
	reallyCleared = false;

	String result = sb.toString();
	if (debugToStderr) {
	    System.err.printf("flushString(): %s\n", result);
	}
	return result;
    }

    /**
     * Push the logical screen to the physical device.
     */
    @Override
    public void flushPhysical() {
	String result = flushString();
	if ((cursorVisible) &&
	    (cursorY <= height - 1) &&
	    (cursorX <= width - 1)
	) {
	    result += terminal.cursor(true);
	    result += terminal.gotoXY(cursorX, cursorY);
	} else {
	    result += terminal.cursor(false);
	}
	terminal.getOutput().write(result);
	terminal.flush();
    }
}
