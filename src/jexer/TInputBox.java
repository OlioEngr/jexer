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

/**
 * TInputBox is a system-modal dialog with an OK button and a text input
 * field.  Call it like:
 *
 * <p>
 * <pre>
 * {@code
 *     box = application.inputBox(title, caption);
 *     if (box.getText().equals("yes")) {
 *         ... the user entered "yes", do stuff ...
 *     }
 * }
 * </pre>
 *
 */
public final class TInputBox extends TMessageBox {

    /**
     * The input field.
     */
    private TField field;

    /**
     * Retrieve the answer text.
     *
     * @return the answer text
     */
    public String getText() {
        return field.getText();
    }

    /**
     * Public constructor.  The input box will be centered on screen.
     *
     * @param application TApplication that manages this window
     * @param title window title, will be centered along the top border
     * @param caption message to display.  Use embedded newlines to get a
     * multi-line box.
     */
    public TInputBox(final TApplication application, final String title,
        final String caption) {

        this(application, title, caption, "");
    }

    /**
     * Public constructor.  The input box will be centered on screen.
     *
     * @param application TApplication that manages this window
     * @param title window title, will be centered along the top border
     * @param caption message to display.  Use embedded newlines to get a
     * multi-line box.
     * @param text initial text to seed the field with
     */
    public TInputBox(final TApplication application, final String title,
        final String caption, final String text) {

        super(application, title, caption, Type.OK, false);

        for (TWidget widget: getChildren()) {
            if (widget instanceof TButton) {
                widget.setY(widget.getY() + 2);
            }
        }

        setHeight(getHeight() + 2);
        field = addField(1, getHeight() - 6, getWidth() - 4, false, text);

        // Yield to the secondary thread.  When I come back from the
        // constructor response will already be set.
        getApplication().yield();
    }

}
