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

import jexer.*;
import jexer.event.*;
import jexer.menu.*;

/**
 * This window demonstates the TText, THScroller, and TVScroller widgets.
 */
public class DemoTextWindow extends TWindow {

    /**
     * Hang onto my TText so I can resize it with the window.
     */
    private TText textField;

    /**
     * Public constructor makes a text window out of any string.
     *
     * @param parent the main application
     * @param title the text string
     * @param text the text string
     */
    public DemoTextWindow(final TApplication parent, final String title,
        final String text) {

        super(parent, title, 0, 0, 44, 20, RESIZABLE);
        textField = addText(text, 1, 1, 40, 16);
    }

    /**
     * Public constructor.
     *
     * @param parent the main application
     */
    public DemoTextWindow(final TApplication parent) {
        this(parent, "Text Area",
"This is an example of a reflowable text field.  Some example text follows.\n" +
"\n" +
"Notice that some menu items should be disabled when this window has focus.\n" +
"\n" +
"This library implements a text-based windowing system loosely\n" +
"reminiscient of Borland's [Turbo\n" +
"Vision](http://en.wikipedia.org/wiki/Turbo_Vision) library.  For those\n" +
"wishing to use the actual C++ Turbo Vision library, see [Sergio\n" +
"Sigala's updated version](http://tvision.sourceforge.net/) that runs\n" +
"on many more platforms.\n" +
"\n" +
"This library is licensed LGPL (\"GNU Lesser General Public License\")\n" +
"version 3 or greater.  See the file COPYING for the full license text,\n" +
"which includes both the GPL v3 and the LGPL supplemental terms.\n" +
"\n");

    }

    /**
     * Handle window/screen resize events.
     *
     * @param event resize event
     */
    @Override
    public void onResize(final TResizeEvent event) {
        if (event.getType() == TResizeEvent.Type.WIDGET) {
            // Resize the text field
            textField.setWidth(event.getWidth() - 4);
            textField.setHeight(event.getHeight() - 4);
            textField.reflow();
            return;
        }

        // Pass to children instead
        for (TWidget widget: getChildren()) {
            widget.onResize(event);
        }
    }

    /**
     * Play with menu items.
     */
    public void onFocus() {
        getApplication().enableMenuItem(2001);
        getApplication().disableMenuItem(TMenu.MID_SHELL);
        getApplication().disableMenuItem(TMenu.MID_EXIT);
    }

    /**
     * Called by application.switchWindow() when another window gets the
     * focus.
     */
    public void onUnfocus() {
        getApplication().disableMenuItem(2001);
        getApplication().enableMenuItem(TMenu.MID_SHELL);
        getApplication().enableMenuItem(TMenu.MID_EXIT);
    }

}
