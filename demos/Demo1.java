/**
 * Jexer - Java Text User Interface - demonstration program
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

import jexer.bits.*;
import jexer.TApplication;

/**
 * The demo application itself.
 */
class DemoApplication extends TApplication {
    /**
     * Public constructor
     */
    public DemoApplication() {
	try {
	    ColorTheme theme = new ColorTheme();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}

/**
 * This class provides a simple demonstration of Jexer's capabilities.
 */
public class Demo1 {
    /**
     * Main entry point.
     *
     * @param  args Command line arguments
     */
    public static void main(String [] args) {
	DemoApplication app = new DemoApplication();
	app.run();
    }

}
