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

import jexer.*;

class DemoMainWindow extends TWindow {
    /*
    // Timer that increments a number
    private TTimer timer;

    // The modal window is a more low-level example of controlling a window
    // "from the outside".  Most windows will probably subclass TWindow and
    // do this kind of logic on their own.
    private TWindow modalWindow;
    private void openModalWindow() {
	modalWindow = application.addWindow("Demo Modal Window", 0, 0,
	    58, 15, TWindow.Flag.MODAL);
	modalWindow.addLabel("This is an example of a very braindead modal window.", 1, 1);
	modalWindow.addLabel("Modal windows are centered by default.", 1, 2);
	modalWindow.addButton("&Close", (modalWindow.width - 8)/2,
	    modalWindow.height - 4, &modalWindowClose);
    }
    private void modalWindowClose() {
	application.closeWindow(modalWindow);
    }

    /// This is an example of having a button call a function.
    private void openCheckboxWindow() {
	new DemoCheckboxWindow(application);
    }

    /// We need to override onClose so that the timer will no longer be
    /// called after we close the window.  TTimers currently are completely
    /// unaware of the rest of the UI classes.
    override public void onClose() {
	application.removeTimer(timer);
    }
     */

    /**
     * Construct demo window.  It will be centered on screen.
     */
    public DemoMainWindow(TApplication parent) {
	this(parent, CENTERED | RESIZABLE);
    }

    /**
     * Constructor.
     */
    private DemoMainWindow(TApplication parent, int flags) {
	// Construct a demo window.  X and Y don't matter because it will be
	// centered on screen.
	super(parent, "Demo Window", 0, 0, 60, 23, flags);

        /*
	int row = 1;

	// Add some widgets
	if (!isModal) {
	    addLabel("Message Boxes", 1, row);
	    addButton("&MessageBoxes", 35, row,
		{
		    new DemoMsgBoxWindow(application);
		}
	    );
	}
	row += 2;

	addLabel("Open me as modal", 1, row);
	addButton("W&indow", 35, row,
	    {
		new DemoMainWindow(application, Flag.MODAL);
	    }
	);

	row += 2;

	addLabel("Variable-width text field:", 1, row);
	addField(35, row++, 15, false, "Field text");

	addLabel("Fixed-width text field:", 1, row);
	addField(35, row, 15, true);
	row += 2;

	if (!isModal) {
	    addLabel("Radio buttons and checkboxes", 1, row);
	    addButton("&Checkboxes", 35, row, &openCheckboxWindow);
	}
	row += 2;

	if (!isModal) {
	    addLabel("Editor window", 1, row);
	    addButton("Edito&r", 35, row,
		{
		    new TEditor(application, 0, 0, 60, 15);
		}
	    );
	}
	row += 2;

	if (!isModal) {
	    addLabel("Text areas", 1, row);
	    addButton("&Text", 35, row,
		{
		    new DemoTextWindow(application);
		}
	    );
	}
	row += 2;

	if (!isModal) {
	    addLabel("Tree views", 1, row);
	    addButton("Tree&View", 35, row,
		{
		    new DemoTreeViewWindow(application);
		}
	    );
	}
	row += 2;

	version(Posix) {
	    if (!isModal) {
		addLabel("Terminal", 1, row);
		addButton("Termi&nal", 35, row,
		    {
			application.openTerminal(0, 0);
		    }
		);
	    }
	    row += 2;
	}

	TProgressBar bar = addProgressBar(1, row, 22);
	row++;
	TLabel timerLabel = addLabel("Timer", 1, row);
	timer = parent.addTimer(100,
	    {
		static int i = 0;
		auto writer = appender!dstring();
		formattedWrite(writer, "Timer: %d", i);
		timerLabel.text = writer.data;
		timerLabel.width = cast(uint)timerLabel.text.length;
		if (i < 100) {
		    i++;
		}
		bar.value = i;
		parent.repaint = true;
	    }, true);
         */
    }
}

/**
 * The demo application itself.
 */
class DemoApplication extends TApplication {
    /**
     * Public constructor
     */
    public DemoApplication() throws Exception {
	super(null, null);
	new DemoMainWindow(this);
	TWindow window2 = new DemoMainWindow(this);
        window2.setHeight(5);
        window2.setWidth(25);
        window2.setX(17);
        window2.setY(6);
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
	try {
	    DemoApplication app = new DemoApplication();
	    app.run();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

}
