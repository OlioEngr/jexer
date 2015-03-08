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
package jexer.bits;

import java.util.HashMap;
import java.util.Map;


/**
 * ColorTheme is a collection of colors keyed by string.
 */
public class ColorTheme {

    /**
     * The current theme colors
     */
    private Map<String, CellAttributes> colors;

    /**
     * Retrieve the CellAttributes by name.
     *
     * @param name hash key
     * @return color associated with hash key
     */
    public CellAttributes getColor(String name) {
	CellAttributes attr = (CellAttributes)colors.get(name);
	return attr;
    }

    /**
     * Save the colors to an ASCII file
     *
     * @param filename file to write to
     */
    public void save(String filename) {
	/*
	auto file = File(filename, "wt");
	foreach (string key; colors.keys.sort) {
	    CellAttributes color = colors[key];
	    file.writefln("%s = %s", key, color);
	}
	 */
    }

    /**
     * Read colors from an ASCII file
     *
     * @param filename file to read from
     */
    public void load(String filename) {
	/*
	string text = std.file.readText!(string)(filename);
	foreach (line; std.string.splitLines!(string)(text)) {
	    string key;
	    string bold;
	    string foreColor;
	    string on;
	    string backColor;
	    auto tokenCount = formattedRead(line, "%s = %s %s %s %s",
		&key, &bold, &foreColor, &on, &backColor);
	    if (tokenCount == 4) {
		std.stdio.stderr.writefln("1 %s = %s %s %s %s",
		    key, bold, foreColor, on, backColor);

		// "key = blah on blah"
		foreColor = bold;
		backColor = on;
		bold = "";
	    } else if (tokenCount == 5) {
		// "key = bold blah on blah"
		std.stdio.stderr.writefln("2 %s = %s %s %s %s",
		    key, bold, foreColor, on, backColor);
	    } else {
		// Unknown line, skip this one
		continue;
	    }
	    CellAttributes color = new CellAttributes();
	    if (bold == "bold") {
		color.bold = true;
	    }
	    color.foreColor = CellAttributes.colorFromString(foreColor);
	    color.backColor = CellAttributes.colorFromString(backColor);
	    colors[key] = color;
	}
	 */
    }

