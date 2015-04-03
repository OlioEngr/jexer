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
package jexer.demos;

import java.io.*;
import java.util.*;

import jexer.*;
import jexer.event.*;
import jexer.menu.*;

/**
 * The demo application itself.
 */
public class DemoApplication extends TApplication {

    /**
     * Add all the widgets of the demo.
     */
    private void addAllWidgets() {
        new DemoMainWindow(this);

        // Add the menus
        addFileMenu();
        addEditMenu();

        TMenu demoMenu = addMenu("&Demo");
        TMenuItem item = demoMenu.addItem(2000, "&Checkable");
        item.setCheckable(true);
        item = demoMenu.addItem(2001, "Disabled");
        item.setEnabled(false);
        item = demoMenu.addItem(2002, "&Normal");
        TSubMenu subMenu = demoMenu.addSubMenu("Sub-&Menu");
        item = demoMenu.addItem(2010, "N&ormal A&&D");
        item = demoMenu.addItem(2050, "Co&lors...");

        item = subMenu.addItem(2000, "&Checkable (sub)");
        item.setCheckable(true);
        item = subMenu.addItem(2001, "Disabled (sub)");
        item.setEnabled(false);
        item = subMenu.addItem(2002, "&Normal (sub)");

        subMenu = subMenu.addSubMenu("Sub-&Menu");
        item = subMenu.addItem(2000, "&Checkable (sub)");
        item.setCheckable(true);
        item = subMenu.addItem(2001, "Disabled (sub)");
        item.setEnabled(false);
        item = subMenu.addItem(2002, "&Normal (sub)");

        addWindowMenu();
    }

    /**
     * Public constructor.
     *
     * @param input an InputStream connected to the remote user, or null for
     * System.in.  If System.in is used, then on non-Windows systems it will
     * be put in raw mode; shutdown() will (blindly!) put System.in in cooked
     * mode.  input is always converted to a Reader with UTF-8 encoding.
     * @param output an OutputStream connected to the remote user, or null
     * for System.out.  output is always converted to a Writer with UTF-8
     * encoding.
     * @throws UnsupportedEncodingException if an exception is thrown when
     * creating the InputStreamReader
     */
    public DemoApplication(final InputStream input,
        final OutputStream output) throws UnsupportedEncodingException {
        super(input, output);
        addAllWidgets();
    }

    /**
     * Handle menu events.
     *
     * @param menu menu event
     * @return if true, the event was processed and should not be passed onto
     * a window
     */
    @Override
    public boolean onMenu(final TMenuEvent menu) {

        if (menu.getId() == 2050) {
            new TEditColorThemeWindow(this);
            return true;
        }

        if (menu.getId() == TMenu.MID_OPEN_FILE) {
            try {
                String filename = fileOpenBox(".");
                 if (filename != null) {
                     try {
                         File file = new File(filename);
                         StringBuilder fileContents = new StringBuilder();
                         Scanner scanner = new Scanner(file);
                         String EOL = System.getProperty("line.separator");

                         try {
                             while (scanner.hasNextLine()) {
                                 fileContents.append(scanner.nextLine() + EOL);
                             }
                             new DemoTextWindow(this, filename,
                                 fileContents.toString());
                         } finally {
                             scanner.close();
                         }
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        return super.onMenu(menu);
    }

    /**
     * Public constructor.
     *
     * @param backendType one of the TApplication.BackendType values
     * @throws Exception if TApplication can't instantiate the Backend.
     */
    public DemoApplication(final BackendType backendType) throws Exception {
        super(backendType);
        addAllWidgets();
    }
}
