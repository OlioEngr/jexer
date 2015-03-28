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

/**
 * This window demonstates the TMessageBox and TInputBox widgets.
 */
public class DemoMsgBoxWindow extends TWindow {

    /**
     * Constructor.
     *
     * @param parent the main application
     */
    DemoMsgBoxWindow(final TApplication parent) {
        this(parent, TWindow.CENTERED | TWindow.RESIZABLE);
    }

    /**
     * Constructor.
     *
     * @param parent the main application
     * @param flags bitmask of MODAL, CENTERED, or RESIZABLE
     */
    DemoMsgBoxWindow(final TApplication parent, final int flags) {
        // Construct a demo window.  X and Y don't matter because it
        // will be centered on screen.
        super(parent, "Message Boxes", 0, 0, 60, 15, flags);

        int row = 1;

        // Add some widgets
        addLabel("Default OK message box", 1, row);
        addButton("Open O&K MB", 35, row,
            new TAction() {
                public void DO() {
                    getApplication().messageBox("OK MessageBox",
"This is an example of a OK MessageBox.  This is the\n" +
"default MessageBox.\n" +
"\n" +
"Note that the MessageBox text can span multiple\n" +
"lines.\n" +
"\n" +
"The default result (if someone hits the top-left\n" +
"close button) is OK.\n",
                        TMessageBox.Type.OK);
                }
            }
        );
        row += 2;

        addLabel("OK/Cancel message box", 1, row);
        addButton("O&pen OKC MB", 35, row,
            new TAction() {
                public void DO() {
                    getApplication().messageBox("OK/Cancel MessageBox",
"This is an example of a OK/Cancel MessageBox.\n" +
"\n" +
"Note that the MessageBox text can span multiple\n" +
"lines.\n" +
"\n" +
"The default result (if someone hits the top-left\n" +
"close button) is CANCEL.\n",
                        TMessageBox.Type.OKCANCEL);
                }
            }
        );
        row += 2;

        addLabel("Yes/No message box", 1, row);
        addButton("Open &YN MB", 35, row,
            new TAction() {
                public void DO() {
                    getApplication().messageBox("Yes/No MessageBox",
"This is an example of a Yes/No MessageBox.\n" +
"\n" +
"Note that the MessageBox text can span multiple\n" +
"lines.\n" +
"\n" +
"The default result (if someone hits the top-left\n" +
"close button) is NO.\n",
                        TMessageBox.Type.YESNO);
                }
            }
        );
        row += 2;

        addLabel("Yes/No/Cancel message box", 1, row);
        addButton("Ope&n YNC MB", 35, row,
            new TAction() {
                public void DO() {
                    getApplication().messageBox("Yes/No/Cancel MessageBox",
"This is an example of a Yes/No/Cancel MessageBox.\n" +
"\n" +
"Note that the MessageBox text can span multiple\n" +
"lines.\n" +
"\n" +
"The default result (if someone hits the top-left\n" +
"close button) is CANCEL.\n",
                        TMessageBox.Type.YESNOCANCEL);
                }
            }
        );
        row += 2;

        addLabel("Input box", 1, row);
        addButton("Open &input box", 35, row,
            new TAction() {
                public void DO() {
                    TInputBox in = getApplication().inputBox("Input Box",
"This is an example of an InputBox.\n" +
"\n" +
"Note that the InputBox text can span multiple\n" +
"lines.\n",
                        "some input text");
                    getApplication().messageBox("Your InputBox Answer",
                        "You entered: " + in.getText());
                }
            }
        );

        addButton("&Close Window", (getWidth() - 14) / 2, getHeight() - 4,
            new TAction() {
                public void DO() {
                    getApplication().closeWindow(DemoMsgBoxWindow.this);
                }
            }
        );
    }
}