    /// Sets to defaults that resemble the Borland IDE colors.
    public void setDefaultTheme() {
	CellAttributes color;

	// TWindow border
	color = new CellAttributes();
	color.foreColor = Color.WHITE;
	color.backColor = Color.BLUE;
	color.bold = true;
	colors.put("twindow.border", color);

	// TWindow background
	color = new CellAttributes();
	color.foreColor = Color.YELLOW;
	color.backColor = Color.BLUE;
	color.bold = true;
	colors.put("twindow.background", color);

	// TWindow border - inactive
	color = new CellAttributes();
	color.foreColor = Color.BLACK;
	color.backColor = Color.BLUE;
	color.bold = true;
	colors.put("twindow.border.inactive", color);

	// TWindow background - inactive
	color = new CellAttributes();
	color.foreColor = Color.YELLOW;
	color.backColor = Color.BLUE;
	color.bold = true;
	colors.put("twindow.background.inactive", color);

	// TWindow border - modal
	color = new CellAttributes();
	color.foreColor = Color.WHITE;
	color.backColor = Color.WHITE;
	color.bold = true;
	colors.put("twindow.border.modal", color);

	// TWindow background - modal
	color = new CellAttributes();
	color.foreColor = Color.BLACK;
	color.backColor = Color.WHITE;
	color.bold = false;
	colors.put("twindow.background.modal", color);

	// TWindow border - modal + inactive
	color = new CellAttributes();
	color.foreColor = Color.BLACK;
	color.backColor = Color.WHITE;
	color.bold = true;
	colors.put("twindow.border.modal.inactive", color);

	// TWindow background - modal + inactive
	color = new CellAttributes();
	color.foreColor = Color.BLACK;
	color.backColor = Color.WHITE;
	color.bold = false;
	colors.put("twindow.background.modal.inactive", color);

	// TWindow border - during window movement - modal
	color = new CellAttributes();
	color.foreColor = Color.GREEN;
	color.backColor = Color.WHITE;
	color.bold = true;
	colors.put("twindow.border.modal.windowmove", color);

	// TWindow border - during window movement
	color = new CellAttributes();
	color.foreColor = Color.GREEN;
	color.backColor = Color.BLUE;
	color.bold = true;
	colors.put("twindow.border.windowmove", color);

	// TWindow background - during window movement
	color = new CellAttributes();
	color.foreColor = Color.YELLOW;
	color.backColor = Color.BLUE;
	color.bold = false;
	colors.put("twindow.background.windowmove", color);

	// TApplication background
	color = new CellAttributes();
	color.foreColor = Color.BLUE;
	color.backColor = Color.WHITE;
	color.bold = false;
	colors.put("tapplication.background", color);

	// TButton text
	color = new CellAttributes();
	color.foreColor = Color.BLACK;
	color.backColor = Color.GREEN;
	color.bold = false;
	colors.put("tbutton.inactive", color);
	color = new CellAttributes();
	color.foreColor = Color.WHITE;
	color.backColor = Color.GREEN;
	color.bold = true;
	colors.put("tbutton.active", color);
	color = new CellAttributes();
	color.foreColor = Color.BLACK;
	color.backColor = Color.WHITE;
	color.bold = true;
	colors.put("tbutton.disabled", color);
	color = new CellAttributes();
	color.foreColor = Color.YELLOW;
	color.backColor = Color.GREEN;
	color.bold = true;
	colors.put("tbutton.mnemonic", color);
	color = new CellAttributes();
	color.foreColor = Color.YELLOW;
	color.backColor = Color.GREEN;
	color.bold = true;
	colors.put("tbutton.mnemonic.highlighted", color);

	// TLabel text
	color = new CellAttributes();
	color.foreColor = Color.WHITE;
	color.backColor = Color.BLUE;
	color.bold = true;
	colors.put("tlabel", color);

	// TText text
	color = new CellAttributes();
	color.foreColor = Color.WHITE;
	color.backColor = Color.BLACK;
	color.bold = false;
	colors.put("ttext", color);

	// TField text
	color = new CellAttributes();
	color.foreColor = Color.WHITE;
	color.backColor = Color.BLUE;
	color.bold = false;
	colors.put("tfield.inactive", color);
	color = new CellAttributes();
	color.foreColor = Color.YELLOW;
	color.backColor = Color.BLACK;
	color.bold = true;
	colors.put("tfield.active", color);

	// TCheckbox
	color = new CellAttributes();
	color.foreColor = Color.WHITE;
	color.backColor = Color.BLUE;
	color.bold = false;
	colors.put("tcheckbox.inactive", color);
	color = new CellAttributes();
	color.foreColor = Color.YELLOW;
	color.backColor = Color.BLACK;
	color.bold = true;
	colors.put("tcheckbox.active", color);


	// TRadioButton
	color = new CellAttributes();
	color.foreColor = Color.WHITE;
	color.backColor = Color.BLUE;
	color.bold = false;
	colors.put("tradiobutton.inactive", color);
	color = new CellAttributes();
	color.foreColor = Color.YELLOW;
	color.backColor = Color.BLACK;
	color.bold = true;
	colors.put("tradiobutton.active", color);

	// TRadioGroup
	color = new CellAttributes();
	color.foreColor = Color.WHITE;
	color.backColor = Color.BLUE;
	color.bold = false;
	colors.put("tradiogroup.inactive", color);
	color = new CellAttributes();
	color.foreColor = Color.YELLOW;
	color.backColor = Color.BLUE;
	color.bold = true;
	colors.put("tradiogroup.active", color);

	// TMenu
	color = new CellAttributes();
	color.foreColor = Color.BLACK;
	color.backColor = Color.WHITE;
	color.bold = false;
	colors.put("tmenu", color);
	color = new CellAttributes();
	color.foreColor = Color.BLACK;
	color.backColor = Color.GREEN;
	color.bold = false;
	colors.put("tmenu.highlighted", color);
	color = new CellAttributes();
	color.foreColor = Color.RED;
	color.backColor = Color.WHITE;
	color.bold = false;
	colors.put("tmenu.mnemonic", color);
	color = new CellAttributes();
	color.foreColor = Color.RED;
	color.backColor = Color.GREEN;
	color.bold = false;
	colors.put("tmenu.mnemonic.highlighted", color);
	color = new CellAttributes();
	color.foreColor = Color.BLACK;
	color.backColor = Color.WHITE;
	color.bold = true;
	colors.put("tmenu.disabled", color);

	// TProgressBar
	color = new CellAttributes();
	color.foreColor = Color.BLUE;
	color.backColor = Color.BLUE;
	color.bold = true;
	colors.put("tprogressbar.complete", color);
	color = new CellAttributes();
	color.foreColor = Color.WHITE;
	color.backColor = Color.BLUE;
	color.bold = false;
	colors.put("tprogressbar.incomplete", color);

	// THScroller / TVScroller
	color = new CellAttributes();
	color.foreColor = Color.CYAN;
	color.backColor = Color.BLUE;
	color.bold = false;
	colors.put("tscroller.bar", color);
	color = new CellAttributes();
	color.foreColor = Color.BLUE;
	color.backColor = Color.CYAN;
	color.bold = false;
	colors.put("tscroller.arrows", color);

	// TTreeView
	color = new CellAttributes();
	color.foreColor = Color.WHITE;
	color.backColor = Color.BLUE;
	color.bold = false;
	colors.put("ttreeview", color);
	color = new CellAttributes();
	color.foreColor = Color.GREEN;
	color.backColor = Color.BLUE;
	color.bold = true;
	colors.put("ttreeview.expandbutton", color);
	color = new CellAttributes();
	color.foreColor = Color.BLACK;
	color.backColor = Color.CYAN;
	color.bold = false;
	colors.put("ttreeview.selected", color);
	color = new CellAttributes();
	color.foreColor = Color.RED;
	color.backColor = Color.BLUE;
	color.bold = false;
	colors.put("ttreeview.unreadable", color);
	color = new CellAttributes();
	color.foreColor = Color.BLACK;
	color.backColor = Color.BLUE;
	color.bold = true;
	colors.put("ttreeview.inactive", color);

	// TText text
	color = new CellAttributes();
	color.foreColor = Color.WHITE;
	color.backColor = Color.BLUE;
	color.bold = false;
	colors.put("tdirectorylist", color);
	color = new CellAttributes();
	color.foreColor = Color.BLACK;
	color.backColor = Color.CYAN;
	color.bold = false;
	colors.put("tdirectorylist.selected", color);
	color = new CellAttributes();
	color.foreColor = Color.BLACK;
	color.backColor = Color.CYAN;
	color.bold = false;
	colors.put("tdirectorylist.unreadable", color);
	color = new CellAttributes();
	color.foreColor = Color.BLACK;
	color.backColor = Color.BLUE;
	color.bold = true;
	colors.put("tdirectorylist.inactive", color);

	// TEditor
	color = new CellAttributes();
	color.foreColor = Color.WHITE;
	color.backColor = Color.BLACK;
	color.bold = false;
	colors.put("teditor", color);


    }

    /**
     * Public constructor.
     */
    public ColorTheme() {
	setDefaultTheme();
    }
}

