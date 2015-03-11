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

/**
 * Jexer - Java Text User Interface library
 *
 * <p>
 * This library is currently in design, but when finished it is intended to
 * implement a text-based windowing system loosely reminiscient of Borland's
 * <a href="http://en.wikipedia.org/wiki/Turbo_Vision">Turbo Vision</a>
 * library.
 *
 * <p>
 * The library is currently under initial development, usage patterns are
 * still being worked on.  Generally the goal will be to build applications
 * somewhat as follows:
 *
 * <p>
 * <pre>
 * {@code
 * import jexer.*;
 *
 * public class MyApplication extends TApplication {
 *
 *     public MyApplication() {
 *         super();
 *
 *         // Create an editor window that has support for copy/paste,
 *         // search text, arrow keys, horizontal and vertical scrollbar, etc.
 *         addEditor();
 *
 *         // Create standard menus for File and Window
 *         addFileMenu();
 *         addWindowMenu();
 *     }
 *
 *     public static void main(String [] args) {
 *         MyApplication app = new MyApplication();
 *         app.run();
 *     }
 * }
 * }
 * </pre>
 */
package jexer;
