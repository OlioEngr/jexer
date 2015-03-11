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
package jexer.bits;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

/**
 * ColorTheme is a collection of colors keyed by string.  A default theme is
 * also provided that matches the blue-and-white theme used by Turbo Vision.
 */
public final class ColorTheme {

    /**
     * The current theme colors.
     */
    private SortedMap<String, CellAttributes> colors;

    /**
     * Public constructor sets the theme to the default.
     */
    public ColorTheme() {
        colors = new TreeMap<String, CellAttributes>();
        setDefaultTheme();
    }

    /**
     * Retrieve the CellAttributes for a named theme color.
     *
     * @param name theme color name, e.g. "twindow.border"
     * @return color associated with name, e.g. bold yellow on blue
     */
    public CellAttributes getColor(final String name) {
        CellAttributes attr = (CellAttributes) colors.get(name);
        return attr;
    }

    /**
     * Save the color theme mappings to an ASCII file.
     *
     * @param filename file to write to
     * @throws IOException if the I/O fails
     */
    public void save(final String filename) throws IOException {
        FileWriter file = new FileWriter(filename);
        for (String key: colors.keySet()) {
            CellAttributes color = getColor(key);
            file.write(String.format("%s = %s\n", key, color));
        }
        file.close();
    }

    /**
     * Read color theme mappings from an ASCII file.
     *
     * @param filename file to read from
     * @throws IOException if the I/O fails
     */
    public void load(final String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line = reader.readLine();
        for (; line != null; line = reader.readLine()) {
            String key;
            String bold;
            String foreColor;
            String backColor;

            // Look for lines that resemble:
            //     "key = blah on blah"
            //     "key = bold blah on blah"
            StringTokenizer tokenizer = new StringTokenizer(line);
            key = tokenizer.nextToken();
            if (!tokenizer.nextToken().equals("=")) {
                // Skip this line
                continue;
            }
            bold = tokenizer.nextToken();
            if (!bold.toLowerCase().equals("bold")) {
                // "key = blah on blah"
                foreColor = bold;
            } else {
                // "key = bold blah on blah"
                foreColor = tokenizer.nextToken().toLowerCase();
            }
            if (!tokenizer.nextToken().toLowerCase().equals("on")) {
                // Skip this line
                continue;
            }
            backColor = tokenizer.nextToken().toLowerCase();

            CellAttributes color = new CellAttributes();
            if (bold.equals("bold")) {
                color.setBold(true);
            }
            color.setForeColor(Color.getColor(foreColor));
            color.setBackColor(Color.getColor(backColor));
            colors.put(key, color);
        }
    }

    /**
     * Sets to defaults that resemble the Borland IDE colors.
     */
    public void setDefaultTheme() {
        CellAttributes color;

        // TWindow border
        color = new CellAttributes();
        color.setForeColor(Color.WHITE);
        color.setBackColor(Color.BLUE);
        color.setBold(true);
        colors.put("twindow.border", color);

        // TWindow background
        color = new CellAttributes();
        color.setForeColor(Color.YELLOW);
        color.setBackColor(Color.BLUE);
        color.setBold(true);
        colors.put("twindow.background", color);

        // TWindow border - inactive
        color = new CellAttributes();
        color.setForeColor(Color.BLACK);
        color.setBackColor(Color.BLUE);
        color.setBold(true);
        colors.put("twindow.border.inactive", color);

        // TWindow background - inactive
        color = new CellAttributes();
        color.setForeColor(Color.YELLOW);
        color.setBackColor(Color.BLUE);
        color.setBold(true);
        colors.put("twindow.background.inactive", color);

        // TWindow border - modal
        color = new CellAttributes();
        color.setForeColor(Color.WHITE);
        color.setBackColor(Color.WHITE);
        color.setBold(true);
        colors.put("twindow.border.modal", color);

        // TWindow background - modal
        color = new CellAttributes();
        color.setForeColor(Color.BLACK);
        color.setBackColor(Color.WHITE);
        color.setBold(false);
        colors.put("twindow.background.modal", color);

        // TWindow border - modal + inactive
        color = new CellAttributes();
        color.setForeColor(Color.BLACK);
        color.setBackColor(Color.WHITE);
        color.setBold(true);
        colors.put("twindow.border.modal.inactive", color);

        // TWindow background - modal + inactive
        color = new CellAttributes();
        color.setForeColor(Color.BLACK);
        color.setBackColor(Color.WHITE);
        color.setBold(false);
        colors.put("twindow.background.modal.inactive", color);

        // TWindow border - during window movement - modal
        color = new CellAttributes();
        color.setForeColor(Color.GREEN);
        color.setBackColor(Color.WHITE);
        color.setBold(true);
        colors.put("twindow.border.modal.windowmove", color);

        // TWindow border - during window movement
        color = new CellAttributes();
        color.setForeColor(Color.GREEN);
        color.setBackColor(Color.BLUE);
        color.setBold(true);
        colors.put("twindow.border.windowmove", color);

        // TWindow background - during window movement
        color = new CellAttributes();
        color.setForeColor(Color.YELLOW);
        color.setBackColor(Color.BLUE);
        color.setBold(false);
        colors.put("twindow.background.windowmove", color);

        // TApplication background
        color = new CellAttributes();
        color.setForeColor(Color.BLUE);
        color.setBackColor(Color.WHITE);
        color.setBold(false);
        colors.put("tapplication.background", color);

        // TButton text
        color = new CellAttributes();
        color.setForeColor(Color.BLACK);
        color.setBackColor(Color.GREEN);
        color.setBold(false);
        colors.put("tbutton.inactive", color);
        color = new CellAttributes();
        color.setForeColor(Color.WHITE);
        color.setBackColor(Color.GREEN);
        color.setBold(true);
        colors.put("tbutton.active", color);
        color = new CellAttributes();
        color.setForeColor(Color.BLACK);
        color.setBackColor(Color.WHITE);
        color.setBold(true);
        colors.put("tbutton.disabled", color);
        color = new CellAttributes();
        color.setForeColor(Color.YELLOW);
        color.setBackColor(Color.GREEN);
        color.setBold(true);
        colors.put("tbutton.mnemonic", color);
        color = new CellAttributes();
        color.setForeColor(Color.YELLOW);
        color.setBackColor(Color.GREEN);
        color.setBold(true);
        colors.put("tbutton.mnemonic.highlighted", color);

        // TLabel text
        color = new CellAttributes();
        color.setForeColor(Color.WHITE);
        color.setBackColor(Color.BLUE);
        color.setBold(true);
        colors.put("tlabel", color);

        // TText text
        color = new CellAttributes();
        color.setForeColor(Color.WHITE);
        color.setBackColor(Color.BLACK);
        color.setBold(false);
        colors.put("ttext", color);

        // TField text
        color = new CellAttributes();
        color.setForeColor(Color.WHITE);
        color.setBackColor(Color.BLUE);
        color.setBold(false);
        colors.put("tfield.inactive", color);
        color = new CellAttributes();
        color.setForeColor(Color.YELLOW);
        color.setBackColor(Color.BLACK);
        color.setBold(true);
        colors.put("tfield.active", color);

        // TCheckbox
        color = new CellAttributes();
        color.setForeColor(Color.WHITE);
        color.setBackColor(Color.BLUE);
        color.setBold(false);
        colors.put("tcheckbox.inactive", color);
        color = new CellAttributes();
        color.setForeColor(Color.YELLOW);
        color.setBackColor(Color.BLACK);
        color.setBold(true);
        colors.put("tcheckbox.active", color);


        // TRadioButton
        color = new CellAttributes();
        color.setForeColor(Color.WHITE);
        color.setBackColor(Color.BLUE);
        color.setBold(false);
        colors.put("tradiobutton.inactive", color);
        color = new CellAttributes();
        color.setForeColor(Color.YELLOW);
        color.setBackColor(Color.BLACK);
        color.setBold(true);
        colors.put("tradiobutton.active", color);

        // TRadioGroup
        color = new CellAttributes();
        color.setForeColor(Color.WHITE);
        color.setBackColor(Color.BLUE);
        color.setBold(false);
        colors.put("tradiogroup.inactive", color);
        color = new CellAttributes();
        color.setForeColor(Color.YELLOW);
        color.setBackColor(Color.BLUE);
        color.setBold(true);
        colors.put("tradiogroup.active", color);

        // TMenu
        color = new CellAttributes();
        color.setForeColor(Color.BLACK);
        color.setBackColor(Color.WHITE);
        color.setBold(false);
        colors.put("tmenu", color);
        color = new CellAttributes();
        color.setForeColor(Color.BLACK);
        color.setBackColor(Color.GREEN);
        color.setBold(false);
        colors.put("tmenu.highlighted", color);
        color = new CellAttributes();
        color.setForeColor(Color.RED);
        color.setBackColor(Color.WHITE);
        color.setBold(false);
        colors.put("tmenu.mnemonic", color);
        color = new CellAttributes();
        color.setForeColor(Color.RED);
        color.setBackColor(Color.GREEN);
        color.setBold(false);
        colors.put("tmenu.mnemonic.highlighted", color);
        color = new CellAttributes();
        color.setForeColor(Color.BLACK);
        color.setBackColor(Color.WHITE);
        color.setBold(true);
        colors.put("tmenu.disabled", color);

        // TProgressBar
        color = new CellAttributes();
        color.setForeColor(Color.BLUE);
        color.setBackColor(Color.BLUE);
        color.setBold(true);
        colors.put("tprogressbar.complete", color);
        color = new CellAttributes();
        color.setForeColor(Color.WHITE);
        color.setBackColor(Color.BLUE);
        color.setBold(false);
        colors.put("tprogressbar.incomplete", color);

        // THScroller / TVScroller
        color = new CellAttributes();
        color.setForeColor(Color.CYAN);
        color.setBackColor(Color.BLUE);
        color.setBold(false);
        colors.put("tscroller.bar", color);
        color = new CellAttributes();
        color.setForeColor(Color.BLUE);
        color.setBackColor(Color.CYAN);
        color.setBold(false);
        colors.put("tscroller.arrows", color);

        // TTreeView
        color = new CellAttributes();
        color.setForeColor(Color.WHITE);
        color.setBackColor(Color.BLUE);
        color.setBold(false);
        colors.put("ttreeview", color);
        color = new CellAttributes();
        color.setForeColor(Color.GREEN);
        color.setBackColor(Color.BLUE);
        color.setBold(true);
        colors.put("ttreeview.expandbutton", color);
        color = new CellAttributes();
        color.setForeColor(Color.BLACK);
        color.setBackColor(Color.CYAN);
        color.setBold(false);
        colors.put("ttreeview.selected", color);
        color = new CellAttributes();
        color.setForeColor(Color.RED);
        color.setBackColor(Color.BLUE);
        color.setBold(false);
        colors.put("ttreeview.unreadable", color);
        color = new CellAttributes();
        color.setForeColor(Color.BLACK);
        color.setBackColor(Color.BLUE);
        color.setBold(true);
        colors.put("ttreeview.inactive", color);

        // TText text
        color = new CellAttributes();
        color.setForeColor(Color.WHITE);
        color.setBackColor(Color.BLUE);
        color.setBold(false);
        colors.put("tdirectorylist", color);
        color = new CellAttributes();
        color.setForeColor(Color.BLACK);
        color.setBackColor(Color.CYAN);
        color.setBold(false);
        colors.put("tdirectorylist.selected", color);
        color = new CellAttributes();
        color.setForeColor(Color.BLACK);
        color.setBackColor(Color.CYAN);
        color.setBold(false);
        colors.put("tdirectorylist.unreadable", color);
        color = new CellAttributes();
        color.setForeColor(Color.BLACK);
        color.setBackColor(Color.BLUE);
        color.setBold(true);
        colors.put("tdirectorylist.inactive", color);

        // TEditor
        color = new CellAttributes();
        color.setForeColor(Color.WHITE);
        color.setBackColor(Color.BLACK);
        color.setBold(false);
        colors.put("teditor", color);

    }

}
